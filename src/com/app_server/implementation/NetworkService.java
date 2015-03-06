package com.app_server.implementation;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.app_server.data.Account;
import com.app_server.data.Tag;
import com.app_server.exceptions.IllegalFieldException;
import com.app_server.utilities.Utilities;

//Path: http://localhost/app_server/ns
@Path("/ns")
public class NetworkService {
	
	// HTTP Get Method
		@GET 
		// Path: http://92.222.33.38:8080/app_server/ns/dologin
		@Path("/dologin")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/dologin?pseudo=abc&password=xyz
		public String doLogin(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws IllegalFieldException, Exception{
			String response = "";
			Account account;
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
		
		
		// HTTP Get Method
		@GET 
		// Path: http://92.222.33.38:8080/app_server/ns/doregister
		@Path("/doregister")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/doregister?pseudo=pqrs&password=abc&first_name=xyz&last_name=cdf&email=hij
		public String doRegister(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name, @QueryParam("email") String email){
			String response = "";
			//System.out.println("Inside doRegister "+pseudo+"  "+password);
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
			//System.out.println("Inside registerUser");
			int result = 3;
			if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){ // I still use my utilities not the mutual ones
				try {
					if(StorageService.insertUser(pseudo, password, first_name, last_name, email)){						
						result = 0;
					}
				} catch(SQLException sqle){					
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						result = 1;
					} 
					//When special characters are used in pseudo, password, first_name, last_name, email)
					else if(sqle.getErrorCode() == 1064){
						//System.out.println(sqle.getErrorCode());
						result = 2;
					}
				}
				catch (Exception e) {					
					System.out.println("Inside registerUser catch e ");
					result = 3;
				}
			}else{
				System.out.println("Inside registerUser else");
				result = 3;
			}
				
			return result;
		}
		
	// HTTP Get Method
	@GET 					
	@Path("/addtag")
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/addtag?pseudo=abc&password=abc&object_name=xyz&picture=url
	public String addTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("object_name") String object_name, @QueryParam("picture") String picture) throws Exception{
		String response = "";
		if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(object_name)&&Utilities.isNotNull(picture)){
			if (StorageService.checkLogin(pseudo, password)){			
				if(StorageService.insertTag(pseudo, object_name, picture)){
					response = Utilities.constructJSON("addtag",true);
				}else{
					response = Utilities.constructJSON("addtag", false, "A problem has occured");
				}
			}else{
				response = Utilities.constructJSON("addtag", false, "Wrong combination pseudo/password");
			}
		}
		else {
			response = Utilities.constructJSON("addtag", false, "Information incomplete");
		}
		
	return response;		
	}
	
	
	// HTTP Get Method
			@GET 
			// Path: http://92.222.33.38:8080/app_server/ns/deletetag
			@Path("/deletetag")
			// Produces JSON as response
			@Produces(MediaType.APPLICATION_JSON) 
			// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/deletetag?pseudo=abc&password=abc&object_name=xyz
			public String deleteTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("object_name") String object_name) throws Exception{
				String response = "";
				if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(object_name)){
					if (StorageService.checkLogin(pseudo, password)){
						if(StorageService.deleteTag(pseudo, object_name)){
							response = Utilities.constructJSON("deletetag",true);
						}else{
							response = Utilities.constructJSON("deletetag", false, "A problem has occured");
						}
					}else{
						response = Utilities.constructJSON("deletetag", false, "Wrong combination pseudo/password");
					}
				}
				else {
					response = Utilities.constructJSON("addtag", false, "Information incomplete");
				}
			return response;		
			}
					
							
		// HTTP Get Method
		@GET 
		// Path: http://92.222.33.38:8080/app_server/ns/retrievetag
		@Path("/retrievetag")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/retrievetag?pseudo=abc&password=abc
		public String retrieveTags(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws Exception{
			JSONObject response = new JSONObject();
			response.put("tag", "retrieveTags");
			if (StorageService.checkLogin(pseudo, password)){

				ArrayList<Tag> ListOfTag = StorageService.retrieveTags(pseudo, password);
				JSONArray arrayOfJsonTag = new JSONArray();

				for(Tag tag : ListOfTag){
					JSONObject tagJson = new JSONObject();
					tagJson.put("tagID", tag.getUid());
					tagJson.put("nameTag", tag.getObjectName());
					tagJson.put("picture", tag.getObjectImageName());
					arrayOfJsonTag.put(tagJson);	
				}					
				response.put("status", true);
				response.put("listTags", arrayOfJsonTag);

			}else{
				response.put("status", false);
				response.put("err_msg", "Wrong combination pseudo/password");
			}
			return response.toString();		
		}
		
		// HTTP Get Method
				@GET 
				// Path: http://92.222.33.38:8080/app_server/ns/modifyemail
				@Path("/modifyemail")
				// Produces JSON as response
				@Produces(MediaType.APPLICATION_JSON) 
				// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/modifyAccount?pseudo=abc&password=xyz&newPseudo=abc&newPassword=xyz&newFirstName=abc&newLastName=abc&newEmail=abc@xyz.com
				public String modifyAccount(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("newEmail") String newEmail) throws Exception{
					String response = "";
					boolean status = true;
					Account account = null;
					if(StorageService.checkLogin(pseudo,password)){
						account = StorageService.doLogin(pseudo, password);				
						if ((account.getEMailAddress()!=newEmail)&&(StorageService.modifyEMailAdress(pseudo, newEmail)))
							account.setMailAddress(newEmail);
						
						JSONObject obj = new JSONObject();
						try {
							obj.put("tag", "login");
							obj.put("status",status);		
							obj.put("email", account.getEMailAddress());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
						}
						return obj.toString();	
					}else{
						response = Utilities.constructJSON("modifyAccount", false, "Incorrect Pseudo/Password");
						return response;
					}	
				}			
}
