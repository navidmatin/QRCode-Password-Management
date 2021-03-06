package com.facelock.facelocker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	private static String key;
	private String username;
    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static String cryptoSharedPref;
    private static String userSharedPref;
    private Toast randPass;
    private CountDownTimer timer;
    private static ExpandableListAdapter listAdapter;
    private static ExpandableListView listView;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        username = getIntent().getStringExtra("User");//getting the username from login
        
        //Getting saved preferences
        cryptoSharedPref="com.facelock.facelocker.crypto";
        userSharedPref="com.facelock.facelocker.usershared";
        cryptoSharedPref=cryptoSharedPref+"."+username;
        userSharedPref=userSharedPref+"."+username;
        SharedPreferences sharedPref = this.getSharedPreferences(cryptoSharedPref,Context.MODE_PRIVATE);
        String defaultvalue= null;
        key=sharedPref.getString("Crypto", defaultvalue);
       
        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        getActionBarThemedContextCompat(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1),
                                getString(R.string.title_section2),
                                getString(R.string.title_section3),
                                getString(R.string.title_section4)
                        }),
                this);
    }

    public void fillPasswords(){
    	SharedPreferences file = getSharedPreferences(userSharedPref, Context.MODE_PRIVATE);
    	PasswordManager pwmanager = PasswordManager.getInstance(key);
    	pwmanager.getLogins(file);
    }

    
	public void storeLogin(View view) {
    	
    	AutoCompleteTextView appField = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
    	EditText usernameField = (EditText) findViewById(R.id.editText1);
    	EditText passwordField = (EditText) findViewById(R.id.editText2);
    	
    	String app = appField.getText().toString();
    	String username = usernameField.getText().toString();
    	String password = passwordField.getText().toString();
    	
    	SharedPreferences logins = getSharedPreferences(userSharedPref, Context.MODE_PRIVATE);
    	
    	Boolean successfulSave = PasswordManager.getInstance(key).storeLogin(app, username, password, logins);
    	
    	if (successfulSave) {
    		Toast.makeText(this, "Save Successful", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(this, "Save Unsuccessful", Toast.LENGTH_SHORT).show();
    	}
    	
		if(randPass!= null)
		{
			randPass.cancel();
			timer.cancel();
		}
    	getActionBar().setSelectedNavigationItem(0);
    	onNavigationItemSelected(0, 0);
    }
    

	public void deleteLogins() {
		SharedPreferences logins = getSharedPreferences(userSharedPref,Context.MODE_PRIVATE);
		Boolean successfulDelete = PasswordManager.deleteAll(logins);
		
		if (successfulDelete) {
			Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Delete Unsuccessful", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void generatePassword (View view) {
		String randomPassword = PasswordGenerator.Generate(8, 16);
		if(randPass!= null)
		{
			randPass.cancel();
			timer.cancel();
		}
    	randPass = Toast.makeText(this,"Random Password: " + randomPassword , Toast.LENGTH_LONG);
    	randPass.show();
    	timer = new CountDownTimer(700000, 1000)
    	{

    	    public void onTick(long millisUntilFinished) {randPass.show();}
    	    public void onFinish() {randPass.show();}

    	}.start();
		EditText passwordField = (EditText) findViewById(R.id.editText2);
		passwordField.setText(randomPassword);
	}


    /**
     * Backward-compatible version of {@link ActionBar#getThemedContext()} that
     * simply returns the {@link android.app.Activity} if
     * <code>getThemedContext</code> is unavailable.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Context getActionBarThemedContextCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getActionBar().getThemedContext();
        } else {
            return this;
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        Fragment fragment = new DummySectionFragment();
        Bundle args = new Bundle();
        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        this.getSupportFragmentManager().executePendingTransactions();
		if(randPass!= null)
		{
			randPass.cancel();
			timer.cancel();
		}
        if(position == 0){
        	fillPasswords();
        	MainActivity.listView = (ExpandableListView) findViewById(R.id.pwGroupList);
            // preparing list data
            HashMap<String, List<String>> temp = PasswordManager.getInstance(key).getMap();
            List<String> keys = new ArrayList<String>();
            if(PasswordManager.getInstance(key).size() == 0)
            	Toast.makeText(this, "No Saved Passwords. \n Click \"Add New\" to Add a Password. ", Toast.LENGTH_LONG).show();
            keys.addAll(temp.keySet());
            MainActivity.listAdapter = new ExpandableListAdapter(this, keys, temp);
            TextView search = (TextView)findViewById(R.id.username);
            search.addTextChangedListener(new TextWatcher() {
            	private HashMap<String, List<String>> pws = PasswordManager.getInstance(key).getMap();
            	   public void afterTextChanged(Editable s) {}

            	   public void beforeTextChanged(CharSequence s, int start,
            	     int count, int after) {
            	   }

            	   public void onTextChanged(CharSequence s, int start,
            	     int before, int count) {
            		   	  HashMap<String, List<String>> temp = (HashMap<String, List<String>>) pws.clone();
            	    	  String username = s.toString();
            	    	  
            	    	  List<String> keys = new ArrayList<String>();
            	          keys.addAll(pws.keySet());
            	          if(username.length() != 0){
	            	          for(int i = keys.size()-1; i >=0; i--){
	            	        	  String key = keys.get(i);
	            	        	  ArrayList<String> names = new ArrayList<String>();
	            	        	  names.addAll(temp.get(key));
	            	        	  for(int j = names.size()-1; j >= 0; j--){
	            	        		  if(!names.get(j).equals(username)){
	            	        			  names.remove(j);
	            	        		  }
	            	        	  }
	            	        	  if(names.size() == 0){
	            	        		  temp.remove(key);
	            	        		  keys.remove(i);
	            	        	  }else{
	            	        		  temp.put(key, names);
	            	        	  }
	            	          }
            	          }
            	          MainActivity.listAdapter = new ExpandableListAdapter(MainActivity.instance, keys, temp);
            	          MainActivity.listView.setAdapter(MainActivity.listAdapter);
            	   }
            	  });
            
            listView.setOnChildClickListener(new OnChildClickListener() {
            	 
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                        int groupPosition, int childPosition, long id) {
                	Toast.makeText(
                            getApplicationContext(),
                            PasswordManager.getInstance(key).getPassword(parent.getExpandableListAdapter().getGroup(groupPosition).toString(), 
                            		parent.getExpandableListAdapter().getChild(groupPosition, childPosition).toString(), 
                            		getSharedPreferences(userSharedPref,Context.MODE_PRIVATE)),

                            Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
            });
            // setting list adapter
            listView.setAdapter(listAdapter);
        }
        
        if(position == 1){
        	fillPasswords();
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, PasswordManager.getInstance(key).getApplications());
            final AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
            textView.setAdapter(adapter);
           
        }
        
        if(position == 2) {
        	deleteLogins();
        	getActionBar().setSelectedNavigationItem(0);
        	onNavigationItemSelected(0, 0);
        }
        if(position == 3) {
        	startActivity(new Intent(this, Login.class));
        	finish();
        }
        
        return true;
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        public static final String ARG_SECTION_TITLE = "section_title";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View rootView;
        	int section_number = getArguments().getInt(ARG_SECTION_NUMBER);
        	switch (section_number) {
        	case 1:
        		rootView = inflater.inflate(R.layout.my_passwords, container, false);
        		break;
        	case 2:
        		rootView = inflater.inflate(R.layout.add_password, container, false);
        		break;
        	default:
        		rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
        		break;
        	}
            return rootView;
        }
    }
    @Override
    protected void onStop()
    {
    	super.onStop();
    	SharedPreferences sharedPref = this.getSharedPreferences(cryptoSharedPref,Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor=sharedPref.edit();
    	try {
			//editor.putString("Crypto", PasswordCrypto.getKey());
			editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    @Override
    public void onBackPressed(){
    	
    }

}
