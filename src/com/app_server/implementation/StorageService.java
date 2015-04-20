package com.app_server.implementation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.app_server.constants.Constants;
import com.app_server.data.Account;
import com.app_server.data.Tag;
import com.app_server.data.Profile;
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
	    account.setBraceletUID(resultSet.getString("braceletUID"));	  
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
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT pseudo,first_name,last_name,email,braceletUID FROM User WHERE pseudo = ? AND password = ?;");
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
	
	public static boolean insertUser(String pseudo, String password, String first_name, String last_name, String email, String braceletUID) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("INSERT into User(pseudo, password, first_name, last_name, email, braceletUID, personal_information_change_time, tags_change_time,profiles_change_time) values(?,?,?,?,?,?,NOW(),NOW(),NOW());");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, Utilities.hashPassword(password));
			preparedStatement.setString( 3, first_name );
			preparedStatement.setString( 4, last_name);
			preparedStatement.setString( 5, email );
			preparedStatement.setString( 6, braceletUID );
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
	
	public static boolean insertTag(String id, String pseudo, String object_name, InputStream picture) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("INSERT into Tag(tag_id,pseudo_owner, object_name, picture_version, picture) values(?,?,?,?,?);");
			preparedStatement.setString(1, id);
			preparedStatement.setString( 2, pseudo );
			preparedStatement.setString( 3, object_name);
			preparedStatement.setInt( 4, 0);
			preparedStatement.setBinaryStream( 5, picture);					
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
	
	public static InputStream downloadImageTag(String pseudo, String id) throws SQLException, Exception {
		Connection dbConn = null;
		InputStream in = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("Select picture FROM Tag WHERE pseudo_owner = ? AND tag_id = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, id);					
			//System.out.println(query);
			ResultSet rs = preparedStatement.executeQuery();
				if(rs.next()){
					in = rs.getBinaryStream("picture");			
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
		return in;
	}
	
	public static boolean deleteProfile(int profileID) throws SQLException, Exception {
		
		//plusieurs suppressions (on supprime d'abord des tables "relations_..")
		//si la première connexion_suppression marche, on effectue la deuxième, etc.
		boolean deleteStatus = false;
		boolean deleteStatus1 = false;

		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("DELETE FROM Relation_profile_tag WHERE profile_id = ?;");
			preparedStatement.setInt( 1, profileID );					
			//System.out.println(query);
			int records = preparedStatement.executeUpdate();
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				deleteStatus = true;
			}
				java.sql.PreparedStatement preparedStatement1 = dbConn.prepareStatement("DELETE FROM Profile WHERE profile_id = ?;");
				preparedStatement1.setInt( 1, profileID );					
				//System.out.println(query);
				int records1 = preparedStatement1.executeUpdate();
				//System.out.println(records);
				//When record is successfully inserted
				if (records1 > 0) {
					deleteStatus1 = true;
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
		//vaut true si les deux ont march� ... du coup si �a foire on ne sait pas dans laquelle des deux.. mais flemme de modifier
		return deleteStatus1;
	}
	
	public static boolean deleteTag(String pseudo,String tagID) throws SQLException, Exception {
		
		//plusieurs suppressions (on supprime d'abord des tables "relations_..")
		//si la première connexion_suppression marche, on effectue la deuxième, etc.
		boolean deleteStatus = false;
		boolean deleteStatus1 = false;
		boolean deleteStatus2 = false;
		boolean deleteStatus3 = false;

		Connection dbConn = null;
		try {
				dbConn = StorageService.createConnection();
				java.sql.PreparedStatement preparedStatement1 = dbConn.prepareStatement("DELETE FROM Relation_profile_tag WHERE tag_id = ?;");
				preparedStatement1.setString( 1, tagID );					
				//System.out.println(query);
				int records1 = preparedStatement1.executeUpdate();
				//System.out.println(records);
				//When record is successfully inserted
				if (records1 > 0) {
					deleteStatus1 = true;
				}		
				java.sql.PreparedStatement preparedStatement2 = dbConn.prepareStatement("DELETE FROM Relation_user_tag WHERE pseudo = ? AND tag_id = ?;");
				preparedStatement2.setString( 1, pseudo );
				preparedStatement2.setString( 2, tagID );					
				//System.out.println(query);
				int records2 = preparedStatement2.executeUpdate();
				//System.out.println(records);
				//When record is successfully inserted
				if (records2 > 0) {
					deleteStatus2 = true;
				}
			
				java.sql.PreparedStatement preparedStatement3 = dbConn.prepareStatement("DELETE FROM Tag WHERE pseudo_owner=? AND tag_id = ?;");
				preparedStatement3.setString(1, pseudo);
				preparedStatement3.setString( 2, tagID );					
				//System.out.println(query);
				int records3 = preparedStatement3.executeUpdate();
				//System.out.println(records);
				//When record is successfully inserted
				if (records3 > 0) {
					deleteStatus3 = true;
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
		//vaut true si les 4 ont march� ... du coup si �a foire on ne sait pas dans laquelle des 4.. mais flemme de modifier
		return deleteStatus3;
	}
		
	public static boolean updateProfileName(int profileID, String newProfileName)  throws SQLException, Exception {
		
		boolean updateStatus = false;
		Connection dbConn1 = null;
		try {
			dbConn1 = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn1.prepareStatement("UPDATE Profile SET profile_name = ? WHERE profile_id = ?;");
			preparedStatement.setString( 1, newProfileName );
			preparedStatement.setInt( 2, profileID );
			//System.out.println(query);
			int records = preparedStatement.executeUpdate();
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				updateStatus = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn1 != null) {
				dbConn1.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn1 != null) {
				dbConn1.close();
			}
			throw e;
		} finally {
			if (dbConn1 != null) {
				dbConn1.close();
			}
		}
		return updateStatus;
	}
		
	public static int getProfileID(String pseudo, String profileName) throws SQLException, Exception {
		
		Connection dbConn = null;
		int profileID = 0;
		try {
			dbConn = StorageService.createConnection();		
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT profile_id FROM Profile WHERE pseudo = ? AND profile_name = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, profileName);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()){
			profileID = rs.getInt("profile_id");
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
		return profileID;
	}

	public static boolean deleteTagFromProfile(String pseudo, int profileID, String id) throws SQLException, Exception {
		
		boolean deleteStatus = false;
		Connection dbConn1 = null;
		try {
			dbConn1 = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn1.prepareStatement("DELETE FROM Relation_profile_tag WHERE profile_id = ? AND tag_id = ?;");
			preparedStatement.setInt( 1, profileID );
			preparedStatement.setString( 2, id );
			//System.out.println(query);
			int records = preparedStatement.executeUpdate();
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				deleteStatus = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn1 != null) {
				dbConn1.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn1 != null) {
				dbConn1.close();
			}
			throw e;
		} finally {
			if (dbConn1 != null) {
				dbConn1.close();
			}
		}
		return deleteStatus;
	}

	public static boolean deleteAllTagsFromProfile(String pseudo, int profileID) throws SQLException, Exception {
		
		boolean deleteStatus = false;
		Connection dbConn1 = null;
		try {
			dbConn1 = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn1.prepareStatement("DELETE FROM Relation_profile_tag WHERE profile_id = ?;");
			preparedStatement.setInt( 1, profileID );
			//System.out.println(query);
			int records = preparedStatement.executeUpdate();
			//System.out.println(records);
			//When record is successfully inserted
			if (records > 0) {
				deleteStatus = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (dbConn1 != null) {
				dbConn1.close();
			}
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
			if (dbConn1 != null) {
				dbConn1.close();
			}
			throw e;
		} finally {
			if (dbConn1 != null) {
				dbConn1.close();
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
	    Tag tag = new Tag (resultSet.getString( "tag_id" ), resultSet.getString( "object_name" ));
	    tag.setImageVersion(resultSet.getInt("picture_version"));
	    return tag;
	}
	
	public static ArrayList<Tag> retrieveTags(String pseudo, String password) throws Exception{
		Connection dbConn = null;
		ArrayList<Tag >result = new ArrayList<Tag>();
		try {
			dbConn = StorageService.createConnection();
			if(StorageService.checkLogin(pseudo, password)){
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT tag_id,object_name,picture_version FROM Tag where pseudo_owner = ?;");
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
	
	public static boolean modifyBraceletUID(String pseudo, String braceletUID) throws SQLException, Exception {
		boolean modifyBraceletUID = false;
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE User SET braceletUID = ? WHERE pseudo = ?;");
			preparedStatement.setString( 1, braceletUID );
			preparedStatement.setString( 2, pseudo);					
			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				modifyBraceletUID = true;
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
		return modifyBraceletUID;
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
		int profileId = 0;
		try {
			dbConn = StorageService.createConnection();		
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT profile_id FROM Profile WHERE pseudo = ? AND profile_name = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, profileName);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()){
				profileId= rs.getInt("profile_id");				
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
		return profileId;
	}
	
	public static String retrievePictureNameFromTagId (String pseudo, String id ) throws Exception {
		Connection dbConn = null;
		String picture_name="";
		try {
			dbConn = StorageService.createConnection();		
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT picture_name FROM Tag WHERE pseudo_owner = ? AND tag_id = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, id);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()){
			picture_name= rs.getString("picture_name");		
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
		return picture_name;
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
	
	
	public static ArrayList<String> retrieveProfileNames(String pseudo, String password) throws Exception{
		Connection dbConn = null;
		ArrayList<String>result = new ArrayList<String>();
		try {
			dbConn = StorageService.createConnection();
			if(StorageService.checkLogin(pseudo, password)){
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT profile_name FROM Profile where pseudo = ?;");
			preparedStatement.setString( 1, pseudo );						
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("profile_name"));
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
	
	public static ArrayList<String> retrieveTagIDsFromProfileID(String pseudo, String password, int profileID) throws Exception{
		Connection dbConn = null;
		ArrayList<String>result = new ArrayList<String>();
		try {			
			dbConn = StorageService.createConnection();			
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT tag_id FROM Relation_profile_tag where profile_id = ?;");
			preparedStatement.setInt( 1, profileID );						
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("tag_id"));
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
	
	public static Tag retrieveTagFromTagID (String id ) throws Exception {
		Connection dbConn = null;
		Tag resultTag = new Tag("a","b");
		try {
			dbConn = StorageService.createConnection();		
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT tag_id, object_name, picture_version FROM Tag WHERE tag_id = ?;");
			preparedStatement.setString( 1, id );
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()){
				resultTag = mapTag(rs);
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
		return resultTag;
	}
	
	public static int retrieveProfileIDFromProfileName (String pseudo, String profileName ) throws Exception {
		Connection dbConn = null;
		int profileId = 0;
		try {
			dbConn = StorageService.createConnection();		
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT profile_id FROM Profile WHERE pseudo = ? AND profile_name = ?;");
			preparedStatement.setString( 1, pseudo );
			preparedStatement.setString( 2, profileName);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()){
				profileId= rs.getInt("profile_id");				
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
		return profileId;
	}
	
	public static Date retrieveLastTagsUpdateTime(String pseudo, String password) throws Exception{
		Connection dbConn = null;
		Date date = null;
		try {
			dbConn = StorageService.createConnection();
			if(StorageService.checkLogin(pseudo, password)){
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT tags_change_time FROM User where pseudo = ? ;");
			preparedStatement.setString( 1, pseudo );			
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				date = rs.getDate("tags_change_time");
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
		return date;
	}
	public static Date retrieveLastPersonalInformationUpdateTime(String pseudo, String password) throws Exception{
		Connection dbConn = null;
		Date date = null;
		try {
			dbConn = StorageService.createConnection();
			if(StorageService.checkLogin(pseudo, password)){
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT personal_information_change_time FROM User where pseudo = ? ;");
			preparedStatement.setString( 1, pseudo );			
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				date = rs.getDate("personal_information_change_time");
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
		return date;
	}
	public static Date retrieveLastProfilesUpdateTime(String pseudo, String password) throws Exception{
		Connection dbConn = null;
		Date date = null;
		try {
			dbConn = StorageService.createConnection();
			if(StorageService.checkLogin(pseudo, password)){
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("SELECT profiles_change_time FROM User where pseudo = ? ;");
			preparedStatement.setString( 1, pseudo );			
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				date = rs.getDate("profiles_change_time");
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
		return date;
	}
	
	public static void modifyLastTagsUpdateTime(String pseudo) throws SQLException, Exception {
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE User SET tags_change_time = NOW() WHERE pseudo = ?;");
			preparedStatement.setString( 1, pseudo );

			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
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
	}
	
	public static void modifyLastProfilesUpdateTime(String pseudo) throws SQLException, Exception {
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE User SET profiles_change_time = NOW() WHERE pseudo = ?;");
			preparedStatement.setString( 1, pseudo );

			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
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
	}

	public static void modifyLastPersonalInformationUpdateTime(String pseudo) throws SQLException, Exception {
		Connection dbConn = null;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE User SET personal_information_change_time = NOW() WHERE pseudo = ?;");
			preparedStatement.setString( 1, pseudo );

			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
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
	}
	
	public static boolean modifyImageTag(String id, InputStream picture) throws Exception {
		Connection dbConn = null;
		boolean modifyImageTag = false;
		try {
			dbConn = StorageService.createConnection();
			java.sql.PreparedStatement preparedStatement = dbConn.prepareStatement("UPDATE Tag SET picture = ? AND picture_version = (picture_version + 1) WHERE tag_id = ?;");
			preparedStatement.setBinaryStream( 1, picture);
			preparedStatement.setString(2,id);

			int records = preparedStatement.executeUpdate();
			//When record is successfully inserted
			if (records > 0) {
				modifyImageTag = true;
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
		return modifyImageTag;
	}
	
}