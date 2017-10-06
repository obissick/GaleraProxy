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
	public static void main(String[] args) {
		SpringApplication.run(GaleraProxyApplication.class, args);
        Configuration prop = new Configuration();
        int lastServer = 0;
        try{
            // and the local port that we listen for connections on
            String hosts[] = prop.getRIP();
            int remoteport = Integer.parseInt(prop.getRPort());
            int localport = Integer.parseInt(prop.getLPort());
            DBNode [] nodes = new DBNode[hosts.length];
            for(int i = 0; i < hosts.length; i++) {
                nodes[i] = new DBNode(hosts[i],"jdbc:mariadb://"+hosts[i]+":"+remoteport,prop.getUser(),prop.getPassword());
                try {
					sendPost(nodes[i].getIP(),nodes[i].getConnectionsSinceStart(),nodes[i].getConnections(),nodes[i].getState());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            ServerSocket server = new ServerSocket(localport);
            while(true){
                int choosenServer = chooseServer(prop.getLoadType(),nodes,lastServer);
                System.out.println("Sending connection to " + hosts[choosenServer] + ":" + remoteport
                    + " on port " + localport);
                new ThreadProxy(server.accept(), hosts[choosenServer], remoteport);
                nodes[choosenServer].incConnectionsSinceStart();
                try {
					sendPost(nodes[choosenServer].getIP(),nodes[choosenServer].getConnectionsSinceStart(),nodes[choosenServer].getConnections(),nodes[choosenServer].getState());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                lastServer = choosenServer;
            }
        }catch(IOException | IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
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
