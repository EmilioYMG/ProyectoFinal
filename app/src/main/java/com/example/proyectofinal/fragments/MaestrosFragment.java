package com.example.proyectofinal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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

public class MaestrosFragment extends Fragment {
    private EditText edtNombre,edtEmail,edtTelefono,edtIdMaestro;
    RequestQueue requestQueue;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //region elementos graficos
        View v=inflater.inflate(R.layout.fragment_maestros,container,false);
        edtIdMaestro=v.findViewById(R.id.edtIdMestro);
        edtNombre=v.findViewById(R.id.edtNombreMaestro);
        edtEmail=v.findViewById(R.id.edtEmailMaestro);
        edtTelefono=v.findViewById(R.id.edtTelefonoMaestro);
        Button btnInsertar=v.findViewById(R.id.btnIngresarMaestro);
        Button btnBuscar=v.findViewById(R.id.btnBuscarMaestro);
        Button btnEditar=v.findViewById(R.id.btnEditarMaestro);
        Button btnEliminar=v.findViewById(R.id.btnEliminarMaestro);
        Button btnReset=v.findViewById(R.id.btnResetMaestro);
        //endregion
        //region onclick botones
        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/MaestroInsertar.php");
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarMaestro("http://192.168.1.71/proyectofinal/MaestroConsulta.php?idMaestro="+edtIdMaestro.getText());
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarMaestro("http://192.168.1.71/proyectofinal/MaestroBorrar.php");
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/MaestroEditar.php");
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
                Toast.makeText(getActivity().getApplicationContext(), "Maestro agregado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("nombreMaestro",edtNombre.getText().toString());
                parametros.put("emailMaestro",edtEmail.getText().toString());
                parametros.put("telefonoMaestro",edtTelefono.getText().toString());
                parametros.put("idMaestro",edtIdMaestro.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void buscarMaestro(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edtIdMaestro.setText(jsonObject.getString("idMaestro"));
                        edtNombre.setText(jsonObject.getString("nombreMaestro"));
                        edtEmail.setText(jsonObject.getString("emailMaestro"));
                        edtTelefono.setText(jsonObject.getString("telefonoMaestro"));
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"Maestro no encontrado",Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue=Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
    private void eliminarMaestro(String URL)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Maestro eliminado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("idMaestro",edtIdMaestro.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //endregion
    //region otros metodos
    private void limpiarForm(){
        edtIdMaestro.setText("");
        edtNombre.setText("");
        edtEmail.setText("");
        edtTelefono.setText("");
    }
    //endregion
}