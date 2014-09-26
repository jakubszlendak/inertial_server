package com.jms.software.inertial;

public class InertialMouseServer {

	public static void main(String[] args) {
		final Server serv =new Server(8888, 20);
		serv.connect();
		serv.startServer();
		String data;
		Thread t= new Thread(new Runnable(){

			@Override
			public void run() {
				
				while(true){
					if(serv.isConnected())
						System.out.println("Receivd: "+serv.popElement());
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		});
		t.start();

	}

}
