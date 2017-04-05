package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.satku.xfusion_platformapp.R;

/**
 * Created by satku on 3/27/2017.
 */

public class Tab3 extends ListFragment  {
    private View rootView3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView3 == null) {
            rootView3 = inflater.inflate(R.layout.fragment_tab3, container, false);
            String MenuItems_tab3[] = {
                    "Data Activity",
                    "Data State",
                    "Mobile Rx bytes",
                    "Mobile Tx bytes",
                    "Total Rx bytes",
                    "Total Tx byte",
                    "Mobile Rx packets",
                    "Mobile Tx packets",
                    "Total Rx packets",
                    "Total Tx packets"

            };
            ListView listView3 = (ListView) rootView3.findViewById(R.id.list_view_tab3);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    MenuItems_tab3);
            listView3.setAdapter(adapter);

            }
        return rootView3;
        }
    }


