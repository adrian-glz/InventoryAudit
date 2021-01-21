package mx.sounds.inventoryaudit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.sounds.inventoryaudit.data.DatabasePostgres;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultaTotalesActivity extends Activity {
	private String cSql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_consultatotales);
		try {
			cSql = "select factura, marca, modelo, color, count(*) as cantidad " +
			" from inventoryaudit " +
			" where fecha >= current_date-7 " +
			" group by factura, marca, modelo, color " +
			" order by factura, marca, modelo, color";
			
            ResultSet rs = DatabasePostgres.getRS(cSql);
            ArrayList<List_ConsultaTotales> datos = new ArrayList<List_ConsultaTotales>(); 
            if (rs == null) {
            	datos.add(new List_ConsultaTotales("", "Reporte Sin Datos","","",""));
            } else {
	            if ((MainActivity.lerr) || (rs.isAfterLast())) {
	            	datos.add(new List_ConsultaTotales("", "Reporte Sin Datos","","",""));
	            } else {
		            while (!(rs.isAfterLast())) {
		            	String val1 = rs.getString("factura");
		            	String val2 = rs.getString("marca");
		            	String val3 = rs.getString("modelo");
		            	String val4 = rs.getString("color");
		            	String val5 = String.format(Locale.getDefault(), "%,d", rs.getInt("cantidad"));
		            	datos.add(new List_ConsultaTotales(val1, val2, val3, val4, val5));
		            	rs.next();
		            }
		            rs.close();
	            }
            }
            rs = null;

			ListView lista = (ListView) findViewById(R.id.ListView_listado);
	        lista.setAdapter(new List_Adapter(this, R.layout.item_consultatotales, datos){
				@Override
				public void onEntrada(Object item_consultatotales, View view) {
			            TextView texto_1 = (TextView) view.findViewById(R.id.textView_1); 
			            texto_1.setText(((List_ConsultaTotales) item_consultatotales).get_texto1()); 
	
			            TextView texto_2 = (TextView) view.findViewById(R.id.textView_2); 
			            texto_2.setText(((List_ConsultaTotales) item_consultatotales).get_texto2()); 
			            
			            TextView texto_3 = (TextView) view.findViewById(R.id.textView_3); 
			            texto_3.setText(((List_ConsultaTotales) item_consultatotales).get_texto3()); 	
			            
			            TextView texto_4 = (TextView) view.findViewById(R.id.textView_4); 
			            texto_4.setText(((List_ConsultaTotales) item_consultatotales).get_texto4()); 
			            
			            TextView texto_5 = (TextView) view.findViewById(R.id.textView_5); 
			            texto_5.setText(((List_ConsultaTotales) item_consultatotales).get_texto5()); 
				}
			});
	        
	        lista.setOnItemClickListener(new OnItemClickListener() { 
	            @Override
	            public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
	            	List_ConsultaTotales elegido = (List_ConsultaTotales) pariente.getItemAtPosition(posicion); 
	            	
	                CharSequence texto = "Registros Capturados: " + elegido.get_texto5();
	                Toast toast = Toast.makeText(ConsultaTotalesActivity.this, texto, Toast.LENGTH_LONG);
	                toast.show();
	            }
	         });
	        
			///////////////////////////////
        } catch (SQLException ex) {
            Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.gc();                
        }
    }
    
       
    public void buttonClose_onclick(View view) {
    	this.finish();
    }    
    
}
