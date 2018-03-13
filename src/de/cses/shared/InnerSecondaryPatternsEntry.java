/*
 * Copyright 2018 
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
package de.cses.shared;

/**
 * @author Nina
 *
 */
public class InnerSecondaryPatternsEntry {
	int innerSecondaryPatternsId;
	String name;
	/**
	 * @return the innerSecondaryPatternsId
	 */
	public int getInnerSecondaryPatternsID() {
		return innerSecondaryPatternsId;
	}
	public InnerSecondaryPatternsEntry() {
		
	}
	
	public InnerSecondaryPatternsEntry(int innerSecondaryPatternsID, String name) {
		
	}
	/**
	 * @param innerSecondaryPatternsId the innerSecondaryPatternsId to set
	 */
	public void setInnerSecondaryPatternsID(int innerSecondaryPatternsId) {
		this.innerSecondaryPatternsId = innerSecondaryPatternsId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	

}
