package de.cses.client.ornamentic;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.ListField;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import de.cses.client.DatabaseService;
import de.cses.client.DatabaseServiceAsync;
import de.cses.client.Util;
import de.cses.client.images.ImageSelector;
import de.cses.client.images.ImageSelectorListener;
import de.cses.client.ui.AbstractEditor;
import de.cses.client.user.UserLogin;
import de.cses.shared.CaveEntry;
import de.cses.shared.ImageEntry;
import de.cses.shared.InnerSecondaryPatternsEntry;
import de.cses.shared.OrnamentCaveRelation;
import de.cses.shared.OrnamentClassEntry;
import de.cses.shared.OrnamentComponentsEntry;
import de.cses.shared.OrnamentEntry;

public  class OrnamenticEditor extends AbstractEditor implements ImageSelectorListener {
	
	private final DatabaseServiceAsync dbService = GWT.create(DatabaseService.class);
	FramedPanel header;
	private VBoxLayoutContainer widget;
	FramedPanel cavesContentPanel;
	private OrnamentCaveRelationProperties ornamentCaveRelationProps;
	private ListStore<OrnamentCaveRelation> caveOrnamentRelationList;
	private ListView<OrnamentCaveRelation, String> cavesList;
	protected PopupPanel imageSelectionDialog;
	protected ImageSelector imageSelector;
	private ListView<ImageEntry, ImageEntry> imageListView;
	private ListStore<ImageEntry> imageEntryList;
	private ListStore<OrnamentClassEntry> ornamentClassEntryList;
	private ImageProperties imgProperties;
	private OrnamentEntry ornamentEntry = null;;
	private ComboBox<OrnamentClassEntry> ornamentClassComboBox;
	private OrnamentClassProperties ornamentClassProps;
	private InnerSecondaryPatternsProperties innerSecondaryPatternsProps;
	private ListStore<InnerSecondaryPatternsEntry> innerSecondaryPatternsEntryList;
	private ListStore<InnerSecondaryPatternsEntry> selectedinnerSecondaryPatternsEntryList;
	private ListStore<OrnamentComponentsEntry> ornamentComponents;
	private ListStore<OrnamentComponentsEntry> selectedOrnamentComponents;
	private OrnamentComponentsProperties ornamentComponentsProps;
	private TextField ornamentCodeTextField;
	public static OrnamentCaveRelationEditor ornamentCaveRelationEditor;
	public static WallOrnamentCaveRelationEditor wallOrnamentCaveRelationEditor;
	public static OrnamenticEditor ornamenticEditor;
	

	@Override
	public Widget asWidget() {
		if (widget == null) {
			BoxLayoutData flex = new BoxLayoutData();
			flex.setFlex(1);
			widget = new VBoxLayoutContainer();
			widget.add(createForm(), flex);
		}
		return widget;
	}

	public OrnamenticEditor(OrnamentEntry ornamentEntry) {
		this.ornamentEntry = ornamentEntry;
	}

