package mayank.example.zendor.onClickSeller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.onClickPicked.onClickPickedCard;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;

public class addRemoveActivity extends AppCompatActivity {

    private TextView submit;
    private Toolbar toolbar;
    private ArrayList<commodity> arrayList;
    private LinearLayout layout1, layout2, layout3, layout4, layout5, layout6, layout7, layout8, layout9, layout10, layout11, layout12;
    private String commo[];
    private String seller_id;
    private LoadingClass lc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodities);

        seller_id = getIntent().getStringExtra("seller_id");


        arrayList = new ArrayList<>();

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);
        layout7 = findViewById(R.id.layout7);
        layout8 = findViewById(R.id.layout8);
        layout9 = findViewById(R.id.layout9);
        layout10 = findViewById(R.id.layout10);
        layout11 = findViewById(R.id.layout11);
        layout12 = findViewById(R.id.layout12);

        lc = new LoadingClass(this);
        getCommodity();

        submit = findViewById(R.id.submit);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addComm = "";
                for (int i = 0; i < arrayList.size(); i++) {
                    if (commo[i] == null) {
                        commo[i] = "";
                    }
                }

                for (int i = 0; i < arrayList.size(); i++) {
                    if (commo[i].length() != 0) {
                        Log.e("Asdasdas", commo[i]);
                        addComm = addComm.concat(commo[i] + ",");
                    }
                }
                if (addComm.length() == 0 || addComm.length() == 4) {
                    Toast.makeText(addRemoveActivity.this, "Select a commodity.", Toast.LENGTH_SHORT).show();
                } else {
                    addComm = addComm.replaceAll("null", "");
                    addComm = addComm.substring(0, addComm.lastIndexOf(","));
                    updateComm(addComm);
                }

            }
        });
    }

    private void getCommodity() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLclass.GET_COMMODITIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                arrayList.clear();
                Log.e("response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("values");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String commodities = object.getString("commodities");
                        String pic_path = object.getString("pic_path");
                        String types = object.getString("types");
                        arrayList.add(new commodity(commodities, pic_path, types));
                    }
                } catch (JSONException e) {
                    Log.e("rrrr", e + "");
                }

                commo = new String[arrayList.size()];
                startLoop(arrayList);

                getSellerCommodities();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                if (error instanceof TimeoutError) {
                    Toast.makeText(addRemoveActivity.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, addRemoveActivity.this.getClass().getName(), addRemoveActivity.this);


                Log.e("server", error + "");
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public class commodity {

        private String commodity;
        private String pic_path;
        private String type;

        public commodity(String commodity, String pic_path, String type) {
            this.commodity = commodity;
            this.pic_path = pic_path;
            this.type = type;
        }

        public String getCommodity() {
            return commodity;
        }

        public String getPic_path() {
            return pic_path;
        }

        public String getType() {
            return type;
        }
    }

    private void startLoop(ArrayList<commodity> list) {
        for (int i = 0; i < list.size(); i++) {
            checkLayout(list.get(i), i);
        }
    }

    private void checkLayout(commodity current, int id) {
        switch (current.getType()) {

            case "Vegetables":
                makeView(layout1, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Fruits":
                makeView(layout2, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Pulses":
                makeView(layout3, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Cereals":
                makeView(layout4, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Cash Crops":
                makeView(layout5, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Oil Seeds":
                makeView(layout6, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Spices":
                makeView(layout7, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Forage Crops":
                makeView(layout8, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Flowers":
                makeView(layout9, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Dairy":
                makeView(layout10, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Live Stocks":
                makeView(layout11, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;

            case "Others":
                makeView(layout12, current.getCommodity() + id, current.getPic_path(), current.getCommodity());
                break;
        }


    }

    private void makeView(LinearLayout layout, String id, String path, String name) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_view, layout, false);
        view.setTag(R.string.commodity, id);
        view.setTag(name);

        view.setOnClickListener(onClickListener);
        ImageView img = view.findViewById(R.id.img);
        TextView na = view.findViewById(R.id.name);

        na.setText(name);

        Glide.with(this).load(URLclass.COMMODITY_PIC_PATH + path).into(img);


        layout.addView(view);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id = (String) v.getTag(R.string.commodity);

            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(id);

            String match = null;
            while (matcher.find()) {
                match = matcher.group();
            }
            int a = Integer.parseInt(match);

            CheckBox cb = v.findViewById(R.id.cb);
            if (cb.isChecked()) {
                commo[a] = "";
                cb.setChecked(false);
                cb.setVisibility(View.GONE);
            } else {
                cb.setVisibility(View.VISIBLE);
                cb.setChecked(true);
                commo[a] = id.replaceAll("[0-9]", "");
            }
        }
    };


    private void getSellerCommodities() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_SELLER_COMMODITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] seller_comm = response.split(",");
                checkCommodities(seller_comm);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(addRemoveActivity.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, addRemoveActivity.this.getClass().getName(), addRemoveActivity.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap();
                params.put("id", seller_id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void checkCommodities(String[] seller_comm) {

        for (int i = 0; i < seller_comm.length; i++) {
           checkCB(seller_comm[i]);
        }

        lc.dismissDialog();

    }

    private void checkCB(String comm){

        LinearLayout layout[] = new LinearLayout[]{layout1, layout2, layout3, layout4, layout5, layout6, layout7, layout8, layout9, layout10, layout11};
        for(int i = 0;i<layout.length;i++){
            View v = layout[i].findViewWithTag(comm);
            if(v != null){
                setCheck(v);
            }
        }
    }

    private void setCheck(View v) {
        String id = (String) v.getTag(R.string.commodity);
        Log.e("comid", id);
        int a = Integer.parseInt(id.replaceAll("[A-z]", "").trim());
        CheckBox cb = v.findViewById(R.id.cb);
        cb.setVisibility(View.VISIBLE);
        cb.setChecked(true);
        commo[a] = id.replaceAll("[0-9]", "");
    }

    private void updateComm(final String str) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.UPDATE_SELLER_COMMODITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lc.dismissDialog();
                        Intent intent = new Intent(addRemoveActivity.this, OnClickSellerCard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("SellerId", "Seller Id: " + seller_id);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(addRemoveActivity.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, addRemoveActivity.this.getClass().getName(), addRemoveActivity.this);


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", seller_id);
                params.put("str", str);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
