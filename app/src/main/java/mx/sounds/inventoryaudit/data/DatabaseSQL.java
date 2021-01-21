/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.sounds.inventoryaudit.data;
import android.content.Context;
import android.widget.Toast;

import java.sql.*;

import mx.sounds.inventoryaudit.MainActivity;
import mx.sounds.inventoryaudit.util.QueryTool;
//import simo.data.DatabasePostgresAccesos;

/**
 *
 * @author jmendoza
 */

public class DatabaseSQL {
    static String driver = "net.sourceforge.jtds.jdbc.Driver"; //"com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static Connection con = null;
    final Object lock = new Object();

    protected static final DatabaseSQL _INSTANCE = new DatabaseSQL();
    public static DatabaseSQL getInstance() { return _INSTANCE; }

    protected DatabaseSQL() {
        try {
            if ( con == null ) {
                Class.forName( driver ).newInstance();
                con = DriverManager.getConnection(MainActivity.urlBD, MainActivity.userBD, MainActivity.passwdBD);
                MainActivity.lConnected = true;
                Toast.makeText(MainActivity.context, "BD Conectada!", Toast.LENGTH_SHORT).show();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            Toast.makeText(MainActivity.context, "ERROR! al conectar BD", Toast.LENGTH_LONG).show();
        }

        if ( con != null )
            System.out.println("Database connected");
        else
            System.out.println("Database NOT connected");
    }

    private void createTables(String cSql) {
        execQuery(cSql);
        System.out.println("Database tables created");
    }

    static public ResultSet getRS(String cSql) {
        QueryTool query = null;
        try {
            //query = new QueryTool(con);
            String req = cSql;
            //PreparedStatement ps = query.setQuery( req );
            //ResultSet rs = query.exec();
            PreparedStatement ps = con.prepareStatement( req );
            ResultSet rs = ps.executeQuery();
            if ( rs.next() )
                MainActivity.lerr = false;
            return (rs);
        }
        catch ( Exception e ) {
            e.printStackTrace();
            MainActivity.lerr = true;
            MainActivity.cMenError = e.getMessage().trim().toLowerCase();
        }
        finally {
            //query.close();
        }
        return null;
    }

    public static boolean execQuery(String queryStr) {
        QueryTool query = null;
        Boolean lret = false;
        try {
            //query = new QueryTool(con);
            //query.setQuery( queryStr );
            //query.exec();
            PreparedStatement ps = con.prepareStatement( queryStr );
            ps.executeUpdate();
            lret = true;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            MainActivity.cMenError = e.getMessage().trim().toLowerCase();
            lret = false;
        }
        finally {
            System.gc();
        }
        return lret;
    }

    public static boolean isConnected() {
        MainActivity.lConnected = false;
        try {
            if ( con == null ) {
                Class.forName( driver ).newInstance();
                con = DriverManager.getConnection(MainActivity.urlBD, MainActivity.userBD, MainActivity.passwdBD);
                MainActivity.lConnected = true;
                Toast.makeText(MainActivity.context, "BD Conectada!", Toast.LENGTH_SHORT).show();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            Toast.makeText(MainActivity.context, "ERROR! al conectar BD", Toast.LENGTH_LONG).show();
        }

        if ( con != null ) {
            System.out.println("Database connected");
            MainActivity.lConnected = true;
        }
        else {
            System.out.println("Database NOT connected");
            MainActivity.lConnected = false;
        }
        return MainActivity.lConnected;
    }

    public static void disconnect() {
        try {
            // Close the active connection
            if ( isConnected() ) {
                //con.commit();
                con.close();
            }

            // Shutdown Derby - the exception contains the shutdown message
            try {
                //con = DriverManager.getConnection( "jdbc:postgresql:;shutdown=true", usr, pwd );
                con = DriverManager.getConnection( MainActivity.urlBD + ";shutdown=true",  MainActivity.userBD, MainActivity.passwdBD );
                con = null;
            }
            catch ( SQLException sqle ) {
                System.out.println(sqle.getMessage());
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.context, "BD Desconectada!", Toast.LENGTH_SHORT).show();
        MainActivity.lConnected = false;
    }


}
