package com.orenbissick.GaleraProxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obissick
 */
public class DBNode{
    private Connection con = null;
    private Statement st = null;
    private ResultSet rs = null;
    private String state = "0";
    private int connections; 
    private String IPAdd = null ;
    private String url;
    private String user;
    private String password;
    private int connectionSinceStart;
        
    DBNode(String ip, String url, String user, String password){
        this.IPAdd = ip;
        this.url = url;
        this.user = user;
        this.password = password;
        this.connectionSinceStart = 0;
    }
        
    public String execQuery(String query) throws SQLException{
        String result = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            if (rs.next()) {
                result = rs.getString(2);
            } 
            close();
        } catch (SQLException ex) {
           System.out.println(ex.getMessage());
        }
        return result;
    }
    
    public String getIP() {
    	return IPAdd;
    }
    
    public String getState(){
        setState();
        return state;
    }
    
    public int getConnections(){
        setConnections();
        return connections;
    }
    
    public void setConnections(){
        try {
            this.connections = Integer.parseInt(execQuery("show global status like 'Threads_connected';"));
        } catch (SQLException ex) {
            Logger.getLogger(DBNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setState(){
        try {
            this.state = execQuery("SHOW STATUS LIKE 'wsrep_local_state';");
        } catch (SQLException ex) {
            Logger.getLogger(DBNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close(){
        try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
               System.out.println(ex.getMessage());
        }
    }
    
    public void incConnectionsSinceStart(){
        this.connectionSinceStart++;
        printStats();
    }
    
    public int getConnectionsSinceStart(){
        return this.connectionSinceStart;
    }
    
    public void printStats(){
        System.out.println("IP Address : Total Connections since start");
        System.out.println(IPAdd + " : " + connectionSinceStart);
    }
}
