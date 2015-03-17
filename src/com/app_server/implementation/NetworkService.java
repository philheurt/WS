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
import com.app_server.engine.FieldVerifier;
import com.app_server.exceptions.ErrorCode;
import com.app_server.exceptions.IllegalFieldException;
import com.app_server.exceptions.TagCode;
import com.app_server.utilities.Utilities;

//Path: http://localhost/app_server/ns
@Path("/ns")
public class NetworkService {
	
	// HTTP Get Method
		@GET 
		// Path: http://92.222.33.38:8080/app_server/ns/login
		@Path("/login")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/login?pseudo=abc&password=xyz
		public String login(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws IllegalFieldException, Exception{
			Account account;
			JSONObject obj = new JSONObject();
			obj.put("tag", TagCode.LOGIN);
			if(!FieldVerifier.verifyName(pseudo)){
				obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
			}
			else 
				if(!FieldVerifier.verifyName(password)){
					obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
				}
				else 
					
			if(StorageService.checkLogin(pseudo,password)){
			account = StorageService.doLogin(pseudo, password);
			
				try {
					obj.put("returnCode", ErrorCode.NO_ERROR);
					obj.put("pseudo", account.getPseudo());
					obj.put("first_name", account.getFirstName());
					obj.put("last_name", account.getLastName());
					obj.put("email", account.getEMailAddress());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}				
			}else{	
				try {
					obj.put("returncode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}			
							
			}
			return obj.toString();
		}
		
		
		// HTTP Get Method
		@GET 
		// Path: http://92.222.33.38:8080/app_server/ns/register
		@Path("/register")  
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/register?pseudo=pqrs&password=abc&first_name=xyz&last_name=cdf&email=hij
		public String register(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name, @QueryParam("email") String email) throws JSONException{					
			int returnCode = 3;
			JSONObject obj = new JSONObject();
			obj.put("tag", TagCode.REGISTER);
			if(!FieldVerifier.verifyName(pseudo)){
				obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
			}
			else 
				if(!FieldVerifier.verifyName(password)){
					obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
				}
				else 
					if(!FieldVerifier.verifyName(first_name)){
						obj.put("returnCode", ErrorCode.MISSING_FIRST_NAME);
					}
					else 
						if(!FieldVerifier.verifyName(last_name)){
							obj.put("returnCode", ErrorCode.MISSING_LAST_NAME);
						}
						else 
							if(!FieldVerifier.verifyEMailAddress(email)){
								obj.put("returnCode", ErrorCode.MISSING_EMAIL);
							}
							else 
								
			if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){ // I still use my utilities not the mutual ones
				try {
					if(StorageService.insertUser(pseudo, password, first_name, last_name, email)){						
						returnCode = ErrorCode.NO_ERROR;
					}
				} catch(SQLException sqle){					
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						returnCode = ErrorCode.USER_ALREADY_REGISTERED;
					} 
					//When special characters are used in pseudo, password, first_name, last_name, email)
					else if(sqle.getErrorCode() == 1064){
						//System.out.println(sqle.getErrorCode());
						returnCode = ErrorCode.ILLEGAL_USE_OF_SPECIAL_CHARACTER;
					}
				}
				catch (Exception e) {					
					System.out.println("Inside doRegister catch e ");					
				}
			}else{
				System.out.println("Inside doRegister else");				
			}	
								
				try {
					
					obj.put("returncode", returnCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}			
			return obj.toString();
					
		}
						
	// HTTP Get Method
	@GET 					
	@Path("/addtag")
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/addtag?pseudo=abc&password=abc&object_name=xyz&picture=url
	public String addTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password,@QueryParam("id") String id, @QueryParam("object_name") String object_name, @QueryParam("picture") String picture) throws Exception{
		JSONObject obj = new JSONObject();
		obj.put("tag", TagCode.ADD_TAG);
		if(!FieldVerifier.verifyName(pseudo)){
			obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
		}
		else 
			if(!FieldVerifier.verifyName(password)){
				obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
			}
			else 
				if(!FieldVerifier.verifyTagUID(id)){
					obj.put("returnCode", ErrorCode.MISSING_TAG_ID);
				}
				else 
					if(!FieldVerifier.verifyTagName(object_name)){
						obj.put("returnCode", ErrorCode.MISSING_TAG_NAME);
					}
					else 
						
		if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(object_name)){
			if (StorageService.checkLogin(pseudo, password)){			
				if(StorageService.insertTag(id, pseudo, object_name, picture)){
					try {
						obj.put("returncode", ErrorCode.NO_ERROR);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}		
				}else{ // problem at the DB level
					try {
						obj.put("returncode", ErrorCode.DATABASE_ACCESS_ISSUE);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}	
				}
			}else{ // wrong pseudo/password combination
				try {
					obj.put("returncode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}	
			}
		}
		else { // information incomplete
			try {
				obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}	
		}
		
	return obj.toString();		
	}
	
	
	// HTTP Get Method
			@GET 
			// Path: http://92.222.33.38:8080/app_server/ns/deletetag
			@Path("/deletetag")
			// Produces JSON as response
			@Produces(MediaType.APPLICATION_JSON) 
			// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/deletetag?pseudo=abc&password=abc&object_name=xyz
			public String deleteTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("id") String id) throws Exception{
				JSONObject obj = new JSONObject();
				obj.put("tag", TagCode.DELETE_TAG);
				if(!FieldVerifier.verifyName(pseudo)){
					obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
				}
				else 
					if(!FieldVerifier.verifyName(password)){
						obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
					}
					else 
						if(!FieldVerifier.verifyTagUID(id)){
							obj.put("returnCode", ErrorCode.MISSING_TAG_ID);
						}
						else 
							
				if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){
					if (StorageService.checkLogin(pseudo, password)){
						if(StorageService.deleteTag(pseudo, id)){
							try {							
								obj.put("returncode", ErrorCode.NO_ERROR);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
							}	
						}else{ // issue at DB level
							try {
								obj.put("returncode", ErrorCode.DATABASE_ACCESS_ISSUE);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
							}	
						}
					}else{ // wrong pseudo/password combination
						try {
							obj.put("returncode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
						}	
					}
				}
				else { // information incomplete
					try {
						obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}	
				}
			return obj.toString();		
			}
					
							
		// HTTP Get Method
		@GET 
		// Path: http://92.222.33.38:8080/app_server/ns/retrievetag
		@Path("/retrievetag")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/retrievetag?pseudo=abc&password=abc
		public String retrieveTags(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws Exception{
			JSONObject obj = new JSONObject();
			obj.put("tag", TagCode.RETRIEVE_TAG);
			if(!FieldVerifier.verifyName(pseudo)){
				obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
			}
			else 
				if(!FieldVerifier.verifyName(password)){
					obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
				}
				else 
					
			if (StorageService.checkLogin(pseudo, password)){

				ArrayList<Tag> ListOfTag = StorageService.retrieveTags(pseudo, password);
				JSONArray arrayOfJsonTag = new JSONArray();

				for(Tag tag : ListOfTag){
					JSONObject tagJson = new JSONObject();
					try {
					tagJson.put("tag_id", tag.getUid());
					tagJson.put("object_name", tag.getObjectName());
					tagJson.put("picture", tag.getObjectImageName());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}	
					arrayOfJsonTag.put(tagJson);	
				}	
				obj.put("returncode", ErrorCode.NO_ERROR);
				obj.put("listTags", arrayOfJsonTag);

			}else{ // wrong pseudo/password combination
				obj.put("returncode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);				
			}
			return obj.toString();		
		}
		
		// HTTP Get Method
				@GET 
				// Path: http://92.222.33.38:8080/app_server/ns/modifyemail
				@Path("/modifyemail")
				// Produces JSON as response
				@Produces(MediaType.APPLICATION_JSON) 
				// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/modifyAccount?pseudo=abc&password=xyz&newPseudo=abc&newPassword=xyz&newFirstName=abc&newLastName=abc&newEmail=abc@xyz.com
				public String modifyEmail(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("new_email") String newEmail) throws Exception{
					JSONObject obj = new JSONObject();
					obj.put("tag", TagCode.MODIFY_EMAIL);
					Account account = null;
					if(!FieldVerifier.verifyName(pseudo)){
						obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
					}
					else 
						if(!FieldVerifier.verifyName(password)){
							obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
						}
						else 
							if(!FieldVerifier.verifyEMailAddress(newEmail)){
								obj.put("returnCode", ErrorCode.MISSING_EMAIL);
							}
							else 
							
					if(StorageService.checkLogin(pseudo,password)){
						account = StorageService.doLogin(pseudo, password);				
						if ((account.getEMailAddress()!=newEmail)&&(StorageService.modifyEMailAdress(pseudo, newEmail))){
							account.setMailAddress(newEmail);												
						try {
							obj.put("returncode",ErrorCode.NO_ERROR);		
							obj.put("email", account.getEMailAddress());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
						}
					
					}else{
							try {
								obj.put("returncode",ErrorCode.DATABASE_ACCESS_ISSUE);								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
							}									
						}
					}else{
					try {
						obj.put("returncode", ErrorCode.UNKNOWN_ERROR);								
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}			
				
				}
					return obj.toString();
				}	
				
				// HTTP Get Method
				@GET 
				// Path: http://92.222.33.38:8080/app_server/ns/modifypassword
				@Path("/modifypassword")
				// Produces JSON as response
				@Produces(MediaType.APPLICATION_JSON) 
				// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/modifypassword?pseudo=abc&password=xyz&newpassword=abc
				public String modifyPassword(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("new_password") String newPassword) throws Exception{
					JSONObject obj = new JSONObject();
					obj.put("tag", TagCode.MODIFY_OBJECT_NAME);
					
					if(!FieldVerifier.verifyName(pseudo)){
						obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
					}
					else 
						if(!FieldVerifier.verifyName(password)){
							obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
						}
						else 
							if(!FieldVerifier.verifyName(newPassword)){
								obj.put("returnCode", ErrorCode.MISSING_NEW_PASSWORD);
							}
							else 
					if(StorageService.checkLogin(pseudo,password)){
						if ((password != newPassword)&&(StorageService.modifyPassword(pseudo, newPassword))){
						
							try {
								obj.put("returncode",ErrorCode.NO_ERROR);										
							} catch (JSONException e) {
								// TODO Auto-generated catch block
							}
						}else{
							try {
								obj.put("returncode", ErrorCode.DATABASE_ACCESS_ISSUE);								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
							}									
						}
					}else{
					try {
						obj.put("returncode",ErrorCode.UNKNOWN_ERROR);								
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}			
				
				}
					return obj.toString();
				}	
				
				// HTTP Get Method
				@GET 
				// Path: http://92.222.33.38:8080/app_server/ns/modifypassword
				@Path("/modifyobjectname")
				// Produces JSON as response
				@Produces(MediaType.APPLICATION_JSON) 
				// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/modifypassword?pseudo=abc&password=xyz&newpassword=abc
				public String modifyObjectName(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("id") String id, @QueryParam("new_object_name") String newObjectName) throws Exception{
					int returnCode = 0;
					JSONObject obj = new JSONObject();
					obj.put("tag", TagCode.MODIFY_OBJECT_NAME);
					
					if(!FieldVerifier.verifyName(pseudo)){
						obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
					}
					else 
						if(!FieldVerifier.verifyName(password)){
							obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
						}
						else 
							if(!FieldVerifier.verifyTagUID(id)){
								obj.put("returnCode", ErrorCode.MISSING_TAG_ID);
							}
							else 
								if(!FieldVerifier.verifyTagName(newObjectName)){
									obj.put("returnCode", ErrorCode.MISSING_NEW_OBJECT_NAME);
								}
								else 
							
					if(StorageService.checkLogin(pseudo,password)){										
						if (StorageService.modifyTagName(id, newObjectName))
						{						
						try {
							obj.put("returncode", ErrorCode.NO_ERROR);		
							obj.put("newobjectname", newObjectName);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
						}												
						}else{
							try {
								obj.put("returncode",ErrorCode.DATABASE_ACCESS_ISSUE);								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
							}									
						}
					}else{
					try {
						obj.put("returncode",ErrorCode.UNKNOWN_ERROR);								
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}			
				
				}
					return obj.toString();
				}	
				
}
