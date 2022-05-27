package com.example.proyectofinal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectofinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActividadesFragment extends Fragment {
    private EditText edtIdAct,edtNombreAct,edtPondAct;
    RequestQueue requestQueue;
    Spinner spActsGrupos;
    private ArrayList<String> actsGruposList;
    private String selectedId,idGrupo,buscarGrupo;
    private TableLayout tbActs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_actividades,container,false);
        tbActs=v.findViewById(R.id.tbActs);
        edtIdAct=v.findViewById(R.id.edtIdAct);
        edtNombreAct=v.findViewById(R.id.edtNombreAct);
        edtPondAct=v.findViewById(R.id.edtPondAct);
        spActsGrupos=v.findViewById(R.id.spActsGrupos);
        Button btnInsertar=v.findViewById(R.id.btnIngresarAct);
        Button btnBuscar=v.findViewById(R.id.btnBuscarAct);
        Button btnEditar=v.findViewById(R.id.btnEditarAct);
        Button btnEliminar=v.findViewById(R.id.btnEliminarAct);
        Button btnReset=v.findViewById(R.id.btnResetAct);
        actsGruposList=new ArrayList<>();
        obtenerArray("http://192.168.1.71/proyectofinal/ActLlenarSpinnerGrupos.php");
        encabezadosTabla();
        llenarTabla("http://192.168.1.71/proyectofinal/ActLlenarTabla.php");
        //region onclick botones
        spActsGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedId=spActsGrupos.getItemAtPosition(spActsGrupos.getSelectedItemPosition()).toString();
                selectedId=selectedId.substring(0,5);
                idGrupo="";
                char[] dividir=selectedId.toCharArray();
                for(int j=0;j<dividir.length;j++){
                    if(Character.isDigit(dividir[j])){
                        idGrupo+=dividir[j];
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
                ejecutarServicio("http://192.168.1.71/proyectofinal/ActInsertar.php");
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarAct("http://192.168.1.71/proyectofinal/ActConsulta.php?idActividad="+edtIdAct.getText());
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarAct("http://192.168.1.71/proyectofinal/ActBorrar.php");
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/ActEditar.php");
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
                Toast.makeText(getActivity().getApplicationContext(), "Actividad agregado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("nombreActividad",edtNombreAct.getText().toString());
                parametros.put("ponderacionActividad",edtPondAct.getText().toString());
                parametros.put("idActividad",edtIdAct.getText().toString());
                parametros.put("grupo",idGrupo);
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void buscarAct(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edtIdAct.setText(jsonObject.getString("idActividad"));
                        edtNombreAct.setText(jsonObject.getString("nombreActividad"));
                        edtPondAct.setText(jsonObject.getString("ponderacionActividad")+"%");
                        buscarGrupo=jsonObject.getString("grupo");
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                mostrarArray();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"Actividad no encontrada",Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue=Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
    private void eliminarAct(String URL)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Actividad eliminada con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("idActividad",edtIdAct.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void obtenerArray(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("actGrupos");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tempId=jsonObject1.getString("idGrupo");
                                String temp=jsonObject1.getString("nombreGrupo");
                                String insert="Id "+tempId+" - Grupo "+temp;
                                actsGruposList.add(insert);

                            }
                            spActsGrupos.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,actsGruposList));
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
    //endregion
    //region otros metodos
    private void limpiarForm(){
        edtIdAct.setText("");
        edtNombreAct.setText("");
        edtPondAct.setText("");
        tbActs.removeAllViews();
        encabezadosTabla();
    }
    private void mostrarArray(){
      int pos=0;
        for(int u=0;u<actsGruposList.size();u++){

            String temp,comp;
            temp="";
            comp=actsGruposList.get(u);
            temp=comp.replace(" ","");
            char[] div=temp.toCharArray();
            temp="";
            for(int i=0;i<div.length;i++){
                if(Character.isDigit(div[i])){
                    temp+=div[i];
                }
            }
            buscarGrupo=buscarGrupo.replace(" ","");
            temp=temp.replace(" ","");
            if(temp.equals(buscarGrupo)){
                pos=actsGruposList.indexOf(comp);
                spActsGrupos.setSelection(pos);
            }
        }
    }
    private void llenarTabla(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest( URL, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data1");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        View registro = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.table_row_acts, null, false);
                        TextView txtIdAct = registro.findViewById(R.id.txtIdAct);
                        TextView txtNombreAct = registro.findViewById(R.id.txtNombreAct);
                        TextView txtPondAct = registro.findViewById(R.id.txtPondAct);
                        TextView txtActGrupo = registro.findViewById(R.id.txtActGrupo);
                        txtIdAct.setText(jsonObject.getString("idActividad"));
                        txtNombreAct.setText(jsonObject.getString("nombreActividad"));
                        txtPondAct.setText(jsonObject.getString("ponderacionActividad"));
                        txtActGrupo.setText(jsonObject.getString("grupo"));
                        tbActs.addView(registro);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"No hay datos",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private void encabezadosTabla(){
        View registro = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.table_row_acts, null, false);
        TextView txtIdAct = registro.findViewById(R.id.txtIdAct);
        TextView txtNombreAct = registro.findViewById(R.id.txtNombreAct);
        TextView txtPondAct = registro.findViewById(R.id.txtPondAct);
        TextView txtActGrupo = registro.findViewById(R.id.txtActGrupo);
        txtIdAct.setText("Id");
        txtNombreAct.setText("Actividad");
        txtPondAct.setText("Ponderación");
        txtActGrupo.setText("Grupo");
        tbActs.addView(registro);
    }
    //endregion



}