package com.jgpindustries.calculadorareembolsos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Evento> eventos;
    private ListView lv;

    private SQLiteController conexion;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Splash Screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conexión con la base de datos
        conexion = new SQLiteController(this, "calculadorareembolsos.bd", null, 3);
        db = conexion.getWritableDatabase();

        // Barra de aplicación
        getSupportActionBar().setTitle(R.string.title_bar_evento);

        // Recuperar eventos de la base de datos y llenar el ListView
        recuperarEventos();
        llenarLista();

        // Item click ListView - Acceder al evento
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Evento evento = (Evento) lv.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, EventoActivity.class);
                intent.putExtra("evento", evento);
                startActivityForResult(intent, 2);
            }
        });

        // Botón flotante - Crear evento
        FloatingActionButton fab = findViewById(R.id.fabAddEvento);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogoEvento = new AlertDialog.Builder(MainActivity.this);
                dialogoEvento.setTitle(R.string.dialog_nuevo_evento);
                dialogoEvento.setMessage(R.string.title_nombre_evento);
                final EditText et_nombreEvento = new EditText(MainActivity.this);
                et_nombreEvento.setHint(R.string.hint_nombre_evento);
                dialogoEvento.setView(et_nombreEvento);

                dialogoEvento.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String nombreEvento = et_nombreEvento.getText().toString();

                        if (!nombreEvento.isEmpty()) {

                            Evento newEvento = new Evento(nombreEvento);

                            // INSERT Evento
                            conexion.insertarEvento(db, newEvento.getNombre());

                            recuperarEventos();
                            llenarLista();

                        } else {
                            Toast.makeText(MainActivity.this, R.string.no_valido, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialogoEvento.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialogoEvento.show();
            }
        });

        // Item long click ListView - Eliminar evento
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final Evento evento = (Evento) lv.getItemAtPosition(position);

                AlertDialog.Builder dialogoEliminarEvento = new AlertDialog.Builder(view.getContext());
                dialogoEliminarEvento.setTitle(R.string.dialog_eliminar_evento);
                dialogoEliminarEvento.setMessage(R.string.text_eliminar_evento);

                dialogoEliminarEvento.setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // DELETE Evento
                        conexion.borrarEvento(db, evento.getId());

                        recuperarEventos();
                        llenarLista();
                    }
                });

                dialogoEliminarEvento.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialogoEliminarEvento.show();

                return true;
            }
        });
    }

    private void recuperarEventos() {

        // ArrayList de eventos
        eventos = new ArrayList<Evento>();

        // Recuperar los eventos de la base de datos
        Cursor c = conexion.getEventosBD(db);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String nombre = c.getString(1);
                eventos.add(new Evento(id, nombre));
            } while (c.moveToNext());
        }
    }

    private void llenarLista() {

        // Adaptador
        ArrayAdapter<Evento> adaptador = new ArrayAdapter<Evento>(MainActivity.this, R.layout.listview_custom, eventos);

        lv = findViewById(R.id.lvEventos);
        lv.setAdapter(adaptador);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Siempre que se vuelva a esta actividad, se actualizan los eventos
        // por si se modificó el nombre del evento.
        recuperarEventos();
        llenarLista();
    }
}
