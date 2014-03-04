package com.facelock.facelocker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jasypt.*;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {


	
    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       
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
                                getString(R.string.title_section2)
                        }),
                this);
        	fillPasswords();
    }

    public void fillPasswords(){
    	PasswordManager.getInstance().storeLogin("Facebook", "Username1", "pass");
    	PasswordManager.getInstance().storeLogin("Facebook", "Username2", "pass");
    	PasswordManager.getInstance().storeLogin("Facebook", "Username3", "pass");
    	PasswordManager.getInstance().storeLogin("Google", "Username4", "pass");
    	PasswordManager.getInstance().storeLogin("Google", "Username5", "pass");
    	PasswordManager.getInstance().storeLogin("Youtube", "Username6", "pass");
    	PasswordManager.getInstance().storeLogin("Reddit", "Username7", "pass");
    	PasswordManager.getInstance().storeLogin("Facebook2", "Username1", "pass");
    	PasswordManager.getInstance().storeLogin("Facebook2", "Username2", "pass");
    	PasswordManager.getInstance().storeLogin("Facebook2", "Username3", "pass");
    	PasswordManager.getInstance().storeLogin("Google2", "Username4", "pass");
    	PasswordManager.getInstance().storeLogin("Google2", "Username5", "pass");
    	PasswordManager.getInstance().storeLogin("Youtube2", "Username6", "pass");
    	PasswordManager.getInstance().storeLogin("Reddit2", "Username7", "pass");
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
        
        if(position == 0){
        	ExpandableListView listView = (ExpandableListView) findViewById(R.id.pwGroupList);
            // preparing list data
            HashMap<String, List<String>> temp = PasswordManager.getInstance().getMap();
            List<String> keys = new ArrayList<String>();
            keys.addAll(temp.keySet());
            ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, keys, temp);
        
            listView.setOnChildClickListener(new OnChildClickListener() {
            	 
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                        int groupPosition, int childPosition, long id) {
                    Toast.makeText(
                            getApplicationContext(),
                            		parent.getExpandableListAdapter().getGroup(groupPosition)
                                    + " : "
                                    + parent.getExpandableListAdapter().getChild(groupPosition, childPosition), Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            });
            // setting list adapter
            listView.setAdapter(listAdapter);
        }
        
        if(position == 1){
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, PasswordManager.getInstance().getApplications());
            final AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.appInput);
            final TextView userView = (TextView)findViewById(R.id.editText1);
            final TextView passView = (TextView)findViewById(R.id.editText2);
            textView.setAdapter(adapter);
            
            Button button = (Button) findViewById(R.id.button1);
            
            button.setOnClickListener(new OnClickListener() {
           	
				@Override
				public void onClick(View v) {
					String application = textView.getText().toString();
					String username = userView.getText().toString();
					String pass = passView.getText().toString();
					
					
					Toast.makeText(
                            getApplicationContext(),
                            "Saved Password", Toast.LENGTH_SHORT)
                            .show();
					PasswordManager.getInstance().storeLogin(application, username, pass);
					passView.setText("");
					userView.setText("");
					textView.setText("");
				}
            });
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

}
