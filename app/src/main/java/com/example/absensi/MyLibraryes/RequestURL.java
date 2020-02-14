package com.example.absensi.MyLibraryes;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RequestURL {
    public interface MyRequest {
        public int getMethod();
        public String getUrl() ;
        public Map<String, String> param(Map<String, String> data);
        public void response(Object response);
        public void err(VolleyError error);
    }

    MyRequest myRequest ;
    Context context ;

    public RequestURL(Context context, MyRequest myRequest){
        this.myRequest = myRequest ;
        this.context = context ;
    }

    public void get(){
        Volley.newRequestQueue(this.context).add(new StringRequest(
                myRequest.getMethod(),
                myRequest.getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        myRequest.response(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        myRequest.err(error);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<String, String>();
                return myRequest.param(data);
            }
        });
    }

}
