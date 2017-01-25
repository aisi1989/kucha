package de.cses.client.walls;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;

import de.cses.client.DatabaseService;
import de.cses.client.DatabaseServiceAsync;
import de.cses.shared.DepictionEntry;
import de.cses.shared.ImageEntry;

import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class Walls implements IsWidget{
	private DatabaseServiceAsync dbService = GWT.create(DatabaseService.class);
	private VBoxLayoutContainer widget;
	private int depictionID;
	private int wallID;
	private AbsolutePanel background;

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
	
	public Walls(int depictionID, int wallID){
	
		this.wallID= wallID; 
		this.depictionID = depictionID;
		
	}
	public Widget createForm(){
		
		FramedPanel framePanel = new FramedPanel();
		framePanel.setHeading("Wall editor");
	
		VerticalPanel main = new VerticalPanel();
		background = new AbsolutePanel();
		main.add(background);
		background.setSize("800px", "400px");
		background.setStyleName("BackgroundStyle");
		background.addStyleDependentName("BackgroundStyle");
		framePanel.add(main);
	
		ButtonBar buttonbar = new ButtonBar();
	
		Button save = new Button("save");
		
		save.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				
			}
			
		});
		buttonbar.add(save);
		
		Button cancel = new Button("cancel");
		buttonbar.add(cancel);
	
	 
		main.add(buttonbar);
		return framePanel;
		
	}
	
	public void createNewDepictionOnWall(final DepictionEntry depiction){
		dbService.getMasterImageEntryForDepiction(depiction.getDepictionID(),new AsyncCallback<ImageEntry>() {


			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			} 
			@Override
			public void onSuccess(ImageEntry imageresult) {
			
				SafeUri uri =  UriUtils.fromString("infosystem/images?imageID=" + imageresult.getImageID());
				final Image image = new Image(uri);
				final SimpleContainer newDepictionContainer = new SimpleContainer();
				 background.add(newDepictionContainer);
				 newDepictionContainer.add(image);
				 background.setWidgetPosition(newDepictionContainer, depiction.getAbsoluteLeft(), depiction.getAbsoluteTop());
				 Draggable drag = new Draggable(newDepictionContainer);
				 newDepictionContainer.setPixelSize(image.getOffsetWidth(), image.getOffsetHeight());
				 Resizable resize = new Resizable(newDepictionContainer, Resizable.Dir.NE,Resizable.Dir.NW, Resizable.Dir.SE, Resizable.Dir.SW);
				 resize.setPreserveRatio(true);
				 
					ResizeHandler resizeHandler =new ResizeHandler(){

						@Override
						public void onResize(ResizeEvent event) {
							
							image.setPixelSize(newDepictionContainer.getOffsetWidth(),newDepictionContainer.getOffsetHeight());
						}
						
					};
					
					newDepictionContainer.addHandler(resizeHandler, ResizeEvent.getType());
			}
		});
		
		
		
	}
	public void getAllDepictionIDsbyWall(){
		dbService.getDepictionsbyWallID(wallID, new AsyncCallback<ArrayList<DepictionEntry>>() {


			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<DepictionEntry> result) {
				
				for (final DepictionEntry depiction : result) {
					if(depiction.getAbsoluteLeft() != -1 && depiction.getAbsoluteTop() != -1 ){
					createNewDepictionOnWall(depiction);
					
					}
		
				}
			}
		});
	}
	
	public void add(final DepictionEntry depiction){
	createNewDepictionOnWall(depiction);
	}
	
}
