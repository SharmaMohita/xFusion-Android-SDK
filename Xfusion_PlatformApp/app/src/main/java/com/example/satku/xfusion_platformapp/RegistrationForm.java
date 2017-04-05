package com.example.satku.xfusion_platformapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.teramatrix.xfusionlibrary.IoTSDK;
import com.teramatrix.xfusionlibrary.receivers.LocationUpdateReceiver;
import com.teramatrix.xfusionlibrary.receivers.ServiceDataUpdateReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mohita on 3/31/2017.
 */

public class RegistrationForm extends AppCompatActivity {
    LocationUpdateReceiver locationUpdateReceiver;
    ServiceDataUpdateReceiver serviceDataUpdateReceiver;
    // Spinner spinner = (Spinner) findViewById(R.id.spinner);

    private class Country {
        public String name;
        public String alias;
        public String id;

        public Country(String id, String alias, String name) {
            this.name = name;
            this.alias = alias;
            this.id = id;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //  Intent intent = new Intent(RegistrationForm.this, MainActivity.class);
        // startActivity(intent);


        IoTSDK.getInstance(this).getCountryList(this, new IoTSDK.ICountryListCallback() {

            @Override
            public void onSuccess(String response_messg) {
                Log.i("CountryList", "onSuccess " + response_messg);
                try {
                    JSONObject jsonObject = new JSONObject(response_messg);
                    JSONArray jsonArray = jsonObject.getJSONArray("object");

                    ArrayList<String> countryList = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Country country = new Country(jsonObject1.getString("name"), jsonObject1.getString("alias"), jsonObject1.getString("id"));
                        countryList.add(country.alias);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String response_messg) {
               // Log.e(TAG, "onFailure: ");
            }

        });

        IoTSDK.getInstance(this).getOrganizationList(this, new IoTSDK.IOrganizationListCallback() {
            @Override
            public void onSuccess(String response_messg) {
                Log.i("OrganizationList", "onSuccess " + response_messg);


            }

            @Override
            public void onFailure(String response_messg) {

            }
        });

       }
    }



