package com.jms.software.inertial;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.lang.Math;

public class DataProcessor {
	
	
	private Server mServer;
	private KalmanFilter filter;
	private Thread mProcessingThread;
	private boolean isRunning = false;
	private DataVect acceleration, rate;
	private double thetaAcc;
	private double[][] thetaFiltered;
	private double[][] A;
	private double[][] H;
	private double[][] Q;
	private double[][] R;
	private double dt;
	private LinkedBlockingDeque<Double> theta, bias;
		
	public DataProcessor(Server server){
		this.mServer = server;
		this.acceleration = new DataVect(0, 0, 0, 0);
		this.rate = new DataVect(0, 0, 00, 0);
		dt = 0.020;
		this.A = new double[][]{
				{1, dt, -dt},
				{0, 1, 0},
				{0, 0, 1}
				};
		this.H = new double[][]{
				{1, 0, 0},
				{0, 1, 0}
				};
		this.Q = new double[][]{
				{0.0008, 0, 0},
				{0, 0.001, 0},
				{0, 0, 0.001}
				};
		this.R = new double[][]{
				{0.09, 0},
				{0, 0.05}
				};
		try {
			filter = new KalmanFilter(3, 2, 0, A, new double[1][1], H, Q, R);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		theta = new LinkedBlockingDeque<Double>();
		bias = new LinkedBlockingDeque<Double>();
//		thetaFiltered = new double[3][1];
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
					mServer.stopServer();
				}
				
			});
			mProcessingThread.start();
		}
		
	}
	
	public void stopProcessing(){
		isRunning = false;
	}
	public double takeTheta(){
		try {
			return theta.takeLast();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -125125;
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
		thetaAcc = -1 * Math.atan2(acceleration.getX(),acceleration.getZ());
		//kalmanize
		double[][] z = new double[][]{
									{thetaAcc},
									{rate.getX()}
									};
		thetaFiltered = filter.kalmanIteration(z, new double[][]{{0}});
		//add result to queue
		theta.addFirst(thetaFiltered[0][0]);
		bias.addFirst(thetaFiltered[0][2]);
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
