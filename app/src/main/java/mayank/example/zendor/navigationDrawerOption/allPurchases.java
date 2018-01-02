package mayank.example.zendor.navigationDrawerOption;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import mayank.example.zendor.landingPageFragment.picked;

import static mayank.example.zendor.MainActivity.showError;

public class allPurchases extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private SharedPreferences sharedPreferences;
    private ArrayList<purchaseClass> arrayList;
    private ImageView back;
    LoadingClass lc ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_purchases);
        recyclerView = findViewById(R.id.purchaseRecyclerView);
        sharedPreferences = getSharedPreferences("details", Context.MODE_PRIVATE);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        lc = new LoadingClass(this);

        getPurchaseData();
    }

    private void getPurchaseData(){
        lc.showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_PURCHASE_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    arrayList.clear();
                    JSONObject json = new JSONObject(response);
                    JSONArray jarray = json.getJSONArray("booked");
                    for(int i =0;i<jarray.length();i++){
                        JSONObject pdata = jarray.getJSONObject(i);
                        String commodity = pdata.getString("commodities");
                        String purchase_id = pdata.getString("purchase_id");
                        String estimated_weight = pdata.getString("estimated_weight");
                        String actual_weight = pdata.getString("actual_weight");
                        String rate = pdata.getString("rate");
                        String booked_ts = pdata.getString("booked_ts");
                        String sellername = pdata.getString("sellername");
                        String booker = pdata.getString("booker");
                        String picker = pdata.getString("picker");
                        String picked = pdata.getString("picked");
                        String roc_b = pdata.getString("roc_b");
                        String roc_p = pdata.getString("roc_p");
                        String collected_ts = pdata.getString("collected_ts");
                        String cancellation_ts4 = pdata.getString("cancellation_ts4");
                        String collected_weight = pdata.getString("collected_weight");
                        String collected_by = pdata.getString("collected_by");
                        String cancelled_by = pdata.getString("cancelled_by");
                        String flag = pdata.getString("flag");
                        arrayList.add(new purchaseClass(commodity, purchase_id, estimated_weight, actual_weight, rate, booked_ts, sellername,
                                        booker, picker, picked, roc_b, roc_p, collected_ts, cancellation_ts4, collected_weight, collected_by, cancelled_by, flag));
                    }

                    allPurchaseAdapter adapter = new allPurchaseAdapter(allPurchases.this,arrayList);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    Toast.makeText(allPurchases.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    lc.dismissDialog();
                }

                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(allPurchases.this, "Error Occured", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(allPurchases.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, allPurchases.this.getClass().getName(), allPurchases.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String pos = sharedPreferences.getString("position", "0");
                String id = sharedPreferences.getString("id","");
                String zoneid = sharedPreferences.getString("zid","");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("pos",pos);
                parameters.put("id", id);
                parameters.put("zid", zoneid);
                return parameters;
            }
        };
        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
