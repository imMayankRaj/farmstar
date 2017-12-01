package mayank.example.zendor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static mayank.example.zendor.sellerDetailActivity.sendOTP;

public class sellerOtpVerify extends AppCompatActivity {


    private EditText e1;
    private TextView choose;
    private String getOtp;
    private Toolbar toolbar;
    private ImageView checked;
    private TextView resend;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_otp_verify);

        e1 = findViewById(R.id.e1);
        choose = findViewById(R.id.choose);
        toolbar = findViewById(R.id.toolbar);
        checked = findViewById(R.id.checked);
        resend = findViewById(R.id.resendOtp);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle bundle;
        bundle = getIntent().getExtras();
        getOtp = intent.getStringExtra("otp");
        number = intent.getStringExtra("phone");

        e1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                check();
            }
        });

        final Bundle finalBundle = bundle;
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(e1.length()!=0 ){
                    String verify = e1.getText().toString();
                    Log.e(verify, getOtp);
                    if(verify.equals(getOtp)) {
                        Intent intent = new Intent(sellerOtpVerify.this, commoditiesActivity.class);
                        intent.putExtra("sellerDetail", finalBundle);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(sellerOtpVerify.this, "Wrong otp entered", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(sellerOtpVerify.this, "Otp has been resend.", Toast.LENGTH_SHORT).show();
                sendOTP(number, getOtp);
            }
        });

    }
    private void check()
    {
        String verify = e1.getText().toString();
        if(verify.equals(getOtp))
            checked.setVisibility(View.VISIBLE);
        else
            checked.setVisibility(View.GONE);
    }
}
