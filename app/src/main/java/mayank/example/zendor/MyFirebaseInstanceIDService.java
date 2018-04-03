package mayank.example.zendor;

import android.app.Service;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import xendorp1.application_classes.AppController;

/**
 * Created by mayan on 3/24/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    SharedPreferences sharedPreferences;
    public MyFirebaseInstanceIDService() {

    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendToken(refreshedToken);
    }


    public void sendToken(final String token){
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
               Map<String, String> params= new HashMap<>();
               params.put("token", token);
               params.put("id", sharedPreferences.getString("id", ""));
               return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
