package com.railkill.lowyatbumper;


import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListFragment extends Fragment {

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);
	        reloadList(rootView);
	        return rootView;
	 }
	 
	 private String getDayName(int dayNumber) {
		 String dn = "";
		 switch(dayNumber) {
		 case 0:
			 dn = "Everyday";
			 break;
		 case 1:
			 dn = "Sundays";
			 break;
		 case 2:
			 dn = "Mondays";
			 break;
		 case 3:
			 dn = "Tuesdays";
			 break;
		 case 4:
			 dn = "Wednesdays";
			 break;
		 case 5:
			 dn = "Thursdays";
			 break;
		 case 6:
			 dn = "Fridays";
			 break;
		 case 7:
			 dn = "Saturdays";
			 break;
		 default:
			 dn = "Error";
			 break;
		 }
		 return dn += " @ ";
	 }
	 
	 private void reloadList(View rv) {
		final View rootV = rv;
	 	final LinearLayout scheduledItems = (LinearLayout) rootV.findViewById(R.id.scheduledItems);
        final ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(getActivity(), "LowyatBumper", Context.MODE_PRIVATE);
        final int size = prefs.getInt("scheduledTasks", 0);
        
        if (size > 0) {
        	rootV.findViewById(R.id.lblNoScheduled).setVisibility(View.GONE);	        	
        	for (int i = 0; i < size; i++) {
        		String alarm = prefs.getString("alarm" + i, null);
        		String[] parts = alarm.split(";");
        		
        		// Add LinearLayout, then add ImageButton and Text
        		LinearLayout item = new LinearLayout(getActivity());
        		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        		LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        		textparams.gravity = Gravity.CENTER_VERTICAL;
        		item.setOrientation(LinearLayout.HORIZONTAL);
        		
        		final int rmIndex = i;
        		ImageButton del = new ImageButton(getActivity());
        		del.setBackgroundResource(R.drawable.delbutton);
        		del.setLayoutParams(params);
        		del.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// Make an ArrayList of alarms from SharedPreferences
						ArrayList<String> scheduleList = new ArrayList<String>();
						 for (int i = 0; i < size; i++) {
							 scheduleList.add(prefs.getString("alarm" + i, null));
						 }
						 // Clear alarms and list of schedules
						 scheduledItems.removeAllViews();
	                     AlarmReceiver.cancelAlarms(getActivity().getApplicationContext());
	                     for (int i = 0; i < size; i++) {
	                    	 prefs.edit().remove("alarm" + i).commit();
	                     }
	                     // Remove the selected schedule from the ArrayList
						 scheduleList.remove(rmIndex);
						 prefs.edit().putInt("scheduledTasks", size-1).commit(); // reduce size by one
	                     // Save the new list into SharedPreferences
						 for (int i = 0; i < scheduleList.size(); i++) {
							 prefs.edit().putString("alarm" + i, scheduleList.get(i)).commit();
						 }
						 // Set the alarms
						 AlarmReceiver.setAlarms(getActivity().getApplicationContext());
						 // Refresh this page
						 reloadList(rootV);
						 Toast.makeText(getActivity(), "Schedule removed.", Toast.LENGTH_LONG).show();
						 if ((size - 1) == 0) {
							 rootV.findViewById(R.id.lblNoScheduled).setVisibility(View.VISIBLE);
						 }
					}
        		});
        		
        		TextView cb = new TextView(getActivity());
        		cb.setLayoutParams(textparams);
        		cb.setText((i+1) + ". " + parts[0] + " (" + getDayName(Integer.parseInt(parts[3])) + parts[1] + ":" + String.format("%02d", Integer.parseInt(parts[2])) + ")");
             
             	item.addView(del);
        		item.addView(cb);
        		scheduledItems.addView(item);
	        }
        }
        
        Button btnClearAlarms = (Button) rootV.findViewById(R.id.btnClearAlarms);
        btnClearAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	scheduledItems.removeAllViews();
            	AlarmReceiver.cancelAlarms(getActivity().getApplicationContext());
            	for (int i = 0; i < size; i++) {
            		prefs.edit().remove("alarm" + i).commit();
            	}
            	prefs.edit().remove("scheduledTasks").commit();
            	Toast.makeText(getActivity(), "All scheduled tasks have been deleted.", Toast.LENGTH_LONG).show();
            	rootV.findViewById(R.id.lblNoScheduled).setVisibility(View.VISIBLE);
            }
        });
	 }
	 
}
