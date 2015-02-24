package com.app_server.interfaces;

import javax.ws.rs.QueryParam;

import com.app_server.data.Account;

public interface NetworkServiceInterface {
	
	// http://localhost:8080/app_server/ns/dologin?pseudo=abc&password=xyz
	public String doLogin(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws Exception;
	
	// http://localhost:8080/app_server/ns/doregister?pseudo=pqrs&password=abc&first_name=xyz&last_name=cdf&email=hij
	public String doLogin(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name, @QueryParam("email") String email);
	
	// http://localhost/<appln-folder-name>/tag/addtag?pseudo=abc&password=abc&object_name=xyz&picture=url
	public String addTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("object_name") String object_name, @QueryParam("picture") String picture) throws Exception;

	// http://localhost/<appln-folder-name>/tag/deletetag?pseudo=abc&password=abc&object_name=xyz
	public String deleteTag(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password, @QueryParam("object_name") String object_name) throws Exception;
	
	// http://localhost/<appln-folder-name>/tag/deletetag?pseudo=abc&password=abc&object_name=xyz
	public String retrieveTags(@QueryParam("pseudo") String pseudo, @QueryParam("password") String password) throws Exception;
	
	
	
	
}
