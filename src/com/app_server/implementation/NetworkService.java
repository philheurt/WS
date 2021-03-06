package com.app_server.implementation;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


import org.json.simple.JSONArray;

import com.app_server.data.Account;
import com.app_server.data.Tag;
import com.app_server.interfaces.NetworkServiceInterface;
import com.app_server.utilities.Utilities;

//Path: http://localhost/app_server/ns
@Path("/ns")
public class NetworkService implements NetworkServiceInterface {
	
	// HTTP Get Method
		@GET 
		// Path: http://localhost:8080/app_server/ns/dologin
		@Path("/dologin")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost:8080/app_server/ns/dologin?pseudo=abc&password=xyz
		public String doLogin(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws Exception{
			String response = "";
			Account account = new Account("a","b","c","d");
			if(StorageService.checkLogin(pseudo,password)){
				account = StorageService.doLogin(pseudo, password);
				JSONObject obj = new JSONObject();
				try {
					obj.put("tag", "login");
					obj.put("status",true);
					obj.put("pseudo", account.getPseudo());
					obj.put("first_name", account.getFirstName());
					obj.put("last_name", account.getLastName());
					obj.put("email", account.getEMailAddress());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}
				return obj.toString();	
			}else{
				response = Utilities.constructJSON("login", false, "Incorrect Email or Password");
				return response;
			}	
		}
		
		/**
		 * Method to check whether the entered credential is valid
		 * 
		 * @param pseudo
		 * @param password
		 * @return
		 */
		private Account checkCredentials(String pseudo, String password){
			// System.out.println("Inside checkCredentials");

			Account account = new Account("a","b","c","d");
			
			if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){
				try {
					account = StorageService.doLogin(pseudo, password);
					//System.out.println("Inside checkCredentials try "+result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//System.out.println("Inside checkCredentials catch");
				}
			}else{
				//System.out.println("Inside checkCredentials else");
			}
				
			return account;
		}
		
		// HTTP Get Method
		@GET 
		// Path: http://localhost:8080/app_server/ns/doregister
		@Path("/doregister")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://localhost:8080/app_server/ns/doregister?pseudo=pqrs&password=abc&first_name=xyz&last_name=cdf&email=hij
		public String doLogin(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name, @QueryParam("email") String email){
			String response = "";
			//System.out.println("Inside doregister "+pseudo+"  "+password);
			int retCode = registerUser(pseudo, password, first_name, last_name, email);
			if(retCode == 0){
				response = Utilities.constructJSON("register",true);
			}else if(retCode == 1){
				response = Utilities.constructJSON("register",false, "You are already registered");
			}else if(retCode == 2){
				response = Utilities.constructJSON("register",false, "Special Characters are not allowed in Pseudo and Password");
			}else if(retCode == 3){
				response = Utilities.constructJSON("register",false, "Error occured");
			}
			return response;
					
		}
		
		private int registerUser(String pseudo, String password, String first_name, String last_name, String email){
			System.out.println("Inside registerUser");
			int result = 3;
			if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){ // I still use my utilities not the mutual ones
				try {
					if(StorageService.insertUser(pseudo, password, first_name, last_name, email)){
						// System.out.println("RegisterUSer if");
						result = 0;
					}
				} catch(SQLException sqle){
					System.out.println("RegisterUSer catch sqle");
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						result = 1;
					} 
					//When special characters are used in pseudo, password, first_name, last_name, email)
					else if(sqle.getErrorCode() == 1064){
						System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Inside registerUser catch e ");
					result = 3;
				}
			}else{
				System.out.println("Inside registerUser else");
				result = 3;
			}
				
