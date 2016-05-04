package com.muhammadalikarami.smartplug;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class ActivityMain extends AppCompatActivity {

    public FrameLayout fragmentMain;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        // fragment management
        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentMain);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            FragmentDevices fragmentDevices = new FragmentDevices();
            ft.add(R.id.fragmentMain, fragmentDevices);
            ft.commit();
        }
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    }

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//    private void scheduleRequest(String scheduleType, Alarm alarm) {
//        if (smartPlugReq != null)
//            smartPlugReq.cancel();
//
//        RequestQueue queue = Volley.newRequestQueue(ActivityMain.this);
//        String url = Statics.scheduleUrl + scheduleType + "/" + alarm.getAlarmName()
//                + "&" + alarm.getWhenSetTime() + "&" + alarm.getExecuteTime()
//                + "&" + alarm.getPlugNum() + "&" + alarm.getAlarmType();
//
//        smartPlugReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                String jsonString = response.toString();
////                    Gson gson = new Gson();
////                    Toast.makeText(ActivityMain.this, jsonString, Toast.LENGTH_SHORT).show();
//                Log.i("INJA", "OK");
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("INJA", "NOK");
////                    new ShowServerErrorDialog(getActivity(), error.networkResponse);
//
//                VolleyLog.e("Error", "Error: " + error.getMessage());
//            }
//        });
//        smartPlugReq.setRetryPolicy(new DefaultRetryPolicy(Statics.DEFAULT_TIMEOUT_MS, Statics.DEFAULT_MAX_RETRIES, Statics.DEFAULT_BACKOFF_MULT));
//        queue.add(smartPlugReq);
//
//    }
//
//// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (smartPlugReq != null)
//            smartPlugReq.cancel();
//    }

}
