package com.example.absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.absensi.MyLibraryes.RequestURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String url = "http://10.17.0.4/fpro_v2/APIcoc/?Req=DAPOK";
    private EditText nip ;
    private Button submit ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onCheckImeiInDapokHandler();

        nip = findViewById(R.id.txt_nip);
        submit = findViewById(R.id.btn_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkIntentAbsenHandler("1701081017");
//                requestHandler();
            }
        });
    }

    private void requestHandler(){
        new RequestURL(getApplicationContext(), new RequestURL.MyRequest() {
            @Override
            public int getMethod() {
                return Request.Method.POST;
            }

            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public Map<String, String> param(Map<String, String> data) {
                data.put("NIP", nip.getText().toString());
                return data;
            }

            @Override
            public void response(Object response) {
                try {
                    JSONArray data = new JSONArray(response.toString());
                    if(!isNullJSONArray(data)){
                        JSONObject firstDataPegawai = data.getJSONObject(0);
                        String nip = firstDataPegawai.getString("NIP");
                        String imei = getMyImei();
                        if(checkMyImeiInDapok(firstDataPegawai)){
                            regitserImeiHandler(nip, imei);
                            linkIntentAbsenHandler(nip);
                        } else {
                            linkIntentAbsenHandler(nip);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void err(VolleyError error) {
                Log.d("Error : ", error.getMessage());
            }
        }).get();
    }

    private void regitserImeiHandler(final String nip, final String imei){
        final String urlUpdate = "http://10.17.0.4/fpro_v2/APIcoc/?Req=UPDATE";
        new RequestURL(getApplicationContext(), new RequestURL.MyRequest() {
            @Override
            public int getMethod() {
                return Request.Method.POST;
            }

            @Override
            public String getUrl() {
                return urlUpdate;
            }

            @Override
            public Map<String, String> param(Map<String, String> data) {
                data.put("IMEI", imei);
                data.put("NIP", nip);
                return data;
            }

            @Override
            public void response(Object response) {
                Log.d("data  ", response.toString());
            }

            @Override
            public void err(VolleyError error) {
                Log.d("Error Register Imei ", error.getMessage());
            }
        }).get();
    }

    private void onCheckImeiInDapokHandler(){
        final String myImei = getMyImei();
        final String urlFindImei = "http://10.17.0.4/fpro_v2/APIcoc/?Req=FINDIMEI";
        new RequestURL(getApplicationContext(), new RequestURL.MyRequest() {
            @Override
            public int getMethod() {
                return Request.Method.POST;
            }

            @Override
            public String getUrl() {
                return urlFindImei;
            }

            @Override
            public Map<String, String> param(Map<String, String> data) {
                data.put("IMEI", myImei);
                return data;
            }

            @Override
            public void response(Object response) {
                JSONArray data = null;
                try {
                    data = new JSONArray(response.toString());
                    Log.d("DATA MASUK", data.toString());
                    if(!isNullJSONArray(data)) {
                        JSONObject firstDataPegawai = data.getJSONObject(0);
                        String nip = firstDataPegawai.getString("NIP");
                        linkIntentAbsenHandler(nip);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void err(VolleyError error) {
                Toast.makeText(getApplicationContext(), "TIDAK SEDANG TERHUBUNG KE INTRANET", Toast.LENGTH_LONG).show();
            }
        }).get();
    }

    private void linkIntentAbsenHandler(String nip){
        Intent IN = new Intent(getApplicationContext(), AbsensiActivity.class);
        IN.putExtra(AbsensiActivity.VALUE_IMEI_PEGAWAI, nip );
        startActivity(IN);
        finish();
    }

    private boolean isNullJSONArray( JSONArray array ){
        return array.length() <= 0 ? true : false ;
    }

    private boolean checkMyImeiInDapok(JSONObject json) throws JSONException {
        return json.getString("IMEI").equalsIgnoreCase("null") ;
    }

    private String getMyImei(){
        int REQUEST_CODE = 101 ;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        }
        return telephonyManager.getDeviceId();
    }

    @Override
    protected void onRestart() {
        onCheckImeiInDapokHandler();
        super.onRestart();
    }
}
