package xendorp1.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;

import static android.content.ContentValues.TAG;
import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class details_zonal_manager extends Fragment {
    private View rootview;
    private String id;
    private String zname;
    private TextView name_val,address_val,zone_val;
    private ImageView profilepic;
    private Intent intent;
    private String num[];
    private Button call;
    private ProgressBar pbar;

    public details_zonal_manager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview=inflater.inflate(R.layout.fragment_details, container, false);
        Bundle bundle=this.getArguments();
        id=null;
        zname=null;
        if(bundle!=null)
        {
            id=bundle.getString("id");
            zname=bundle.getString("zname");
        }
        name_val=rootview.findViewById(R.id.name_value);
        address_val=rootview.findViewById(R.id.address_value);
        zone_val=rootview.findViewById(R.id.zone_value);
        call = rootview.findViewById(R.id.call);
        zone_val.setText(zname);
        profilepic=rootview.findViewById(R.id.profilepic);
        pbar = rootview.findViewById(R.id.pbar);

        getData(id);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(num);
            }
        });

        return rootview;
    }

    void getData(final String id) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_ZONAL_MANAGER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    name_val.setText(jsonObject.getString("name"));
                    address_val.setText(jsonObject.getString("address"));
                    String prof=jsonObject.getString("profilepic");
                    String mob=jsonObject.getString("mob");
                    String othermob = jsonObject.getString("othermob");

                    mob = mob+","+othermob;
                    num = mob.split(",");

                    if(!(prof==null||prof=="null"))
                        Glide.with(getActivity()).load(URLclass.COMMODITY_PIC_PATH+prof)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        pbar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "Error Loading Picture.", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        pbar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(profilepic);
                } catch (Exception e) {
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
                    showError(error, details_zonal_manager.class.getName(), getActivity());


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "getzonalmanager");
    }

    private void callDialog(final String a[]) {

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(a));
        numberList.removeAll(Collections.singleton("null"));

        final String[] b = numberList.toArray(new String[numberList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Call :")
                .setItems(b, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String number = b[which];
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91"+number));
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
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
        if(requestCode == 2){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(intent);
            }
        }
    }

}
