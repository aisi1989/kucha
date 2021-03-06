/*
 * Copyright 2016-2018
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.DateTimePropertyEditor;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.ListField;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.sencha.gxt.widget.core.client.form.validator.MinLengthValidator;
import com.sencha.gxt.widget.core.client.form.validator.MinNumberValidator;

import de.cses.client.DatabaseService;
import de.cses.client.DatabaseServiceAsync;
import de.cses.client.StaticTables;
import de.cses.client.Util;
import de.cses.client.bibliography.BibliographySelector;
import de.cses.client.images.ImageSelector;
import de.cses.client.images.ImageSelectorListener;
import de.cses.client.ui.AbstractEditor;
import de.cses.client.user.UserLogin;
import de.cses.client.walls.WallSelector;
import de.cses.client.walls.Walls;
import de.cses.shared.CaveEntry;
import de.cses.shared.DepictionEntry;
import de.cses.shared.DistrictEntry;
import de.cses.shared.ExpeditionEntry;
import de.cses.shared.IconographyEntry;
import de.cses.shared.ImageEntry;
import de.cses.shared.LocationEntry;
import de.cses.shared.ModeOfRepresentationEntry;
import de.cses.shared.PreservationAttributeEntry;
import de.cses.shared.SiteEntry;
import de.cses.shared.StyleEntry;
import de.cses.shared.VendorEntry;
import de.cses.shared.WallEntry;

public class DepictionEditor extends AbstractEditor {

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final DatabaseServiceAsync dbService = GWT.create(DatabaseService.class);
	private TextArea inscriptionsTestArea;
	private TextField datingField;
	private NumberField<Double> widthNF;
	private NumberField<Double> heightNF;
	private DateField purchaseDateField;
	private DateField dateOfAcquisitionField;
	private TextArea descriptionArea;
	private TextField backgroundColourField;
	private TextArea generalRemarksArea;
	protected IconographySelector iconographySelector;
	protected ImageSelector imageSelector;
	private FramedPanel mainPanel;
	protected PopupPanel imageSelectionDialog;
	private ListView<ImageEntry, ImageEntry> imageListView;
	private ListStore<ImageEntry> imageEntryLS;
	private ImageProperties imgProperties;
	private DepictionEntry correspondingDepictionEntry;
	private VendorProperties vendorProps;
	private ListStore<VendorEntry> vendorEntryLS;
	private ComboBox<VendorEntry> vendorSelection;
	private ComboBox<StyleEntry> styleSelection;
	private StyleProperties styleProps;
	private ListStore<StyleEntry> styleEntryLS;
	private CaveProperties caveProps;
	private ListStore<CaveEntry> caveEntryLS;
	private ComboBox<CaveEntry> caveSelectionCB;
	private ExpeditionProperties expedProps;
	private ListStore<ExpeditionEntry> expedEntryLS;
	private ComboBox<ExpeditionEntry> expedSelectionCB;
	private ComboBox<ModeOfRepresentationEntry> modeOfRepresentationSelectionCB;
	protected PopupPanel wallEditorDialog;
	private Walls wallEditor;
	protected PopupPanel iconographySelectionDialog;
	private WallSelector wallSelectorPanel;
	private TextArea separateAksarasTextArea;
	private ModesOfRepresentationProperties morProps;
	private ListStore<ModeOfRepresentationEntry> modeOfRepresentationLS;
	private ComboBox<LocationEntry> locationSelectionCB;
	private LocationProperties locationProps;
	private ListStore<LocationEntry> locationEntryLS;
	private PreservationAttributeProperties presAttributeProps;
	private ListStore<PreservationAttributeEntry> preservationAttributesLS, selectedPreservationAttributesLS;
	private TextField shortNameTF;
	private BibliographySelector bibliographySelector;

	interface DepictionProperties extends PropertyAccess<DepictionEntry> {
		ModelKeyProvider<DepictionEntry> depictionID();
		LabelProvider<DepictionEntry> name();
	}

	interface DepictionViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml depiction(String name);
	}

	interface CaveProperties extends PropertyAccess<CaveEntry> {
		ModelKeyProvider<CaveEntry> caveID();
		LabelProvider<CaveEntry> officialNumber();
	}

	interface CaveViewTemplates extends XTemplates {
		@XTemplate("<div>{officialNumber}: {officialName}<br>{siteDistrictInformation}</div>")
		SafeHtml caveLabel(String siteDistrictInformation, String officialNumber, String officialName);

		@XTemplate("<div>{officialNumber}<br>{siteDistrictInformation}</div>")
		SafeHtml caveLabel(String siteDistrictInformation, String officialNumber);
	}

	interface LocationProperties extends PropertyAccess<LocationEntry> {
		ModelKeyProvider<LocationEntry> locationID();
		LabelProvider<LocationEntry> name();
	}

	interface LocationViewTemplates extends XTemplates {
		@XTemplate("<div>{name}<br>{town}, {country}</div>")
		SafeHtml caveLabel(String name, String town, String country);

		@XTemplate("<div>{name}<br>{country}</div>")
		SafeHtml caveLabel(String name, String country);

		@XTemplate("<div>{name}</div>")
		SafeHtml caveLabel(String name);
	}

	interface ExpeditionProperties extends PropertyAccess<ExpeditionEntry> {
		ModelKeyProvider<ExpeditionEntry> expeditionID();
		LabelProvider<ExpeditionEntry> name();
	}

	interface ExpeditionViewTemplates extends XTemplates {
		@XTemplate("<div>{expedName}<br>Leader: {leaderName}<br>{startYear} - {endYear}</div>")
		SafeHtml expedLabel(String expedName, String leaderName, String startYear, String endYear);
	}
	
	interface ModesOfRepresentationProperties extends PropertyAccess<ModeOfRepresentationEntry> {
		ModelKeyProvider<ModeOfRepresentationEntry> modeOfRepresentationID();
		LabelProvider<ModeOfRepresentationEntry> name();
	}
	
	interface ModesOfRepresentationViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml morLabel(String name);
	}

	interface VendorProperties extends PropertyAccess<VendorEntry> {
		ModelKeyProvider<VendorEntry> vendorID();

		LabelProvider<VendorEntry> vendorName();
	}

	interface VendorViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml vendorName(String name);
	}

	interface StyleProperties extends PropertyAccess<StyleEntry> {
		ModelKeyProvider<StyleEntry> styleID();

		LabelProvider<StyleEntry> styleName();
	}

	interface StyleViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml styleName(String name);
	}
	
	interface PreservationAttributeProperties extends PropertyAccess<PreservationAttributeEntry> {
		ModelKeyProvider<PreservationAttributeEntry> preservationAttributeID();
		LabelProvider<PreservationAttributeEntry> uniqueID();
		ValueProvider<PreservationAttributeEntry, String> name();
	}

	interface ImageProperties extends PropertyAccess<ImageEntry> {
		ModelKeyProvider<ImageEntry> imageID();
		LabelProvider<ImageEntry> title();
		ValueProvider<ImageEntry, String> shortName();
	}
	
	class TitleElement {
		private String element;
		
		public TitleElement(String element) {
			super();
			this.element = element;
		}

		public String getElement() {
			return element;
		}
	}

	/**
	 * creates the view how a thumbnail of an image entry will be shown currently we are relying on the url of the image until we have user management implemented
	 * and protect images from being viewed from the outside without permission
	 * 
	 * @author alingnau
	 *
	 */
