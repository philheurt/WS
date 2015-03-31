package com.app_server.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.app_server.constants.Constants;
import com.app_server.data.Account;
import com.app_server.data.Tag;
import com.app_server.utilities.Utilities;

public class StorageService {	

	/**
	 * Method to create a connection to the database
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public static Connection createConnection() throws Exception {
		Connection con = null;
			Class.forName(Constants.dbClass);
			con = DriverManager.getConnection(Constants.dbUrl, Constants.dbUser, Constants.dbPwd);
			return con;
	}
	/**
     * Method to check whether pseudo and password combination are correct
     * 
     * @param pseudo
     * @param password
     * @return
     * @throws Exception
     */
	public static boolean checkLogin(String pseudo, String password) throws Exception {
		boolean isUserAvailable = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = StorageService.createConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT * FROM User WHERE pseudo = ? AND password = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, Utilities.hashPassword(password));
			//System.out.println(query);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {				
				isUserAvailable = true;
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return isUserAvailable;
	}
	
    /**
     * Method to map the result of the login query into an account object
     * 
     * @param resultSet
     * @return
     * @throws SQLException
     * 
     */
	
	private static Account map( ResultSet resultSet ) throws SQLException {
	    Account account = new Account (resultSet.getString( "pseudo" ), resultSet.getString( "first_name" ),resultSet.getString( "last_name" ),resultSet.getString( "email" ));	  
	    return account;
	}
	
	/**
     * Method to check whether pseudo and password combination are correct
     * 
     * @param pseudo
     * @param password
     * @return
     * @throws Exception
     * 
     */
	
	public static Account doLogin(String pseudo, String password) throws Exception {
		Connection dbConn = null;
		Account account = null;
		try {
			dbConn = StorageService.createConnection();		
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT pseudo,first_name,last_name,email FROM User WHERE pseudo = ? AND password = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, Utilities.hashPassword(password));
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				account = map(rs);
			}
		} catch (SQLException sqle) {
			dbConn.close();
			throw sqle;
		} catch (Exception e) {
					
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return account;
	}
	
	/**
	 * Method to insert a User with its pseudo, password, first_name, last_name and email in the database
	 * 
	 * @param pseudo
	 * @param password
	 * @param first_name
	 * @param last_name
	 * @param email 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 * 
	 */
	
	public static boolean insertUser(String pseudo, String password, String first_name, String last_name, String email) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("INSERT into User(pseudo, password, first_name, last_name, email) values(?,?,?,?,?);");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, Utilities.hashPassword(password));
			preparedStatement.setString( 3, first_name );
			preparedStatement.setString( 4, last_name);
			preparedStatement.setString( 5, email );						
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	
	/**
	 * Method to insert  a tag in the database
	 * 
	 * @param pseudo
	 * @param password
	 * @param object_name
	 * @param picture
	 * @param email 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 * 
	 */
	
	public static boolean insertTag(String id, String pseudo, String object_name, String picture) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("INSERT into Tag(tag_id,pseudo_owner, object_name, picture) values(?,?,?,?);");
			preparedStatement.setString(1, id);
			preparedStatement.setString( 2, pseudo );
			preparedStatement.setString( 3, object_name);
			preparedStatement.setString( 4, picture);					
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	public static boolean deleteTag(String pseudo, String id) throws SQLException, Exception {
		boolean deleteStatus = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("DELETE FROM Tag WHERE pseudo_owner = ? && tag_id = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, id);					
			//System.out.println(query);
			int records = preparedStatement.executeUpdate();
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				deleteStatus = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return deleteStatus;
	}
	
	/**
     * Method to map the result of the login query into an account object
     * 
     * @param resultSet
     * @return
     * @throws SQLException
     * 
     */
	
	private static Tag mapTag( ResultSet resultSet ) throws SQLException {
	    Tag tag = new Tag (resultSet.getString( "tag_id" ), resultSet.getString( "object_name" ),resultSet.getString( "picture" ));	  
	    return tag;
	}
	
	public static ArrayList<Tag> retrieveTags(String pseudo, String password) throws Exception{
		Connection dbConn = null;
		ArrayList<Tag >result = new ArrayList<Tag>();
		try {
			dbConn = StorageService.createConnection();
			if(StorageService.checkLogin(pseudo, password)){
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT tag_id,object_name,picture FROM Tag where pseudo_owner = ?;");
			preparedStatement.setString( 1, pseudo );						
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result.add(mapTag(rs));
			}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return result;
	}
			
	public static boolean modifyPassword(String pseudo, String newPassword) throws SQLException, Exception {
		boolean modifyPassword = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE User SET password = ? WHERE pseudo = ?;");
			preparedStatement.setString( 1, Utilities.hashPassword(newPassword) );
			preparedStatement.setString( 2, pseudo);					
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				modifyPassword = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return modifyPassword;
	}
	
	
	public static boolean modifyEMailAdress(String pseudo, String newEMailAdress) throws SQLException, Exception {
		boolean modifyEMailAdress = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE User SET email = ? WHERE pseudo = ?;");
			preparedStatement.setString( 1, newEMailAdress );
			preparedStatement.setString( 2, pseudo);					
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				modifyEMailAdress = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return modifyEMailAdress;
	}
	
	public static boolean modifyTagName(String id, String newObjectName, String pseudo) throws SQLException, Exception {
		boolean modifyTagName = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE Tag SET object_name = ? WHERE tag_id = ? and pseudo_owner = ?;");
			preparedStatement.setString( 1, newObjectName );
			preparedStatement.setString( 2, id);
			preparedStatement.setString( 3, pseudo);
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				modifyTagName = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return modifyTagName;
	}
	
	public static boolean insertProfile(String pseudo, String profileName) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("INSERT into Profile(pseudo, profile_name ) values(?,?);");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, profileName);						
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	public static int retrieveProfileIdFromName (String pseudo, String profileName ) throws Exception {
		Connection dbConn = null;
		int profileId;
		try {
			dbConn = StorageService.createConnection();		
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT profile_id FROM Profile WHERE pseudo = ? AND profile_name = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, profileName);
			ResultSet rs = preparedStatement.executeQuery();
			profileId= rs.getInt("profile_id");					
		} catch (SQLException sqle) {
			dbConn.close();
			throw sqle;
		} catch (Exception e) {
					
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return profileId;
	}
	
	public static boolean insertTagToProfile(String pseudo, String profileName, String tagId) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		int profileId = retrieveProfileIdFromName(pseudo,profileName);
		try {
			dbConn = StorageService.createConnection();								
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("INSERT into Relation_profile_tag(profile_id, tag_id ) values(?,?);");
			preparedStatement.setInt( 1, profileId );
			preparedStatement.setString( 2, tagId);						
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			dbConn.close();
			throw sqle;
		} catch (Exception e) {
					
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	
	public static ArrayList<Tag> retrieveTagsFromProfile(String pseudo, String password, String profileName) throws Exception{
		Connection dbConn = null;
		ArrayList<Tag >result = new ArrayList<Tag>();
		try {			
			dbConn = StorageService.createConnection();			
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT tag_id,object_name,picture FROM Relation_profile_tag where profile_id = ?;");
			preparedStatement.setInt( 1, retrieveProfileIdFromName(pseudo,profileName) );						
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result.add(mapTag(rs));
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return result;
	}
	
}