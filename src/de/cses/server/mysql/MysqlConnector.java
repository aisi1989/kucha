/*
 * Copyright 2016-2017 
 * Saxon Academy of Science in Leipzig, Germany
 * 
 * This is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License version 3 (GPL v3) as published by the Free Software Foundation.
 * 
 * This software is distributed WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. Please read the GPL v3 for more details.
 * 
 * You should have received a copy of the GPL v3 along with the software. 
 * If not, you can access it from here: <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */
package de.cses.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import de.cses.server.ServerProperties;
import de.cses.server.UserManager;
import de.cses.shared.AuthorEntry;
import de.cses.shared.CaveAreaEntry;
import de.cses.shared.CaveEntry;
import de.cses.shared.CaveGroupEntry;
import de.cses.shared.CavePart;
import de.cses.shared.CaveTypeEntry;
import de.cses.shared.CeilingTypeEntry;
import de.cses.shared.DepictionEntry;
import de.cses.shared.DistrictEntry;
import de.cses.shared.ExpeditionEntry;
import de.cses.shared.IconographyEntry;
import de.cses.shared.ImageEntry;
import de.cses.shared.ImageTypeEntry;
import de.cses.shared.MainTypologicalClass;
import de.cses.shared.ModeOfRepresentationEntry;
import de.cses.shared.OrientationEntry;
import de.cses.shared.OrnamentCaveType;
import de.cses.shared.OrnamentEntry;
import de.cses.shared.OrnamentFunction;
import de.cses.shared.OrnamentOfOtherCulturesEntry;
import de.cses.shared.OrnamentPosition;
import de.cses.shared.PhotographerEntry;
import de.cses.shared.PictorialElementEntry;
import de.cses.shared.PreservationClassificationEntry;
import de.cses.shared.PublicationEntry;
import de.cses.shared.RegionEntry;
import de.cses.shared.SiteEntry;
import de.cses.shared.StructureOrganization;
import de.cses.shared.StyleEntry;
import de.cses.shared.UserEntry;
import de.cses.shared.VendorEntry;
import de.cses.shared.WallEntry;
import de.cses.shared.WallLocationEntry;

/**
 * This is the central Database connector. Here are all methods that we need for standard database operations, including user login and access management.
 * 
 * @author alingnau
 *
 */
public class MysqlConnector {

	private String url; // MysqlConnector.db.url
	private String user; // MysqlConnector.db.user
	private String password; // MysqlConnector.db.password

	private int auto_increment_id;

	private static MysqlConnector instance = null;
	private ServerProperties serverProperties = ServerProperties.getInstance();

	/**
	 * By calling this method, we avoid that a new instance will be created if there is already one existing. If this method is called without an instance
	 * existing, one will be created.
	 * 
	 * @return
	 */
	public static synchronized MysqlConnector getInstance() {
		if (instance == null) {
			instance = new MysqlConnector();
		}
		return instance;
	}

	private Connection connection;