//	interface ImageViewTemplates extends XTemplates {
////		@XTemplate("<div style='border-style: solid; border-color: #99ff66; border-width: 3px;'><center><img src='{imageUri}' style='width: 230px; height: auto; align-content: center; margin: 5px;'></center><label style='font-size:12px'>{shortName}</label></br><label style='font-size:8px'>{title}</label></div>")
//		@XTemplate("<figure style='border-style: solid; border-color: #99ff66; border-width: 3px; margin: 0;'>"
//				+ "<img src='{imageUri}' style='position: relative; padding: 5px; width: 230px; background: white;'>"
//				+ "<figcaption style='font-size:12px; padding: 10px; text-align: center;'>{shortName} ({imageFormat})<p style='font-size:10px;'> <tpl for='titleList'> {element}<wbr> </tpl> </p></figcaption></figure>")
//		SafeHtml openAccessImage(SafeUri imageUri, String shortName, ArrayList<TitleElement> titleList, String imageFormat);
//
////		@XTemplate("<div style='border-style: solid; border-color: #ff1a1a; border-width: 3px;'><center><img src='{imageUri}' style='width: 230px; height: auto; align-content: center; margin: 5px;'></center><label style='font-size:12px'>{shortName}</label></br><label style='font-size:8px'>{title}</label></div>")
//		@XTemplate("<figure style='border-style: solid; border-color: #ff1a1a; border-width: 3px; margin: 0;'>"
//				+ "<img src='{imageUri}' style='position: relative; padding: 5px; width: 230px; background: white;'>"
//				+ "<figcaption style='font-size:12px; padding: 10px; text-align: center;'>{shortName} ({imageFormat})<p style='font-size:10px;'> <tpl for='titleList'> {element}<wbr> </tpl> </p></figcaption></figure>")
//		SafeHtml nonOpenAccessImage(SafeUri imageUri, String shortName, ArrayList<TitleElement> titleList, String imageFormat);
//
////		@XTemplate("<div style='border-style: solid; border-color: #0073e6; border-width: 3px;'><center><img src='{imageUri}' style='width: 230px; height: auto; align-content: center; margin: 5px;'></center><label style='font-size:12px'>{shortName}</label></br><label style='font-size:8px'>{title}</label></div>")
////		SafeHtml masterImage(SafeUri imageUri, String shortName, String title, String imageFormat);
//		@XTemplate("<figure style='border-style: solid; border-color: #0073e6; border-width: 3px; margin: 0;'>"
//				+ "<img src='{imageUri}' style='position: relative; padding: 5px; width: 230px; background: white;'>"
//				+ "<figcaption style='font-size:12px; padding: 10px; text-align: center;'>{shortName} ({imageFormat})<p style='font-size:10px;'> <tpl for='titleList'> {element}<wbr> </tpl> </p></figcaption></figure>")
//		SafeHtml masterImage(SafeUri imageUri, String shortName, ArrayList<TitleElement> titleList, String imageFormat);
//	}

	public DepictionEditor(DepictionEntry entry) {
		if (entry != null) {
			correspondingDepictionEntry = entry;
		} else {
			correspondingDepictionEntry = new DepictionEntry();
		}

		imgProperties = GWT.create(ImageProperties.class);
		imageEntryLS = new ListStore<ImageEntry>(imgProperties.imageID());
		if (correspondingDepictionEntry.getDepictionID() > 0) {
			loadImages();
		}
		vendorProps = GWT.create(VendorProperties.class);
		vendorEntryLS = new ListStore<VendorEntry>(vendorProps.vendorID());
		styleProps = GWT.create(StyleProperties.class);
		styleEntryLS = new ListStore<StyleEntry>(styleProps.styleID());
		caveProps = GWT.create(CaveProperties.class);
		caveEntryLS = new ListStore<CaveEntry>(caveProps.caveID());
		locationProps = GWT.create(LocationProperties.class);
		locationEntryLS = new ListStore<LocationEntry>(locationProps.locationID());
		expedProps = GWT.create(ExpeditionProperties.class);
		expedEntryLS = new ListStore<ExpeditionEntry>(expedProps.expeditionID());
		morProps = GWT.create(ModesOfRepresentationProperties.class);
		modeOfRepresentationLS = new ListStore<ModeOfRepresentationEntry>(morProps.modeOfRepresentationID());
		presAttributeProps = GWT.create(PreservationAttributeProperties.class);
		preservationAttributesLS = new ListStore<PreservationAttributeEntry>(presAttributeProps.preservationAttributeID());
		selectedPreservationAttributesLS = new ListStore<PreservationAttributeEntry>(presAttributeProps.preservationAttributeID());

		initPanel();
		loadCaves();
		loadLocations();
		loadStyles();
		loadVendors();
		loadExpeditions();
		loadModesOfRepresentation();
		loadPreservationAttributes();
	}

	/**
	 * 
	 */
	private void loadExpeditions() {
		for (ExpeditionEntry exped : StaticTables.getInstance().getExpeditionEntries().values()) {
			expedEntryLS.add(exped);
		}
		if (correspondingDepictionEntry.getExpedition() != null) {
			expedSelectionCB.setValue(correspondingDepictionEntry.getExpedition());
		}
	}

	/**
	 * 
	 */
	private void loadVendors() {
		for (VendorEntry ve : StaticTables.getInstance().getVendorEntries().values()) {
			vendorEntryLS.add(ve);
		}
		if (correspondingDepictionEntry.getVendor() != null) {
			vendorSelection.setValue(correspondingDepictionEntry.getVendor());
		}
		vendorEntryLS.addSortInfo(new StoreSortInfo<VendorEntry>(new ValueProvider<VendorEntry, String>() {

			@Override
			public String getValue(VendorEntry object) {
				return object.getVendorName();
			}

			@Override
			public void setValue(VendorEntry object, String value) {
			}

			@Override
			public String getPath() {
				return "vendorName";
			}
		}, SortDir.ASC));
	}
	
	/**
	 * 
	 */
	private void loadModesOfRepresentation() {
		for (ModeOfRepresentationEntry morEntry : StaticTables.getInstance().getModesOfRepresentationEntries().values()) {
			modeOfRepresentationLS.add(morEntry);
		}
		if (correspondingDepictionEntry.getModeOfRepresentationID() > 0) {
			modeOfRepresentationSelectionCB.setValue(modeOfRepresentationLS.findModelWithKey(Integer.toString(correspondingDepictionEntry.getModeOfRepresentationID())));
		}
	}

	/**
	 * 
	 */
	private void loadStyles() {
		for (StyleEntry se : StaticTables.getInstance().getStyleEntries().values()) {
			styleEntryLS.add(se);
		}
		if (correspondingDepictionEntry.getStyleID() > 0) {
			styleSelection.setValue(styleEntryLS.findModelWithKey(Integer.toString(correspondingDepictionEntry.getStyleID())));
		}
	}

	/**
	 * 
	 */
	private void loadCaves() {
		dbService.getCaves(new AsyncCallback<ArrayList<CaveEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<CaveEntry> caveResults) {
				for (CaveEntry ce : caveResults) {
					caveEntryLS.add(ce);
				}
				if (correspondingDepictionEntry.getCave() != null) {
					CaveEntry ce = correspondingDepictionEntry.getCave();
//					CaveEntry ce = caveEntryLS.findModelWithKey(Integer.toString(correspondingDepictionEntry.getCaveID()));
					caveSelectionCB.setValue(ce);
					wallSelectorPanel.setCave(ce);
					wallSelectorPanel.selectWall(correspondingDepictionEntry.getWallID());
				}
			}
		});
	}
	
	 private void loadLocations() {
			for (LocationEntry locEntry : StaticTables.getInstance().getLocationEntries().values()) {
				locationEntryLS.add(locEntry);
			}
//			locationEntryLS.addSortInfo(new StoreSortInfo<LocationEntry>(new ValueProvider<LocationEntry, String>(){
//
//				@Override
//				public String getValue(LocationEntry object) {
//					return object.getName();
//				}
//
//				@Override
//				public void setValue(LocationEntry object, String value) {}
//
//				@Override
//				public String getPath() {
//					return "name";
//				}}, SortDir.ASC));
			
			if (correspondingDepictionEntry.getLocation() != null) {
				locationSelectionCB.setValue(correspondingDepictionEntry.getLocation());
			}
	 }
	 
	 private void loadPreservationAttributes() {
		 dbService.getPreservationAttributes(new AsyncCallback<ArrayList<PreservationAttributeEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<PreservationAttributeEntry> result) {
				preservationAttributesLS.clear();
				for (PreservationAttributeEntry pae : result) {
					preservationAttributesLS.add(pae);
				}
				preservationAttributesLS.addSortInfo(new StoreSortInfo<PreservationAttributeEntry>(presAttributeProps.name(), SortDir.ASC));
			}
		});
	 }

	/**
	 * 
	 */
	private void loadImages() {
		for (ImageEntry ie : correspondingDepictionEntry.getRelatedImages()) {
			imageEntryLS.add(ie);
		}
		
//		dbService.getRelatedImages(correspondingDepictionEntry.getDepictionID(), new AsyncCallback<ArrayList<ImageEntry>>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				caught.printStackTrace();
//			}
//
//			@Override
//			public void onSuccess(ArrayList<ImageEntry> imgResults) {
//				for (ImageEntry ie : imgResults) {
//					imageEntryLS.add(ie);
//				}
//			}
//		});
	}

	@Override
	public Widget asWidget() {
		if (mainPanel == null) {
			initPanel();
		}
		return mainPanel;
	}

	/**
	 * Here the view is created. This is only done once at the beginning!
	 */
	private void initPanel() {

		// the images related with the depiction entry that will be shown on the right
		imageListView = new ListView<ImageEntry, ImageEntry>(imageEntryLS, new IdentityValueProvider<ImageEntry>() {
			@Override
			public void setValue(ImageEntry object, ImageEntry value) {
			}
		});

		imageListView.setCell(new SimpleSafeHtmlCell<ImageEntry>(new AbstractSafeHtmlRenderer<ImageEntry>() {
			final ImageViewTemplates imageViewTemplates = GWT.create(ImageViewTemplates.class);

			public SafeHtml render(ImageEntry item) {
				SafeUri imageUri = UriUtils.fromString("resource?imageID=" + item.getImageID() + "&thumb=300" + UserLogin.getInstance().getUsernameSessionIDParameterForUri());
				ArrayList<TitleElement> titleList = new ArrayList<TitleElement>();
				for (String s : item.getTitle().split("_")) {
					titleList.add(new TitleElement(s));
				}
				String imageAuthor = item.getImageAuthor() != null ? "Author: " + item.getImageAuthor().getLabel() : "";
				String copyrightStr = (item.getCopyright() != null && item.getCopyright().length() > 0) ? "\u00A9 " + item.getCopyright() : ""; 
				
				if (item.getImageID() == correspondingDepictionEntry.getMasterImageID()) {
					return imageViewTemplates.masterImage(imageUri, item.getShortName(), titleList, item.getFilename().substring(item.getFilename().lastIndexOf(".")+1).toUpperCase(), imageAuthor, copyrightStr);
				} else if (item.isOpenAccess()) {
					return imageViewTemplates.openAccessImage(imageUri, item.getShortName(), titleList, item.getFilename().substring(item.getFilename().lastIndexOf(".")+1).toUpperCase(), imageAuthor, copyrightStr);
				} else {
					return imageViewTemplates.nonOpenAccessImage(imageUri, item.getShortName(), titleList, item.getFilename().substring(item.getFilename().lastIndexOf(".")+1).toUpperCase(), imageAuthor, copyrightStr);
				}
			}
		}));

//		imageListView.setSize("340", "290");
		ListField<ImageEntry, ImageEntry> imageViewLF = new ListField<ImageEntry, ImageEntry>(imageListView);
//		imageViewLF.setSize("250px", "1.0");

		/**
		 * --------------------- content of first tab (BASICS) starts here --------------------------------
		 */
		
		FramedPanel shortNameFP = new FramedPanel();
		shortNameFP.setHeading("Short Name");
		shortNameTF = new TextField();
		shortNameTF.setEmptyText("optional short name");
		shortNameTF.setValue(correspondingDepictionEntry.getShortName());
		shortNameTF.addValidator(new MaxLengthValidator(32));
		shortNameTF.addValidator(new Validator<String>() {
			
			@Override
			public List<EditorError> validate(Editor<String> editor, String value) {
				List<EditorError> l = new ArrayList<EditorError>();
				if ((caveSelectionCB.getCurrentValue() == null) && ((shortNameTF.getCurrentValue() == null) || (shortNameTF.getCurrentValue().isEmpty()))) {
					l.add(new DefaultEditorError(editor, "please select either Cave or enter short name", value));
				}
				return l;
			}
		});
		shortNameTF.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setShortName(event.getValue());
			}
		});
		CheckBox openAccessCB = new CheckBox();
		openAccessCB.setBoxLabel("open access");
		openAccessCB.setValue(correspondingDepictionEntry.isOpenAccess());
		openAccessCB.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				correspondingDepictionEntry.setOpenAccess(event.getValue());
			}
		});
		VerticalLayoutContainer shortNameVLC = new VerticalLayoutContainer();
		shortNameVLC.add(shortNameTF, new VerticalLayoutData(1.0, .5));
		shortNameVLC.add(openAccessCB, new VerticalLayoutData(1.0, .5));
		shortNameFP.add(shortNameVLC);

		
		FramedPanel caveSelectionFP = new FramedPanel();
		caveSelectionFP.setHeading("Located in Cave");
		caveSelectionCB = new ComboBox<CaveEntry>(caveEntryLS, new LabelProvider<CaveEntry>() {

			@Override
			public String getLabel(CaveEntry item) {
				StaticTables st = StaticTables.getInstance();
				DistrictEntry de = null;
				SiteEntry se = null;
				de = st.getDistrictEntries().get(item.getDistrictID());
				if (de != null) {
					se = st.getSiteEntries().get(de.getSiteID());
				}
				return (se != null ? se.getName()+": " : (de != null ? de.getName()+": " : "")) + item.getOfficialNumber() 
					+ (item.getHistoricName() != null ? " "+item.getHistoricName() : "");
			}
		}, new AbstractSafeHtmlRenderer<CaveEntry>() {

			@Override
			public SafeHtml render(CaveEntry item) {
				final CaveViewTemplates cvTemplates = GWT.create(CaveViewTemplates.class);
				StaticTables st = StaticTables.getInstance();
				DistrictEntry de = null;
				SiteEntry se = null;
				de = st.getDistrictEntries().get(item.getDistrictID());
				if (de != null) {
					se = st.getSiteEntries().get(de.getSiteID());
				}
				String siteDistrictInformation = (se != null ? se.getName() : "") + (de != null ? (se != null ? " / " : "") + de.getName() : "");
				if ((item.getHistoricName() != null) && (item.getHistoricName().length() > 0)) {
					return cvTemplates.caveLabel(siteDistrictInformation, item.getOfficialNumber(), item.getHistoricName());
				} else {
					return cvTemplates.caveLabel(siteDistrictInformation, item.getOfficialNumber());
				}
			}
		});
		caveSelectionCB.setEmptyText("nothing selected");
		caveSelectionCB.setTypeAhead(true);
		caveSelectionCB.setEditable(false);
		caveSelectionCB.setTriggerAction(TriggerAction.ALL);
		caveSelectionCB.addSelectionHandler(new SelectionHandler<CaveEntry>() {

			@Override
			public void onSelection(SelectionEvent<CaveEntry> event) {
				correspondingDepictionEntry.setCave(event.getSelectedItem());
				wallSelectorPanel.setCave(event.getSelectedItem());
				shortNameTF.validate();
			}
		});
		caveSelectionCB.setToolTip("This field can only be changed until a depiction is allocated to a wall");
		// TODO check if wallTypeID is set, then set caveSelectionCB.editable(false)
		ToolButton resetCaveSelectionTB = new ToolButton(ToolButton.REFRESH);
		resetCaveSelectionTB.setToolTip(Util.createToolTip("reset selection"));
		resetCaveSelectionTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				caveSelectionCB.setValue(null, true);
			}
		});
		caveSelectionFP.addTool(resetCaveSelectionTB);
		caveSelectionFP.add(caveSelectionCB);

		FramedPanel acquiredByExpeditionFP = new FramedPanel();
		acquiredByExpeditionFP.setHeading("Acquired by expedition");
		expedSelectionCB = new ComboBox<ExpeditionEntry>(expedEntryLS, expedProps.name(), new AbstractSafeHtmlRenderer<ExpeditionEntry>() {

			@Override
			public SafeHtml render(ExpeditionEntry item) {
				final ExpeditionViewTemplates expedTemplates = GWT.create(ExpeditionViewTemplates.class);
				DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy");
				return expedTemplates.expedLabel(item.getName(), item.getLeader(), dtf.format(item.getStartDate()), dtf.format(item.getEndDate()));
			}
		});
		expedSelectionCB.setEmptyText("nothing selected");
		expedSelectionCB.addSelectionHandler(new SelectionHandler<ExpeditionEntry>() {

			@Override
			public void onSelection(SelectionEvent<ExpeditionEntry> event) {
				correspondingDepictionEntry.setExpedition(event.getSelectedItem());
			}
		});
		expedSelectionCB.setTypeAhead(false);
		expedSelectionCB.setEditable(false);
		expedSelectionCB.setTriggerAction(TriggerAction.ALL);
		expedSelectionCB.addSelectionHandler(new SelectionHandler<ExpeditionEntry>() {

			@Override
			public void onSelection(SelectionEvent<ExpeditionEntry> event) {
				correspondingDepictionEntry.setExpedition(event.getSelectedItem());
			}
		});
		ToolButton expedSelectionTB = new ToolButton(ToolButton.REFRESH);
		expedSelectionTB.setToolTip(Util.createToolTip("reset selection"));
		expedSelectionTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				expedSelectionCB.setValue(null, true);
			}
		});
		acquiredByExpeditionFP.addTool(expedSelectionTB);
		acquiredByExpeditionFP.add(expedSelectionCB);
		
		FramedPanel vendorFP = new FramedPanel();
		vendorFP.setHeading("Vendor");
		vendorSelection = new ComboBox<VendorEntry>(vendorEntryLS, vendorProps.vendorName(), new AbstractSafeHtmlRenderer<VendorEntry>() {

			@Override
			public SafeHtml render(VendorEntry item) {
				final VendorViewTemplates vTemplates = GWT.create(VendorViewTemplates.class);
				return vTemplates.vendorName(item.getVendorName());
			}

		});
		vendorSelection.setEmptyText("nothing selected");
		vendorSelection.setTypeAhead(false);
		vendorSelection.setEditable(false);
		vendorSelection.setTriggerAction(TriggerAction.ALL);
		vendorSelection.addSelectionHandler(new SelectionHandler<VendorEntry>() {

			@Override
			public void onSelection(SelectionEvent<VendorEntry> event) {
				correspondingDepictionEntry.setVendor(event.getSelectedItem());
			}
		});
		ToolButton resetVendorSelectionTB = new ToolButton(ToolButton.REFRESH);
		resetVendorSelectionTB.setToolTip(Util.createToolTip("reset selection"));
		resetVendorSelectionTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				vendorSelection.setValue(null, true);
			}
		});
		// adding new vendors is necessary
		ToolButton newVendorPlusTool = new ToolButton(ToolButton.PLUS);
		newVendorPlusTool.setToolTip(Util.createToolTip("add Vendor"));
		newVendorPlusTool.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				PopupPanel addVendorDialog = new PopupPanel();
				FramedPanel newVendorFP = new FramedPanel();
				newVendorFP.setHeading("Add Vendor");
				TextField vendorNameField = new TextField();
				vendorNameField.addValidator(new MinLengthValidator(2));
				vendorNameField.addValidator(new MaxLengthValidator(32));
				vendorNameField.setValue("");
				vendorNameField.setWidth(200);
				newVendorFP.add(vendorNameField);
				TextButton saveButton = new TextButton("save");
				saveButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						if (vendorNameField.isValid()) {
							VendorEntry vEntry = new VendorEntry();
							vEntry.setVendorName(vendorNameField.getCurrentValue());
							dbService.insertVendorEntry(vEntry, new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}

								@Override
								public void onSuccess(Integer result) {
									vEntry.setVendorID(result);
									vendorEntryLS.add(vEntry);
								}
							});
							addVendorDialog.hide();
						}
					}
				});
				newVendorFP.addButton(saveButton);
				TextButton cancelButton = new TextButton("cancel");
				cancelButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						addVendorDialog.hide();
					}
				});
				newVendorFP.addButton(cancelButton);
				addVendorDialog.add(newVendorFP);
				addVendorDialog.setModal(true);
				addVendorDialog.center();
			}
		});		
		vendorFP.addTool(resetVendorSelectionTB);
		vendorFP.addTool(newVendorPlusTool);
		vendorFP.add(vendorSelection);

		FramedPanel datePurchasedFP = new FramedPanel();
		datePurchasedFP.setHeading("Date purchased");
		purchaseDateField = new DateField(new DateTimePropertyEditor("yyyy"));
		purchaseDateField.setValue(correspondingDepictionEntry.getPurchaseDate());
		purchaseDateField.setEmptyText("nothing selected");
		// TODO add change handler
		datePurchasedFP.add(purchaseDateField);
		
		FramedPanel currentLocationFP = new FramedPanel();
		currentLocationFP.setHeading("Current Location");
		locationSelectionCB = new ComboBox<LocationEntry>(locationEntryLS, locationProps.name(), new AbstractSafeHtmlRenderer<LocationEntry>() {

			@Override
			public SafeHtml render(LocationEntry item) {
				final LocationViewTemplates lvTemplates = GWT.create(LocationViewTemplates.class);
				if ((item.getCounty() != null) && (!item.getCounty().isEmpty())) {
					if ((item.getTown() != null) && (!item.getTown().isEmpty())) {
						return lvTemplates.caveLabel(item.getName(), item.getRegion()!=null && !item.getRegion().isEmpty() ? item.getTown()+" ("+item.getRegion()+")" : item.getTown(), item.getCounty());
					} else if ((item.getRegion() != null) && (!item.getRegion().isEmpty())) {
						return lvTemplates.caveLabel(item.getName(), item.getTown()!=null && !item.getTown().isEmpty() ? item.getTown()+" ("+item.getRegion()+")" : item.getRegion(), item.getCounty());
					} else {
						return lvTemplates.caveLabel(item.getName(), item.getCounty());
					}
				} else {
					return lvTemplates.caveLabel(item.getName());
				}
			}
		});
		locationSelectionCB.setEmptyText("select current location");
		locationSelectionCB.setTypeAhead(false);
		locationSelectionCB.setEditable(false);
		locationSelectionCB.setTriggerAction(TriggerAction.ALL);
		locationSelectionCB.addValueChangeHandler(new ValueChangeHandler<LocationEntry>() {

			@Override
			public void onValueChange(ValueChangeEvent<LocationEntry> event) {
				correspondingDepictionEntry.setLocation(event.getValue());
			}
		});
		ToolButton resetLocationSelectionTB = new ToolButton(ToolButton.REFRESH);
		resetLocationSelectionTB.setToolTip(Util.createToolTip("reset selection"));
		resetLocationSelectionTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				locationSelectionCB.setValue(null, true);
			}
		});
		// adding new locations
		ToolButton newLocationPlusTool = new ToolButton(ToolButton.PLUS);
		newLocationPlusTool.setToolTip(Util.createToolTip("add Location"));
		newLocationPlusTool.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				PopupPanel addLocationDialog = new PopupPanel();
				FramedPanel newLocationFP = new FramedPanel();
				newLocationFP.setHeading("Add Location");
				VerticalLayoutContainer locationVLC = new VerticalLayoutContainer();
				TextField locationNameField = new TextField();
				locationNameField.addValidator(new MinLengthValidator(2));
				locationNameField.addValidator(new MaxLengthValidator(64));
				locationVLC.add(new FieldLabel(locationNameField, "Name"));
				TextField locationTownField = new TextField();
				locationTownField.addValidator(new MinLengthValidator(2));
				locationTownField.addValidator(new MaxLengthValidator(64));
				locationVLC.add(new FieldLabel(locationTownField, "Town"));
				TextField locationRegionField = new TextField();
				locationRegionField.addValidator(new MinLengthValidator(2));
				locationRegionField.addValidator(new MaxLengthValidator(64));
				locationVLC.add(new FieldLabel(locationRegionField, "Region"));
				TextField locationCountryField = new TextField();
				locationCountryField.addValidator(new MinLengthValidator(2));
				locationCountryField.addValidator(new MaxLengthValidator(64));
				locationVLC.add(new FieldLabel(locationCountryField, "Country"));
				TextField locationUrlField = new TextField();
				locationUrlField.addValidator(new MinLengthValidator(2));
				locationUrlField.addValidator(new MaxLengthValidator(256));
				locationVLC.add(new FieldLabel(locationUrlField, "URL"));
				newLocationFP.add(locationVLC);
				TextButton saveButton = new TextButton("save");
				saveButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						if (locationNameField.isValid()) {
							LocationEntry lEntry = new LocationEntry();
							lEntry.setName(locationNameField.getCurrentValue());
							lEntry.setTown(locationTownField.getCurrentValue());
							lEntry.setRegion(locationRegionField.getCurrentValue());
							lEntry.setCounty(locationCountryField.getCurrentValue());
							lEntry.setUrl(locationUrlField.getCurrentValue());
							dbService.insertLocationEntry(lEntry, new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}

								@Override
								public void onSuccess(Integer result) {
									lEntry.setLocationID(result);
									locationEntryLS.add(lEntry);
								}
							});
							addLocationDialog.hide();
						}
					}
				});
				newLocationFP.addButton(saveButton);
				TextButton cancelButton = new TextButton("cancel");
				cancelButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						addLocationDialog.hide();
					}
				});
				newLocationFP.addButton(cancelButton);
				addLocationDialog.add(newLocationFP);
				addLocationDialog.setModal(true);
				addLocationDialog.center();
			}
		});				
		currentLocationFP.addTool(resetLocationSelectionTB);
		currentLocationFP.addTool(newLocationPlusTool);
		currentLocationFP.add(locationSelectionCB);

		FramedPanel inventoryNumberFP = new FramedPanel();
		inventoryNumberFP.setHeading("Inventory Number");
		TextField inventoryNumberTF = new TextField();
		inventoryNumberTF.addValidator(new MaxLengthValidator(128));
		inventoryNumberTF.setValue(correspondingDepictionEntry.getInventoryNumber());
		inventoryNumberTF.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setInventoryNumber(event.getValue());
			}
		});
		inventoryNumberFP.add(inventoryNumberTF);
		
		FramedPanel stateOfPreservationFP = new FramedPanel();
		stateOfPreservationFP.setHeading("State of Preservation");
		ToolButton addPreservationAttributeTB = new ToolButton(ToolButton.PLUS);
		addPreservationAttributeTB.setToolTip(Util.createToolTip("Add Preservation Attribute"));
		stateOfPreservationFP.addTool(addPreservationAttributeTB);
		addPreservationAttributeTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				PopupPanel addPreservationAttributeDialog = new PopupPanel();
				FramedPanel newPreservationAttributeFP = new FramedPanel();
				newPreservationAttributeFP.setHeading("Add Preservation Attribute");
				TextField preservationAttributeNameField = new TextField();
				preservationAttributeNameField.addValidator(new MinLengthValidator(2));
				preservationAttributeNameField.addValidator(new MaxLengthValidator(32));
				preservationAttributeNameField.setValue("");
				preservationAttributeNameField.setWidth(200);
				newPreservationAttributeFP.add(preservationAttributeNameField);
				TextButton saveButton = new TextButton("save");
				saveButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						if (preservationAttributeNameField.isValid()) {
							PreservationAttributeEntry paEntry = new PreservationAttributeEntry();
							paEntry.setName(preservationAttributeNameField.getCurrentValue());
							dbService.insertPreservationAttributeEntry(paEntry, new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									newPreservationAttributeFP.hide();
								}

								@Override
								public void onSuccess(Integer result) {
									paEntry.setPreservationAttributeID(result);
									preservationAttributesLS.add(paEntry);
									addPreservationAttributeDialog.hide();
								}
							});
						}
					}
				});
				newPreservationAttributeFP.addButton(saveButton);
				TextButton cancelButton = new TextButton("cancel");
				cancelButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						addPreservationAttributeDialog.hide();
					}
				});
				newPreservationAttributeFP.addButton(cancelButton);
				addPreservationAttributeDialog.add(newPreservationAttributeFP);
				addPreservationAttributeDialog.setModal(true);
				addPreservationAttributeDialog.center();
			}
		});
		ListView<PreservationAttributeEntry, String> preservationAttributesListView = new ListView<PreservationAttributeEntry, String>(preservationAttributesLS, presAttributeProps.name());
		preservationAttributesListView.setToolTip("to select attributes drag right");
		ListView<PreservationAttributeEntry, String> selectedPreservationAttributesListView = new ListView<PreservationAttributeEntry, String>(selectedPreservationAttributesLS, presAttributeProps.name());
		selectedPreservationAttributesListView.setToolTip("to deselect attributes drag left");
		
		new ListViewDragSource<PreservationAttributeEntry>(preservationAttributesListView).setGroup("paGroup");
		new ListViewDragSource<PreservationAttributeEntry>(selectedPreservationAttributesListView).setGroup("paGroup");
		
		new ListViewDropTarget<PreservationAttributeEntry>(preservationAttributesListView).setGroup("paGroup");
		new ListViewDropTarget<PreservationAttributeEntry>(selectedPreservationAttributesListView).setGroup("paGroup");

		BorderLayoutContainer borderLayoutContainer = new BorderLayoutContainer();
    borderLayoutContainer.setWestWidget(preservationAttributesListView, new BorderLayoutData(0.5));
    borderLayoutContainer.setCenterWidget(selectedPreservationAttributesListView, new BorderLayoutData(0.5));

    stateOfPreservationFP.add(borderLayoutContainer);
    
		VerticalLayoutContainer basicsLeftVLC = new VerticalLayoutContainer();
		
		basicsLeftVLC.add(shortNameFP, new VerticalLayoutData(1.0, .15));
		basicsLeftVLC.add(caveSelectionFP, new VerticalLayoutData(1.0, .1));
		basicsLeftVLC.add(acquiredByExpeditionFP, new VerticalLayoutData(1.0, .1));
		basicsLeftVLC.add(vendorFP, new VerticalLayoutData(1.0, .1));
		basicsLeftVLC.add(datePurchasedFP, new VerticalLayoutData(1.0, .1));
		basicsLeftVLC.add(currentLocationFP, new VerticalLayoutData(1.0, .1));
		basicsLeftVLC.add(inventoryNumberFP, new VerticalLayoutData(1.0, .1));
