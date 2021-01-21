package mx.sounds.inventoryaudit;

import mx.sounds.inventoryaudit.data.DatabaseSQL;
import mx.sounds.inventoryaudit.util.Base64Encoding;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import mx.sounds.inventoryaudit.BuildConfig;

public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_ID = "-1";
	public final static String codigo_search = "-1";
	public static String gondola_search = "-1";
	public static Boolean lsessionPg;
	public static Boolean lConnected=false;
	public static Boolean lerr;
	public static String[] img;
	public static String cMenError = "";
	public static String cschema = "public";
	public static int nStore;
	public static String urlBD;
	public static String userBD;
	public static String passwdBD;
	public static Context context;
	public static String gondola = "0";


	Spinner SpinnerSucursales;
	ArrayList<String> lista;
	ArrayAdapter<String> adaptador;

	final int versionCode = BuildConfig.VERSION_CODE;
	final String versionName = BuildConfig.VERSION_NAME;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	public void sndCapturar(View view) {
		if (MainActivity.lConnected) {
			Intent intent = new Intent(this, CapturarActivity.class);
			intent.putExtra(MainActivity.EXTRA_ID, "-1");
			startActivity(intent);
		} else {
			msbox("Error!", "No esta conectado a ninguna Red!");
		}
	}

	public void sndConsultar(View view) {
		if (MainActivity.lConnected) {
			Intent intent = new Intent(this, AskGondolaActivity.class);
			startActivity(intent);
		} else {
			msbox("Error!", "No esta conectado a ninguna Red!");
		}
	}

	public void sndConsultarTotales(View view) {
		Intent intent = new Intent(this, ConsultaTotalesActivity.class);
		startActivity(intent);
	}

	public void sndSubirFotos(View view) {
		Intent intent = new Intent(this, SubirFotosActivity.class);
		startActivity(intent);
	}

	public void sndProcDif(View view) {
		if (MainActivity.lConnected) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else {
			msbox("Error!", "No esta conectado a ninguna Red!");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		img = new String[4];
		img[0] = Base64Encoding.fDecode("YW5nZWxt");
		img[1] = Base64Encoding.fDecode("TWFkbGpkYTE=");
		img[2] = Base64Encoding.fDecode("Y3VzdG9tc3Nu");
		img[3] = Base64Encoding.fDecode("TWFkbGpkYTE=");

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (!(networkInfo != null && networkInfo.isConnected())) {
			msbox("Error!", "No hay conexion de Red disponible!");
			return;
		}

		setContentView(R.layout.activity_main);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
		TextView textviewVersionName = (TextView) findViewById(R.id.textViewVersionName);
		textviewVersionName.setText("Ver: " + versionName);
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		loadSpinnerSucursales();
		context = getApplicationContext();
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

	public void sndCerrar(View view) {
		if (MainActivity.lConnected) {
			DatabaseSQL.disconnect();
		}
		this.finish();
	}

	public void sndDesconectar(View view) {
		Spinner spinner = (Spinner) findViewById(R.id.SpinnerSucursales);
		if (MainActivity.lConnected) {
			DatabaseSQL.disconnect();
		}
		if (!MainActivity.lConnected) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				spinner.setEnabled(true);
			}
		}
	}

	public void sndConectar(View view) {
		if (MainActivity.lConnected) {

			DatabaseSQL.disconnect();
		}
		String cItemSelected;
		Spinner spinner = (Spinner) findViewById(R.id.SpinnerSucursales);
		cItemSelected = spinner.getSelectedItem().toString();
		MainActivity.nStore = Integer.parseInt(cItemSelected.substring(0, 5).trim());
		userBD = "usounds";
		passwdBD = "madljda";
		switch (MainActivity.nStore) {
			case 3:
				urlBD = "jdbc:jtds:sqlserver://192.168.3.90:1084/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 5:
				urlBD = "jdbc:jtds:sqlserver://192.168.5.90:65220/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 6:
				urlBD = "jdbc:jtds:sqlserver://192.168.1.80:55024/Taller;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 9:
				urlBD = "jdbc:jtds:sqlserver://192.168.9.90:4436/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 17:
				urlBD = "jdbc:jtds:sqlserver://192.168.17.90:2271/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 19:
				urlBD = "jdbc:jtds:sqlserver://192.168.19.90:1045/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 21:
				urlBD = "jdbc:jtds:sqlserver://192.168.1.80:55024/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			/*case 22:
				urlBD = "jdbc:jtds:sqlserver://192.168.22.90:49202/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;*/
			case 23:
				urlBD = "jdbc:jtds:sqlserver://192.168.1.90:58891/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 25:
				//AGREGAR PUERTO DE BD LIBRAMIENTO
				urlBD = "jdbc:jtds:sqlserver://192.168.22.90:56978/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;

			case 26:
				//AGREGAR PUERTO DE BD LIBRAMIENTO
				urlBD = "jdbc:jtds:sqlserver://192.168.1.96:64493/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;

			case 27:
				//AGREGAR PUERTO DE BD AMAZON ON SITE
				urlBD = "jdbc:jtds:sqlserver://192.168.1.97:61998/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 28:
				//AGREGAR PUERTO DE BD LINIO
				urlBD = "jdbc:jtds:sqlserver://192.168.1.98:49684/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;
			case 29:
				//AGREGAR PUERTO DE BD WALMART
				urlBD = "jdbc:jtds:sqlserver://192.168.1.95:52261/CML;loginTimeout=60;socketTimeout=600;socketKeepAlive=true";
				break;

		}
		DatabaseSQL.isConnected();
		if (MainActivity.lConnected) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				spinner.setEnabled(false);
			}
		}
	}

	private void loadSpinnerSucursales() {
		String cSql;
		Spinner spinner = (Spinner) findViewById(R.id.SpinnerSucursales);
		lista = new ArrayList<String>();
		lista.add("    3 - PJ        ");
		lista.add("    5 - RV        ");
		lista.add("    6 - TA        ");
		lista.add("    9 - RG        ");
		lista.add("   17 - SZ        ");
		lista.add("   19 - TR        ");
		lista.add("   21 - BOD       ");
		//lista.add("   22 - MI        ");
		lista.add("   23 - SI        ");
		lista.add("   25 - PL        ");
		lista.add("   26 - AL        ");
		lista.add("   27 - AO        ");
		lista.add("   28 - LI        ");
		lista.add("   29 - WA        ");



		adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista);
		adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adaptador);
	}

	public static String padLeft(String s, int n) {
		s = s.trim();
		int nlen = n - s.length();
		for (int nfor = 0; nfor < nlen; nfor++) {
			s = " " + s;
		}
		return s;
	}

	public static String padLeft0(String s, int n) {
		s = s.trim();
		int nlen = n - s.length();
		for (int nfor = 0; nfor < nlen; nfor++) {
			s = "0" + s;
		}
		return s;
	}


	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://mx.sounds.inventoryaudit/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://mx.sounds.inventoryaudit/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}
