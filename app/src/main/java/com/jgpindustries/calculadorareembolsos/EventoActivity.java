package com.jgpindustries.calculadorareembolsos;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class EventoActivity extends AppCompatActivity
        implements ParticipantesFragment.InsertParticipante, ParticipantesFragment.GetParticipantes, ParticipantesFragment.BorrarParticipante,
        GastosFragment.GetGastos, GastosFragment.GetParticipantes, GastosFragment.InsertGasto,
        GastosFragment.ActualizarReembolsos, GastosFragment.ActualizarGasto, GastosFragment.BorrarGasto,
        ReembolsosFragment.GetReembolsos {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Evento evento;
    private ArrayList<Participante> participantes;
    private ArrayList<Gasto> gastos;
    private ArrayList<Reembolso> reembolsos;

    private SQLiteController conexion;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        // Recibir objeto Evento a través de Intent
        evento = (Evento) getIntent().getSerializableExtra("evento");
        String nombreEvento = evento.getNombre();

        // Configurar barra de aplicación: Mostrar nombre del evento y back button
        getSupportActionBar().setTitle(nombreEvento);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Conexión con la base de datos
        conexion = new SQLiteController(this, "calculadorareembolsos.bd", null, 3);
        db = conexion.getWritableDatabase();

        // Llenar todas las listas con los datos recuperados de la base de datos
        recuperarDatos();

        // Configuración de la vista de pestañas
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Configurar back button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void recuperarDatos() {
        recuperarParticipantes();
        recuperarGastos();
    }

    private void recuperarParticipantes() {

        // ArrayList de participantes
        participantes = new ArrayList<Participante>();

        // Recuperar los participantes de la base de datos
        Cursor cParticipantes = conexion.getParticipantesBD(db, evento.getId());
        if (cParticipantes.moveToFirst()) {
            do {
                int id = cParticipantes.getInt(0);
                String nombre = cParticipantes.getString(1);
                participantes.add(new Participante(id, nombre));
            } while (cParticipantes.moveToNext());
        }
    }

    private void recuperarGastos() {

        // ArrayList de gastos
        gastos = new ArrayList<Gasto>();

        // Recuperar los gastos de la base de datos
        Cursor cGastos = conexion.getGastosBD(db, evento.getId());
        if (cGastos.moveToFirst()) {
            do {
                int id = cGastos.getInt(0);
                String nombre = cGastos.getString(1);
                double coste = cGastos.getDouble(2);
                int idComprador = cGastos.getInt(3);
                Cursor cursorParticipanteComprador = conexion.getParticipanteBD(db, idComprador);
                Participante participanteComprador = new Participante();
                if (cursorParticipanteComprador.moveToFirst()) {
                    participanteComprador.setId(cursorParticipanteComprador.getInt(0));
                    participanteComprador.setNombre(cursorParticipanteComprador.getString(1));
                }

                Cursor cursorConsumidores = conexion.getConsumidoresGastoBD(db, id);
                ArrayList<Participante> consumidores = new ArrayList<Participante>();
                if (cursorConsumidores.moveToFirst()) {
                    do {
                        int idConsumidor = cursorConsumidores.getInt(1);
                        Cursor cursorParticipanteConsumidor = conexion.getParticipanteBD(db, idConsumidor);
                        Participante participanteConsumidor = new Participante();
                        if (cursorParticipanteConsumidor.moveToFirst()) {
                            participanteConsumidor.setId(cursorParticipanteConsumidor.getInt(0));
                            participanteConsumidor.setNombre(cursorParticipanteConsumidor.getString(1));
                        }
                        consumidores.add(participanteConsumidor);
                    } while (cursorConsumidores.moveToNext());
                }

                gastos.add(new Gasto(id, nombre, coste, participanteComprador, consumidores));
            } while (cGastos.moveToNext());
        }

        // ArrayList de reembolsos
        reembolsos = new ArrayList<Reembolso>();

        // Crear reembolsos a partir de la lista de gastos
        for (Gasto gasto : gastos) {
            generarReembolso(gasto);
        }

        // Simplificar la lista de reembolsos
        unificarReembolsos();
    }

    private void generarReembolso(Gasto gasto) {

        // Crea todos los reembolsos posibles por cada gasto
        double gastoDividido = gasto.getCoste() / gasto.getConsumidores().size();
        for (Participante pConsumidor : gasto.getConsumidores()) {
            if (gasto.getComprador().getId() != pConsumidor.getId()) {
                reembolsos.add(new Reembolso(gastoDividido, gasto.getComprador(), pConsumidor));
            }
        }
    }

    private void unificarReembolsos() {

        boolean finalizar = false;
        while (!finalizar) {
            finalizar = true;
            for (int i = 0; i < reembolsos.size() && finalizar; i++) {
                for (int j = 0; j < reembolsos.size() && finalizar; j++) {

                    if (i != j) {

                        // Si el Beneficiario i y el Pagador j son iguales
                        if (reembolsos.get(i).getBeneficiario().getId() == reembolsos.get(j).getPagador().getId()) {
                            // Si el Beneficiario i y el Pagador j son iguales y el Beneficiario j y el Pagador i son iguales, es un Reembolso inverso.
                            if (reembolsos.get(j).getBeneficiario().getId() == reembolsos.get(i).getPagador().getId()) {
                                reembolsoInverso(i, j);
                                finalizar = false;
                            }
                            // Si el Beneficiario i y el Pagador j son iguales pero el Beneficiario j y el Pagador i son diferentes, es un Reembolso en cadena.
                            else {
                                reembolsosEnCadena(i, j);
                                finalizar = false;
                            }
                        }
                        // Si los beneficiarios son iguales, y los pagadores son iguales, es un Reembolso igual.
                        else if (reembolsos.get(i).getPagador().getId() == reembolsos.get(j).getPagador().getId() && reembolsos.get(i).getBeneficiario().getId() == reembolsos.get(j).getBeneficiario().getId()) {
                            reembolsos.get(i).addCantidad(reembolsos.get(j).getCantidad());
                            reembolsos.remove(j);
                            finalizar = false;
                        }
                    }
                }
            }
        }
    }

    private void reembolsoInverso(int i, int j) {

        Reembolso reembolsoA = reembolsos.get(i);
        Reembolso reembolsoB = reembolsos.get(j);
        double diferencia = reembolsoA.getCantidad() - reembolsoB.getCantidad();

        if (diferencia > 0) {
            reembolsoA.setCantidad(diferencia);
            reembolsos.remove(j);

        } else if (diferencia < 0) {
            reembolsoB.setCantidad(-diferencia);
            reembolsos.remove(i);

        } else if (diferencia == 0) {
            reembolsos.remove(i);
            reembolsos.remove(j-1);
        }
    }

    private void reembolsosEnCadena(int i, int j) {

        Reembolso reembolsoA = reembolsos.get(i);
        Reembolso reembolsoB = reembolsos.get(j);
        double diferencia = reembolsoA.getCantidad() - reembolsoB.getCantidad();

        if (diferencia > 0) {
            reembolsoA.setBeneficiario(reembolsoB.getBeneficiario());
            reembolsoA.setCantidad(reembolsoB.getCantidad());
            reembolsoB.setBeneficiario(reembolsoB.getPagador());
            reembolsoB.setPagador(reembolsoA.getPagador());
            reembolsoB.setCantidad(diferencia);

        } else if (diferencia < 0) {
            reembolsoA.setBeneficiario(reembolsoB.getBeneficiario());
            reembolsoB.setCantidad(-diferencia);

        } else if (diferencia == 0) {
            reembolsoA.setBeneficiario(reembolsoB.getBeneficiario());
            reembolsos.remove(j);
        }
    }

    @Override
    public void insertParticipante(Participante p) {
        conexion.insertarParticipante(db, p.getNombre(), evento.getId());
        recuperarParticipantes();
    }

    @Override
    public ArrayList<Participante> getParticipantes() {
        return participantes;
    }

    @Override
    public ArrayList<Gasto> getGastos() {
        return gastos;
    }

    @Override
    public void insertGasto(Gasto g) {
        conexion.insertarGasto(db, g);
        g.setId(conexion.getMaxIdGasto(db));
        conexion.insertarConsumidoresGasto(db, g);
        recuperarGastos();
    }

    @Override
    public ArrayList<Reembolso> getReembolsos() {
        return reembolsos;
    }

    @Override
    public void actualizarReembolsos() {
        String tag = "android:switcher:" + R.id.viewPager + ":" + 2;
        ReembolsosFragment f = (ReembolsosFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.actualizarReembolsos();
    }

    @Override
    public void actualizarGasto(int id, Gasto g) {
        conexion.actualizarGasto(db, id, g);
        conexion.actualizarConsumidoresGasto(db, id, g);
        recuperarGastos();
    }

    @Override
    public void borrarGasto(int id) {
        conexion.borrarGasto(db, id);
        recuperarGastos();
    }

    @Override
    public void borrarParticipante(int id) {
        if (!conexion.comprobarBorrarParticipante(db, id)) {
            conexion.borrarParticipante(db, id);
            recuperarParticipantes();
        } else {
            Toast.makeText(this, R.string.participante_usado, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_evento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.renombrarEvento) {

            AlertDialog.Builder dialogoRenombrarEvento = new AlertDialog.Builder(EventoActivity.this);
            dialogoRenombrarEvento.setTitle(R.string.dialog_renombrar_evento);
            dialogoRenombrarEvento.setMessage(R.string.title_nombre_evento);
            final EditText et_nombreEvento = new EditText(EventoActivity.this);
            et_nombreEvento.setHint(R.string.hint_nombre_evento);
            dialogoRenombrarEvento.setView(et_nombreEvento);

            dialogoRenombrarEvento.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String nombreEvento = et_nombreEvento.getText().toString();

                    if (!nombreEvento.isEmpty()) {

                        // UPDATE Evento
                        conexion.actualizarEvento(db, evento.getId(), nombreEvento);

                        getSupportActionBar().setTitle(nombreEvento);

                    } else {
                        Toast.makeText(EventoActivity.this, R.string.no_valido, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialogoRenombrarEvento.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialogoRenombrarEvento.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
