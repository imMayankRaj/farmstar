package mayank.example.zendor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    private apiConnect connect;
    private RequestQueue requestQueue;
    private View view;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        view = findViewById(R.id.loginPage);
        progressBar = findViewById(R.id.progress);

        sharedPreferences  =getSharedPreferences("details", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        requestQueue = Volley.newRequestQueue(LoginActivity.this);

        Boolean status = sharedPreferences.getBoolean("loginStatus", false);
        if(status){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String pass = password.getText().toString();
                if(uname.length()==0 || pass.length()==0){
                    Snackbar.make(view,"Some fields are left empty", BaseTransientBottomBar.LENGTH_LONG).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    getLoginStatus(uname, pass);
                }
            }
        });
    }


    private void getLoginStatus(final String uname, final String pass){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("response", response);
                try {
                    JSONObject res = new JSONObject(response);
                    String stat = res.getString("status");
                    if(stat.equals("invalid"))
                        Snackbar.make(view,"Incorrect Username or password",BaseTransientBottomBar.LENGTH_LONG).show();
                    else {

                        String name = res.getString("name");
                        String position = res.getString("flag");
                        String path = res.getString("path");
                        String zone = res.getString("zone");
                        String id = res.getString("id");
                        String zid = res.getString("zid");
                        editor.putString("name",name);
                        editor.putString("position",position);
                        editor.putString("path",path);
                        editor.putString("zone",zone);
                        editor.putBoolean("loginStatus",true);
                        editor.putString("zid", zid);
                        editor.putString("id", id);
                        editor.apply();
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error+"");
                Snackbar.make(view,"Network Error",BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("uname", uname);
                map.put("pass", pass);
                return map;
            }
        };
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

}
