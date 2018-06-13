/*
 * Copyright 2018 
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
package de.cses.server.json;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.cses.shared.CaveEntry;
import de.cses.shared.DepictionEntry;
import de.cses.shared.ImageEntry;

/**
 * @author alingnau
 *
 */
public class DepictionSerializer implements JsonSerializer<DepictionEntry> {

	/**
	 * 
	 */
	public DepictionSerializer() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(DepictionEntry entry, Type typeOfSource, JsonSerializationContext context) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("depictionID", entry.getDepictionID());
		jsonObj.addProperty("description", entry.getDescription());
		jsonObj.add("cave", serializeCave(entry.getCave()));
		jsonObj.add("relatedImages", serializeImageList(entry.getRelatedImages()));
		return jsonObj;
	}
	
	private JsonObject serializeCave(CaveEntry ce) {
		JsonObject jsonObj = new JsonObject();
		if (ce != null) {
			jsonObj.addProperty("caveID", ce.getCaveID());
			jsonObj.addProperty("officialNumber", ce.getOfficialNumber());
			jsonObj.addProperty("historicalName", ce.getHistoricName());
			jsonObj.addProperty("optionalHistoricalName", ce.getOptionalHistoricName());
			jsonObj.addProperty("caveTypeID", ce.getCaveTypeID());
			jsonObj.addProperty("siteID", ce.getSiteID());
			jsonObj.addProperty("districtID", ce.getDistrictID());
			jsonObj.addProperty("regionID", ce.getRegionID());
			jsonObj.addProperty("optionalCaveSketch", ce.getOptionalCaveSketch());
		}
		return jsonObj;
	}
	
	private JsonArray serializeImageList(ArrayList<ImageEntry> list) {
		JsonArray imageArray = new JsonArray();
		for (ImageEntry ie : list) {
			imageArray.add(serializeImage(ie));	
		}
		return imageArray;
	}

	/**
	 * @param ie
	 * @return
	 */
	private JsonObject serializeImage(ImageEntry ie) {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("imageID", ie.getImageID());
		jsonObj.addProperty("shortName", ie.getShortName());
		jsonObj.addProperty("filename", ie.getFilename());
		return jsonObj;
	}

}