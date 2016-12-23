package de.cses.client.walls;



import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class Walls implements IsWidget{
	private VBoxLayoutContainer widget;
	Image image;
	 SimpleContainer door = new SimpleContainer();
	 String imageURI= "";

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
	
	public Walls(String imageURI){
		this.imageURI = imageURI;
		
	}
	public Widget createForm(){
		
		

		FramedPanel framePanel = new FramedPanel();
		framePanel.setHeading("Wall editor");
	
		VerticalPanel main = new VerticalPanel();
		final AbsolutePanel background = new AbsolutePanel();
		main.add(background);
		background.setSize("800px", "400px");
		background.setStyleName("BackgroundStyle");
		background.addStyleDependentName("BackgroundStyle");
		framePanel.add(main);
	
		ButtonBar buttonbar = new ButtonBar();
		Button newDepiction = new Button("add Depiction");
		buttonbar.add(newDepiction);
		
		Button save = new Button("save");
		buttonbar.add(save);
		
		Button cancel = new Button("cancel");
		buttonbar.add(cancel);
		
		
		
		ClickHandler depictionHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				 background.add(door);
				 image = new Image(imageURI);
				 door.add(image);
				 Draggable drag = new Draggable(door);
				 door.setPixelSize(image.getOffsetWidth(), image.getOffsetHeight());
				 Resizable resize = new Resizable(door, Resizable.Dir.NE,Resizable.Dir.NW, Resizable.Dir.SE, Resizable.Dir.SW);
				 resize.setPreserveRatio(true);
				 
			}
		};
		
		ResizeHandler resizeHandler =new ResizeHandler(){

			@Override
			public void onResize(ResizeEvent event) {
				
				image.setPixelSize(door.getOffsetWidth(),door.getOffsetHeight());
			}
			
		};
		door.addHandler(resizeHandler, ResizeEvent.getType());
		
		newDepiction.addClickHandler(depictionHandler);
		main.add(buttonbar);
		return framePanel;
		
	}
	
	public void setImageURI(String imageURI){
		this.imageURI = imageURI;
	}
	
	public String getImageURI(){
		return imageURI;
	}
}
