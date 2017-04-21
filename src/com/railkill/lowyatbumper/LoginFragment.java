package com.railkill.lowyatbumper;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment implements AsyncResponse {
		
	private String[][] topicArray;
	// List of bumpable forum numbers:
    private String[] bumpableForums = { 
    		"156", "119", "157", "162", "15",
    		"84", "11", "261", "276", "275",
    		"278", "57", "55", "266", "279",
    		"155", "98", "265", "267", "277",
    		"54", "262", "132", "180", "238",
    		"264", "135", "216", "217", "219",
    		"268", "72", "272", "273", "271",
    		"274", "263", "113", "218", "269",
    		"220", "224", "225", "226", "227",
    		"237", "21", "134", "270", "148",
    		"128", "152" };
    private int checked;
    private boolean isLoggedIn;
    private boolean isBumping;
    
    private EditText txtUsername;
    private EditText txtPassword;
    private TextView lblError;
    private TextView lblLogged;
    private TextView lblName;
    private LinearLayout items;
    private Button btnClear;
    private ImageButton btnLogin;
    private ProgressDialog loading;
    
	public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bumper, container, false);
        txtUsername = (EditText) rootView.findViewById(R.id.txtUsername);
        txtPassword = (EditText) rootView.findViewById(R.id.txtPassword);
        lblError = (TextView) rootView.findViewById(R.id.lblError);
        lblLogged = (TextView) rootView.findViewById(R.id.lblLoggedIn);
        lblName = (TextView) rootView.findViewById(R.id.lblName);
        items = (LinearLayout) rootView.findViewById(R.id.layoutItems);
        isLoggedIn = false;
        isBumping = false;
        checked = 0;
        
        btnClear = (Button) rootView.findViewById(R.id.btnClear);
        // TODO: When the app starts, check if there is stored username and password.
        ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(getActivity(), "LowyatBumper", Context.MODE_PRIVATE);
        String un = prefs.getString("username", null);
        String pw = prefs.getString("password", null);
        
        if (un != null && pw != null) {
        	// Automatically perform the login if there is.
        	if (un != "" && pw != "") {
        		login(un, pw);
        	}
        }
        
        // LOGIN FUNCTIONALITY
        btnLogin = (ImageButton) rootView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	if (!isLoggedIn) {
                    		login(txtUsername.getText().toString(), txtPassword.getText().toString());
                    	}
                    	else {
                    		// If already logged in, do logout function.
                    		// Essentially, just display username and password fields for re-entry.
                    		txtUsername.setVisibility(View.VISIBLE);
                    		txtPassword.setVisibility(View.VISIBLE);
                    		lblLogged.setVisibility(View.INVISIBLE);
                    		lblName.setVisibility(View.INVISIBLE);
                    		txtUsername.setText("");
                    		txtPassword.setText("");
                    		lblError.setText(getString(R.string.errortext));
                    		btnLogin.setImageResource(R.drawable.loginbutton);
                    		isLoggedIn = false;
                    		// Also, clear all posts.
                    		items.removeAllViews();
                    		checked = 0;
                    		checkClearButton(btnClear);
                    	}
                    }
        });
        
        // TODO: Select All / Clear checkbox button.
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (checked > 0) {
            		// If there are checked, means CLEAR them.
            		// Select all checkboxes in items, uncheck the checkboxes.
            		for (int i = 0; i < items.getChildCount(); i++) {
            			CheckBox cb = (CheckBox) items.getChildAt(i);
            			cb.setChecked(false);
            		}
            		checked = 0;
            		checkClearButton(btnClear);
            	}
            	else {
            		// Nothing checked, select ALL.
            		// Select all checkboxes in items, check the checkboxes.
            		for (int i = 0; i < items.getChildCount(); i++) {
            			CheckBox cb = (CheckBox) items.getChildAt(i);
            			cb.setChecked(true);
            			//checked++;
            		}
            		checkClearButton(btnClear);
            	}
            }
        });
        
        // BUMP BUTTON
        // Look through checkboxes and bump checked ones.
        Button btnBump = (Button) rootView.findViewById(R.id.btnBump);
        btnBump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        		// Build bump URLs into an array
        		if (checked > 0 && topicArray != null) {
            		ArrayList<String> bumpList = getBumpList();
                	// Bump the URLs
            		if (bumpList != null && !bumpList.isEmpty()) {
            			try {
                    		ObscuredSharedPreferences pre = ObscuredSharedPreferences.getPrefs(getActivity(), "LowyatBumper", Context.MODE_PRIVATE);
                    		loading = new ProgressDialog(getActivity());
                    		loading.setMessage("Bumping...");
                    		loading.show();
                    		isBumping = true;
                    		LoginHandler bumpRun = new LoginHandler(bumpList);
                    		bumpRun.delegate = LoginFragment.this;
                        	bumpRun.execute("https://forum.lowyat.net/index.php?act=Login&CODE=01",
                        			pre.getString("username", txtUsername.getText().toString()), pre.getString("password", txtPassword.getText().toString()));
                        	
            			}
            			catch (Exception e) {
            				e.printStackTrace();
            				loading.dismiss();
            			}
            		}
            		else {
            			Toast.makeText(getActivity(), "No bumpable posts selected.", Toast.LENGTH_LONG).show();
            		}
            	}
            	else {
            		Toast.makeText(getActivity(), "No posts available.", Toast.LENGTH_LONG).show();
            	}
        	}
        });
        
        // TODO: Schedule button.
        Button btnSchedule = (Button) rootView.findViewById(R.id.btnSchedule);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		ArrayList<String> bumpList = getBumpList();
        		if (bumpList == null || bumpList.isEmpty()) {
        			// Toast.makeText(getActivity(), "No bumpable posts selected.", Toast.LENGTH_LONG).show();
        			// Instead of error, open up list of schedules instead.
        			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();       
           		 	transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
           		 	transaction.replace(R.id.container, new ListFragment()).addToBackStack(null)
    	        	 .commit();	
        		}
        		else {
        			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();       
           		 	transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
           		 	transaction.replace(R.id.container, new ScheduleFragment(bumpList)).addToBackStack(null)
    	        	 .commit();
        		}
        	}
        });
        AlarmReceiver.setAlarms(getActivity().getApplicationContext());
        return rootView;
    }
    
    private boolean isBumpable(String forum_id) {
    	return Arrays.asList(bumpableForums).contains(forum_id);
    }
    
    private void checkClearButton(Button btn) {
    	if (checked > 0) {
    		btn.setText(getActivity().getString(R.string.cleartext));
    	}
    	else {
    		btn.setText(getActivity().getString(R.string.selectalltext));
    	}
    }
    
    private void login(String u, String p) {
    	try {
    		loading = new ProgressDialog(getActivity());
    		loading.setMessage("Retrieving posts...");
    		loading.show();
    		LoginHandler loginRun = new LoginHandler();
    		loginRun.delegate = LoginFragment.this;
        	loginRun.execute("https://forum.lowyat.net/index.php?act=Login&CODE=01",
        			u, p);
        	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
		    		  Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(txtUsername.getWindowToken(), 0);  
		    imm.hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);  
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		loading.dismiss();
    	}
    }
    
    private ArrayList<String> getBumpList() {
    	if (checked > 0 && topicArray != null) {
    		ArrayList<String> bumpList = new ArrayList<String>();
        	for (int i = 0; i < topicArray.length; i++) {
        		if (topicArray[i][4] == "Y") {
        			// Bump the topic.
        			bumpList.add("https://forum.lowyat.net/index.php?act=Post&CODE=10000&f=" + topicArray[i][2] + "&t="
        					+ topicArray[i][1] + "&auth_key=" + topicArray[i][5]);
        		}
        	}
        	return bumpList;
    	}
    	return null;
    }

	@Override
	public void processFinish(String[][] output) {
		// First, clear the items.
		items.removeAllViews();
		checked = 0;
		checkClearButton(btnClear);
		// When login finishes, this will execute. (TESTED AND WORKING)
		loading.dismiss();
		topicArray = output;
		ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(getActivity(), "LowyatBumper", Context.MODE_PRIVATE);
		if (topicArray != null) {
			// Save preferences of username and password
	        if (!txtUsername.getText().toString().matches("") && !txtPassword.getText().toString().matches("")) {
	        	prefs.edit().putString("username",txtUsername.getText().toString()).commit();
			    prefs.edit().putString("password",txtPassword.getText().toString()).commit();
	        }
	        
    		// Replace username and password fields with just username (logged in).
    		txtUsername.setVisibility(View.INVISIBLE);
    		txtPassword.setVisibility(View.INVISIBLE);
    		lblLogged.setVisibility(View.VISIBLE);
    		lblName.setText(prefs.getString("username", "(Error)"));
    		lblName.setVisibility(View.VISIBLE);
    		// Set loggedIn to true, change the login button to logout button!
    		isLoggedIn = true;
    		btnLogin.setImageResource(R.drawable.logoutbutton);
    		// Create checkboxes and list out bumpable posts.
    		int b = 0;
    		for (int i = 0; i < topicArray.length; i++) {
    			if (isBumpable(topicArray[i][2])) {
    				CheckBox cb = new CheckBox(getActivity());
    				final int temp = i;
                    cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                            	topicArray[temp][4] = "Y";
                            	checked++;
                            }
                            else {
                            	topicArray[temp][4] = "N";
                            	checked--;
                            }
                            checkClearButton(btnClear);
                        }
                    });
                    cb.setText(Html.fromHtml(topicArray[i][0]) + "\n(" + topicArray[i][3] + ")\n");
                    items.addView(cb);
                    b++;
    			}
        	}
    		lblError.setText(b + " bumpable posts found.");
    	}
    	else {
    		
    		if (isBumping) {
    			isBumping = false;
    			Toast.makeText(getActivity(), "Bump request sent.", Toast.LENGTH_LONG).show();
    			// Refresh the list of topics
    			items.removeAllViews();
    			checked = 0;
        		checkClearButton(btnClear);
    			login(prefs.getString("username", txtUsername.getText().toString()), prefs.getString("password", txtPassword.getText().toString()));
    		}
    		else {
    			lblError.setText("Unable to retrieve posts.");
    		}
    	}
	}
}
