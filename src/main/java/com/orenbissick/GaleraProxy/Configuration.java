package com.orenbissick.GaleraProxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author obissick
 */
public final class Configuration {
    
    private String [] rIP;
    private String rPort;
    private String lPort;
    private String user;
    private String password;
    private String loadType;
    private Properties prop = new Properties();
    private InputStream input = null;
    private String OS = System.getProperty("os.name").toLowerCase();
    
    Configuration(){
	try{
            if (isWindows()) {
		input = new FileInputStream("src/main/java/com/orenbissick/GaleraProxy/settings.properties");
            } else if (isMac()) {
		
            } else if (isUnix()) {
		input = new FileInputStream("/etc/galeraproxy/settings.properties");
            } else if (isSolaris()) {
		
            } else {
		System.out.println("Your OS is not support!!");
            }

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            this.rIP = prop.getProperty("fip").split(",");
            this.rPort = prop.getProperty("fport");
            this.lPort = prop.getProperty("lport");
            this.user = prop.getProperty("dbuser");
            this.password = prop.getProperty("dbpassword");
            this.loadType = prop.getProperty("ltype");

	}catch(IOException ex){
            ex.printStackTrace();
	}finally{
            if(input != null){
		try{
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
	}
    }
    
    public String[] getRIP(){
        return this.rIP;
    }
    public String getRPort(){
        return this.rPort;
    }
    public String getLPort(){
        return this.lPort;
    }
    public String getUser(){
        return user;
    }
    public String getPassword(){
        return password;
    }
    public String getLoadType(){
        return loadType;
    }
    public boolean isWindows() {
        return (OS.contains("win"));
    }
    public boolean isMac() {
        return (OS.contains("mac"));
    }
    public boolean isUnix() {
	return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
    public boolean isSolaris() {
	return (OS.contains("sunos"));
    }
}
