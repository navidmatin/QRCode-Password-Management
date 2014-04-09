package com.facelock.facelocker;


import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class QRLogin extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private QRDatabaseHelper dh;
	private UserLoginTask mAuthTask = null;

	Intent intent;
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	public String mPassword;

	// UI references.
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.qr_login);

		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		findViewById(R.id.sign_in_button_qr).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						//Scanning QR Code
						intent = new Intent("com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
						startActivityForResult(intent, 0);
					}
				});
		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {


		
		/*
		try {
			String encryptedmPassword = PasswordCrypto.encrypt(mPassword, "000102030405060708090A0B0C0D0E0F");
			mPassword=encryptedmPassword;
			
		}
		catch(Exception e) {
			Context context=getApplicationContext();
			CharSequence _error= "Initial Password encryption failed!";
			int duration=Toast.LENGTH_LONG;
			Toast toast= Toast.makeText(context, _error, duration);
			toast.show();
			
		}*/

		boolean cancel = false;
		View focusView = null;
		
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			//Checking for email and password
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			if(checkLogin())
			{
				showProgress(true);
				mAuthTask = new UserLoginTask();
				mAuthTask.execute((Void) null);
				startActivity(intent);
			}
		}
	}
	@Override
	protected void onResume(){
		super.onResume();
		ImageView imageView = (ImageView) findViewById(R.id.qrCodeScanned);
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(mPassword, null,
		        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 500);

		try {
		    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
		    imageView.setImageBitmap(bitmap);
		} catch (WriterException e) {
		    e.printStackTrace();
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode==0) {
			if(resultCode == RESULT_OK){
				mPassword = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
			} else if(resultCode==RESULT_CANCELED) {
				//Handle cancel
			}
		}
	}
	private boolean checkLogin(){
		
		
		String username = getIntent().getStringExtra("User");
		mEmail=username;
		this.dh=new QRDatabaseHelper(this);
		List<String> names=this.dh.selectAll(username,mPassword);
		if(names.size()>0){
			//Login successful
			startLogin(username);
			return true;
		} else {
			//Try again?
			new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("Login failed")
				.setNeutralButton("Try Again", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						failedLogin();
					}
				}).show();
			return false;
		}
	}
	private void failedLogin(){
		startActivity(new Intent(this, Login.class));
	}
	private void startLogin(String username)
	{
		intent = new Intent(this, MainActivity.class);
		intent.putExtra("User", username);
	}
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} 
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
		
	}
    @Override
    public void onBackPressed(){
    	
    }
}
