package mayank.example.zendor.onClickSeller;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import mayank.example.zendor.R;

public class OnClickSellerCard extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout header;
    private String sellerId;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_seller_card);

        sellerId = getIntent().getStringExtra("SellerId");

        viewPager = findViewById(R.id.viewPager);
        header = findViewById(R.id.header);
        header.setupWithViewPager(viewPager);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager.setOffscreenPageLimit(3);

        createpager();
    }

    private void createpager(){
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(sellerDetails.newInstance(sellerId), "Details");
        adapter.addFrag(sellerPurchases.newInstance(sellerId), "Purchases");
        adapter.addFrag(sellerLedger.newInstance(sellerId), "Ledger");
        viewPager.setAdapter(adapter);
    }

    public static class viewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        public viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        public void addFrag(Fragment fragment, String title){
            fragmentArrayList.add(fragment);
            titleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}
