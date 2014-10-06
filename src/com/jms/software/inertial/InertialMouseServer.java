package com.jms.software.inertial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InertialMouseServer {

	
	
	public static void main(String[] args) {
		final Server serv =new Server(8888, 20);
		final DataProcessor processor = new DataProcessor(serv);
		processor.startProcessing();
		//serv.connect();
		//serv.startServer();
		String data;
		/*Thread t= new Thread(new Runnable(){

			@Override
			public void run() {
				FileWriter fw = null;
				try {
					fw= new FileWriter("out.txt");
					
					
				} catch (IOException e) {}
				
				BufferedWriter out = new BufferedWriter(fw);
				while(true){
					try {
						out.write(String.format("%3.3f %3.3f %3.3f", processor.getResultV(),processor.getResultX(),processor.getResultTime()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					if(serv.isConnected())
//						System.out.println("Receivd: "+serv.popElement());
//					try {
//						Thread.sleep(10);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					
				}
				
			}
			
		});
		t.start();*/

	}

}
