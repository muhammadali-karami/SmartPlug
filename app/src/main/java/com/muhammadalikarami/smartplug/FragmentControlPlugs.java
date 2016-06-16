package com.muhammadalikarami.smartplug;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
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
import com.muhammadalikarami.smartplug.utility.TimeUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    private JsonObjectRequest powerReq = null;
    private JsonObjectRequest addAlarmReq = null;
    private JsonObjectRequest cancelAlarmReq = null;
    private JsonObjectRequest syncReq = null;

    private RelativeLayout rlAdd;
    private TimePicker timePicker;
    private Spinner spinnerPlug;
    private Switch switchPower;
    private LinearLayout llAdd;
    private CustomTextView txtAdd;

    private Alarm newAlarm;

    private Runnable poolingRunnable;
    private Handler poolingHandler = new Handler();
    private int POOLING_DELAY = 5 * 1000;// 5 second

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_control_plugs, container, false);

        return rootView;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
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

        timePicker.setIs24HourView(true);

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
            if (alarms.size() != 20) {
                int intervalTime = TimeUtility.getDiffTimeForSetAlarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                TimeUtility.showRemainingTime();
                Calendar cal = Calendar.getInstance();

                // create alarm
                newAlarm = new Alarm();
                newAlarm.setAlarmId(0);
                newAlarm.setAlarmName("Alarm-Name");
                newAlarm.setWhenSetTime(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
                newAlarm.setExecuteTime(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                newAlarm.setPlugNum(plugs.get(spinnerPlug.getSelectedItemPosition()).getPlugNum());
                if (switchPower.isChecked())
                    newAlarm.setAlarmStatus(AlarmStatus.ON);
                else
                    newAlarm.setAlarmStatus(AlarmStatus.OFF);
                // end

                Animation upBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
                rlAdd.startAnimation(upBottom);
                rlAdd.setVisibility(View.GONE);

                addAlarmRequest(intervalTime);
            }
            else {
                Animation upBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
                rlAdd.startAnimation(upBottom);
                rlAdd.setVisibility(View.GONE);

                Toast.makeText(Utility.getContext(), getString(R.string.error_max_alarm_added), Toast.LENGTH_LONG).show();
            }
        }
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void powerRequest(final int position, final int plugNum, final String plugStatus) {
        if (powerReq != null)
            powerReq.cancel();

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

        powerReq = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
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
        powerReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(powerReq);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void addAlarmRequest(int intervalTime) {
        if (addAlarmReq != null)
            addAlarmReq.cancel();

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
        String url = Statics.scheduleUrl + "/" + newAlarm.getPlugNum() + "/" + newAlarm.getAlarmStatus() + "/" + newAlarm.getAlarmName() + "&" + newAlarm.getWhenSetTime()
                    + "&" + newAlarm.getExecuteTime() + "&" + intervalTime;
        Log.i("Status", url);
        addAlarmReq = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String jsonString = response.toString();
                Gson gson = new Gson();
                GeneralResponse generalResponse = gson.fromJson(jsonString, GeneralResponse.class);
                if (generalResponse.isOk()) {
                    Handler handler=new Handler();
                    Runnable runnable = new Runnable() {
                        public void run() {
                            syncRequest();
                        }
                    };
                    handler.postDelayed(runnable, 500);
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
        addAlarmReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(addAlarmReq);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void cancelAlarmRequest(final int position) {
        if (cancelAlarmReq != null)
            cancelAlarmReq.cancel();

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
        String url = Statics.cancelUrl + "/" + alarms.get(position).getAlarmId();
        Log.i("Status", url);
        cancelAlarmReq = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String jsonString = response.toString();
                Gson gson = new Gson();
                GeneralResponse generalResponse = gson.fromJson(jsonString, GeneralResponse.class);
                if (generalResponse.isOk()) {
                    alarmAdapter.remove(alarms.get(position));
                    alarmAdapter.notifyDataSetChanged();
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
        cancelAlarmReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(cancelAlarmReq);

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
                Log.i("What", jsonString);
                Gson gson = new Gson();
                SyncResponse syncResponse = gson.fromJson(jsonString, SyncResponse.class);
                if (syncResponse.isOk()) {
                    plugs = new ArrayList();
                    alarms = new ArrayList();

                    if (syncResponse.getData() != null) {
                        for (int i = 0; i < syncResponse.getData().getPlugs().length; i++) {
                            Plug plug = new Plug();
                            plug.setPlugNum(syncResponse.getData().getPlugs()[i].getPlugNum());
                            plug.setPlugName(syncResponse.getData().getPlugs()[i].getPlugName());
                            plug.setPlugStatus(syncResponse.getData().getPlugs()[i].getPlugStatus());

                            plugs.add(plug);
                        }

                        for (int i = 0; i < syncResponse.getData().getAlarms().length; i++) {
                            Alarm alarm = new Alarm();
                            alarm.setAlarmId(syncResponse.getData().getAlarms()[i].getAlarmId());
                            alarm.setPlugNum(syncResponse.getData().getAlarms()[i].getPlugNum());
                            alarm.setAlarmName(syncResponse.getData().getAlarms()[i].getAlarmName());
                            alarm.setWhenSetTime(syncResponse.getData().getAlarms()[i].getWhenSetTime());
                            alarm.setExecuteTime(syncResponse.getData().getAlarms()[i].getExecuteTime());
                            alarm.setAlarmStatus(syncResponse.getData().getAlarms()[i].getAlarmStatus());

                            alarms.add(alarm);
                        }

                        plugAdapter = new AdapterPlugs(Utility.getContext(), FragmentControlPlugs.this, plugs);
                        lvPlugs.setAdapter(plugAdapter);


                        Collections.sort(alarms, new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                Alarm p1 = (Alarm) o1;
                                Alarm p2 = (Alarm) o2;
                                return p1.getExecuteTime().compareToIgnoreCase(p2.getExecuteTime());
                            }
                        });
                        alarmAdapter = new AdapterAlarms(Utility.getContext(), FragmentControlPlugs.this, alarms);
                        lvAlarms.setAdapter(alarmAdapter);

                        List<String> list = new ArrayList<String>();
                        for (int i = 0; i < plugs.size(); i++)
                            list.add(plugs.get(i).getPlugName());
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPlug.setAdapter(dataAdapter);
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
        syncReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(syncReq);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public String getPlugName(int plugNum) {
        for (int i = 0; i < plugs.size(); i++) {
            if (plugs.get(i).getPlugNum() == plugNum)
                return plugs.get(i).getPlugName();
        }
        return "";
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    @Override
    public void onResume() {
        super.onResume();
        syncRequest();

        // pooling sync
//        poolingRunnable = new Runnable() {
//            public void run() {
//                syncRequest();
//                poolingHandler.postDelayed(poolingRunnable, POOLING_DELAY);
//            }
//        };
//        poolingHandler.postDelayed(poolingRunnable, POOLING_DELAY);
        // end
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    @Override
    public void onStop() {
        super.onStop();
        if (powerReq != null)
            powerReq.cancel();
        if (addAlarmReq != null)
            addAlarmReq.cancel();
        if (cancelAlarmReq != null)
            cancelAlarmReq.cancel();
        if (syncReq != null)
            syncReq.cancel();

        // cancel pooling
//        poolingHandler.removeCallbacks(poolingRunnable);
        // end
    }
}
