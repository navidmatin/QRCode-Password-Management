package com.facelock.facelocker;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class Email {
	public static void email(Bitmap image, Context context){
		String keyname="GeneratedCode" +System.currentTimeMillis()/1000;
		String path=MediaStore.Images.Media.insertImage(context.getContentResolver(), image, keyname, "");
		//ArrayList<Uri> imageUris=new ArrayList<Uri>();
		Uri uri= Uri.parse(path);
		//imageUris.add(uri);
	    //intent.setAction(Intent.ACTION_VIEW);
	   // intent.setDataAndType(uri, "image/*");
		
		Intent sharingIntent= new Intent(Intent.ACTION_SEND);
		
		String shareBody = "Here is the Generated Key"+System.currentTimeMillis()/10000;
		sharingIntent.setType("image/png");
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "FaceLocker Generated Key");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
		sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
		
		context.startActivity(Intent.createChooser(sharingIntent, "Send the key via"));
		
	}
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
