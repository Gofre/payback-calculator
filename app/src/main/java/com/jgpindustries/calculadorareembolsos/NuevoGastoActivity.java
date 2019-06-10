package com.jgpindustries.calculadorareembolsos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class NuevoGastoActivity extends AppCompatActivity {

    private boolean modoEdicion = false;
    private Gasto gastoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_gasto);

        // Configurar barra de aplicación: Mostrar título y back button
        getSupportActionBar().setTitle("Crear Gasto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // ArrayList de participantes
        ArrayList<Participante> participantes = (ArrayList<Participante>) getIntent().getSerializableExtra("participantes");

        // Adaptador
        final ArrayAdapter<Participante> adaptador = new ArrayAdapter<Participante>(NuevoGastoActivity.this, android.R.layout.simple_list_item_1, participantes);

        // Introducir los participantes en el Spinner
        final Spinner spPagador = findViewById(R.id.sp_comprador);
        spPagador.setAdapter(adaptador);

        // Adaptador - CheckBox List de consumidores
        ArrayAdapter<Participante> adaptadorConsumidores = new ArrayAdapter<Participante>(NuevoGastoActivity.this, android.R.layout.simple_list_item_multiple_choice, participantes);

        // Configurar ListView e introducir los participantes
        final ListView lv = findViewById(R.id.lv_consumidores);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setItemsCanFocus(false);
        lv.setAdapter(adaptadorConsumidores);

        // Recuperar el gasto a editar
        gastoActual = (Gasto) getIntent().getSerializableExtra("gasto");
        if (gastoActual != null) {
            modoEdicion = true;
            llenarComponentes();
        }

        // Cambiar título
        if (modoEdicion) {
            getSupportActionBar().setTitle("Editar Gasto");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_nuevo_gasto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Tick Button - Confirmar creación de gasto
        if (item.getItemId() == R.id.tick_button) {

            String nombre = ((EditText)findViewById(R.id.et_nombre)).getText().toString();
            String coste = ((EditText)findViewById(R.id.et_precio)).getText().toString();

            // Obtener consumidores seleccionados
            ListView lv = findViewById(R.id.lv_consumidores);
            ArrayList<Participante> consumidoresSeleccionados = new ArrayList<Participante>();
            SparseBooleanArray sparseBooleanArray = lv.getCheckedItemPositions(); // Consumidores seleccionados

            // Validar datos
            boolean valido = true;
            if (nombre == null || nombre.isEmpty()) {
                valido = false;
            }
            if (coste == null || coste.isEmpty() || Double.parseDouble(coste) <= 0) {
                valido = false;
            }
            if (sparseBooleanArray.size() <= 0) {
                valido = false;
            }

            int listaLength = lv.getCount(); // Total de items

            if (valido) {

                for (int i=0; i<listaLength; i++) {

                    if (sparseBooleanArray.get(i)) {

                        consumidoresSeleccionados.add((Participante) lv.getItemAtPosition(i));
                    }
                }

                // Intent
                Intent intent = new Intent();
                intent.putExtra("nombreGasto", nombre);
                intent.putExtra("coste", Double.parseDouble(coste));
                intent.putExtra("comprador", (Participante)((Spinner)findViewById(R.id.sp_comprador)).getSelectedItem());
                intent.putExtra("consumidores", consumidoresSeleccionados);

                // ModoEdicion
                if (!modoEdicion) {
                    setResult(2, intent);
                } else {
                    intent.putExtra("gastoActual", gastoActual);
                    setResult(3, intent);
                }

                finish();

            } else {
                Toast.makeText(this, "Los datos no son correctos", Toast.LENGTH_SHORT).show();
            }

        } else { // Back button
            finish();
        }
        return true;
    }

    private void llenarComponentes() {

        ((EditText)findViewById(R.id.et_nombre)).setText(gastoActual.getNombre());
        ((EditText)findViewById(R.id.et_precio)).setText(String.valueOf(gastoActual.getCoste()));

        Spinner spComprador = findViewById(R.id.sp_comprador);
        for (int i=0; i<spComprador.getCount(); i++) {
            if (gastoActual.getComprador().getId() == ((Participante)spComprador.getItemAtPosition(i)).getId()) {
                spComprador.setSelection(i);
            }
        }

        ListView lvConsumidores = findViewById(R.id.lv_consumidores);
        for (int i=0; i<gastoActual.getConsumidores().size(); i++) {
            for (int j=0; j<lvConsumidores.getCount(); j++) {
                if (gastoActual.getConsumidores().get(i).getId() == ((Participante)lvConsumidores.getItemAtPosition(j)).getId()) {
                    lvConsumidores.setItemChecked(j, true);
                }
            }
        }
    }
}
