package com.cordova.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FaceSend;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class SMSComposer extends CordovaPlugin{
	  public static final String ACTION_SHOW_SMS_COMPOSER = "showSMSComposer";
	  public static final String ACTION_SHOW_EMAIL_COMPOSER = "showEMAILComposer";
	  public static final String ACTION_SHARE_FACEBOOK = "shareFacebook";
	  public static final String ACTION_SHARE_OTHERS= "shareOthers";
	  public static final String ACTION_SEND_COMMENT= "sendComment";
	  String message;
	  @Override
	  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		  
		  try {
			    if (ACTION_SHOW_SMS_COMPOSER.equals(action)) {
			    	String message=args.getString(0);

			    	Uri uri = Uri.parse("smsto:");
			        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			        intent.putExtra("sms_body", message);  
			        this.cordova.getActivity().startActivity(intent);
			        callbackContext.success();
			       return true;
			    }
			    else if(ACTION_SHOW_EMAIL_COMPOSER.equals(action)){
			    	String subject=args.getString(0);
			    	String message=args.getString(1);
			    	
			    	Intent intent = new Intent(Intent.ACTION_SEND);
			    	intent.setType("text/html");
			    	intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			    	intent.putExtra(Intent.EXTRA_TEXT, message);

			    	this.cordova.getActivity().startActivity(Intent.createChooser(intent, "Send Email"));
				       callbackContext.success();
				       return true;
			    }
			    else if(ACTION_SEND_COMMENT.equals(action)){
			    	String subject=args.getString(0);
			    	String message=args.getString(1);

			    	sendMail("to@gmail.com",subject, message);
				       callbackContext.success();
				       return true;
			    }
			    else if(ACTION_SHARE_FACEBOOK.equals(action)){
			    	message=args.getString(0);
			    	final String title=args.getString(0);
			    	final String description=args.getString(0);

			    	cordova.getActivity().runOnUiThread(new Runnable() {
			            public void run() {
					    	share(title,description,message);
			            }
			        });

			    	return true;
			    }
			    else if(ACTION_SHARE_OTHERS.equals(action)){
			    	String subject=args.getString(0);
			    	String message=args.getString(0);
			    	Intent sendIntent = new Intent();
			    	sendIntent.setAction(Intent.ACTION_SEND);
			    	sendIntent.putExtra(Intent.EXTRA_TEXT, message);
			    	sendIntent.setType("text/plain");
			    	cordova.getActivity().startActivity(sendIntent);
			    	callbackContext.success();
				       return true;
			    }
			    callbackContext.error("Invalid action");
			    return true;
			} catch(Exception e) {
			    System.err.println("Exception: " + e.getMessage());
			    callbackContext.error(e.getMessage());
			    return true;
			} 
	  }
	  public void share(String title,String caption,String description) {
			FaceSend facebook = new FaceSend(this.cordova.getActivity());
			facebook.sendText(title, caption, description);
		}
	  private void sendMail(String address,String Subject,String Message) {
			// TODO Auto-generated method stub

			String Address;
			Address=(address);
			GMailSender mailsender = new GMailSender("your gmail id", "your gmail password");

	        String[] toArr = { "to@gmail.com"};
	        mailsender.set_to(toArr);
	        mailsender.set_from("your gmail id");
	        mailsender.set_subject(Subject);
	        mailsender.setBody(Message);

	        try {
	            //mailsender.addAttachment("/sdcard/filelocation");

	            if (mailsender.send()) {
	                Toast.makeText(this.cordova.getActivity(),
	                        "Email was sent successfully.",
	                        Toast.LENGTH_LONG).show();
	            } else {
	                Toast.makeText(this.cordova.getActivity(), "Email was not sent.",
	                        Toast.LENGTH_LONG).show();
	            }
	        } catch (Exception e) {
	           
	            Log.e("MailApp", "Could not send email", e);
	        }		   
		}

}
