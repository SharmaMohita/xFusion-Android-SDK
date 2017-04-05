package fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.satku.xfusion_platformapp.R;
import com.example.satku.xfusion_platformapp.pager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohita on 2/13/2017.
 */

public class DashboardFragment extends Fragment {
    private View rootView;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    //This is our viewPager
    private ViewPager viewPager;
    private ActionBar actionBar;
    private int[] tabIcons = {
            R.drawable.ic_phone_white_24dp,
            R.drawable.ic_network_check_white_24dp,
            R.drawable.ic_traffic_white_24dp
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);


            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            /**
             * This statement will make toolbar to  work as action bar.
             */
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

//            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            /**
             * This statement will make reference of viewPager whose id is viewpager.
             */
            viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            /**
             * This statement will make reference of TabLayout whose id is tabs.
             */
            tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

            /**
             * This statement will link the given ViewPager and this TabLayout together.
             */
            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();


        }
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {

        ArrayList<String> pData = new ArrayList<>();
        pData.add("Phone Type");
        pData.add("IMEI Number");
        pData.add("Manufacturer Model");
        pData.add("Sim Serial Number");
        pData.add("SIM Country ISO");
        pData.add("SIM State");
        pData.add("Software Version");
        pData.add("SubscriberID");
        pData.add("Battery Status");
        Bundle pBundle = new Bundle();
        pBundle.putStringArrayList("data", pData);
        Fragment pFragment = new FragmentList();
        pFragment.setArguments(pBundle);

        ArrayList<String> nData = new ArrayList<>();
        nData.add("Network Operator");
        nData.add("Network State");
        nData.add("Technology");
        nData.add("Phone Network Type");
        nData.add("In Roaming");
        nData.add("Call State");
        nData.add("Signal Strength(dbm)");
        nData.add("Signal Level");
        nData.add("CID");
        nData.add("CI");
        nData.add("TAC");
        nData.add("LAC");
        nData.add("MCC");
        nData.add("MNC");
        nData.add("PSC");
        Bundle nBundle = new Bundle();
        nBundle.putStringArrayList("data", nData);
        Fragment nFragment = new FragmentList();
        nFragment.setArguments(nBundle);

        ArrayList<String> tData = new ArrayList<>();
        tData.add("Data Activity");
        tData.add("Data State");
        tData.add("Mobile Rx bytes");
        tData.add("Mobile Tx bytes");
        tData.add("Total Rx bytes");
        tData.add("Total Tx byte");
        tData.add("Mobile Rx packets");
        tData.add("Mobile Rx bytes");
        tData.add("Mobile Tx packets");
        tData.add("Total Rx packets");
        tData.add("Total Tx packets");
        Bundle tBundle = new Bundle();
        tBundle.putStringArrayList("data", tData);
        Fragment tFragment = new FragmentList();
        tFragment.setArguments(tBundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(pFragment, "Phone");
        adapter.addFragment(nFragment, "Network");
        adapter.addFragment(tFragment, "Traffic");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * This is the constructor of ViewPagerAdapter class.
         */
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /*
         * This method is used to add the fragment in the fragment list with their title.
         * @param fragment This parameter is used for the fragment that will be add in fragment list.
         * @param title    This parameter is used for the title of the fragment.
         */
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}