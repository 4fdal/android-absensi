package com.example.absensi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class AbsensiActivity extends AppCompatActivity {

    public static String VALUE_IMEI_PEGAWAI = "VALUE_IMEI_PEGAWAI" ;
    private IntentIntegrator qrScan ;
    private Button btn_absen ;
    private String NIP ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);
        NIP = getIntent().getSerializableExtra(VALUE_IMEI_PEGAWAI).toString();

        btn_absen = findViewById(R.id.btn_absen);
        qrScan = new IntentIntegrator(this);
        btn_absen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("data QR CODE", data+" "+requestCode+" "+requestCode);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                try {
                    JSONObject object = new JSONObject(result.getContents());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                btn_absen.setBackgroundResource(R.drawable.btn_absen);
                btn_absen.setText("TEALAH ABSEN");
                btn_absen.setEnabled(false);
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "KODE QR TIDAK TERBACA SILAHKAN COBA LAGI", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
