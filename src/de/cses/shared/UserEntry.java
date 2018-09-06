/*
 * Copyright 2017 
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

import java.util.Date;

/**
 * @author alingnau
 *
 */
public class UserEntry extends AbstractEntry {

	public static final int ACCESS_LEVEL_ASSOCIATED = 1;
	public static final int ACCESS_LEVEL_FULL = 2;
	
	private int userID;
	private String username;
	private String firstname;
	private String lastname;
	private String email;
	private String affiliation;
	private int accessrights;
	private String sessionID;
	private long loginDate;

	/**
	 * 
	 */
	public UserEntry() {
		this(0, "", "", "", "", "", ACCESS_LEVEL_ASSOCIATED);
	}

	/**
	 * @param userID
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param affiliation
	 * @param accessrights
	 */
	public UserEntry(int userID, String username, String firstname, String lastname, String email, String affiliation, int accessrights) {
		super();
		this.userID = userID;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.affiliation = affiliation;
		this.accessrights = accessrights;
		Date now = new Date();
		setLoginDate(now.getTime());
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public int getAccessrights() {
		return accessrights;
	}

	public void setAccessrights(int accessrights) {
		this.accessrights = accessrights;
	}

	/* (non-Javadoc)
	 * @see de.cses.shared.AbstractEntry#uniqueID()
	 */
	@Override
	public String getUniqueID() {
		return "User-" + userID;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the loginDate
	 */
	public long getLoginDate() {
		return loginDate;
	}

	/**
	 * @param loginDate the loginDate to set
	 */
	public void setLoginDate(long loginDate) {
		this.loginDate = loginDate;
	}
	
	

}
