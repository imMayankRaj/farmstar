package xendorp1.fragments;


import android.os.Bundle;
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


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayank.example.zendor.R;
import xendorp1.adapters.executive_recycler_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zonal_manager_card;

import static android.content.ContentValues.TAG;
import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.MainActivity.showToast;
import static xendorp1.fragments.zonal_manager.pagerSlidingTabStrip;

/**
 * A simple {@link Fragment} subclass.
 */
public class executive_zonal_manager extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View rootview;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private executive_recycler_adapter executive_recycler_adapter;
    private String id;
    private List<zonal_manager_card> zonal_manager_cardList;
    private LinearLayout layout;
    private TextView textView;


    public executive_zonal_manager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =inflater.inflate(R.layout.fragment_executive_zonal_manager, container, false);
        Bundle bundle=this.getArguments();
        id=null;
        if(bundle!=null)
        {
            id=bundle.getString("id");
        }
        recyclerView =rootview.findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        swipeRefreshLayout=rootview.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        zonal_manager_cardList = new ArrayList<>();

        layout = rootview.findViewById(R.id.noDataLayout);
        textView = rootview.findViewById(R.id.text);



        getExecutives();
        return rootview;
    }
    void getExecutives()
    {
        layout.setVisibility(View.GONE);

        zonal_manager_cardList.clear();
        swipeRefreshLayout.setRefreshing(true);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_EXECUTIVES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        zonal_manager_card zonal_manager_card=new zonal_manager_card();
                        zonal_manager_card.setZid(jsonObject.getString("zid"));
                        zonal_manager_card.setName(jsonObject.getString("name"));
                        zonal_manager_card.setUsername(jsonObject.getString("mob"));
                        zonal_manager_card.setZone_name(jsonObject.getString("zname"));
                        zonal_manager_card.setPassword(jsonObject.getString("pwd"));
                        zonal_manager_card.setStatus(jsonObject.getInt("status"));
                        zonal_manager_card.setId(jsonObject.getString("id"));
                        zonal_manager_card.setCb(jsonObject.getString("cb"));
                        zonal_manager_card.setNumber(jsonObject.getString("mob")+","+jsonObject.getString("othermob"));
                        zonal_manager_cardList.add(zonal_manager_card);
                    }
                    executive_recycler_adapter=new executive_recycler_adapter(getActivity(),zonal_manager_cardList,executive_zonal_manager.this);
                    recyclerView.setAdapter(executive_recycler_adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setAdapter(null);
                    e.printStackTrace();
                    layout.setVisibility(View.VISIBLE);
                    textView.setText("No Executives .");

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "" + error.getMessage());
                recyclerView.setAdapter(null);
                Toast.makeText(getActivity(), "Some error occured. Please try again", Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, executive_zonal_manager.class.getName(), getActivity());


            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "getexecutives");
    }
    @Override
    public void onRefresh() {
        getExecutives();
    }
}
