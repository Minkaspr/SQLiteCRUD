package com.mk.sqlitecrud.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mk.sqlitecrud.R;
import com.mk.sqlitecrud.model.Pedido;
import com.mk.sqlitecrud.ui.adapter.PedidoAdapter;
import com.mk.sqlitecrud.util.DatabaseHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvPlatosPedidos;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPlatosPedidos = findViewById(R.id.rvPlatosPedidos);
        db = new DatabaseHelper(this);
        FloatingActionButton btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, PedidoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        List<Pedido> listaPedidos = db.obtenerPedidos();
        PedidoAdapter adapter = new PedidoAdapter(this, listaPedidos);
        rvPlatosPedidos.setLayoutManager(new LinearLayoutManager(this));
        rvPlatosPedidos.setAdapter(adapter);
    }

}