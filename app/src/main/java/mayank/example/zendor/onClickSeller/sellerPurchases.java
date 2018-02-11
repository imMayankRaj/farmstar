package mayank.example.zendor.onClickSeller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.MainActivity.showToast;


public class sellerPurchases extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private static String SELLER_ID = "seller_id";
    private String seller_id;
    private ArrayList<sellerPurchaseClass> arrayList;
    private LoadingClass lc;
    private LinearLayout layout;
    private TextView textView;



    public sellerPurchases() {

    }

    public static sellerPurchases newInstance(String seller_id) {
        sellerPurchases fragment = new sellerPurchases();
        Bundle bundle = new Bundle();
        bundle.putString(SELLER_ID, seller_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            seller_id = getArguments().getString(SELLER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_purchases2, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(getActivity());
        arrayList = new ArrayList<>();

        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        layout = view.findViewById(R.id.noDataLayout);
        textView = view.findViewById(R.id.text);


        lc = new LoadingClass(getActivity());

        getSellerPurchaseData();

        return view;
    }

    private void getSellerPurchaseData(){
        lc.showDialog();
        layout.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_PURCHASE_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                arrayList.clear();
               try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray purchases = jsonObject.getJSONArray("booked");
                    for (int i =0;i<purchases.length();i++){
                        JSONObject sellerPurchases = purchases.getJSONObject(i);
                        String flag = sellerPurchases.getString("flag");
                        String pid = sellerPurchases.getString("purchase_id");

                        if(flag.equals("bk")){
                            String booked_ts = sellerPurchases.getString("booked_ts");
                            String est_weight = sellerPurchases.getString("estimated_weight");
                            String rate = sellerPurchases.getString("rate");
                            String commodity = sellerPurchases.getString("commodities");
                            String name = sellerPurchases.getString("booker");
                            arrayList.add(new sellerPurchaseClass(commodity, rate, est_weight, booked_ts, name, flag, pid));
                        }else if(flag.equals("pk")){
                            String picked_ts = sellerPurchases.getString("picked_ts");
                            String actual_weight = sellerPurchases.getString("actual_weight");
                            String rate = sellerPurchases.getString("rate");
                            String commodity = sellerPurchases.getString("commodities");
                            String name = sellerPurchases.getString("picker");
                            arrayList.add(new sellerPurchaseClass(commodity, rate, actual_weight, picked_ts, name, flag, pid));
                        }else if(flag.equals("co")){
                            String collected_ts = sellerPurchases.getString("collected_ts");
                            String collected_weight = sellerPurchases.getString("collected_weight");
                            String rate = sellerPurchases.getString("rate");
                            String commodity = sellerPurchases.getString("commodities");
                            String name = sellerPurchases.getString("collected_by");
                            arrayList.add(new sellerPurchaseClass(commodity, rate, collected_weight, collected_ts, name, flag, pid));
                        }else if(flag.equals("cn")){
                            String cancelled_ts = sellerPurchases.getString("cancelled_ts");
                            String roc_b = sellerPurchases.getString("roc_b");
                            String roc_p = sellerPurchases.getString("roc_p");
                            String rate = sellerPurchases.getString("rate");
                            String commodity = sellerPurchases.getString("commodities");
                            String name = sellerPurchases.getString("cancelled_by");
                            String roc = roc_b.equals("null") ?roc_p :roc_b;
                            arrayList.add(new sellerPurchaseClass(commodity, rate, roc, cancelled_ts, name, flag, pid));
                        }

                    }
                   ArrayList<sellerPurchaseClass> pk = new ArrayList<>();
                   ArrayList<sellerPurchaseClass> bk = new ArrayList<>();
                   ArrayList<sellerPurchaseClass> co = new ArrayList<>();
                   ArrayList<sellerPurchaseClass> cn = new ArrayList<>();
                   ArrayList<sellerPurchaseClass> finalList = new ArrayList<>();
                   for(int a=0;a<arrayList.size();a++){
                       if(arrayList.get(a).getFlag().equals("pk")){
                           pk.add(arrayList.get(a));
                       }else if(arrayList.get(a).getFlag().equals("bk")){
                           bk.add(arrayList.get(a));
                       }else if(arrayList.get(a).getFlag().equals("co")){
                           co.add(arrayList.get(a));
                       }else {
                           cn.add(arrayList.get(a));
                       }
                   }
                   finalList.addAll(pk);
                   finalList.addAll(bk);
                   finalList.addAll(co);
                   finalList.addAll(cn);

                   if(finalList.size() == 0){
                       layout.setVisibility(View.VISIBLE);
                       textView.setText("No Purchase For This Seller.");
                   }else
                       layout.setVisibility(View.GONE);

                    sellerPurchasesAdapter adapter = new sellerPurchasesAdapter(getActivity(), finalList);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                   Log.e("respo", e+"");
                   layout.setVisibility(View.VISIBLE);
                   textView.setText("No Purchases For This Seller.");

               }
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                Log.e("roor error", error+"");

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerPurchases.class.getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("seller_id", seller_id.substring(11));
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }



}