	public Widget createForm() {
		Util.doLogging("Create form von ornamenticeditor gestartet");
		ornamentCaveRelationEditor = new OrnamentCaveRelationEditor();
		 wallOrnamentCaveRelationEditor = new WallOrnamentCaveRelationEditor();
		 ornamenticEditor = this;
		HorizontalLayoutContainer horizontBackground = new HorizontalLayoutContainer();
		VerticalLayoutContainer verticalgeneral2Background = new VerticalLayoutContainer();

		VerticalLayoutContainer verticalgeneral3Background = new VerticalLayoutContainer();

		imgProperties = GWT.create(ImageProperties.class);
		ornamentComponentsProps = GWT.create(OrnamentComponentsProperties.class);
		ornamentClassProps = GWT.create(OrnamentClassProperties.class);
		innerSecondaryPatternsProps = GWT.create(InnerSecondaryPatternsProperties.class);
		ornamentComponentsProps = GWT.create(OrnamentComponentsProperties.class);
		imageEntryList = new ListStore<ImageEntry>(imgProperties.imageID());
		ornamentCaveRelationProps = GWT.create(OrnamentCaveRelationProperties.class);
		ornamentClassEntryList = new ListStore<OrnamentClassEntry>(ornamentClassProps.ornamentClassID());
		innerSecondaryPatternsEntryList = new ListStore<InnerSecondaryPatternsEntry>(innerSecondaryPatternsProps.innerSecondaryPatternsID());
		selectedinnerSecondaryPatternsEntryList = new ListStore<InnerSecondaryPatternsEntry>(innerSecondaryPatternsProps.innerSecondaryPatternsID());

		selectedOrnamentComponents = new ListStore<OrnamentComponentsEntry>(ornamentComponentsProps.ornamentComponentsID());
		ornamentComponents = new ListStore<OrnamentComponentsEntry>(ornamentComponentsProps.ornamentComponentsID());

		dbService.getOrnamentComponents(new AsyncCallback<ArrayList<OrnamentComponentsEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(ArrayList<OrnamentComponentsEntry> result) {

				ornamentComponents.clear();
				selectedOrnamentComponents.clear();
				if (ornamentEntry != null) {
					for (OrnamentComponentsEntry pe : result) {
						int count = 0;
						for (OrnamentComponentsEntry oe : ornamentEntry.getOrnamentComponents()) {
							if (pe.getOrnamentComponentsID() != oe.getOrnamentComponentsID()) {
								count++;
							}
							if (count == ornamentEntry.getOrnamentComponents().size()) {
								ornamentComponents.add(pe);
							}
						}
					}
					for (OrnamentComponentsEntry oe : ornamentEntry.getOrnamentComponents()) {
						selectedOrnamentComponents.add(oe);
					}
					if (ornamentEntry.getOrnamentComponents().size() == 0) {
						for (OrnamentComponentsEntry nu : result) {
							ornamentComponents.add(nu);
						}
					}
				} else {
					for (OrnamentComponentsEntry pe : result) {
						ornamentComponents.add(pe);
					}
				}

			}
		});

