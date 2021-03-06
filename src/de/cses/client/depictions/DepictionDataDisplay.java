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
package de.cses.client.depictions;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.MarginData;

import de.cses.client.StaticTables;
import de.cses.client.ui.AbstractDataDisplay;
import de.cses.client.user.UserLogin;
import de.cses.shared.DepictionEntry;
import de.cses.shared.PreservationAttributeEntry;

/**
 * @author alingnau
 *
 */
public class DepictionDataDisplay extends AbstractDataDisplay {
	
	private DepictionEntry entry;
	
	/**
	 * 
	 */
	public DepictionDataDisplay(DepictionEntry e) {
		super();
		entry = e;
		String cave = "";
		String wall = "";
		SafeUri realCaveSketchUri = null;
		DepictionViewTemplates view = GWT.create(DepictionViewTemplates.class);
		if (e.getCave() != null) {
			if (e.getCave().getSiteID() > 0) {
				cave += StaticTables.getInstance().getSiteEntries().get(e.getCave().getSiteID()).getShortName() + ": ";
			}
			cave += e.getCave().getOfficialNumber() + ((e.getCave().getHistoricName() != null && e.getCave().getHistoricName().length() > 0) ? " (" + e.getCave().getHistoricName() + ")" : ""); 
			realCaveSketchUri = UriUtils.fromString("/resource?cavesketch=" + e.getCave().getOptionalCaveSketch() + UserLogin.getInstance().getUsernameSessionIDParameterForUri());
			wall = e.getWallID() > 0 ? StaticTables.getInstance().getWallLocationEntries().get(e.getCave().getWall(e.getWallID()).getWallLocationID()).getLabel() : "";
		}
		String shortname = e.getShortName() != null ? e.getShortName() : "";
		String expedition = e.getExpedition() != null ? e.getExpedition().getName() : "";
		String vendor = e.getVendor() != null ? e.getVendor().getVendorName() : "";
		String location = e.getLocation() != null ? e.getLocation().getName() : "";
		String date = e.getPurchaseDate() != null ? e.getPurchaseDate().toString() : "";
		String stateOfPreservation = "";
		for (PreservationAttributeEntry pae : e.getPreservationAttributesList()) {
			stateOfPreservation += stateOfPreservation.length() > 0 ? ", " + pae.getName() : pae.getName();
		}
		SafeUri imageUri = UriUtils.fromString("resource?imageID=" + e.getMasterImageID() + "&thumb=700" + UserLogin.getInstance().getUsernameSessionIDParameterForUri());
		SafeUri fullImageUri = UriUtils.fromString("resource?imageID=" + e.getMasterImageID() + UserLogin.getInstance().getUsernameSessionIDParameterForUri());
		String style = e.getStyleID() > 0 ? StaticTables.getInstance().getStyleEntries().get(e.getStyleID()).getStyleName() : "";
		String modesOfRepresentation = e.getModeOfRepresentationID() > 0 ? StaticTables.getInstance().getModesOfRepresentationEntries().get(e.getModeOfRepresentationID()).getName() : "";
		HTML htmlWidget = new HTML(view.display(
				shortname, 
				e.getInventoryNumber() != null ? e.getInventoryNumber() : "",  
				cave,
				wall,
				expedition, 
				vendor, 
				date, 
				location, 
				stateOfPreservation, 
				imageUri,
				fullImageUri, 
				realCaveSketchUri, 
				e.getWidth(), e.getHeight(),
				style, 
				modesOfRepresentation, 
				e.getDescription() != null ? e.getDescription() : "",
				e.getGeneralRemarks() != null ? e.getGeneralRemarks() : "",
				e.getOtherSuggestedIdentifications() != null ? e.getOtherSuggestedIdentifications() : "",
				e.getRelatedIconographyList(),
				e.getRelatedBibliographyList(),
				e.getLastChangedByUser(),
				e.getLastChangedOnDate()
			));
		htmlWidget.addStyleName("html-data-display");
		add(htmlWidget, new MarginData(0, 0, 0, 0));
		setHeading((shortname.length() > 0 ? shortname + " " : "") + (cave.length() > 0 ? " in " + cave : ""));
	}

	/* (non-Javadoc)
	 * @see de.cses.client.ui.AbstractDataDisplay#getUniqueID()
	 */
	@Override
	public String getUniqueID() {
		return entry.getUniqueID();
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof DepictionDataDisplay) {
//			return ((DepictionDataDisplay) obj).getUniqueID() == this.getUniqueID();
//		} else {
//			return false;
//		}
//	}

}
