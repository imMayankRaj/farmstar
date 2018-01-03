package xendorp1.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import xendorp1.adapters.zonal_manager_recycler_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zonal_manager_card;

import static android.content.ContentValues.TAG;
import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class zonal_managers extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View rootview;
    private Toolbar toolbar;
    private RelativeLayout next;
    private zonal_manager_recycler_adapter zonal_manager_recycler_adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String zid;
    public static TextView click;

    public zonal_managers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_zonal_managers, container, false);
        final Bundle bundle=this.getArguments();
        zid=null;
        if(bundle!=null)
        {
            zid=bundle.getString("zid");
        }
        Log.d("zid",zid);
        next=rootview.findViewById(R.id.next);
        click = rootview.findViewById(R.id.click);
        toolbar=rootview.findViewById(R.id.toolbar1);
        toolbar.setTitle("Zonal Managers");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        recyclerView=rootview.findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        swipeRefreshLayout=rootview.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        next=rootview.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new add_zonal_manager();
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.drawer_layout,fragment);

                Bundle bundle1 = new Bundle();
                bundle1.putString("zid", zid);
                fragment.setArguments(bundle1);

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getZonalManagers();
            }
        });

        getZonalManagers();
        return rootview;
    }
    void getZonalManagers()
    {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_ZONAL_MANAGERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    List<zonal_manager_card> zonal_manager_cardList=new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        zonal_manager_card zonal_manager_card=new zonal_manager_card();
                        zonal_manager_card.setName(jsonObject.getString("name"));
                        zonal_manager_card.setZid(jsonObject.getString("zid"));
                        zonal_manager_card.setUsername(jsonObject.getString("mob"));
                        zonal_manager_card.setZone_name(jsonObject.getString("zname"));
                        zonal_manager_card.setPassword(jsonObject.getString("pwd"));
                        zonal_manager_card.setStatus(jsonObject.getInt("status"));
                        zonal_manager_card.setId(jsonObject.getString("id"));
                        zonal_manager_card.setCb(jsonObject.getString("cb"));
                        zonal_manager_card.setNumber(jsonObject.getString("mob")+","+jsonObject.getString("othermob"));
                        zonal_manager_cardList.add(zonal_manager_card);
                    }
                    zonal_manager_recycler_adapter=new zonal_manager_recycler_adapter(getActivity(),zonal_manager_cardList,zonal_managers.this);
                    recyclerView.setAdapter(zonal_manager_recycler_adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } catch (Exception e) {
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setAdapter(null);
                    e.printStackTrace();
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
                    showError(error, zonal_managers.class.getName(), getActivity());


            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("zid",zid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "getzonalmanagers");
    }
    @Override
    public void onRefresh() {
        getZonalManagers();
    }

}
