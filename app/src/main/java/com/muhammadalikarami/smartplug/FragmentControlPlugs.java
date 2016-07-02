package com.muhammadalikarami.smartplug;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muhammadalikarami.smartplug.models.AlarmStatus;
import com.muhammadalikarami.smartplug.objects.Alarm;
import com.muhammadalikarami.smartplug.objects.Plug;
import com.muhammadalikarami.smartplug.request.EmptyRequest;
import com.muhammadalikarami.smartplug.response.GeneralResponse;
import com.muhammadalikarami.smartplug.response.SyncResponse;
import com.muhammadalikarami.smartplug.utility.TimeUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Admin on 4/30/2016.
 */
public class FragmentControlPlugs extends Fragment implements View.OnClickListener {

    private View rootView;
    private RelativeLayout rlFooter;
    private LinearLayout llAddSchedule;
    private TextView txtAddSchedule;
    private ListView lvPlugs;
    private ListView lvAlarms;
    private View blackLine;
    private View greenLine;
    private AdapterPlugs plugAdapter;
    private AdapterAlarms alarmAdapter;
    private ArrayList<Plug> plugs = new ArrayList();
    private ArrayList<Alarm> alarms = new ArrayList();
    private CircularProgressView progress;
    private JsonObjectRequest powerReq = null;
    private JsonObjectRequest addAlarmReq = null;
    private JsonObjectRequest cancelAlarmReq = null;
    private JsonObjectRequest syncReq = null;

    private RelativeLayout rlAdd;
    private TimePicker timePicker;
    private Spinner spinnerPlug;
    private Switch switchPower;
    private LinearLayout llAdd;
    private TextView txtAdd;
    private String NOT_SELECTED_PLUG = "-----";

    private RelativeLayout rlNotAvailable;
    private ImageView imgSync;

    private Alarm newAlarm;
    private int MAX_ALARM = 15;

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
        blackLine = rootView.findViewById(R.id.blackLine);
        greenLine = rootView.findViewById(R.id.blackLine);
        rlFooter = (RelativeLayout) rootView.findViewById(R.id.rlFooter);
        llAddSchedule = (LinearLayout) rootView.findViewById(R.id.llAddSchedule);
        txtAddSchedule = (TextView) rootView.findViewById(R.id.txtAddSchedule);
        progress = (CircularProgressView) rootView.findViewById(R.id.progress);
        // add page
        rlAdd = (RelativeLayout) rootView.findViewById(R.id.rlAdd);
        timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
        spinnerPlug = (Spinner) rootView.findViewById(R.id.spinnerPlug);
        switchPower = (Switch) rootView.findViewById(R.id.switchPower);
        llAdd = (LinearLayout) rootView.findViewById(R.id.llAdd);
        txtAdd = (TextView) rootView.findViewById(R.id.txtAdd);
        // not available page
        rlNotAvailable = (RelativeLayout) rootView.findViewById(R.id.rlNotAvailable);
        imgSync = (ImageView) rootView.findViewById(R.id.imgSync);

        timePicker.setIs24HourView(true);

        // set listeners
        llAddSchedule.setOnClickListener(this);
        llAdd.setOnClickListener(this);
        imgSync.setOnClickListener(this);

        switchPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    switchPower.setText(getString(R.string.xml_on));
                else
                    switchPower.setText(getString(R.string.xml_off));
            }
        });
        // end

        List<String> list = new ArrayList<String>();
        list.add(NOT_SELECTED_PLUG);
        for (int i = 0; i < plugs.size(); i++)
            list.add(plugs.get(i).getPlugName());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlug.setAdapter(dataAdapter);

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
            if (alarms.size() < MAX_ALARM) {
                // create alarm
                if (spinnerPlug.getSelectedItemPosition() != 0) {
                    int intervalTime = TimeUtility.getDiffTimeForSetAlarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    Calendar cal = Calendar.getInstance();

                    newAlarm = new Alarm();
                    newAlarm.setAlarmId(0);
                    newAlarm.setAlarmName("Alarm-Name");
                    newAlarm.setWhenSetTime(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
                    newAlarm.setExecuteTime(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                    newAlarm.setPlugNum(plugs.get((spinnerPlug.getSelectedItemPosition() - 1)).getPlugNum());
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
                    Toast.makeText(Utility.getContext(), getString(R.string.error_please_select_a_plug), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Animation upBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
                rlAdd.startAnimation(upBottom);
                rlAdd.setVisibility(View.GONE);

                Toast.makeText(Utility.getContext(), getString(R.string.error_max_alarm_added), Toast.LENGTH_LONG).show();
            }
        }
        else if (v == imgSync) {
            syncRequest();
        }
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    private void showRefreshPage() {
        rlAdd.setVisibility(View.GONE);
        lvPlugs.setVisibility(View.GONE);
        lvAlarms.setVisibility(View.GONE);
        blackLine.setVisibility(View.GONE);
        greenLine.setVisibility(View.GONE);
        llAddSchedule.setVisibility(View.GONE);

        rlNotAvailable.setVisibility(View.VISIBLE);
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    private void hideRefreshPage() {
        rlNotAvailable.setVisibility(View.GONE);

        lvPlugs.setVisibility(View.VISIBLE);
        lvAlarms.setVisibility(View.VISIBLE);
        blackLine.setVisibility(View.VISIBLE);
        greenLine.setVisibility(View.VISIBLE);
        llAddSchedule.setVisibility(View.VISIBLE);
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void powerRequest(final int position, final int plugNum, final String plugStatus) {
        hideRefreshPage();
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
                showRefreshPage();
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
        hideRefreshPage();
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
//        Log.i(Statics.TAG, url);
        addAlarmReq = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String jsonString = response.toString();
                Gson gson = new Gson();
                GeneralResponse generalResponse = gson.fromJson(jsonString, GeneralResponse.class);
                if (generalResponse.isOk()) {
                    TimeUtility.showRemainingTime();

                    Handler handler=new Handler();
                    Runnable runnable = new Runnable() {
                        public void run() {
                            syncRequest();
                        }
                    };
                    handler.postDelayed(runnable, 500);
                }
                else {
                    for (int i = 0; i < generalResponse.getMessages().length; i++) {
                        switch (generalResponse.getMessages()[i]) {
                            case Statics.ERROR_MAX_ALARM_ADDED:
//                                Log.i(Statics.TAG, getString(R.string.error_max_alarm_added));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showRefreshPage();
                VolleyLog.e("Error", "Error: " + error.networkResponse);
                if (error.networkResponse != null) {
                    VolleyLog.e("Error", "Error: " + error.networkResponse.statusCode);
                }
            }
        });
        addAlarmReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(addAlarmReq);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void cancelAlarmRequest(final int position) {
        hideRefreshPage();
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
//        Log.i("Status", url);
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
                showRefreshPage();
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
        hideRefreshPage();
        progress.startAnimation();
        progress.setVisibility(View.VISIBLE);
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
                hideRefreshPage();
                String jsonString = response.toString();
//                Log.i(Statics.TAG, jsonString);
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


//                        Collections.sort(alarms, new Comparator() {
//                            @Override
//                            public int compare(Object o1, Object o2) {
//                                Alarm p1 = (Alarm) o1;
//                                Alarm p2 = (Alarm) o2;
//                                return p1.getExecuteTime().compareToIgnoreCase(p2.getExecuteTime());
//                            }
//                        });
                        Collections.reverse(alarms);
                        alarmAdapter = new AdapterAlarms(Utility.getContext(), FragmentControlPlugs.this, alarms);
                        lvAlarms.setAdapter(alarmAdapter);

                        List<String> list = new ArrayList<String>();
                        list.add(NOT_SELECTED_PLUG);
                        for (int i = 0; i < plugs.size(); i++)
                            list.add(plugs.get(i).getPlugName());
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPlug.setAdapter(dataAdapter);

                        progress.stopAnimation();
                        progress.setVisibility(View.GONE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.stopAnimation();
                progress.setVisibility(View.GONE);
                showRefreshPage();
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

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    @Override
    public void onResume() {
        super.onResume();
        syncRequest();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
                    if (rlAdd.getVisibility() == View.VISIBLE) {
                        Animation upBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
                        rlAdd.startAnimation(upBottom);
                        rlAdd.setVisibility(View.GONE);
                    }
                    else {
                        getActivity().finish();
                    }
                    return true;
                }

                return false;
            }
        });
    }
}
