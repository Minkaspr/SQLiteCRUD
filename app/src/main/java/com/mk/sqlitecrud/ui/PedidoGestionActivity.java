package com.mk.sqlitecrud.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.sqlitecrud.R;
import com.mk.sqlitecrud.model.Pedido;
import com.mk.sqlitecrud.model.Plato;
import com.mk.sqlitecrud.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PedidoGestionActivity extends AppCompatActivity {
    private LinearLayout llContenedorCheckBox;
    private TextView detallePedido;
    private TextView total;
    private DatabaseHelper db;
    private Pedido pedido;
    private double precioTotal = 0.0;
    private boolean cambioEnPedido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_gestion);

        llContenedorCheckBox = findViewById(R.id.llContenedorCheckBox);
        detallePedido = findViewById(R.id.detallePedido);
        total = findViewById(R.id.total);
        db = new DatabaseHelper(this);

        // Recuperar el ID del pedido desde el Intent
        int idPedido = getIntent().getIntExtra("ID_PEDIDO", -1);
        if (idPedido != -1) {
            pedido = db.obtenerPedido(idPedido);
            List<Plato> platosPedido = db.obtenerPlatosPedido(idPedido);
            cargarDatosPedido(platosPedido);
        }

        AppCompatButton btnEliminar = findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(v -> {
            boolean exito = db.eliminarPedido(pedido.getId());
            if (exito) {
                Toast.makeText(this, "Pedido eliminado con éxito.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar el pedido.", Toast.LENGTH_LONG).show();
            }
        });

        AppCompatButton btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(v -> {
            List<Plato> platosSeleccionados = new ArrayList<>();
            for (int i = 0; i < llContenedorCheckBox.getChildCount(); i++) {
                View view = llContenedorCheckBox.getChildAt(i);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    if (checkBox.isChecked()) {
                        Plato plato = (Plato) checkBox.getTag();
                        platosSeleccionados.add(plato);
                    }
                }
            }
            pedido.setPrecioTotal(precioTotal);
            boolean exito = db.actualizarPedido(pedido, platosSeleccionados);
            if (exito) {
                Toast.makeText(this, "Pedido actualizado con éxito.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar el pedido.", Toast.LENGTH_LONG).show();
            }
        });

        if(!cambioEnPedido){
            String ttl = "Total: $" + pedido.getPrecioTotal();
            total.setText(ttl);
        }
    }

    private void cargarDatosPedido(List<Plato> platosPedido) {
        List<Plato> todosLosPlatos = db.obtenerPlatos();
        for (Plato plato : todosLosPlatos) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(plato.getNombre());
            checkBox.setTag(plato);
            // Verificamos si el plato está en el pedido
            for (Plato platoPedido : platosPedido) {
                if (platoPedido.getId() == plato.getId()) {
                    checkBox.setChecked(true);
                    detallePedido.append("\n" + plato.getNombre() + ": $" + plato.getPrecio());
                    precioTotal += plato.getPrecio();
                    break;
                }
            }
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> gestionarSeleccionPlato(plato, isChecked));
            llContenedorCheckBox.addView(checkBox);
        }
        actualizarTotal();
    }

    private void gestionarSeleccionPlato(Plato plato, boolean isChecked) {
        cambioEnPedido = true;
        if (isChecked) {
            detallePedido.append("\n" + plato.getNombre() + ": $" + plato.getPrecio());
            precioTotal += plato.getPrecio();
        } else {
            String detalle = "\n" + plato.getNombre() + ": $" + plato.getPrecio();
            String detallesActuales = detallePedido.getText().toString();
            detallesActuales = detallesActuales.replace(detalle, "");
            detallePedido.setText(detallesActuales);
            precioTotal -= plato.getPrecio();
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        if (cambioEnPedido) {
            String ttl = "Total: $" + precioTotal;
            total.setText(ttl);
        }
    }
}
