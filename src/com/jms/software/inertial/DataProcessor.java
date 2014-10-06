package com.jms.software.inertial;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.lang.Math;

public class DataProcessor {
	
	
	private Server mServer;
	private Thread mProcessingThread;
	private boolean isRunning = false;
	private DataVect acceleration, rate;
	private double thetaAcc;
	private double thetaFiltered;
		
	public DataProcessor(Server server){
		this.mServer = server;
		this.acceleration = new DataVect(0, 0, 0, 0);
		this.rate = new DataVect(0, 0, 00, 0);
	}
	
	public void startProcessing(){
		mServer.connect();
		if(mProcessingThread!=null)
			return;
		else{
			if(!mServer.isConnected())
				mServer.connect();
			mProcessingThread = new Thread(new Runnable(){

				@Override
				public void run() {
					if(!mServer.isRunning())
						mServer.startServer();
					isRunning=true;
					while(isRunning){
						String data = mServer.takeElement();
						if(data!=null){
							process(data);							
								
						
						}
					}
					
				}
				
			});
			mProcessingThread.start();
		}
		
	}
	
	private void process(String data){
		//parse
		String[] dataArray = new String[7];
		dataArray=data.split(" ");
		if(dataArray.length!=7)
			return;
		acceleration.setX(Float.parseFloat(dataArray[0]));
		acceleration.setY(Float.parseFloat(dataArray[1]));
		acceleration.setZ(Float.parseFloat(dataArray[2]));
		rate.setX(Float.parseFloat(dataArray[4]));
		rate.setX(Float.parseFloat(dataArray[5]));
		rate.setX(Float.parseFloat(dataArray[6]));
		//prepare measure vector
		thetaAcc = -1 * Math.atan2(acceleration.getX(),acceleration.getZ()); yooo sprawdz czy siê zgadza znak
		
		
		
	}
	
	private class DataVect{
		private float x,y,z;
		private float time;
		public DataVect(float x, float y,float z, float t){
			this.x=x;
			this.y=y;
			this.z=z;
			this.time=t;
		}
		public float getX() {
			return x;
		}
		public float getY() {
			return y;
		}
		public float getZ() {
			return z;
		}
		public float getTime(){
			return time;
		}
	
		public void setX(float x) {
			this.x = x;
		}
		public void setY(float y) {
			this.y = y;
		}
		public void setZ(float z) {
			this.z = z;
		}
		public void setTime(float t){
			this.time = t;
		}
		public void setVector(float x, float y,float z, float t){
			this.x=x;
			this.y=y;
			this.z=z;
			this.time=t;
		}
		
		
		
		
		
	}
}
