package com.example.projekuas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TambahAlamat extends AppCompatActivity {
    Button btnDiscardAlamat, btnSaveAlamat;
    EditText edtNamaPenerima, edtNomorTelepon, edtAlamat, edtKodePos;
    String username, receiverName, receiverPhone, receiverAddress, receiverPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tambah_alamat);

        edtNamaPenerima = findViewById(R.id.edtNamaPenerima);
        edtNomorTelepon = findViewById(R.id.edtNomorTelepon);
        edtAlamat = findViewById(R.id.edtAlamat);
        edtKodePos = findViewById(R.id.edtKodePos);

        btnSaveAlamat = findViewById(R.id.btnSaveAlamat);
        btnSaveAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");

                receiverName = edtNamaPenerima.getText().toString();
                receiverPhone = edtNomorTelepon.getText().toString();
                receiverAddress = edtAlamat.getText().toString();
                receiverPostalCode = edtKodePos.getText().toString();

                RequestQueue queue = Volley.newRequestQueue(TambahAlamat.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Configuration.URL_ADD_ADDRESS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Toast.makeText(TambahAlamat.this,
                                    "New address has been saved", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), DaftarAlamat.class);
                            Bundle bundle = getIntent().getExtras();
                            username = bundle.getString("username");
                            i.putExtra("username", username);
                            startActivity(i);
                        } catch (Exception ex) {
                            Log.e("Error: ", ex.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ", error.getMessage());
                        Toast.makeText(TambahAlamat.this,
                                "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("receiverName", receiverName);
                        params.put("receiverPhone", receiverPhone);
                        params.put("receiverAddress", receiverAddress);
                        params.put("receiverPostalCode", receiverPostalCode);
                        return params;
                    }
                };
                queue.getCache().clear();
                queue.add(stringRequest);
            }
        });

        btnDiscardAlamat = findViewById(R.id.btnDiscardAlamat);
        btnDiscardAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DaftarAlamat.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                i.putExtra("username", username);
                startActivity(i);
            }
        });
    }

}