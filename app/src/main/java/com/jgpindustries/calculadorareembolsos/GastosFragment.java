package com.jgpindustries.calculadorareembolsos;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GastosFragment extends Fragment {

    private ArrayList<Gasto> gastos;
    private ListView lvGastos;
    private TextView tvTotal;

    private GetGastos GG;
    private GetParticipantes GP;
    private InsertGasto IG;
    private ActualizarReembolsos AR;
    private ActualizarGasto AG;
    private BorrarGasto BG;

    public GastosFragment() {
    }

    interface GetGastos {
        ArrayList<Gasto> getGastos();
    }

    interface GetParticipantes {
        ArrayList<Participante> getParticipantes();
    }

    interface InsertGasto {
        void insertGasto(Gasto g);
    }

    interface ActualizarReembolsos {
        void actualizarReembolsos();
    }

    interface ActualizarGasto {
        void actualizarGasto(int id, Gasto g);
    }

    interface BorrarGasto {
        void borrarGasto(int id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gastos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvGastos = view.findViewById(R.id.lvGastos);
        tvTotal = view.findViewById(R.id.tvTotal);

        // Recuperar gastos de la base de datos e introducirlos en el ListView
        getGastos(view);

        // Item click ListView - Editar gasto
        lvGastos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Gasto g = (Gasto) lvGastos.getItemAtPosition(position);

                Intent intent = new Intent(view.getContext(), NuevoGastoActivity.class);
                intent.putExtra("participantes", GP.getParticipantes());
                intent.putExtra("gasto", g);
                startActivityForResult(intent, 2);
            }
        });

        // Botón flotante - Crear gasto
        view.findViewById(R.id.fabAddGasto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Si el array de participantes está vacío, no se podrá crear el gasto
                ArrayList<Participante> participantes = GP.getParticipantes();
                if (!participantes.isEmpty()) {

                    Intent intent = new Intent(view.getContext(), NuevoGastoActivity.class);
                    intent.putExtra("participantes", participantes);
                    startActivityForResult(intent, 2);
                } else {
                    Toast.makeText(view.getContext(), R.string.participantes_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Item long click ListView - Eliminar gasto
        lvGastos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                final Gasto g = (Gasto) lvGastos.getItemAtPosition(position);

                AlertDialog.Builder dialogoEliminarGasto = new AlertDialog.Builder(view.getContext());
                dialogoEliminarGasto.setTitle(R.string.dialog_eliminar_gasto);
                dialogoEliminarGasto.setMessage(R.string.text_eliminar_gasto);

                dialogoEliminarGasto.setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // DELETE Gasto
                        BG.borrarGasto(g.getId());

                        // Actualizar ListView
                        getGastos(view);

                        // Actualizar reembolsos
                        AR.actualizarReembolsos();
                    }
                });

                dialogoEliminarGasto.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialogoEliminarGasto.show();

                return true;
            }
        });
    }

    private void getGastos(View view) {

        // ArrayList de gastos
        gastos = new ArrayList<Gasto>();

        // Recuperar gastos de la base de datos
        gastos = GG.getGastos();

        // Adaptador
        ArrayAdapter<Gasto> adaptador = new ArrayAdapter<Gasto>(view.getContext(), android.R.layout.simple_list_item_1, gastos);

        // Introducir los gastos en el ListView
        lvGastos.setAdapter(adaptador);

        // Calcular total gastado
        double total = 0.0;
        for (Gasto g : gastos) {
            total += g.getCoste();
        }
        tvTotal.setText("Total: " + total + "€");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {

            // Generar gasto
            Gasto gasto = new Gasto(data.getStringExtra("nombreGasto"), data.getDoubleExtra("coste", 0.00), (Participante) data.getSerializableExtra("comprador"), (ArrayList<Participante>) data.getSerializableExtra("consumidores"));

            // INSERT Gasto
            IG.insertGasto(gasto);

            // Actualizar ListView
            getGastos(this.getView());

            // Actualizar reembolsos
            AR.actualizarReembolsos();

        } else if (resultCode == 3) {

            // Gasto editado
            Gasto gastoActual = (Gasto) data.getSerializableExtra("gastoActual");

            // Generar gasto
            Gasto gasto = new Gasto(data.getStringExtra("nombreGasto"), data.getDoubleExtra("coste", 0.00), (Participante) data.getSerializableExtra("comprador"), (ArrayList<Participante>) data.getSerializableExtra("consumidores"));

            // UPDATE Gasto
            AG.actualizarGasto(gastoActual.getId(), gasto);

            // Actualizar ListView
            getGastos(this.getView());

            // Actualizar reembolsos
            AR.actualizarReembolsos();

        } else {
            Toast.makeText(this.getView().getContext(), R.string.cancelado, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            GG = (GetGastos) getActivity();
            GP = (GetParticipantes) getActivity();
            IG = (InsertGasto) getActivity();
            AR = (ActualizarReembolsos) getActivity();
            AG = (ActualizarGasto) getActivity();
            BG = (BorrarGasto) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(this.getResources().getString(R.string.error_onattach));
        }
    }
}
