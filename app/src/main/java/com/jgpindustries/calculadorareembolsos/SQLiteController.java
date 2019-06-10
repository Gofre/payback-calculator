package com.jgpindustries.calculadorareembolsos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteController extends SQLiteOpenHelper {

    public SQLiteController(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE evento" + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL);");

        db.execSQL("CREATE TABLE participante" + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "idEvento INTEGER NOT NULL, "
                + "FOREIGN KEY (idEvento) REFERENCES evento(id));");

        db.execSQL("CREATE TABLE gasto" + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "coste DOUBLE NOT NULL, "
                + "idParticipanteComprador INTEGER NOT NULL, "
                + "FOREIGN KEY (idParticipanteComprador) REFERENCES participante(id));");

        db.execSQL("CREATE TABLE participantesConsumidoresGasto" + " ("
                + "idGasto INTEGER, "
                + "idParticipanteConsumidor INTEGER, "
                + "PRIMARY KEY (idGasto, idParticipanteConsumidor) , "
                + "FOREIGN KEY (idGasto) REFERENCES gasto(id), "
                + "FOREIGN KEY (idParticipanteConsumidor) REFERENCES participante(id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertarEvento(SQLiteDatabase db, String nombreEvento) {

        String[] args = new String[] {nombreEvento};

        db.execSQL("INSERT INTO evento VALUES(null, ?)", args);
    }

    public Cursor getEventosBD(SQLiteDatabase db) {

        Cursor c = db.rawQuery("SELECT * FROM evento", null);
        return c;
    }

    public void borrarEvento(SQLiteDatabase db, int idEvento) {

        String[] args = new String[] {String.valueOf(idEvento)};

        Cursor c = db.rawQuery("SELECT id FROM participante WHERE idEvento = ?", args);
        if (c.moveToFirst()) {
            do {
                int idParticipante = c.getInt(0);

                String[] argsGasto = new String[] {String.valueOf(idParticipante)};
                db.execSQL("DELETE FROM participantesConsumidoresGasto WHERE idParticipanteConsumidor = ?", argsGasto);
                db.execSQL("DELETE FROM gasto WHERE idParticipanteComprador = ?", argsGasto);

            } while (c.moveToNext());
        }

        db.execSQL("DELETE FROM participante WHERE idEvento = ?", args);
        db.execSQL("DELETE FROM evento WHERE id = ?", args);
    }

    public void actualizarEvento(SQLiteDatabase db, int idEvento, String nombreEvento) {

        String[] args = new String[] {nombreEvento, String.valueOf(idEvento)};

        db.execSQL("UPDATE evento SET nombre = ? WHERE id = ?", args);
    }

    public void insertarParticipante(SQLiteDatabase db, String nombreParticipante, int idEvento) {

        String[] args = new String[] {nombreParticipante, String.valueOf(idEvento)};

        db.execSQL("INSERT INTO participante VALUES(null, ?, ?)", args);
    }

    public Cursor getParticipantesBD(SQLiteDatabase db, int idEvento) {

        String[] args = new String[] {String.valueOf(idEvento)};

        Cursor c = db.rawQuery("SELECT * FROM participante WHERE idEvento = ?", args);
        return c;
    }

    public Cursor getParticipanteBD(SQLiteDatabase db, int id) {

        String[] args = new String[] {String.valueOf(id)};

        Cursor c = db.rawQuery("SELECT * FROM participante WHERE id = ?", args);
        return c;
    }

    public boolean comprobarBorrarParticipante(SQLiteDatabase db, int id) {

        String[] args = new String[] {String.valueOf(id)};

        boolean participanteUsado = false;

        Cursor c1 = db.rawQuery("SELECT * FROM participantesConsumidoresGasto WHERE idParticipanteConsumidor = ?", args);
        Cursor c2 = db.rawQuery("SELECT * FROM gasto WHERE idParticipanteComprador = ?", args);
        if (c1.moveToFirst()) {
            participanteUsado = true;
        }
        if (c2.moveToFirst()) {
            participanteUsado = true;
        }

        return participanteUsado;
    }

    public void borrarParticipante(SQLiteDatabase db, int id) {

        String[] args = new String[] {String.valueOf(id)};

        db.execSQL("DELETE FROM participante WHERE id = ?", args);
    }

    public void insertarGasto(SQLiteDatabase db, Gasto gasto) {

        String[] args = new String[] {gasto.getNombre(), String.valueOf(gasto.getCoste()), String.valueOf(gasto.getComprador().getId())};

        db.execSQL("INSERT INTO gasto VALUES(null, ?, ?, ?)", args);
    }

    public Cursor getGastosBD(SQLiteDatabase db, int idEvento) {

        String[] args = new String[] {String.valueOf(idEvento)};

        Cursor c = db.rawQuery("SELECT * FROM gasto g INNER JOIN participante p ON g.idParticipanteComprador = p.id INNER JOIN evento e ON p.idEvento = e.id WHERE e.id = ?", args);
        return c;
    }

    public int getMaxIdGasto(SQLiteDatabase db) {

        Cursor c = db.rawQuery("SELECT id FROM gasto ORDER BY id DESC LIMIT 1", null);
        if (c != null && c.moveToFirst()) {
            return c.getInt(0);
        }
        return 1;
    }

    public void insertarConsumidoresGasto(SQLiteDatabase db, Gasto gasto) {

        for (int i=0; i<gasto.getConsumidores().size(); i++) {

            String[] args = new String[] {String.valueOf(gasto.getId()), String.valueOf(gasto.getConsumidores().get(i).getId())};

            db.execSQL("INSERT INTO participantesConsumidoresGasto VALUES(?, ?)", args);
        }
    }

    public Cursor getConsumidoresGastoBD(SQLiteDatabase db, int idGasto) {

        String[] args = new String[] {String.valueOf(idGasto)};

        Cursor c = db.rawQuery("SELECT * FROM participantesConsumidoresGasto WHERE idGasto = ?", args);
        return c;
    }

    public void actualizarGasto(SQLiteDatabase db, int idGasto, Gasto gasto) {

        String[] args = new String[] {gasto.getNombre(), String.valueOf(gasto.getCoste()), String.valueOf(gasto.getComprador().getId()), String.valueOf(idGasto)};

        db.execSQL("UPDATE gasto SET nombre = ?, coste = ?, idParticipanteComprador = ? WHERE id = ?", args);
    }

    public void actualizarConsumidoresGasto(SQLiteDatabase db, int idGasto, Gasto gasto) {

        String[] argsDelete = new String[] {String.valueOf(idGasto)};
        db.execSQL("DELETE FROM participantesConsumidoresGasto WHERE idGasto = ?", argsDelete);

        for (int i=0; i<gasto.getConsumidores().size(); i++) {

            String[] argsInsert = new String[] {String.valueOf(idGasto), String.valueOf(gasto.getConsumidores().get(i).getId())};

            db.execSQL("INSERT INTO participantesConsumidoresGasto VALUES(?, ?)", argsInsert);
        }
    }

    public void borrarGasto(SQLiteDatabase db, int idGasto) {

        String[] args = new String[] {String.valueOf(idGasto)};
        db.execSQL("DELETE FROM gasto WHERE id = ?", args);
        db.execSQL("DELETE FROM participantesConsumidoresGasto WHERE idGasto = ?", args);
    }
}
