
package com.facebook.android;

public class DialogError extends Throwable
{

	private static final long serialVersionUID = 1L;

	
	private int mErrorCode;

	/** The URL that the dialog was trying to load */
	private String mFailingUrl;

	public DialogError(String message, int errorCode, String failingUrl)
	{
		super(message);
		mErrorCode = errorCode;
		mFailingUrl = failingUrl;
	}

	int getErrorCode()
	{
		return mErrorCode;
	}

	String getFailingUrl()
	{
		return mFailingUrl;
	}

}
