package com.muhammadalikarami.smartplug;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muhammadalikarami.smartplug.models.AlarmStatus;
import com.muhammadalikarami.smartplug.objects.Alarm;
import com.muhammadalikarami.smartplug.objects.Plug;
import com.muhammadalikarami.smartplug.request.EmptyRequest;
import com.muhammadalikarami.smartplug.response.GeneralResponse;
import com.muhammadalikarami.smartplug.response.SyncResponse;
import com.muhammadalikarami.smartplug.utility.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Admin on 4/30/2016.
 */
public class FragmentControlPlugs extends Fragment implements View.OnClickListener {

    private View rootView;
    private RelativeLayout rlFooter;
    private LinearLayout llAddSchedule;
    private CustomTextView txtAddSchedule;
    private ListView lvPlugs;
    private ListView lvAlarms;
    private AdapterPlugs plugAdapter;
    private AdapterAlarms alarmAdapter;
    private ArrayList<Plug> plugs;
    private ArrayList<Alarm> alarms;
    private JsonObjectRequest smartPlugReq = null;
    private JsonObjectRequest syncReq = null;

    private RelativeLayout rlAdd;
    private TimePicker timePicker;
    private Spinner spinnerPlug;
    private Switch switchPower;
    private LinearLayout llAdd;
    private CustomTextView txtAdd;

    private Alarm newAlarm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_control_plugs, container, false);

        init();

        return rootView;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    private void init() {
        lvPlugs = (ListView) rootView.findViewById(R.id.lvPlugs);
        lvAlarms = (ListView) rootView.findViewById(R.id.lvAlarms);
        rlFooter = (RelativeLayout) rootView.findViewById(R.id.rlFooter);
        llAddSchedule = (LinearLayout) rootView.findViewById(R.id.llAddSchedule);
        txtAddSchedule = (CustomTextView) rootView.findViewById(R.id.txtAddSchedule);
        // add page
        rlAdd = (RelativeLayout) rootView.findViewById(R.id.rlAdd);
        timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
        spinnerPlug = (Spinner) rootView.findViewById(R.id.spinnerPlug);
        switchPower = (Switch) rootView.findViewById(R.id.switchPower);
        llAdd = (LinearLayout) rootView.findViewById(R.id.llAdd);
        txtAdd = (CustomTextView) rootView.findViewById(R.id.txtAdd);


        llAddSchedule.setOnClickListener(this);
        llAdd.setOnClickListener(this);

        switchPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    switchPower.setText(getString(R.string.xml_on));
                else
                    switchPower.setText(getString(R.string.xml_off));
            }
        });

        syncRequest();

