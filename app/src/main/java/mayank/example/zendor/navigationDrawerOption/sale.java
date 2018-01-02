package mayank.example.zendor.navigationDrawerOption;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import mayank.example.zendor.onClickBuyer.buyerSale;
import mayank.example.zendor.onClickBuyer.saleAdapter;
import mayank.example.zendor.onClickSeller.OnClickSellerCard;
import mayank.example.zendor.onClickSeller.sellerDetails;
import mayank.example.zendor.onClickSeller.sellerLedger;
import mayank.example.zendor.onClickSeller.sellerPurchases;

import static mayank.example.zendor.MainActivity.showError;

public class sale extends AppCompatActivity {


    private ArrayList<buyerSale.saleClass> dispatchedList;
    private ArrayList<buyerSale.saleClass> deliveredlist;
    private ArrayList<buyerSale.saleClass> paymentReceivedList;
    private ViewPager viewPager;
    private TabLayout header;
    private ImageView back;
    LoadingClass lc ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        viewPager = findViewById(R.id.viewPager);
        header = findViewById(R.id.header);
        header.setupWithViewPager(viewPager);
        back = findViewById(R.id.back);
        lc = new LoadingClass(this);


        viewPager.setOffscreenPageLimit(3);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dispatchedList = new ArrayList<>();
        deliveredlist = new ArrayList<>();
        paymentReceivedList = new ArrayList<>();

        getSaleDetail();

        createpager();
    }

    private void createpager(){
        OnClickSellerCard.viewPagerAdapter adapter = new OnClickSellerCard.viewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new saleDispatched(dispatchedList),"Dispatched");
        adapter.addFrag(new saleDelivered(deliveredlist), "Delivered");
        adapter.addFrag(new salePaymentReceived(paymentReceivedList), "Payment Received");
        viewPager.setAdapter(adapter);
    }

    private void getSaleDetail(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ALL_SALES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("sale respo", response);
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
                                dispatchedList.add(new buyerSale.saleClass(bname, sid, commodity, weight, rate, flag, ts));
                                break;
                            case "de":
                                ts = sale.getString("ts2");
                                weight = sale.getString("w2");
                                deliveredlist.add(new buyerSale.saleClass(bname, sid, commodity, weight, rate, flag, ts));
                                break;
                            case "ps":
                                ts = sale.getString("ts1");
                                weight = sale.getString("w2");
                                paymentReceivedList.add(new buyerSale.saleClass(bname, sid, commodity, weight, rate, flag, ts));
                                break;
                        }
                    }
                } catch (JSONException e) {
                    lc.dismissDialog();
                    Toast.makeText(sale.this, "Some error occured", Toast.LENGTH_SHORT).show();
                    Log.e("sale erros", e+"");
                }

                createpager();
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(sale.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sale.this.getClass().getName(), sale.this);


                lc.dismissDialog();
            }
        });

        ApplicationQueue.getInstance(this).addToRequestQueue(stringRequest);
    }


}
