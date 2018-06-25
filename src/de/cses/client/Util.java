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

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import de.cses.client.user.UserLogin;
import de.cses.shared.ImageEntry;

/**
 * @author alingnau
 *
 */
public class Util {

	public static final String REGEX_EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	public static final String REGEX_URL_PATTERN = "^(((https?|ftps?)://)(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)([).!';/?:,][[:blank:]])?$";

	private static final DatabaseServiceAsync dbService = GWT.create(DatabaseService.class);
	protected static SafeUri imageUri;

	/**
	 * This class only contains static methods for utility purpose, so we never want to create an instance
	 */
	private Util() {
	}

	/**
	 * Returns a SafeUri for either the full image or the thumbnail related to
	 * 
	 * @param entry
	 * @return The main (master) image representing the depiction as a preview
	 */
	@Deprecated
	public static SafeUri getMasterImageUri(int depictionID, final int thumbnail) {
		dbService.getMasterImageEntryForDepiction(depictionID, new AsyncCallback<ImageEntry>() {

			@Override
			public void onFailure(Throwable caught) {
				imageUri = null;
			}

			@Override
			public void onSuccess(ImageEntry result) {
				imageUri = UriUtils.fromString("resource?imageID=" + result.getImageID() + (thumbnail > 0 ? "&thumb=" + thumbnail : "")
						+ UserLogin.getInstance().getUsernameSessionIDParameterForUri());
			}
		});
		return imageUri;
	}

	public static void showWarning(String header, String message) {
		final PopupPanel dialog = new PopupPanel();
		FramedPanel dialogPanel = new FramedPanel();
		dialog.getElement().getStyle().setBorderColor("#FF0000");
		dialogPanel.setHeading(header);
		dialogPanel.add(new Label(message));
		TextButton okButton = new TextButton("OK");
		okButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				dialog.hide();
			}
		});
		dialogPanel.addButton(okButton);
		dialog.add(dialogPanel);
		dialog.setModal(true);
		dialog.setGlassEnabled(true);
		dialog.center();
	}

	public static void showYesNo(String header, String message, SelectHandler yesHandler, SelectHandler noHandler) {
		final PopupPanel dialog = new PopupPanel();
		FramedPanel dialogPanel = new FramedPanel();
		dialog.getElement().getStyle().setBorderColor("#FF0000");
		dialogPanel.setHeading(header);
		dialogPanel.add(new Label(message));
		TextButton yesButton = new TextButton("Yes");
		yesButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				dialog.hide();
				yesHandler.onSelect(event);
			}
		});
		dialogPanel.addButton(yesButton);
		TextButton noButton = new TextButton("No");
		noButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				dialog.hide();
				noHandler.onSelect(event);
			}
		});
		dialogPanel.addButton(noButton);
		
		dialog.add(dialogPanel);
		dialog.setModal(true);
		dialog.setGlassEnabled(true);
		dialog.center();
	}

	public static void doLogging(String message) {
		String usertag = Cookies.getCookie(UserLogin.USERNAME);
		dbService.doLogging(usertag!=null ? usertag : "unknown", message, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) { }

			@Override
			public void onSuccess(Object result) { }
		});
	}

}
