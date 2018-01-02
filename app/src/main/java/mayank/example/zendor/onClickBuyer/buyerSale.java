package mayank.example.zendor.onClickBuyer;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class buyerSale extends Fragment {


    public static String BUYER_ID = "buyer_id";
    private static String buyer_id;
    private static RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private static ArrayList<saleClass> arrayList;
    private static LoadingClass lc;

    public buyerSale() {
        // Required empty public constructor
    }

    public static buyerSale newInstance(String buyer_id) {
        buyerSale fragment = new buyerSale();
        Bundle bundle = new Bundle();
        bundle.putString(BUYER_ID, buyer_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buyer_id = getArguments().getString(BUYER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_sale, container, false);
        recyclerView = view.findViewById(R.id.saleRecyclerView);

        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        lc = new LoadingClass(getActivity());


        getSaleDetail(getActivity());

        return view;
    }

    public static void getSaleDetail(final Context context){
        lc.showDialog();
        arrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.BUYER_SALE_TAB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray saleArray = json.getJSONArray("values");
                    for (int i =0;i<saleArray.length();i++){
                        JSONObject sale = saleArray.getJSONObject(i);
                        String bname = sale.getString("bname");
                        String sid = sale.getString("saleid");
                        String commodity = sale.getString("commodities");
                        String rate = sale.getString("rate");
                        String flag = sale.getString("flag");
                        String ts = null;
                        String weight = null;
                        switch (flag){
                            case "di":
                                ts = sale.getString("ts1");
                                weight = sale.getString("w1");
                                break;
                            case "de":
                                ts = sale.getString("ts2");
                                weight = sale.getString("w2");
                                break;
                            case "ps":
                                ts = sale.getString("ts3");
                                weight = sale.getString("w2");
                                break;
                        }
                        if(!flag.equals("cn"))
                            arrayList.add(new saleClass(bname, sid, commodity, weight, rate, flag, ts));
                    }
                } catch (JSONException e) {
                    Log.e("json error", e+"");
                }


                saleAdapter adapter = new saleAdapter(context, arrayList, 1);
                recyclerView.setAdapter(adapter);
                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("buyer_id", buyer_id);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public static class saleClass{

        private String bname;
        private String sid;
        private String cname;
        private String weight;
        private String rate;
        private String ts;
        private String flag;

        public saleClass(String bname, String sid, String cname, String weight, String rate, String flag, String ts){

            this.bname = bname;
            this.sid = sid;
            this.cname = cname;
            this.weight = weight;
            this.rate = rate;
            this.flag = flag;
            this.ts = ts;
        }

        public String getFlag() {
            return flag;
        }

        public String getRate() {
            return rate;
        }

        public String getWeight() {
            return weight;
        }

        public String getCname() {
            return cname;
        }

        public String getBname() {
            return bname;
        }

        public String getSid() {
            return sid;
        }

        public String getTs() {
            return ts;
        }
    }

}
