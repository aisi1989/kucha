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
package de.cses.shared;

import java.sql.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DepictionEntry implements IsSerializable {

/**
 * SELECT `DepictionID`, `Style`, `Inscriptions`, `Dating`, `Dimension.height`, `Dimension.width`, `PurchaseDate`, `VendorID`, 
 * `ExpeditionID`, `DateOfAcquisition`, `CurrentLocationID`, `Description`, `BackgroundColour`, `Material`, 
 * `GeneralRemarks`, `OtherSuggestedIdentifications`, `StoryID`, `CaveID` FROM `Depictions` WHERE 1	
 */
	
	private int depictionID;
	private int styleID;
	private String inscriptions;
	private String dating;
	private String description;
	private String backgroundColour;
	private String material;
	private String generalRemarks;
	private String otherSuggestedIdentifications;
	private double width, height;
	private Date dateOfAcquisition;
	private int expeditionID;
	private Date purchaseDate;
	private int currentLocationID;
	private int vendorID;
	private int storyID;
	private int caveID;
	private int absoluteLeft;
	private int absoluteTop;
	
	public DepictionEntry() {
		depictionID = 0;
	}

	public DepictionEntry(int depictionID, int styleID, String inscriptions, String dating, String description,
			String backgroundColour, String material, String generalRemarks, String otherSuggestedIdentifications, int width,
			int height, Date dateOfAcquisition, int expeditionID, Date purchaseDate, int currentLocationID, int vendorID,
			int storyID, int caveID) {
		super();
		this.depictionID = depictionID;
		this.styleID = styleID;
		this.inscriptions = inscriptions;
		this.dating = dating;
		this.description = description;
		this.backgroundColour = backgroundColour;
		this.material = material;
		this.generalRemarks = generalRemarks;
		this.otherSuggestedIdentifications = otherSuggestedIdentifications;
		this.width = width;
		this.height = height;
		this.dateOfAcquisition = dateOfAcquisition;
		this.expeditionID = expeditionID;
		this.purchaseDate = purchaseDate;
		this.currentLocationID = currentLocationID;
		this.vendorID = vendorID;
		this.storyID = storyID;
		this.caveID = caveID;
	}

	public int getDepictionID() {
		return depictionID;
	}

	public void setDepictionID(int depictionID) {
		this.depictionID = depictionID;
	}

	public int getStyleID() {
		return styleID;
	}

	public void setStyleID(int styleID) {
		this.styleID = styleID;
	}

	public String getInscriptions() {
		return inscriptions;
	}

	public void setInscriptions(String inscriptions) {
		this.inscriptions = inscriptions;
	}

	public String getDating() {
		return dating;
	}

	public void setDating(String dating) {
		this.dating = dating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBackgroundColour() {
		return backgroundColour;
	}

	public void setBackgroundColour(String backgroundColour) {
		this.backgroundColour = backgroundColour;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getGeneralRemarks() {
		return generalRemarks;
	}

	public void setGeneralRemarks(String generalRemarks) {
		this.generalRemarks = generalRemarks;
	}

	public String getOtherSuggestedIdentifications() {
		return otherSuggestedIdentifications;
	}

	public void setOtherSuggestedIdentifications(String otherSuggestedIdentifications) {
		this.otherSuggestedIdentifications = otherSuggestedIdentifications;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Date getDateOfAcquisition() {
		return dateOfAcquisition;
	}

	public void setDateOfAcquisition(Date dateOfAcquisition) {
		this.dateOfAcquisition = dateOfAcquisition;
	}

	public int getExpeditionID() {
		return expeditionID;
	}

	public void setExpeditionID(int expeditionID) {
		this.expeditionID = expeditionID;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public int getCurrentLocationID() {
		return currentLocationID;
	}

	public void setCurrentLocationID(int currentLocationID) {
		this.currentLocationID = currentLocationID;
	}

	public int getVendorID() {
		return vendorID;
	}

	public void setVendorID(int vendorID) {
		this.vendorID = vendorID;
	}

	public int getStoryID() {
		return storyID;
	}

	public void setStoryID(int storyID) {
		this.storyID = storyID;
	}

	public int getCaveID() {
		return caveID;
	}

	public void setCaveID(int caveID) {
		this.caveID = caveID;
	}
	
	/**
	 * This method is needed by for the DepictionViewTemplate 
	 * @see de.cses.client.depictions.DepictionEditor
	 * @return String containing caveID and depictionID
	 */
	public String getName() {
		return "Cave: "+caveID+" Depiction: "+depictionID;
	}

	public int getAbsoluteLeft() {
		return absoluteLeft;
	}

	public void setAbsoluteLeft(int absoluteLeft) {
		this.absoluteLeft = absoluteLeft;
	}

	public int getAbsoluteTop() {
		return absoluteTop;
	}

	public void setAbsoluteTop(int absoluteTop) {
		this.absoluteTop = absoluteTop;
	}
	

}
