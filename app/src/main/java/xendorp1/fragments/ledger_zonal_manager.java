package xendorp1.fragments;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.onClickSeller.ledgerAdapter;
import mayank.example.zendor.onClickSeller.sellerLedger;

import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;


/**
 * A simple {@link Fragment} subclass.
 */
public class ledger_zonal_manager extends Fragment {

    private ListView ZmLedgerView;
    private TextView cb;
    private ArrayList<sellerLedger.ledgerClass> ledgerList;
    private TextView ZmNameAndZone;
    private String zmid;
    private TextView request;
    private ImageView addCredit;
    private SharedPreferences sharedPreferences;
    private LoadingClass lc;
    private String CB;



    public ledger_zonal_manager() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            Bundle bundle = getArguments();
            zmid = bundle.getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger_zonal_manager, container, false);

        ZmLedgerView = view.findViewById(R.id.ledgerView);
        cb = view.findViewById(R.id.cb);
        ZmNameAndZone = view.findViewById(R.id.sellerNameAndZone);
        ledgerList = new ArrayList<>();
        request = view.findViewById(R.id.request);
        addCredit = view.findViewById(R.id.addCredit);

        lc = new LoadingClass(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("details", MODE_PRIVATE);


        getZmCb();
        getZmLedger();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double cb = Double.parseDouble(CB);
                if(cb < 0 || cb == 0)
                    Toast.makeText(getActivity(), "Not Enough Credits.", Toast.LENGTH_SHORT).show();
                else
                    requestDialog();
            }
        });

        addCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCreditDialog();
            }
        });

        return view;
    }


    private void getZmCb(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("zm cb", response);
                try {
                    JSONObject json = new JSONObject(response);
                    CB = json.getString("current_balance");
                    cb.setText(CB);
                } catch (JSONException e) {
                    e.printStackTrace();
                    lc.dismissDialog();
                }
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error", error+"");
                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",zmid);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getZmLedger(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ZM_LEDGER , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ledgerList.clear();
                Log.e("zm ledger", response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray ledgerArray = json.getJSONArray("ledger");
                    for(int i =0;i<ledgerArray.length();i++){
                        JSONObject ledger = ledgerArray.getJSONObject(i);
                        String date = ledger.getString("date");
                        String pid = ledger.getString("pid");
                        String balance = ledger.getString("Balance");
                        String cd = ledger.getString("cd");
                        ledgerList.add(new sellerLedger.ledgerClass(date, pid, cd,'\u20B9'+ balance));
                    }

                    JSONObject details = json.getJSONObject("details");
                    String name = details.getString("zm");
                    String szone = details.getString("zm_zone");
                    ZmNameAndZone.setText(name+" - "+szone);
                } catch (JSONException e) {
                    Log.e("error", e+"");
                    e.printStackTrace();
                    lc.dismissDialog();
                }

                ledgerAdapter adapter = new ledgerAdapter(getActivity(),0, ledgerList);
                ZmLedgerView.setAdapter(adapter);
                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",zmid);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addCreditDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_credit_dialog);

        ImageView back = dialog.findViewById(R.id.back);
        final EditText amount = dialog.findViewById(R.id.amount);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView request = dialog.findViewById(R.id.pay);
        final EditText desc = dialog.findViewById(R.id.dsc);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amt = amount.getText().toString();
                String description = desc.getText().toString();
                addCredit(amt, description);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void requestDialog(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ledger_request);

        ImageView back = dialog.findViewById(R.id.back);
        final EditText amount = dialog.findViewById(R.id.amount);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView request = dialog.findViewById(R.id.request);
        final EditText desc = dialog.findViewById(R.id.dsc);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amt = amount.getText().toString();
                String description = desc.getText().toString();
                int a = Integer.parseInt(amt);
                double cb = Double.parseDouble(CB);
                if(a > cb)
                    sendRequest(amt, description);
                else
                    Toast.makeText(getActivity(), "Not Enough Credits.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendRequest(final String amt, final String desc){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                getZmCb();
                getZmLedger();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",zmid);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addCredit(final String amt, final String desc){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_CREDIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getZmCb();
                getZmLedger();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id","");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("sid",id);
                parameters.put("rid", zmid);
                parameters.put("amt", amt);
                parameters.put("des", desc);


                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


}
