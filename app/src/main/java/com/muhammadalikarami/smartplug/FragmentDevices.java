package com.muhammadalikarami.smartplug;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.muhammadalikarami.smartplug.objects.Plug;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 4/30/2016.
 */
public class FragmentDevices extends Fragment implements View.OnClickListener {

    private View rootView;
    private ListView lv;
    private RelativeLayout rlDevices, rlAddSchedule;
    private AdapterFindSmartPlugs simpleAdapter;
    private ArrayList<Plug> plugs;
    private JsonObjectRequest smartPlugReq = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_devices, container, false);

        init();

        return rootView;
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    private void init() {

        // get objects
        lv = (ListView) rootView.findViewById(R.id.lv);
        rlDevices = (RelativeLayout) rootView.findViewById(R.id.rlDevices);
        rlAddSchedule = (RelativeLayout) rootView.findViewById(R.id.rlAddSchedule);

        // set listeners
        rlDevices.setOnClickListener(this);
        rlAddSchedule.setOnClickListener(this);

        plugs = new ArrayList();
        Plug plug1 = new Plug();
        plug1.setPlugNum(1);
        plug1.setPlugName("Plug 1");
        plug1.setIsOn(true);

        Plug plug2 = new Plug();
        plug2.setPlugNum(2);
        plug2.setPlugName("Plug 2");
        plug2.setIsOn(false);

        plugs.add(plug1);
        plugs.add(plug2);

        simpleAdapter = new AdapterFindSmartPlugs(Utility.getContext(),FragmentDevices.this, plugs);
        lv.setAdapter(simpleAdapter);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    @Override
    public void onClick(View v) {
        if (v == rlDevices) {git
        }
        else if (v == rlAddSchedule) {

        }


//        else if (v == btnScheduleOn) {
//            Alarm alarm = new Alarm();
//            alarm.setAlarmName("MuhammadAliON");
//            alarm.setWhenSetTime(987654321L);
//            alarm.setExecuteTime(987654326L);
//            alarm.setPlugNum(1);
//            alarm.setAlarmType(AlarmType.ON);
//
////            scheduleRequest(ScheduleType.ONCE_ON_AFTER, alarm);
//        }
//        else if (v == btnScheduleOff) {
//            Alarm alarm = new Alarm();
//            alarm.setAlarmName("MuhammadAliOFF");
//            alarm.setWhenSetTime(987654321L);
//            alarm.setExecuteTime(987654326L);
//            alarm.setPlugNum(1);
//            alarm.setAlarmType(AlarmType.OFF);
//
////            scheduleRequest(ScheduleType.ONCE_OFF_AFTER, alarm);
//        }
//        else if (v == btnSync) {
//
//        }
    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void simpleRequest(int plugNum, String alarmType) {
        if (smartPlugReq != null)
            smartPlugReq.cancel();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Statics.simpleUrl + "/" + plugNum + "/" + alarmType;

        smartPlugReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String jsonString = response.toString();
//                    Gson gson = new Gson();
//                    Toast.makeText(ActivityMain.this, jsonString, Toast.LENGTH_SHORT).show();
                Log.i("INJA", "OK");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("INJA", "NOK");
//                    new ShowServerErrorDialog(getActivity(), error.networkResponse);

                VolleyLog.e("Error", "Error: " + error.getMessage());
            }
        });
        smartPlugReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
        queue.add(smartPlugReq);

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    @Override
    public void onStop() {
        super.onStop();
        if (smartPlugReq != null)
            smartPlugReq.cancel();
    }

}
