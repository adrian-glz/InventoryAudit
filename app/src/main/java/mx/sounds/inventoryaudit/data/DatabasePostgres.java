/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.sounds.inventoryaudit.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.sounds.inventoryaudit.MainActivity;
import mx.sounds.inventoryaudit.util.QueryTool;
import mx.sounds.inventoryaudit.util.TunnelSSHPostgres;
//import simo.modules.AccessControl;

/**
 *
 * @author jmendoza
 */

public class DatabasePostgres {
    static String driver = "org.postgresql.Driver";
    public static Connection con = null;
    final Object lock = new Object();
    //static Database db = Database.getInstance();
    
    protected static final DatabasePostgres _INSTANCE = new DatabasePostgres();
    public static DatabasePostgres getInstance() { return _INSTANCE; }

    protected DatabasePostgres() {
        try {

            if ( con == null ) {
                Class.forName( driver ).newInstance();
                //con = DriverManager.getConnection(Database.getURL(), Database.getUSERNAME(), Database.getCLAVE());
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/CFDi?loginTimeout=30&socketTimeout=30", 
                		MainActivity.img[2], MainActivity.img[3]);
                MainActivity.lConnected = true;
                execQuery("SET search_path = public;");
            }
        }
        catch ( Exception e ) {
            // Database connect failed because it probably doesn't exist
            e.printStackTrace();
            MainActivity.lConnected = false;
            MainActivity.cMenError = e.getMessage().trim().toLowerCase();
            if (lFoundConnectionError(MainActivity.cMenError)) {
                //JOptionPane.showMessageDialog(frame, "Sin Conexion a Internet !", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);            
            }
        }

