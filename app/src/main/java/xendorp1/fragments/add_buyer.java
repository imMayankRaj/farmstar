package xendorp1.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayank.example.zendor.R;
import xendorp1.adapters.zone_card_spinner_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zone_card;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class add_buyer extends Fragment {
    private View rootview;
    private ImageView addnum;
    private ImageView profilepic;
    private LinearLayout linearLayout;
    private Button choose;
    private LinearLayout commodities;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EditText company;
    private Boolean photoChanged;
    private RelativeLayout cancel;
    private RelativeLayout submit;
    private RelativeLayout zonecont;
    private EditText name;
    private Spinner zone;
    private zone_card_spinner_adapter zone_card_spinner_adapter;
    private SharedPreferences sharedPreferences;
    private EditText address,primary_phone,capacity,gst;
    private String pos;
    public add_buyer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_add_buyer, container, false);
        zonecont=rootview.findViewById(R.id.zonecont);
        zone=rootview.findViewById(R.id.zone_values);
        sharedPreferences=getActivity().getSharedPreferences("details",MODE_PRIVATE);
        pos=sharedPreferences.getString("position","");
        if(pos.equals("0"))
        {
            getZones();
        }
        else
        {
            zonecont.setVisibility(View.GONE);
        }
        name=rootview.findViewById(R.id.name_value);
        addnum=rootview.findViewById(R.id.add_num);
        profilepic=rootview.findViewById(R.id.profilepic);
        toolbar=rootview.findViewById(R.id.toolbar1);
        progressBar=rootview.findViewById(R.id.progressbar);
        cancel=rootview.findViewById(R.id.cancel);
        submit=rootview.findViewById(R.id.submit);
        address=rootview.findViewById(R.id.address_value);
        choose=rootview.findViewById(R.id.commodities_value);
        company=rootview.findViewById(R.id.company_value);
        primary_phone=rootview.findViewById(R.id.numedit);
        commodities=rootview.findViewById(R.id.llayout11);
        capacity=rootview.findViewById(R.id.capacity_value);
        gst=rootview.findViewById(R.id.gst_value);
        toolbar.setTitle("Add a Buyer");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        linearLayout=rootview.findViewById(R.id.phno_values);
        addnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View view1=inflater.inflate(R.layout.phone_number_inputter1,null);
                linearLayout.addView(view1);
                ImageView removenum=view1.findViewById(R.id.remove);
                removenum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.removeView(view1);
                    }
                });
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commodities.removeAllViews();
                final AlertDialog dialog =new AlertDialog.Builder(getActivity())
                        .setView(R.layout.select_commodity)
                        .create();
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int k) {
                        LinearLayout llayout21=dialog.findViewById(R.id.llayout21);
                        for(int i=0;i<llayout21.getChildCount();i++)
                        {
                            CheckBox cb=(CheckBox)llayout21.getChildAt(i);
                            if(cb.isChecked())
                            {
                                final View view1=inflater.inflate(R.layout.commodity_add,null);
                                commodities.addView(view1);
                                TextView tv1=view1.findViewById(R.id.comname);
                                tv1.setText(cb.getText());
                                ImageView remove=view1.findViewById(R.id.remove);
                                remove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        commodities.removeView(view1);
                                    }
                                });
                            }
                        }
                    }
                });
                dialog.show();

            }
        });
        profilepic.setScaleType(ImageView.ScaleType.CENTER);
        profilepic.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.ic_add_a_photo_black_24dp));
        photoChanged=false;
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Permission not granted
                    //Asking user to grant permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Permission to read and write to storage");
                    builder.setMessage("This app needs permission to read and write images to storage");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                    136);
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else
                {
                    openImageIntent();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(company.getText().length()==0||name.getText().length()==0||primary_phone.getText().length()==0||address.getText().length()==0||capacity.getText().length()==0||commodities.getChildCount()==0||(pos.equals("0")&&zone.getSelectedItemPosition()==0))
                {
                    Toast.makeText(getActivity(), "fields cant be empty", Toast.LENGTH_SHORT).show();
                }
                else if (primary_phone.getText().length()!=10)
                {
                    Toast.makeText(getActivity(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final String nameval=name.getText().toString();
                    final String primarymob=primary_phone.getText().toString();
                    final String addressval=address.getText().toString();
                    final String capacityval=capacity.getText().toString();
                    final String gstval=gst.getText().toString();
                    final String companyval=company.getText().toString();
                    String other_nos="";
                    for(int i=0;i<linearLayout.getChildCount();i++)
                    {
                        View child_view=linearLayout.getChildAt(i);
                        EditText numedit=child_view.findViewById(R.id.numedit);
                        if(numedit.getText().length()==10)
                        {
                            other_nos=other_nos+numedit.getText().toString()+",";
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    final String finalOther_nos = other_nos;
                    progressBar.setVisibility(View.VISIBLE);
                    String commodities_val="";
                    for(int i=0;i<commodities.getChildCount();i++)
                    {
                        View child_view=commodities.getChildAt(i);
                        TextView textview=child_view.findViewById(R.id.comname);
                        if(textview.getText().length()!=0)
                        {
                            commodities_val=commodities_val+textview.getText().toString()+",";
                        }
                    }
                    final String finalCommodities_val = commodities_val;


                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_ADD_BUYER, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "Register Response: " + response.toString());
                            try {
                                JSONObject jobj=new JSONObject(response);
                                boolean error=jobj.getBoolean("error");
                                if(error)
                                {
                                    Toast.makeText(getActivity(), jobj.getString("error_message"), Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Successfully added buyer", Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
                                progressBar.setVisibility(View.GONE);
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Some network error occured. Please try again", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "" + error.getMessage());
                            Toast.makeText(getActivity(), "Some network error occured. Please try again", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }){

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting params to register url
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("name",nameval);
                            params.put("mob",primarymob);
                            params.put("address",addressval);
                            params.put("commodities",finalCommodities_val.substring(0,finalCommodities_val.length()-1));
                            params.put("capacity",capacityval);
                            if(gstval.length()!=0)
                            params.put("gstin",gstval);
                            params.put("company_name",companyval);
                            sharedPreferences=getActivity().getSharedPreferences("details",MODE_PRIVATE);
                            String id= sharedPreferences.getString("id","");
                            if(pos.equals("0"))
                            {
                                zone_card zone_card=(xendorp1.cards.zone_card)zone.getSelectedItem();
                                String zone_id=zone_card.getZone_id();
                                params.put("zid",zone_id);
                            }
                            else
                            {
                                String zid=sharedPreferences.getString("zid","");
                                params.put("zid",zid);
                            }
                            params.put("adder_id",id);
                            Log.d("other_nos",finalOther_nos);
                            if(finalOther_nos.length()!=0) {
                                String nos=finalOther_nos.substring(0,finalOther_nos.length()-1);
                                params.put("othermob", nos);
                            }
                            if(photoChanged) {
                                Bitmap drawable=((BitmapDrawable)profilepic.getDrawable()).getBitmap();
                                params.put("profilepic", getStringImage(drawable));
                            }
                            return params;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(strReq, "addBuyer");
                }
            }
        });
        return rootview;
    }
    private void openImageIntent() {
        Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(120,120)
                .getIntent(getContext());
        startActivityForResult(intent, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 5) {
                Log.d("here","here111");
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),resultUri);
                        File file = new File(resultUri.getPath());
                        profilepic.setScaleType(ImageView.ScaleType.FIT_XY);
                        profilepic.setImageBitmap(bitmap);
                        photoChanged=true;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
            }

        }

    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //For requesting permission
        switch (requestCode) {
            case 136: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    openImageIntent();
                } else {
                    // Permission Denied
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void getZones()
    {
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_ZONES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    List<zone_card> zonelist=new ArrayList<>();
                    zonelist.add(new zone_card());
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        zone_card zone_card = new zone_card();
                        zone_card.setZone_name(jsonObject.getString("zname"));
                        Log.d("zone_name",jsonObject.getString("zname"));
                        zone_card.setZone_id(jsonObject.getString("zid"));
                        zonelist.add(zone_card);
                    }
                    zone_card_spinner_adapter=new zone_card_spinner_adapter(getActivity(),R.layout.zone_card,zonelist,getLayoutInflater());
                    zone.setAdapter(zone_card_spinner_adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "getzones");
    }

}
