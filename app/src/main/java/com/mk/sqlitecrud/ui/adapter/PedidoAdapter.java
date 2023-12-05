package com.mk.sqlitecrud.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mk.sqlitecrud.R;
import com.mk.sqlitecrud.model.Pedido;
import com.mk.sqlitecrud.ui.PedidoGestionActivity;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private final Context context;
    private final List<Pedido> listaPedidos;

    public PedidoAdapter(Context context, List<Pedido> listaPedidos) {
        this.context = context;
        this.listaPedidos = listaPedidos;
    }

    @Override
    public PedidoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);
        String cod = "Id: "+pedido.getId();
        String cant = "Cant. Platos: " + pedido.getCantidadPlatos();
        String prec = "Prec. Total: S/"+pedido.getPrecioTotal();
        holder.tvId.setText(cod);
        holder.tvCantPlatos.setText(cant);
        holder.tvPrecio.setText(prec);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PedidoGestionActivity.class);
            intent.putExtra("ID_PEDIDO", pedido.getId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvCantPlatos, tvPrecio;

        PedidoViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvCantPlatos = itemView.findViewById(R.id.tvCantPlatos);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }
    }
}
