package com.jms.software.inertial;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class InertialMouseServer {

	
	
	public static void main(String[] args) {
		final Server serv =new Server(8888, 20);
		final DataProcessor processor = new DataProcessor(serv);
		processor.startProcessing();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("data.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
			double data = processor.takeTheta();
			if(data!=-125125)
				writer.println(processor.takeTheta());
		}
		
	}

}
