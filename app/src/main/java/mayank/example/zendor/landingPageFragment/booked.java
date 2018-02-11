package mayank.example.zendor.landingPageFragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import mayank.example.zendor.ShowToast;
import mayank.example.zendor.URLclass;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.MainActivity.showToast;
import static mayank.example.zendor.MainActivity.tabLayout;
import static mayank.example.zendor.MainActivity.toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class booked extends Fragment {


    private SharedPreferences sharedPreferences;
    private ArrayList<bookedClass> bookedList;
    private RecyclerView bookedRecyclerView;
    private LinearLayoutManager llm;
    private LoadingClass lc;
    private SwipeRefreshLayout swipe;
    private LinearLayout layout;
    private TextView textView;

    public booked() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buyer, container, false);
        sharedPreferences = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);
        bookedRecyclerView = view.findViewById(R.id.bookedrecyclerView);
        swipe = view.findViewById(R.id.swipe);


        layout = view.findViewById(R.id.noDataLayout);
        textView = view.findViewById(R.id.text);


        lc = new LoadingClass(getActivity());
        llm = new LinearLayoutManager(getActivity());

        bookedRecyclerView.setLayoutManager(llm);
        bookedRecyclerView.setHasFixedSize(true);

        bookedList = new ArrayList<>();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                getPurchaseData();
            }
        });

        getPurchaseData();


        return view;
    }

    public void getPurchaseData(){
        layout.setVisibility(View.GONE);

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.BOOKED_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("bresponse", response);
                bookedList.clear();
                try {
                    JSONObject bookedParse = new JSONObject(response);
                    JSONArray bookedArray = bookedParse.getJSONArray("picked");
                    for(int i=0;i<bookedArray.length();i++){
                        JSONObject bookedDetails = bookedArray.getJSONObject(i);
                        String commodities = bookedDetails.getString("commodities");
                        String zname = bookedDetails.getString("zname");
                        String purchase_id = bookedDetails.getString("purchase_id");
                        String estimated_weight = bookedDetails.getString("estimated_weight");
                        String rate = bookedDetails.getString("rate");
                        String booked_ts = bookedDetails.getString("booked_ts");
                        String sellername = bookedDetails.getString("sellername");
                        String booker = bookedDetails.getString("booker");
                        bookedList.add(new bookedClass(commodities, purchase_id,sellername, zname, estimated_weight, rate, booked_ts));
                    }

                } catch (JSONException e) {
                   Log.e("bexception", e+"");
                   lc.dismissDialog();
                }



                bookedAdapter adapter = new bookedAdapter(getActivity(), bookedList);
                bookedRecyclerView.setAdapter(adapter);

                MainActivity.tabLayout.getTabAt(1).setText("BOOKED"+"("+bookedList.size()+")");


                if(bookedList.size() == 0){
                    layout.setVisibility(View.VISIBLE);
                    textView.setText("No Booked Data Available.");
                }


                lc.dismissDialog();
                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, booked.class.getName(), getActivity());

                lc.dismissDialog();
                swipe.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String pos = sharedPreferences.getString("position", "0");
                String id = sharedPreferences.getString("id","");
                String zoneid = sharedPreferences.getString("zid","");
                HashMap<String, String> map = new HashMap();
                Log.e("hiiii", pos+" "+id+" "+zoneid+" ");
                map.put("pos", pos);
                map.put("id", id);
                map.put("zid",zoneid);
                map.put("flag","bk");
                return map;
            }
            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
