package mx.sounds.inventoryaudit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.sounds.inventoryaudit.data.DatabasePostgres;
import mx.sounds.inventoryaudit.util.SFTP;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class SubirFotosActivity extends ActionBarActivity {
	public static String cSql;
	String cfile="";
	int lCapFoto=0;
	int nReg=0;
	int nMax=100;
	private Handler mHandler = new Handler();
	static ProgressBar mProgress;
	ResultSet rs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subir_fotos);
		try {
			cSql = "select count(*) as reg from inventoryaudit where not subida and length(trim(foto)) > 0 ";
			rs = DatabasePostgres.getRS(cSql);
			if (rs == null) {
            	return;
            } else {
	            if ((MainActivity.lerr) || (rs.isAfterLast())) {
	            	return;
	            } else {
	            	nMax = rs.getInt("reg");
	            	nReg=0;
	            	if (nMax>0) {
		            	mProgress = (ProgressBar) findViewById(R.id.progressBar1);
		            	mProgress.setMax(nMax);
		            	rs.close();
		            	
		            	new Thread(new Runnable() {
		                    public void run() {
		                        //while (nReg < nMax) {
	                        	//nReg = doWork();
	    		            	cSql = "select * from inventoryaudit where not subida and length(trim(foto)) > 0 order by id ";
	    		    			rs = DatabasePostgres.getRS(cSql);
	    		    			
	    		    			try {
		    			            while (!(rs.isAfterLast())) {
	    			            		lCapFoto=0;
	    								cfile = rs.getString("foto").trim();
	    								lCapFoto = SFTP.UploadSFTP(MainActivity.img[0], MainActivity.img[1], "sounds.mx", 7822, cfile, "/var/www/web2/sn_fotos/", true);
	    								nReg++;
	    								//mProgress.setProgress(nReg);
			                            // Update the progress bar
			                            mHandler.post(new Runnable() {
			                                public void run() {
			                                    mProgress.setProgress(nReg);
			                                }
			                            });		    								
		    			            	if (lCapFoto>0) {
		    			            		cSql = "update inventoryaudit set subida = true, fecha_subida = now() where id = " + rs.getInt("id");
		    			            		mx.sounds.inventoryaudit.data.DatabasePostgres.execQuery(cSql);
		    			            	}			            	
		    			            	rs.next();
		    			            }
		    			            rs.close();
		    			            SubirFotosActivity.this.finish();
	    		    			} catch (Exception ex) {
    					            System.out.println("Problema para accesar: "+cfile);
    					        }
		                        //}
		                    }
		                }).start();
		            	
	            	} else {
	            		SubirFotosActivity.this.finish();
	            	}
	            }
            }
            rs = null;

		} catch (SQLException ex) {
            Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.gc();  
        }		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.subir_fotos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
