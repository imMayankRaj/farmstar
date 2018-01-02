package mayank.example.zendor.onClickExecutive;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;

import static android.content.ContentValues.TAG;
import static mayank.example.zendor.MainActivity.showError;

public class executive_details extends Fragment {

    public static String EXECU_ID = "exec_id";
    public static String ENAME = "ename";
    private String execu_id;
    private TextView name_val, address_val, zone_val;
    private ImageView profilepic;
    private String zname;
    private String nums[];
    private Intent intent;
    private Button call;
    private LoadingClass lc;

    public executive_details() {
        // Required empty public constructor
    }


    public static executive_details newInstance(String param1, String ename) {
        executive_details fragment = new executive_details();
        Bundle args = new Bundle();
        args.putString(EXECU_ID, param1);
        args.putString(ENAME, ename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            execu_id = getArguments().getString(EXECU_ID);
            zname = getArguments().getString(ENAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_executive_details, container, false);

        name_val = rootview.findViewById(R.id.name_value);
        address_val = rootview.findViewById(R.id.address_value);
        zone_val = rootview.findViewById(R.id.zone_value);
        profilepic = rootview.findViewById(R.id.profilepic);
        call = rootview.findViewById(R.id.call);

        lc = new LoadingClass(getActivity());
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(nums);
            }
        });

        zone_val.setText(": " + zname);

        getData();

        return rootview;
    }

    private void getData() {
        lc.showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_ZONAL_MANAGER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    name_val.setText(jsonObject.getString("name"));
                    address_val.setText(jsonObject.getString("address"));
                    String prof = jsonObject.getString("profilepic");

                    String num = jsonObject.getString("mob") + "," + jsonObject.getString("othermob");
                    nums = num.split(",");

                    if (!(prof == null || prof.equals("null")))
                        Glide.with(getActivity()).load(URLclass.COMMODITY_PIC_PATH + prof).into(profilepic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "" + error.getMessage());
                lc.dismissDialog();
                Toast.makeText(getActivity(), "Some error occured. Please try again", Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, executive_details.class.getName(), getActivity());



            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", execu_id);
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
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91" + number));
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                                    2);
                            return;
                        } else
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
