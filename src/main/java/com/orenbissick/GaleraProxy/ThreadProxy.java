package com.orenbissick.GaleraProxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles a socket connection to the proxy server from the client and uses 2
 * threads to proxy between server and client
 *
 * @author obissick
 *
 */
public class ThreadProxy extends Thread{
    private Socket sClient;
    private final String SERVER_URL;
    private final int SERVER_PORT;
    ThreadProxy(Socket sClient, String ServerUrl, int ServerPort){
        this.SERVER_URL = ServerUrl;
        this.SERVER_PORT = ServerPort;
        this.sClient = sClient;
        this.start();
    }
    public void run() {
        try {
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = sClient.getInputStream();
            final OutputStream outToClient = sClient.getOutputStream();
            Socket client = null, server = null;
            // connects a socket to the server
            try{
                server = new Socket(SERVER_URL, SERVER_PORT);
            }catch(IOException e){
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        outToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = server.getInputStream();
            final OutputStream outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread(){
                @Override
                public void run(){
                    int bytes_read;
                    try{
                        while((bytes_read = inFromClient.read(request)) != -1) {
                            outToServer.write(request, 0, bytes_read);
                            outToServer.flush();
                            //TODO CREATE YOUR LOGIC HERE
                        }
                    }catch(IOException e){
                    }
                    try{
                        outToServer.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try{
                while((bytes_read = inFromServer.read(reply)) != -1){
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                    //TODO CREATE YOUR LOGIC HERE
                }
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            outToClient.close();
            sClient.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}