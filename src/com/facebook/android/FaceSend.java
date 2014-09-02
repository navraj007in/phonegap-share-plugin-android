package com.facebook.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.SessionEvents.AuthListener;

public class FaceSend {
	private static final String FACEBOOK_APPID = "251603178276136";
	private static final String FACEBOOK_PERMISSION = "publish_stream";
	public Activity activity;
	public String name, caption, description;
	public Bitmap bitmap;
	Facebook facebook;
	public static final String APP_ID = "251603178276136";

	private AsyncFacebookRunner mAsyncRunner;
	//public static final String APP_ID = "162243893843964";
	private static final String[] PERMISSIONS = new String[] {
			"publish_stream", "read_stream", "offline_access" };
	public boolean isImage = false;

	public FaceSend(Activity activity) {
		this.activity = activity;
		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		// Session restoration
		SessionStore.restore(facebook, activity);

		// Adding the authentication listener
		SessionEvents.addAuthListener(new SampleAuthListener());
	}

	public void sendText(String name, String caption, String description) {
		this.name = name;
		this.caption = caption;
		this.description = description;
		isImage = false;
		facebook.setBooleanVariable(false);
		sendData();
		System.out.println("in the send text");
	}

	public void sendImage(Bitmap bitmap) {
		this.bitmap = bitmap;
		isImage = true;
		sendData();
	}

	// Obtain the post id and send a post request
	public class SampleDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);

				mAsyncRunner.request(postId, new Bundle(),
						new WallPostRequestListener());

			} else {
				Log.d("Facebook-Example", "No wall post made");
			}
		}
	}

	private void sendData() {
		System.out.println("in the send data ");
		if (facebook.isSessionValid()) {
			// Asking whether user wants to logout
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Facebook session");

			builder.setMessage("You are alread logged in to your facebook account. Do you want to sign out?");

			// Log out the user
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										facebook.logout(activity);
									} catch (MalformedURLException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
					});

			// Upload the image
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							// Creating the bitmap of the image to be
							// uploaded

							// defining the byte array and the
							// ByteArrayOutputStream
							if (isImage) {
								byte[] data = null;
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								bitmap.compress(Bitmap.CompressFormat.JPEG,
										100, baos);
								data = baos.toByteArray();
								Bundle parameters = new Bundle();
								parameters.putString("method", "photos.upload");
								parameters.putByteArray("picture", data);
								mAsyncRunner.request(null, parameters, "POST",
										new SampleUploadListener(), null);
							} else {

								Bundle parameters = new Bundle();
								parameters.putString("attachment",
										"{\"name\":\"" + name
												+ "\",\"caption\": \""
												+ caption
												+ " ::\",\"description\":\""
												+ description + "\"}");

								facebook.dialog(activity, "stream.publish",
										parameters, new SampleDialogListener());
							}
						}
					});
			builder.show();
		} else {
			// Authenticate the user
			facebook.authorize(activity, APP_ID, PERMISSIONS,
					new LoginDialogListener());
		}
	}

	// Post the data and get the response
	public class WallPostRequestListener extends BaseRequestListener {

		public void onComplete(final String response) {
			Log.d("Facebook-Example", "Got response: " + response);
			@SuppressWarnings("unused")
			String message = "<empty>";
			try {
				JSONObject json = Util.parseJson(response);
				message = json.getString("message");
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}

	}

	// Upload the image and get the response
	public class SampleUploadListener extends BaseRequestListener {

		@Override
		public void onComplete(String response) {
			try {
				Log.d("Facebook-Example", "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String src = json.getString("src");
				Log.d("Source: ", src);
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}

		}

	}

	// The dialog listener providing various functions when the dialog completes
	private final class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			SessionEvents.onLoginSuccess();

		}

		public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}

	}

	public class SampleAuthListener implements AuthListener {

		// Posting the data after successful authentication
		public void onAuthSucceed() {
			System.out.println("Here");
			if (!isImage) {

				Bundle parameters = new Bundle();
				parameters.putString("attachment", "{\"name\":\"" + name
						+ "\",\"caption\": \"" + caption
						+ " ::\",\"description\":\"" + description + "\"}");

				facebook.dialog(activity, "stream.publish", parameters,
						new SampleDialogListener());
			} else {

				byte[] data = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				data = baos.toByteArray();

				Bundle parameters = new Bundle();
				parameters.putString("method", "photos.upload");
				parameters.putByteArray("picture", data);
				mAsyncRunner.request(null, parameters, "POST",
						new SampleUploadListener(), null);

			}
		}

		public void onAuthFail(String error) {
		}
	}

}
