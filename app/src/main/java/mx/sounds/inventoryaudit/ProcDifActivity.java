package mx.sounds.inventoryaudit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import mx.sounds.inventoryaudit.data.DatabaseSQL;

public class ProcDifActivity extends AppCompatActivity {

    private EditText tvDisplayDate1;
    private Button btnChangeDate1;

    private int year1;
    private int month1;
    private int day1;
    private String cSql;

    ArrayList<String> lista;
    ArrayAdapter<String> adaptador;

    static final int DATE_DIALOG_ID1 = 998;

    private View mProcDifFormView;
    private View mProgressView;

    private ProcDifTask mProcDifTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proc_dif);
        setTitle("Proceso de Diferencias de Inventario");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProcDifFormView = findViewById(R.id.procdif_form);
        mProgressView = findViewById(R.id.procdif_progress);
    }

    public void buttonProcDif_onclick(View view) {
        Boolean lresult=false;
        String cfecha1="";
        if (mProcDifTask != null) {
            return;
        }

        try {


         showProgress(true);

            Context context = getApplicationContext();
            CharSequence text = "ADVERTENCIA NO CIERRE NI SALGA DE ESTA PANTALLA , ESPERE QUE TERMINE EL PROCESO!";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            cfecha1 = year1 + "-" + month1 + "-" + day1;
            cSql = "delete from invent; insert into invent (codigo, cantidad) " +
            "select codigo, sum(cantidad) as cantidad from InventoryAudit where fecha >= '" + cfecha1 + "' group by codigo; " +
            "exec spp_cargaperiodosdosmeses; " +
            "declare @tcv as float; "+
            "set @tcv = (select TipoCambioVenta from infor); "+
            "exec spp_ADiferenciasDeInventario @tcv";
            mProcDifTask = new ProcDifTask(cSql);
            mProcDifTask.execute((Void) null);
            /*
            lresult = DatabaseSQL.execQuery(cSql);
            //showProgress(false);
            if (lresult) {
                msbox("Informacion", "Proceso Realizado Correctamente!");
            } else {
                msbox("Error!", "Error al procesar. Favor de volver a Intentarlo!");
            }
            */
        } catch (Exception ex) {
            //Logger.getLogger(DatabaseSQL.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.gc();
        }

    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProcDifFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProcDifFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProcDifFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProcDifFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    public void setCurrentDateOnView() {
        tvDisplayDate1 = (EditText) findViewById(R.id.edit_Date1);

        final Calendar c = Calendar.getInstance();
        year1 = c.get(Calendar.YEAR);
        month1 = c.get(Calendar.MONTH)+1;
        day1 = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate1.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day1).append("-").append(month1).append("-")
                .append(year1).append(" "));
    }

    public void addListenerOnButton() {
        btnChangeDate1 = (Button) findViewById(R.id.button1);
        btnChangeDate1.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID1);

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID1:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener1,
                        year1, month1-1,day1);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener1
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year1 = selectedYear;
            month1 = selectedMonth+1;
            day1 = selectedDay;

            // set selected date into textview
            tvDisplayDate1.setText(new StringBuilder().append(day1)
                    .append("-").append(month1).append("-").append(year1)
                    .append(" "));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.sales1, menu);
        setCurrentDateOnView();
        addListenerOnButton();
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

    public class ProcDifTask extends AsyncTask<Void, Void, Boolean> {

        private final String mcSql;
        private Boolean lresult;

        ProcDifTask(String cSql) {
            mcSql = cSql;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                lresult=false;
                lresult = DatabaseSQL.execQuery(mcSql);
            } catch (Exception e) {
                return false;
            }
            return lresult;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mProcDifTask = null;
            showProgress(false);
            if (success) {
                msbox("Informacion", "Proceso Realizado Correctamente!");
            } else {
                msbox("Error!", "Error al procesar. Favor de volver a Intentarlo!");
            }
        }

        @Override
        protected void onCancelled() {
            mProcDifTask = null;
            showProgress(false);
        }
    }

}
