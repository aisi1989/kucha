/*
 * Copyright 2016 
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import de.cses.shared.AuthorEntry;
import de.cses.shared.CaveEntry;
import de.cses.shared.DepictionEntry;
import de.cses.shared.CaveTypeEntry;
import de.cses.shared.DistrictEntry;
import de.cses.shared.ExpeditionEntry;
import de.cses.shared.IconographyEntry;
import de.cses.shared.ImageEntry;
import de.cses.shared.OrnamentEntry;
import de.cses.shared.OrnamentOfOtherCulturesEntry;
import de.cses.shared.PhotographerEntry;
import de.cses.shared.PictorialElementEntry;
import de.cses.shared.PublicationEntry;
import de.cses.shared.StyleEntry;
import de.cses.shared.VendorEntry;

/**
 * This is the central Database connector. Here are all method located that we
 * need for standard database operations, including use login and access
 * management.
 * 
 * @author alingnau
 *
 */
public class MysqlConnector {

	private static String url = "jdbc:mysql://kucha.informatik.hu-berlin.de/infosys?useUnicode=true&characterEncoding=UTF-8"; //$NON-NLS-1$
	private static String user = Messages.getString("MysqlConnector.db.user");
	private static String password = Messages.getString("MysqlConnector.db.password");
	private int auto_increment_id;

	private static MysqlConnector instance = null;

