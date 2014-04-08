package com.facelock.facelocker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
	private String mUsername;
	private String mPassword;
	private String mPConfirm;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private EditText mPConfirmView;
	private View mregisterFormView;
	private View mregisterStatusView;
	private TextView mregisterStatusMessageView;
	private DatabaseHelper dh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		// Set up the register form.
		mUsername = getIntent().getStringExtra(EXTRA_EMAIL);
		mUsernameView = (EditText) findViewById(R.id.register_username);
		mUsernameView.setText(mUsername);

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
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the register attempt.
		mUsername = mUsernameView.getText().toString();
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

		if (cancel) {
			// There was an error; don't attempt register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			
			if ((mPassword.equals(mPConfirm)) && (!mUsername.equals(""))
					&& (!mPassword.equals("")) && (!mPConfirm.equals(""))) {
				this.dh = new DatabaseHelper(this);
				this.dh.insert(mUsername, mPassword);
				// this.labResult.setText("Added");
				Toast.makeText(Register.this, "new record inserted",
						Toast.LENGTH_SHORT).show();
				mregisterStatusMessageView.setText(R.string.register_progress_creating_new_user);
				showProgress(true);
				startActivity(new Intent(this, Login.class));
			} else if ((mUsername.equals("")) || (mPassword.equals(""))
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
