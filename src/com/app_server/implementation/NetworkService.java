package com.app_server.implementation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

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

//Path: http://92.222.33.38:8080/app_server/ns
@Path("/ns")
public class NetworkService {
	
	// HTTP Get Method
		@GET 
		// Path: http://92.222.33.38:8080/app_server/ns/login
		@Path("/login")
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/login?pseudo=abc&password=xyz
		public String login(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws IllegalFieldException, Exception, JSONException{
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
			
					obj.put("returnCode", ErrorCode.NO_ERROR);
					obj.put("pseudo", account.getPseudo());
					obj.put("first_name", account.getFirstName());
					obj.put("last_name", account.getLastName());
					obj.put("email", account.getEMailAddress());
			
			}else{	
					obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);			
							
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
		public String register(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name, @QueryParam("email") String email) throws Exception{					
			int returnCode = ErrorCode.DATABASE_ACCESS_ISSUE;
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
										
				try {
					if(StorageService.insertUser(pseudo, password, first_name, last_name, email)){						
						returnCode = ErrorCode.NO_ERROR;
					}else{
						returnCode = ErrorCode.DATABASE_ACCESS_ISSUE;			
						
					}
				} catch(SQLException sqle){					
					//When Primary key violation occurs that means user is already registered
					if(sqle.getErrorCode() == 1062){
						returnCode = ErrorCode.USER_ALREADY_REGISTERED;
					} 
					//When special characters are used in pseudo, password, first_name, last_name, email)
					else if(sqle.getErrorCode() == 1064){
						returnCode = ErrorCode.ILLEGAL_USE_OF_SPECIAL_CHARACTER;
					}
				}
													
					obj.put("returncode", returnCode);		
			return obj.toString();
					
		}
	
	// HTTP Get Method
	@GET 					
	@Path("/addtag")
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/addtag?pseudo=abc&password=abc&object_name=xyz&picture=url
		public String addTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password,@QueryParam("id") String id, @QueryParam("object_name") String object_name, @QueryParam("picture") String picture) throws Exception, JSONException{
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
				if(StorageService.insertTag(id, pseudo, object_name, picture, null)){
						obj.put("returnCode", ErrorCode.NO_ERROR);		
				}else{ // problem at the DB level
						obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);	
				}
			}else{ // wrong pseudo/password combination
					obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
			}
		}
		else { // information incomplete
				obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);	
		}
		
	return obj.toString();		
	}
	
	
	
	// HTTP POST Method
	@POST 					
	@Path("/addtagwithphoto")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/addtag?pseudo=abc&password=abc&object_name=xyz&picture=url
		public String addTagWithPhoto( @FormDataParam("file") InputStream picture, @FormDataParam("pseudo") String pseudo, @FormDataParam("password") String password,@FormDataParam("tagUID") String tagUID, @FormDataParam("object_name") String object_name, @FormDataParam("picture_name") String picture_name) throws Exception, JSONException{
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
				if(!FieldVerifier.verifyTagUID(tagUID)){
					obj.put("returnCode", ErrorCode.MISSING_TAG_ID);
				}
				else 
					if(!FieldVerifier.verifyTagName(object_name)){
						obj.put("returnCode", ErrorCode.MISSING_TAG_NAME);
					}
					else
						if(!FieldVerifier.verifyName(picture_name)){
							obj.put("returnCode", ErrorCode.MISSING_TAG_PICTURE_NAME);
						}
						else 
						
		if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(object_name)){
			if (StorageService.checkLogin(pseudo, password)){			
				if(StorageService.insertTag(tagUID, pseudo, object_name, picture_name, picture)){
						obj.put("returnCode", ErrorCode.NO_ERROR);		
				}else{ // problem at the DB level
						obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);	
				}
			}else{ // wrong pseudo/password combination
					obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
			}
		}
		else { // information incomplete
				obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);	
		}
		
	return obj.toString();		
	}
	
	// HTTP Get Method
				@GET 
				// Path: http://92.222.33.38:8080/app_server/ns/deletetag
				@Path("/downloadimagetag")
				@Produces(MediaType.APPLICATION_OCTET_STREAM) 
				// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/deletetag?pseudo=abc&password=abc&object_name=xyz
		public Response downloadImageTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("id") String id) throws Exception, JSONException{				
					ResponseBuilder response = null;
					String picture_name = "";
					if(!FieldVerifier.verifyName(pseudo)){
						
					}
					else 
						if(!FieldVerifier.verifyName(password)){
							
						}
						else 
							if(!FieldVerifier.verifyTagUID(id)){
								
							}
							else 
								if (StorageService.checkLogin(pseudo, password)){
									picture_name = StorageService.retrievePictureNameFromTagId(pseudo, id);
									InputStream in = StorageService.downloadImageTag(pseudo, id);
									ByteArrayOutputStream out = new ByteArrayOutputStream();
							        int data = in.read();
							        while (data >= 0) {
							          out.write((char) data);
							          data = in.read();
							        } 
							        out.flush();	
							        response = Response.ok(out.toByteArray());
							        ((ResponseBuilder) response).header("Content-Disposition",
											"attachment; filename="+picture_name+".jpg");

								}else{ // wrong pseudo/password combination
										
							}
				return response.build();		
				}
	
	// HTTP Get Method
			@GET 
			// Path: http://92.222.33.38:8080/app_server/ns/deletetag
			@Path("/deletetag")
			// Produces JSON as response
			@Produces(MediaType.APPLICATION_JSON) 
			// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/deletetag?pseudo=abc&password=abc&object_name=xyz
		public String deleteTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("id") String id) throws Exception, JSONException{
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
						if(StorageService.deleteTag(id)){						
								obj.put("returnCode", ErrorCode.NO_ERROR);	
						}else{ // issue at DB level
								obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);
						}
					}else{ // wrong pseudo/password combination
							obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
					}
				}
				else { // information incomplete
						obj.put("returnCode", ErrorCode.INFORMATION_INCOMPLETE);	
				}
			return obj.toString();		
			}
			
			
			
			// HTTP Get Method
						@GET 
						// Path: http://92.222.33.38:8080/app_server/ns/removetagfromprofile
						@Path("/removetagfromprofile")
						// Produces JSON as response
						@Produces(MediaType.APPLICATION_JSON) 
						// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/removetagfromprofile?pseudo=abc&password=abc&id=xyz&profile_name=abc
					public String removeTagFromProfile(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("id") String id, @QueryParam("profile_name") String profileName) throws Exception, JSONException{
							JSONObject obj = new JSONObject();
							obj.put("removetagfromprofile", TagCode.DELETE_TAG_FROM_PROFILE);
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
										if(!FieldVerifier.verifyName(profileName)){
											obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
										}
										
							if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){
								if (StorageService.checkLogin(pseudo, password)){
									//traitement d'erreur ?
									int profileID = StorageService.getProfileID(pseudo, profileName);
									if(StorageService.deleteTagFromProfile(pseudo, profileID, id)){						
											obj.put("returnCode", ErrorCode.NO_ERROR);	
									}else{ // issue at DB level
											obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);
									}
								}else{ // wrong pseudo/password combination
										obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
								}
							}
							else { // information incomplete
									obj.put("returnCode", ErrorCode.INFORMATION_INCOMPLETE);	
							}
						return obj.toString();		
						}
						
						
						
						
						
						// HTTP Get Method
						@GET 
						// Path: http://92.222.33.38:8080/app_server/ns/removeprofile
						@Path("/removeprofile")
						// Produces JSON as response
						@Produces(MediaType.APPLICATION_JSON) 
						// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/removeprofile?pseudo=abc&password=abc&profile_name=abc
					public String removeProfile(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("profile_name") String profileName) throws Exception, JSONException{
							JSONObject obj = new JSONObject();
							obj.put("removeprofile", TagCode.DELETE_PROFILE);
							if(!FieldVerifier.verifyName(pseudo)){
								obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
							}
							else 
								if(!FieldVerifier.verifyName(password)){
									obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
								}
								else 
										if(!FieldVerifier.verifyName(profileName)){
											obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
										}
										
							if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){
								if (StorageService.checkLogin(pseudo, password)){
									//traitement d'erreur ?
									int profileID = StorageService.getProfileID(pseudo, profileName);
									if(StorageService.deleteProfile(profileID)){						
											obj.put("returnCode", ErrorCode.NO_ERROR);	
									}else{ // issue at DB level
											obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);
									}
								}else{ // wrong pseudo/password combination
										obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
								}
							}
							else { // information incomplete
									obj.put("returnCode", ErrorCode.INFORMATION_INCOMPLETE);	
							}
						return obj.toString();		
						}
						
						
						
						
						
						
						// HTTP Get Method
						@GET 
						// Path: http://92.222.33.38:8080/app_server/ns/modifyprofilename
						@Path("/modifyprofilename")
						// Produces JSON as response
						@Produces(MediaType.APPLICATION_JSON) 
						// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/modifyprofilename?pseudo=abc&password=abc&id=xyz&profile_name=abc&new_profile_name=abc
					public String modifyProfileName(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("profile_name") String profileName, @QueryParam("new_profile_name") String newProfileName) throws Exception, JSONException{
							JSONObject obj = new JSONObject();
							obj.put("modifyprofilename", TagCode.MODIFY_PROFILE_NAME);
							if(!FieldVerifier.verifyName(pseudo)){
								obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
							}
							else 
								if(!FieldVerifier.verifyName(password)){
									obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
								}
								else 
									if(!FieldVerifier.verifyName(profileName)){
										obj.put("returnCode", ErrorCode.MISSING_TAG_ID);
									}
									else 
										if(!FieldVerifier.verifyName(newProfileName)){
											obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
										}
										
							if(Utilities.isNotNull(pseudo) && Utilities.isNotNull(password)){
								if (StorageService.checkLogin(pseudo, password)){
									//traitement d'erreur ?
									int profileID = StorageService.getProfileID(pseudo, profileName);
									if(StorageService.updateProfileName(profileID, newProfileName)){						
											obj.put("returnCode", ErrorCode.NO_ERROR);	
									}else{ // issue at DB level
											obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);
									}
								}else{ // wrong pseudo/password combination
										obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
								}
							}
							else { // information incomplete
									obj.put("returnCode", ErrorCode.INFORMATION_INCOMPLETE);	
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
		public String retrieveTags(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws Exception, JSONException{
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
					tagJson.put("picture_name", tag.getObjectImageName());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}	
					arrayOfJsonTag.put(tagJson);	
				}	
				obj.put("returnCode", ErrorCode.NO_ERROR);
				obj.put("listTags", arrayOfJsonTag);

			}else{ // wrong pseudo/password combination
				obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);				
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
		public String modifyEmail(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("new_email") String newEmail) throws Exception, JSONException{
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
				obj.put("returnCode",ErrorCode.NO_ERROR);		
				obj.put("email", account.getEMailAddress());					
		}else{
					obj.put("returnCode",ErrorCode.DATABASE_ACCESS_ISSUE);																
			}
		}else{
			obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);											
	
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
		public String modifyPassword(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("new_password") String newPassword) throws Exception, JSONException{
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
			
					obj.put("returnCode",ErrorCode.NO_ERROR);										
			}else{
					obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);																	
			}
		}else{
			obj.put("returnCode",ErrorCode.UNKNOWN_ERROR);											
	
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
		public String modifyObjectName(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("id") String id, @QueryParam("new_object_name") String newObjectName) throws Exception, JSONException{
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
			if (StorageService.modifyTagName(id, newObjectName, pseudo))
			{						
				obj.put("returnCode", ErrorCode.NO_ERROR);		
				obj.put("newobjectname", newObjectName);												
			}else{
					obj.put("returnCode",ErrorCode.DATABASE_ACCESS_ISSUE);																	
			}
		}else{
			obj.put("returnCode",ErrorCode.UNKNOWN_ERROR);											
	
	}
		return obj.toString();
	}	
	
	// HTTP Get Method
	@GET 
	// Path: http://92.222.33.38:8080/app_server/ns/createprofile
	@Path("/createprofile")
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/createprofile?pseudo=abc&password=xyz&profile_name=abc
		public String createProfile(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("profile_name") String profileName) throws Exception, JSONException{
		JSONObject obj = new JSONObject();
		obj.put("tag", TagCode.CREATE_PROFILE);
		if(!FieldVerifier.verifyName(pseudo)){
			obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
		}
		else 
			if(!FieldVerifier.verifyName(password)){
				obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
			}
			else 
				if(!FieldVerifier.verifyEMailAddress(profileName)){
					obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
				}
				else 
				
		if(StorageService.checkLogin(pseudo,password)){			
			if (StorageService.insertProfile(pseudo, profileName)){
				obj.put("returnCode", ErrorCode.NO_ERROR);	
			}else{
					obj.put("returnCode",ErrorCode.DATABASE_ACCESS_ISSUE);																
			}
		}else{
			obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);											
	
	}
		return obj.toString();
	}	
	
	// HTTP Get Method
	@GET 					
	@Path("/addtagtoprofile")
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/addtag?pseudo=abc&password=abc&profile_name=xyz&id=abc
		public String addTagToProfile(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password,@QueryParam("profile_name") String profileName, @QueryParam("id") String id) throws Exception, JSONException{
		JSONObject obj = new JSONObject();
		obj.put("tag", TagCode.ADD_TAG_TO_PROFILE);
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
						if(!FieldVerifier.verifyName(profileName)){
							obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
						}
						else 
						
		if(Utilities.isNotNull(pseudo) &&Utilities.isNotNull(profileName)){
			if (StorageService.checkLogin(pseudo, password)){			
				if(StorageService.insertTagToProfile(pseudo, profileName, id)){
						obj.put("returnCode", ErrorCode.NO_ERROR);
						ArrayList<Tag> ListOfTag = StorageService.retrieveTagsFromProfile(pseudo, password, profileName);
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
							obj.put("listTags", arrayOfJsonTag);
						}	
				}else{ // problem at the DB level
						obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);	
				}
			}else{ // wrong pseudo/password combination
					obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
			}
		}
		else { // information incomplete
				obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);	
		}
		
	return obj.toString();		
	}
	
	// HTTP POST Method
		@POST 					
		@Path("/addtagstoprofile")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
		// Produces JSON as response
		@Produces(MediaType.APPLICATION_JSON) 
		// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/addtagstoprofile
			public String addTagsToProfile( @FormDataParam("pseudo") String pseudo, @FormDataParam("password") String password,@FormDataParam("profileName") String profileName, @FormDataParam("jsonUIDs") String jsonUIDs) throws Exception, JSONException{
			JSONObject obj = new JSONObject();
			obj.put("tag", TagCode.ADD_TAG);
			
			ArrayList<String> listUIDs = new ArrayList<String>();
			
			//on interprète le json pour remplir listUIDs
			JSONObject jsonTemp = new JSONObject(jsonUIDs);
			int i = 0;
			//on utilise que JSON.get(missing key) = NULL
			while (Utilities.isNotNull((String) jsonTemp.get(Integer.toString(i)))) {
				listUIDs.add((String) jsonTemp.get(Integer.toString(i)));
				i++;
			}
			
			boolean bool = false; 
			for(int i1=0; i1<listUIDs.size(); i1++) {
				if(!FieldVerifier.verifyTagUID(listUIDs.get(i1))){
					bool = true;
				}
			}
			
			if(!FieldVerifier.verifyName(pseudo)){
				obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
			}
			else 
				if(!FieldVerifier.verifyName(password)){
					obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
				}
				else 
					if (bool) {
						obj.put("returnCode", ErrorCode.MISSING_TAG_ID);
					}
					else
						if(!FieldVerifier.verifyName(profileName)){
							obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
						}
						else				
			if(Utilities.isNotNull(pseudo)){
				if (StorageService.checkLogin(pseudo, password)){			
					//on effecctue l'insertion et un bool2 à côté pour le message d'erreur, qu'on fait après pour conserver la chaîne de if/else
					boolean bool2 = false; 
					for(int i1=0; i1<listUIDs.size(); i1++) {
						if(StorageService.insertTagToProfile(pseudo, profileName, listUIDs.get(i))){
							bool2 = true;
						}
					}
					if(bool2){
							obj.put("returnCode", ErrorCode.NO_ERROR);	
							ArrayList<Tag> ListOfTag = StorageService.retrieveTagsFromProfile(pseudo, password, profileName);
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
								obj.put("listTags", arrayOfJsonTag);
							}	
					}else{ // problem at the DB level
							obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);	
					}
				}else{ // wrong pseudo/password combination
						obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
				}
			}
			else { // information incomplete
					obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);	
			}
			
		return obj.toString();		
		}
		
		
		
		
		// HTTP POST Method
				@POST 					
				@Path("/removetagsfromprofile")
				@Consumes(MediaType.MULTIPART_FORM_DATA)
				// Produces JSON as response
				@Produces(MediaType.APPLICATION_JSON) 
				// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/addtagstoprofile
					public String removeTagsFromProfile( @FormDataParam("pseudo") String pseudo, @FormDataParam("password") String password,@FormDataParam("profileName") String profileName, @FormDataParam("jsonUIDs") String jsonUIDs) throws Exception, JSONException{
					JSONObject obj = new JSONObject();
					obj.put("tag", TagCode.DELETE_TAGS_FROM_PROFILE);
					
					ArrayList<String> listUIDs = new ArrayList<String>();
					
					//on interprète le json pour remplir listUIDs
					JSONObject jsonTemp = new JSONObject(jsonUIDs);
					int i = 0;
					//on utilise que JSON.get(missing key) = NULL
					while (Utilities.isNotNull((String) jsonTemp.get(Integer.toString(i)))) {
						listUIDs.add((String) jsonTemp.get(Integer.toString(i)));
						i++;
					}
					
					//contrôle d'erreur : avec bool est false si au moins un est false
					boolean bool = false; 
					for(int i1=0; i1<listUIDs.size(); i1++) {
						if(!FieldVerifier.verifyTagUID(listUIDs.get(i1))){
							bool = true;
						}
					}
					
					if(!FieldVerifier.verifyName(pseudo)){
						obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
					}
					else 
						if(!FieldVerifier.verifyName(password)){
							obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
						}
						else 
							if (bool) {
								obj.put("returnCode", ErrorCode.MISSING_TAG_ID);
							}
							else
								if(!FieldVerifier.verifyName(profileName)){
									obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
								}
								else				
					if(Utilities.isNotNull(pseudo)){
						if (StorageService.checkLogin(pseudo, password)){			
							//on effecctue l'insertion et un bool2 à côté pour le message d'erreur, qu'on fait après pour conserver la chaîne de if/else
							boolean bool2 = false; 
							for(int i1=0; i1<listUIDs.size(); i1++) {
								if(StorageService.deleteTagFromProfile(pseudo, StorageService.getProfileID(pseudo, profileName), listUIDs.get(i))){
									bool2 = true;
								}
							}
							if(bool2){
									obj.put("returnCode", ErrorCode.NO_ERROR);	
									ArrayList<Tag> ListOfTag = StorageService.retrieveTagsFromProfile(pseudo, password, profileName);
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
										obj.put("listTags", arrayOfJsonTag);
									}	
							}else{ // problem at the DB level
									obj.put("returnCode", ErrorCode.DATABASE_ACCESS_ISSUE);	
							}
						}else{ // wrong pseudo/password combination
								obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);	
						}
					}
					else { // information incomplete
							obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);	
					}
					
				return obj.toString();		
				}
		
		
		
		
	
	
	// HTTP Get Method
	@GET 					
	@Path("/retrieveprofile")
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://92.222.33.38:8080/app_server/ns/retrieveprofile?pseudo=abc&password=abc&profile_name=xyz
		public String retrieveProfile(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password,@QueryParam("profile_name") String profileName) throws Exception, JSONException{
		JSONObject obj = new JSONObject();
		obj.put("tag", TagCode.RETRIEVE_PROFILE);
		if(!FieldVerifier.verifyName(pseudo)){
			obj.put("returnCode", ErrorCode.MISSING_PSEUDO);
		}
		else						
			if(!FieldVerifier.verifyName(password)){
				obj.put("returnCode", ErrorCode.MISSING_PASSWORD);
			}
			else 
					if(!FieldVerifier.verifyName(profileName)){
						obj.put("returnCode", ErrorCode.MISSING_PROFILE_NAME);
					}
					else 
						
		if(Utilities.isNotNull(pseudo) &&Utilities.isNotNull(profileName)){
			if (StorageService.checkLogin(pseudo, password)){							
						obj.put("returnCode", ErrorCode.NO_ERROR);
						obj.put("profileName", profileName);
						ArrayList<Tag> ListOfTag = StorageService.retrieveTagsFromProfile(pseudo, password, profileName);
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
							obj.put("listTags", arrayOfJsonTag);
						}		
				
			}else{ // wrong pseudo/password combination
					obj.put("returnCode", ErrorCode.INVALID_PSEUDO_PASSWORD_COMBINATION);						
			}
		}else { // information incomplete
			obj.put("returncode", ErrorCode.INFORMATION_INCOMPLETE);	
		}
		
	return obj.toString();		
	}
	
}
