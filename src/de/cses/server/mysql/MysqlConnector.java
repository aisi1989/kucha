/*
 * Copyright 2016 
 * Saxon Academy of Science in Leipzig, Germany
 * 
 * This is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU Affero General Public License version 3 (GNU AGPLv3) as published by the Free Software Foundation.
 * 
 * This software is distributed WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. Please read the GNU AGPLv3 for more details.
 * 
 * You should have received a copy of the GNU AGPLv3 along with the software. 
 * If not, you can access it from here: <https://www.gnu.org/licenses/agpl-3.0.txt>.
 */
package de.cses.server.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import de.cses.shared.District;

/**
 * 
 * @author alingnau
 *
 */
public class MysqlConnector {

	private static String url = "jdbc:mysql://kucha.informatik.hu-berlin.de/infosys?useUnicode=true&characterEncoding=UTF-8"; //$NON-NLS-1$
	private static String user = Messages.getString("MysqlConnector.db.user");
	private static String password = Messages.getString("MysqlConnector.db.password");

	private static MysqlConnector instance = null;

	public static synchronized MysqlConnector getInstance() {
		if (instance == null) {
			instance = new MysqlConnector();
		}
		return instance;
	}

	private Connection connection;

	private MysqlConnector() {

		try {
			Class.forName("org.mariadb.jdbc.Driver"); //$NON-NLS-1$

			System.err.println("class loaded"); //$NON-NLS-1$

			try {
				connection = DriverManager.getConnection(MysqlConnector.url, MysqlConnector.user, MysqlConnector.password);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Connection getConnection() {
		try {
			if (connection == null || !connection.isValid(5)) {
				connection = DriverManager.getConnection(MysqlConnector.url, MysqlConnector.user, MysqlConnector.password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	public synchronized Hashtable<String, String> getTestTable() {

		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();

			String sql = "SELECT * FROM test"; //$NON-NLS-1$

			ResultSet rs = stmt.executeQuery(sql);

			Hashtable<String, String> results = new Hashtable<String, String>();

			String name, age;
			while (rs.next()) {
				name = rs.getString("Name"); //$NON-NLS-1$
				age = rs.getString("Age"); //$NON-NLS-1$
				results.put(name, age);
				System.err.println("(" + name + "," + age + ") read from database"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			rs.close();
			stmt.close();
			dbc.close();
			System.err.println("Database request finished"); //$NON-NLS-1$
			return results;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public synchronized ArrayList<District> getDistricts() {
		ArrayList<District> Districts = new ArrayList<District>();
		Connection dbc = getConnection();
		Statement stmt;
		try {
			stmt = dbc.createStatement();
			String sql = "SELECT * FROM Districts"; //$NON-NLS-1$

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				District District = new District();
				District.setDistrictID(rs.getInt("DistrictID"));
				District.setName(rs.getString("Name"));
				District.setDescription(rs.getString("Description"));
				Districts.add(District);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Districts;
	}

	public synchronized int createNewImageEntry() {
		Connection dbc = getConnection();
		Statement stmt;
		int generatedKey = -1;
		try {
			stmt = dbc.createStatement();

			String sql = "INSERT INTO Images (Comment) VALUES ('please type your comment here')";
			stmt.execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet keys = stmt.getGeneratedKeys();
			while (keys.next()) { 
				// there should only be 1 key returned here but we need to modify this in case
				// we have requested multiple new entries. works for the moment
				generatedKey = keys.getInt(1);
				System.err.println("result key " + generatedKey);
			}
			keys.close();
			stmt.close();
			dbc.close();
			System.err.println("Database request finished"); //$NON-NLS-1$

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return generatedKey;
	}

}
