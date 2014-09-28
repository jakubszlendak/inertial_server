package com.jms.software.inertial;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

public class DataProcessor {
	private DataVect mVelocity;
	private DataVect mPosition;
	
	private LinkedBlockingDeque<DataVect> mVelocityBuffer;
	private LinkedBlockingDeque<DataVect> mPositionBuffer;
	
	private Server mServer;
	private Thread mProcessingThread;
	private boolean isRunning = false;
	
	private float prevTimeV = 0.0f;
	private float prevTimeX = 0.0f;
	
	public DataProcessor(Server server){
		mVelocity = new DataVect(0,0,0,0);
		mPosition = new DataVect(0,0,0,0);
		this.mServer = server;
		mVelocityBuffer = new LinkedBlockingDeque<DataVect>();
		mPositionBuffer = new LinkedBlockingDeque<DataVect>();
		
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
					
					/*TODO shit content*/
					FileWriter fw=null;
					try {
						fw = new FileWriter("out.txt");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BufferedWriter out = new BufferedWriter(fw);
					
					/*end of shit*/
					isRunning=true;
					while(isRunning){
						String data = mServer.takeElement();

						//System.out.println("Processor received from server: "+data);
						if(data!=null){
							data = data.replaceAll(",", ".");
							String[] split = data.split(" ");
							if(split.length==4){
								float[] dataNumeric = new float[4];
								for(int i=0; i<split.length; i++){
									dataNumeric[i]=Float.parseFloat(split[i]);
								}
								
								DataVect vect = new DataVect(dataNumeric[0], dataNumeric[1], dataNumeric[2], dataNumeric[3]);
								if(prevTimeV==0.0)
									prevTimeV=vect.getTime();
								if(prevTimeX==0.0)
									prevTimeX=vect.getTime();
								integrate(vect, mVelocity, prevTimeV);
								prevTimeV=vect.getTime();
								integrate(mVelocity, mPosition, prevTimeX);
								prevTimeX=vect.getTime();
								mVelocityBuffer.addFirst(mVelocity);
								mPositionBuffer.addFirst(mPosition);
//								try {
									System.out.println(String.format("%3.3f %3.3f %3.3f, %3.3f", vect.getTime(), vect.getX(), mVelocity.getX(), mPosition.getX()));
//								} catch (IOException e) {
									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
								
								
							}
						}
					}
					
				}
				
			});
			mProcessingThread.start();
		}
		
	}
	private void integrate(DataVect vect, DataVect result, float prevTime){
		float deltaT = vect.getTime() - prevTime;
		System.out.println("dT= "+deltaT);
		float x = result.getX();
		float y = result.getY();
		float z = result.getZ();
		result.setX(x+(vect.getX()*deltaT));
		result.setY(y+(vect.getY()*deltaT));
		result.setZ(z+(vect.getZ()*deltaT));
		result.setTime(vect.getTime());
		
		
		
	}
	public float getResultV(){
		//TODO remove this shit
		return mVelocityBuffer.pollLast().getX();
	}
	public float getResultX(){
		//TODO remove this shit
		return mPositionBuffer.pollLast().getX();
	}
	
	public float getResultTime(){
		return mPositionBuffer.peekLast().getTime();
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
