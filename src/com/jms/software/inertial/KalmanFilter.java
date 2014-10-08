package com.jms.software.inertial;
import Jama.Matrix;

public class KalmanFilter {
	
	private Matrix A;
	private Matrix B;
	private Matrix H;
	
	private Matrix Q;
	private Matrix R;
	
	private double dt;
	
	private Matrix x;
	private Matrix z;
	
	private Matrix P;
	private Matrix I;
	
	private Matrix K;
	
	private int rankX;
	private int rankZ;
	private int rankU;
	/**
	 * 
	 * @param rankX Rank of state vector X
	 * @param rankY Rank of measure vector Z
	 * @param A Transition matrix
	 * @param B Control matrix
	 * @param H 
	 * @param Q
	 * @param R
	 * @throws Exception 
	 */
	public KalmanFilter(int rankX, int rankZ, int rankU, double[][] A, double[][] B, double[][] H, double[][] Q, double[][] R ) throws Exception{
		if(A.length!=rankX)
			throw new Exception("Invalid transition matrix!");
		if(H[0].length!=rankZ)
			throw new Exception("Invalid H matrix!");
		if((Q.length!=rankX && Q[0].length!=rankX) || (R.length!=rankZ && R[0].length!=rankZ))
			throw new Exception("Q and R matrices must be square and of proper rank!");
		
		this.A = new Matrix(A);
		this.B = new Matrix(B);
		this.H = new Matrix(H);
		this.Q = new Matrix(Q);
		this.R = new Matrix(R);
		this.rankX = rankX;
		this.rankZ = rankZ;
		this.rankU = rankU;
		x = new Matrix(rankX, 1, 0.0);
		z = new Matrix(rankZ, 1, 0.0);
		P = new Matrix(rankX, rankX, 0.0);
		I = Matrix.identity(rankX, rankX);		
		
	}
	
	public double[][] kalmanIteration(double[][] Z, double [][] u){
		//prediction
		x = (A.times(x));
		P = (A.times(P)).times(A.transpose()).plus(Q);
		//correction
		K = new Matrix(rankX,1);
		K = (P.times( H.transpose() ) ).times((((H.times(P)).times( H.transpose() )).plus(R)).inverse());
		z = new Matrix(Z);
		x = x.plus(K.times(z.minus(H.times(x))));
		P = (I.minus(K.times(H))).times(P);
		
		return x.getArray();
	}
	
	
	

}
