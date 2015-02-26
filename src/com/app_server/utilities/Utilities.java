package com.app_server.utilities;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.app_server.data.Tag;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Utilities {
	
	/**
	 * Null check Method
	 * 
	 * @param txt
	 * @return
	 * 
	 */
	
	public static boolean isNotNull(String txt) {
		// System.out.println("Inside isNotNull");
		return txt != null && txt.trim().length() >= 0 ? true : false;
	}

	/**
	 * Method to construct JSON
	 * 
	 * @param tag
	 * @param status
	 * @return
	 * 
	 */
	
	public static String constructJSON(String tag, boolean status) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("status", new Boolean(status));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON with Error Msg
	 * 
	 * @param tag
	 * @param status
	 * @param err_msg
	 * @return
	 * 
	 */
	
	public static String constructJSON(String tag, boolean status, String err_msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("status", new Boolean(status));
			obj.put("error_msg", err_msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return obj.toString(); 
	}
	
	public static String constructJSON(String tag, boolean status, ArrayList<Tag> ListTag) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("status", new Boolean(status));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return obj.toString(); 
	}
	
	/**
	 * Array of characters used for hexa-decimal conversion.
	 */
	
	private static final byte charactersSet[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Will return the hash of a password.
	 * 
	 * @param password
	 *            User password
	 * @return hexa-decimal string representation of the hashed password.
	 * @throws NoSuchAlgorithmException
	 *             Thrown in case the hash algorithm could not be found.
	 */
	public static String hashPassword(String password) throws NoSuchAlgorithmException {
		return convertBytesToHexString(MessageDigest.getInstance("SHA-256").digest(password.getBytes()));
	}

	/**
	 * Converts a byte array to the corresponding hexa-decimal string
	 * representation.
	 * 
	 * @param array
	 *            Byte array
	 * @return Hexa-decimal string
	 */
	public static String convertBytesToHexString(byte array[]) {
		byte stringBytes[] = new byte[2 * array.length];

		for (int i = 0; i < array.length; i++) {
			stringBytes[2 * i] = charactersSet[(array[i] & 0xf0) >> 4];
			stringBytes[2 * i + 1] = charactersSet[array[i] & 0x0f];
		}

		return new String(stringBytes);
	}
}
