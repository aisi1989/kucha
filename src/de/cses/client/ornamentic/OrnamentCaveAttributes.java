package de.cses.client.ornamentic;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import de.cses.client.DatabaseService;
import de.cses.client.DatabaseServiceAsync;
import de.cses.shared.CaveEntry;
import de.cses.shared.DistrictEntry;
import de.cses.shared.OrnamentCaveRelation;
import de.cses.shared.OrnamentEntry;
import de.cses.shared.OrnamentOfOtherCulturesEntry;

public class OrnamentCaveAttributes extends PopupPanel{
	
	private ComboBox<CaveEntry> caveEntryComboBox;
	private ListStore<CaveEntry> caveEntryList;
	private CaveEntryProperties caveEntryProps;
	private ListStore<OrnamentOfOtherCulturesEntry> ornamentOfOtherCulturesEntryList;
	private ListStore<OrnamentOfOtherCulturesEntry> similarOrnamentsofOtherCulturesListStore;
	private OrnamentOfOtherCulturesEntryProperties ornamentOfOtherCulturesEntryProps;
	private ListStore<DistrictEntry> districtEntryList;
	private DistrictEntryProperties districtEntryProps;
	private ListStore<OrnamentEntry> ornamentEntryList;
	private OrnamentEntryProperties ornamentEntryProps;
	private ComboBox<DistrictEntry> districtComboBox;
	private final DatabaseServiceAsync dbService = GWT.create(DatabaseService.class);
	private PopupPanel popup = this;
	private ListStore<OrnamentEntry>selectedSimilarOrnaments;
	private ListStore<OrnamentEntry>selectedRedlatedOrnaments;
	private Ornamentic ornamentic;


