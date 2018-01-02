package xendorp1.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayank.example.zendor.R;
import xendorp1.adapters.zone_card_recycler_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zone_card;

import static android.content.ContentValues.TAG;
import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */

public class workforce extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View rootview;
    private Toolbar toolbar;
    private RelativeLayout next;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private zone_card_recycler_adapter zone_card_recycler_adapter;
    public workforce() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview=inflater.inflate(R.layout.fragment_workforce,container,false);
        next=rootview.findViewById(R.id.next);
        toolbar=rootview.findViewById(R.id.toolbar1);
        recyclerView=rootview.findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        swipeRefreshLayout=rootview.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        getZones();
        toolbar.setTitle("Select Zone");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog=new AlertDialog.Builder(getActivity())
                        .setView(R.layout.add_zone)
                        .setCancelable(false)
                        .create();
                dialog.show();
                RelativeLayout submit=dialog.findViewById(R.id.submit);
                RelativeLayout cancel=dialog.findViewById(R.id.cancel);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                        final EditText zone_name=dialog.findViewById(R.id.zone_name_value);
                        final EditText latitude=dialog.findViewById(R.id.lat_value);
                        final EditText longitude=dialog.findViewById(R.id.long_value);
                        if(zone_name.getText().length()==0||latitude.getText().length()==0||longitude.getText().length()==0)
                        {
                            Toast.makeText(getActivity(), "Please enter values of all the fields", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            StringRequest strReq = new StringRequest(Request.Method.POST,
                                    AppConfig.URL_ADD_ZONE, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "Register Response: " + response.toString());
                                    try {
                                        JSONObject jobj=new JSONObject(response);
                                        boolean error=jobj.getBoolean("error");
                                        if(error)
                                        {
                                            Toast.makeText(getActivity(), "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), "Successfully added zone", Toast.LENGTH_SHORT).show();
                                            getZones();
                                            dialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "" + error.getMessage());
                                    Toast.makeText(getActivity(), "Some error occured. Please try again", Toast.LENGTH_SHORT).show();

                                    if (error instanceof TimeoutError) {
                                        Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                                    } else
                                        showError(error, workforce.class.getName(), getActivity());

                                }
                            }){

                                @Override
                                protected Map<String, String> getParams() {
                                    // Posting params to register url
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("zname",zone_name.getText().toString());
                                    params.put("lati",latitude.getText().toString());
                                    params.put("longi",longitude.getText().toString());
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(strReq, "getzones");
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                Toolbar toolbar1=dialog.findViewById(R.id.toolbar2);
                toolbar1.setTitle("Add Zone");
                toolbar1.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }
        });
        return rootview;
    }
    @Override
    public void onRefresh() {
        getZones();
    }
    void getZones()
    {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_ZONES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    List<zone_card> zonelist=new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        zone_card zone_card = new zone_card();
                        zone_card.setZone_name(jsonObject.getString("zname"));
                        zone_card.setZone_id(jsonObject.getString("zid"));
                        zonelist.add(zone_card);
                    }

                    zone_card_recycler_adapter=new zone_card_recycler_adapter(getActivity(),zonelist,workforce.this);
                    recyclerView.setAdapter(zone_card_recycler_adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setAdapter(null);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(null);

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, workforce.class.getName(), getActivity());


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "getzones");

    }

}
