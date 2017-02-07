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

import com.google.gwt.user.client.rpc.AsyncCallback;

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

public interface DatabaseServiceAsync {

	void dbServer(String name, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getDistricts(AsyncCallback<ArrayList<DistrictEntry>> callback) throws IllegalArgumentException;

	void getImage(int imageID, AsyncCallback<ImageEntry> callback) throws IllegalArgumentException;

	void getImages(AsyncCallback<ArrayList<ImageEntry>> callback) throws IllegalArgumentException;

	void getImages(String where, AsyncCallback<ArrayList<ImageEntry>> callback) throws IllegalArgumentException;

	void getPhotographer(AsyncCallback<ArrayList<PhotographerEntry>> callback) throws IllegalArgumentException;

	void getCaves(AsyncCallback<ArrayList<CaveEntry>> callback) throws IllegalArgumentException;

	void getCavesbyDistrictID(int DistrictID, AsyncCallback<ArrayList<CaveEntry>> callback) throws IllegalArgumentException;

	void getOrnaments(AsyncCallback<ArrayList<OrnamentEntry>> callback) throws IllegalArgumentException;

	void getOrnamentsOfOtherCultures(AsyncCallback<ArrayList<OrnamentOfOtherCulturesEntry>> callback)
			throws IllegalArgumentException;

	void updateEntry(String sqlUpdate, AsyncCallback<Boolean> callback) throws IllegalArgumentException;

	void deleteEntry(String sqlDelete, AsyncCallback<Boolean> callback) throws IllegalArgumentException;

	void saveOrnamentEntry(OrnamentEntry ornamentEntry, AsyncCallback<Boolean> callback) throws IllegalArgumentException;

	void insertEntry(String sqlInsert, AsyncCallback<Integer> callback) throws IllegalArgumentException;

	void getDepictions(AsyncCallback<ArrayList<DepictionEntry>> callback) throws IllegalArgumentException;

	void getIconography(AsyncCallback<ArrayList<IconographyEntry>> callback) throws IllegalArgumentException;

	void getPictorialElements(AsyncCallback<ArrayList<PictorialElementEntry>> asyncCallback) throws IllegalArgumentException;

	void getCaveTypebyID(int caveTypeID, AsyncCallback<CaveTypeEntry> callback) throws IllegalArgumentException;

	void getCaveTypes(AsyncCallback<ArrayList<CaveTypeEntry>> callback) throws IllegalArgumentException;

	void getDepictionEntry(int depictionID, AsyncCallback<DepictionEntry> callback);

	void getVendors(AsyncCallback<ArrayList<VendorEntry>> callback) throws IllegalArgumentException;

	void getStyles(AsyncCallback<ArrayList<StyleEntry>> callback) throws IllegalArgumentException;

	void getExpeditions(AsyncCallback<ArrayList<ExpeditionEntry>> asyncCallback) throws IllegalArgumentException;

	void getPublicationEntry(int id, AsyncCallback<PublicationEntry> asyncCallback) throws IllegalArgumentException;

	void getAuthorEntry(int id, AsyncCallback<AuthorEntry> asyncCallback) throws IllegalArgumentException;
	
	void getMasterImageEntryForDepiction(int depictionID, AsyncCallback<ImageEntry> asyncCallback) throws IllegalArgumentException;
	
	void getDepictionsbyWallID(int wallID, AsyncCallback<ArrayList<DepictionEntry>> asyncCallback) throws IllegalArgumentException;
	
	void saveDepiction(int depictionID, int absoluteLeft,int absoluteTop, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
}
