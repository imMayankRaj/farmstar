package xendorp1.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.sellerExtraData;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;

/**
 * A simple {@link Fragment} subclass.
 */
public class add_executive extends Fragment {
    private View rootview;
    private ImageView addnum;
    private ImageView profilepic;
    private LinearLayout linearLayout;
    private Toolbar toolbar;
    private Boolean photoChanged;
    private RelativeLayout cancel;
    private RelativeLayout submit;
    private EditText name;
    private EditText address;
    private EditText username;
    private TextInputEditText password;
    private TextInputLayout passcont;
    private EditText primary_ph_no;
    private String zid;
    private String id;
    private xendorp1.adapters.zone_card_spinner_adapter zone_card_spinner_adapter;
    private String imgPath;
    private LoadingClass lc;

    public add_executive() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        id = null;
        if (bundle != null) {
            id = bundle.getString("id");
            zid = bundle.getString("zid");
        }
        Log.d("id1", id);
        rootview = inflater.inflate(R.layout.fragment_add_executive, container, false);
        photoChanged = false;
        addnum = rootview.findViewById(R.id.add_num);
        toolbar = rootview.findViewById(R.id.toolbar1);
        cancel = rootview.findViewById(R.id.cancel);
        submit = rootview.findViewById(R.id.submit);
        passcont = rootview.findViewById(R.id.passcont);
        primary_ph_no = rootview.findViewById(R.id.numedit);
        name = rootview.findViewById(R.id.name_value);
        username = rootview.findViewById(R.id.username_value);
        address = rootview.findViewById(R.id.address_value);
        password = rootview.findViewById(R.id.password_value);
        lc = new LoadingClass(getActivity());

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 4)
                    passcont.setError("Enter atleast 4 characters");
                else
                    passcont.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        primary_ph_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

        linearLayout = rootview.findViewById(R.id.phno_values);
        profilepic = rootview.findViewById(R.id.profilepic);
        profilepic.setScaleType(ImageView.ScaleType.CENTER);
        profilepic.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_add_a_photo_black_24dp));
        addnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View view1 = inflater.inflate(R.layout.phone_number_inputter1, null);
                linearLayout.addView(view1);
                ImageView removenum = view1.findViewById(R.id.remove);
                removenum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.removeView(view1);
                    }
                });
            }
        });
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
                                    135);
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
                } else {
                    openImageIntent();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0 || primary_ph_no.getText().length() == 0 || address.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Required fields are empty", Toast.LENGTH_LONG).show();
                } else if (primary_ph_no.getText().length() != 10) {
                    Toast.makeText(getActivity(), "Please enter a valid phone number", Toast.LENGTH_LONG).show();
                } else {
                    lc.showDialog();


                    if (photoChanged) {

                        long time = System.currentTimeMillis();
                        final String path = "_" + time + imgPath.substring(imgPath.lastIndexOf("."));

                        try {

                            new MultipartUploadRequest(getActivity(), URLclass.UPLOAD_IMAGES)
                                    .addFileToUpload(imgPath, "image")
                                    .addParameter("name", path)
                                    .setNotificationConfig(new UploadNotificationConfig())
                                    .setMaxRetries(10)
                                    .setDelegate(new UploadStatusDelegate() {
                                        @Override
                                        public void onProgress(Context context, UploadInfo uploadInfo) {

                                        }

                                        @Override
                                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                            Toast.makeText(getActivity(), "Error Occured.Please Retry.", Toast.LENGTH_SHORT).show();


                                        }

                                        @Override
                                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                                            final String name_val = name.getText().toString();
                                            final String phone_num = username.getText().toString();
                                            final String address_val = address.getText().toString();
                                            final String password_val = password.getText().toString();
                                            String other_nos = "";
                                            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                                                View child_view = linearLayout.getChildAt(i);
                                                EditText numedit = child_view.findViewById(R.id.numedit);
                                                if (numedit.getText().length() == 10) {
                                                    other_nos = other_nos + numedit.getText().toString() + ",";
                                                } else {
                                                    Toast.makeText(getActivity(), "Please enter a valid phone number", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                            }
                                            final String finalOther_nos = other_nos;
                                            StringRequest strReq = new StringRequest(Request.Method.POST,
                                                    AppConfig.URL_ADD_PERSON, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d(TAG, "Register Response: " + response.toString());
                                                    try {
                                                        addInCb(getActivity());
                                                        JSONObject jobj = new JSONObject(response);
                                                        boolean error = jobj.getBoolean("error");
                                                        if (error) {
                                                            Toast.makeText(getActivity(), jobj.getString("error_message"), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getActivity(), "Successfully added executive.Please refresh" , Toast.LENGTH_LONG).show();
                                                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                                                        }
                                                    } catch (Exception e) {
                                                        Toast.makeText(getActivity(), "Some network error occured. Please try again", Toast.LENGTH_SHORT).show();
                                                        e.printStackTrace();
                                                    }

                                                    lc.dismissDialog();
                                                 /*   executives.click1.performClick();
                                                    getActivity().getFragmentManager().popBackStack();*/

                                                }
                                            }, new Response.ErrorListener() {

                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e(TAG, "" + error.getMessage());
                                                    Toast.makeText(getActivity(), "Some network error occured. Please try again", Toast.LENGTH_SHORT).show();
                                                    lc.dismissDialog();


                                                    if (error instanceof TimeoutError) {
                                                        Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                                                    } else
                                                        showError(error, add_executive.class.getName(), getActivity());


                                                }
                                            }) {

                                                @Override
                                                protected Map<String, String> getParams() {
                                                    // Posting params to register url
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("name", name_val);
                                                    params.put("mob", phone_num);
                                                    params.put("address", address_val);
                                                    params.put("zid", zid);
                                                    params.put("pwd", password_val);
                                                    params.put("flag", "2");
                                                    params.put("adder_id", id);
                                                    Log.d("other_nos", finalOther_nos);
                                                    if (finalOther_nos.length() != 0) {
                                                        String nos = finalOther_nos.substring(0, finalOther_nos.length() - 1);
                                                        params.put("othermob", nos);
                                                    }
                                                    params.put("path", path);

                                                    return params;
                                                }
                                            };
                                            AppController.getInstance().addToRequestQueue(strReq, "addZonalManager");


                                        }

                                        @Override
                                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                                        }
                                    })
                                    .startUpload();

                        } catch (Exception exc) {
                            Toast.makeText(getActivity(), "Error Occured.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        final String name_val = name.getText().toString();
                        final String phone_num = username.getText().toString();
                        final String address_val = address.getText().toString();
                        final String password_val = password.getText().toString();
                        String other_nos = "";
                        for (int i = 0; i < linearLayout.getChildCount(); i++) {
                            View child_view = linearLayout.getChildAt(i);
                            EditText numedit = child_view.findViewById(R.id.numedit);
                            if (numedit.getText().length() == 10) {
                                other_nos = other_nos + numedit.getText().toString() + ",";
                            } else {
                                Toast.makeText(getActivity(), "Please enter a valid phone number", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        final String finalOther_nos = other_nos;
                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                AppConfig.URL_ADD_PERSON, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Register Response: " + response.toString());
                                try {
                                    addInCb(getActivity());
                                    JSONObject jobj = new JSONObject(response);
                                    boolean error = jobj.getBoolean("error");
                                    if (error) {
                                        Toast.makeText(getActivity(), jobj.getString("error_message"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Successfully added executive. Please refresh.", Toast.LENGTH_LONG).show();
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "Some network error occured. Please try again", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                                lc.dismissDialog();
                              /*  executives.click1.performClick();
                                getActivity().getFragmentManager().popBackStack();*/

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "" + error.getMessage());
                                Toast.makeText(getActivity(), "Some network error occured. Please try again", Toast.LENGTH_SHORT).show();

                                lc.dismissDialog();
                                if (error instanceof TimeoutError) {
                                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                                } else
                                    showError(error, add_executive.class.getName(), getActivity());


                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                // Posting params to register url
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("name", name_val);
                                params.put("mob", phone_num);
                                params.put("address", address_val);
                                params.put("zid", zid);
                                params.put("pwd", password_val);
                                params.put("flag", "2");
                                params.put("adder_id", id);
                                Log.d("other_nos", finalOther_nos);
                                if (finalOther_nos.length() != 0) {
                                    String nos = finalOther_nos.substring(0, finalOther_nos.length() - 1);
                                    params.put("othermob", nos);
                                }

                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(strReq, "addZonalManager");


                    }
                }
            }
        });
        return rootview;
    }

    private void openImageIntent() {
        Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(120, 120)
                .getIntent(getContext());
        startActivityForResult(intent, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 5) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        String filename = System.currentTimeMillis()+".jpg";
                        File file = new File(getActivity().getFilesDir(), filename);

                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, out);

                        imgPath = file.getPath();

                        profilepic.setScaleType(ImageView.ScaleType.FIT_XY);
                        profilepic.setImageBitmap(bitmap);
                        photoChanged = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
            }

        }

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //For requesting permission
        switch (requestCode) {
            case 135: {
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

    public static void addInCb(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLclass.ADD_IN_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
