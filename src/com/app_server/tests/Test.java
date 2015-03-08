package com.app_server.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import org.codehaus.jettison.json.JSONArray;

import com.app_server.constants.Constants;
import com.app_server.implementation.NetworkService;
import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) {

			NetworkService NS = new NetworkService();
			try {
				NS.doRegister("josé", "secret", "abc", "abc", "abc");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Connection con = null;
			try {
				con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			System.out.println(con);
	}

}