	private MysqlConnector() {

		user = serverProperties.getProperty("MysqlConnector.db.user");
		password = serverProperties.getProperty("MysqlConnector.db.password");
		url = serverProperties.getProperty("MysqlConnector.db.url");
		try {
			Class.forName("org.mariadb.jdbc.Driver"); //$NON-NLS-1$
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Connection getConnection() {
		try {
			if (connection == null || !connection.isValid(5)) {
				connection = DriverManager.getConnection(url, user, password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Selects all districts from the table 'Districts' in the database
	 * 
	 * @return
	 */
	public ArrayList<DistrictEntry> getDistricts() {
		ArrayList<DistrictEntry> result = new ArrayList<DistrictEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Districts");
			while (rs.next()) {
				result.add(new DistrictEntry(rs.getInt("DistrictID"), rs.getString("Name"), rs.getInt("SiteID"), rs.getString("Description"),
						rs.getString("Map"), rs.getString("ArialMap")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * 
	 * @param id
	 *          DistrictID
	 * @return The corresponding DistrictEntry from the table Districts
	 */
	public DistrictEntry getDistrict(int id) {
		DistrictEntry result = null;
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Districts WHERE DistrictID=" + id);
			while (rs.next()) {
				result = new DistrictEntry(rs.getInt("DistrictID"), rs.getString("Name"), rs.getInt("SiteID"), rs.getString("Description"),
						rs.getString("Map"), rs.getString("ArialMap"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Creates a new image entry in the table 'Images'
	 * 
	 * @return auto incremented primary key 'ImageID'
	 */
	public synchronized ImageEntry createNewImageEntry() {
		ImageEntry entry = new ImageEntry();
		Connection dbc = getConnection();
		PreparedStatement pstmt;

		try {
			pstmt = dbc.prepareStatement(
					"INSERT INTO Images (Filename, Title, ShortName, Copyright, PhotographerID, Comment, Date, ImageTypeID, ImageMode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, entry.getTitle());
			pstmt.setString(2, entry.getShortName());
			pstmt.setString(3, entry.getCopyright());
			pstmt.setInt(4, entry.getPhotographerID());
			pstmt.setString(5, entry.getComment());
			pstmt.setString(6, entry.getDate());
			pstmt.setInt(7, entry.getImageTypeID());
			pstmt.setBoolean(8, entry.isPublicImage());
			pstmt.execute();
			ResultSet keys = pstmt.getGeneratedKeys();
			if (keys.first()) {
				// there should only be 1 key returned here but we need to modify this
				// in case
				// we have requested multiple new entries. works for the moment
				entry.setImageID(keys.getInt(1));
			}
			keys.close();
			pstmt.close();
		} catch (SQLException e) {
			return null;
		}
		return entry;
	}

	/**
	 * Executes a pre-defined SQL INSERT statement and returns the generated (auto-increment) unique key from the table.
	 * 
	 * @return auto incremented primary key
	 */
	public synchronized int insertEntry(String sqlInsert) {
		Connection dbc = getConnection();
		Statement stmt;
		int generatedKey = -1;
		try {
			stmt = dbc.createStatement();
			stmt.execute(sqlInsert, Statement.RETURN_GENERATED_KEYS);
			ResultSet keys = stmt.getGeneratedKeys();
			while (keys.next()) {
				// there should only be 1 key returned here but we need to modify this
				// in case
				// we have requested multiple new entries. works for the moment
				generatedKey = keys.getInt(1);
			}
			keys.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return generatedKey;
	}

	/**
	 * Executes an SQL update using a pre-defined SQL UPDATE string
	 * 
	 * @param sqlUpdate
	 *          The full sql string including the UPDATE statement
	 * @return true if sucessful
	 */
	public synchronized boolean updateEntry(String sqlUpdate) {
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			stmt.executeUpdate(sqlUpdate);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Executes a SQL delete using a predefined SQL DELETE string
	 * 
	 * @param sqlDelete
	 *          The full sql string including the DELETE statement
	 * @return true if sucessful
	 */
	public boolean deleteEntry(String sqlDelete) {
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			stmt.execute(sqlDelete);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param sqlWhere
	 * @return
	 */
	public ArrayList<DepictionEntry> getDepictions() {
		return getDepictions(null);
	}

	public ArrayList<DepictionEntry> getDepictions(String sqlWhere) {
		ArrayList<DepictionEntry> results = new ArrayList<DepictionEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery((sqlWhere == null) ? "SELECT * FROM Depictions" : "SELECT * FROM Depictions WHERE " + sqlWhere);
			while (rs.next()) {
				results.add(new DepictionEntry(rs.getInt("DepictionID"), rs.getInt("StyleID"), rs.getString("Inscriptions"),
						rs.getString("SeparateAksaras"), rs.getString("Dating"), rs.getString("Description"), rs.getString("BackgroundColour"),
						rs.getString("GeneralRemarks"), rs.getString("OtherSuggestedIdentifications"), rs.getDouble("Width"), rs.getDouble("Height"),
						rs.getInt("ExpeditionID"), rs.getDate("PurchaseDate"), rs.getInt("CurrentLocationID"), rs.getInt("VendorID"),
						rs.getInt("StoryID"), rs.getInt("CaveID"), rs.getInt("WallID"), rs.getInt("IconographyID"),
						rs.getInt("ModeOfRepresentationID")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public DepictionEntry getDepictionEntry(int depictionID) {
		Connection dbc = getConnection();
		DepictionEntry result = null;
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Depictions WHERE DepictionID=" + depictionID);
			if (rs.next()) { // we only need to call this once, since we do not expect
												// more than 1 result!
				result = new DepictionEntry(rs.getInt("DepictionID"), rs.getInt("StyleID"), rs.getString("Inscriptions"),
						rs.getString("SeparateAksaras"), rs.getString("Dating"), rs.getString("Description"), rs.getString("BackgroundColour"),
						rs.getString("GeneralRemarks"), rs.getString("OtherSuggestedIdentifications"), rs.getInt("Dimension.width"),
						rs.getInt("Dimension.height"), rs.getInt("ExpeditionID"), rs.getDate("PurchaseDate"), rs.getInt("CurrentLocationID"),
						rs.getInt("VendorID"), rs.getInt("StoryID"), rs.getInt("CaveID"), rs.getInt("WallID"), rs.getInt("IconographyID"),
						rs.getInt("ModeOfRepresentationID"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @return ArrayList<String> with result from 'SELECT * FROM Images'
	 */
	public ArrayList<ImageEntry> getImageEntries() {
		ArrayList<ImageEntry> results = new ArrayList<ImageEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Images");
			while (rs.next()) {
				results.add(new ImageEntry(rs.getInt("ImageID"), rs.getString("Filename"), rs.getString("Title"), rs.getString("ShortName"),
						rs.getString("Copyright"), rs.getInt("PhotographerID"), rs.getString("Comment"), rs.getString("Date"), rs.getInt("ImageTypeID"),
						rs.getBoolean("ImageMode")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<ImageEntry> getImageEntries(String sqlWhere) {
		ArrayList<ImageEntry> results = new ArrayList<ImageEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Images WHERE " + sqlWhere);
			while (rs.next()) {
				results.add(new ImageEntry(rs.getInt("ImageID"), rs.getString("Filename"), rs.getString("Title"), rs.getString("ShortName"),
						rs.getString("Copyright"), rs.getInt("PhotographerID"), rs.getString("Comment"), rs.getString("Date"), rs.getInt("ImageTypeID"),
						rs.getBoolean("ImageMode")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	/**
	 * 
	 * @param imageID
	 * @return ImageEntry that matches imageID, or NULL
	 */
	public ImageEntry getImageEntry(int imageID) {
		Connection dbc = getConnection();
		ImageEntry result = null;
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Images WHERE ImageID=" + imageID);
			if (rs.first()) {
				result = new ImageEntry(rs.getInt("ImageID"), rs.getString("Filename"), rs.getString("Title"), rs.getString("ShortName"),
						rs.getString("Copyright"), rs.getInt("PhotographerID"), rs.getString("Comment"), rs.getString("Date"), rs.getInt("ImageTypeID"),
						rs.getBoolean("ImageMode"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<PhotographerEntry> getPhotographerEntries() {
		ArrayList<PhotographerEntry> results = new ArrayList<PhotographerEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Photographers");
			while (rs.next()) {
				results.add(new PhotographerEntry(rs.getInt(1), rs.getString(2)));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<CaveEntry> getCaves() {
		return getCaves(null);
	}

	public ArrayList<CaveEntry> getCaves(String sqlWhere) {
		ArrayList<CaveEntry> results = new ArrayList<CaveEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery((sqlWhere == null) ? "SELECT * FROM Caves" : "SELECT * FROM Caves WHERE " + sqlWhere);
			while (rs.next()) {
				CaveEntry ce = new CaveEntry(rs.getInt("CaveID"), rs.getString("OfficialNumber"), rs.getString("HistoricName"),
						rs.getString("OptionalHistoricName"), rs.getInt("CaveTypeID"), rs.getInt("DistrictID"), rs.getInt("RegionID"),
						rs.getInt("OrientationID"), rs.getString("StateOfPreservation"), rs.getString("Findings"), rs.getString("Notes"),
						rs.getString("FirstDocumentedBy"), rs.getInt("FirstDocumentedInYear"), rs.getInt("PreservationClassificationID"),
						rs.getInt("CaveGroupID"), rs.getString("OptionalCaveSketch"), rs.getString("C14url"), rs.getString("C14DocumentFilename"));
				ce.setCaveAreaList(getCaveAreas(ce.getCaveID()));
				ce.setWallList(getWalls(ce.getCaveID()));
				results.add(ce);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public CaveEntry getCave(int id) {
		CaveEntry result = null;
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Caves WHERE CaveID=" + id);
			if (rs.first()) {
				result = new CaveEntry(rs.getInt("CaveID"), rs.getString("OfficialNumber"), rs.getString("HistoricName"),
						rs.getString("OptionalHistoricName"), rs.getInt("CaveTypeID"), rs.getInt("DistrictID"), rs.getInt("RegionID"),
						rs.getInt("OrientationID"), rs.getString("StateOfPreservation"), rs.getString("Findings"), rs.getString("Notes"),
						rs.getString("FirstDocumentedBy"), rs.getInt("FirstDocumentedInYear"), rs.getInt("PreservationClassificationID"),
						rs.getInt("CaveGroupID"), rs.getString("OptionalCaveSketch"), rs.getString("C14url"), rs.getString("C14DocumentFilename"));
				result.setCaveAreaList(getCaveAreas(result.getCaveID()));
				result.setWallList(getWalls(result.getCaveID()));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public ArrayList<CaveEntry> getCavesbyDistrictID(int districtID) {
		ArrayList<CaveEntry> results = new ArrayList<CaveEntry>();
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = dbc.prepareStatement("SELECT * FROM Caves WHERE DistrictID=?");
			pstmt.setInt(1, districtID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				CaveEntry ce = new CaveEntry(rs.getInt("CaveID"), rs.getString("OfficialNumber"), rs.getString("HistoricName"),
						rs.getString("OptionalHistoricName"), rs.getInt("CaveTypeID"), rs.getInt("DistrictID"), rs.getInt("RegionID"),
						rs.getInt("OrientationID"), rs.getString("StateOfPreservation"), rs.getString("Findings"), rs.getString("Notes"),
						rs.getString("FirstDocumentedBy"), rs.getInt("FirstDocumentedInYear"), rs.getInt("PreservationClassificationID"),
						rs.getInt("CaveGroupID"), rs.getString("OptionalCaveSketch"), rs.getString("C14url"), rs.getString("C14DocumentFilename"));
				ce.setCaveAreaList(getCaveAreas(ce.getCaveID()));
				ce.setWallList(getWalls(ce.getCaveID()));
				results.add(ce);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<OrnamentEntry> getOrnaments() {
		ArrayList<OrnamentEntry> results = new ArrayList<OrnamentEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Ornaments");
			while (rs.next()) {
				results.add(new OrnamentEntry(rs.getInt("OrnamentID"), rs.getString("Code")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<OrnamentOfOtherCulturesEntry> getOrnametsOfOtherCultures() {
		ArrayList<OrnamentOfOtherCulturesEntry> results = new ArrayList<OrnamentOfOtherCulturesEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM OrnamentsOfOtherCultures");
			while (rs.next()) {
				results.add(new OrnamentOfOtherCulturesEntry(rs.getInt("OtherOrnamentID"), rs.getString("Description")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public CaveTypeEntry getCaveTypebyID(int caveTypeID) {
		CaveTypeEntry result = null;
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CaveType WHERE CaveTypeID =" + caveTypeID);
			while (rs.next()) {
				result = new CaveTypeEntry(rs.getInt("CaveTypeID"), rs.getString("NameEN"), rs.getString("DescriptionEN"),
						rs.getString("SketchName"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<CaveTypeEntry> getCaveTypes() {
		Connection dbc = getConnection();
		ArrayList<CaveTypeEntry> results = new ArrayList<CaveTypeEntry>();
		Statement stmt;

		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CaveType");
			while (rs.next()) {
				CaveTypeEntry caveType = new CaveTypeEntry(rs.getInt("CaveTypeID"), rs.getString("NameEN"), rs.getString("DescriptionEN"),
						rs.getString("SketchName"));
				results.add(caveType);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return results;
		}
		return results;

	}

	public boolean saveOrnamentEntry(OrnamentEntry ornamentEntry) {
		System.err.println("aufgerufen save ornament entry");
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery(
					"INSERT INTO Ornaments (Code, Description, Remarks, Interpretation, OrnamentReferences, Annotation , MainTypologicalClass, StructureOrganization ) VALUES ('"
							+ ornamentEntry.getCode() + "','" + ornamentEntry.getDescription() + "','" + ornamentEntry.getRemarks() + "','"
							+ ornamentEntry.getInterpretation() + "','" + ornamentEntry.getReferences() + "','" + ornamentEntry.getAnnotations() + "','"
							+ ornamentEntry.getMaintypologycalClass().getMainTypologicalClassID() + "','"
							+ ornamentEntry.getStructureOrganization().getStructureOrganizationID() + "')");
			rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			while (rs.next()) {
				auto_increment_id = rs.getInt(1);
			}
			for (ImageEntry entry : ornamentEntry.getImages()) {
				rs = stmt.executeQuery(
						"INSERT INTO OrnamentImageRelation (OrnamentID, ImageID) VALUES('" + auto_increment_id + " ','" + entry.getImageID() + "')");
			}
			for (int i = 0; i < ornamentEntry.getCavesRelations().size(); i++) {
				stmt = dbc.createStatement();
				rs = stmt.executeQuery(
						"INSERT INTO CaveOrnamentRelation (CaveID, OrnamentID, Colours, Notes, GroupOfOrnaments,RelatedElementsOfOtherCultures, SimilarElementsOfOtherCultures ) VALUES ("
								+ ornamentEntry.getCavesRelations().get(i).getCave().getCaveID() + "," + auto_increment_id + ",'"
								+ ornamentEntry.getCavesRelations().get(i).getColours() + "','" + ornamentEntry.getCavesRelations().get(i).getNotes()
								+ "','" + ornamentEntry.getCavesRelations().get(i).getGroup() + "','"
								+ ornamentEntry.getCavesRelations().get(i).getRelatedelementeofOtherCultures() + "','"
								+ ornamentEntry.getCavesRelations().get(i).getSimilarelementsOfOtherCultures() + "')");
				rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
				while (rs.next()) {
					auto_increment_id = rs.getInt(1);
				}

				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getOrientations().size(); j++) {
					System.err.println("orientation");
					rs = stmt.executeQuery("INSERT INTO OrnamentOrientationRelation (OrnamentCaveRelationID, OrientationID) VALUES ("
							+ auto_increment_id + "," + ornamentEntry.getCavesRelations().get(i).getOrientations().get(i).getOrientationID() + ")");
				}

				System.err.println("pictorial gespeichert Anzahl ist: " + ornamentEntry.getCavesRelations().get(i).getPictorialElements().size());
				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getPictorialElements().size(); j++) {
					System.err.println("pictorial gespeichert");
					rs = stmt.executeQuery(
							"INSERT INTO OrnamentCavePictorialRelation (OrnamentCaveRelationID, PictorialElementID) VALUES (" + auto_increment_id + ","
									+ ornamentEntry.getCavesRelations().get(i).getPictorialElements().get(j).getPictorialElementID() + ")");
				}

				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getRelatedOrnamentsRelations().size(); j++) {
					rs = stmt.executeQuery("INSERT INTO RelatedOrnamentsRelation (OrnamentID, OrnamentCaveRelationID) VALUES ("
							+ ornamentEntry.getCavesRelations().get(i).getRelatedOrnamentsRelations().get(j).getOrnamentID() + "," + auto_increment_id
							+ ")");
				}
				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getSimilarOrnamentsRelations().size(); j++) {
					rs = stmt.executeQuery("INSERT INTO SimilarOrnamentsRelation (OrnamentID, OrnamentCaveRelationID) VALUES ("
							+ ornamentEntry.getCavesRelations().get(i).getSimilarOrnamentsRelations().get(j).getOrnamentID() + "," + auto_increment_id
							+ ")");
				}
				System.err.println(
						"wallcaveornamentrelation wird hinzugefuegt, anzahl ist " + ornamentEntry.getCavesRelations().get(i).getWalls().size());
				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getWalls().size(); j++) {
					System.err.println(
							"wallcaveornamentrelation wird hinzugefuegt, anzahl ist " + ornamentEntry.getCavesRelations().get(i).getWalls().size());
					rs = stmt.executeQuery("INSERT INTO WallCaveOrnamentRelation (OrnamentCaveRelationID, WallID, Position, Function, Notes) VALUES ("
							+ auto_increment_id + "," + ornamentEntry.getCavesRelations().get(i).getWalls().get(j).getWallID() + ",'"
							+ ornamentEntry.getCavesRelations().get(i).getWalls().get(j).getPosition().getOrnamentPositionID() + "','"
							+ ornamentEntry.getCavesRelations().get(i).getWalls().get(j).getFunction().getOrnamentFunctionID() + "','"
							+ ornamentEntry.getCavesRelations().get(i).getWalls().get(j).getNotes() + "')");
				}

			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public IconographyEntry getIconographyEntry(int id) {
		IconographyEntry result = null;
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Iconography WHERE IconographyID=" + id);
			if (rs.first()) {
				result = new IconographyEntry(rs.getInt("IconographyID"), rs.getInt("ParentID"), rs.getString("Text"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<IconographyEntry> getIconography() {
		ArrayList<IconographyEntry> root = getIconographyEntries(0);

		for (IconographyEntry item : root) {
			processIconographyTree(item);
		}
		return root;
	}

	protected void processIconographyTree(IconographyEntry parent) {
		ArrayList<IconographyEntry> children = getIconographyEntries(parent.getIconographyID());
		if (children != null) {
			parent.setChildren(children);
			for (IconographyEntry child : children) {
				processIconographyTree(child);
			}
		}
	}

	protected ArrayList<IconographyEntry> getIconographyEntries(int parentID) {
		ArrayList<IconographyEntry> results = new ArrayList<IconographyEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		String where = (parentID == 0) ? "IS NULL" : "= " + parentID;

		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Iconography WHERE ParentID " + where);
			while (rs.next()) {
				results.add(new IconographyEntry(rs.getInt("IconographyID"), rs.getInt("ParentID"), rs.getString("Text")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<PictorialElementEntry> getPictorialElements(int rootID) {
		ArrayList<PictorialElementEntry> root = getPictorialElementEntries(rootID);

		for (PictorialElementEntry item : root) {
			processPictorialElementsTree(item);
		}
		return root;
	}

	private void processPictorialElementsTree(PictorialElementEntry parent) {
		ArrayList<PictorialElementEntry> children = getPictorialElementEntries(parent.getPictorialElementID());
		if (children != null) {
			parent.setChildren(children);
			for (PictorialElementEntry child : children) {
				processPictorialElementsTree(child);
			}
		}
	}

	protected ArrayList<PictorialElementEntry> getPictorialElementEntries(int parentID) {
		ArrayList<PictorialElementEntry> results = new ArrayList<PictorialElementEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		String where = (parentID == 0) ? "IS NULL" : "= " + parentID;

		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM PictorialElements WHERE ParentID " + where);
			while (rs.next()) {
				results.add(new PictorialElementEntry(rs.getInt("PictorialElementID"), rs.getInt("ParentID"), rs.getString("Text")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<VendorEntry> getVendors() {
		ArrayList<VendorEntry> results = new ArrayList<VendorEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Vendors");
			while (rs.next()) {
				results.add(new VendorEntry(rs.getInt("VendorID"), rs.getString("VendorName")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<StyleEntry> getStyles() {
		ArrayList<StyleEntry> results = new ArrayList<StyleEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Styles");
			while (rs.next()) {
				results.add(new StyleEntry(rs.getInt("StyleID"), rs.getString("StyleName")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<ExpeditionEntry> getExpeditions() {
		ArrayList<ExpeditionEntry> results = new ArrayList<ExpeditionEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Expeditions");
			while (rs.next()) {
				results.add(new ExpeditionEntry(rs.getInt("ExpeditionID"), rs.getString("Name"), rs.getString("Leader"), rs.getDate("StartDate"),
						rs.getDate("EndDate")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<PublicationEntry> getPublications() {
		ArrayList<PublicationEntry> results = new ArrayList<PublicationEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Publications");
			while (rs.next()) {
				results.add(new PublicationEntry(rs.getInt("PublicationID"), rs.getString("Editors"), rs.getString("Type"), rs.getString("DOI"),
						rs.getString("Pages"), rs.getDate("Year"), rs.getInt("PublisherID"), rs.getString("Title.English"),
						rs.getString("Title.Phonetic"), rs.getString("Title.Original"), rs.getString("Abstract")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public PublicationEntry getPublicationEntry(int id) {
		PublicationEntry result = null;
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Publications WHERE PublicationID=" + id);
			while (rs.next()) {
				result = new PublicationEntry(rs.getInt("PublicationID"), rs.getString("Editors"), rs.getString("Type"), rs.getString("DOI"),
						rs.getString("Pages"), rs.getDate("Year"), rs.getInt("PublisherID"), rs.getString("Title.English"),
						rs.getString("Title.Phonetic"), rs.getString("Title.Original"), rs.getString("Abstract"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @param id
	 * @return
	 */
	public AuthorEntry getAuthorEntry(int id) {
		AuthorEntry result = null;
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Authors WHERE AuthorID=" + id);
			while (rs.next()) {
				result = new AuthorEntry(rs.getInt("AuthorID"), rs.getString("Lastname"), rs.getString("Firstname"), rs.getDate("KuchaVisitDate"),
						rs.getString("Affiliation"), rs.getString("Email"), rs.getString("Homepage"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @param depictionID
	 * @return
	 */
	public int getRelatedMasterImageID(int depictionID) {
		int result = 0;
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM DepictionImageRelation WHERE (DepictionID=" + depictionID + " AND IsMaster=" + true + ")");
			if (rs.first()) {
				result = rs.getInt("ImageID");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return result;
	}

	/**
	 * @param depictionID
	 * @return
	 */
	public ArrayList<ImageEntry> getRelatedImages(int depictionID) {
		ArrayList<ImageEntry> results = new ArrayList<ImageEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM Images WHERE ImageID IN (SELECT ImageID FROM DepictionImageRelation WHERE DepictionID=" + depictionID + ")");
			while (rs.next()) {
				results.add(new ImageEntry(rs.getInt("ImageID"), rs.getString("Filename"), rs.getString("Title"), rs.getString("ShortName"),
						rs.getString("Copyright"), rs.getInt("PhotographerID"), rs.getString("Comment"), rs.getString("Date"), rs.getInt("ImageTypeID"),
						rs.getBoolean("ImageMode")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public ArrayList<DepictionEntry> getAllDepictionsbyWall(int wallID) {
		ArrayList<DepictionEntry> depictions = new ArrayList<DepictionEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT DepictionID, AbsoluteLeft, AbsoluteTop FROM Depictions WHERE WallID = " + wallID);
			while (rs.next()) {
				DepictionEntry depiction = new DepictionEntry();
				depiction.setDepictionID(rs.getInt("DepictionID"));
				depiction.setAbsoluteLeft(rs.getInt("AbsoluteLeft"));
				depiction.setAbsoluteTop(rs.getInt("AbsoluteTop"));

				depictions.add(depiction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return depictions;
	}

	public String saveDepiction(int depictionID, int AbsoluteLeft, int AbsoluteTop) {
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			stmt.executeQuery(
					"UPDATE Depictions SET AbsoluteLeft =" + AbsoluteLeft + ", AbsoluteTop =" + AbsoluteTop + " WHERE DepictionID =" + depictionID);
		} catch (SQLException e) {
			e.printStackTrace();
			return "failed to save depiction";

		}
		return "saved";
	}

	/**
	 * Updates the information of the entry given as parameter in the SQL database.
	 * 
	 * @param entry
	 * @return
	 */
	public synchronized boolean updateImageEntry(ImageEntry entry) {
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = dbc.prepareStatement(
					"UPDATE Images SET Filename=?, Title=?, ShortName=?, Copyright=?, PhotographerID=?, Comment=?, Date=?, ImageTypeID=?, ImageMode=? WHERE ImageID=?");
			pstmt.setString(1, entry.getFilename());
			pstmt.setString(2, entry.getTitle());
			pstmt.setString(3, entry.getShortName());
			pstmt.setString(4, entry.getCopyright());
			pstmt.setInt(5, entry.getPhotographerID());
			pstmt.setString(6, entry.getComment());
			pstmt.setString(7, entry.getDate());
			pstmt.setInt(8, entry.getImageTypeID());
			pstmt.setBoolean(9, entry.isPublicImage());
			pstmt.setInt(10, entry.getImageID());
			pstmt.execute();
			pstmt.close();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/**
	 * @return A list of all Regions as RegionEntry objects
	 */
	public ArrayList<RegionEntry> getRegions() {
		ArrayList<RegionEntry> result = new ArrayList<RegionEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Regions");
			while (rs.next()) {
				result.add(new RegionEntry(rs.getInt("RegionID"), rs.getString("PhoneticName"), rs.getString("OriginalName"),
						rs.getString("EnglishName"), rs.getInt("SiteID")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return
	 */
	public ArrayList<SiteEntry> getSites() {
		ArrayList<SiteEntry> result = new ArrayList<SiteEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sites");
			while (rs.next()) {
				result.add(new SiteEntry(rs.getInt("SiteID"), rs.getString("Name"), rs.getString("AlternativeName")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param id
	 * @return
	 */
	public SiteEntry getSite(int id) {
		SiteEntry result = null;
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sites WHERE SiteID=" + id);
			if (rs.first()) {
				result = new SiteEntry(rs.getInt("SiteID"), rs.getString("Name"), rs.getString("AlternativeName"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<OrientationEntry> getOrientations() {
		OrientationEntry result = null;
		ArrayList<OrientationEntry> orientations = new ArrayList<OrientationEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM OrnamenticOrientation");
			while (rs.next()) {
				result = new OrientationEntry(rs.getInt("OrnamenticOrientationID"), rs.getString("Name"));
				orientations.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orientations;
	}

	/**
	 * @return
	 */
	public ArrayList<OrientationEntry> getOrientationInformation() {
		ArrayList<OrientationEntry> result = new ArrayList<OrientationEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM OrientationInformation");
			while (rs.next()) {
				result.add(new OrientationEntry(rs.getInt("OrientationID"), rs.getString("NameEN")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param depictionID
	 * @return
	 */
	public ArrayList<PictorialElementEntry> getRelatedPE(int depictionID) {
		ArrayList<PictorialElementEntry> result = new ArrayList<PictorialElementEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM PictorialElements WHERE PictorialElementID IN (SELECT PictorialElementID FROM DepictionPERelation WHERE DepictionID="
							+ depictionID + ")");
			while (rs.next()) {
				result.add(new PictorialElementEntry(rs.getInt("PictorialElementID"), rs.getInt("ParentID"), rs.getString("Text")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<MainTypologicalClass> getMainTypologicalClass() {
		MainTypologicalClass result = null;
		ArrayList<MainTypologicalClass> maintypologicalclasses = new ArrayList<MainTypologicalClass>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM MainTypologicalClass");
			while (rs.next()) {
				result = new MainTypologicalClass(rs.getInt("MainTypologicalClassID"), rs.getString("Name"));
				maintypologicalclasses.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maintypologicalclasses;
	}

	public MainTypologicalClass getMainTypologicalClassbyID(int maintypoID) {
		MainTypologicalClass result = null;

		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM MainTypologicalClass WHERE MainTypologicalClassID = " + maintypoID);
			while (rs.next()) {
				result = new MainTypologicalClass(rs.getInt("MainTypologicalClassID"), rs.getString("Name"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<WallEntry> getWalls() {
		WallEntry result = null;
		ArrayList<WallEntry> walls = new ArrayList<WallEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Walls");
			while (rs.next()) {
				result = new WallEntry(rs.getInt("CaveID"), rs.getInt("WallLocationID"), rs.getInt("PreservationClassificationID"),
						rs.getDouble("Width"), rs.getDouble("Height"));
				walls.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return walls;
	}

	public WallEntry getWall(int wallID) {
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		WallEntry result = null;
		try {
			pstmt = dbc.prepareStatement("SELECT * FROM Walls WHERE WallID=?");
			pstmt.setInt(1, wallID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				result = new WallEntry(rs.getInt("CaveID"), rs.getInt("WallLocationID"), rs.getInt("PreservationClassificationID"),
						rs.getDouble("Width"), rs.getDouble("Height"));
			}
			pstmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		return result;
	}

	public WallEntry getWall(int caveID, String locationLabel) {
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		WallEntry result = null;
		try {
			pstmt = dbc.prepareStatement("SELECT * FROM Walls WHERE CaveID=? AND LocationLabel=?");
			pstmt.setInt(1, caveID);
			pstmt.setString(2, locationLabel);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				result = new WallEntry(rs.getInt("CaveID"), rs.getInt("WallLocationID"), rs.getInt("PreservationClassificationID"),
						rs.getDouble("Width"), rs.getDouble("Height"));
			}
			pstmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		return result;
	}

	public ArrayList<StructureOrganization> getStructureOrganizations() {
		StructureOrganization result = null;
		ArrayList<StructureOrganization> structureOrganizations = new ArrayList<StructureOrganization>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM StructureOrganization");
			while (rs.next()) {
				result = new StructureOrganization(rs.getInt("StructureOrganizationID"), rs.getString("Name"));
				structureOrganizations.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return structureOrganizations;
	}

	public ArrayList<CavePart> getCaveParts() {
		CavePart result = null;
		ArrayList<CavePart> caveparts = new ArrayList<CavePart>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CavePart");
			while (rs.next()) {
				result = new CavePart(rs.getInt("CavePartID"), rs.getString("Name"));
				caveparts.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return caveparts;
	}

	public ArrayList<OrnamentPosition> getOrnamentPosition() {
		OrnamentPosition result = null;
		ArrayList<OrnamentPosition> positions = new ArrayList<OrnamentPosition>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM OrnamenticPosition");
			while (rs.next()) {
				result = new OrnamentPosition(rs.getInt("OrnamenticPositionID"), rs.getString("Name"));
				positions.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return positions;
	}

	public ArrayList<OrnamentFunction> getOrnamentFunction() {
		OrnamentFunction result = null;
		ArrayList<OrnamentFunction> functions = new ArrayList<OrnamentFunction>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM OrnamenticFunction");
			while (rs.next()) {
				result = new OrnamentFunction(rs.getInt("OrnamenticFunctionID"), rs.getString("Name"));
				functions.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return functions;
	}

	public ArrayList<OrnamentCaveType> getOrnamentCaveTypes() {
		OrnamentCaveType result = null;
		ArrayList<OrnamentCaveType> cavetypes = new ArrayList<OrnamentCaveType>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM OrnamentCaveType");
			while (rs.next()) {
				result = new OrnamentCaveType(rs.getInt("OrnamentCaveTypeID"), rs.getString("Name"));
				cavetypes.add(result);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cavetypes;
	}

	public ArrayList<OrnamentEntry> getOrnamentsWHERE(String sqlWhere) {
		ArrayList<OrnamentEntry> results = new ArrayList<OrnamentEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery((sqlWhere == null) ? "SELECT * FROM Ornaments" : "SELECT * FROM Ornaments WHERE " + sqlWhere);
			while (rs.next()) {
				results.add(new OrnamentEntry(rs.getInt("OrnamentID"), rs.getString("Code")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	/**
	 * @return
	 */
	public ArrayList<CeilingTypeEntry> getCeilingTypes() {
		ArrayList<CeilingTypeEntry> result = new ArrayList<CeilingTypeEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CeilingTypes");
			while (rs.next()) {
				result.add(new CeilingTypeEntry(rs.getInt("CeilingTypeID"), rs.getString("Name")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @return
	 */
	public CeilingTypeEntry getCeilingType(int id) {
		CeilingTypeEntry result = new CeilingTypeEntry();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CeilingTypes WHERE CeilingTypeID=" + id);
			if (rs.first()) {
				result = new CeilingTypeEntry(rs.getInt("CeilingTypeID"), rs.getString("Name"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @return
	 */
	public ArrayList<PreservationClassificationEntry> getPreservationClassifications() {
		ArrayList<PreservationClassificationEntry> result = new ArrayList<PreservationClassificationEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM PreservationClassifications");
			while (rs.next()) {
				result.add(new PreservationClassificationEntry(rs.getInt("PreservationClassificationID"), rs.getString("Name")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * TODO: only save existing areas depeding on cave type!
	 * 
	 * @param caveEntry
	 * @return
	 */
	public synchronized boolean updateCaveEntry(CaveEntry caveEntry) {
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = dbc.prepareStatement("UPDATE Caves SET OfficialNumber=?, HistoricName=?, OptionalHistoricName=?, CaveTypeID=?, DistrictID=?, "
					+ "RegionID=?, OrientationID=?, StateOfPreservation=?, Findings=?, Notes=?, FirstDocumentedBy=?, FirstDocumentedInYear=?, PreservationClassificationID=?, "
					+ "CaveGroupID=?, OptionalCaveSketch=?, C14url=?, C14DocumentFilename=? WHERE CaveID=?");
			pstmt.setString(1, caveEntry.getOfficialNumber());
			pstmt.setString(2, caveEntry.getHistoricName());
			pstmt.setString(3, caveEntry.getOptionalHistoricName());
			pstmt.setInt(4, caveEntry.getCaveTypeID());
			pstmt.setInt(5, caveEntry.getDistrictID());
			pstmt.setInt(6, caveEntry.getRegionID());
			pstmt.setInt(7, caveEntry.getOrientationID());
			pstmt.setString(8, caveEntry.getStateOfPerservation());
			pstmt.setString(9, caveEntry.getFindings());
			pstmt.setString(10, caveEntry.getNotes());
			pstmt.setString(11, caveEntry.getFirstDocumentedBy());
			pstmt.setInt(12, caveEntry.getFirstDocumentedInYear());
			pstmt.setInt(13, caveEntry.getPreservationClassificationID());
			pstmt.setInt(14, caveEntry.getCaveGroupID());
			pstmt.setString(15, caveEntry.getOptionalCaveSketch());
			pstmt.setString(16, caveEntry.getC14url());
			pstmt.setString(17, caveEntry.getC14DocumentFilename());
			pstmt.setInt(18, caveEntry.getCaveID());
			pstmt.executeUpdate();
			pstmt.close();
			for (CaveAreaEntry caEntry : caveEntry.getCaveAreaList()) {
				writeCaveArea(caEntry);
			}
			for (WallEntry wEntry : caveEntry.getWallList()) {
				writeWall(wEntry);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param caveEntry
	 * @return
	 */
	public synchronized int insertCaveEntry(CaveEntry caveEntry) {
		int newCaveID;
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = dbc.prepareStatement(
					"INSERT INTO Caves (OfficialNumber, HistoricName, OptionalHistoricName, CaveTypeID, DistrictID, RegionID, OrientationID, StateOfPreservation, "
							+ "Findings, Notes, FirstDocumentedBy, FirstDocumentedInYear, PreservationClassificationID, CaveGroupID, OptionalCaveSketch, C14url, C14DocumentFilename) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, caveEntry.getOfficialNumber());
			pstmt.setString(2, caveEntry.getHistoricName());
			pstmt.setString(3, caveEntry.getOptionalHistoricName());
			pstmt.setInt(4, caveEntry.getCaveTypeID());
			pstmt.setInt(5, caveEntry.getDistrictID());
			pstmt.setInt(6, caveEntry.getRegionID());
			pstmt.setInt(7, caveEntry.getOrientationID());
			pstmt.setString(8, caveEntry.getStateOfPerservation());
			pstmt.setString(9, caveEntry.getFindings());
			pstmt.setString(10, caveEntry.getNotes());
			pstmt.setString(11, caveEntry.getFirstDocumentedBy());
			pstmt.setInt(12, caveEntry.getFirstDocumentedInYear());
			pstmt.setInt(13, caveEntry.getPreservationClassificationID());
			pstmt.setInt(14, caveEntry.getCaveGroupID());
			pstmt.setString(15, caveEntry.getOptionalCaveSketch());
			pstmt.setString(16, caveEntry.getC14url());
			pstmt.setString(17, caveEntry.getC14DocumentFilename());
			newCaveID = pstmt.executeUpdate();
			pstmt.close();
			if (newCaveID > 0) {
				for (CaveAreaEntry caEntry : caveEntry.getCaveAreaList()) {
					writeCaveArea(caEntry);
				}
				for (WallEntry wEntry : caveEntry.getWallList()) {
					writeWall(wEntry);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return 0;
		}
		return newCaveID;
	}

	/**
	 * @param caveID
	 */
	public synchronized boolean updateC14DocumentFilename(int caveID, String c14DocumentFilename) {
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = dbc.prepareStatement("UPDATE Caves SET C14DocumentFilename=? WHERE CaveID=?");
			pstmt.setString(1, c14DocumentFilename);
			pstmt.setInt(2, caveID);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	public ArrayList<CaveGroupEntry> getCaveGroups() {
		ArrayList<CaveGroupEntry> result = new ArrayList<CaveGroupEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CaveGroups");
			while (rs.next()) {
				result.add(new CaveGroupEntry(rs.getInt("CaveGroupID"), rs.getString("Name")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @param username
	 * @param password
	 * @return
	 */
	public UserEntry userLogin(String username, String password) {
		UserEntry result = null;
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Users WHERE Username = '" + username + "' AND Password = '" + password + "'");
			if (rs.first()) {
				result = new UserEntry(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Firstname"), rs.getString("Lastname"),
						rs.getString("Email"), rs.getString("Affiliation"), rs.getInt("Accessrights"));
				result.setSessionID(UUID.randomUUID().toString());
				UserManager.getInstance().addUser(username, result);
			} else {
				System.err.println("wrong password for user " + username + ": hash = " + password);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @return
	 */
	public ArrayList<ImageTypeEntry> getImageTypes() {
		ArrayList<ImageTypeEntry> result = new ArrayList<ImageTypeEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ImageTypes");
			while (rs.next()) {
				result.add(new ImageTypeEntry(rs.getInt("ImageTypeID"), rs.getString("Name")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @param list
	 * @param depictionEntry
	 * @return
	 */
	public synchronized int insertDepictionEntry(DepictionEntry de, ArrayList<ImageEntry> imgEntryList,
			ArrayList<PictorialElementEntry> peEntryList) {
		int newDepictionID;
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = dbc.prepareStatement(
					"INSERT INTO Depictions (StyleID, Inscriptions, SeparateAksaras, Dating, Height, Width, PurchaseDate, VendorID, ExpeditionID, "
							+ "CurrentLocationID, Description, BackgroundColour, GeneralRemarks, OtherSuggestedIdentifications, "
							+ "StoryID, CaveID, WallID, AbsoluteLeft, AbsoluteTop, IconographyID, ModeOfRepresentationID) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, de.getStyleID());
			pstmt.setString(2, de.getInscriptions());
			pstmt.setString(3, de.getSeparateAksaras());
			pstmt.setString(4, de.getDating());
			pstmt.setDouble(5, de.getHeight());
			pstmt.setDouble(6, de.getWidth());
			pstmt.setDate(7, de.getPurchaseDate());
			pstmt.setInt(8, de.getVendorID());
			pstmt.setInt(9, de.getExpeditionID());
			pstmt.setInt(10, de.getCurrentLocationID());
			pstmt.setString(11, de.getDescription());
			pstmt.setString(12, de.getBackgroundColour());
			pstmt.setString(13, de.getGeneralRemarks());
			pstmt.setString(14, de.getOtherSuggestedIdentifications());
			pstmt.setInt(15, de.getStoryID());
			pstmt.setInt(16, de.getCaveID());
			pstmt.setInt(17, de.getWallID());
			pstmt.setInt(18, de.getAbsoluteLeft());
			pstmt.setInt(19, de.getAbsoluteTop());
			pstmt.setInt(20, de.getIconographyID());
			pstmt.setInt(21, de.getModeOfRepresentationID());
			newDepictionID = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return 0;
		}
		if (newDepictionID > 0) {
			if (imgEntryList.size() > 0) {
				updateDepictionImageRelation(de.getDepictionID(), imgEntryList);
			}
			if (peEntryList.size() > 0) {
				updateDepictionPERelation(de.getDepictionID(), peEntryList);
			}
		}
		return newDepictionID;
	}

	/**
	 * @param correspondingDepictionEntry
	 * @param imgEntryList
	 * @param selectedPEList
	 * @return <code>true</code> when operation is successful
	 */
	public synchronized boolean updateDepictionEntry(DepictionEntry de, ArrayList<ImageEntry> imgEntryList,
			ArrayList<PictorialElementEntry> selectedPEList) {
		// System.err.println("==> updateDepictionEntry called");
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = dbc.prepareStatement(
					"UPDATE Depictions SET StyleID=?, Inscriptions=?, SeparateAksaras=?, Dating=?, Height=?, Width=?, PurchaseDate=?, VendorID=?, ExpeditionID=?, "
							+ "CurrentLocationID=?, Description=?, BackgroundColour=?, GeneralRemarks=?, OtherSuggestedIdentifications=?, "
							+ "StoryID=?, CaveID=?, WallID=?, AbsoluteLeft=?, AbsoluteTop=?, IconographyID=?, ModeOfRepresentationID=? WHERE DepictionID=?");
			pstmt.setInt(1, de.getStyleID());
			pstmt.setString(2, de.getInscriptions());
			pstmt.setString(3, de.getSeparateAksaras());
			pstmt.setString(4, de.getDating());
			pstmt.setDouble(5, de.getHeight());
			pstmt.setDouble(6, de.getWidth());
			pstmt.setDate(7, de.getPurchaseDate());
			pstmt.setInt(8, de.getVendorID());
			pstmt.setInt(9, de.getExpeditionID());
			pstmt.setInt(10, de.getCurrentLocationID());
			pstmt.setString(11, de.getDescription());
			pstmt.setString(12, de.getBackgroundColour());
			pstmt.setString(13, de.getGeneralRemarks());
			pstmt.setString(14, de.getOtherSuggestedIdentifications());
			pstmt.setInt(15, de.getStoryID());
			pstmt.setInt(16, de.getCaveID());
			pstmt.setInt(17, de.getWallID());
			pstmt.setInt(18, de.getAbsoluteLeft());
			pstmt.setInt(19, de.getAbsoluteTop());
			pstmt.setInt(20, de.getIconographyID());
			pstmt.setInt(21, de.getModeOfRepresentationID());
			pstmt.setInt(22, de.getDepictionID());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		if (imgEntryList.size() > 0) {
			updateDepictionImageRelation(de.getDepictionID(), imgEntryList);
		}
		if (selectedPEList.size() > 0) {
			updateDepictionPERelation(de.getDepictionID(), selectedPEList);
		}
		return true;
	}

	private synchronized void updateDepictionImageRelation(int depictionID, ArrayList<ImageEntry> imgEntryList) {
		deleteEntry("DELETE FROM DepictionImageRelation WHERE DepictionID=" + depictionID);
		Connection dbc = getConnection();
		PreparedStatement pstmt;
		// System.err.println("==> updateDepictionImageRelation called");
		try {
			String insertSqlString = "INSERT INTO DepictionImageRelation VALUES ";
			for (int i = 0; i < imgEntryList.size(); ++i) {
				if (i == 0) {
					insertSqlString = insertSqlString.concat("(?, ?, ?)");
				} else {
					insertSqlString = insertSqlString.concat(", (?, ?, ?)");
				}
			}
			pstmt = dbc.prepareStatement(insertSqlString);
			for (ImageEntry entry : imgEntryList) {
				if (imgEntryList.indexOf(entry) == 0) {
					pstmt.setInt(1, depictionID);
					pstmt.setInt(2, entry.getImageID());
					pstmt.setInt(3, 1);
				} else {
					int idx = imgEntryList.indexOf(entry);
					pstmt.setInt(idx * 3 + 1, depictionID);
					pstmt.setInt(idx * 3 + 2, entry.getImageID());
					pstmt.setInt(idx * 3 + 3, 0);
				}
			}
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return;
		}
	}

	private synchronized void updateDepictionPERelation(int depictionID, ArrayList<PictorialElementEntry> peEntryList) {
		deleteEntry("DELETE FROM DepictionPERelation WHERE DepictionID=" + depictionID);
		String insertSqlString = "INSERT INTO DepictionPERelation VALUES ";
		Iterator<PictorialElementEntry> it = peEntryList.iterator();
		System.err.println("==> updateDepictionPERelation called");
		while (it.hasNext()) {
			PictorialElementEntry entry = it.next();
			if (peEntryList.indexOf(entry) == 0) {
				insertSqlString = insertSqlString.concat("(" + depictionID + ", " + entry.getPictorialElementID() + ")");
			} else {
				insertSqlString = insertSqlString.concat(", (" + depictionID + ", " + entry.getPictorialElementID() + ")");
			}
		}
		insertEntry(insertSqlString);
	}

	/**
	 * @return
	 */
	public ArrayList<ModeOfRepresentationEntry> getModesOfRepresentations() {
		ArrayList<ModeOfRepresentationEntry> result = new ArrayList<ModeOfRepresentationEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ModesOfRepresentation");
			while (rs.next()) {
				result.add(new ModeOfRepresentationEntry(rs.getInt("ModeOfRepresentationID"), rs.getString("Name")));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<CaveAreaEntry> getCaveAreas(int caveID) {
		CaveAreaEntry caEntry;
		Connection dbc = getConnection();
		PreparedStatement caveAreaStatement;
		ArrayList<CaveAreaEntry> result = new ArrayList<CaveAreaEntry>();
		ResultSet caveAreaRS;
		try {
			caveAreaStatement = dbc.prepareStatement("SELECT * FROM CaveAreas WHERE CaveID=?");
			caveAreaStatement.setInt(1, caveID);
			caveAreaRS = caveAreaStatement.executeQuery();
			while (caveAreaRS.next()) {
				caEntry = new CaveAreaEntry(caveAreaRS.getInt("CaveID"), caveAreaRS.getString("CaveAreaLabel"),
						caveAreaRS.getDouble("ExpeditionMeasuredWidth"), caveAreaRS.getDouble("ExpeditionMeasuredHeight"),
						caveAreaRS.getDouble("ExpeditionMeasuredLength"), caveAreaRS.getDouble("ModernMeasuredWidth"),
						caveAreaRS.getDouble("ModernMeasuredHeight"), caveAreaRS.getDouble("ModernMeasuredLength"),
						caveAreaRS.getInt("PreservationClassificationID"), caveAreaRS.getInt("CeilingTypeID1"), caveAreaRS.getInt("CeilingTypeID2"),
						caveAreaRS.getInt("CeilingPreservationClassificationID1"), caveAreaRS.getInt("CeilingPreservationClassificationID2"),
						caveAreaRS.getInt("FloorPreservationClassificationID"));
				result.add(caEntry);
			}
			caveAreaStatement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		return result;
	}

	protected synchronized boolean writeCaveArea(CaveAreaEntry entry) {
		int newID;
		Connection dbc = getConnection();
		PreparedStatement caveAreaStatement;
		try {
			caveAreaStatement = dbc.prepareStatement(
					"INSERT INTO CaveAreas (CaveID, CaveAreaLabel, ExpeditionMeasuredWidth, ExpeditionMeasuredLength, ExpeditionMeasuredHeight, ModernMeasuredWidth, ModernMeasuredLength, ModernMeasuredHeight, "
							+ "PreservationClassificationID, CeilingTypeID1, CeilingTypeID2, CeilingPreservationClassificationID1, CeilingPreservationClassificationID2, FloorPreservationClassificationID=?) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE "
							+ "ExpeditionMeasuredWidth=?, ExpeditionMeasuredLength=?, ExpeditionMeasuredHeight=?, ModernMeasuredWidth=?, ModernMeasuredLength=?, ModernMeasuredHeight=?, "
							+ "PreservationClassificationID=?, CeilingTypeID1=?, CeilingTypeID2=?, CeilingPreservationClassificationID1=?, CeilingPreservationClassificationID2=?, FloorPreservationClassificationID=?");
			caveAreaStatement.setInt(1, entry.getCaveID());
			caveAreaStatement.setString(2, entry.getCaveAreaLabel());
			caveAreaStatement.setDouble(3, entry.getExpeditionWidth());
			caveAreaStatement.setDouble(4, entry.getExpeditionLength());
			caveAreaStatement.setDouble(5, entry.getExpeditionHeight());
			caveAreaStatement.setDouble(6, entry.getModernWidth());
			caveAreaStatement.setDouble(7, entry.getModernLength());
			caveAreaStatement.setDouble(8, entry.getModernHeight());
			caveAreaStatement.setInt(9, entry.getPreservationClassificationID());
			caveAreaStatement.setInt(10, entry.getCeilingTypeID1());
			caveAreaStatement.setInt(11, entry.getCeilingTypeID2());
			caveAreaStatement.setInt(12, entry.getCeilingPreservationClassificationID1());
			caveAreaStatement.setInt(13, entry.getCeilingPreservationClassificationID2());
			caveAreaStatement.setInt(14, entry.getFloorPreservationClassificationID());
			caveAreaStatement.setDouble(15, entry.getExpeditionWidth());
			caveAreaStatement.setDouble(16, entry.getExpeditionLength());
			caveAreaStatement.setDouble(17, entry.getExpeditionHeight());
			caveAreaStatement.setDouble(18, entry.getModernWidth());
			caveAreaStatement.setDouble(19, entry.getModernLength());
			caveAreaStatement.setDouble(20, entry.getModernHeight());
			caveAreaStatement.setInt(21, entry.getPreservationClassificationID());
			caveAreaStatement.setInt(22, entry.getCeilingTypeID1());
			caveAreaStatement.setInt(23, entry.getCeilingTypeID2());
			caveAreaStatement.setInt(24, entry.getCeilingPreservationClassificationID1());
			caveAreaStatement.setInt(25, entry.getCeilingPreservationClassificationID2());
			caveAreaStatement.setInt(26, entry.getFloorPreservationClassificationID());
			newID = caveAreaStatement.executeUpdate();
			caveAreaStatement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		return (newID > 0);
	}

	protected synchronized boolean writeWall(WallEntry entry) {
		int newID;
		Connection dbc = getConnection();
		PreparedStatement wallStatement;
		try {
			wallStatement = dbc.prepareStatement("INSERT INTO Walls (CaveID, WallLocationID, PreservationClassificationID, Width, Height) "
					+ "VALUES (?, ?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE " + "PreservationClassificationID=?, Width=?, Height=?");
			wallStatement.setInt(1, entry.getCaveID());
			wallStatement.setInt(2, entry.getWallLocationID());
			wallStatement.setInt(3, entry.getPreservationClassificationID());
			wallStatement.setDouble(4, entry.getWidth());
			wallStatement.setDouble(5, entry.getHeight());
			wallStatement.setInt(6, entry.getPreservationClassificationID());
			wallStatement.setDouble(7, entry.getWidth());
			wallStatement.setDouble(8, entry.getHeight());
			newID = wallStatement.executeUpdate();
			wallStatement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		return (newID > 0);
	}

	/**
	 * @param caveID
	 * @return
	 */
	public ArrayList<WallEntry> getWalls(int caveID) {
		WallEntry entry;
		Connection dbc = getConnection();
		PreparedStatement wallStatement;
		ArrayList<WallEntry> result = new ArrayList<WallEntry>();
		ResultSet wallRS;
		try {
			wallStatement = dbc.prepareStatement("SELECT * FROM Walls WHERE CaveID=?");
			wallStatement.setInt(1, caveID);
			wallRS = wallStatement.executeQuery();
			while (wallRS.next()) {
				entry = new WallEntry(wallRS.getInt("CaveID"), wallRS.getInt("WallLocationID"), wallRS.getInt("PreservationClassificationID"),
						wallRS.getDouble("Width"), wallRS.getDouble("Height"));
				result.add(entry);
			}
			wallStatement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * @return
	 */
	public ArrayList<WallLocationEntry> getWallLocations() {
		WallLocationEntry entry;
		Connection dbc = getConnection();
		PreparedStatement wallLocationStatement;
		ArrayList<WallLocationEntry> result = new ArrayList<WallLocationEntry>();
		ResultSet wallLocationRS;
		try {
			wallLocationStatement = dbc.prepareStatement("SELECT * FROM WallLocations");
			wallLocationRS = wallLocationStatement.executeQuery();
			while (wallLocationRS.next()) {
				entry = new WallLocationEntry(wallLocationRS.getInt("WallLocationID"), wallLocationRS.getString("Label"),
						wallLocationRS.getString("CaveAreaLabel"));
				result.add(entry);
			}
			wallLocationStatement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
		return result;
	}

}
