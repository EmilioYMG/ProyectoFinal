package com.example.proyectofinal.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class CalifFragment extends Fragment {

    private TableLayout tbCalif;
    RequestQueue requestQueue;
    private EditText edtIdCalifAlumno;
    private ArrayList<String> califList,actList;
    private TextView txtPromedio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_calif,container,false);
        tbCalif=v.findViewById(R.id.tbCalif);
        tbCalif.removeAllViews();
        encabezadosTabla();
        txtPromedio=v.findViewById(R.id.txtPromedio);
        edtIdCalifAlumno=v.findViewById(R.id.edtIdCalifAlumno);
        Button btnBuscar=v.findViewById(R.id.btnBuscarCalif);
        Button btnReset=v.findViewById(R.id.btnResetCalif);
        califList=new ArrayList<>();
        actList=new ArrayList<>();
        obtenerArray("http://192.168.1.71/proyectofinal/CalifObtenerActs.php");

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                califList=new ArrayList<>();
                llenarTabla("http://192.168.1.71/proyectofinal/CalifLlenarTabla.php?alumno="+edtIdCalifAlumno.getText());

            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarForm();
            }
        });
        return v;
    }
    private void obtenerArray(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("acts");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                String tempId=jsonObject1.getString("idActividad");
                                actList.add(tempId);
                            }
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
    private void llenarTabla(String URL){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest( URL, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        View registro = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.table_row_calif, null, false);
                        TextView txtIdBoleta = registro.findViewById(R.id.txtIdBoleta);
                        TextView txtIdAlumno = registro.findViewById(R.id.txtIdAlumno);
                        TextView txtAct = registro.findViewById(R.id.txtAct);
                        TextView txtCalif = registro.findViewById(R.id.txtCalif);
                        txtIdBoleta.setText(jsonObject.getString("idBoleta"));
                        txtIdAlumno.setText(jsonObject.getString("alumno"));
                        txtAct.setText(jsonObject.getString("actividad"));
                        txtCalif.setText(jsonObject.getString("calificacion"));
                        String tmp=jsonObject.getString("calificacion");
                        tbCalif.addView(registro);
                        califList.add(tmp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                promedio();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"No hay datos",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private void promedio(){
        float tmp=0;
        float prom=0;
        for(int i=0;i<califList.size();i++){

            tmp=Integer.parseInt(califList.get(i));
            tmp+=tmp;
        }
        int tamaño=actList.size();
         prom=tmp/tamaño;

        txtPromedio.setText(" "+String.valueOf(prom));
    }
    private void limpiarForm(){
        edtIdCalifAlumno.setText("");
        txtPromedio.setText("");
        tbCalif.removeAllViews();
        encabezadosTabla();

    }
    private void encabezadosTabla(){
        View registro = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.table_row_calif, null, false);
        TextView txtIdBoleta = registro.findViewById(R.id.txtIdBoleta);
        TextView txtIdAlumno = registro.findViewById(R.id.txtIdAlumno);
        TextView txtAct = registro.findViewById(R.id.txtAct);
        TextView txtCalif = registro.findViewById(R.id.txtCalif);
        txtIdBoleta.setText("Boleta");
        txtIdAlumno.setText("Alumno");
        txtAct.setText("Actividad");
        txtCalif.setText("Calificacion");
        tbCalif.addView(registro);
    }

}