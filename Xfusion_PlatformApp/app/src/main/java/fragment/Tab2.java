package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.satku.xfusion_platformapp.R;

/**
 * Created by satku on 3/27/2017.
 */

public class Tab2 extends Fragment {
    private View rootView2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rootView2==null)
        {
            rootView2= inflater.inflate(R.layout.fragment_tab2, container, false);
            String MenuItems_tab2 [] = {
                    "Network Operator",
                    "Network State",
                    "Technology",
                    "Network Country ISO",
                    "Phone Network Type",
                    "In Roaming",
                    "Call State",
                    "Signal Strength(dbm)",
                    "Signal Level",
                    "CID",
                    "CI",
                    "TAC",
                    "LAC",
                    "MCC",
                    "MNC",
                    "PSC"};

            ListView listView2 = (ListView) rootView2.findViewById(R.id.list_view_tab2);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    MenuItems_tab2);
            listView2.setAdapter(adapter);

            }
        return rootView2;
         }
    }