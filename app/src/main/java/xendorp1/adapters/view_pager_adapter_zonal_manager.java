package xendorp1.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import xendorp1.fragments.details_zonal_manager;
import xendorp1.fragments.executive_zonal_manager;
import xendorp1.fragments.ledger_zonal_manager;


/**
 * Created by GOTHAM on 02-11-2017.
 */

public class view_pager_adapter_zonal_manager extends FragmentPagerAdapter {
    private Bundle args;
    public ledger_zonal_manager fragment2;
    public view_pager_adapter_zonal_manager(FragmentManager fm, Bundle args) {
        super(fm);
        this.args=args;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                Fragment fragment= new details_zonal_manager();
                fragment.setArguments(args);
                return fragment;
            case 1:
                Fragment fragment1= new executive_zonal_manager();
                fragment1.setArguments(args);
                return fragment1;
            case 2:
                fragment2= new ledger_zonal_manager();
                fragment2.setArguments(args);
                return fragment2;

        }

        return null;
    }


    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Details";
            case 1:
                return "Executive";
            case 2:
                return "Ledger";
        }
        return  null;
    }
}
