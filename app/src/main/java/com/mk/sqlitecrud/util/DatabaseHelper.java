package com.mk.sqlitecrud.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mk.sqlitecrud.model.Pedido;
import com.mk.sqlitecrud.model.Plato;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gestion_pedidos.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        crearTablas(db);
        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void crearTablas(SQLiteDatabase db) {
        // Crear la tabla "Plato"
        String tablaPlato = "CREATE TABLE Plato (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nombre TEXT NOT NULL," +
                "Precio REAL NOT NULL" +
                ");";
        db.execSQL(tablaPlato);

        // Crear la tabla "Pedido"
        String tablaPedido = "CREATE TABLE Pedido (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "PrecioTotal REAL NOT NULL," +
                "Hora TEXT NOT NULL," +
                "Fecha TEXT NOT NULL" +
                ");";
        db.execSQL(tablaPedido);

        // Crear la tabla "PedidoPlato"
        String tablaPedidoPlato = "CREATE TABLE PedidoPlato (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ID_Pedido INTEGER NOT NULL," +
                "ID_Plato INTEGER NOT NULL," +
                "FOREIGN KEY (ID_Pedido) REFERENCES Pedido (ID)," +
                "FOREIGN KEY (ID_Plato) REFERENCES Plato (ID)" +
                ");";
        db.execSQL(tablaPedidoPlato);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {
        // Insertar datos en "Plato"
        String[] platos = {"Pizza", "Hamburguesa", "Ensalada"};
        double[] precios = {10.0, 8.0, 6.0};
        for (int i = 0; i < platos.length; i++) {
            ContentValues values = new ContentValues();
            values.put("Nombre", platos[i]);
            values.put("Precio", precios[i]);
            db.insert("Plato", null, values);
        }

        // Insertar datos en "Pedido"
        String[] fechas = {"01/01/2023", "02/01/2023"};
        String[] horas = {"12:00", "13:00"};
        double[] preciosTotales = {18.0, 24.0};
        for (int i = 0; i < fechas.length; i++) {
            ContentValues values = new ContentValues();
            values.put("Fecha", fechas[i]);
            values.put("Hora", horas[i]);
            values.put("PrecioTotal", preciosTotales[i]);
            long idPedido = db.insert("Pedido", null, values);

            // Insertar datos en "PedidoPlato"
            for (int j = 1; j <= i + 2; j++) {
                values = new ContentValues();
                values.put("ID_Pedido", idPedido);
                values.put("ID_Plato", j);
                long idPedidoPlato = db.insert("PedidoPlato", null, values);
            }
        }
    }

    public List<Pedido> obtenerPedidos() {
        List<Pedido> listaPedidos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT Pedido.ID, " +
                "COUNT(PedidoPlato.ID_Plato) AS CantidadPlatos, " +
                "Pedido.PrecioTotal " +
                        "FROM Pedido " +
                        "INNER JOIN PedidoPlato ON Pedido.ID = PedidoPlato.ID_Pedido " +
                        "GROUP BY Pedido.ID, Pedido.PrecioTotal", null);

        if (cursor.moveToFirst()) {
            do {
                Pedido pedido = new Pedido();
                pedido.setId(cursor.getInt(0));
                pedido.setCantidadPlatos(cursor.getInt(1));
                pedido.setPrecioTotal(cursor.getDouble(2));
                listaPedidos.add(pedido);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaPedidos;
    }

    public List<Plato> obtenerPlatos() {
        List<Plato> listaPlatos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Plato", null);

        if (cursor.moveToFirst()) {
            do {
                Plato plato = new Plato();
                plato.setId(cursor.getInt(0));
                plato.setNombre(cursor.getString(1));
                plato.setPrecio(cursor.getDouble(2));
                listaPlatos.add(plato);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaPlatos;
    }

    public long registrarPedido(double precioTotal, List<Plato> platosSeleccionados) {
        // Obtener la fecha y hora actual
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Insertar el pedido en la tabla "Pedido"
        ContentValues values = new ContentValues();
        values.put("Fecha", fecha);
        values.put("Hora", hora);
        values.put("PrecioTotal", precioTotal);
        SQLiteDatabase db = this.getWritableDatabase();
        long idPedido = db.insert("Pedido", null, values);

        // Insertar los platos del pedido en la tabla "PedidoPlato"
        for (Plato plato : platosSeleccionados) {
            values = new ContentValues();
            values.put("ID_Pedido", idPedido);
            values.put("ID_Plato", plato.getId());
            db.insert("PedidoPlato", null, values);
        }
        return idPedido;
    }

    public Pedido obtenerPedido(int idPedido) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT Pedido.ID, " +
                        "Pedido.PrecioTotal " +
                        "FROM Pedido " +
                        "WHERE Pedido.ID = ?",
                new String[]{String.valueOf(idPedido)});

        Pedido pedido = null;
        if (cursor.moveToFirst()) {
            pedido = new Pedido();
            pedido.setId(cursor.getInt(0));
            pedido.setPrecioTotal(cursor.getDouble(1));
        }
        cursor.close();
        return pedido;
    }


    public boolean actualizarPedido(Pedido pedido, List<Plato> platosSeleccionados) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PrecioTotal", pedido.getPrecioTotal());

        // Actualizar el pedido en la tabla "Pedido"
        int filasAfectadas = db.update("Pedido", values, "ID = ?", new String[]{String.valueOf(pedido.getId())});

        // Borrar los platos del pedido en la tabla "PedidoPlato"
        db.delete("PedidoPlato", "ID_Pedido = ?", new String[]{String.valueOf(pedido.getId())});

        // Insertar los nuevos platos del pedido en la tabla "PedidoPlato"
        for (Plato plato : platosSeleccionados) {
            values = new ContentValues();
            values.put("ID_Pedido", pedido.getId());
            values.put("ID_Plato", plato.getId());
            db.insert("PedidoPlato", null, values);
        }

        return filasAfectadas > 0;
    }


    public boolean eliminarPedido(int idPedido) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Pedido", "ID = ?", new String[]{String.valueOf(idPedido)}) > 0;
    }

    public List<Plato> obtenerPlatosPedido(int idPedido) {
        List<Plato> platosPedido = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Plato.* FROM Plato INNER JOIN PedidoPlato ON Plato.ID = PedidoPlato.ID_Plato WHERE PedidoPlato.ID_Pedido = ?", new String[]{String.valueOf(idPedido)});

        if (cursor.moveToFirst()) {
            do {
                Plato plato = new Plato();
                plato.setId(cursor.getInt(0));
                plato.setNombre(cursor.getString(1));
                plato.setPrecio(cursor.getDouble(2));
                platosPedido.add(plato);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return platosPedido;
    }

}