			return result;
		}
		
	//Path: http://localhost/app_server/
		// HTTP Get Method
			@GET 
			// Path: http://localhost/app_server/addtag
			@Path("/addtag")
			// Produces JSON as response
			@Produces(MediaType.APPLICATION_JSON) 
			// Query parameters are parameters: http://localhost/<appln-folder-name>/tag/addtag?pseudo=abc&password=abc&object_name=xyz&picture=url
			public String addTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("object_name") String object_name, @QueryParam("picture") String picture) throws Exception{
				String response = "";
				if (StorageService.checkLogin(pseudo, password)){
					if(checkCredentials(pseudo, object_name, picture)){
						response = Utilities.constructJSON("addtag",true);
					}else{
						response = Utilities.constructJSON("addtag", false, "A problem has occured");
					}
				}else{
					response = Utilities.constructJSON("addtag", false, "Wrong combination pseudo/password");
				}
				
			return response;		
			}
			
			private boolean checkCredentials(String pseudo, String object_name, String picture){
				System.out.println("Inside checkCredentials");
				boolean result = false;
				if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(object_name)&&Utilities.isNotNull(picture)){
					try {
						result = StorageService.insertTag(pseudo, object_name, picture);
						//System.out.println("Inside checkCredentials try "+result);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//System.out.println("Inside checkCredentials catch");
						result = false;
					}
				}else{
					//System.out.println("Inside checkCredentials else");
					result = false;
				}
					
				return result;
			}
			
			// HTTP Get Method
					@GET 
					// Path: http://localhost/<appln-folder-name>/tag/deletetag
					@Path("/deletetag")
					// Produces JSON as response
					@Produces(MediaType.APPLICATION_JSON) 
					// Query parameters are parameters: http://localhost/<appln-folder-name>/tag/deletetag?pseudo=abc&password=abc&object_name=xyz
					public String deleteTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("object_name") String object_name) throws Exception{
						String response = "";
						if (StorageService.checkLogin(pseudo, password)){
							if(checkDeleteTag(pseudo, object_name)){
								response = Utilities.constructJSON("deletetag",true);
							}else{
								response = Utilities.constructJSON("deletetag", false, "A problem has occured");
							}
						}else{
							response = Utilities.constructJSON("deletetag", false, "Wrong combination pseudo/password");
						}
					return response;		
					}
					
					private boolean checkDeleteTag(String pseudo, String object_name){
						System.out.println("Inside checkDeleteTag");
						boolean result = false;
						if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(object_name)){
							try {
								result = StorageService.deleteTag(pseudo, object_name);
								//System.out.println("Inside checkDeleteTag try "+result);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								//System.out.println("Inside checkDeleteTag catch");
								result = false;
							}
						}else{
							//System.out.println("Inside checkDeleteTag else");
							result = false;
						}
							
						return result;
					}
					
					// HTTP Get Method
					@SuppressWarnings("unchecked")
					@GET 
					// Path: http://localhost/<appln-folder-name>/tag/deletetag
					@Path("/retrievetag")
					// Produces JSON as response
					@Produces(MediaType.APPLICATION_JSON) 
					// Query parameters are parameters: http://localhost/<appln-folder-name>/tag/deletetag?pseudo=abc&password=abc&object_name=xyz
					public String retrieveTags(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws Exception{
						String response = "";
						if (StorageService.checkLogin(pseudo, password)){
							
							ArrayList<Tag> ListOfTag = StorageService.retrieveTags(pseudo, password);
							JSONArray arrayOfJsonTag = new JSONArray();
							
							for(Tag tag : ListOfTag){
								JSONObject tagJson = new JSONObject();
								tagJson.put("tagID", tag.getUid());
								tagJson.put("nameTag", tag.getObjectName());
								tagJson.put("picture", tag.getObjectImageName());
								arrayOfJsonTag.add(tagJson);	
							}
							JSONObject reponse = new JSONObject();
							
							reponse.put("tag", "retrieveTags");
							reponse.put("status", true);
							reponse.put("listTags", arrayOfJsonTag);
							
						}else{
							response = Utilities.constructJSON("retrieveTags", false, "Wrong combination pseudo/password");
						}
					return response.toString();		
					}
}
