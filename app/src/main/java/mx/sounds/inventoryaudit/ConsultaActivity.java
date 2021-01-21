package mx.sounds.inventoryaudit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.sounds.inventoryaudit.data.DatabaseSQL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultaActivity extends Activity {
	//public final static String EXTRA_ID = "mx.sounds.inventoryaudit.ID";
	//private ListView lista; 
	private String cSql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_consulta);
        
		///////////////////////////////		
		try {
			cSql = "select i.*, c.descripcion from inventoryaudit i, codigos c where i.codigo = c.codigo and (i.gondola = '"+
					MainActivity.gondola +"' or '0'= '" + MainActivity.gondola + "') order by i.id desc";
			
            ResultSet rs = DatabaseSQL.getRS(cSql);
            ArrayList<List_Consulta> datos = new ArrayList<List_Consulta>(); 
            if (rs == null) {
            	datos.add(new List_Consulta("", "Reporte Sin Datos","","",""));
            } else {
	            if ((MainActivity.lerr) || (rs.isAfterLast())) {
	            	datos.add(new List_Consulta("", "Reporte Sin Datos","","",""));
	            } else {
		            while (!(rs.isAfterLast())) {
		            	String val1 = rs.getString("codigo");
		            	String val2 = rs.getString("gondola");
		            	String val3 = String.format(Locale.getDefault(), "%5d", rs.getInt("cantidad"));
		            	String val4 = rs.getString("descripcion");
		            	String val5 = rs.getString("fecha");
		            	datos.add(new List_Consulta(val1, val2, val3, val4, val5));
		            	rs.next();
		            }
		            rs.close();
	            }
            }
            rs = null;

			ListView lista = (ListView) findViewById(R.id.ListView_listado);
	        lista.setAdapter(new List_Adapter(this, R.layout.item_consulta, datos){
				@Override
				public void onEntrada(Object item_consulta, View view) {
			            TextView texto_1 = (TextView) view.findViewById(R.id.textView_1); 
			            texto_1.setText(((List_Consulta) item_consulta).get_texto1()); 
	
			            TextView texto_2 = (TextView) view.findViewById(R.id.textView_2); 
			            texto_2.setText(((List_Consulta) item_consulta).get_texto2()); 
			            
			            TextView texto_3 = (TextView) view.findViewById(R.id.textView_3); 
			            texto_3.setText(((List_Consulta) item_consulta).get_texto3()); 	
			            
			            TextView texto_4 = (TextView) view.findViewById(R.id.textView_4); 
			            texto_4.setText(((List_Consulta) item_consulta).get_texto4()); 
			            
			            TextView texto_5 = (TextView) view.findViewById(R.id.textView_5); 
			            texto_5.setText(((List_Consulta) item_consulta).get_texto5()); 
			            
				}
			});
	        
	        lista.setOnItemClickListener(new OnItemClickListener() { 
	            @Override
	            public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
	            	List_Consulta elegido = (List_Consulta) pariente.getItemAtPosition(posicion); 
	            	
	                CharSequence texto = "Codigo Seleccionado: " + elegido.get_texto1();
	                Toast toast = Toast.makeText(ConsultaActivity.this, texto, Toast.LENGTH_LONG);
	                toast.show();
	            	/*
	        		Intent intent = new Intent(ConsultaActivity.this , CapturarActivity.class);
	        		String message = elegido.get_texto1();
	        		intent.putExtra(MainActivity.EXTRA_ID, message);
	        		startActivity(intent);
	        		ConsultaActivity.this.finish();
	        		*/
	            }
	         });
	        
			///////////////////////////////
        } catch (SQLException ex) {
            //Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.gc();                
        }
    }
    
       
    public void buttonClose_onclick(View view) {
    	this.finish();
    }    
    
}
