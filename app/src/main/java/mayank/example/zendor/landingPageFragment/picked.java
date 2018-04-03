package mayank.example.zendor.landingPageFragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.MainActivity;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.MainActivity.showToast;
import static mayank.example.zendor.MainActivity.tabLayout;
import static mayank.example.zendor.MainActivity.toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class picked extends Fragment {

    private ArrayList<pickedClass> pickedList;
    private SharedPreferences sharedPreferences;
    private RecyclerView pickedRecyclerView;
    private LinearLayoutManager llm;
    private LoadingClass lc;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layout;
    private TextView textView;
    private DatabaseReference mDatabase;
    private int count = 0;

    public picked() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picked, container, false);
        sharedPreferences = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);
        pickedRecyclerView = view.findViewById(R.id.pickedRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipe);


        lc = new LoadingClass(getActivity());
        llm = new LinearLayoutManager(getActivity());

        layout = view.findViewById(R.id.noDataLayout);
        textView = view.findViewById(R.id.text);


        pickedRecyclerView.setLayoutManager(llm);
        pickedRecyclerView.setHasFixedSize(true);

        String zid = sharedPreferences.getString("zid", "");


        String pos = sharedPreferences.getString("position", "");

        if (!pos.equals("0"))
            mDatabase = FirebaseDatabase.getInstance().getReference().child("picking").child(zid);
        else
            mDatabase = FirebaseDatabase.getInstance().getReference().child("picking");


        if(!pos.equals("0")) {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        long time = System.currentTimeMillis();
                        mDatabase.setValue(time + "");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        mDatabase.addValueEventListener(valueEventListener);
        //    pickedList = new ArrayList<>();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getPickedData();
            }
        });


        getPickedData();

        return view;
    }

    public void removeListener() {
        mDatabase.removeEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists() && count != 0)
                getPickedData();
            count++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void getPickedData() {
        lc.showDialog();
        layout.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PICKED_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("presponse", response);
                pickedList = new ArrayList<>();
                pickedList.clear();
                MainActivity.tabLayout.getTabAt(2).setText("SELLERS" + "(" + pickedList.size() + ")");

                try {
                    JSONObject pickedParse = new JSONObject(response);
                    JSONArray pickedArray = pickedParse.getJSONArray("picked");
                    for (int i = 0; i < pickedArray.length(); i++) {
                        JSONObject pickedDetails = pickedArray.getJSONObject(i);
                        String commodities = pickedDetails.getString("commodities");
                        String zname = pickedDetails.getString("zname");
                        String purchase_id = pickedDetails.getString("purchase_id");
                        String actual_weight = pickedDetails.getString("actual_weight");
                        String rate = pickedDetails.getString("rate");
                        String picked_ts = pickedDetails.getString("picked_ts");
                        String sellername = pickedDetails.getString("sellername");
                        String booker = pickedDetails.getString("booker");
                        pickedList.add(new pickedClass(commodities, purchase_id, sellername, zname, actual_weight, rate, picked_ts));
                    }

                } catch (JSONException e) {
                    Log.e("pexception", e + "");
                    lc.dismissDialog();

                }

                if (pickedList.size() == 0) {
                    layout.setVisibility(View.VISIBLE);
                    textView.setText("No Picked Data Available.");
                }


                pickedAdapter adapter = new pickedAdapter(getActivity(), pickedList);
                pickedRecyclerView.setAdapter(adapter);
                MainActivity.tabLayout.getTabAt(2).setText("PICKED" + "(" + pickedList.size() + ")");

                lc.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, picked.class.getName(), getActivity());


                swipeRefreshLayout.setRefreshing(false);
                lc.dismissDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String pos = sharedPreferences.getString("position", "0");
                String id = sharedPreferences.getString("id", "");
                String zoneid = sharedPreferences.getString("zid", "");
                HashMap<String, String> map = new HashMap();
                map.put("pos", pos);
                map.put("id", id);
                map.put("zid", zoneid);
                map.put("flag", "pk");
                return map;
            }

            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(valueEventListener);

    }
}