//		basicsLeftVLC.add(positionNoteFP, new VerticalLayoutData(1.0, .15));
		basicsLeftVLC.add(stateOfPreservationFP, new VerticalLayoutData(1.0, .25));

		FramedPanel wallSelectorFP = new FramedPanel();
		wallSelectorFP.setHeading("Wall");
		ToolButton wallEditorTB = new ToolButton(ToolButton.PIN);
		wallEditorTB.setEnabled(false);
		wallEditorTB.setToolTip(Util.createToolTip("set position on wall"));
		
//		TextButton wallEditorButton = new TextButton("set position on wall");
		wallEditor = new Walls(1, false);
		wallEditorTB.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				wallEditorDialog = new PopupPanel();
				wallEditorDialog.add(wallEditor);
				wallEditor.createNewDepictionOnWall(correspondingDepictionEntry, true, true);
				wallEditor.setPanel(wallEditorDialog);
				wallEditorDialog.setModal(true);
				wallEditorDialog.center();
			}
		});
		/**
		 * the wall visualisation will be implemented at a later time
		 */
//		wallSelectorFP.addTool(wallEditorTB);
		wallSelectorPanel = new WallSelector(new SelectionHandler<WallEntry>() {

			@Override
			public void onSelection(SelectionEvent<WallEntry> event) {
				correspondingDepictionEntry.setWallID(event.getSelectedItem().getWallLocationID());
			}
		});
		wallSelectorFP.add(wallSelectorPanel);

		FramedPanel positionNoteFP = new FramedPanel();
		positionNoteFP.setHeading("Position Notes");
		TextArea positionNotesTA = new TextArea();
		positionNotesTA.setValue(correspondingDepictionEntry.getPositionNotes());
		positionNotesTA.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setPositionNotes(event.getValue());
			}
		});
		positionNoteFP.add(positionNotesTA);
		
		VerticalLayoutContainer basicsRightVLC = new VerticalLayoutContainer();
		basicsRightVLC.add(wallSelectorFP, new VerticalLayoutData(1.0, .85));
		basicsRightVLC.add(positionNoteFP, new VerticalLayoutData(1.0, .15));

		HorizontalLayoutContainer basicsTabHLC = new HorizontalLayoutContainer();
		basicsTabHLC.add(basicsLeftVLC, new HorizontalLayoutData(.4, 1.0));
		basicsTabHLC.add(basicsRightVLC, new HorizontalLayoutData(.6, 1.0));

		/**
		 * --------------------- content of second tab (Descriptions) starts here --------------------------------
		 */

		HorizontalLayoutContainer dimensionHLC = new HorizontalLayoutContainer();
		FramedPanel widthFP = new FramedPanel();
		widthFP.setHeading("Width");
		widthNF = new NumberField<Double>(new NumberPropertyEditor.DoublePropertyEditor());
		widthNF.addValidator(new MinNumberValidator<Double>((double) 0));
		widthNF.setValue(correspondingDepictionEntry.getWidth());
		widthNF.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				correspondingDepictionEntry.setWidth(event.getValue());
			}
		});
		widthFP.add(widthNF);
		dimensionHLC.add(widthFP, new HorizontalLayoutData(.5	, 1.0));
		
		FramedPanel heightFP = new FramedPanel();
		heightFP.setHeading("Height");
		heightNF = new NumberField<Double>(new NumberPropertyEditor.DoublePropertyEditor());
		heightNF.addValidator(new MinNumberValidator<Double>((double) 0));
		heightNF.setValue(correspondingDepictionEntry.getHeight());
		heightNF.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				correspondingDepictionEntry.setHeight(event.getValue());
			}
		});
		heightFP.add(heightNF);
		dimensionHLC.add(heightFP, new HorizontalLayoutData(.5, 1.0));

		FramedPanel styleFP = new FramedPanel();
		styleFP.setHeading("Style");
		styleSelection = new ComboBox<StyleEntry>(styleEntryLS, styleProps.styleName(), new AbstractSafeHtmlRenderer<StyleEntry>() {

			@Override
			public SafeHtml render(StyleEntry item) {
				final StyleViewTemplates svTemplates = GWT.create(StyleViewTemplates.class);
				return svTemplates.styleName(item.getStyleName());
			}
		});
		styleSelection.setEmptyText("nothing selected");
		styleSelection.setTypeAhead(false);
		styleSelection.setEditable(false);
		styleSelection.setTriggerAction(TriggerAction.ALL);
		styleSelection.addSelectionHandler(new SelectionHandler<StyleEntry>() {

			@Override
			public void onSelection(SelectionEvent<StyleEntry> event) {
				correspondingDepictionEntry.setStyleID(event.getSelectedItem().getStyleID());
			}
		});
		ToolButton resetStyleSelectionTB = new ToolButton(ToolButton.REFRESH);
		resetStyleSelectionTB.setToolTip(Util.createToolTip("reset selection"));
		resetStyleSelectionTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				styleSelection.setValue(null, true);
			}
		});
		styleFP.addTool(resetStyleSelectionTB);
		styleFP.add(styleSelection);

		FramedPanel modesOfRepresentationFP = new FramedPanel();
		modesOfRepresentationFP.setHeading("Modes of Representation");
		modeOfRepresentationSelectionCB = new ComboBox<ModeOfRepresentationEntry>(modeOfRepresentationLS, morProps.name(), new AbstractSafeHtmlRenderer<ModeOfRepresentationEntry>() {

			@Override
			public SafeHtml render(ModeOfRepresentationEntry morEntry) {
				ModesOfRepresentationViewTemplates morTemplates = GWT.create(ModesOfRepresentationViewTemplates.class);
				return morTemplates.morLabel(morEntry.getName());
			}
			
		});
		modeOfRepresentationSelectionCB.setEmptyText("nothing selected");
		modeOfRepresentationSelectionCB.setTypeAhead(false);
		modeOfRepresentationSelectionCB.setEditable(false);
		modeOfRepresentationSelectionCB.setTriggerAction(TriggerAction.ALL);
		modeOfRepresentationSelectionCB.addSelectionHandler(new SelectionHandler<ModeOfRepresentationEntry>() {

			@Override
			public void onSelection(SelectionEvent<ModeOfRepresentationEntry> event) {
				correspondingDepictionEntry.setModeOfRepresentationID(event.getSelectedItem().getModeOfRepresentationID());
			}
		});
		ToolButton resetModeOfRepresentationSelectionTB = new ToolButton(ToolButton.REFRESH);
		resetModeOfRepresentationSelectionTB.setToolTip(Util.createToolTip("reset selection"));
		resetModeOfRepresentationSelectionTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				modeOfRepresentationSelectionCB.setValue(null, true);
			}
		});
		modesOfRepresentationFP.addTool(resetModeOfRepresentationSelectionTB);
		modesOfRepresentationFP.add(modeOfRepresentationSelectionCB);

		FramedPanel backgroundColourFP = new FramedPanel();
		backgroundColourFP.setHeading("Background colour");
		backgroundColourField = new TextField();
		backgroundColourField.setValue(correspondingDepictionEntry.getBackgroundColour());
		backgroundColourField.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setBackgroundColour(event.getValue());
			}
		});
		backgroundColourFP.add(backgroundColourField);

		FramedPanel inscriptionsFP = new FramedPanel();
		inscriptionsFP.setHeading("Inscriptions");
		inscriptionsTestArea = new TextArea();
		inscriptionsTestArea.setText(correspondingDepictionEntry.getInscriptions());
		inscriptionsTestArea.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setInscriptions(event.getValue());
			}
		});
		inscriptionsFP.add(inscriptionsTestArea);

		FramedPanel separateAksarasFP = new FramedPanel();
		separateAksarasFP.setHeading("Separate Akṣaras");
		separateAksarasTextArea = new TextArea();
		separateAksarasTextArea.setText(correspondingDepictionEntry.getSeparateAksaras());
		separateAksarasTextArea.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setSeparateAksaras(event.getValue());
			}
		});
		separateAksarasFP.add(separateAksarasTextArea);

		FramedPanel datingFP  = new FramedPanel();
		datingFP.setHeading("Dating");
		datingField = new TextField();
		// datingField.setWidth(130);
		datingField.setText(correspondingDepictionEntry.getDating());
		datingField.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setDating(event.getValue());
			}
		});
		datingFP.add(datingField);

		VerticalLayoutContainer descriptionLeftVLC = new VerticalLayoutContainer();
		descriptionLeftVLC.add(dimensionHLC, new VerticalLayoutData(1.0, .1));
		descriptionLeftVLC.add(styleFP, new VerticalLayoutData(1.0, .1));
		descriptionLeftVLC.add(modesOfRepresentationFP, new VerticalLayoutData(1.0, .1));
		descriptionLeftVLC.add(backgroundColourFP, new VerticalLayoutData(1.0, .1));
		descriptionLeftVLC.add(inscriptionsFP, new VerticalLayoutData(1.0, .25));
		descriptionLeftVLC.add(separateAksarasFP, new VerticalLayoutData(1.0, .25));
		descriptionLeftVLC.add(datingFP, new VerticalLayoutData(1.0, .1));

		FramedPanel descriptionFP = new FramedPanel();
		descriptionFP.setHeading("Description");
		descriptionArea = new TextArea();
		descriptionArea.setValue(correspondingDepictionEntry.getDescription());
		descriptionArea.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setDescription(event.getValue());
			}
		});
		descriptionFP.add(descriptionArea);

		FramedPanel generalRemarksFP = new FramedPanel();
		generalRemarksFP.setHeading("General remarks");
		generalRemarksArea = new TextArea();
		generalRemarksArea.setValue(correspondingDepictionEntry.getGeneralRemarks());
		generalRemarksArea.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setGeneralRemarks(event.getValue());
			}
		});
		generalRemarksFP.add(generalRemarksArea);

		FramedPanel otherSuggestedIdentificationsFP = new FramedPanel();
		otherSuggestedIdentificationsFP.setHeading("Other suggested identifications");
		TextArea otherSuggestedIdentificationsTA = new TextArea();
		otherSuggestedIdentificationsTA.setValue(correspondingDepictionEntry.getOtherSuggestedIdentifications());
		otherSuggestedIdentificationsTA.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				correspondingDepictionEntry.setOtherSuggestedIdentifications(event.getValue());
			}
		});
		otherSuggestedIdentificationsFP.add(otherSuggestedIdentificationsTA);

		VerticalLayoutContainer descriptionRightVLC = new VerticalLayoutContainer();
		descriptionRightVLC.add(descriptionFP, new VerticalLayoutData(1.0, 1.0 / 3));
		descriptionRightVLC.add(generalRemarksFP, new VerticalLayoutData(1.0, 1.0 / 3));
		descriptionRightVLC.add(otherSuggestedIdentificationsFP, new VerticalLayoutData(1.0, 1.0 / 3));

		HorizontalLayoutContainer descriptionTabHLC = new HorizontalLayoutContainer();
		descriptionTabHLC.add(descriptionLeftVLC, new HorizontalLayoutData(.35, 1.0));
		descriptionTabHLC.add(descriptionRightVLC, new HorizontalLayoutData(.65, 1.0));

		/**
		 * --------------------- definition of image panel on right side starts here --------------------------------
		 */
		imageSelector = new ImageSelector(new ImageSelectorListener() {

			@Override
			public void imageSelected(ImageEntry imgEntry) {
				if ((imgEntry != null) && (imgEntry.getImageID() != 0)) {
					imageEntryLS.add(imgEntry);
				}
				imageSelectionDialog.hide();
			}
		});
		
		ToolButton addImageTB = new ToolButton(ToolButton.PLUS);
		addImageTB.setToolTip(Util.createToolTip("add image"));
		addImageTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				imageSelectionDialog = new PopupPanel();
				imageSelectionDialog.add(imageSelector);
				imageSelectionDialog.setModal(true);
				imageSelectionDialog.center();
			}
		});

		ToolButton removeImageTB = new ToolButton(ToolButton.MINUS);
		removeImageTB.setToolTip(Util.createToolTip("remove image"));
		removeImageTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				imageEntryLS.remove(imageListView.getSelectionModel().getSelectedItem());
			}
		});
		
		ToolButton setMasterTB = new ToolButton(ToolButton.PIN);
		setMasterTB.setToolTip(Util.createToolTip("Set master image.", "The master image will be displayed on top of this list and used for previews in the system (e.g. thumbnails)."));
		setMasterTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				ImageEntry entry = imageListView.getSelectionModel().getSelectedItem();
				correspondingDepictionEntry.setMasterImageID(entry.getImageID());
				imageListView.refresh();
			}
		});
		
		ToolButton infoTB = new ToolButton(ToolButton.QUESTION);
		infoTB.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				PopupPanel dialog = new PopupPanel();
				FramedPanel infoDialogFP = new FramedPanel();
				infoDialogFP.setHeading("Colour schema");
				VerticalPanel infoVP = new VerticalPanel();
				infoVP.add(new HTML("<div><label style='font-size: 12px; color: #0073e6;'>Master Image</label></div>"));
				infoVP.add(new HTML("<div><label style='font-size: 12px; color: #99ff66;'>Open Access Image</label></div>"));
				infoVP.add(new HTML("<div><label style='font-size: 12px; color: #ff1a1a;'>Non Open Access Image</label></div>"));
				infoDialogFP.add(infoVP);
				TextButton okButton = new TextButton("OK");
				okButton.addSelectHandler(new SelectHandler() {
					
					@Override
					public void onSelect(SelectEvent event) {
						dialog.hide();
					}
				});
				infoDialogFP.addButton(okButton);
				dialog.add(infoDialogFP);
				dialog.setModal(true);
				dialog.setGlassEnabled(true);
				dialog.center();
			}
		});

		ToolButton zoomTB = new ToolButton(ToolButton.MAXIMIZE);
		zoomTB.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				ImageEntry ie = imageListView.getSelectionModel().getSelectedItem();
				if (ie != null) {
					com.google.gwt.user.client.Window.open("/resource?imageID=" + ie.getImageID() + UserLogin.getInstance().getUsernameSessionIDParameterForUri(),"_blank",null);
				}
			}
		});
		zoomTB.setToolTip(Util.createToolTip("View selected image in full size.", "This will open a new browser tab."));

		FramedPanel depictionImagesPanel = new FramedPanel();
		depictionImagesPanel.setHeading("Images");
		depictionImagesPanel.add(imageViewLF);
		depictionImagesPanel.addTool(infoTB);
		depictionImagesPanel.addTool(zoomTB);
		depictionImagesPanel.addTool(addImageTB);
		depictionImagesPanel.addTool(removeImageTB);
		depictionImagesPanel.addTool(setMasterTB);

		/**
		 * ---------------------- content of third tab (Iconography & Pictorial Elements) starts here ---------------------
		 */
		iconographySelector = new IconographySelector(StaticTables.getInstance().getIconographyEntries().values());
		dbService.getRelatedIconography(correspondingDepictionEntry.getDepictionID(), new AsyncCallback<ArrayList<IconographyEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<IconographyEntry> iconographyRelationList) {
				iconographySelector.setSelectedIconography(iconographyRelationList);
			}
		});

		/**
		 * ---------------------- content of fourth tab (Bibliography Selector) ---------------------
		 */
		bibliographySelector = new BibliographySelector(correspondingDepictionEntry.getRelatedBibliographyList());
