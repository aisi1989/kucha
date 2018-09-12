/*
 * Copyright 2017 
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.XTemplate;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DragSource;

import de.cses.client.StaticTables;
import de.cses.client.ui.AbstractEditor;
import de.cses.client.ui.AbstractView;
import de.cses.client.user.UserLogin;
import de.cses.shared.AbstractEntry;
import de.cses.shared.CaveEntry;
import de.cses.shared.DepictionEntry;

/**
 * @author alingnau
 *
 */
public class DepictionView extends AbstractView {

	interface Resources extends ClientBundle {
		@Source("buddha.png")
		ImageResource logo();
	}

	interface DepictionViewTemplates extends XTemplates {
		@XTemplate("<div><center><img src='{imgUri}'></img></center></div>")
		SafeHtml view(SafeUri imgUri);
		
//		@XTemplate("<div><center><img src='{imgUri}'></img></center><label style='font-size:9px' > DepictionID {id} </label></br></div>")
//		SafeHtml view(SafeUri imgUri, int id);

		@XTemplate("<div><center><img src='{imgUri}'></img></center><label style='font-size:9px' > {label} </label></br></div>")
		SafeHtml view(SafeUri imgUri, String label);

		@XTemplate("<div><center><img src='{imgUri}'></img></center><label style='font-size:9px'>{caveLabel}<br>{depictionLabel}</label></br></div>")
		SafeHtml view(SafeUri imgUri, String caveLabel, String depictionLabel);

//		@XTemplate("<div><center><img src='{imgUri}'></img></center><label style='font-size:9px'>{caveLabel}<br>{caveName}<br>{depictionLabel}</label></br></div>")
//		SafeHtml view(SafeUri imgUri, String caveLabel, String caveName, String depictionLabel);
	}

	interface DepictionDisplayTemplates extends XTemplates {
		@XTemplate("<iframe style='width: 100%; height:100%' src='{uri}'><p>Your browser does not support iframes.</p></iframe>")
		SafeHtml depiction(SafeUri uri);
	}

	private DepictionEntry depictionEntry;
	private DepictionViewTemplates dvTemplates;
	private DepictionDisplayTemplates ddTemplates;
	private Resources resources;

	/**
	 * @param text
	 */
	public DepictionView(DepictionEntry entry) {
		depictionEntry = entry;
		resources = GWT.create(Resources.class);
		dvTemplates = GWT.create(DepictionViewTemplates.class);
		ddTemplates = GWT.create(DepictionDisplayTemplates.class);
		setHTML(ddTemplates.depiction(UriUtils.fromString("/resource?depictionID=" + depictionEntry.getDepictionID() + "&preview=true" + UserLogin.getInstance().getUsernameSessionIDParameterForUri())));
//		CaveEntry ce = entry.getCave();
//		if (ce != null) {
//			setHTML(dvTemplates.view(UriUtils.fromString("resource?imageID=" + entry.getMasterImageID() + "&thumb=80" + UserLogin.getInstance().getUsernameSessionIDParameterForUri()), 
//					StaticTables.getInstance().getSiteEntries().get(ce.getSiteID()).getShortName() + " " + ce.getOfficialNumber(), 
//					ce.getHistoricName() != null ? ce.getHistoricName() : (depictionEntry.getShortName() != null ? depictionEntry.getShortName() : "")));
//		} else {
//			setHTML(dvTemplates.view(UriUtils.fromString("resource?imageID=" + entry.getMasterImageID() + "&thumb=80" + UserLogin.getInstance().getUsernameSessionIDParameterForUri()), 
//					depictionEntry.getShortName() != null ? depictionEntry.getShortName() : ""));
//		}
		setPixelSize(150, 150);

		DragSource source = new DragSource(this) {

			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);
				event.setData(depictionEntry);
				event.getStatusProxy().update(dvTemplates.view(resources.logo().getSafeUri()));
			}
			
		};
	}

	/* (non-Javadoc)
	 * @see de.cses.client.ui.AbstractView#getEditor()
	 */
	@Override
	protected AbstractEditor getEditor() {
		return new DepictionEditor(depictionEntry);
	}

	/* (non-Javadoc)
	 * @see de.cses.client.ui.AbstractView#getEntry()
	 */
	@Override
	protected AbstractEntry getEntry() {
		return depictionEntry;
	}

	@Override
	public void closeRequest(AbstractEntry entry) {
		super.closeRequest(entry);
		if (entry != null && entry instanceof DepictionEntry) {
			depictionEntry = (DepictionEntry) entry;
		}
//		setHTML(dvTemplates.view(UriUtils.fromString("resource?imageID=" + depictionEntry.getMasterImageID() + "&thumb=80" + UserLogin.getInstance().getUsernameSessionIDParameterForUri()), depictionEntry.getDepictionID()));

		CaveEntry ce = depictionEntry.getCave();
		setHTML(dvTemplates.view(UriUtils.fromString("resource?imageID=" + depictionEntry.getMasterImageID() + "&thumb=80" + UserLogin.getInstance().getUsernameSessionIDParameterForUri()), 
				StaticTables.getInstance().getSiteEntries().get(ce.getSiteID()).getShortName() + " " + ce.getOfficialNumber(), 
				ce.getHistoricName() != null ? ce.getHistoricName() : (depictionEntry.getShortName() != null ? depictionEntry.getShortName() : "")));
	}

//	/* (non-Javadoc)
//	 * @see de.cses.client.ui.EditorListener#updateEntryRequest(de.cses.shared.AbstractEntry)
//	 */
//	@Override
//	public void updateEntryRequest(AbstractEntry updatedEntry) { }

	/* (non-Javadoc)
	 * @see de.cses.client.ui.AbstractView#getPermalink()
	 */
	@Override
	protected String getPermalink() {
		return "/json?paintedRepID=" + depictionEntry.getDepictionID();
	}

}
