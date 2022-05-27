package com.example.proyectofinal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectofinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PadresFragment extends Fragment {
    RequestQueue requestQueue;
    private EditText edtIdPadre,edtNombrePadre,edtEmailPadre,edtTelefonoCasaPadre,edtTelefonoCelPadre;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //region elementos graficos
        View v=inflater.inflate(R.layout.fragment_padres,container,false);
        edtIdPadre=v.findViewById(R.id.edtIdPadre);
        edtNombrePadre=v.findViewById(R.id.edtNombrePadre);
        edtEmailPadre=v.findViewById(R.id.edtEmailPadre);
        edtTelefonoCasaPadre=v.findViewById(R.id.edtTelefonoCasaPadre);
        edtTelefonoCelPadre=v.findViewById(R.id.edtTelefonoCelPadre);
        Button btnInsertar=v.findViewById(R.id.btnIngresarPadre);
        Button btnBuscar=v.findViewById(R.id.btnBuscarPadre);
        Button btnEditar=v.findViewById(R.id.btnEditarPadre);
        Button btnEliminar=v.findViewById(R.id.btnEliminarPadre);
        Button btnReset=v.findViewById(R.id.btnResetPadre);
        //endregion
        //region onclick botones
        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/PadreInsertar.php");
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPadre("http://192.168.1.71/proyectofinal/PadreConsulta.php?idPadre="+edtIdPadre.getText());
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarPadre("http://192.168.1.71/proyectofinal/PadreBorrar.php");
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/PadreEditar.php");
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarForm();
            }
        });
        //endregion
        return v;
    }
    //region comunicacion php-app
    private void ejecutarServicio(String URL)//AgregarMaestro
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Padre agregado con éxito", Toast.LENGTH_SHORT).show();
                limpiarForm();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("nombrePadre",edtNombrePadre.getText().toString());
                parametros.put("telefonoCasa",edtTelefonoCasaPadre.getText().toString());
                parametros.put("telefonoCelular",edtTelefonoCelPadre.getText().toString());
                parametros.put("emailPadre",edtEmailPadre.getText().toString());
                parametros.put("idPadre",edtIdPadre.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void buscarPadre(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edtIdPadre.setText(jsonObject.getString("idPadre"));
                        edtNombrePadre.setText(jsonObject.getString("nombrePadre"));
                        edtTelefonoCasaPadre.setText(jsonObject.getString("telefonoCasa"));
                        edtTelefonoCelPadre.setText(jsonObject.getString("telefonoCelular"));
                        edtEmailPadre.setText(jsonObject.getString("emailPadre"));

                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"Padre no encontrado",Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue=Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
    private void eliminarPadre(String URL)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Padre eliminado con éxito", Toast.LENGTH_SHORT).show();
                limpiarForm();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("idPadre",edtIdPadre.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //endregion
    //region otros metodos
    private void limpiarForm(){
        edtIdPadre.setText("");
        edtNombrePadre.setText("");
        edtTelefonoCasaPadre.setText("");
        edtTelefonoCelPadre.setText("");
        edtEmailPadre.setText("");

    }
    //endregion
}