        if ( con != null ) {
            System.out.println("Database connected");
        } else {
            System.out.println("Database NOT connected");
        }
    }

    private void createTables(String cSql) {
        execQuery(cSql);
        System.out.println("Database tables created");
    }

    public static boolean execQuery(String queryStr) {
        QueryTool query = null;
        Boolean lret = false;
        try {
            //if (DatabasePostgres.isConnectedCheck()) {
            query = new QueryTool(con);
            query.setQuery( queryStr );
            query.exec();
            lret = true;
            //} 
        }
        catch ( Exception e ) {
            e.printStackTrace();
            MainActivity.cMenError = e.getMessage().trim().toLowerCase();
            if (lFoundConnectionError(MainActivity.cMenError)) {
                //JOptionPane.showMessageDialog(frame, "Sin Conexion a Internet !\n" + SiMo.cMenError, "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);            
            }
            lret = false;
        }
        finally {
            query.close();
            query = null;
            System.gc();
        }
        return lret;
    }

    //public boolean isConnected() throws SQLException {
    public static boolean isConnected() {
        QueryTool query = null;
        if (MainActivity.lConnected) {
            try {
                TunnelSSHPostgres.main();                
                //con = DriverManager.getConnection(Database.getURL(), Database.getUSERNAME(), Database.getCLAVE());
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/CFDi?loginTimeout=30&socketTimeout=30", 
                		MainActivity.img[2], MainActivity.img[3]);
                MainActivity.cschema = "public";
                execQuery("SET search_path = " + MainActivity.cschema + ";");                
                MainActivity.lConnected = true;
            } catch ( Exception e ) {
            	MainActivity.lConnected = false;
            	MainActivity.cMenError = e.getMessage().trim().toLowerCase();
                if (DatabasePostgres.lFoundConnectionError(MainActivity.cMenError)) {
                    //JOptionPane.showMessageDialog(frame, "Sin Conexion a Internet !\n" + SiMo.cMenError, "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);            
                }
                
            }
        }
        return MainActivity.lConnected;
    }
    
    public static boolean isConnectedCheck() {
        return isConnectedCheck(true); // Default Halt the System, if an error append
    }
    
    public static boolean isConnectedCheck(boolean lHalt) {
        Connection con = null;
        Boolean lret = false;
        String url = "jdbc:postgresql://localhost:5431/SM_v122?loginTimeout=30&socketTimeout=30";
        try {
            Class.forName( driver ).newInstance();
            //con = DriverManager.getConnection(Database.getURL(), Database.getUSERNAME(), Database.getCLAVE());
            con = DriverManager.getConnection(url, MainActivity.img[2], MainActivity.img[3]);
            lret = true;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            MainActivity.cMenError = e.getMessage().trim().toLowerCase();
            if (lHalt) {
                //JOptionPane.showMessageDialog(frame, "Sin Conexion a Internet !\n" + SiMo.cMenError, "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);            
            }
            lret = false;
        }
        con = null;
        url = null;
        System.gc();
        return lret;
    }

    public static void disconnect() {
        try {
			MainActivity.lConnected = false;
	        con.close();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static ResultSet getRS(String cSql) {
        QueryTool query = null;
        try {
            //if (DatabasePostgres.isConnectedCheck()) {
            query = new QueryTool(con);
            String req = cSql;
            PreparedStatement ps = query.setQuery( req );
            ResultSet rs = query.exec();
            if ( rs.next() ) {
            	MainActivity.lerr = false;
                return (rs);
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            MainActivity.lerr = true;
            MainActivity.cMenError = e.getMessage().trim().toLowerCase();
            if (lFoundConnectionError(MainActivity.cMenError)) {
                //JOptionPane.showMessageDialog(frame, "Sin Conexion a Internet !\n" + SiMo.cMenError, "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);            
            }
        } 
        return null;
    }
    
    public static Boolean lFoundConnectionError(String cMenError) {
        Boolean lret=false;
        if ((cMenError.contains("i/o")) && (cMenError.contains("backend"))) {
            lret = true;
        } else {
            if ((cMenError.contains("connection")) && (cMenError.contains("refused"))) {
                lret = true;
            }   
        }
        return lret;
    }
    
    /*
    public Date getServerDate() {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        Date dRet = new java.sql.Date(0);

        try {
            dRet = sdf.parse("2000-1-1");
        } catch (ParseException ex) {
            Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isConnected()) {
            try {
                dRet = getRS("select current_date as fecha").getDate("fecha");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dRet;
    }
    */

   
    public static java.sql.Timestamp getServerDate(int nEmpresa) {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                //dRet = getRS("select now() as fecha").getTimestamp("fecha");
                ResultSet rs = getRS("select sm_0001.getServerTime("+ nEmpresa + ") as fecha");
                dRet = rs.getTimestamp("fecha");
                rs.close();
                rs = null;
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                System.gc();                
            }
        //}
        return dRet;
    }
    
   
    public static java.sql.Time getServerTime(int nEmpresa) {
        java.sql.Time dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                //ResultSet rs = getRS("select now() as fecha");
                ResultSet rs = getRS("select sm_0001.getServerTime("+ nEmpresa + ") as fecha");
                dRet = rs.getTime("fecha");
                rs.close();
                rs = null;
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                System.gc();                
            }
        //}
        return dRet;
    }    

    public static java.sql.Timestamp getServerProductsDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        ResultSet rs;

        //if (isConnectedCheck()) {
            try {
                //dRet = getRS("select date_products from " + SiMo.cschema + ".simo_infor").getTimestamp("date_products");
                rs = getRS("select date_products from " + MainActivity.cschema + ".simo_infor");
                dRet = rs.getTimestamp("date_products");                
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();      
        return dRet;
    }

    public static java.sql.Timestamp getServerCurrenciesDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                dRet = getRS("select date_currencies from " + MainActivity.cschema + ".simo_infor").getTimestamp("date_currencies");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return dRet;
    }

    public static java.sql.Timestamp getServerCustomersDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                dRet = getRS("select date_customers from " + MainActivity.cschema + ".simo_infor").getTimestamp("date_customers");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return dRet;
    }
    
    public static java.sql.Timestamp getServerStoresDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                dRet = getRS("select date_stores from " + MainActivity.cschema + ".simo_infor").getTimestamp("date_stores");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return dRet;
    }    

    public static java.sql.Timestamp getServerPaymentFormsDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                dRet = getRS("select date_paymentforms from " + MainActivity.cschema + ".simo_infor").getTimestamp("date_paymentforms");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return dRet;
    }    

    public static java.sql.Timestamp getServercategoriesDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                dRet = getRS("select date_categories from " + MainActivity.cschema + ".simo_infor").getTimestamp("date_categories");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return dRet;
    }    
    
    public static java.sql.Timestamp getServerproducts_to_categoriesDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                dRet = getRS("select date_products_to_categories from " + MainActivity.cschema + ".simo_infor").getTimestamp("date_products_to_categories");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return dRet;
    }    

    public static java.sql.Timestamp getServerSpecialsDate() {
        java.sql.Timestamp dRet = null; // = new java.sql.Date(0);
        //if (isConnectedCheck()) {
            try {
                dRet = getRS("select date_specials from " + MainActivity.cschema + ".simo_infor").getTimestamp("date_specials");
            } catch (SQLException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return dRet;
    }    

    public static String getSerieInvoice(int nstore) {
        String cRet = ""; 
        //if (isConnectedCheck()) {
            try {
                cRet = getRS("select invoice_serie from " + MainActivity.cschema + ".simo_stores_cfdi where stores_id = " + nstore).getString("invoice_serie").trim();
            } catch (Exception ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return cRet;
    }    

    public static int getDefaultCurrency() {
        int nRet = 0; 
        //if (isConnectedCheck()) {
            try {
                nRet = getRS("select id from " + MainActivity.cschema + ".simo_currencies where currencies_default <> 0 order by id limit 1 ").getInt("id");
            } catch (Exception ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        DatabasePostgres.disconnect();
        TunnelSSHPostgres.disconnect();  
        MainActivity.lConnected = true;
        DatabasePostgres.isConnected();              
        return nRet;
    }    
    
}
