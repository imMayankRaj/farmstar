package xendorp1.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.R;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class details_zonal_manager extends Fragment {
    private View rootview;
    private String id;
    private String zname;
    private TextView name_val,address_val,zone_val;
    private ImageView profilepic;
    public details_zonal_manager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview=inflater.inflate(R.layout.fragment_details, container, false);
        Bundle bundle=this.getArguments();
        id=null;
        zname=null;
        if(bundle!=null)
        {
            id=bundle.getString("id");
            zname=bundle.getString("zname");
        }
        name_val=rootview.findViewById(R.id.name_value);
        address_val=rootview.findViewById(R.id.address_value);
        zone_val=rootview.findViewById(R.id.zone_value);
        zone_val.setText(zname);
        profilepic=rootview.findViewById(R.id.profilepic);
        getData(id);
        return rootview;
    }

    void getData(final String id) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_ZONAL_MANAGER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    name_val.setText(jsonObject.getString("name"));
                    address_val.setText(jsonObject.getString("address"));
                    String prof=jsonObject.getString("profilepic");
                    if(!(prof==null||prof=="null"))
                        Glide.with(getActivity()).load(AppConfig.URL_PROFILE_PIC+prof).into(profilepic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "" + error.getMessage());
                Toast.makeText(getActivity(), "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "getzonalmanager");
    }

}
