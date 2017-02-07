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
package de.cses.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.cses.shared.AuthorEntry;
import de.cses.shared.CaveEntry;
import de.cses.shared.CaveTypeEntry;
import de.cses.shared.DepictionEntry;
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
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("db")
public interface DatabaseService extends RemoteService {

	String dbServer(String name) throws IllegalArgumentException;

	ArrayList<DistrictEntry> getDistricts() throws IllegalArgumentException;

	ArrayList<ImageEntry> getImages() throws IllegalArgumentException;

	ArrayList<ImageEntry> getImages(String where) throws IllegalArgumentException;

	ArrayList<PhotographerEntry> getPhotographer() throws IllegalArgumentException;

	ImageEntry getImage(int imageID) throws IllegalArgumentException;

	ArrayList<CaveEntry> getCaves() throws IllegalArgumentException;

	ArrayList<CaveEntry> getCavesbyDistrictID(int DistrictID) throws IllegalArgumentException;

	ArrayList<OrnamentEntry> getOrnaments() throws IllegalArgumentException;

	ArrayList<OrnamentOfOtherCulturesEntry> getOrnamentsOfOtherCultures() throws IllegalArgumentException;

	ArrayList<DepictionEntry> getDepictions() throws IllegalArgumentException;

	DepictionEntry getDepictionEntry(int depictionID) throws IllegalArgumentException;

	boolean updateEntry(String sqlUpdate);

	boolean deleteEntry(String sqlDelete);

	int insertEntry(String sqlInsert);

	ArrayList<IconographyEntry> getIconography() throws IllegalArgumentException;

	ArrayList<PictorialElementEntry> getPictorialElements() throws IllegalArgumentException;

	boolean saveOrnamentEntry(OrnamentEntry ornamentEntry) throws IllegalArgumentException;

	CaveTypeEntry getCaveTypebyID(int caveTypeID) throws IllegalArgumentException;

	ArrayList<CaveTypeEntry> getCaveTypes() throws IllegalArgumentException;

	ArrayList<VendorEntry> getVendors() throws IllegalArgumentException;

	ArrayList<StyleEntry> getStyles() throws IllegalArgumentException;

	ArrayList<ExpeditionEntry> getExpeditions() throws IllegalArgumentException;

	PublicationEntry getPublicationEntry(int id);

	AuthorEntry getAuthorEntry(int id);

	ImageEntry getMasterImageEntryForDepiction(int depictionID);
	
	ArrayList<DepictionEntry> getDepictionsbyWallID(int wallID);
	
	String saveDepiction(int depictionID, int absoluteLeft, int absoluteTop);

}