//        sampleAlarms();
//        alarmAdapter = new AdapterAlarms(Utility.getContext(),FragmentControlPlugs.this, alarms);
//        lvAlarms.setAdapter(alarmAdapter);
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    @Override
    public void onClick(View v) {
        if (v == llAddSchedule) {
            Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
            rlAdd.startAnimation(bottomUp);
            rlAdd.setVisibility(View.VISIBLE);
        }
        else if (v == llAdd) {
            Calendar c = Calendar.getInstance();
            if (timePicker.getCurrentHour() < c.get(Calendar.HOUR) || timePicker.getCurrentMinute() < c.get(Calendar.MINUTE) + 1) {
                timePicker.setCurrentHour(c.get(Calendar.HOUR));
                timePicker.setCurrentMinute(c.get(Calendar.MINUTE) + 1);

                Toast.makeText(Utility.getContext(), getString(R.string.xml_please_select_correct_time), Toast.LENGTH_LONG).show();
            }
            else {
                newAlarm = new Alarm();
                int intervalTime = calculateIntervalTime(c.get(Calendar.HOUR), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                Log.i("INTERVAL", "" + intervalTime);
                newAlarm.setAlarmId(0);
                newAlarm.setAlarmName("");
                newAlarm.setWhenSetTime(c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE));
                newAlarm.setExecuteTime(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                newAlarm.setIntervalTime(intervalTime);
                newAlarm.setPlugNum(plugs.get(spinnerPlug.getSelectedItemPosition()).getPlugNum());
                newAlarm.setPlugName(plugs.get(spinnerPlug.getSelectedItemPosition()).getPlugName());
                if (switchPower.isChecked())
                    newAlarm.setPlugStatus(AlarmStatus.ON);
                else
                    newAlarm.setPlugStatus(AlarmStatus.OFF);
// TODO bayad bere bare zakhireye etelaat
                Animation upBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
                rlAdd.startAnimation(upBottom);
                rlAdd.setVisibility(View.GONE);
            }
        }
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void powerRequest(final int position, final int plugNum, final String plugStatus) {
        if (smartPlugReq != null)
            smartPlugReq.cancel();

        final EmptyRequest emptyRequest = new EmptyRequest();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(emptyRequest);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Statics.simpleUrl + "/" + plugNum + "/" + plugStatus;

        smartPlugReq = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String jsonString = response.toString();
                Gson gson = new Gson();
                GeneralResponse generalResponse = gson.fromJson(jsonString, GeneralResponse.class);
                if (generalResponse.isOk()) {
                    if (plugStatus.equals(AlarmStatus.ON)) {
                        plugs.get(position).setPlugStatus(AlarmStatus.ON);
                        plugAdapter.notifyDataSetChanged();
                    }
                    else {
                        plugs.get(position).setPlugStatus(AlarmStatus.OFF);
                        plugAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error", "Error: " + error.networkResponse);
                if (error.networkResponse != null)
                    VolleyLog.e("Error", "Error: " + error.networkResponse.statusCode);
            }
        });
        smartPlugReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(smartPlugReq);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void syncRequest() {
        if (syncReq != null)
            syncReq.cancel();

        final EmptyRequest emptyRequest = new EmptyRequest();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(emptyRequest);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Statics.syncUrl;

        syncReq = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String jsonString = response.toString();
                Gson gson = new Gson();
                SyncResponse syncResponse = gson.fromJson(jsonString, SyncResponse.class);
                if (syncResponse.isOk()) {
                    plugs = new ArrayList();
                    for (int i = 0; i < syncResponse.getData().getPlugs().length; i++) {
                        Plug plug = new Plug();
                        plug.setPlugNum(syncResponse.getData().getPlugs()[i].getPlugNum());
                        plug.setPlugName(syncResponse.getData().getPlugs()[i].getPlugName());
                        plug.setPlugStatus(syncResponse.getData().getPlugs()[i].getPlugStatus());

                        plugs.add(plug);
                    }

                    plugAdapter = new AdapterPlugs(Utility.getContext(), FragmentControlPlugs.this, plugs);
                    lvPlugs.setAdapter(plugAdapter);

                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < plugs.size(); i++)
                        list.add(plugs.get(i).getPlugName());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPlug.setAdapter(dataAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error", "Error: " + error.networkResponse);
                if (error.networkResponse != null)
                    VolleyLog.e("Error", "Error: " + error.networkResponse.statusCode);
            }
        });
        syncReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(syncReq);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    private int calculateIntervalTime(int calendarHours, int calendarMins, int calendarSeconds, int pickerHours, int pickerMins) {
        int interval = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.set(Calendar.HOUR, calendarHours);
        c1.set(Calendar.MINUTE, calendarMins);
        c1.set(Calendar.SECOND, calendarSeconds);

        c2.set(Calendar.HOUR, pickerHours);
        c2.set(Calendar.MINUTE, pickerMins);
        c2.set(Calendar.SECOND, 0);

        interval = (int) ((c2.getTimeInMillis() - c1.getTimeInMillis())/1000);

        return interval;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    @Override
    public void onStop() {
        super.onStop();
        if (smartPlugReq != null)
            smartPlugReq.cancel();
        if (syncReq != null)
            syncReq.cancel();
    }
}
