package com.example.projekuas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class EditAlamat extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btnDiscardAlamat, btnSaveAlamat, btnDeleteAlamat;
    String username, receiverName, receiverPhone, receiverAddress, receiverPostalCode;
    int addressID, custID;
    EditText edtNamaPenerima1, edtNomorTelepon1, edtAlamat1, edtKodePos1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alamat);

        Bundle bundle = getIntent().getExtras();
        username = (String)bundle.get("username");
        addressID = (int)bundle.get("addressID");
        custID = (int)bundle.get("custID");
        receiverName = (String)bundle.get("receiverName");
        receiverPhone = (String)bundle.get("receiverPhone");
        receiverAddress = (String)bundle.get("receiverAddress");
        receiverPostalCode = (String)bundle.get("receiverPostalCode");

        edtNamaPenerima1 = findViewById(R.id.edtNamaPenerima1);
        edtNomorTelepon1 = findViewById(R.id.edtNomorTelepon1);
        edtAlamat1 = findViewById(R.id.edtAlamat1);
        edtKodePos1 = findViewById(R.id.edtKodePos1);

        edtNamaPenerima1.setText(receiverName);
        edtNomorTelepon1.setText(receiverPhone);
        edtAlamat1.setText(receiverAddress);
        edtKodePos1.setText(receiverPostalCode);

        btnSaveAlamat = findViewById(R.id.btnSaveAlamat);
        btnSaveAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getIntent().getExtras();
                addressID = (int)bundle.get("addressID");

                receiverName = edtNamaPenerima1.getText().toString();
                receiverPhone = edtNomorTelepon1.getText().toString();
                receiverAddress = edtAlamat1.getText().toString();
                receiverPostalCode = edtKodePos1.getText().toString();

                RequestQueue queue = Volley.newRequestQueue(EditAlamat.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Configuration.URL_UPDATE_ADDRESS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Toast.makeText(EditAlamat.this,
                                    "Address has been updated", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditAlamat.this,
                                "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("addressID", String.valueOf(addressID));
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

        btnDeleteAlamat = findViewById(R.id.btnDeleteAlamat);
        btnDeleteAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogDeleteAddress();
            }
        });
    }

    private void openDialogDeleteAddress() {
        final Dialog dialog = new Dialog(EditAlamat.this);

        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.dialog_delete_address, null);

        dialog.setContentView(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_popup));
        }

        Button btnCancel = v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnDelete = v.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(EditAlamat.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Configuration.URL_DELETE_ADDRESS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ",error.getMessage());
                        Toast.makeText(EditAlamat.this,
                                "Check your internet connection!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }){
                    protected HashMap<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("addressID", String.valueOf(addressID));
                        return map;
                    }
                };
                queue.add(stringRequest);
                Intent i = new Intent(getApplicationContext(), DaftarAlamat.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                i.putExtra("username", username);
                startActivity(i);
                Toast.makeText(EditAlamat.this, "Address has been deleted",
                        Toast.LENGTH_LONG).show();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}