		dbService.getInnerSecondaryPatterns(new AsyncCallback<ArrayList<InnerSecondaryPatternsEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<InnerSecondaryPatternsEntry> result) {

				innerSecondaryPatternsEntryList.clear();
				selectedinnerSecondaryPatternsEntryList.clear();
				if (ornamentEntry != null) {
					for (InnerSecondaryPatternsEntry pe : result) {
						int count = 0;
						for (InnerSecondaryPatternsEntry oe : ornamentEntry.getInnerSecondaryPatterns()) {
							if (pe.getInnerSecondaryPatternsID() != oe.getInnerSecondaryPatternsID()) {
								count++;
							}
							if (count == ornamentEntry.getInnerSecondaryPatterns().size()) {
								innerSecondaryPatternsEntryList.add(pe);
							}
						}
					}
					for (InnerSecondaryPatternsEntry oe : ornamentEntry.getInnerSecondaryPatterns()) {
						selectedinnerSecondaryPatternsEntryList.add(oe);
					}
					if (ornamentEntry.getInnerSecondaryPatterns().size() == 0) {
						for (InnerSecondaryPatternsEntry nu : result) {
							innerSecondaryPatternsEntryList.add(nu);
						}
					}
				} else {
					for (InnerSecondaryPatternsEntry pe : result) {
						innerSecondaryPatternsEntryList.add(pe);
					}
				}

			}
		});

		dbService.getOrnamentClass(new AsyncCallback<ArrayList<OrnamentClassEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<OrnamentClassEntry> result) {
				ornamentClassEntryList.clear();
				for (OrnamentClassEntry pe : result) {
					ornamentClassEntryList.add(pe);
				}
				if (ornamentEntry != null) {
					ornamentClassComboBox.setValue(ornamentClassEntryList.findModelWithKey(Integer.toString(ornamentEntry.getOrnamentClass())));
				}
			}
		});

		TabPanel tabpanel = new TabPanel();
		tabpanel.setWidth(620);
		tabpanel.setHeight(600);

		VerticalLayoutContainer panel = new VerticalLayoutContainer();
		VerticalLayoutContainer panel2 = new VerticalLayoutContainer();

		ornamentCodeTextField = new TextField();
		ornamentCodeTextField.setAllowBlank(false);
		header = new FramedPanel();
		header.setHeading("Ornament Code");
		header.add(ornamentCodeTextField);
		panel.add(header, new VerticalLayoutData(1.0, .125));
		if (ornamentEntry != null) {
			ornamentCodeTextField.setValue(ornamentEntry.getCode());
		}

		header = new FramedPanel();
		header.setHeading("Description");
		TextArea discription = new TextArea();
		panel.add(header, new VerticalLayoutData(1.0, .3));
		header.add(discription);
		discription.setAllowBlank(true);
		if (ornamentEntry != null) {
			discription.setValue(ornamentEntry.getDescription());
		}

		TextArea remarks = new TextArea();
		remarks.setAllowBlank(true);
		header = new FramedPanel();
		header.setHeading("Remarks");
		header.add(remarks);
		panel.add(header, new VerticalLayoutData(1.0, .3));
		if (ornamentEntry != null) {
			remarks.setValue(ornamentEntry.getRemarks());
		}

		TextArea interpretation = new TextArea();
		interpretation.setAllowBlank(true);
		header = new FramedPanel();
		header.setWidth(300);
		header.setHeading("Interpretation");
		header.add(interpretation);
		verticalgeneral2Background.add(header, new VerticalLayoutData(1.0, .3));
		if (ornamentEntry != null) {
			interpretation.setValue(ornamentEntry.getInterpretation());
		}

		ornamentClassComboBox = new ComboBox<OrnamentClassEntry>(ornamentClassEntryList, ornamentClassProps.name(),
				new AbstractSafeHtmlRenderer<OrnamentClassEntry>() {

					@Override
					public SafeHtml render(OrnamentClassEntry item) {
						final OrnamentClassViewTemplates pvTemplates = GWT.create(OrnamentClassViewTemplates.class);
						return pvTemplates.ornamentClass(item.getName());
					}
				});

		header = new FramedPanel();
		header.setHeading("Ornament Class");
		ornamentClassComboBox.setTriggerAction(TriggerAction.ALL);
		header.add(ornamentClassComboBox);
		panel.add(header, new VerticalLayoutData(1.0, .125));

		ToolButton addOrnamentClassButton = new ToolButton(ToolButton.PLUS);

		FramedPanel ornamentClassFramedPanel = new FramedPanel();
		ornamentClassFramedPanel.setHeading("New Ornament Class");

		ToolButton saveOrnamentClass = new ToolButton(ToolButton.SAVE);
		ornamentClassFramedPanel.add(saveOrnamentClass);

		ToolButton cancelOrnamentClass = new ToolButton(ToolButton.CLOSE);

		ornamentClassFramedPanel.addTool(cancelOrnamentClass);
		ornamentClassFramedPanel.addTool(saveOrnamentClass);

		HorizontalLayoutContainer newOrnamentClassLayoutPanel = new HorizontalLayoutContainer();
		TextField newOrnamentClassTextField = new TextField();
		newOrnamentClassLayoutPanel.add(newOrnamentClassTextField);
		ornamentClassFramedPanel.add(newOrnamentClassLayoutPanel);
		addOrnamentClassButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				PopupPanel newOrnamentClassPopup = new PopupPanel();
				newOrnamentClassPopup.add(ornamentClassFramedPanel);
				ornamentClassFramedPanel.setSize("150", "80");
				newOrnamentClassPopup.center();
				cancelOrnamentClass.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						newOrnamentClassPopup.hide();
					}
				});
				saveOrnamentClass.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						OrnamentClassEntry entry = new OrnamentClassEntry();
						entry.setName(newOrnamentClassTextField.getText());
						dbService.addOrnamentClass(entry, new AsyncCallback<OrnamentClassEntry>() {

							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							@Override
							public void onSuccess(OrnamentClassEntry result) {
								Util.doLogging(this.getClass().getName() + " added " + result.getName());
								ornamentClassEntryList.add(result);
								newOrnamentClassPopup.hide();
							}
						});
						newOrnamentClassPopup.hide();
					}
				});

			}
		});
		header.addTool(addOrnamentClassButton);
		
		ToolButton renameOrnamentClassButton = new ToolButton(ToolButton.REFRESH);
		
		FramedPanel renameornamentClassFramedPanel = new FramedPanel();
		renameornamentClassFramedPanel.setHeading("New Ornament Class");

		ToolButton saveRenameOrnamentClass = new ToolButton(ToolButton.SAVE);
		renameornamentClassFramedPanel.add(saveRenameOrnamentClass);

		ToolButton cancelRenameOrnamentClass = new ToolButton(ToolButton.CLOSE);

		renameornamentClassFramedPanel.addTool(cancelRenameOrnamentClass);
		renameornamentClassFramedPanel.addTool(saveRenameOrnamentClass);

		HorizontalLayoutContainer renameOrnamentClassLayoutPanel = new HorizontalLayoutContainer();
		TextField renameOrnamentClassTextField = new TextField();
		renameOrnamentClassLayoutPanel.add(renameOrnamentClassTextField);
		renameornamentClassFramedPanel.add(renameOrnamentClassLayoutPanel);
		
		renameOrnamentClassButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if(ornamentClassComboBox.getValue() == null) {
					Window.alert("Please select an item to rename");
				}
				else {
				PopupPanel renameOrnamentClassPopup = new PopupPanel();
				renameOrnamentClassPopup.add(renameornamentClassFramedPanel);
				renameornamentClassFramedPanel.setSize("150", "80");
				renameOrnamentClassPopup.center();
				renameOrnamentClassTextField.setText(ornamentClassComboBox.getValue().getName());
				cancelRenameOrnamentClass.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						renameOrnamentClassPopup.hide();
					}
				});
				saveRenameOrnamentClass.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						
						OrnamentClassEntry entry = 	ornamentClassComboBox.getValue();
						entry.setName(renameOrnamentClassTextField.getText());
						dbService.renameOrnamentClass(entry, new AsyncCallback<OrnamentClassEntry>() {

							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							@Override
							public void onSuccess(OrnamentClassEntry result) {
								Util.doLogging(this.getClass().getName() + " renamed " + result.getName());
								renameOrnamentClassPopup.hide();
							}
						});
						renameOrnamentClassPopup.hide();
					}
				});

			}
		}});
		
		header.addTool(renameOrnamentClassButton);


		final TextArea references = new TextArea();
		references.setAllowBlank(true);
		header = new FramedPanel();
		header.setHeading("References");
		header.add(references);
		verticalgeneral2Background.add(header, new VerticalLayoutData(1.0, .3));
		if (ornamentEntry != null) {
			references.setText(ornamentEntry.getReferences());
		}


		
		ToolButton addCaveTool = new ToolButton(ToolButton.PLUS);
		

		VerticalPanel cavesPanel = new VerticalPanel();
		header = new FramedPanel();
		header.setHeading("Cave");

		header.add(cavesPanel);
		panel2.add(header, new VerticalLayoutData(1.0, 1.0));

		ClickHandler addCaveClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
			ornamentCaveRelationEditor.show();
			}
		};

		addCaveTool.addHandler(addCaveClickHandler, ClickEvent.getType());

		cavesContentPanel = new FramedPanel();
		cavesPanel.add(cavesContentPanel);
		caveOrnamentRelationList = new ListStore<OrnamentCaveRelation>(ornamentCaveRelationProps.ornamentCaveRelationID());

		cavesList = new ListView<OrnamentCaveRelation, String>(caveOrnamentRelationList, ornamentCaveRelationProps.name());
		cavesList.setAllowTextSelection(true);

		if (ornamentEntry != null) {
			for (int i = 0; i < ornamentEntry.getCavesRelations().size(); i++) {
				caveOrnamentRelationList.add(ornamentEntry.getCavesRelations().get(i));
			}
		}

		cavesContentPanel.setHeading("Added caves");
		cavesContentPanel.add(cavesList);
		
		

		ToolButton edit = new ToolButton(ToolButton.REFRESH);
		ToolButton delete = new ToolButton(ToolButton.CLOSE);

		cavesContentPanel.addTool(addCaveTool);
		cavesContentPanel.addTool(edit);
		cavesContentPanel.addTool(delete);

		ClickHandler deleteClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				caveOrnamentRelationList.remove(cavesList.getSelectionModel().getSelectedItem());

			}
		};
		delete.addHandler(deleteClickHandler, ClickEvent.getType());

		ClickHandler editClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ornamentCaveRelationEditor.show(cavesList.getSelectionModel().getSelectedItem());
			}
		};
		edit.addHandler(editClickHandler, ClickEvent.getType());


		ClickHandler saveClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!ornamentCodeTextField.validate()) {
					Util.showWarning("Missing information", "Please insert Ornamentation Code!");
					return;
				}
				
				if (ornamentEntry == null) {
					ornamentEntry = new OrnamentEntry();
				}

				ArrayList<OrnamentCaveRelation> corList = new ArrayList<OrnamentCaveRelation>();
				for (int i = 0; i < caveOrnamentRelationList.size(); i++) {
					corList.add(caveOrnamentRelationList.get(i));
				}
				ornamentEntry.setCavesRelations(corList);

				ArrayList<ImageEntry> ieList = new ArrayList<ImageEntry>();
				for (int i = 0; i < imageEntryList.size(); i++) {
					ieList.add(imageEntryList.get(i));
				}
				ornamentEntry.setImages(ieList);

				ornamentEntry.setCode(ornamentCodeTextField.getText());
				ornamentEntry.setDescription(discription.getText());
				ornamentEntry.setRemarks(remarks.getText());
				ornamentEntry.setInterpretation(interpretation.getText());
				ornamentEntry.setReferences(references.getText());
				if (ornamentClassComboBox.getValue() == null) {
					ornamentEntry.setOrnamentClass(0);
				} else {
					Util.doLogging("ID gesetzt");
					ornamentEntry.setOrnamentClass(ornamentClassComboBox.getValue().getOrnamentClassID());
				}

				ArrayList<InnerSecondaryPatternsEntry> ispeList = new ArrayList<InnerSecondaryPatternsEntry>();
				for (int i = 0; i < selectedinnerSecondaryPatternsEntryList.size(); i++) {
					ispeList.add(selectedinnerSecondaryPatternsEntryList.get(i));
				}
				ornamentEntry.setInnerSecondaryPatterns(ispeList);

				ArrayList<OrnamentComponentsEntry> oceList = new ArrayList<OrnamentComponentsEntry>();
				for (int i = 0; i < selectedOrnamentComponents.size(); i++) {
					oceList.add(selectedOrnamentComponents.get(i));
				}
				ornamentEntry.setOrnamentComponents(oceList);

				// send ornament to server
				if (ornamentEntry.getOrnamentID() == 0) {
					dbService.saveOrnamentEntry(ornamentEntry, new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							Util.showWarning("Saving failed", caught.getMessage());
						}

						@Override
						public void onSuccess(Integer result) {
							if (result > 0) {
								Util.doLogging(this.getClass().getName() + " saving sucessful");
								ornamentEntry.setOrnamentID(result);
//							updateEntry(ornamentEntry);
								closeEditor(ornamentEntry);
							} else {
								Util.showWarning("Saving failed", "ornamentID == 0");
							}
						}
					});
				} else {
					dbService.updateOrnamentEntry(ornamentEntry, new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							Util.showWarning("Update failed", caught.getMessage());
						}

						@Override
						public void onSuccess(Boolean result) {
							closeEditor(ornamentEntry);
						}
					});
				}

			}
		};
		

		FramedPanel framedpanelornamentic = new FramedPanel();
		

		ClickHandler cancelHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				closeEditor(null);
			}

		};
		



		horizontBackground.add(panel, new HorizontalLayoutData(.5, 1.0));
		horizontBackground.add(panel2, new HorizontalLayoutData(.5, 1.0));
		framedpanelornamentic.setHeading("1. General");
		framedpanelornamentic.add(horizontBackground);

		tabpanel.add(framedpanelornamentic, "1. General");

		FramedPanel general2FramedPanel = new FramedPanel();
		general2FramedPanel.setHeading("2. General");
		tabpanel.add(general2FramedPanel, "2. General");
		general2FramedPanel.add(verticalgeneral2Background);

		FramedPanel general3FramedPanel = new FramedPanel();
		general3FramedPanel.setHeading("3. General");
		tabpanel.add(general3FramedPanel, "3. General");
		general3FramedPanel.add(verticalgeneral3Background);

		HorizontalLayoutContainer ornamentComponentsHorizontalPanel = new HorizontalLayoutContainer();

		ListView<OrnamentComponentsEntry, String> ornamentComponentView = new ListView<OrnamentComponentsEntry, String>(ornamentComponents,
				ornamentComponentsProps.name());
		ListView<OrnamentComponentsEntry, String> selectedOrnamentComponentView = new ListView<OrnamentComponentsEntry, String>(
				selectedOrnamentComponents, ornamentComponentsProps.name());
		ornamentComponentsHorizontalPanel.add(ornamentComponentView, new HorizontalLayoutData(.5, 1.0, new Margins(1)));
		ornamentComponentsHorizontalPanel.add(selectedOrnamentComponentView, new HorizontalLayoutData(.5, 1.0, new Margins(1)));

		new ListViewDragSource<OrnamentComponentsEntry>(ornamentComponentView).setGroup("Components");
		new ListViewDragSource<OrnamentComponentsEntry>(selectedOrnamentComponentView).setGroup("Components");

		new ListViewDropTarget<OrnamentComponentsEntry>(selectedOrnamentComponentView).setGroup("Components");
		new ListViewDropTarget<OrnamentComponentsEntry>(ornamentComponentView).setGroup("Components");

		header = new FramedPanel();
		header.setHeading("Select Ornament Components");
		if (ornamentEntry != null) {
			for (int i = 0; i < ornamentEntry.getOrnamentComponents().size(); i++) {
				selectedOrnamentComponents.add(ornamentEntry.getOrnamentComponents().get(i));

			}
		}
		header.add(ornamentComponentsHorizontalPanel);

		verticalgeneral3Background.add(header, new VerticalLayoutData(1.0, .3));

		ToolButton addComponentButton = new ToolButton(ToolButton.PLUS);
		header.addTool(addComponentButton);


		addComponentButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				PopupPanel newComponentPopup = new PopupPanel();
				FramedPanel componentFramedPanel = new FramedPanel();
				componentFramedPanel.setHeading("New Component");

				ToolButton saveComponent = new ToolButton(ToolButton.SAVE);
				componentFramedPanel.addTool(saveComponent);

				ToolButton cancelComponent = new ToolButton(ToolButton.CLOSE);

				componentFramedPanel.addTool(cancelComponent);

				HorizontalLayoutContainer newComponentLayoutPanel = new HorizontalLayoutContainer();
				TextField newComponentTextField = new TextField();
				newComponentLayoutPanel.add(newComponentTextField);
				componentFramedPanel.add(newComponentLayoutPanel);
				
				newComponentPopup.add(componentFramedPanel);
				newComponentPopup.setSize("150px", "80px");
				newComponentPopup.center();

				cancelComponent.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						newComponentPopup.hide();
					}
				});
				saveComponent.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						OrnamentComponentsEntry entry = new OrnamentComponentsEntry();
						entry.setName(newComponentTextField.getText());

						dbService.addOrnamentComponent(entry, new AsyncCallback<OrnamentComponentsEntry>() {

							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							@Override
							public void onSuccess(OrnamentComponentsEntry result) {
								ornamentComponents.add(entry);
								Util.doLogging(this.getClass().getName() + " saving sucessful");
								newComponentPopup.hide();
							}
						});
					}
				});
			}
		});

		ToolButton renameComponentButton = new ToolButton(ToolButton.REFRESH);
		header.addTool(renameComponentButton);
		renameComponentButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if(ornamentComponentView.getSelectionModel().getSelectedItem() == null) {
					Window.alert("Please select an item to rename");
					
				}
				else {
				PopupPanel renameComponentPopup = new PopupPanel();
				FramedPanel renamecomponentFramedPanel = new FramedPanel();
				renamecomponentFramedPanel.setHeading("New Component");

				ToolButton saveRenameComponent = new ToolButton(ToolButton.SAVE);
				renamecomponentFramedPanel.addTool(saveRenameComponent);

				ToolButton cancelRenameComponent = new ToolButton(ToolButton.CLOSE);

				renamecomponentFramedPanel.addTool(cancelRenameComponent);

				HorizontalLayoutContainer renameComponentLayoutPanel = new HorizontalLayoutContainer();
				TextField renameComponentTextField = new TextField();
				renameComponentLayoutPanel.add(renameComponentTextField);
				renamecomponentFramedPanel.add(renameComponentLayoutPanel);
				
				renameComponentPopup.add(renamecomponentFramedPanel);
				renameComponentPopup.setSize("150px", "80px");
				renameComponentPopup.center();
				
				renameComponentTextField.setText(ornamentComponentView.getSelectionModel().getSelectedItem().getName());

				cancelRenameComponent.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						renameComponentPopup.hide();
					}
				});
				saveRenameComponent.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						OrnamentComponentsEntry entry = new OrnamentComponentsEntry();
						entry.setName(renameComponentTextField.getText());
						

						dbService.renameOrnamentComponents(entry, new AsyncCallback<OrnamentComponentsEntry>() {

							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							@Override
							public void onSuccess(OrnamentComponentsEntry result) {
								Util.doLogging(this.getClass().getName() + " renaming sucessful");
								renameComponentPopup.hide();
							}
						});
					}
				});
			}
		}});
		
		
		HorizontalLayoutContainer innerSecondaryPatternsHorizontalPanel = new HorizontalLayoutContainer();

		ListView<InnerSecondaryPatternsEntry, String> innerSecondaryPatternsView = new ListView<InnerSecondaryPatternsEntry, String>(
				innerSecondaryPatternsEntryList, innerSecondaryPatternsProps.name());
		ListView<InnerSecondaryPatternsEntry, String> selectedinnerSecondaryPatternsView = new ListView<InnerSecondaryPatternsEntry, String>(
				selectedinnerSecondaryPatternsEntryList, innerSecondaryPatternsProps.name());
		innerSecondaryPatternsHorizontalPanel.add(innerSecondaryPatternsView, new HorizontalLayoutData(.5, 1.0, new Margins(1)));
		innerSecondaryPatternsHorizontalPanel.add(selectedinnerSecondaryPatternsView, new HorizontalLayoutData(.5, 1.0, new Margins(1)));

		new ListViewDragSource<InnerSecondaryPatternsEntry>(innerSecondaryPatternsView).setGroup("innersec");
		new ListViewDragSource<InnerSecondaryPatternsEntry>(selectedinnerSecondaryPatternsView).setGroup("innersec");

		new ListViewDropTarget<InnerSecondaryPatternsEntry>(selectedinnerSecondaryPatternsView).setGroup("innersec");
		new ListViewDropTarget<InnerSecondaryPatternsEntry>(innerSecondaryPatternsView).setGroup("innersec");

		header = new FramedPanel();
		header.setHeading("Select inner Secondary Patterns");
		ToolButton addInnerSecondaryPatternsButton = new ToolButton(ToolButton.PLUS);

		
		addInnerSecondaryPatternsButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				PopupPanel newInnerSecondaryPatternPopup = new PopupPanel();
				FramedPanel innersecFramedPanel = new FramedPanel();
				innersecFramedPanel.setHeading("New Inner Secondary Pattern");

				ToolButton saveInnerSec = new ToolButton(ToolButton.SAVE);
				innersecFramedPanel.add(saveInnerSec);

				ToolButton cancelInnerSec = new ToolButton(ToolButton.CLOSE);

				innersecFramedPanel.addTool(cancelInnerSec);
				innersecFramedPanel.addTool(saveInnerSec);

				HorizontalLayoutContainer newinnersecLayoutPanel = new HorizontalLayoutContainer();
				TextField newinnersecTextField = new TextField();
				newinnersecLayoutPanel.add(newinnersecTextField);
				innersecFramedPanel.add(newinnersecLayoutPanel);
				newInnerSecondaryPatternPopup.add(innersecFramedPanel);
				innersecFramedPanel.setSize("150", "80");
				newInnerSecondaryPatternPopup.center();
				cancelInnerSec.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						newInnerSecondaryPatternPopup.hide();
					}
				});
				saveInnerSec.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						InnerSecondaryPatternsEntry entry = new InnerSecondaryPatternsEntry();
						entry.setName(newinnersecTextField.getText());
						dbService.addInnerSecondaryPatterns(entry, new AsyncCallback<InnerSecondaryPatternsEntry>() {

							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							@Override
							public void onSuccess(InnerSecondaryPatternsEntry result) {
								Util.doLogging(this.getClass().getName() + "saving sucessful");
								innerSecondaryPatternsEntryList.add(result);
								newInnerSecondaryPatternPopup.hide();
							}
						});
						newInnerSecondaryPatternPopup.hide();
					}
				});
			}
		});

		if (ornamentEntry != null) {
			for (int i = 0; i < ornamentEntry.getInnerSecondaryPatterns().size(); i++) {
				selectedinnerSecondaryPatternsEntryList.add(ornamentEntry.getInnerSecondaryPatterns().get(i));

			}
		}
		header.add(innerSecondaryPatternsHorizontalPanel);
		header.addTool(addInnerSecondaryPatternsButton);

		verticalgeneral3Background.add(header, new VerticalLayoutData(1.0, .3));

		FramedPanel imagesFramedPanel = new FramedPanel();
		imagesFramedPanel.setHeading("Images");

		imageListView = new ListView<ImageEntry, ImageEntry>(imageEntryList, new IdentityValueProvider<ImageEntry>() {
			@Override
			public void setValue(ImageEntry object, ImageEntry value) {
			}
		});

		if (ornamentEntry != null) {
			for (int i = 0; i < ornamentEntry.getImages().size(); i++) {
				imageEntryList.add(ornamentEntry.getImages().get(i));
			}
		}

		imageListView.setCell(new SimpleSafeHtmlCell<ImageEntry>(new AbstractSafeHtmlRenderer<ImageEntry>() {
			final ImageViewTemplates imageViewTemplates = GWT.create(ImageViewTemplates.class);

			public SafeHtml render(ImageEntry item) {
				SafeUri imageUri = UriUtils.fromString(
						"resource?imageID=" + item.getImageID() + "&thumb=150" + UserLogin.getInstance().getUsernameSessionIDParameterForUri());
				return imageViewTemplates.image(imageUri, item.getTitle());
			}
		}));

		imageListView.setSize("500", "290");
		ListField<ImageEntry, ImageEntry> lf = new ListField<ImageEntry, ImageEntry>(imageListView);
		lf.setSize("500", "300");

		imageSelector = new ImageSelector(this);
		TextButton addImageButton = new TextButton("Select Image");
		addImageButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				imageSelectionDialog = new PopupPanel();
				imageSelectionDialog.add(imageSelector);
				imageSelectionDialog.setModal(true);
				imageSelectionDialog.center();
			}
		});
		TextButton removeImageButton = new TextButton("Remove Image");
		removeImageButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				imageEntryList.remove(imageListView.getSelectionModel().getSelectedItem());
			}
		});

		VerticalPanel imagesVerticalPanel = new VerticalPanel();
		imagesVerticalPanel.add(lf);
		HorizontalPanel hbp = new HorizontalPanel();
		hbp.add(addImageButton);
		hbp.add(removeImageButton);
		imagesVerticalPanel.add(hbp);
		tabpanel.add(imagesFramedPanel, "Images");
		imagesFramedPanel.add(imagesVerticalPanel);
		tabpanel.setTabScroll(true);

		ToolButton closeButton = new ToolButton(ToolButton.CLOSE);
		ToolButton saveButton = new ToolButton(ToolButton.SAVE);
		closeButton.addHandler(cancelHandler, ClickEvent.getType());
		saveButton.addHandler(saveClickHandler, ClickEvent.getType());
		
		
		FramedPanel backgroundPanel = new FramedPanel();
		HorizontalLayoutContainer horiPanel = new HorizontalLayoutContainer();
		horiPanel.setSize("650", "600");
		backgroundPanel.add(horiPanel);
		horiPanel.add(tabpanel);
		backgroundPanel.setHeading("Ornamentic Editor");
		backgroundPanel.addTool(saveButton);
		backgroundPanel.addTool(closeButton);
		
		return backgroundPanel;

	}

	interface ImageProperties extends PropertyAccess<ImageEntry> {
		ModelKeyProvider<ImageEntry> imageID();

		LabelProvider<ImageEntry> title();
	}

	interface ImageViewTemplates extends XTemplates {
		@XTemplate("<img align=\"center\" margin=\"20\" src=\"{imageUri}\"><br> {title}")
		SafeHtml image(SafeUri imageUri, String title);

	}

	interface OrnamentClassViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml ornamentClass(String name);
	}

	interface OrnamentClassProperties extends PropertyAccess<OrnamentClassEntry> {
		ModelKeyProvider<OrnamentClassEntry> ornamentClassID();

		LabelProvider<OrnamentClassEntry> name();
	}

	interface OrnamentComponentsProperties extends PropertyAccess<OrnamentComponentsEntry> {
		ModelKeyProvider<OrnamentComponentsEntry> ornamentComponentsID();

		ValueProvider<OrnamentComponentsEntry, String> name();
	}

	interface InnerSecondaryPatternsProperties extends PropertyAccess<InnerSecondaryPatternsEntry> {
		ModelKeyProvider<InnerSecondaryPatternsEntry> innerSecondaryPatternsID();

		ValueProvider<InnerSecondaryPatternsEntry, String> name();
	}

	interface OrnamentCaveRelationProperties extends PropertyAccess<CaveEntry> {
		ModelKeyProvider<OrnamentCaveRelation> ornamentCaveRelationID();

		ValueProvider<OrnamentCaveRelation, String> name();
	}

	public ListStore<OrnamentCaveRelation> getCaveOrnamentRelationList() {
		return caveOrnamentRelationList;
	}

	public void setCaveOrnamentRelationList(ListStore<OrnamentCaveRelation> caveOrnamentRelationList) {
		this.caveOrnamentRelationList = caveOrnamentRelationList;
	}

	public ListView<OrnamentCaveRelation, String> getCavesList() {
		return cavesList;
	}

	public void setCavesList(ListView<OrnamentCaveRelation, String> cavesList) {
		this.cavesList = cavesList;
	}


	@Override
	public void imageSelected(ImageEntry entry) {
		if (entry.getImageID() != 0) {
			imageEntryList.add(entry);
		}
		imageSelectionDialog.hide();
	}

}