	/**
	 * By calling this method, we avoid that a new instance will be created if
	 * there is already one existing. If this method is called without an instance
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

		try {
			Class.forName("org.mariadb.jdbc.Driver"); //$NON-NLS-1$
			connection = DriverManager.getConnection(MysqlConnector.url, MysqlConnector.user, MysqlConnector.password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Connection getConnection() {
		try {
			if (connection == null || !connection.isValid(5)) {
				connection = DriverManager.getConnection(MysqlConnector.url, MysqlConnector.user, MysqlConnector.password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	/**
	 * This is a test method that can be used for testing purpose only. Likely to
	 * disappear in later versions!
	 * 
	 * @return
	 */
	public synchronized Hashtable<String, String> getTestTable() {

		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();

			String sql = "SELECT * FROM test"; //$NON-NLS-1$

			ResultSet rs = stmt.executeQuery(sql);

			Hashtable<String, String> results = new Hashtable<String, String>();

			String name, age;
			while (rs.next()) {
				name = rs.getString("Name"); //$NON-NLS-1$
				age = rs.getString("Age"); //$NON-NLS-1$
				results.put(name, age);
				System.err.println("(" + name + "," + age + ") read from database"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			rs.close();
			stmt.close();
			dbc.close();
			System.err.println("Database request finished"); //$NON-NLS-1$
			return results;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Selects all districts from the table 'Districts' in the database
	 * 
	 * @return
	 */
	public synchronized ArrayList<DistrictEntry> getDistricts() {
		ArrayList<DistrictEntry> DistrictEntries = new ArrayList<DistrictEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			String sql = "SELECT * FROM Districts"; //$NON-NLS-1$

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				DistrictEntry DistrictEntry = new DistrictEntry();
				DistrictEntry.setDistrictID(rs.getInt("DistrictID"));
				DistrictEntry.setName(rs.getString("Name"));
				DistrictEntry.setDescription(rs.getString("Description"));
				DistrictEntries.add(DistrictEntry);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return DistrictEntries;
	}

	/**
	 * Creates a new image entry in the table 'Images'
	 * 
	 * @return auto incremented primary key 'ImageID'
	 */
	public synchronized int createNewImageEntry() {
		Connection dbc = getConnection();
		Statement stmt;
		int generatedKey = -1;
		try {
			stmt = dbc.createStatement();
			stmt.execute("INSERT INTO Images (Title,Comment,ImageType) VALUES ('New Image','please type your comment here','photo')",
					Statement.RETURN_GENERATED_KEYS);
			ResultSet keys = stmt.getGeneratedKeys();
			while (keys.next()) {
				// there should only be 1 key returned here but we need to modify this
				// in case
				// we have requested multiple new entries. works for the moment
				generatedKey = keys.getInt(1);
				System.err.println("result key " + generatedKey);
			}
			keys.close();
			stmt.close();
			dbc.close();
			System.err.println("Database request finished"); //$NON-NLS-1$
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return generatedKey;
	}

	/**
	 * Executes a pre-defined SQL INSERT statement and returns the generated
	 * (auto-increment) unique key from the table.
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
				System.err.println("result key " + generatedKey);
			}
			keys.close();
			stmt.close();
			dbc.close();
			System.err.println("Database request finished"); //$NON-NLS-1$
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
			dbc.close();
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
	public synchronized boolean deleteEntry(String sqlDelete) {
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			stmt.execute(sqlDelete);
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public synchronized ArrayList<DepictionEntry> getDepictions() {
		ArrayList<DepictionEntry> results = new ArrayList<DepictionEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Depictions");
			while (rs.next()) {
				results.add(new DepictionEntry(rs.getInt("DepictionID"), rs.getInt("StyleID"), rs.getString("Inscriptions"),
						rs.getString("Dating"), rs.getString("Description"), rs.getString("BackgroundColour"), rs.getString("Material"),
						rs.getString("GeneralRemarks"), rs.getString("OtherSuggestedIdentifications"), rs.getInt("Dimension.width"),
						rs.getInt("Dimension.height"), rs.getDate("DateOfAcquisition"), rs.getInt("ExpeditionID"), rs.getDate("PurchaseDate"),
						rs.getInt("CurrentLocationID"), rs.getInt("VendorID"), rs.getInt("StoryID"), rs.getInt("CaveID")));
			}
			rs.close();
			stmt.close();
			dbc.close();
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
						rs.getString("Dating"), rs.getString("Description"), rs.getString("BackgroundColour"), rs.getString("Material"),
						rs.getString("GeneralRemarks"), rs.getString("OtherSuggestedIdentifications"), rs.getInt("Dimension.width"),
						rs.getInt("Dimension.height"), rs.getDate("DateOfAcquisition"), rs.getInt("ExpeditionID"), rs.getDate("PurchaseDate"),
						rs.getInt("CurrentLocationID"), rs.getInt("VendorID"), rs.getInt("StoryID"), rs.getInt("CaveID"));
			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @return ArrayList<String> with result from 'SELECT * FROM Images'
	 */
	public synchronized ArrayList<ImageEntry> getImageEntries() {
		ArrayList<ImageEntry> results = new ArrayList<ImageEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Images");
			while (rs.next()) {
				results.add(new ImageEntry(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getString(6),
						rs.getDate(7), rs.getString(8)));
			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<ImageEntry> getImageEntries(String sqlWhere) {
		ArrayList<ImageEntry> results = new ArrayList<ImageEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Images WHERE " + sqlWhere);
			while (rs.next()) {
				results.add(new ImageEntry(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getString(6),
						rs.getDate(7), rs.getString(8)));
			}
			rs.close();
			stmt.close();
			dbc.close();
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
	public synchronized ImageEntry getImageEntry(int imageID) {
		Connection dbc = getConnection();
		ImageEntry result = null;
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Images WHERE ImageID=" + imageID);
			while (rs.next()) {
				result = new ImageEntry(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getString(6),
						rs.getDate(7), rs.getString(8));
			}
			rs.close();
			stmt.close();
			dbc.close();
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
	public synchronized ArrayList<PhotographerEntry> getPhotographerEntries() {
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
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	// public synchronized boolean updateImage(String data) {
	// Connection dbc = getConnection();
	// Statement stmt;
	// try {
	// stmt = dbc.createStatement();
	// int result= stmt.executeUpdate(data);
	// stmt.close();
	// dbc.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// return false;
	// }
	// return true;
	// }

	public synchronized ArrayList<CaveEntry> getCaves() {
		ArrayList<CaveEntry> results = new ArrayList<CaveEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Caves");
			while (rs.next()) {
				results.add(new CaveEntry(rs.getInt("CaveID"), rs.getInt("DistrictID"), rs.getString("OfficialName"),
						rs.getString("OfficialNumber"), rs.getString("HistoricName"), rs.getInt("CaveTypeID"),
						rs.getString("StateOfPreservation"), rs.getString("Orientation"), rs.getString("Pedestals"),
						rs.getString("Findings")));

			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<CaveEntry> getCavesbyDistrictID(int DistrictID) {
		ArrayList<CaveEntry> results = new ArrayList<CaveEntry>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Caves WHERE DistrictID =" + DistrictID);
			while (rs.next()) {
				results.add(new CaveEntry(rs.getInt("CaveID"), rs.getInt("DistrictID"), rs.getString("OfficialName"),
						rs.getString("OfficialNumber"), rs.getString("HistoricName"), rs.getInt("CaveTypeID"),
						rs.getString("StateOfPreservation"), rs.getString("Orientation"), rs.getString("Pedestals"),
						rs.getString("Findings")));

			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<OrnamentEntry> getOrnaments() {
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
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<OrnamentOfOtherCulturesEntry> getOrnametsOfOtherCultures() {
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
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized CaveTypeEntry getCaveTypebyID(int caveTypeID) {
		Connection dbc = getConnection();
		CaveTypeEntry resultCaveType = new CaveTypeEntry();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CaveType WHERE CaveTypeID =" + caveTypeID);
			while (rs.next()) {
				resultCaveType.setEnDescription(rs.getString("DescriptionEN"));
				resultCaveType.setEnShortname(rs.getString("NameEN"));

			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return resultCaveType;
	}

	public synchronized ArrayList<CaveTypeEntry> getCaveTypes() {
		System.err.println("IN METHODE DRIN");
		Connection dbc = getConnection();
		ArrayList<CaveTypeEntry> results = new ArrayList<CaveTypeEntry>();
		Statement stmt;
		
		
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CaveType");
			while (rs.next()) {
				System.err.println("cave type gefunden");
				CaveTypeEntry caveType = new CaveTypeEntry(rs.getInt("CaveTypeID"), rs.getString("NameEN"),
						rs.getString("DescriptionEN"));
				results.add(caveType);
			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			System.err.println("error in db zugriff");
			e.printStackTrace();
			return results;
		}
		return results;

	}

	public synchronized boolean saveOrnamentEntry(OrnamentEntry ornamentEntry) {
		System.err.println("reached saving function");

		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt
					.executeQuery("INSERT INTO Ornaments (Code, Description, Remarks, Interpretation, Sketch, OrnamentReferences) VALUES ("
							+ ornamentEntry.getCode() + "," + ornamentEntry.getDescription() + "," + ornamentEntry.getRemarks() + ","
							+ ornamentEntry.getInterpretation() + "," + ornamentEntry.getSketch() + "," + ornamentEntry.getReferences() + ")");
			rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			while (rs.next()) {
				auto_increment_id = rs.getInt(1);
			}
			System.err.println("inserted into ornaments got autoid");
			for (int i = 0; i < ornamentEntry.getCavesRelations().size(); i++) {
				stmt = dbc.createStatement();
				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getRelatedOrnamentsRelationID().size(); j++) {
					rs = stmt.executeQuery("INSERT INTO RelatedOrnamentsRelation (OrnamentID1, OrnamentID2, CaveID) VALUES ("
							+ auto_increment_id + "," + ornamentEntry.getCavesRelations().get(i).getRelatedOrnamentsRelationID().get(j) + ","
							+ ornamentEntry.getCavesRelations().get(i).getCaveID() + ")");
				}
				System.err.println("inserted related ornaments");
				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getSimilarOrnamentsRelationID().size(); j++) {
					rs = stmt.executeQuery("INSERT INTO SimilarOrnamentsRelation (OrnamentID1, OrnamentID2, CaveID) VALUES ("
							+ auto_increment_id + "," + ornamentEntry.getCavesRelations().get(i).getSimilarOrnamentsRelationID().get(j) + ","
							+ ornamentEntry.getCavesRelations().get(i).getCaveID() + ")");
				}
				System.err.println("inserted similar ornaments");
				for (int j = 0; j < ornamentEntry.getCavesRelations().get(i).getOtherCulturalOrnamentsRelationID().size(); j++) {
					rs = stmt.executeQuery(
							"INSERT INTO OrnamentsOfOtherCulturesRelation (OrnamentID, OrnamentOfOtherCulturesID, CaveID) VALUES ("
									+ auto_increment_id + ","
									+ ornamentEntry.getCavesRelations().get(i).getOtherCulturalOrnamentsRelationID().get(j) + ","
									+ ornamentEntry.getCavesRelations().get(i).getCaveID() + ")");
				}
				System.err.println("inserted ornaments of other cultures");
				rs = stmt.executeQuery(
						"INSERT INTO CaveOrnamentRelation (CaveID, OrnamentID, Style, Orientation, Structure, MainTypologicalClass, Colours, Position, Function, CavePart, Notes,GroupOfOrnaments) VALUES ("
								+ ornamentEntry.getCavesRelations().get(i).getCaveID() + "," + auto_increment_id + ","
								+ ornamentEntry.getCavesRelations().get(i).getStyle() + ","
								+ ornamentEntry.getCavesRelations().get(i).getOrientation() + ","
								+ ornamentEntry.getCavesRelations().get(i).getStructure() + ","
								+ ornamentEntry.getCavesRelations().get(i).getMainTopologycalClass() + ","
								+ ornamentEntry.getCavesRelations().get(i).getColours() + ","
								+ ornamentEntry.getCavesRelations().get(i).getPosition() + ","
								+ ornamentEntry.getCavesRelations().get(i).getFunction() + ","
								+ ornamentEntry.getCavesRelations().get(i).getCavepart() + ","
								+ ornamentEntry.getCavesRelations().get(i).getNotes() + "," + ornamentEntry.getCavesRelations().get(i).getGroup()
								+ ")");
				System.err.println("inserted cave relation");
			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public synchronized ArrayList<IconographyEntry> getIconography() {
		ArrayList<IconographyEntry> root = getIconographyEntries(0);

		for (IconographyEntry item : root) {
			processIconographyTree(item);
		}
		return root;
	}

	private synchronized void processIconographyTree(IconographyEntry parent) {
		ArrayList<IconographyEntry> children = getIconographyEntries(parent.getIconographyID());
		if (children != null) {
			parent.setChildren(children);
			for (IconographyEntry child : children) {
				processIconographyTree(child);
			}
		}
	}

	protected synchronized ArrayList<IconographyEntry> getIconographyEntries(int parentID) {
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
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<PictorialElementEntry> getPictorialElements() {
		ArrayList<PictorialElementEntry> root = getPictorialElementEntries(0);

		for (PictorialElementEntry item : root) {
			processPictorialElementsTree(item);
		}
		return root;
	}

	private synchronized void processPictorialElementsTree(PictorialElementEntry parent) {
		ArrayList<PictorialElementEntry> children = getPictorialElementEntries(parent.getPictorialElementID());
		if (children != null) {
			parent.setChildren(children);
			for (PictorialElementEntry child : children) {
				processPictorialElementsTree(child);
			}
		}
	}

	protected synchronized ArrayList<PictorialElementEntry> getPictorialElementEntries(int parentID) {
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
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<VendorEntry> getVendors() {
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
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<StyleEntry> getStyles() {
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
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<ExpeditionEntry> getExpeditions() {
		ArrayList<ExpeditionEntry> results = new ArrayList<ExpeditionEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Expeditions");
			while (rs.next()) {
				results.add(new ExpeditionEntry(rs.getInt("ExpeditionID"), rs.getString("Name"), rs.getString("Leader"),
						rs.getDate("StartDate"), rs.getDate("EndDate")));
			}
			rs.close();
			stmt.close();
			dbc.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized ArrayList<PublicationEntry> getPublications() {
		ArrayList<PublicationEntry> results = new ArrayList<PublicationEntry>();
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Publications");
			while (rs.next()) {
				results.add(
						new PublicationEntry(rs.getInt("PublicationID"), rs.getString("Editors"), rs.getString("Type"), rs.getString("DOI"),
								rs.getString("Pages"), rs.getDate("Year"), rs.getInt("PublisherID"), rs.getString("Title.English"),
								rs.getString("Title.Phonetic"), rs.getString("Title.Original"), rs.getString("Abstract")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	public synchronized PublicationEntry getPublicationEntry(int id) {
		PublicationEntry result = null;
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Publications WHERE PublicationID=" + id);
			while (rs.next()) {
				result = new PublicationEntry(rs.getInt("PublicationID"), rs.getString("Editors"), rs.getString("Type"),
						rs.getString("DOI"), rs.getString("Pages"), rs.getDate("Year"), rs.getInt("PublisherID"),
						rs.getString("Title.English"), rs.getString("Title.Phonetic"), rs.getString("Title.Original"),
						rs.getString("Abstract"));
			}
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
	public synchronized AuthorEntry getAuthorEntry(int id) {
		AuthorEntry result = null;
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Authors WHERE AuthorID=" + id);
			while (rs.next()) {
				result = new AuthorEntry(rs.getInt("AuthorID"), rs.getString("Lastname"), rs.getString("Firstname"),
						rs.getDate("KuchaVisitDate"), rs.getString("Affiliation"), rs.getString("Email"), rs.getString("Homepage"));
			}
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
	public synchronized int getRelatedMasterImageID(int depictionID) {
		int result = 0;
		Connection dbc = getConnection();

		Statement stmt;
		try {
			stmt = dbc.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM DepictionImageRelation WHERE DepictionID=" + depictionID);
			while (rs.next() && (result == 0)) {
				if (rs.getBoolean("IsMaster")) {
					result = rs.getInt("ImageID");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return result;
	}
	
	public ArrayList<DepictionEntry> getAllDepictionsbyWall(int wallID){
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
	
	public String saveDepiction(int depictionID, int AbsoluteLeft, int AbsoluteTop){
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			stmt.executeQuery("UPDATE Depictions SET AbsoluteLeft ="+ AbsoluteLeft + ", AbsoluteTop ="+ AbsoluteTop+ " WHERE DepictionID ="+ depictionID );
	
		} catch (SQLException e) {
			e.printStackTrace();
			return "failed to save depiction";
			
		}
		return "saved";
		
		
	}

}
