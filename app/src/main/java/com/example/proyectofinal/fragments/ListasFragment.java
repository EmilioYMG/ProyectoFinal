package com.example.proyectofinal.fragments;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal.R;

public class ListasFragment extends AppCompatActivity implements View.OnClickListener{
    private Button btnAgregar;
    private ListView ListView;
    private EditText EditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.fragment_listas);
        btnAgregar=findViewById(R.id.btnAgregar);
        ListView=findViewById(R.id.lstvAlumnos);
        EditText=findViewById(R.id.edtLista);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnAgregar:
        }
    }
}
