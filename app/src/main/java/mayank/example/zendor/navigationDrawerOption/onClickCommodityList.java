package mayank.example.zendor.navigationDrawerOption;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import mayank.example.zendor.landingPageFragment.picked;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;

public class onClickCommodityList extends AppCompatActivity {


    private String comm;
    private RecyclerView commodityRview;
    private LinearLayoutManager llm;
    private ArrayList<commodityClass> arrayList;
    private TextView commodity;
    private SharedPreferences sharedPreferences;
    private LoadingClass lc;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_commodity_list);

        commodityRview = findViewById(R.id.indiCommodityList);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llm = new LinearLayoutManager(this);

        lc = new LoadingClass(this);

        commodityRview.setLayoutManager(llm);
        commodityRview.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        commodity = findViewById(R.id.commodity);

        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        if(getIntent().getExtras() != null)
            comm = getIntent().getExtras().getString("comm");

        commodity.setText(comm);


        getCommDetails();

    }

    private void getCommDetails(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_COMMODITY_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("com_response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray comm = json.getJSONArray("total");
                    for(int i =0;i<comm.length();i++){
                        JSONObject COMM = comm.getJSONObject(i);
                        String zname = COMM.getString("zonename");
                        String cw = COMM.getString("collected_weight");
                        String pw = COMM.getString("actual_weight");
                        String bw = COMM.getString("est_weight");
                        arrayList.add(new commodityClass(zname, bw, pw, cw));
                    }
                } catch (JSONException e) {
                    lc.dismissDialog();
                    Log.e("error_com", e+"");
                }

                commodityListAdapter adapter = new commodityListAdapter(onClickCommodityList.this, arrayList, comm);
                commodityRview.setAdapter(adapter);
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickCommodityList.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickCommodityList.this.getClass().getName(), onClickCommodityList.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id","");
                String pos = sharedPreferences.getString("position","");
                String zid = sharedPreferences.getString("zid","");

                Log.e("com", comm+" "+pos+" zid"+ zid+" "+ id);

                Map<String, String> map = new HashMap<>();
                map.put("comm", comm.trim());
                map.put("pos", pos);
                map.put("zid", zid);
                map.put("id", id);
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public class commodityClass{

        private String zname;
        private String bookedWeight;
        private String pickedWeight;
        private String collectedWeight;

        public commodityClass(String zname, String bookedWeight, String pickedWeight, String collectedWeight){

            this.zname = zname;
            this.bookedWeight = bookedWeight;
            this.pickedWeight = pickedWeight;
            this.collectedWeight = collectedWeight;
        }

        public String getBookedWeight() {
            return bookedWeight;
        }

        public String getCollectedWeight() {
            return collectedWeight;
        }

        public String getPickedWeight() {
            return pickedWeight;
        }

        public String getZname() {
            return zname;
        }
    }


}
