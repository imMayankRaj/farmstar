package mayank.example.zendor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import mayank.example.zendor.R;

public class commoditiesActivity extends AppCompatActivity {

    private ImageView v1,v2,v3,v4,f1,f2,f3,f4,p1,p2,p3,p4;
    private CheckBox vc1,vc2,vc3,vc4,fc1,fc2,fc3,fc4,pc1,pc2,pc3,pc4;
    private TextView submit;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodities);

        final Bundle bundle = getIntent().getBundleExtra("sellerDetail");
        v1 = findViewById(R.id.v1);
        v2 = findViewById(R.id.v2);
        v3 = findViewById(R.id.v3);
        v4 = findViewById(R.id.v4);
        f1 = findViewById(R.id.f1);
        f2 = findViewById(R.id.f2);
        f3 = findViewById(R.id.f3);
        f4 = findViewById(R.id.f4);
        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        p3 = findViewById(R.id.p3);
        p4 = findViewById(R.id.p4);
        vc1 = findViewById(R.id.vc1);
        vc2 = findViewById(R.id.vc2);
        vc3 = findViewById(R.id.vc3);
        vc4 = findViewById(R.id.vc4);
        fc1 = findViewById(R.id.fc1);
        fc2 = findViewById(R.id.fc2);
        fc3 = findViewById(R.id.fc3);
        fc4 = findViewById(R.id.fc4);
        pc1 = findViewById(R.id.pc1);
        pc2 = findViewById(R.id.pc2);
        pc3 = findViewById(R.id.pc3);
        pc4 = findViewById(R.id.pc4);
        submit = findViewById(R.id.submit);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(commoditiesActivity.this, LoginActivity.class));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(commoditiesActivity.this, sellerExtraData.class);
                intent.putExtra("sellerDetail", bundle);
                startActivity(intent);
            }
        });

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vc1.isChecked()) {
                    vc1.setVisibility(View.VISIBLE);
                    vc1.setChecked(true);
                }else {
                    vc1.setVisibility(View.INVISIBLE);
                    vc1.setChecked(false);
                }
            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vc2.isChecked()) {
                    vc2.setVisibility(View.VISIBLE);
                    vc2.setChecked(true);
                }else {
                    vc2.setVisibility(View.INVISIBLE);
                    vc2.setChecked(false);
                }
            }
        });

        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vc3.isChecked()) {
                    vc3.setVisibility(View.VISIBLE);
                    vc3.setChecked(true);
                }else {
                    vc3.setVisibility(View.INVISIBLE);
                    vc3.setChecked(false);
                }
            }
        });

        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vc4.isChecked()) {
                    vc4.setVisibility(View.VISIBLE);
                    vc4.setChecked(true);
                }else {
                    vc4.setVisibility(View.INVISIBLE);
                    vc4.setChecked(false);
                }
            }
        });

        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fc1.isChecked()) {
                    fc1.setVisibility(View.VISIBLE);
                    fc1.setChecked(true);
                }else {
                    fc1.setVisibility(View.INVISIBLE);
                    fc1.setChecked(false);
                }
            }
        });

        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fc2.isChecked()) {
                    fc2.setVisibility(View.VISIBLE);
                    fc2.setChecked(true);
                }else {
                    fc2.setVisibility(View.INVISIBLE);
                    fc2.setChecked(false);
                }
            }
        });
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fc3.isChecked()) {
                    fc3.setVisibility(View.VISIBLE);
                    fc3.setChecked(true);
                }else {
                    fc3.setVisibility(View.INVISIBLE);
                    fc3.setChecked(false);
                }
            }
        });
        f4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fc4.isChecked()) {
                    fc4.setVisibility(View.VISIBLE);
                    fc4.setChecked(true);
                }else {
                    fc4.setVisibility(View.INVISIBLE);
                    fc4.setChecked(false);
                }
            }
        });

        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pc1.isChecked()) {
                    pc1.setVisibility(View.VISIBLE);
                    pc1.setChecked(true);
                }else {
                    pc1.setVisibility(View.INVISIBLE);
                    pc1.setChecked(false);
                }
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pc2.isChecked()) {
                    pc2.setVisibility(View.VISIBLE);
                    pc2.setChecked(true);
                }else {
                    pc2.setVisibility(View.INVISIBLE);
                    pc2.setChecked(false);
                }
            }
        });

        p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pc3.isChecked()) {
                    pc3.setVisibility(View.VISIBLE);
                    pc3.setChecked(true);
                }else {
                    pc3.setVisibility(View.INVISIBLE);
                    pc3.setChecked(false);
                }
            }
        });

        p4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pc4.isChecked()) {
                    pc4.setVisibility(View.VISIBLE);
                    pc4.setChecked(true);
                }else {
                    pc4.setVisibility(View.INVISIBLE);
                    pc4.setChecked(false);
                }
            }
        });






    }
}
