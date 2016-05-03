package com.muhammadalikarami.smartplug;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.muhammadalikarami.smartplug.models.AlarmType;
import com.muhammadalikarami.smartplug.models.ScheduleType;
import com.muhammadalikarami.smartplug.objects.Alarm;

import org.json.JSONObject;

public class ActivityMain extends AppCompatActivity {

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get objects
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        // set objects
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());

        // set listeners
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

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