//		if (correspondingDepictionEntry.getRelatedBibliographyList().size() > 0) {
//			bibliographySelector.setSelectedEntries(correspondingDepictionEntry.getRelatedBibliographyList());
//		}
		
		/**
		 * --------------------------- next the editor as a whole will be assembled -------------------
		 */

		TabPanel tabPanel = new TabPanel();
		tabPanel.setTabScroll(false);
		tabPanel.add(basicsTabHLC, "Basics");
		tabPanel.add(descriptionTabHLC, "Description");
		tabPanel.add(iconographySelector, "Iconography & Pictorial Elements");
		tabPanel.add(bibliographySelector, "Bibliography Selector");
		
		HorizontalLayoutContainer mainHLC = new HorizontalLayoutContainer();
		mainHLC.add(tabPanel, new HorizontalLayoutData(.7, 1.0));
		mainHLC.add(depictionImagesPanel, new HorizontalLayoutData(.3, 1.0));
		
		ToolButton saveToolButton = new ToolButton(ToolButton.SAVE);
		saveToolButton.setToolTip(Util.createToolTip("save"));
		saveToolButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				saveDepictionEntry(false);
			}
		});
		
		ToolButton closeToolButton = new ToolButton(ToolButton.CLOSE);
		closeToolButton.setToolTip(Util.createToolTip("close"));
		closeToolButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				de.cses.client.Util.showYesNo("Exit Warning!", "Do you wish to save before exiting?", new SelectHandler() {
					
					@Override
					public void onSelect(SelectEvent event) {
						saveDepictionEntry(true);
					}
				}, new SelectHandler() {
						
					@Override
					public void onSelect(SelectEvent event) {
						 closeEditor(null);
					}
				});
			}
		});
		
		mainPanel = new FramedPanel();
		mainPanel.setHeading("Painted Representation Editor (" + (correspondingDepictionEntry.getDepictionID() > 0 ? correspondingDepictionEntry.getShortName() : "NEW") + ")");

		mainPanel.add(mainHLC);
		mainPanel.setSize("900px", "650px");
		mainPanel.addTool(saveToolButton);
		mainPanel.addTool(closeToolButton);
	}

	/**
	 * Called when the save button is pressed. Calls <code>DepictionEditorListener.depictionSaved(correspondingDepictionEntry)<code>
	 * @param close 
	 */
	protected void saveDepictionEntry(boolean close) {
		if (!shortNameTF.validate()) {
			return;
		}
		ArrayList<ImageEntry> relatedImageEntryList = new ArrayList<ImageEntry>();
		for (int i = 0; i < imageEntryLS.size(); ++i) {
			relatedImageEntryList.add(imageEntryLS.get(i));
		}
		correspondingDepictionEntry.setRelatedImages(relatedImageEntryList);
		correspondingDepictionEntry.setRelatedBibliographyList(bibliographySelector.getSelectedEntries());
		correspondingDepictionEntry.setLastChangedByUser(UserLogin.getInstance().getUsername());
		
		if (correspondingDepictionEntry.getDepictionID() == 0) {
			dbService.insertDepictionEntry(correspondingDepictionEntry, iconographySelector.getSelectedIconography(), new AsyncCallback<Integer>() {

				@Override
				public void onSuccess(Integer newDepictionID) {
					correspondingDepictionEntry.setDepictionID(newDepictionID.intValue());
//					updateEntry(correspondingDepictionEntry);
					if (close) {
						closeEditor(correspondingDepictionEntry);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					de.cses.client.Util.doLogging(caught.getLocalizedMessage());
				}
			});
		} else {
			dbService.updateDepictionEntry(correspondingDepictionEntry, iconographySelector.getSelectedIconography(), new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					de.cses.client.Util.doLogging(caught.getLocalizedMessage());
				}

				@Override
				public void onSuccess(Boolean updateSucessful) {
//					updateEntry(correspondingDepictionEntry);
					if (close) {
						closeEditor(correspondingDepictionEntry);
					}
				}
			});
		}
	}

}