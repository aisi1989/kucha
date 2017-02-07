package de.cses.client.walls;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;

import de.cses.client.DatabaseService;
import de.cses.client.DatabaseServiceAsync;
import de.cses.shared.DepictionEntry;

public class Walls implements IsWidget{
	private DatabaseServiceAsync dbService = GWT.create(DatabaseService.class);
	private VBoxLayoutContainer widget;
	private int wallID;
	private AbsolutePanel background;
	private boolean editable = false;
	ButtonBar buttonbar = new ButtonBar();
	private PopupPanel panel;
	private Button save = new Button("save");

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
	
	public Walls(int wallID, boolean editable){
		this.editable = editable;
		this.wallID= wallID; 
		
	}
	public Widget createForm(){
		
		FramedPanel framePanel = new FramedPanel();
		framePanel.setHeading("Wall editor");
	
		VerticalPanel main = new VerticalPanel();
		background = new AbsolutePanel();
		main.add(background);
		background.setSize("800px", "400px");
		framePanel.add(main);
	

		
		Button cancel = new Button("close");
		buttonbar.add(cancel);
		
		cancel.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				panel.hide();
			}
		});
		
	 
		main.add(buttonbar);
		return framePanel;
		
	}
	
	public void createNewDepictionOnWall(DepictionEntry depiction, boolean editable, boolean firstTime){
		
				DepictionView depictionview = new DepictionView(depiction.getDepictionID(), editable );
				Window.alert("positioning new depiction");
				 background.add(depictionview);
				 if(firstTime){
					 background.setWidgetPosition(depictionview, 30, 30);
				 }
				 else{
				 background.setWidgetPosition(depictionview, depiction.getAbsoluteLeft(), depiction.getAbsoluteTop());
				 }
		
	}
	public void getAllDepictionIDsbyWall(){
		dbService.getDepictionsbyWallID(wallID, new AsyncCallback<ArrayList<DepictionEntry>>() {


			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<DepictionEntry> result) {
				Window.alert("found one depiction for this wall");
				
				for (final DepictionEntry depiction : result) {
					if(depiction.getAbsoluteLeft() != -1 && depiction.getAbsoluteTop() != -1 ){
						Window.alert("depiction wurde bereits plaziert");
					createNewDepictionOnWall(depiction, editable, false);
					
					}
		
				}
			}
		});
	}
	
	public void add(DepictionEntry depiction){
	displayButtons(true);
	createNewDepictionOnWall(depiction, true, true);
	}
	public void show(){
		getAllDepictionIDsbyWall();
		Window.alert("going to show");
	}

	public PopupPanel getPanel() {
		return panel;
	}

	public void setPanel(PopupPanel panel) {
		this.panel = panel;
	}
	
	public void displayButtons(boolean display){
		if(display){	
			save.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					
					Iterator<Widget> iterator = background.iterator();
					while(iterator.hasNext()){
						if(iterator.next() instanceof DepictionView){
						DepictionView depictionView =(DepictionView) iterator.next();
						
					int absoluteLeft = 	depictionView.getAbsoluteLeft();
					int absoluteTop = 	depictionView.getAbsoluteTop();
					dbService.saveDepiction(depictionView.getDepictionID(), absoluteLeft, absoluteTop, new AsyncCallback<String>() {


						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}

						@Override
						public void onSuccess(String result) {
							
						}
					});
						
					}
					}
				}
				});
				buttonbar.add(save);
			
		}
		else{
				buttonbar.remove(save);
			
		}
					
	
					}
}
