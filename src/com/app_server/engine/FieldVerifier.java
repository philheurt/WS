package com.app_server.engine;


/**
 * This class provides methods to verify if fields like passwords or e-mail addresses are syntactically correct. 
 * @author propriétaire
 *
 */
public class FieldVerifier {

    public static boolean verifyName(String value) {
    	return (value.length() > 0)&&(value.length() < 20);
    }

    public static boolean verifyEMailAddress(String email) {
        return ((email.indexOf('@') >= 0)&&(email.length() < 40));
    }

    public static boolean verifyPassword(String password) {
        return password.length() >= 6;
    }

    public static boolean verifyTagUID(String uid) {
        return uid.length() > 0;
    }

    public static boolean verifyTagName(String name) {
        return name.length() > 0;
    }




	
	
	
//	public static boolean verify(String value)
//	{
//		return true;
//	}
	
}
