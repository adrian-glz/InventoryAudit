package mx.sounds.inventoryaudit;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.sounds.inventoryaudit.data.DatabaseSQL;

public class BuscarCodigoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_codigo);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        try {
            String cSql = "select * from codigos where codigo like '%" + query + "%' or " +
                    " codigoprov like '%" + query + "%' or " +
                    " codigo2 like '%" + query + "%' or " +
                    " descripcion like '%" + query + "%'  order by descripcion";
            ResultSet rs = DatabaseSQL.getRS(cSql);
            ArrayList<List_Buscar> datos = new ArrayList<List_Buscar>();
            if (rs == null) {
                datos.add(new List_Buscar("Reporte Sin Datos","","","",""));
            } else {
                if ((MainActivity.lerr) || (rs.isAfterLast())) {
                    datos.add(new List_Buscar("Reporte Sin Datos","","","",""));
                } else {
                    while (!(rs.isAfterLast())) {
                        String val1 = rs.getString("codigo");
                        String val2 = rs.getString("codigoprov");
                        String val3 = rs.getString("codigo2");
                        String val4 = rs.getString("descripcion");
                        String val5 = String.format(Locale.getDefault(), "%,12.2f%n", rs.getDouble("precioventa"));
                        datos.add(new List_Buscar(val1, val2, val3, val4, val5));
                        rs.next();
                    }
                    rs.close();
                }
            }
            rs = null;

            ListView lista = (ListView) findViewById(R.id.ListView_Busqueda);
            lista.setAdapter(new List_Adapter(this, R.layout.item_buscar_codigo, datos){
                @Override
                public void onEntrada(Object item_buscar_codigo, View view) {
                    TextView texto_1 = (TextView) view.findViewById(R.id.textView_1);
                    texto_1.setText(((List_Buscar) item_buscar_codigo).get_texto_codigo());

                    TextView texto_2 = (TextView) view.findViewById(R.id.textView_2);
                    texto_2.setText(((List_Buscar) item_buscar_codigo).get_texto_codigoprov());

                    TextView texto_3 = (TextView) view.findViewById(R.id.textView_3);
                    texto_3.setText(((List_Buscar) item_buscar_codigo).get_texto_codigo2());

                    TextView texto_4 = (TextView) view.findViewById(R.id.textView_4);
                    texto_4.setText(((List_Buscar) item_buscar_codigo).get_texto_descripcion());

                    TextView texto_5 = (TextView) view.findViewById(R.id.textView_5);
                    texto_5.setText(((List_Buscar) item_buscar_codigo).get_texto_precio());

                }
            });

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                    List_Buscar elegido = (List_Buscar) pariente.getItemAtPosition(posicion);

                    CharSequence texto = "Codigo Seleccionado: " + elegido.get_texto_codigo();
                    Toast toast = Toast.makeText(BuscarCodigoActivity.this, texto, Toast.LENGTH_LONG);
                    toast.show();

                    Intent intent = new Intent(BuscarCodigoActivity.this , CapturarActivity.class);
                    String message = elegido.get_texto_codigo();
                    intent.putExtra("codigo_search", message);
                    intent.putExtra("gondola_search", MainActivity.gondola_search);
                    //MainActivity.codigo_search = message;
                    startActivity(intent);
                    BuscarCodigoActivity.this.finish();


                }
            });

            ///////////////////////////////
        } catch (Exception ex) {
            msbox("Error!", "Error al procesar busqueda! " + ex.getMessage());
        } finally{
            System.gc();
        }

    }

    public void msbox(String str, String str2) {
        final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(str);
        dlgAlert.setMessage(str2);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //finish();
                dialog.cancel();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }


    public void buttonClose_onclick(View view) {
        this.finish();
    }
}
