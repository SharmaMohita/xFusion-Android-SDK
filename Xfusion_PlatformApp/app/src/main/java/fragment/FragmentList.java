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

import java.util.ArrayList;

/**
 * Created by satku on 3/27/2017.
 */

public class FragmentList extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_list, container, false);
            ListView listView3 = (ListView) rootView.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    getArguments().getStringArrayList("data"));
            listView3.setAdapter(adapter);

        }
        return rootView;
    }
}


