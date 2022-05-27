package com.example.proyectofinal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectofinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GruposFragment extends Fragment {
    RequestQueue requestQueue;
    private EditText edtIdGrupo,edtNombreGrupo;
    private Spinner spGruposTutores;
    private ArrayList<String> tutoresList;
    private String selectedId, idTutor,tutorBuscar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //region elementos graficos
        View v=inflater.inflate(R.layout.fragment_grupos,container,false);
        edtIdGrupo=v.findViewById(R.id.edtIdGrupo);
        edtNombreGrupo=v.findViewById(R.id.edtNombreGrupo);
        spGruposTutores=v.findViewById(R.id.spGruposTutores);
        Button btnInsertar = v.findViewById(R.id.btnIngresarGrupo);
        Button btnBuscar=v.findViewById(R.id.btnBuscarGrupo);
        Button btnEditar = v.findViewById(R.id.btnEditarGrupo);
        Button btnEliminar = v.findViewById(R.id.btnEliminarGrupo);
        Button btnReset=v.findViewById(R.id.btnResetGrupo);
        tutoresList=new ArrayList<>();
        //endregion
        obtenerArray("http://192.168.1.71/proyectofinal/GrupoLlenarSpinnerTutores.php");
        spGruposTutores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedId=spGruposTutores.getItemAtPosition(spGruposTutores.getSelectedItemPosition()).toString();
                selectedId=selectedId.substring(0,5);
                idTutor ="";
                char[] div=selectedId.toCharArray();
                for(int z=0;z<div.length;z++){
                    if(Character.isDigit(div[z])){
                        idTutor +=div[z];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/GrupoInsertar.php");
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarGrupo("http://192.168.1.71/proyectofinal/GrupoConsulta.php?idGrupo="+edtIdGrupo.getText());
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarGrupo("http://192.168.1.71/proyectofinal/GrupoBorrar.php");
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/GrupoEditar.php");
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
    private void ejecutarServicio(String URL)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Tutor agregado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("nombreGrupo",edtNombreGrupo.getText().toString());
                parametros.put("tutor",idTutor);
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void buscarGrupo(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edtIdGrupo.setText(jsonObject.getString("idGrupo"));
                        edtNombreGrupo.setText(jsonObject.getString("nombreGrupo"));
                        tutorBuscar=(jsonObject.getString("tutor"));
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                mostrarTutorActual();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"Grupo no encontrado",Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue=Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
    private void eliminarGrupo(String URL)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Grupo eliminado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("idGrupo",edtIdGrupo.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //endregion

    //region otros metodos
    private void limpiarForm(){
        edtIdGrupo.setText("");
        edtNombreGrupo.setText("");
        spGruposTutores.setSelection(0);
    }
    private void obtenerArray(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("tutores");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tempId=jsonObject1.getString("idTutor");
                                String temp=jsonObject1.getString("maestro");
                                String insert="Id "+tempId+" - Id Maestro "+temp;
                                tutoresList.add(insert);

                            }
                            spGruposTutores.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,tutoresList));
                        }catch (JSONException e){e.printStackTrace();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }
    private void mostrarTutorActual(){
        int pos=0;
        for(int u=0;u<tutoresList.size();u++){

            String temp,comp;
            temp="";
            comp=tutoresList.get(u);
            Log.d("completo",String.valueOf(comp));
            temp=comp.replace(" ","");
            temp=temp.substring(0,7);
            char[] div=temp.toCharArray();
            temp="";
            for(int i=0;i<div.length;i++){
                if(Character.isDigit(div[i])){
                    temp+=div[i];
                }
            }
            Log.d("comparar",String.valueOf(temp));
            Log.d("comparar2",String.valueOf(tutorBuscar));
            tutorBuscar=tutorBuscar.replace(" ","");
            temp=temp.replace(" ","");
            if(temp.equals(tutorBuscar)){
                pos=tutoresList.indexOf(comp);
                spGruposTutores.setSelection(pos);
            }
        }
    }
    //endregion
}