package mx.sounds.inventoryaudit;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.sounds.inventoryaudit.data.DatabasePostgres;
import mx.sounds.inventoryaudit.data.DatabaseSQL;
import mx.sounds.inventoryaudit.util.SFTP;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

public class CapturarActivity extends ActionBarActivity {
    public static String cSql;
    public static String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    public String EXTRA_ID="-1";
    public String codigo_search="-1";
    public String gondola_search="-1";
    public int nDelReg=0;
    public int nQuery=0;
    double nTC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturar);
/////////   event enter direct in txtgondola
        EditText tg = (EditText) findViewById(R.id.txtgondola);
        tg.clearFocus();
        tg.requestFocus();


        ///***** gondola cantidad
        ((EditText)findViewById(R.id.txtcantidad)).setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            { if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            { if (!event.isShiftPressed()) {

                Button btn3;
                btn3 = (Button)findViewById(R.id.btngrabar);
                btn3.performClick();

                return true; // consume.
      }

            } return false; // pass on to other listeners.

            } });
        ///******

        ///***** gondola enter
        ((EditText)findViewById(R.id.txtgondola)).setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            { if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            { if (!event.isShiftPressed()) {
           return true; // consume.
            }
                EditText txtcod1 = (EditText) findViewById(R.id.txtcodigo);
                txtcod1.clearFocus();
                txtcod1.requestFocus();
            } return false; // pass on to other listeners.

            } });
        ///******

        ////// event with enter in edittext txtcodigo pass
        ((EditText)findViewById(R.id.txtcodigo)).setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            { if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            { if (!event.isShiftPressed()) {

                  Button btn;
                btn = (Button)findViewById(R.id.btnSig);
                btn.performClick();

            return true; // consume.
            }

            } return false; // pass on to other listeners.
            } });

        //////
  /////
  /////

        EXTRA_ID="-1";
        codigo_search = "-1";
        gondola_search = "-1";
        Intent intent = getIntent();
        if (intent.getStringExtra("EXTRA_ID") != null) {
            EXTRA_ID = intent.getStringExtra("EXTRA_ID");
        }
        if (intent.getStringExtra("codigo_search") != null) {
            codigo_search = intent.getStringExtra("codigo_search");
        }
        if (intent.getStringExtra("gondola_search") != null) {
            gondola_search = intent.getStringExtra("gondola_search");
        }

        try {
            cSql = "select top 1 * from infor ";
            ResultSet rs = DatabaseSQL.getRS(cSql);
            if (!(rs == null)) {
                if (!((MainActivity.lerr) || (rs.isAfterLast()))) {
                    if (!(rs.isAfterLast())) {
                        nTC = rs.getDouble("tipocambioventa");
                    }
                    rs.close();
                }
            }
            rs = null;
        } catch (SQLException ex) {
            Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.gc();
        }

        if (!EXTRA_ID.equals("-1")) { //Editar uno existente
            try {
                cSql = "select * from inventoryaudit where id = " + EXTRA_ID;
                ResultSet rs = DatabaseSQL.getRS(cSql);
                if (rs == null) {
                    return;
                } else {
                    if ((MainActivity.lerr) || (rs.isAfterLast())) {
                        return;
                    } else {
                        if (!(rs.isAfterLast())) {
                            String val1 = rs.getString("codigo");
                            EditText txtcodigo = (EditText) findViewById(R.id.txtcodigo);
                            txtcodigo.setText(val1);
                            String val2 = rs.getString("gondola");
                            EditText txtgondola = (EditText) findViewById(R.id.txtgondola);
                            txtgondola.setText(val2);
                            String val3 = rs.getString("cantidad");
                            EditText txtcantidad = (EditText) findViewById(R.id.txtcantidad);
                            txtcantidad.setText(val3);
                        }
                        rs.close();
                    }
                }
                rs = null;

            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                System.gc();
            }
        }
        if ((gondola_search.trim().length() > 0) && (!gondola_search.equals("-1"))) {
            EditText txtgondola = (EditText) findViewById(R.id.txtgondola);
            txtgondola.setText(gondola_search);
        }
        if ((codigo_search.trim().length() > 0) && (!codigo_search.equals("-1"))) {
            EditText txtcodigo = (EditText) findViewById(R.id.txtcodigo);
            txtcodigo.setText(codigo_search);
            Button btn;
            btn = (Button)findViewById(R.id.btnSig);
            btn.performClick();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.capturar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sndCodigoSiguiente(View view) {
        int nReg=0;
        String ccod="";
        String cdes="";
        int ngpo=0;
        double nprecio=0;
        TextView lblcodigo = (TextView) findViewById(R.id.lblcodigo);
        TextView lbldescripcion = (TextView) findViewById(R.id.lbldescripcion);
        TextView lblgrupo = (TextView) findViewById(R.id.lblgrupo);
        TextView lblprecio = (TextView) findViewById(R.id.lblprecio);
        lblcodigo.setText("");
        lbldescripcion.setText("");
        lblgrupo.setText("");
        lblprecio.setText("");
        EditText txtcodigo = (EditText) findViewById(R.id.txtcodigo);
        EditText txtgondola = (EditText) findViewById(R.id.txtgondola);
        EditText txtcantidad = (EditText) findViewById(R.id.txtcantidad);
        txtcantidad.setText("");
        //txtgondola.requestFocus();

        if (txtcodigo.getText().toString().length()==0)  {
            Toast toast = Toast.makeText(CapturarActivity.this, "Capture Codigo!", Toast.LENGTH_LONG);
            toast.show();
            finish();
            return;
        }
        nReg=0;
        try {
            nReg = 0;
            cSql = "select top 1 isnull(abc.preciooferta,c.precioventa) as precioventaoferta, c.* from codigos c left join (" +
                    "SELECT Codigo, round((PrecioOferta),2) as PrecioOferta " +
                    "FROM OfertasCodigo " +
                    "WHERE sucursal=99 and ((convert(char(10),fechainicio,102)) <= (convert(char(10),getdate(),102))) " +
                    "AND ((convert(char(10),fechatermino,102)) >= (convert(char(10),getdate(),102))) AND (Activa <> 0) " +
                    ") as abc on (c.codigo = abc.codigo) " +
                    "where upper(c.codigo) = upper('" + txtcodigo.getText().toString().trim() + "') or " +
                    " upper(c.codigoprov) = upper('" + txtcodigo.getText().toString().trim() + "') or " +
                    " upper(c.codigo2) = upper('" + txtcodigo.getText().toString().trim() + "')";
            ResultSet rs = DatabaseSQL.getRS(cSql);
            if (rs == null) {
                return;
            } else {
                if ((MainActivity.lerr) || (rs.isAfterLast())) {
                    Toast toast = Toast.makeText(CapturarActivity.this, "Codigo NO Existe!", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                    return;
                } else {
                    if (!(rs.isAfterLast())) {
                        //nReg = rs.getInt("reg");
                        ccod = rs.getString("codigo");
                        cdes = rs.getString("descripcion");
                        ngpo = rs.getInt("grupo");
                        if (rs.getInt("nacional") == 0) {
                            nprecio = round(rs.getDouble("precioventaoferta") * nTC,0);
                        } else {
                            nprecio = round(rs.getDouble("precioventaoferta"),0);
                        }
                    }
                    rs.close();
                }
            }
            rs = null;
            lblcodigo.setText("Codigo : " + ccod);
            lbldescripcion.setText("Descrip: " + cdes);
            lblgrupo.setText("Grupo  : " + ngpo);
            lblprecio.setText("Precio : " + nprecio);
            if (txtgondola.getText().toString().length()==0)  {
                txtgondola.setFocusableInTouchMode(true);
                txtgondola.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.showSoftInput(txtgondola, InputMethodManager.SHOW_IMPLICIT);
            } else {
             txtcantidad.setFocusableInTouchMode(true);    ///este el perrote
          txtcantidad.requestFocus();
            }
        } catch (Exception ex) {
            //Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            Toast toast = Toast.makeText(CapturarActivity.this, "Codigo NO Existe!", Toast.LENGTH_LONG);
            toast.show();
        } finally{
            System.gc();
            //EditText txtcant = (EditText) findViewById(R.id.txtcantidad);
         //   txtcant.requestFocus();
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void sndLimpiarCodigo(View view) {
        EditText editText = (EditText) findViewById(R.id.txtcodigo);
        editText.setText("");
    }

    public void sndLimpiarGondola(View view) {
        EditText editText = (EditText) findViewById(R.id.txtgondola);
        editText.setText("");
    }

    public void sndLimpiarCantidad(View view) {
        EditText editText = (EditText) findViewById(R.id.txtcantidad);
        editText.setText("");
    }


    public void sndGrabar(View view) {
        String cfile="";
        Boolean lCapFoto=false;
        int nReg=0;
        String cCodigo="";
        EditText txtcodigo = (EditText) findViewById(R.id.txtcodigo);
        EditText txtgondola = (EditText) findViewById(R.id.txtgondola);
        EditText txtcantidad = (EditText) findViewById(R.id.txtcantidad);
        TextView lblcodigo = (TextView) findViewById(R.id.lblcodigo);
        TextView lbldescripcion = (TextView) findViewById(R.id.lbldescripcion);
        TextView lblgrupo = (TextView) findViewById(R.id.lblgrupo);
        TextView lblprecio = (TextView) findViewById(R.id.lblprecio);

        if (txtcodigo.getText().toString().length()==0)  {
            Toast toast = Toast.makeText(CapturarActivity.this, "Capture Codigo!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (txtgondola.getText().toString().length()==0)  {
            Toast toast = Toast.makeText(CapturarActivity.this, "Capture Gondola!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (txtcantidad.getText().toString().length()==0)  {
            Toast toast = Toast.makeText(CapturarActivity.this, "Capture Cantidad!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        nReg=0;
        try {
            nReg = 0;
            cSql = "select count(*) as reg from codigos where upper(codigo) = upper('" + txtcodigo.getText().toString().trim() + "') or " +
                    " upper(codigoprov) = upper('" + txtcodigo.getText().toString().trim() + "') or " +
                    " upper(codigo2) = upper('" + txtcodigo.getText().toString().trim() + "')";
            ResultSet rs = DatabaseSQL.getRS(cSql);
            if (rs == null) {
                return;
            } else {
                if ((MainActivity.lerr) || (rs.isAfterLast())) {
                    Toast toast = Toast.makeText(CapturarActivity.this, "Codigo NO Existe!", Toast.LENGTH_LONG);
                    toast.show();
                    //finish();
                    return;
                } else {
                    if (!(rs.isAfterLast())) {
                        nReg = rs.getInt("reg");
                    }
                    rs.close();
                }
            }
            rs = null;
        } catch (SQLException ex) {
            Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.gc();
        }
        if (nReg == 0) {
            Toast toast = Toast.makeText(CapturarActivity.this, "Codigo NO Existe!", Toast.LENGTH_LONG);
            toast.show();
            //finish();
          //  txtcodigo.setFocusableInTouchMode(true);
            //txtcodigo.requestFocus();
            return;
        } else {
            try {
                cSql = "select * from codigos where upper(codigo) = upper('" + txtcodigo.getText().toString().trim() + "') or " +
                        " upper(codigoprov) = upper('" + txtcodigo.getText().toString().trim() + "') or " +
                        " upper(codigo2) = upper('" + txtcodigo.getText().toString().trim() + "')";
                ResultSet rs = DatabaseSQL.getRS(cSql);
                if (rs == null) {
                    return;
                } else {
                    if ((MainActivity.lerr) || (rs.isAfterLast())) {
                        Toast toast = Toast.makeText(CapturarActivity.this, "Codigo NO Existe!", Toast.LENGTH_LONG);
                        toast.show();
                        //finish();
                        return;
                    } else {
                        if (!(rs.isAfterLast())) {
                            cCodigo = rs.getString("codigo");
                        }
                        rs.close();
                    }
                }
                rs = null;
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                System.gc();
            }
        }
        if  (EXTRA_ID.equals("-1")) {
            cSql = "insert into inventoryaudit (codigo, gondola, cantidad, fecha) " +
                    " values( '" + cCodigo +
                    "','" + txtgondola.getText().toString().trim().toUpperCase() +
                    "','" + txtcantidad.getText().toString().trim().toUpperCase() +
                    "'," + " getdate());";
        } else {
            cSql = "update inventoryaudit set codigo = '" + cCodigo +
                    "',gondola = '" + txtgondola.getText().toString().trim().toUpperCase() +
                    "', cantidad = '" + txtcantidad.getText().toString().trim().toUpperCase() +
                    "', fecha = getdate() " + " where id = " + EXTRA_ID;
        }
        Boolean lResult = DatabaseSQL.execQuery(cSql);
        if (lResult) {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
            txtcodigo.setText("");
            txtcantidad.setText("");
            lblcodigo.setText("");
            lbldescripcion.setText("");
            lblgrupo.setText("");
            lblprecio.setText("");

            EditText tt = (EditText) findViewById(R.id.txtgondola);
            tt.clearFocus();
            tt.requestFocus();
            Context context = getApplicationContext();
            CharSequence text = "Codigo capturado, continua con el siguiente!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            //////////////////////
        } else {
            msbox("Error!", "No fue posible Grabar Datos en la BD!");
        }
        if (!EXTRA_ID.equals("-1")) {
            this.finish();

        }
    }

    public void sndBorrar(View view) {
        if  (!EXTRA_ID.equals("-1")) {
            QueryBoxDel("Borrar?", "Desea Borrar Registro en la BD ?");
        }
    }

    public void sndScan(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
      /*      String scanContent = scanningResult.getContents();
            EditText txtcodigo = (EditText) findViewById(R.id.txtcodigo);
            txtcodigo.setText(scanContent);
            Button btn;
            btn = (Button)findViewById(R.id.btnSig);
            btn.performClick();
            EditText txtcod1 = (EditText) findViewById(R.id.txtcodigo);
            txtcod1.requestFocus();*/
			/*
			EditText editText2 = (EditText) findViewById(R.id.editText2);
			if (editText2.getText().toString().length()==0)  {
				editText2.setFocusableInTouchMode(true);
				editText2.requestFocus();
				//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				//imm.showSoftInput(editText2, InputMethodManager.SHOW_IMPLICIT);
			} else {
				EditText txtcantidad = (EditText) findViewById(R.id.txtcantidad);
				txtcantidad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(hasFocus){
							Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
						}else {
							Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
						}
					}
				});
				txtcantidad.setFocusableInTouchMode(true);
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				//txtcantidad.requestFocus();
				//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				//imm.showSoftInput(txtcantidad, InputMethodManager.SHOW_IMPLICIT);
			}
			*/
        } else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No se escaneo!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void sndCodigoBuscar(View view) {
        EditText txtgondola = (EditText) findViewById(R.id.txtgondola);
        MainActivity.gondola_search = txtgondola.getText().toString();
        onSearchRequested();
    }

    public void sndCerrar(View view) {
        this.finish();
    }

    public void msbox(String str,String str2)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle(str);
        dlgAlert.setMessage(str2);
        dlgAlert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }






    public void QueryBoxDel(String str,String str2)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle(str);
        dlgAlert.setMessage(str2);
        dlgAlert.setPositiveButton("SI",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                cSql = "delete from  inventoryaudit where id = " + EXTRA_ID;
                Boolean lResult = mx.sounds.inventoryaudit.data.DatabasePostgres.execQuery(cSql);
                if (lResult) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    msbox("Exito!", "Se borro Registro en la BD!");
                } else {
                    msbox("Error!", "No fue posible Borrar Registro en la BD!");
                }
                finish();
            }
        });
        dlgAlert.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}
