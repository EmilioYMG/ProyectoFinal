package com.example.proyectofinal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

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

public class AgregarFragment extends Fragment  {
    private EditText edtNombre,edtApePa,edtApeMa,edtEmail,edtId,edtNumLista;
    RequestQueue requestQueue;
    private Spinner spGrupos,spPadre;
    private ArrayList<String> gruposList,padresList;
    private String selectedId, idGrupo=null,selectedPadre,idPadre=null,buscarGrupo,buscarPadre;
    private TextView txtGrupoBuscar,txtPadreBuscar;
    private SwitchCompat swEdicion;
    private  boolean banderaEdicion=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_agregar,container,false);
        swEdicion=v.findViewById(R.id.swtEditar);
        txtGrupoBuscar=v.findViewById(R.id.txtGrupoBuscar);
        txtPadreBuscar=v.findViewById(R.id.txtPadreBuscar);
        edtNombre=v.findViewById(R.id.edtNombre);
        edtNumLista=v.findViewById(R.id.edtNumLista);
        edtApePa=v.findViewById(R.id.edtApePa);
        edtApeMa=v.findViewById(R.id.edtApeMa);
        edtEmail=v.findViewById(R.id.edtEmail);
        edtId=v.findViewById(R.id.edtId);
        Button btnInsertar = v.findViewById(R.id.btnIngresar);
        Button btnBuscar=v.findViewById(R.id.btnBuscar);
        Button btnEditar = v.findViewById(R.id.btnEditar);
        Button btnEliminar = v.findViewById(R.id.btnEliminar);
        Button btnReset=v.findViewById(R.id.btnReset);
        spGrupos=v.findViewById(R.id.spGrupos);
        spPadre=v.findViewById(R.id.spPadre);
        gruposList=new ArrayList<>();
        padresList=new ArrayList<>();

        txtGrupoBuscar.setWidth(160);
        txtPadreBuscar.setWidth(50);


        obtenerArray("http://192.168.1.71/proyectofinal/AlumnoLlenarSpinnerGrupos.php");
        obtenerArrayPadre("http://192.168.1.71/proyectofinal/AlumnoLlenarSpinnerPadres.php");


        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ejecutarServicio("http://192.168.1.71/proyectofinal/AlumnoInsertar.php");
            }
        });
        swEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swEdicion.isChecked()){banderaEdicion=true;}
                else{banderaEdicion=false;}
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banderaEdicion){
                    buscarAlumno("http://192.168.1.71/proyectofinal/AlumnoConsulta.php?idAlumno="+edtId.getText());
                    swEdicion.setChecked(false);
                    banderaEdicion=false;
                    txtPadreBuscar.setVisibility(View.GONE);
                    txtGrupoBuscar.setVisibility(View.GONE);
                }else{
                    spGrupos.setEnabled(false);
                    spGrupos.setVisibility(View.GONE);
                    spPadre.setVisibility(View.GONE);
                    spPadre.setEnabled(false);
                    buscarAlumno("http://192.168.1.71/proyectofinal/AlumnoConsulta.php?idAlumno="+edtId.getText());
                    txtGrupoBuscar.setVisibility(View.VISIBLE);
                    txtPadreBuscar.setVisibility(View.VISIBLE);
                    txtGrupoBuscar.setWidth(1000);
                    txtPadreBuscar.setWidth(1000);
                }

            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarAlumno("http://192.168.1.71/proyectofinal/AlumnoBorrar.php");
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarForm();
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/AlumnoEditar.php");
            }
        });
        spGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedId=spGrupos.getItemAtPosition(spGrupos.getSelectedItemPosition()).toString();
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
        spPadre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPadre=spPadre.getItemAtPosition(spPadre.getSelectedItemPosition()).toString();
                selectedPadre=selectedPadre.substring(0,7);
                idPadre="";
                char[] dividir=selectedPadre.toCharArray();
                for (i=0;i<dividir.length;i++){
                    if(Character.isDigit(dividir[i])){
                        idPadre+=dividir[i];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return v;
    }
    private void ejecutarServicio(String URL)//AgregarAlumno
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Alumno agregado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("nombreAlumno",edtNombre.getText().toString());
                parametros.put("apellidoPaternoAlumno",edtApePa.getText().toString());
                parametros.put("apellidoMaternoAlumno",edtApeMa.getText().toString());
                parametros.put("email",edtEmail.getText().toString());
                parametros.put("grupo",idGrupo);
                Log.d("editar",String.valueOf(idGrupo));
                parametros.put("padre",idPadre);
                parametros.put("numLista",edtNumLista.getText().toString());
                parametros.put("idAlumno",edtId.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void buscarAlumno(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int k = 0; k < response.length(); k++) {
                    try {
                        jsonObject = response.getJSONObject(k);
                        edtId.setText(jsonObject.getString("idAlumno"));
                        edtNombre.setText(jsonObject.getString("nombreAlumno"));
                        edtApePa.setText(jsonObject.getString("apellidoPaternoAlumno"));
                        edtApeMa.setText(jsonObject.getString("apellidoMaternoAlumno"));
                        edtEmail.setText(jsonObject.getString("email"));
                        edtNumLista.setText(jsonObject.getString("numLista"));
                        buscarGrupo=(jsonObject.getString("grupo"));
                        buscarPadre=jsonObject.getString("padre");
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
               mostrarSpinnersBuscar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"Alumno no encontrado",Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue=Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
    private void eliminarAlumno(String URL)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity().getApplicationContext(), "Alumno eliminado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("idAlumno",edtId.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void limpiarForm(){
        edtId.setText("");
        edtNombre.setText("");
        edtApePa.setText("");
        edtApeMa.setText("");
        edtEmail.setText("");
        edtNumLista.setText("");
        spPadre.setVisibility(View.VISIBLE);
        spGrupos.setVisibility(View.VISIBLE);
        spPadre.setEnabled(true);
        spGrupos.setEnabled(true);
        txtGrupoBuscar.setText("");
        txtGrupoBuscar.setWidth(160);
        txtPadreBuscar.setText("");
        txtPadreBuscar.setWidth(50);
        swEdicion.setChecked(false);
        banderaEdicion=false;
    }
    private void mostrarSpinnersBuscar(){
        int pos=0,pos1=0;
        for(int u=0;u<gruposList.size();u++){

            String temp,comp;
            temp="";
            comp=gruposList.get(u);
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
                pos=gruposList.indexOf(comp);
            }
        }

        for(int u=0;u<padresList.size();u++){

            String temp1,comp1;
            temp1="";
            comp1=padresList.get(u);
            Log.d("completo",String.valueOf(comp1));
            temp1=comp1.replace(" ","");
            char[] div1=temp1.toCharArray();
            temp1="";
            for(int i=0;i<div1.length;i++){
                if(Character.isDigit(div1[i])){
                    temp1+=div1[i];
                }
            }
            Log.d("comparar",String.valueOf(temp1));
            Log.d("comparar2",String.valueOf(buscarPadre));
            buscarPadre=buscarPadre.replace(" ","");
            temp1=temp1.replace(" ","");
            if(temp1.equals(buscarPadre)){
                pos1=padresList.indexOf(comp1);
            }
        }
        //Log.d("valor",String.valueOf(gruposPos));
        if(banderaEdicion){
            spGrupos.setSelection(pos);
            spPadre.setSelection(pos1);
        }else{
            txtGrupoBuscar.setText(gruposList.get(pos));
            txtPadreBuscar.setText(padresList.get(pos1));
        }

    }
    private void obtenerArray(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("grupos");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tempId=jsonObject1.getString("idGrupo");
                                String temp=jsonObject1.getString("nombreGrupo");
                                String insert="Id "+tempId+" - Grupo "+temp;
                                gruposList.add(insert);

                            }
                            spGrupos.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,gruposList));
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
    private void obtenerArrayPadre(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("padres");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tempPaId=jsonObject1.getString("idPadre");
                                String temp=jsonObject1.getString("nombrePadre");
                                String insert="Id "+tempPaId+" - Nombre "+temp;
                                padresList.add(insert);

                            }
                            spPadre.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,padresList));
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
}
