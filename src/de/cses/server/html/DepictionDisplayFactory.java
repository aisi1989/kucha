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
package de.cses.server.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.gwt.safehtml.shared.UriUtils;

import de.cses.client.StaticTables;
import de.cses.client.user.UserLogin;
import de.cses.server.mysql.MysqlConnector;
import de.cses.shared.AnnotatedBiblographyEntry;
import de.cses.shared.DepictionEntry;
import de.cses.shared.IconographyEntry;
import de.cses.shared.PreservationAttributeEntry;

/**
 * @author alingnau
 *
 */
public class DepictionDisplayFactory {
	
	private String html;
	private DepictionEntry entry;
	private MysqlConnector connector = MysqlConnector.getInstance();

	/**
	 * 
	 */
	public DepictionDisplayFactory(int depictionID, String sessionID) {
		entry = connector.getDepictionEntry(depictionID);
		String content;
//		String htmlFilename = System.getProperty("user.dir") + "/lib/html/DepictionDisplayFull.html";
		try {			
//			content = FileUtils.readFileToString(new File(htmlFilename), StandardCharsets.UTF_8);
			content = FileUtils.readFileToString(new File(getClass().getResource("./template/DepictionDisplayFull.html").getFile()), StandardCharsets.UTF_8);
			String imageUri = String.format("resource?imageID=%d&thumb=700&sessionID=%s", entry.getMasterImageID(), sessionID);
			String fullImageUri = "resource?imageID=" + entry.getMasterImageID() + "&sessionID=" + sessionID;
			String figureCaption = entry.getShortName() + (entry.getWidth() > 0 || entry.getHeight() > 0 ? " (width: " + entry.getWidth() + " cm, height: " + entry.getHeight() + " cm)" : "");
			String caveName = "";
			String realCaveSketchUri = "";
			if (entry.getCave() != null) {
				caveName = connector.getSite(entry.getCave().getSiteID()).getShortName() + " " + entry.getCave().getOfficialNumber();
				if (entry.getCave().getSiteID() > 0) {
					caveName += connector.getSite(entry.getCave().getSiteID()).getShortName() + ": ";
				}
				caveName += entry.getCave().getOfficialNumber() + ((entry.getCave().getHistoricName() != null && entry.getCave().getHistoricName().length() > 0) ? " (" + entry.getCave().getHistoricName() + ")" : ""); 
				realCaveSketchUri = "resource?cavesketch=" + entry.getCave().getOptionalCaveSketch() + "&sessionID=" + sessionID;
			}
			String stateOfPreservation = "";
			for (PreservationAttributeEntry pae : entry.getPreservationAttributesList()) {
				stateOfPreservation += stateOfPreservation.length() > 0 ? ", " + pae.getName() : pae.getName();
			}
			String style = entry.getStyleID() > 0 ? connector.getStylebyID(entry.getStyleID()).getStyleName() : "";
			String modeOfRepresentation = entry.getModeOfRepresentationID() > 0 ? connector.getModesOfRepresentation(entry.getModeOfRepresentationID()).getName() : "";
			String iconographyList = "";
			String pictorialElementsList = "";
			for (IconographyEntry ie : entry.getRelatedIconographyList()) {
				if (ie.getIconographyID() < 2000) {
					iconographyList += "<li>" + ie.getText() + "</li>";
				} else {
					pictorialElementsList += "<li>" + ie.getText() + "</li>";
				}
			}
			String bibList = "";
			for (AnnotatedBiblographyEntry be : entry.getRelatedBibliographyList()) {
				String listElement = "<li>" + be.getAuthors() + (!be.getYearORG().isEmpty() ? " ("+be.getYearORG()+"). " : ". ")
						+ "<i>" + be.getTitleORG() + "</i>" + (!be.getTitleEN().isEmpty() ? " ("+be.getTitleEN()+"). " : ". ");
				if (!be.getParentTitleORG().isEmpty()) {
					listElement += "In " + (!be.getEditors().isEmpty() ? be.getEditors() + " (" + be.getEditorType() + ") <i>" : "<i>") + be.getParentTitleEN() + "</i>"
							+ (!be.getPagesORG().isEmpty() ? " ("+be.getPagesORG()+"). " : ". ");
				}
				if (be.getPublisher().isEmpty()) {
					listElement += be.getPublisher() + ".";
				}
				listElement += "</li>";
				bibList += listElement;
			}
			
			html = String.format(content, fullImageUri, imageUri, figureCaption, 
					entry.getInventoryNumber()!=null ? entry.getInventoryNumber() : "", 
					caveName,
					entry.getExpedition() != null ? entry.getExpedition().getName() : "",
					entry.getVendor() != null ? entry.getVendor().getVendorName() : "",
					entry.getPurchaseDate() != null ? entry.getPurchaseDate().toString() : "",
					entry.getLocation() != null ? entry.getLocation().getName() : "",
					stateOfPreservation,
					style,
					modeOfRepresentation,
					realCaveSketchUri,
					iconographyList,
					pictorialElementsList,
					entry.getDescription() != null ? entry.getDescription() : "",
					entry.getGeneralRemarks() != null ? entry.getGeneralRemarks() : "",
					entry.getOtherSuggestedIdentifications() != null ? entry.getOtherSuggestedIdentifications() : "",
					bibList,
					entry.getLastChangedOnDate(), entry.getLastChangedByUser());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
//		html = "<link rel='stylesheet' href='https://kuchatest.saw-leipzig.de/infosystem/reset.css'><link rel='stylesheet' href='/KuchaApplication.css'>";
//		
//		html += "<div class='data-view'>";

//		String imageUri = "resource?imageID=" + entry.getMasterImageID() + "&thumb=700&sessionID=" + sessionID;
		
//		html += "<figure style='text-align: center; margin: 0;'>";
//		html += "<a href='" + fullImageUri + "' target='_blank'> <img src='" + imageUri + "' style='position: relative; width: 100%; height: auto;'></a>";
//		html += "<figcaption style='font-family: verdana; font-size: 12px;'> " + entry.getShortName();
//		if (entry.getWidth() > 0 || entry.getHeight() > 0) {
//			html += " (width: " + entry.getWidth() + " cm, height: " + entry.getHeight() + " cm";
//		}
//		html += "</figcaption></figure>";
//		
//		html += "<h4 class='data-display'>Summary</h4><table class='data-view'>";
//		if (entry.getInventoryNumber() != null && !entry.getInventoryNumber().isEmpty()) {
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Inventory No.</i></td><td class='data-view-right'>" + entry.getInventoryNumber() + "</td></tr>";
//		}
//		if (entry.getCave() != null) {
//			CaveEntry ce = entry.getCave();
//			String caveStr = "";
//			if (ce.getSiteID() > 0) {
//				caveStr += connector.getSite(ce.getSiteID()).getShortName() + ": ";
//			}
//			caveStr += ce.getOfficialNumber() + ((ce.getHistoricName() != null && ce.getHistoricName().length() > 0) ? " (" + ce.getHistoricName() + ")" : ""); 			
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Located in Cave</i></td><td class='data-view-right'>" + caveStr + "</td></tr>";
//		}
//		if (entry.getExpedition() != null) {
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Found by expedition</i></td><td class='data-view-right'>" + entry.getExpedition().getName() + "</td></tr>";
//		}
//		if (entry.getVendor() != null) {
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Vendor</i></td><td class='data-view-right'>" +entry.getVendor().getVendorName() + "</td></tr>";
//		}
//		if (entry.getPurchaseDate() != null) {
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Purchase Date</i></td><td class='data-view-right'>" + entry.getPurchaseDate().toString() + "</td></tr>";
//		}
//		if (entry.getLocation() != null) {
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Current location</i></td><td class='data-view-right'>" + entry.getLocation().getName() + "</td></tr>";
//		}
//		if (!entry.getPreservationAttributesList().isEmpty()) {
//			String preservationStr = "";
//			for (PreservationAttributeEntry pae : entry.getPreservationAttributesList()) {
//				preservationStr += preservationStr.length() > 0 ? ", " + pae.getName() : pae.getName();
//			}
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>State of preservation</i></td><td class='data-view-right'>" + preservationStr + "</td></tr>";
//		}
//		if (entry.getStyleID() > 0) {
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Style</i></td><td class='data-view-right'>" + connector.getStylebyID(entry.getStyleID()).getStyleName() + "</td></tr>";
//		}
//		if (entry.getModeOfRepresentationID() > 0) {
//			html += "<tr style='border-bottom: 1px solid black'><td class='data-view-left'><i>Mode of representation</i></td><td class='data-view-right'>" + connector.getModesOfRepresentation(entry.getModeOfRepresentationID()).getName() + "</td></tr>";
//		}
//		html += "</table>";
//		
//		
//		html += "<p class='date'>Last changes on " + entry.getLastChangedOnDate() + " by " + entry.getLastChangedByUser() + ".</p></div>";
	}

	public String getHtml() {
		return html;
	}
	
	private String readFromJARFile(String filename) throws IOException {
			  InputStream is = getClass().getResourceAsStream(filename);
			  InputStreamReader isr = new InputStreamReader(is);
			  BufferedReader br = new BufferedReader(isr);
			  StringBuffer sb = new StringBuffer();
			  String line;
			  while ((line = br.readLine()) != null) 
			  {
			    sb.append(line);
			  }
			  br.close();
			  isr.close();
			  is.close();
			  return sb.toString();
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
