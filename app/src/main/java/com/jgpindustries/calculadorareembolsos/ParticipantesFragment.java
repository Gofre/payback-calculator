package com.jgpindustries.calculadorareembolsos;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParticipantesFragment extends Fragment {

    private ArrayList<Participante> participantes;
    private ListView lvParticipantes;

    private InsertParticipante IP;
    private GetParticipantes GP;
    private BorrarParticipante BP;

    public ParticipantesFragment() {
    }

    interface InsertParticipante {
        void insertParticipante(Participante p);
    }

    interface GetParticipantes {
        ArrayList<Participante> getParticipantes();
    }

    interface BorrarParticipante {
        void borrarParticipante(int id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_participantes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvParticipantes = view.findViewById(R.id.lvParticipantes);

        // Recuperar participantes de la base de datos e introducirlos en el ListView
        getParticipantes(view);

        // Botón flotante - Añadir participante
        view.findViewById(R.id.fabAddParticipante).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogoParticipante = new AlertDialog.Builder(view.getContext());
                dialogoParticipante.setTitle(R.string.dialog_nuevo_participante);
                dialogoParticipante.setMessage(R.string.title_nombre_participante);
                final EditText et_nombreParticipante = new EditText(view.getContext());
                dialogoParticipante.setView(et_nombreParticipante);

                dialogoParticipante.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String nombreParticipante = et_nombreParticipante.getText().toString();

                        if (!nombreParticipante.isEmpty()) {

                            Participante newParticipante = new Participante(nombreParticipante);

                            // INSERT Participante
                            IP.insertParticipante(newParticipante);

                            // Actualizar ListView
                            getParticipantes(view);
                        } else {
                            Toast.makeText(view.getContext(), R.string.no_valido, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialogoParticipante.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialogoParticipante.show();
            }
        });

        // Item long click ListView - Eliminar participante
        lvParticipantes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                final Participante p = (Participante) lvParticipantes.getItemAtPosition(position);

                final AlertDialog.Builder dialogoParticipante = new AlertDialog.Builder(view.getContext());
                dialogoParticipante.setTitle(R.string.dialog_eliminar_participante);
                dialogoParticipante.setMessage(R.string.text_eliminar_participante);

                dialogoParticipante.setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // DELETE Participante
                        BP.borrarParticipante(p.getId());

                        // Actualizar ListView
                        getParticipantes(view);
                    }
                });

                dialogoParticipante.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialogoParticipante.show();

                return true;
            }
        });
    }

    private void getParticipantes(View view) {

        // ArrayList de participantes
        participantes = new ArrayList<Participante>();

        // Recuperar participantes de la Base de Datos
        participantes = GP.getParticipantes();

        // Adaptador
        ArrayAdapter<Participante> adaptador = new ArrayAdapter<Participante>(view.getContext(), android.R.layout.simple_list_item_1, participantes);

        // Introducir los participantes en el ListView
        lvParticipantes.setAdapter(adaptador);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            IP = (InsertParticipante) getActivity();
            GP = (GetParticipantes) getActivity();
            BP = (BorrarParticipante) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(this.getResources().getString(R.string.error_onattach));
        }
    }
}
