package com.railkill.lowyatbumper;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ScheduleFragment extends Fragment {
	
	private ArrayList<String> bumpList;
	
	public ScheduleFragment(ArrayList<String> bl) {
		this.bumpList = bl;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setalarm_details, container, false);
        
        final EditText txtAlarmName = (EditText) rootView.findViewById(R.id.alarm_details_name);
        final TimePicker timepicker = (TimePicker) rootView.findViewById(R.id.alarm_details_time_picker);
        final Spinner cmbSelectDay = (Spinner) rootView.findViewById(R.id.cmbSelectDay);
        TextView lblNumberBump = (TextView) rootView.findViewById(R.id.lblNumberBump);
        lblNumberBump.setText("" + bumpList.size());
        
        Button btnApplyAlarm = (Button) rootView.findViewById(R.id.btnApplyAlarm);
        btnApplyAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	if (!txtAlarmName.getText().toString().matches("")) {
                    		if (!txtAlarmName.getText().toString().contains(";")) {
                    			String bumpString = txtAlarmName.getText().toString() + ";" + timepicker.getCurrentHour() + ";" + timepicker.getCurrentMinute() + ";";
                        		
                        		// Check if Everyday or Specific Day is selected
                            	// Add data at the end of bumpString specifying the day in integer
                            	// 0 = Everyday | 1-7 = Monday to Sunday
                        		bumpString += cmbSelectDay.getSelectedItemPosition() + ";";                      
                        		
                            	for (int i = 0; i < bumpList.size(); i++) {
                            		bumpString += bumpList.get(i) + ";";
                            	}
                            	 	
                            	ObscuredSharedPreferences prefs = ObscuredSharedPreferences.getPrefs(getActivity(), "LowyatBumper", Context.MODE_PRIVATE);
                            	int size = prefs.getInt("scheduledTasks", 0);
                    		    prefs.edit().putString("alarm" + size, bumpString).commit();
                    		    prefs.edit().putInt("scheduledTasks", size+1).commit();
                    		    
                    		    // Set alarm to bump the code
                    		    // AlarmReceiver.setAlarms(getActivity().getApplicationContext());
                    		    Toast.makeText(getActivity(), "Schedule added.", Toast.LENGTH_LONG).show();
                    		    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    		    		  Context.INPUT_METHOD_SERVICE);
                    		    imm.hideSoftInputFromWindow(txtAlarmName.getWindowToken(), 0);  
                    		    getFragmentManager().popBackStackImmediate();
                    		}
                    		else {
                    			Toast.makeText(getActivity(), "Schedule name cannot contain semicolons.", Toast.LENGTH_LONG).show();
                    		}
                    	}
                    	else {
                    		// Show error message, an alarm name is required.
                    		Toast.makeText(getActivity(), "A schedule name is required.", Toast.LENGTH_LONG).show();
                    	}
                    	
                    }
        });
        return rootView;
    }
}
