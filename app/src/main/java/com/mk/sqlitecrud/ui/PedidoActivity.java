package com.mk.sqlitecrud.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.sqlitecrud.R;
import com.mk.sqlitecrud.model.Plato;
import com.mk.sqlitecrud.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PedidoActivity extends AppCompatActivity {
    private LinearLayout llContenedorCheckBox;
    private RadioGroup rgDescuentos;
    private DatabaseHelper db;
    private TextView detallePedido;
    private TextView total;
    private double[] subTotal = {0.0};
    private double[] precioTotal = {0.0};
    private int[] descuentos = {0, 5, 10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        llContenedorCheckBox = findViewById(R.id.llContenedorCheckBox);
        detallePedido = findViewById(R.id.detallePedido);
        total = findViewById(R.id.total);
        rgDescuentos = new RadioGroup(this);
        db = new DatabaseHelper(this);
        cargarPlatos();
        cargarDescuentos();
        AppCompatButton btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> {
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
            long idPedido = db.registrarPedido(precioTotal[0], platosSeleccionados);
            if (idPedido == -1) {
                Toast.makeText(this, "Error al registrar el pedido.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pedido registrado con éxito. ID del pedido: " + idPedido, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cargarPlatos() {
        List<Plato> listaPlatos = db.obtenerPlatos();
        for (Plato plato : listaPlatos) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(plato.getNombre());
            checkBox.setTag(plato);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> gestionarSeleccionPlato(plato, isChecked));
            llContenedorCheckBox.addView(checkBox);
        }
    }

    private void cargarDescuentos() {
        for (int i = 0; i < descuentos.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(descuentos[i] + "%");
            rb.setId(i);
            rgDescuentos.addView(rb);
            if (i == 0) {
                rgDescuentos.check(i);
            }
        }
        llContenedorCheckBox.addView(rgDescuentos);
        rgDescuentos.setOnCheckedChangeListener((group, checkedId) -> actualizarTotal());
    }

    private void gestionarSeleccionPlato(Plato plato, boolean isChecked) {
        if (isChecked) {
            detallePedido.append("\n" + plato.getNombre() + ": $" + plato.getPrecio());
            subTotal[0] += plato.getPrecio();
        } else {
            String detalle = "\n" + plato.getNombre() + ": $" + plato.getPrecio();
            String detallesActuales = detallePedido.getText().toString();
            detallesActuales = detallesActuales.replace(detalle, "");
            detallePedido.setText(detallesActuales);
            subTotal[0] -= plato.getPrecio();
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        int descuento = descuentos[rgDescuentos.getCheckedRadioButtonId()];
        precioTotal[0] = subTotal[0] * (1 - descuento / 100.0);
        String ttl = "SubTotal: $" + subTotal[0] + "\nDescuento: " + descuento + "%\nTotal: $" + precioTotal[0];
        total.setText(ttl);
    }
}
