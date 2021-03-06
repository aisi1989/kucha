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
package de.cses.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.cses.server.json.CaveEntrySerializer;
import de.cses.server.json.DepictionSerializer;
import de.cses.server.mysql.MysqlConnector;
import de.cses.shared.CaveEntry;
import de.cses.shared.CaveTypeEntry;
import de.cses.shared.DepictionEntry;
import de.cses.shared.DistrictEntry;
import de.cses.shared.ExpeditionEntry;
import de.cses.shared.IconographyEntry;
import de.cses.shared.RegionEntry;
import de.cses.shared.SiteEntry;
import de.cses.shared.StyleEntry;
import de.cses.shared.UserEntry;

/**
 * @author alingnau
 *
 */
@SuppressWarnings("serial")
public class JsonServlet extends HttpServlet {

	private MysqlConnector connector = MysqlConnector.getInstance();
	private HttpServletRequest request;
	private HttpServletResponse response;

	/**
	 * 
	 */
	public JsonServlet() { }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		this.request = request;
		this.response = response;
		
		Enumeration<String> reqList = request.getParameterNames();
		if (reqList.hasMoreElements()) {
			switch (reqList.nextElement()) {
				case "login":
					login();
					break;
					
				case "checkSession":
					if (connector.checkSessionID(request.getParameter("sessionID"))) {
						response.sendError(HttpServletResponse.SC_NO_CONTENT);
					} else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					break;
					
				case "caveID":
					getCaves(connector.checkSessionID(request.getParameter("sessionID")));
					break;
					
				case "siteID":
					if (connector.checkSessionID(request.getParameter("sessionID"))) {
						getSites();
					} else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					break;
					
				case "regionID":
					if (connector.checkSessionID(request.getParameter("sessionID"))) {
						getRegions();
					} else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					break;
					
				case "districtID":
					if (connector.checkSessionID(request.getParameter("sessionID"))) {
						getDistricts();
					} else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					break;
					
				case "iconographyID":
					if (connector.checkSessionID(request.getParameter("sessionID"))) {
						getIconography();
					} else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					break;
					
				case "paintedRepID":
					if (connector.checkSessionID(request.getParameter("sessionID"))) {
						getDepiction();
					} else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					break;
					
				case "caveTypeID":
					getCaveTypes();
					break;
					
				case "styleID":
					getStyles();
					break;
					
				case "expeditionID":
					getExpeditions();
					break;
					
				case "paintedRepFromIconographyID":
					getRelatedDepictionsFromIconography();
					break;
					
				case "exclusivePaintedRepFromIconographyID":
					getExclusiveRelatedDepictionsFromIconography();
					break;
					
				default:
					response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
					break;
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		response.getWriter().close();
	}
	
	private void login() throws IOException {
		String loginStr = request.getParameter("login");
		String passwordStr = request.getParameter("pw");
		UserEntry currentUser = connector.userLogin(loginStr, passwordStr);
		if (currentUser.getSessionID() != null) {
			response.getWriter().write(currentUser.getSessionID());
		}
		response.setContentType("application/json");
	}
	
	private void getCaves(boolean publicOnly) throws IOException {
		String caveIDStr = request.getParameter("caveID");
		String sqlWhere=null;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();
		ArrayList<CaveEntry> caveEntries; 

		if ("all".equals(caveIDStr)) {
			if (request.getParameter("siteID") != null) {
				sqlWhere = "(SiteID IN (" + request.getParameter("siteID") + ")";
			}
			if (request.getParameter("districtID") != null) {
				if (sqlWhere != null) {
					sqlWhere = sqlWhere.concat(" OR DistrictID IN (" + request.getParameter("districtID") + ")");
				} else {
					sqlWhere = "(DistrictID IN (" + request.getParameter("districtID") + ")";
				}
			}
			if (request.getParameter("regionID") != null) {
				if (sqlWhere != null) {
					sqlWhere = sqlWhere.concat(" OR RegionID IN (" + request.getParameter("regionID") + ")");
				} else {
					sqlWhere = "(RegionID IN (" + request.getParameter("regionID") + ")";
				}
			}
			if (sqlWhere != null) {
				sqlWhere = sqlWhere.concat(")");
			}
			if (request.getParameter("caveTypeID") != null) {
				if (sqlWhere != null) {
					sqlWhere = sqlWhere.concat(" AND CaveTypeID IN (" + request.getParameter("caveTypeID") + ")");
				} else {
					sqlWhere = "CaveTypeID IN (" + request.getParameter("caveTypeID") + ")";
				}
			}
			if (publicOnly) {
				if (sqlWhere != null) {
					sqlWhere = sqlWhere.concat(") AND OpenAccess=1");
				} else {
					sqlWhere = "OpenAccess=1";
				}
			}
			
			caveEntries = connector.getCaves(sqlWhere);
		} else {
			caveEntries = connector.getCaves("CaveID IN (" + caveIDStr + ")");			
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(CaveEntry.class, new CaveEntrySerializer());
		Gson gson = gsonBuilder.create();		
		out.println(gson.toJson(caveEntries));
		out.close();
	}

	private void getSites() throws IOException {
		String siteIDStr = request.getParameter("siteID");
		Gson gs = new Gson();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();
		ArrayList<SiteEntry> siteEntries;
		
		if ("all".equals(siteIDStr)) {
			siteEntries = connector.getSites();
		} else {
			siteEntries = connector.getSites("SiteID IN (" + siteIDStr + ")");			
		}
		out.println(gs.toJson(siteEntries));
		out.close();
	}

	private void getRegions() throws IOException {
		String siteIDStr = request.getParameter("regionID");
		Gson gs = new Gson();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();
		ArrayList<RegionEntry> regionEntries ;
		
		if ("all".equals(siteIDStr)) {
			regionEntries = connector.getRegions();
		} else {
			regionEntries = connector.getRegions("RegionID IN (" + siteIDStr + ")");
		}
		out.println(gs.toJson(regionEntries));
		out.close();
	}

	private void getDistricts() throws IOException {
		String districtIDStr = request.getParameter("districtID");
		Gson gs = new Gson();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();
		ArrayList<DistrictEntry> districtEntries;

		if ("all".equals(districtIDStr)) {
			districtEntries = connector.getDistricts();
		} else {
			districtEntries = connector.getDistricts("DistrictID IN (" + districtIDStr + ")");			
		}
		out.println(gs.toJson(districtEntries));
		out.close();
	}

	private void getIconography() throws IOException {
		String iconographyIDStr = request.getParameter("iconographyID");
		Gson gs = new Gson();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();
		ArrayList<IconographyEntry> iconographyEntries ;

		if ("all".equals(iconographyIDStr)) {
			iconographyEntries = connector.getIconography(0);
		} else if ("used".equals(iconographyIDStr)) {
			iconographyEntries = connector.getIconographyEntriesUsedInDepictions();
		} else {
			iconographyEntries = connector.getIconographyEntries("IconographyID IN (" + iconographyIDStr + ")");			
		}
		out.println(gs.toJson(iconographyEntries));
		out.close();
	}
	
	private void getDepiction() throws IOException {
		String depictionIDStr = request.getParameter("paintedRepID");
		String sqlWhere = null;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();
		ArrayList<DepictionEntry> depictionEntries ;

		if ("all".equals(depictionIDStr)) {
			if (request.getParameter("caveID") != null) {
				sqlWhere = "CaveID IN (" + request.getParameter("caveID") + ")";
			}
			depictionEntries = connector.getDepictions(sqlWhere);
		} else {
			if (request.getParameter("caveID") != null) {
				sqlWhere = "DepictionID IN (" + depictionIDStr + ") AND CaveID IN (" + request.getParameter("caveID") + ")";
			} else {
				sqlWhere = "DepictionID IN (" + depictionIDStr + ")";
			}
			depictionEntries = connector.getDepictions(sqlWhere);
		}
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(DepictionEntry.class, new DepictionSerializer());
		Gson gson = gsonBuilder.create();		
		out.println(gson.toJson(depictionEntries));
		out.close();
	}
	
	private void getRelatedDepictionsFromIconography() throws IOException {
		String iconographyIDs = request.getParameter("paintedRepFromIconographyID");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(DepictionEntry.class, new DepictionSerializer());
		Gson gson = gsonBuilder.create();		
		if (iconographyIDs != null) {
		 ArrayList<DepictionEntry> depictionEntries = connector.getRelatedDepictions(iconographyIDs, 0);
		 out.println(gson.toJson(depictionEntries));
		}
		out.close();
	}
	
	private void getExclusiveRelatedDepictionsFromIconography() throws IOException {
		String iconographyIDs = request.getParameter("exclusivePaintedRepFromIconographyID");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(DepictionEntry.class, new DepictionSerializer());
		Gson gson = gsonBuilder.create();		
		if (iconographyIDs != null) {
		 ArrayList<DepictionEntry> depictionEntries = connector.getRelatedDepictions(iconographyIDs, iconographyIDs.split(",").length);
		 out.println(gson.toJson(depictionEntries));
		}
		out.close();
	}
	
	private void getCaveTypes() throws IOException {
		String caveTypeIDStr = request.getParameter("caveTypeID");
		Gson gs = new Gson();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();

		ArrayList<CaveTypeEntry> caveTypeEntries;

		if ("all".equals(caveTypeIDStr)) {
			caveTypeEntries = connector.getCaveTypes();
		} else {
			caveTypeEntries = connector.getCaveTypes("CaveTypeID IN (" + caveTypeIDStr + ")");
		}
		out.println(gs.toJson(caveTypeEntries));
		out.close();
	}
	
	private void getStyles() throws IOException {
		String styleIDStr = request.getParameter("styleID");
		Gson gs = new Gson();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();

		ArrayList<StyleEntry> caveTypeEntries;

		if ("all".equals(styleIDStr)) {
			caveTypeEntries = connector.getStyles();
		} else {
			caveTypeEntries = connector.getStyles("StyleID IN (" + styleIDStr + ")");
		}
		out.println(gs.toJson(caveTypeEntries));
		out.close();
	}
	
	private void getExpeditions() throws IOException {
		String expeditionIDStr = request.getParameter("expeditionID");
		Gson gs = new Gson();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF8");
		PrintWriter out = response.getWriter();

		ArrayList<ExpeditionEntry> expeditionEntries;

		if ("all".equals(expeditionIDStr)) {
			expeditionEntries = connector.getExpeditions();
		} else {
			expeditionEntries = connector.getExpeditions("ExpeditionID IN (" + expeditionIDStr + ")");
		}
		out.println(gs.toJson(expeditionEntries));
		out.close();
	}
	
}
