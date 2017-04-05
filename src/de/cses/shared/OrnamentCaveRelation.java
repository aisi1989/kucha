package de.cses.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OrnamentCaveRelation implements IsSerializable{
	private int ornamentID;
	private String name;
	private int caveID;
	private int style;
	private String orientation;
	private String structure;
	private int mainTopologycalClass;
	private String colours;
	private String position;
	private String function;
	private String cavepart;
	private String notes;
	private int group;
	private List<OrnamentEntry> similarOrnamentsRelationID= new ArrayList<OrnamentEntry>();
	private List<OrnamentEntry> relatedOrnamentsRelationID = new ArrayList<OrnamentEntry>();
	private List<OrnamentOfOtherCulturesEntry>otherCulturalOrnamentsRelationID = new ArrayList<OrnamentOfOtherCulturesEntry>();
	
	
	public OrnamentCaveRelation(){
		
	}


	public int getOrnamentID() {
		return ornamentID;
	}


	public void setOrnamentID(int ornamentID) {
		this.ornamentID = ornamentID;
	}


	public int getCaveID() {
		return caveID;
	}


	public void setCaveID(int caveID) {
		this.caveID = caveID;
	}


	public int getStyle() {
		return style;
	}


	public void setStyle(int style) {
		this.style = style;
	}


	public String getOrientation() {
		return orientation;
	}


	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}


	public String getStructure() {
		return structure;
	}


	public void setStructure(String structure) {
		this.structure = structure;
	}


	public int getMainTopologycalClass() {
		return mainTopologycalClass;
	}


	public void setMainTopologycalClass(int mainTopologycalClass) {
		this.mainTopologycalClass = mainTopologycalClass;
	}


	public String getColours() {
		return colours;
	}


	public void setColours(String colours) {
		this.colours = colours;
	}


	public String getPosition() {
		return position;
	}


	public void setPosition(String position) {
		this.position = position;
	}


	public String getFunction() {
		return function;
	}


	public void setFunction(String function) {
		this.function = function;
	}


	public String getCavepart() {
		return cavepart;
	}


	public void setCavepart(String cavepart) {
		this.cavepart = cavepart;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public int getGroup() {
		return group;
	}


	public void setGroup(int group) {
		this.group = group;
	}




	public List<OrnamentEntry> getSimilarOrnamentsRelationID() {
		return similarOrnamentsRelationID;
	}


	public void setSimilarOrnamentsRelationID(List<OrnamentEntry> similarOrnamentsRelationID) {
		this.similarOrnamentsRelationID = similarOrnamentsRelationID;
	}


	public List<OrnamentEntry> getRelatedOrnamentsRelationID() {
		return relatedOrnamentsRelationID;
	}


	public void setRelatedOrnamentsRelationID(List<OrnamentEntry> relatedOrnamentsRelationID) {
		this.relatedOrnamentsRelationID = relatedOrnamentsRelationID;
	}


	public List<OrnamentOfOtherCulturesEntry> getOtherCulturalOrnamentsRelationID() {
		return otherCulturalOrnamentsRelationID;
	}


	public void setOtherCulturalOrnamentsRelationID(List<OrnamentOfOtherCulturesEntry> otherCulturalOrnamentsRelationID) {
		this.otherCulturalOrnamentsRelationID = otherCulturalOrnamentsRelationID;
	}


	public void setOtherCulturalOrnamentsRelationID(
			ArrayList<OrnamentOfOtherCulturesEntry> otherCulturalOrnamentsRelationID) {
		this.otherCulturalOrnamentsRelationID = otherCulturalOrnamentsRelationID;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}



	
	

}
