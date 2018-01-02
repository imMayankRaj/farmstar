package xendorp1.fragments;


import android.content.SharedPreferences;
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
import xendorp1.adapters.executive_recycler_adapter;
import xendorp1.adapters.zonal_manager_recycler_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zonal_manager_card;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class executives extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View rootview;
    private Toolbar toolbar;
    private RelativeLayout next;
    private xendorp1.adapters.executive_recycler_adapter executive_recycler_adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String id;
    public static TextView click1;

    public executives() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview=inflater.inflate(R.layout.fragment_executives, container, false);
        final Bundle bundle=this.getArguments();
        id=null;
        if(bundle!=null)
        {
            id=bundle.getString("id");
        }
        Log.d("id",id);
        next=rootview.findViewById(R.id.next);
        click1 = rootview.findViewById(R.id.executivesClick);
        toolbar=rootview.findViewById(R.id.toolbar1);
        toolbar.setTitle("Executives");
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
                Fragment fragment = new add_executive();
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("details",MODE_PRIVATE);
                String id= sharedPreferences.getString("id","");
                String zid=sharedPreferences.getString("zid","");
                Log.d("id",id);
                Bundle bundle1=new Bundle();
                bundle1.putString("id",id);
                bundle1.putString("zid",zid);
                fragment.setArguments(bundle1);
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.drawer_layout,fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        click1.setOnClickListener(new View.OnClickListener() {
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
                AppConfig.URL_GET_EXECUTIVES, new Response.Listener<String>() {
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
                        zonal_manager_card.setNumber(jsonObject.getString("mob")+","+jsonObject.getString("othermob"));
                        zonal_manager_cardList.add(zonal_manager_card);
                    }
                    executive_recycler_adapter=new executive_recycler_adapter(getActivity(),zonal_manager_cardList,executives.this);
                    recyclerView.setAdapter(executive_recycler_adapter);
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
                    showError(error, executives.class.getName(), getActivity());


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
        getZonalManagers();
    }

}