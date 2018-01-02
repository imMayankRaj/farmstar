package xendorp1.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.R;
import xendorp1.adapters.view_pager_adapter_zonal_manager;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;

import static android.content.ContentValues.TAG;
import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class zonal_manager extends Fragment {
    private View rootview;
    private Toolbar toolbar;
    private view_pager_adapter_zonal_manager view_pager_adapter_zonal_manager;
    private RelativeLayout disable,add_executive;
    private TabLayout pagerSlidingTabStrip;
    private ViewPager viewPager;
    private String id;
    private LinearLayout linearLayout;
    public zonal_manager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Bundle bundle=this.getArguments();
        int status=bundle.getInt("status");
        rootview= inflater.inflate(R.layout.fragment_zonal_manager, container, false);
        linearLayout=rootview.findViewById(R.id.buttons);
        if(status==0)
        {
            linearLayout.setVisibility(View.GONE);
        }

        toolbar=rootview.findViewById(R.id.toolbar1);
        toolbar.setTitle("Zonal Manager");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        disable=rootview.findViewById(R.id.disable);
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog =new AlertDialog.Builder(getActivity())
                        .setView(R.layout.disable_dialog)
                        .setCancelable(false)
                        .create();
                dialog.show();
                Button no =dialog.findViewById(R.id.no);
                Button yes=dialog.findViewById(R.id.yes);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                AppConfig.URL_DISABLE, new Response.Listener<String>() {
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
                                        Toast.makeText(getActivity(), "disabled", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
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
                                    showError(error, zonal_manager.class.getName(), getActivity());


                            }
                        }){

                            @Override
                            protected Map<String, String> getParams() {
                                // Posting params to register url
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id",bundle.getString("id"));
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(strReq, "getzones");
                    }
                });
            }
        });
        add_executive=rootview.findViewById(R.id.add_executive);
        add_executive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new add_executive();
                fragment.setArguments(bundle);
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.drawer_layout,fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        pagerSlidingTabStrip=rootview.findViewById(R.id.pagerSlidingTabStrip);
        viewPager=rootview.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        view_pager_adapter_zonal_manager=new view_pager_adapter_zonal_manager(getChildFragmentManager(),bundle);
        viewPager.setAdapter(view_pager_adapter_zonal_manager);
        pagerSlidingTabStrip.setupWithViewPager(viewPager);
        return rootview;
    }

}
