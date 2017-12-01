package mayank.example.zendor;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mayank on 10/31/2017.
 */

public class apiConnect {

    private Context context;
    private RequestQueue requestQueue;
    private String TAG;
    private HashMap<String ,String > parameters;

    public apiConnect(Context context,String TAG){
        this.context = context;
        this.TAG = TAG;
    }

    public RequestQueue getRequestQueue(){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

}
