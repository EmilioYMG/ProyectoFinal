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
import android.widget.TextView;
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

public class TutoresFragment extends Fragment {
    RequestQueue requestQueue;
    private EditText edtIdTutor;
    private ArrayList<String> maestrosList;
    Spinner spTutores;
    private String selectedId,idMaestro,maestroBuscar,maestroArray;
    TextView txtTutorActual;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //region elementos graficos
        View v=inflater.inflate(R.layout.fragment_tutores,container,false);
        edtIdTutor=v.findViewById(R.id.edtIdTutor);
        spTutores=v.findViewById(R.id.spTutores);
        Button btnInsertar = v.findViewById(R.id.btnIngresarTutor);
        Button btnBuscar=v.findViewById(R.id.btnBuscarTutor);
        Button btnEditar = v.findViewById(R.id.btnEditarTutor);
        Button btnEliminar = v.findViewById(R.id.btnEliminarTutor);
        Button btnReset=v.findViewById(R.id.btnResetTutor);
        txtTutorActual=v.findViewById(R.id.txtTutorActual);
        maestrosList=new ArrayList<>();
        //endregion
        obtenerArray("http://192.168.1.71/proyectofinal/TutorLlenarSpinnerMaestros.php");
        //region onclick botones
        spTutores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedId=spTutores.getItemAtPosition(spTutores.getSelectedItemPosition()).toString();
                selectedId=selectedId.substring(0,5);
                idMaestro="";
                char[] div=selectedId.toCharArray();
                for(int z=0;z<div.length;z++){
                    if(Character.isDigit(div[z])){
                        idMaestro+=div[z];
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
                ejecutarServicio("http://192.168.1.71/proyectofinal/TutorInsertar.php");
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buscarMaestro("http://192.168.1.71/proyectofinal/TutorConsulta.php?idTutor="+edtIdTutor.getText());

            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarMaestro("http://192.168.1.71/proyectofinal/TutorBorrar.php");
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://192.168.1.71/proyectofinal/TutorEditar.php");
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
                parametros.put("maestro",idMaestro);
                parametros.put("idTutor",edtIdTutor.getText().toString());
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
                        edtIdTutor.setText(jsonObject.getString("idTutor"));
                        maestroBuscar=(jsonObject.getString("maestro"));
                        Log.d("buscar",String.valueOf(maestroBuscar));
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                mostrarTutorActual();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"Tutor no encontrado",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity().getApplicationContext(), "Tutor eliminado con éxito", Toast.LENGTH_SHORT).show();
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
                parametros.put("idTutor",edtIdTutor.getText().toString());
                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //endregion
    //region otros metodos
    private void limpiarForm(){
        edtIdTutor.setText("");
        txtTutorActual.setText("");
    }
    private void obtenerArray(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("maestros");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tempId=jsonObject1.getString("idMaestro");
                                String temp=jsonObject1.getString("nombreMaestro");
                                String insert="Id "+tempId+" - Maestro "+temp;
                                maestrosList.add(insert);

                            }
                            spTutores.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,maestrosList));
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
        for(int u=0;u<maestrosList.size();u++){

            String temp,comp;
            temp="";
            comp=maestrosList.get(u);
            Log.d("completo",String.valueOf(comp));
            temp=comp.replace(" ","");
            char[] div=temp.toCharArray();
            temp="";
            for(int i=0;i<div.length;i++){
                if(Character.isDigit(div[i])){
                    temp+=div[i];
                }
            }
            Log.d("comparar",String.valueOf(temp));
            Log.d("comparar2",String.valueOf(maestroBuscar));
            maestroBuscar=maestroBuscar.replace(" ","");
            temp=temp.replace(" ","");
            if(temp.equals(maestroBuscar)){
                pos=maestrosList.indexOf(comp);
                Log.d("posicion",String.valueOf(pos));
                txtTutorActual.setText(maestrosList.get(pos));
            }

        }

    }
    //endregion
}