package com.facelock.facelocker;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class Email {
	public static void send(FragmentActivity activity, String email, String subject, String text){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		i.putExtra(Intent.EXTRA_TEXT   , text);
		try {
		    activity.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
}
