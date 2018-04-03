package xendorp1.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayank.example.zendor.MainActivity;
import mayank.example.zendor.R;
import xendorp1.adapters.buyer_recycler_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.buyer_card;

import static android.content.ContentValues.TAG;
import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.MainActivity.showToast;

/**
 * A simple {@link Fragment} subclass.
 */
public class buyers extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View rootview;
    private Toolbar toolbar;
    private buyer_recycler_adapter buyer_recycler_adapter;
    private RelativeLayout next;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private String pos;
    public static TextView click;
    private EditText searchBuyer;
    private ImageView cut;
    private RelativeLayout view;
    private LinearLayout layout;
    private TextView textView;


    private List<buyer_card> buyer_cardList;

    public buyers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_buyers, container, false);

        sharedPreferences = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);
        pos = sharedPreferences.getString("position", "");
        // Inflate the layout for this fragment
        next = rootview.findViewById(R.id.next);
        toolbar = rootview.findViewById(R.id.toolbar1);
        click = rootview.findViewById(R.id.click);
        searchBuyer = rootview.findViewById(R.id.search);
        cut = rootview.findViewById(R.id.cut);
        view = rootview.findViewById(R.id.parent);

        layout = view.findViewById(R.id.noDataLayout);
        textView = view.findViewById(R.id.text);


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        recyclerView = rootview.findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        swipeRefreshLayout = rootview.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
       /* if(pos.equals("0"))
        getBuyer();
        else
            getBuyerFromZid();*/

        buyer_cardList = new ArrayList<>();

        getBuyer();
        next = rootview.findViewById(R.id.next);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new add_buyer();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.drawer_layout, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBuyer();
            }
        });


        searchBuyer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() != 0) {
                    cut.setImageDrawable(null);
                    cut.setBackgroundResource(R.drawable.ic_cancel_black_24dp);


                    Set<buyer_card> buyerTempList = new HashSet<>();

                    try {

                        long a = Long.parseLong(searchBuyer.getText().toString());
                        for (int i = 0; i < buyer_cardList.size(); i++) {
                            String num[] = buyer_cardList.get(i).getNumber().split(",");
                            for (int k = 0; k < num.length; k++) {
                                if (num[k].trim().contains(s.toString().trim()) || num[k].equals(s.toString())) {
                                    buyerTempList.add(buyer_cardList.get(i));
                                }
                            }
                        }

                    } catch (Exception e) {
                        for (int i = 0; i < buyer_cardList.size(); i++) {
                            String num = buyer_cardList.get(i).getName();
                            if (num.toLowerCase().contains(s.toString().toLowerCase())) {
                                buyerTempList.add(buyer_cardList.get(i));
                            }

                        }
                    }

                    Log.e("sadasd", buyerTempList.size()+"");
                    ArrayList<buyer_card> list = new ArrayList<>(buyerTempList);
                    buyer_recycler_adapter = new buyer_recycler_adapter(getActivity(), list);
                    recyclerView.setAdapter(buyer_recycler_adapter);

                } else {
                    recyclerView.setAdapter(null);
                    buyer_recycler_adapter = new buyer_recycler_adapter(getActivity(), buyer_cardList);
                    recyclerView.setAdapter(buyer_recycler_adapter);
                    cut.setImageDrawable(null);
                    cut.setBackgroundResource(R.drawable.ic_search_black_24dp);

                }

            }
        });

        cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(null);
                buyer_recycler_adapter = new buyer_recycler_adapter(getActivity(), buyer_cardList);
                recyclerView.setAdapter(buyer_recycler_adapter);
                searchBuyer.setText("");
                cut.setImageDrawable(null);
                cut.setBackgroundResource(R.drawable.ic_search_black_24dp);
            }
        });

        return rootview;
    }

    @Override
    public void onRefresh() {
      getBuyer();
    }


    public void getBuyer() {
        swipeRefreshLayout.setRefreshing(true);
        layout.setVisibility(View.GONE);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_BUYERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                buyer_cardList.clear();

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        buyer_card buyer_card = new buyer_card();
                        buyer_card.setName(jsonObject.getString("name"));
                        buyer_card.setBuyer_id(jsonObject.getString("id"));
                        buyer_card.setNumber(jsonObject.getString("mob"));
                        buyer_card.setCb(jsonObject.getString("cb"));
                        buyer_cardList.add(buyer_card);
                    }
                    buyer_recycler_adapter = new buyer_recycler_adapter(getActivity(), buyer_cardList);
                    recyclerView.setAdapter(buyer_recycler_adapter);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    buyer_cardList.clear();
                    recyclerView.setAdapter(null);
                    swipeRefreshLayout.setRefreshing(false);
                    layout.setVisibility(View.VISIBLE);
                    textView.setText("No Buyers Available.");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                buyer_cardList.clear();
                recyclerView.setAdapter(null);
                swipeRefreshLayout.setRefreshing(false);

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyers.class.getName(), getActivity());


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "getbuyers");
    }

    private void getBuyerFromZid() {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_BUYERS_FROM_ZID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    List<buyer_card> buyer_cardList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        buyer_card buyer_card = new buyer_card();
                        buyer_card.setName(jsonObject.getString("name"));
                        buyer_card.setBuyer_id(jsonObject.getString("id"));
                        buyer_cardList.add(buyer_card);
                    }
                    buyer_recycler_adapter = new buyer_recycler_adapter(getActivity(), buyer_cardList);
                    recyclerView.setAdapter(buyer_recycler_adapter);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    recyclerView.setAdapter(null);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.setAdapter(null);
                swipeRefreshLayout.setRefreshing(false);

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyers.class.getName(), getActivity());


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                String zid = sharedPreferences.getString("zid", "");
                params.put("zid", zid);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "getbuyers");
    }
}