	public OrnamentCaveAttributes() {
		super(false);
		caveEntryProps = GWT.create(CaveEntryProperties.class);
		ornamentEntryProps = GWT.create(OrnamentEntryProperties.class);
		districtEntryProps = GWT.create(DistrictEntryProperties.class);
		caveEntryList = new ListStore<CaveEntry>(caveEntryProps.caveID());
		districtEntryList = new ListStore<DistrictEntry>(districtEntryProps.districtID());
		ornamentEntryList = new ListStore<OrnamentEntry>(ornamentEntryProps.OrnamentID());
		selectedSimilarOrnaments = new ListStore<OrnamentEntry>(ornamentEntryProps.OrnamentID());
		selectedRedlatedOrnaments = new ListStore<OrnamentEntry>(ornamentEntryProps.OrnamentID());
		ornamentOfOtherCulturesEntryProps = GWT.create(OrnamentOfOtherCulturesEntryProperties.class);
		ornamentOfOtherCulturesEntryList = new ListStore<OrnamentOfOtherCulturesEntry>(ornamentOfOtherCulturesEntryProps.ornamentOfOtherCulturesID());
		similarOrnamentsofOtherCulturesListStore = new ListStore<OrnamentOfOtherCulturesEntry>(ornamentOfOtherCulturesEntryProps.ornamentOfOtherCulturesID());

		dbService.getOrnaments(new AsyncCallback<ArrayList<OrnamentEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<OrnamentEntry> result) {
				ornamentEntryList.clear();
				for (OrnamentEntry pe : result) {
					ornamentEntryList.add(pe);
				}
			}
		});
		
		dbService.getDistricts(new AsyncCallback<ArrayList<DistrictEntry>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<DistrictEntry> result) {
				districtEntryList.clear();
				for (DistrictEntry pe : result) {
					districtEntryList.add(pe);
				}
			}
		});
		
		dbService.getOrnamentsOfOtherCultures(new AsyncCallback<ArrayList<OrnamentOfOtherCulturesEntry>>() {


			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<OrnamentOfOtherCulturesEntry> result) {
				ornamentOfOtherCulturesEntryList.clear();
				for (OrnamentOfOtherCulturesEntry pe : result) {
					ornamentOfOtherCulturesEntryList.add(pe);
				}
			}
		});
		
		
		setWidget(createForm());

		
		
		
		
		
		
		

	}
	
	
	
	
	public Widget createForm(){
		VerticalPanel ornamentCaveAttributesPanel = new VerticalPanel();
		VBoxLayoutContainer vlcCave = new VBoxLayoutContainer();

		FramedPanel cavesFrame = new FramedPanel();
		ornamentCaveAttributesPanel.add(cavesFrame);
		cavesFrame.add(vlcCave);
		cavesFrame.setHeading("Cave");
		
		districtComboBox = new ComboBox<DistrictEntry>(districtEntryList, districtEntryProps.name(),
				new AbstractSafeHtmlRenderer<DistrictEntry>() {

					@Override
					public SafeHtml render(DistrictEntry item) {
						final DistrictViewTemplates pvTemplates = GWT.create(DistrictViewTemplates.class);
						return pvTemplates.district(item.getName());
					}
				});
		
		vlcCave.add(new FieldLabel(districtComboBox, "Select District"));
		
		SelectionHandler<DistrictEntry> districtSelectionHandler = new SelectionHandler<DistrictEntry>(){

			@Override
			public void onSelection(SelectionEvent<DistrictEntry> event) {
				int p = event.getSelectedItem().getDistrictID();
				caveEntryList.clear();
				
				dbService.getCavesbyDistrictID(p,new AsyncCallback<ArrayList<CaveEntry>>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(ArrayList<CaveEntry> result) {
						for (CaveEntry pe : result) {
							caveEntryList.add(pe);
						}
					}
				});
				
			}
			
		};
		
		districtComboBox.addSelectionHandler(districtSelectionHandler);
		
		
		caveEntryComboBox = new ComboBox<CaveEntry>(caveEntryList, caveEntryProps.name(),
				new AbstractSafeHtmlRenderer<CaveEntry>() {

					@Override
					public SafeHtml render(CaveEntry item) {
						final CaveViewTemplates pvTemplates = GWT.create(CaveViewTemplates.class);
						return pvTemplates.caver(item.getName());
					}
				});
		
		vlcCave.add(new FieldLabel(caveEntryComboBox, "Select Cave"));
	  final TextField caveType = new TextField();
	  caveType.setAllowBlank(false);
	  caveType.setEnabled(false);
	  vlcCave.add(new FieldLabel(caveType, "Cave type"));
	  
	  
		SelectionHandler<CaveEntry> caveSelectionHandler = new SelectionHandler<CaveEntry>(){

			@Override
			public void onSelection(SelectionEvent<CaveEntry> event) {
				String p = event.getSelectedItem().getName();
				caveType.setText(p);
			}
			
		};
		Button addWalls = new Button("Select Walls");
		vlcCave.add(new FieldLabel(addWalls, "Select Walls"));
		
		caveEntryComboBox.addSelectionHandler(caveSelectionHandler);
		
		VBoxLayoutContainer vlcAttributes = new VBoxLayoutContainer();
		
	  final TextField style = new TextField();
	  style.setAllowBlank(true);
	  vlcAttributes.add(new FieldLabel(style, "Style in which it is used"));
	  
	  final TextField group = new TextField();
	  group.setAllowBlank(true);
	  vlcAttributes.add(new FieldLabel(group, "Group of ornaments"));
	  
	  final TextField orientation = new TextField();
	  orientation.setAllowBlank(true);
	  vlcAttributes.add(new FieldLabel(orientation, "Orientation"));
	  
	  final TextField structureOrganization = new TextField();
	  structureOrganization.setAllowBlank(true);
	  vlcAttributes.add(new FieldLabel(structureOrganization, "Structure-Organization"));
	  
	  final TextField mainTopologycalclass = new TextField();
	  mainTopologycalclass.setAllowBlank(true);
	  vlcAttributes.add(new FieldLabel(mainTopologycalclass, "Main typologycal class"));
	  
	  final TextField colours = new TextField();
	  colours.setAllowBlank(true);
	  vlcAttributes.add(new FieldLabel(colours, "Colours used"));
	  
	  FramedPanel attributes = new FramedPanel();
	  attributes.setHeading("Attributes");
	  attributes.add(vlcAttributes);
	  ornamentCaveAttributesPanel.add(attributes);

	  
	  FramedPanel occupiedPosition = new FramedPanel();
	  occupiedPosition.setHeading("Occupied Position");
	  VBoxLayoutContainer vlcOccupiedPosition = new VBoxLayoutContainer();
	  occupiedPosition.add(vlcOccupiedPosition);
	  ornamentCaveAttributesPanel.add(occupiedPosition);
	  
	  final TextField position = new TextField();
	  position.setAllowBlank(true);
	  vlcOccupiedPosition.add(new FieldLabel(position, "Position"));
	  
	  final TextField function = new TextField();
	  function.setAllowBlank(true);
	  vlcOccupiedPosition.add(new FieldLabel(function, "Function"));
	  
	  final TextField cavePart = new TextField();
	  cavePart.setAllowBlank(true);
	  vlcOccupiedPosition.add(new FieldLabel(cavePart, "Cave Part"));
	  
	  final TextField notes = new TextField();
	  notes.setAllowBlank(true);
	  vlcOccupiedPosition.add(new FieldLabel(notes, "Notes"));
	  
	  FramedPanel relationToOtherOrnaments = new FramedPanel();
	  relationToOtherOrnaments.setHeading("Similar of related elements or ornaments");
	  VBoxLayoutContainer vlcRelationToTherornaments = new VBoxLayoutContainer();
	  
	  relationToOtherOrnaments.add(vlcRelationToTherornaments);
	  
	  TextField groupOfOrnaments = new TextField();
	  groupOfOrnaments.setAllowBlank(true);
	  vlcRelationToTherornaments.add(new FieldLabel(groupOfOrnaments, "Group of Ornaments"));
	  
	  HorizontalPanel relatedOrnamentsHorizontalPanel = new HorizontalPanel();
	  relatedOrnamentsHorizontalPanel.setPixelSize(210, 210);
	  
	  ListView<OrnamentEntry, String> ornamentListViewRelated = new ListView<OrnamentEntry, String>(ornamentEntryList, ornamentEntryProps.code());
	  ornamentListViewRelated.setPixelSize(100, 100);
	  ListView<OrnamentEntry, String> selectedRelatedOrnamentsListView = new ListView<OrnamentEntry, String>(selectedRedlatedOrnaments, ornamentEntryProps.code());
	  selectedRelatedOrnamentsListView.setPixelSize(100, 100);
	  relatedOrnamentsHorizontalPanel.add(ornamentListViewRelated);
	  relatedOrnamentsHorizontalPanel.add(selectedRelatedOrnamentsListView);
	  
    new ListViewDragSource<OrnamentEntry>(ornamentListViewRelated).setGroup("relatedOrnament");
    new ListViewDragSource<OrnamentEntry>(selectedRelatedOrnamentsListView).setGroup("relatedOrnament");

    new ListViewDropTarget<OrnamentEntry>(selectedRelatedOrnamentsListView).setGroup("relatedOrnament");
    new ListViewDropTarget<OrnamentEntry>(ornamentListViewRelated).setGroup("relatedOrnament");
    
    
	  vlcRelationToTherornaments.add(new FieldLabel(relatedOrnamentsHorizontalPanel, "Select related ornaments"));
	  
	  
	  
	  HorizontalPanel similarOrnamentsHorizontalPanel = new HorizontalPanel();
	  similarOrnamentsHorizontalPanel.setPixelSize(210, 210);
	  ListView<OrnamentEntry, String> ornamentListViewSimilar = new ListView<OrnamentEntry, String>(ornamentEntryList, ornamentEntryProps.code());
	  ornamentListViewSimilar.setPixelSize(100, 100);
	  ListView<OrnamentEntry, String> selectedSimilarOrnamentsListView = new ListView<OrnamentEntry, String>(selectedSimilarOrnaments, ornamentEntryProps.code());
	  selectedSimilarOrnamentsListView.setPixelSize(100, 100);
	  similarOrnamentsHorizontalPanel.add(ornamentListViewSimilar);
	  similarOrnamentsHorizontalPanel.add(selectedSimilarOrnamentsListView);
    
    new ListViewDragSource<OrnamentEntry>(ornamentListViewSimilar).setGroup("similarOrnament");
    new ListViewDragSource<OrnamentEntry>(selectedSimilarOrnamentsListView).setGroup("similarOrnament");

    new ListViewDropTarget<OrnamentEntry>(selectedSimilarOrnamentsListView).setGroup("similarOrnament");
    new ListViewDropTarget<OrnamentEntry>(ornamentListViewSimilar).setGroup("similarOrnament");
    
	  vlcRelationToTherornaments.add(new FieldLabel(similarOrnamentsHorizontalPanel, "Select similar ornaments"));
	  
	  
	  HorizontalPanel similarElementsHorizontalPanel = new HorizontalPanel();
	  similarElementsHorizontalPanel.setPixelSize(210, 210);
	  ListView<OrnamentOfOtherCulturesEntry, String> elementsListViewSimilar = new ListView<OrnamentOfOtherCulturesEntry, String>(ornamentOfOtherCulturesEntryList, ornamentOfOtherCulturesEntryProps.name());
	  elementsListViewSimilar.setPixelSize(100, 100);
	  ListView<OrnamentOfOtherCulturesEntry, String> selectedSimilarElementsListView = new ListView<OrnamentOfOtherCulturesEntry, String>(similarOrnamentsofOtherCulturesListStore, ornamentOfOtherCulturesEntryProps.name());
	  selectedSimilarElementsListView.setPixelSize(100, 100);
	  similarElementsHorizontalPanel.add(elementsListViewSimilar);
	  similarElementsHorizontalPanel.add(selectedSimilarElementsListView);
	  

    new ListViewDragSource<OrnamentOfOtherCulturesEntry>(elementsListViewSimilar).setGroup("similarElement");
    new ListViewDragSource<OrnamentOfOtherCulturesEntry>(selectedSimilarElementsListView).setGroup("similarElement");

    new ListViewDropTarget<OrnamentOfOtherCulturesEntry>(elementsListViewSimilar).setGroup("similarElement");
    new ListViewDropTarget<OrnamentOfOtherCulturesEntry>(selectedSimilarElementsListView).setGroup("similarElement");
    
	  vlcRelationToTherornaments.add(new FieldLabel(similarElementsHorizontalPanel, "Select similar elements of other cultures"));
	 
	  TextField relatedElementsofOtherCultures = new TextField();
	  relatedElementsofOtherCultures.setPixelSize(100, 200);
	  vlcRelationToTherornaments.add(new FieldLabel(relatedElementsofOtherCultures, "Describe related elements of other cultures"));
	  
	  
	  ornamentCaveAttributesPanel.add(relationToOtherOrnaments);
	  
	  HorizontalPanel buttonsPanel = new HorizontalPanel();
	  
	  TextButton save = new TextButton("save");
	  
	  TextButton cancel = new TextButton("cancel");
	  buttonsPanel.add(save);
	  buttonsPanel.add(cancel);
	  ornamentCaveAttributesPanel.add(buttonsPanel);
	  
	  ClickHandler cancelClickHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
			popup.hide();		
			}
	  };
	  cancel.addHandler(cancelClickHandler, ClickEvent.getType());
	  
	  ClickHandler saveClickHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
			OrnamentCaveRelation ornamentCaveRelation = new OrnamentCaveRelation();
			ornamentCaveRelation.setCaveID(caveEntryComboBox.getValue().getCaveID());
			ornamentCaveRelation.setName(caveEntryComboBox.getValue().getName());
			ornamentCaveRelation.setCavepart(cavePart.getText());
			ornamentCaveRelation.setFunction(function.getText());
			ornamentCaveRelation.setColours(colours.getText());
			ornamentCaveRelation.setGroup(Integer.parseInt(group.getText()));
			ornamentCaveRelation.setOrientation(orientation.getText());
			ornamentCaveRelation.setPosition(position.getText());
			ornamentCaveRelation.setNotes(notes.getText());
			ornamentCaveRelation.setMainTopologycalClass(Integer.parseInt(mainTopologycalclass.getText()));
			ornamentCaveRelation.setStyle(Integer.parseInt(style.getText()));
			ornamentCaveRelation.setStructure(structureOrganization.getText());
			ornamentCaveRelation.setOtherCulturalOrnamentsRelationID(similarOrnamentsofOtherCulturesListStore.getAll());
			ornamentCaveRelation.setRelatedOrnamentsRelationID(selectedRedlatedOrnaments.getAll());
			ornamentCaveRelation.setSimilarOrnamentsRelationID(selectedSimilarOrnaments.getAll());
			ornamentic.getCaveOrnamentRelationList().add(ornamentCaveRelation);
			//doenst work, listview still needs more space
			ornamentic.getCavesList().refresh();
			
			popup.hide();		
			}
	  };
	  save.addHandler(saveClickHandler, ClickEvent.getType());
	  
	  
		
		
	  FramedPanel panel = new FramedPanel();
	  panel.setHeading("New Cave Relation");
	  panel.add(ornamentCaveAttributesPanel);
		return panel;
		
	}
	interface CaveEntryProperties extends PropertyAccess<CaveEntry> {
		ModelKeyProvider<CaveEntry> caveID();

		LabelProvider<CaveEntry> name();
	}
	interface CaveViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml caver(String name);
	}
	interface OrnamentEntryProperties extends PropertyAccess<OrnamentEntry> {
		ModelKeyProvider<OrnamentEntry> OrnamentID();
		 @Path("code")
	   ValueProvider<OrnamentEntry, String> code();
	}
	interface OrnamentOfOtherCulturesEntryProperties extends PropertyAccess<OrnamentOfOtherCulturesEntry> {
		ModelKeyProvider<OrnamentOfOtherCulturesEntry> ornamentOfOtherCulturesID();

		ValueProvider<OrnamentOfOtherCulturesEntry, String> name();
	}
	interface DistrictEntryProperties extends PropertyAccess<DistrictEntry> {
		ModelKeyProvider<DistrictEntry> districtID();

		LabelProvider<DistrictEntry> name();
	}
	interface OrnamentViewTemplates extends XTemplates {
		@XTemplate("<div>{code}</div>")
		SafeHtml ornament(String code);
	}
	interface DistrictViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml district(String name);
	}
	interface OrnamentOfOtherCulturesEntryViewTemplates extends XTemplates {
		@XTemplate("<div>{name}</div>")
		SafeHtml ornamentOfOtherCultures(String name);
	}




	public Ornamentic getOrnamentic() {
		return ornamentic;
	}




	public void setOrnamentic(Ornamentic ornamentic) {
		this.ornamentic = ornamentic;
	}





	
}
