package com.orenbissick.GaleraProxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GaleraProxyApplication {
	private static final String USER_AGENT = "Mozilla/5.0";
    static Configuration prop = new Configuration();
	static int choosenServer;
    static int lastServer = 0;
    
    static String hosts[] = prop.getRIP();
    static int remoteport = Integer.parseInt(prop.getRPort());
    static int localport = Integer.parseInt(prop.getLPort());
    static DBNode [] nodes = new DBNode[hosts.length];
	public static void main(String[] args) {
		SpringApplication.run(GaleraProxyApplication.class, args);
        
        try{
            // and the local port that we listen for connections on
            
            for(int i = 0; i < hosts.length; i++) {
                nodes[i] = new DBNode(hosts[i],"jdbc:mariadb://"+hosts[i]+":"+remoteport,prop.getUser(),prop.getPassword());
            }
            final ServerSocket server = new ServerSocket(localport);
            
            Thread proxyThread = new Thread(
            		() -> {
                    	while(true){
                    		runProxy(server,remoteport,localport,hosts,nodes);
                    	}
            });
            proxyThread.start();
                
            Thread apiThread = new Thread(
                    () -> {
                    	while(true) {
                    		for(int i = 0; i < hosts.length; i++) {
                                nodes[i] = new DBNode(hosts[i],"jdbc:mariadb://"+hosts[i]+":"+remoteport,prop.getUser(),prop.getPassword());
                                try {
                					sendPost(nodes[i].getIP(),nodes[i].getConnectionsSinceStart(),nodes[i].getConnections(),nodes[i].getState());
                				} catch (Exception e) {
                					e.printStackTrace();
                				}
                            }
                    	}
                });
            apiThread.start();
            
        }catch(IOException | IllegalArgumentException e){
            System.out.println(e.getMessage());   
        }
	}
	
	public static void runProxy(ServerSocket server, int remoteport, int localport, String [] hosts, DBNode[] nodes) {
                	
        choosenServer = chooseServer(prop.getLoadType(),nodes,lastServer);
        System.out.println("Sending connection to " + hosts[choosenServer] + ":" + remoteport
            + " on port " + localport);
        try {
			new ThreadProxy(server.accept(), hosts[choosenServer], remoteport);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        nodes[choosenServer].incConnectionsSinceStart();
        
        lastServer = choosenServer;
	}
	
	public static int chooseServer(String type, DBNode[] nodes, int last){
        int next = 0;
        Random rand = new Random();
        String wsrepState;
        DBNode [] upNodes = getAvailableNodes(nodes);
   
        if(type.equalsIgnoreCase("random")){
            next = rand.nextInt(upNodes.length);
        }
        else if(type.equalsIgnoreCase("round-robin")){
            if(last != upNodes.length-1){
                next = last+1;       
            }else{
                next = 0;
            }
        }
        else if(type.equalsIgnoreCase("least-connection")){
            int smallest = 0;
        
            for(int i =0;i<upNodes.length;i++) {
                if(upNodes[i].getConnections() < upNodes[smallest].getConnections()) {
                    smallest = i;
                    next = i;
                }
            }
        }
        else{
            throw new IllegalArgumentException("Invalid load balancer type.");
        }
        return next;
    }
    
    public static DBNode[] getAvailableNodes(DBNode[] nodes){
        DBNode [] upNodes = new DBNode[nodes.length];
        String wsrepState;
        for(int i =0; i < nodes.length; i++){
            wsrepState = nodes[i].getState();
            if("4".equals(wsrepState)){
                upNodes[i] = nodes[i];
            }
        }
        return upNodes;
    }
    private static void sendPost(String name, int totalConnections, int currConnections, String status) throws Exception {
    	
		String url = "http://localhost:8080/stats";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "name="+name+"&totalConnections="+totalConnections+"&currConnections="+currConnections+"&status="+status;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

	}
}
