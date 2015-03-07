package com.app_server.exceptions;

public class IllegalFieldException extends IllegalArgumentException
{
	private static final long serialVersionUID = 6349027407947995384L;
	
	public final int fieldId;

	public static final int PSEUDO = 0;
	public static final int FIRSTNAME = 1;
	public static final int LASTNAME = 2;
	public static final int EMAIL_ADDRESS = 3;
	public static final int BIRTH_DATE = 4;
	public static final int PASSWORD = 5;

	public static final int TAG_UID = 10;
	public static final int TAG_OBJECT_NAME = 11;
	public static final int TAG_OBJECT_IMAGE = 12;
	

	public IllegalFieldException(int fieldId, String message)
	{
		super(message);
		this.fieldId = fieldId;
	}
	
	public int getFieldId()
	{
		return fieldId;
	}
}
