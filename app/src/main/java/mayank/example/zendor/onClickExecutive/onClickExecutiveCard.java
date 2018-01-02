package mayank.example.zendor.onClickExecutive;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import mayank.example.zendor.R;
import mayank.example.zendor.onClickSeller.OnClickSellerCard;
import mayank.example.zendor.onClickSeller.sellerDetails;
import mayank.example.zendor.onClickSeller.sellerLedger;
import mayank.example.zendor.onClickSeller.sellerPurchases;

public class onClickExecutiveCard extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout header;
    private String eid;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_executive_card);

        viewPager = findViewById(R.id.viewPager);
        header = findViewById(R.id.header);
        header.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);

        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            eid = bundle.getString("exec_id");
            name = bundle.getString("name");
        }


        createpager();

    }

    private void createpager(){
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(executive_details.newInstance(eid, name), "Details");
        adapter.addFrag(executive_allpurchases.newInstance(eid), "Purchases");
        adapter.addFrag(executiveLedger.newInstance(eid), "Ledger");
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
