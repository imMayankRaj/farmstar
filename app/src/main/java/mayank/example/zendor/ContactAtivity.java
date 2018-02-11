package mayank.example.zendor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.onClickBooked.onClickBookedCard;
import xendorp1.application_classes.AppController;

public class ContactAtivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private String[] number;
    private LoadingClass lc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_ativity);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lc = new LoadingClass(this);

        sharedPreferences  =getSharedPreferences("details", MODE_PRIVATE);

        final String pos = sharedPreferences.getString("position", "");

        if (ActivityCompat.checkSelfPermission(ContactAtivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContactAtivity.this, new String[]{Manifest.permission.CALL_PHONE},
                    2);

        }

        TextView c_zonal = findViewById(R.id.contactZonal);
        TextView c_zendor = findViewById(R.id.contactZendor);
        TextView mail = findViewById(R.id.mail);

        if (pos.equals("2")) {
            c_zonal.setVisibility(View.VISIBLE);
            getNumber();
        }

       c_zonal.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
               callDialog(number);
            }
        });

        c_zendor.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+918874444111"));
                    startActivity(intent);
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "abhikuiitk@gmail.com", null));
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));

            }
        });

    }

    private void getNumber(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_ZM_NUMBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();
                number = response.split(",");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    Intent intent;
    private void callDialog(final String a[]) {

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(a));
        numberList.removeAll(Collections.singleton("null"));

        final String[] b = numberList.toArray(new String[numberList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Call :")
                .setItems(b, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String number = b[which];
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91"+number));
                        if (ActivityCompat.checkSelfPermission(ContactAtivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ContactAtivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                    2);
                            return;
                        }else
                            startActivity(intent);
                        dialog.dismiss();
                    }
                });

        builder.create();
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
        }
    }

}
