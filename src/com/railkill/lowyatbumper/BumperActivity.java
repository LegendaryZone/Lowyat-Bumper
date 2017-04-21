package com.railkill.lowyatbumper;

/*
 * 	LOWYAT BUMPER - A scheduler app for automatically bumping posts.
 * 	Created by NICHOLAS WONG WAI HONG (RailKill) © 2015 All Rights Reserved.
 * 
 * 	Most of the coding and all of the art is done by me.
 *  Code snippets and tutorials that helped me with this app are credited below.
 * 	Always ask permission before reusing or editing my stuff.
 * 
 *  Special thanks to mkyong's tutorial at
 *  (http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/)
 *  for teaching me how to use HTTPsURLConnection correctly.
 *  
 *  Also thanks to emmby from StackOverflow and a more elaborate code from
 *  Sam Bossen at (http://right-handed-monkey.blogspot.com/2014/04/obscured-shared-preferences-for-android.html)
 *  including WorxForUs Base64 library to support lower level Android devices used
 *  for obscuring SharedPreferences so that sensitive details are not in plain sight.
 *  
 *  At one point, I implemented Google's License Verification Library and I removed it.
 *  It causes too much problems, and the License Checker even crashes if you have Google Play but with
 *  no Google Account. LVL is full of bugs and is so easily circumvented that it's not worth introducing at all.
 *  It is also known to have false-positives. Unless Google fixes this, DO NOT use LVL in ANY APP ever, it's a waste of time.
 */

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class BumperActivity extends ActionBarActivity {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bumper);
        
        if (savedInstanceState == null) {
        	getSupportFragmentManager().beginTransaction()
            .add(R.id.container, new LoginFragment())
            .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bumper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	showHelp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showHelp() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	    builder.setTitle("Lowyat Bumper Help");
  	    builder.setCancelable(true);
  	    builder.setMessage(Html.fromHtml(getString(R.string.helptext)));
  	    builder.setPositiveButton("Back",
  	            new DialogInterface.OnClickListener() {
  	                public void onClick(DialogInterface dialog, int id) {
  	                    dialog.cancel();
  	                }
  	            });
  	    AlertDialog aboutMenu = builder.create();
  	    aboutMenu.show();
  	    
  	    ((TextView)aboutMenu.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}
