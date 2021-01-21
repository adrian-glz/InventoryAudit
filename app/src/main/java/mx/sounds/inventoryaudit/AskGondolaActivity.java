package mx.sounds.inventoryaudit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AskGondolaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_gondola);
        setTitle("Consulta de Captura");
    }

    public void sndConsultar(View view) {
        if (MainActivity.lConnected) {
            EditText editText1 = (EditText) findViewById(R.id.editText1);
            if (editText1.getText().toString().trim().length()==0)  {
                Toast toast = Toast.makeText(this, "Capture Gondola!", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            MainActivity.gondola = editText1.getText().toString().trim();
            Intent intent = new Intent(this, ConsultaActivity.class);
            startActivity(intent);
        } else {
            msbox("Error!", "No esta conectado a ninguna Red!");
        }
    }

    public void sndCerrar(View view) {
        this.finish();
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

}
