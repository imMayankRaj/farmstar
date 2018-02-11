package mayank.example.zendor.landingPageFragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.MainActivity;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.apiConnect;
import mayank.example.zendor.commoditiesActivity;
import mayank.example.zendor.sellerDetailActivity;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.MainActivity.showToast;
import static mayank.example.zendor.MainActivity.tabLayout;
import static mayank.example.zendor.MainActivity.toast;

/**
 * A simple {@link Fragment} subclass.
 */

public class sellers extends Fragment implements View.OnClickListener{


    private View view;
    public static RecyclerView recyclerView;
    private LinearLayoutManager llm;
    public static ArrayList<sellerClass> arrayList;
    private RequestQueue requestQueue;
    private apiConnect connect;
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static sellerAdapter adapter;
    private LoadingClass lc;
    private LinearLayout layout;
    private TextView textView;


    public sellers() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sellers, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.addSeller);
        swipeRefreshLayout = view.findViewById(R.id.swipe);

        lc = new LoadingClass(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);

        llm = new LinearLayoutManager(getActivity());

        layout = view.findViewById(R.id.noDataLayout);
        textView = view.findViewById(R.id.text);


        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        arrayList = new ArrayList<>();
        connect = new apiConnect(getActivity(),"Seller");


        lc.showDialog();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getSellersData();
                MainActivity.searchSeller.setText("");
            }
        });

        fab.setOnClickListener(this);
        getSellersData();


        return view;
    }
    private void getSellersData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLERSDATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("seller res", response);
                layout.setVisibility(View.GONE);

                try {
                    arrayList.clear();
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");
                    if(status.equals("success")){
                        JSONArray array = json.getJSONArray("details");
                        for(int i=0; i<array.length(); i++){
                            JSONObject details = array.getJSONObject(i);
                            String name = details.getString("name");
                            String address = details.getString("address");
                            String seller_id = details.getString("seller_id");
                            String last_purchase = details.getString("last_purchase");
                            String number = details.getString("number");
                            arrayList.add(new sellerClass(seller_id, name, address, last_purchase, number));
                        }
                    }
                } catch (JSONException e) {
                    arrayList.clear();
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);

                }

                lc.dismissDialog();



                if(arrayList.size() == 0){
                    layout.setVisibility(View.VISIBLE);
                    textView.setText("No Sellers Available.");
                }


                adapter = new sellerAdapter(getActivity(), arrayList);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                MainActivity.tabLayout.getTabAt(0).setText("SELLERS"+"("+arrayList.size()+")");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellers.class.getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String pos = sharedPreferences.getString("position", "0");
                String id = sharedPreferences.getString("id","");
                String zoneid = sharedPreferences.getString("zid","");
                HashMap<String, String> map = new HashMap();
                map.put("pos", pos);
                map.put("id", id);
                map.put("zid",zoneid);
                return map;
            }

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }



    @Override
    public void onClick(View v) {

        startActivity(new Intent(getActivity(), sellerDetailActivity.class));
    }

    private String sortOnExpiry(sellerClass o) {
        String time = o.getLast_purchase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            Log.e("parsing error", e + "");
            e.printStackTrace();
        }
        long updated_time = date.getTime() ;
        return updated_time + "";

    }


}
