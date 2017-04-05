package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.satku.xfusion_platformapp.R;
import com.teramatrix.xfusionlibrary.IoTSDK;


/**
 * Created by satku on 3/27/2017.
 */

public class Tab1 extends ListFragment {
    private View rootView1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView1 == null) {
            rootView1 = inflater.inflate(R.layout.fragment_tab, container, false);
                String MenuItem_tab1[] = {
                                            "Phone Type",
                                            "IMEI Number",
                                            "Manufacturer Model",
                                            "Sim Serial Number",
                                            "SIM Country ISO",
                                            "SIM State",
                                            "Software Version",
                                            "SubscriberID",
                                            "Battery Status"
                };
                ListView listView = (ListView) rootView1.findViewById(R.id.list_view_tab1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        MenuItem_tab1);
                listView.setAdapter(adapter);

            }
        return rootView1;


        }

    }