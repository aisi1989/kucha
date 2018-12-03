/*
 * Copyright 2017 - 2018
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
package de.cses.client.images;

import java.util.ArrayList;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

import de.cses.client.StaticTables;
import de.cses.client.ui.AbstractFilter;
import de.cses.shared.AbstractSearchEntry;
import de.cses.shared.ImageTypeEntry;

/**
 * @author alingnau
 *
 */
public class ImageFilter extends AbstractFilter {

	private TextField shortnameSearch;
	private TextField titleSearch;
	private TextField copyrightSearch;
	private Radio andSearch;
	private Radio orSearch;
	private ImageTypeProperties imageTypeProps;
	private ListStore<ImageTypeEntry> imageTypeEntryList, selectedImagesTypesList;

	interface ImageTypeProperties extends PropertyAccess<ImageTypeEntry> {
		ModelKeyProvider<ImageTypeEntry> imageTypeID();

		LabelProvider<ImageTypeEntry> uniqueID();

		ValueProvider<ImageTypeEntry, String> name();
	}

	interface ImageTypeViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml imageTypeLabel(String name);
	}

	/**
	 * @param filterName
	 */
	public ImageFilter(String filterName) {
		super(filterName);
		imageTypeProps = GWT.create(ImageTypeProperties.class);
		imageTypeEntryList = new ListStore<ImageTypeEntry>(imageTypeProps.imageTypeID());
		selectedImagesTypesList = new ListStore<ImageTypeEntry>(imageTypeProps.imageTypeID());
		for (ImageTypeEntry ite : StaticTables.getInstance().getImageTypeEntries().values()) {
			imageTypeEntryList.add(ite);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.cses.client.ui.AbstractFilter#getFilterUI()
	 */
	@Override
	protected Widget getFilterUI() {
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();

		// TODO 
		titleSearch = new TextField();
		titleSearch.addValidator(new RegExValidator("^[a-zA-Z0-9 _\\-]*$", "We are working on a new search interface. Currently only a-z, A-Z, 0-9, _, - and [SPACE] are allowed."));
		titleSearch.setAutoValidate(true);
		titleSearch.setValue("");
		titleSearch.setEmptyText("search image title");
		vlc.add(titleSearch, new VerticalLayoutData(1.0, .125));

		shortnameSearch = new TextField();
		shortnameSearch.addValidator(new RegExValidator("^[a-zA-Z0-9 _\\-]*$", "We are working on a new search interface. Currently only a-z, A-Z, 0-9, _, - and [SPACE] are allowed."));
		shortnameSearch.setAutoValidate(true);
		shortnameSearch.setValue("");
		shortnameSearch.setEmptyText("search image short name");
		vlc.add(shortnameSearch, new VerticalLayoutData(1.0, .125));

		copyrightSearch = new TextField();
		copyrightSearch.addValidator(new RegExValidator("^[a-zA-Z0-9 _\\-]*$", "We are working on a new search interface. Currently only a-z, A-Z, 0-9, _, - and [SPACE] are allowed."));
		copyrightSearch.setAutoValidate(true);
		copyrightSearch.setValue("");
		copyrightSearch.setEmptyText("search image copyright");
		vlc.add(copyrightSearch, new VerticalLayoutData(1.0, .125));

		HorizontalPanel searchTypeHP = new HorizontalPanel();
		andSearch = new Radio();
		andSearch.setBoxLabel("AND");
		orSearch = new Radio();
		orSearch.setBoxLabel("OR");
		ToggleGroup tg = new ToggleGroup();
		tg.add(andSearch);
		tg.add(orSearch);
		andSearch.setValue(true);
		searchTypeHP.add(andSearch);
		searchTypeHP.add(orSearch);
		vlc.add(searchTypeHP, new VerticalLayoutData(1.0, .125));

		DualListField<ImageTypeEntry, String> dualListField = new DualListField<ImageTypeEntry, String>(imageTypeEntryList,
				selectedImagesTypesList, imageTypeProps.name(), new TextCell());
		dualListField.setEnableDnd(true);
		dualListField.getDownButton().removeFromParent();
		dualListField.getUpButton().removeFromParent();
		dualListField.setMode(DualListField.Mode.INSERT);

		vlc.add(dualListField, new VerticalLayoutData(1.0, .50));

		vlc.setHeight("300px");
		return vlc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.cses.client.ui.AbstractFilter#getSqlWhereClause()
	 */
	public ArrayList<String> getSqlWhereClause() {
		ArrayList<String> result = new ArrayList<String>();
		String textFieldQuery = "";
		
		if (!titleSearch.getValue().isEmpty() && titleSearch.validate()) {
			textFieldQuery = "Title LIKE '%" + titleSearch.getValue().replace("_", "\\_") + "%'";
		}
		if (!shortnameSearch.getValue().isEmpty() && shortnameSearch.validate()) {
			textFieldQuery = textFieldQuery.concat((!textFieldQuery.isEmpty() ? (andSearch.getValue() ? " AND " : " OR ") : "")
					+ "ShortName LIKE '%" + shortnameSearch.getValue().replace("_", "\\_") + "%'");
		}
		if (!copyrightSearch.getValue().isEmpty() && copyrightSearch.validate()) {
			textFieldQuery = textFieldQuery.concat((!textFieldQuery.isEmpty() ? (andSearch.getValue() ? " AND " : " OR ") : "")
					+ "Copyright LIKE '%" + copyrightSearch.getValue().replace("_", "\\_") + "%'");
		}
		if (!textFieldQuery.isEmpty()) {
			result.add("(" + textFieldQuery + ")");
		}

		String imageTypeQuery = "";
		for (ImageTypeEntry ite : selectedImagesTypesList.getAll()) {
			if (imageTypeQuery.isEmpty()) {
				imageTypeQuery = "" + ite.getImageTypeID();
			} else {
				imageTypeQuery = imageTypeQuery.concat(", " + ite.getImageTypeID());
			}
		}
		if (!imageTypeQuery.isEmpty()) {
			result.add("(ImageTypeID IN (" + imageTypeQuery + "))");
		}
		return result;
	}

	@Override
	public AbstractSearchEntry getSearchEntry() {
		// TODO Auto-generated method stub
		return null;
	}

}
