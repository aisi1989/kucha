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
package de.cses.server.htmlfactory;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import de.cses.client.StaticTables;
import de.cses.client.user.UserLogin;
import de.cses.server.mysql.MysqlConnector;
import de.cses.shared.CaveEntry;
import de.cses.shared.DepictionEntry;
import de.cses.shared.PreservationAttributeEntry;
import de.cses.shared.UserEntry;

/**
 * @author alingnau
 *
 */
public class DepictionDisplay {
	
	private String html;
	private DepictionEntry entry;
	private MysqlConnector connector = MysqlConnector.getInstance();

	/**
	 * 
	 */
	public DepictionDisplay(int depictionID, int userAccessLevel) {
		entry = connector.getDepictionEntry(depictionID);
		html = "<div class='data-view'>";

		String imageUri = "resource?imageID=" + entry.getMasterImageID() + "&thumb=700" + UserLogin.getInstance().getUsernameSessionIDParameterForUri();
		String fullImageUri = "resource?imageID=" + entry.getMasterImageID() + UserLogin.getInstance().getUsernameSessionIDParameterForUri();
		html += "<figure style='text-align: center; margin: 0;'>";
		html += "<a href='" + fullImageUri + "' target='_blank'> <img src='" + imageUri + "' style='position: relative; width: 100%; height: auto;'></a>";
		html += "<figcaption style='font-family: verdana; font-size: 12px;'> " + entry.getShortName();
		if (entry.getWidth() > 0 || entry.getHeight() > 0) {
			html += " (width: " + entry.getWidth() + " cm, height: " + entry.getHeight() + " cm";
		}
		html += "</figcaption></figure>";
		
		html += "<h4 class='data-display'>Summary</h4><table class='data-view'>";
		if (!entry.getInventoryNumber().isEmpty()) {
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Inventory No.</i></td><td class='data-view-right'>" + entry.getInventoryNumber() + "</td></tr>";
		}
		if (entry.getCave() != null) {
			CaveEntry ce = entry.getCave();
			String caveStr = "";
			if (ce.getSiteID() > 0) {
				caveStr += connector.getSite(ce.getSiteID()).getShortName() + ": ";
			}
			caveStr += ce.getOfficialNumber() + ((ce.getHistoricName() != null && ce.getHistoricName().length() > 0) ? " (" + ce.getHistoricName() + ")" : ""); 			
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Located in Cave</i></td><td class='data-view-right'>" + caveStr + "</td></tr>";
		}
		if (entry.getExpedition() != null) {
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Found by expedition</i></td><td class='data-view-right'>" + entry.getExpedition().getName() + "</td></tr>";
		}
		if (entry.getVendor() != null) {
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Vendor</i></td><td class='data-view-right'>" +entry.getVendor().getVendorName() + "</td></tr>";
		}
		if (entry.getPurchaseDate() != null) {
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Purchase Date</i></td><td class='data-view-right'>" + entry.getPurchaseDate().toString() + "</td></tr>";
		}
		if (entry.getLocation() != null) {
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Current location</i></td><td class='data-view-right'>" + entry.getLocation().getName() + "</td></tr>";
		}
		if (!entry.getPreservationAttributesList().isEmpty()) {
			String preservationStr = "";
			for (PreservationAttributeEntry pae : entry.getPreservationAttributesList()) {
				preservationStr += preservationStr.length() > 0 ? ", " + pae.getName() : pae.getName();
			}
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>State of preservation</i></td><td class='data-view-right'>" + preservationStr + "</td></tr>";
		}
		if (entry.getStyleID() > 0) {
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Style</i></td><td class='data-view-right'>" + connector.getStylebyID(entry.getStyleID()).getStyleName() + "</td></tr>";
		}
		if (entry.getModeOfRepresentationID() > 0) {
			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Mode of representation</i></td><td class='data-view-right'>" + connector.getModesOfRepresentation(entry.getModeOfRepresentationID()) + "</td></tr>";
		}
		html += "</table>";
		
		
		html += "<p class='date'>Last changes on " + entry.getLastChangedOnDate() + " by " + entry.getLastChangedByUser() + ".</p></div>";
	}

	public String getHtml() {
		return html;
	}
	
	
	/**

	<tpl if="cave != &quot;&quot;">
	<figure style='text-align: center; margin: 10;'>
		<img src='{realCaveSketchUri}'
			style='position: relative; width: 100%; height: auto;'>
		<figcaption style='font-size: 14px;'>Cave sketch</figcaption>
	</figure>
	</tpl>

	<h4 class="data-display">Indexing</h4>

	<h5 class="data-display">Iconography</h5>
	<ul class="simple-list">
		<tpl for="iconography">
			<tpl if="iconographyID &lt; 2000"><li>{text}</li></tpl>
		</tpl>
	</ul>

	<h5 class="data-display">Pictorial Elements</h5>
	<ul class="simple-list">
		<tpl for="iconography">
			<tpl if="iconographyID &gt; 2000"><li>{text}</li></tpl>
		</tpl>
	</ul>

	<h4 class="data-display">Description</h4>
	<p class="data-display">{description}</p>

	<h4 class="data-display">General Remarks</h4>
	<p class="data-display">{generalRemarks}</p>

	<h4 class="data-display">Other Suggested Identifications</h4>
	<p class="data-display">{otherSuggestedIdentifications}</p>

	<h4 class="data-display">Related Bibliography</h4>
	<ol class="simple-list">
		<tpl for='bib'>
		<li>{authors} <tpl if="yearORG != &quot;&quot;">({yearORG})</tpl>.
			<i>{titleORG}</i> <tpl if="titleEN != &quot;&quot;">({titleEN})</tpl>.
			<tpl if="parentTitleORG != &quot;&quot;"> In <tpl
				if="editors != &quot;&quot;"> {editors} (eds.)</tpl> <i>{parentTitleORG}</i>
			<tpl if="pagesORG != &quot;&quot;"> (pp. {pagesORG})</tpl>. </tpl> <tpl
				if="publisher != &quot;&quot;"> {publisher}. </tpl>
		</li>
		</tpl>
	</ol>
	
	<p class="date">Last changes on {timestamp} by {user}.</p></div>


	 * 
	 */

}
