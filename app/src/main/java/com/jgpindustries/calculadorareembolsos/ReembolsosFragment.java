package com.jgpindustries.calculadorareembolsos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReembolsosFragment extends Fragment {

    private ArrayList<Reembolso> reembolsos;
    private ListView lvReembolsos;

    private GetReembolsos GR;

    public ReembolsosFragment() {
    }

    interface GetReembolsos {
        ArrayList<Reembolso> getReembolsos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reembolsos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvReembolsos = view.findViewById(R.id.lvReembolsos);

        // Recuperar reembolsos de la base de datos e introducirlos en el ListView
        getReembolsos(view);
    }

    private void getReembolsos(View view) {

        // ArrayList de reembolsos
        reembolsos = new ArrayList<Reembolso>();

        // Recuperar reembolsos de la Base de Datos
        reembolsos = GR.getReembolsos();

        // Adaptador
        ArrayAdapter<Reembolso> adaptador = new ArrayAdapter<Reembolso>(view.getContext(), android.R.layout.simple_list_item_1, reembolsos);

        // Introducir los gastos en el ListView
        lvReembolsos.setAdapter(adaptador);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            GR = (GetReembolsos) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(this.getResources().getString(R.string.error_onattach));
        }
    }

    protected void actualizarReembolsos() {
        getReembolsos(this.getView());
    }
}
