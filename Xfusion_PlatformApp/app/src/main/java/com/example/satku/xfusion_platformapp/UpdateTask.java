package com.example.satku.xfusion_platformapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by satku on 2/10/2017.
 */

public class UpdateTask extends AsyncTask<Void, Void, Void> {
    private Context mCon;

    public UpdateTask(Context con)
    {
        mCon = con;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // Set a time to simulate a long update process.
            Thread.sleep(4000);

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    protected void onPostExecute(Void nope) {
        // Give some feedback on the UI.
        Toast.makeText(mCon, "refresh",
                Toast.LENGTH_LONG).show();

        // Change the menu back
      //  ((MainActivity) mCon).resetUpdating();
    }
}


