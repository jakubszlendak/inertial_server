package com.jms.software.inertial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Server{
	
	protected static final String DISCONNECT_FLAG = "__DISCONNECT__";
	private Thread mServerThread;
	private ServerSocket mServerSocket;
	private Socket mSocket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean isConnected;
	private boolean isRunning; 
	
	private LinkedList<String> mReceivedData;
	
	private String mDataToSend;
	private int mPortNumber, mPeriod;
	
	public Server(int portNumber, int period) {
		this.mReceivedData = (LinkedList)Collections.synchronizedList(new LinkedList<String>());
		this.mPortNumber = portNumber;
		this.mPeriod = period;
		this.mDataToSend=null;
		
	}
	public void setConnectionParameters(int portNumber, int period){
		this.mPortNumber = portNumber;
		this.mPeriod = period;
	}
	
	public void connect(){
		try{
			mServerSocket = new ServerSocket(mPortNumber);
			System.out.println("Waiting for connection...");
			mSocket = mServerSocket.accept();
			System.out.println("Connection accepted.");
			out = new PrintWriter(mSocket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			System.out.println("I/O streams setted up.");
			isConnected=true;
		} catch(IOException e){
			System.out.println("Error occured when connecting: "+e.getMessage());
			disconnect();
			
		}	
		
	}
	/**
	 * Starts server. If not connected, connects.
	 */
	public void startServer(){
		mServerThread = new Thread(new Runnable(){

			@Override
			public void run() {
				if(!isConnected){
					connect();
				}
				else{
					isRunning=true;
					while(isRunning){
						String data=null;
						try {
							data=in.readLine();
						} catch (IOException e) {
							System.out.println("Error occured when reading from socket stream: "+e.getMessage());
						}
						if(data!=null){
							if(data==DISCONNECT_FLAG)
								isRunning=false;
							mReceivedData.addFirst(data);
						}
						
						synchronized(mDataToSend){
							if(mDataToSend!=null){
								out.println(mDataToSend);
								mDataToSend=null;
							}
						}
						try {
							Thread.sleep(mPeriod);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					disconnect();
				}
				
			}
			
		});
		
		mServerThread.start();
		
		
		
	}
	private void disconnect() {
		try{
			mServerSocket.close();
			mSocket.close();
			in.close();
			out.close();
		}catch(IOException e1){
			System.out.println("Error occured when closing connection: "+e1.getMessage());
		}
		isConnected=false;		
	}
	
	/**
	 * Stops and disconnects server
	 */
	public void stopServer(){
		isRunning=false;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mServerThread=null;
		
	}
	
	public String popElement(){
		if(!mReceivedData.isEmpty())
			return mReceivedData.pop();
		else
			return null;
	}
	
	public LinkedList<String> getInputBuffer(){
		return mReceivedData;
	}
	
	public synchronized void sendData(String data){
		mDataToSend = data;
	}
	

	
}
