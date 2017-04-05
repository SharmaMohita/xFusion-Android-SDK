package com.example.satku.xfusion_platformapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import  android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.teramatrix.xfusionlibrary.IoTSDK;
import com.teramatrix.xfusionlibrary.exception.InvalidRequestParametersException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements IoTSDK.IUserAuthorizationCallback {

    Button b1;
    private String TAG = MainActivity.class.getSimpleName();
    EditText txtTitle1, txtTitle2;
    EditText eName, ePassword;
    Button button;
    EditText ed1, ed2;
    private String text;
    TextView textTxt;
    private SPUtil sharedPreference;
    EditText username, password;
    private String is_added_to_metadata;

    ArrayList<HashMap<String, String>> countryList;
    ArrayList<HashMap<String, String>> organizationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // startActivity(new Intent(this, RegistrationForm.class));
        //  if(!new SPUtil(this).getString(SPUtil.PASSWORD).equals("")) {
        //   finish();
        //      startActivity(new Intent(this, MainActivity.class));
        //  }

        textTxt = (TextView) findViewById(R.id.txt_login);
        eName = (EditText) findViewById(R.id.editText_name);
        Typeface typeface3 = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.ttf");
        eName.setTypeface(typeface3);
        ePassword = (EditText) findViewById(R.id.editText_password);
        Typeface typeface4 = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.ttf");
        ePassword.setTypeface(typeface4);

        IoTSDK ioTSdk = IoTSDK.getInstance(getApplicationContext());
        ioTSdk.requestPermission(this);

        findViewById(R.id.txt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = ((EditText) findViewById(R.id.editText_name)).getText().toString();
                String password = ((EditText) findViewById(R.id.editText_password)).getText().toString();

                try {
                    IoTSDK.getInstance(LoginActivity.this).login(LoginActivity.this, username,
                            password, LoginActivity.this);

                } catch (InvalidRequestParametersException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    @Override
    public void onSuccess(String response_messg) throws JSONException {
      //  startActivity(new Intent(LoginActivity.this, RegistrationForm.class));

       // Intent intent = new Intent(LoginActivity.this, RegistrationForm.class);
        //startActivity(intent);

        SPUtil spUtil = new SPUtil(this);
        spUtil.setString(SPUtil.USERNAME, eName.getText().toString().trim());
        //spUtil.setString(SPUtil.PASSWORD, epassword.getText().toString().trim());
      ;
        countryList = new ArrayList<>();
        txtTitle1 = (EditText) findViewById(R.id.txt_edit);
        organizationList = new ArrayList<>();


             /* String str ="{{  \n" +
                "   \\\"description\":\"Successfull\",\n" +
                "   \"object\":[  \n" +
                "      {  \n" +
                "         \"device_id\":\"911407300688095_PLATFORM_SDK_preeti.burad@teramatrix.co2123\",\n" +
                "         \"port\":\"1883\",\n" +
                "         \"api_url\":\"http://i.teramatrix.in:7878/XFusionPlatform/iothub/publisher\",\n" +
                "         \"id\":3149,\n" +
                "         \"ip_address\":\"192.168.1.97\",\n" +
                "         \"is_added_to_metadata\":0,\n" +
                "         \"gateway_id\":3,\n" +
                "         \"connection_parameters\":null,\n" +
                "         \"status\":\"Registered Successfully\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"list\":null,\n" +
                "   \"valid\":true\n" +
                "}\n" +
                "\n}";*/
         try {
            Log.e(TAG, response_messg);
            JSONObject jsonObject = new JSONObject(response_messg);
             JSONArray jsonArray = jsonObject.getJSONArray("object");
             ArrayList<device> devices = new ArrayList<>();
             for (int i = 0; i < jsonArray.length(); i++) {
                 JSONObject jsonObject_local=  jsonArray.getJSONObject(i);
                 device d = new device();
                 d.setDevice_id(jsonObject_local.getString("device_id"));
                 d.setPort(jsonObject_local.getString("port"));
                 d.setApi_url(jsonObject_local.getString("api_url"));
                 d.setId(jsonObject_local.getString("id"));
                 d.setIp_address(jsonObject_local.getString("ip_address"));
                 d.setIs_added_to_metadata(jsonObject_local.getInt("is_added_to_metadata")+"");
                 d.setGateway_id(jsonObject_local.getString("gateway_id"));
                 d.setConnection_parameters(jsonObject_local.getString("connection_parameters"));
                 d.setStatus(jsonObject_local.getString("status"));
                 devices.add(d);
                 is_added_to_metadata=jsonObject_local.getInt("is_added_to_metadata")+"";
             }
         }    catch (JSONException e)
                {
                     e.printStackTrace();
                }

        if (is_added_to_metadata.equalsIgnoreCase("1"))
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        else if (is_added_to_metadata.equalsIgnoreCase("0"))
            startActivity(new Intent(LoginActivity.this, RegistrationForm.class));

        finish();

    }


    @Override
    public void onFailure(String s) {
        Log.e(TAG, "onFailure: ");
    }

}
