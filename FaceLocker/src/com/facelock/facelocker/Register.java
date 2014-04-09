package com.facelock.facelocker;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a register screen to the user, offering registration as
 * well.
 */
public class Register extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";


	// Values for email and password at the time of the register attempt.
	private String mEmail;
	private String mPassword;
	private String mPConfirm;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPConfirmView;
	private View mregisterFormView;
	private View mregisterStatusView;
	private TextView mregisterStatusMessageView;
	private DatabaseHelper dh;
	private QRDatabaseHelper dhqr;
	private String qrPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ImageView to display the QR code in.  This should be defined in 
		// your Activity's XML layout file
		setContentView(R.layout.register);
		
		ImageView imageView = (ImageView) findViewById(R.id.qrCode);

		String qrData = PasswordGenerator.Generate(20, 30);
		qrPassword=qrData;
		int qrCodeDimention = 500;

		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData, null,
		        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);

		try {
		    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
		    imageView.setImageBitmap(bitmap);
		} catch (WriterException e) {
		    e.printStackTrace();
		}
		
		// Set up the register form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.register_username);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.register_password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.register || id == EditorInfo.IME_NULL) {
							attemptregister();
							return true;
						}
						return false;
					}
				});
		mPConfirmView = (EditText) findViewById(R.id.confirm_password);
		mPConfirmView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.register || id == EditorInfo.IME_NULL) {
					attemptregister();
					return true;
				}
				return false;
			}
		});
		mregisterFormView = findViewById(R.id.register_form);
		mregisterStatusView = findViewById(R.id.register_status);
		mregisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.create_new_user_btn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptregister();
					}
				});
	}
	/**
	 * Attempts to sign in or register the account specified by the register form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual register attempt is made.
	 */
	public void attemptregister() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the register attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPConfirm = mPConfirmView.getText().toString();		

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			
			if ((mPassword.equals(mPConfirm)) && (!mEmail.equals(""))
					&& (!mPassword.equals("")) && (!mPConfirm.equals(""))) {
				this.dh = new DatabaseHelper(this);
				this.dhqr= new QRDatabaseHelper(this);
				this.dh.insert(mEmail, mPassword);
				this.dhqr.insert(mEmail, qrPassword);
				// this.labResult.setText("Added");
				Toast.makeText(Register.this, "new record inserted",
						Toast.LENGTH_SHORT).show();
				mregisterStatusMessageView.setText(R.string.register_progress_creating_new_user);
				showProgress(true);
				startActivity(new Intent(this, Login.class));
			} else if ((mEmail.equals("")) || (mPassword.equals(""))
					|| (mPConfirm.equals(""))) {
				Toast.makeText(Register.this, "Missing entry", Toast.LENGTH_SHORT)
						.show();
			} else if (!mPassword.equals(mPConfirm)) {
				new AlertDialog.Builder(this)
						.setTitle("Error")
						.setMessage("passwords do not match")
						.setNeutralButton("Try Again",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})

						.show();
			}
		}
	}

	/**
	 * Shows the progress UI and hides the register form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mregisterStatusView.setVisibility(View.VISIBLE);
			mregisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mregisterStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mregisterFormView.setVisibility(View.VISIBLE);
			mregisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mregisterFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mregisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mregisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
