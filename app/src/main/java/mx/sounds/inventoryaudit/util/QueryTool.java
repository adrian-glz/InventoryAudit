/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.sounds.inventoryaudit.util;

import java.sql.*;

/**
 *
 * @author ericjbruno
 */
public class QueryTool {
    protected String driver, url, user, pwd;
    protected Connection conn = null;
    protected String query = null;
    protected PreparedStatement stmt = null;
    protected ResultSet rs = null;

    public QueryTool(Connection conn) {
        this.conn = conn;
    }

    public QueryTool(
            String driver, String url, String user, String pwd) throws Exception {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pwd = pwd;

        // Attempt to get connection
        Class.forName(driver).newInstance();
        conn = DriverManager.getConnection(url, user, pwd);
    }

    public Connection getConnection() {
        return conn;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setUsername(String user) {
        this.user = user;
    }

    public void setPassword(String pwd) {
        this.pwd = pwd;
    }

    public PreparedStatement setQuery(String query) throws Exception {
        if ( conn == null )
            conn = getPooledConnection();

        this.query = query;
        this.stmt = doCreateStatement();

        return stmt;
    }

    public PreparedStatement setQuery(
            Connection conn, String query) throws Exception {
        this.conn = conn;
        return setQuery( query );
    }

    public PreparedStatement setQueryWithReturn(String query) throws Exception {
        return setQuery( query );
    }

    public PreparedStatement setQueryWithReturn(
            Connection conn, String query) throws Exception {
        this.conn = conn;
        return setQuery( query );
    }

    public ResultSet exec() throws Exception {
        PreparedStatement ps = (PreparedStatement)stmt;
        if ( ps.execute() )
            return ps.getResultSet();

        // An error occured. Output all of the related messages
        while ( true ) {
            if ( ps.getMoreResults() ) {
                rs = ps.getResultSet();
                break;
            }
            else if ( ps.getUpdateCount() == -1 ) {
                break;
            }
        }

        SQLWarning warning = ps.getWarnings();
        while ( warning != null ) {
            System.out.println(warning.getMessage());
            warning = warning.getNextWarning();
        }

        return rs;
    }

    // Closes all the query-related resources
    public void close() {
        try { conn.commit(); } catch ( Exception e ) { }
        try { rs.close(); } catch ( Exception e ) { }
        try { stmt.close(); } catch ( Exception e ) { }
    }

    /////////////////////////////////////////////////////////////

    protected Connection getPooledConnection() throws Exception {
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, user, pwd);
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        return conn;
    }

    protected PreparedStatement doCreateStatement() throws Exception {
        return conn.prepareCall(query);
    }
}
