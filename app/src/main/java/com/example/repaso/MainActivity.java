package com.example.repaso;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText id, nombre, cargo;
    ListView listaTrabajador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = findViewById(R.id.ptID);
        nombre = findViewById(R.id.ptNombre);
        cargo = findViewById(R.id.ptCargo);
        listaTrabajador = findViewById(R.id.LVtrabajador);
        MostrarTrabajador();
    }

    public void AgregarTrabajador(View view) {
        //Conexión a la base de datos DBtrabajador
        SQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BDtrabajador", null, 1);
        //Habilitamos la base de datos para lectura y escritura
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        //Pasamos los datos ingresados en el PlainText a una cadena de texto (String)
        String aID = id.getText().toString();
        String aNombre = nombre.getText().toString();
        String aCargo = cargo.getText().toString();
        //Validar que no haya campos vacios
        if (aID.isEmpty() || aNombre.isEmpty() || aCargo.isEmpty()) {
            Toast.makeText(this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show();
        } else {
            //Creamos una bolsa para guardar los datos
            ContentValues DatosUsuarios = new ContentValues();
            //Insertamos los datos de los PlainText en los campos correspondientes de la tabla
            DatosUsuarios.put("ID_Usuario", aID);
            DatosUsuarios.put("NombreTrabajador", aNombre);
            DatosUsuarios.put("CargoTrabajador", aCargo);
            BaseDeDatos.insert("Trabajadores", null, DatosUsuarios);
            BaseDeDatos.close();
            //Limpiamos los campos de texto
            id.setText("");
            nombre.setText("");
            cargo.setText("");
            //Llamamos a la variable MostrarTrabajador para que se vea en el ListView
            MostrarTrabajador();

        }
    }

    public void MostrarTrabajador() {
        SQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BDtrabajador", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        //Los datos de la consulta SQL se guardan en Cursor para recorrer fila por fila
        Cursor fila = BaseDeDatos.rawQuery("Select * from Trabajadores", null);
        //Creamos un array para guardar los datos de usuarios en tipo String
        ArrayList<String> lArray = new ArrayList<>();
        //El cursor se dirije al primer dato
        if (fila.moveToFirst()) {
            do {
                String mID = fila.getString(0);
                String mNombre = fila.getString(1);
                String mCargo = fila.getString(2);
                String UserInfo = "Id: "+mID+ ", Nombre: "+mNombre+ ", Cargo: "+mCargo;
                //Añade a la lista los elementos de cada fila
                lArray.add(UserInfo);
                //Bucle que va a extraer los datos fila por fila
            } while (fila.moveToNext());
        }
        BaseDeDatos.close();
        //Se crea un adapter para hacer conectarlo con nuestro Array y visualizarse en un ListView (listaTrabajador)
        ArrayAdapter<String> lAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lArray);
        listaTrabajador.setAdapter(lAdapter);
    }

    public void ActualizarTrabajador(View view) {
        SQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BDtrabajador", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String uID = id.getText().toString();
        String uNombre = nombre.getText().toString();
        String uCargo = cargo.getText().toString();
        //Si el campo tiene texto (falso), entonces será true (El campo no está vacío)
        if (!uNombre.isEmpty() && !uCargo.isEmpty()) {
            //Creamos una bolsa para los datos actualizados
            ContentValues DatosUsuarios = new ContentValues();
            DatosUsuarios.put("NombreTrabajador", uNombre);
            DatosUsuarios.put("CargoTrabajador", uCargo);
            //Hacemos la consulta update para actualizar los datos de nombre y cargo (La ID no se cambia, es unica)
            int cantidad = BaseDeDatos.update("Trabajadores", DatosUsuarios, "ID_Usuario=" +uID, null);
            //Si cantidad es igual a un dato existente, la operación se realizó
            if (cantidad == 1) {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                id.setText("");
                nombre.setText("");
                cargo.setText("");
                MostrarTrabajador();
            }else {
                Toast.makeText(this, "No existe la ID ingresada", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Debes rellenar los campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void BuscarTrabajador(View view) {
        SQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BDtrabajador", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String bID = id.getText().toString();
        if(!bID.isEmpty()) {
            Cursor fila = BaseDeDatos.rawQuery("SELECT NombreTrabajador, CargoTrabajador FROM Trabajadores Where ID_Usuario=" +bID, null);
            if (fila.moveToFirst()){
                nombre.setText(fila.getString(0));
                cargo.setText(fila.getString(1));
                BaseDeDatos.close();
            }else {
                Toast.makeText(this, "El id "+bID+" ingresado no existe", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show();
        }
    }

    public void EliminarTrabajador(View view) {
        SQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BDtrabajador", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String eID = id.getText().toString();
        if (!eID.isEmpty()) {
           int eliminar  = BaseDeDatos.delete("Trabajadores", "ID_Usuario="+ eID, null);
           if (eliminar == 1){
               Toast.makeText(this, "Se elimino exitosamente", Toast.LENGTH_SHORT).show();
               id.setText("");
               nombre.setText("");
               cargo.setText("");
               MostrarTrabajador();
           } else {
               Toast.makeText(this, "La ID ingresada no existe", Toast.LENGTH_SHORT).show();
           }
        } else {
            Toast.makeText(this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show();
        }
    }
}