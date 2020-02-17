package com.example.absensi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.absensi.MyLibraryes.RequestURL;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class AbsensiActivity extends AppCompatActivity {

    public static String VALUE_IMEI_PEGAWAI = "VALUE_IMEI_PEGAWAI" ;
    private String base_url = "http://10.17.0.4/fpro_v2/APIcoc/" ;
    private IntentIntegrator qrScan ;
    private Button btn_absen ;
    private String NIP ;
    private TextView nip, nmAcara, waktuAcara, deskAbsensi, statusAbsensi, waktuAbsensi ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);
        NIP = getIntent().getSerializableExtra(VALUE_IMEI_PEGAWAI).toString();

        nip = findViewById(R.id.txt_nip_kamu);
        nmAcara = findViewById(R.id.txt_nama_acara);
        waktuAcara = findViewById(R.id.txt_waktu_acara);
        deskAbsensi = findViewById(R.id.txt_desk_absensi);
        statusAbsensi = findViewById(R.id.txt_status_absensi);
        btn_absen = findViewById(R.id.btn_absen);
        waktuAbsensi = findViewById(R.id.txt_waktu_absensi);

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
                JSONObject dataQrCode = null ;
                try {
                    dataQrCode = new JSONObject(result.getContents());
                    nip.setText(NIP);
                    nmAcara.setText(dataQrCode.getString("acara"));
                    waktuAcara.setText(dataQrCode.getString("waktu_tanggal_acara"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.handlerAbsensi(dataQrCode);
                this.hanlderCheckAbsensi();
//                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "KAMU TELAH KELUAR DARI QR CODE", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void handlerAbsensi(final JSONObject dataQrCode){
        final String urlAbsensi = base_url+"?Req=ABSEN";
        new RequestURL(getApplicationContext(), new RequestURL.MyRequest() {
            @Override
            public int getMethod() {
                return Request.Method.POST ;
            }

            @Override
            public String getUrl() {
                return urlAbsensi;
            }

            @Override
            public Map<String, String> param(Map<String, String> data) {
                try {
                    data.put("NIP", NIP);
                    data.put("ACARA", dataQrCode.getString("acara"));
                    data.put("TANGGAL", dataQrCode.getString("waktu_tanggal_acara"));
                    data.put("JAM_MASUK", getDateTime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            public void response(Object response) {
                JSONObject object = null ;
                String message = "" ;
                String status = "" ;
                try {
                    object = new JSONObject(response.toString());
                    message = object.getString("message").toUpperCase() ;
                    status = object.getString("status").toUpperCase() ;
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    deskAbsensi.setText(message);
                    statusAbsensi.setText(status);
                    statusAbsensi.setTextColor(
                            status.equalsIgnoreCase("ONTIME")
                                    ? getResources().getColor(R.color.colorGreen)
                                    : getResources().getColor(R.color.colorRed)
                    );
                    waktuAbsensi.setText(getDateTime());
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void err(VolleyError error) {

            }
        }).get();
    }

    public void hanlderCheckAbsensi(){
        btn_absen.setBackgroundResource(R.drawable.btn_absen);
        btn_absen.setText("TELAH ABSEN, ULANGI SCAN");
//        btn_absen.setEnabled(false);
    }

    public String getDateTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
    }
}
