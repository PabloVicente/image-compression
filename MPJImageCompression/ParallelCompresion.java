/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import mpi.* ;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Pablo
 */
public class ParallelCompresion {

     /**
     * @param args the command line arguments
     *         
     */
	 
    final static int N=8;
    static int P, me, B;   
    static int[][] allPixels;  
    static WritableRaster wr2;
	static BufferedImage imageOutput;
		
    public static void main(String[] args) throws Exception {        
        // TODO code application logic here
        MPI.Init(args) ;
         
        int me = MPI.COMM_WORLD.Rank() ;
        int P = MPI.COMM_WORLD.Size() ;
		
        int[] rows = new int [1] ;
	    int[] columns =  new int [1] ;
		
        if(me == 0)
        {
			BufferedImage image = null;
			try {
			image = ImageIO.read(new File("medium.jpg"));
			} catch (IOException e) {
			}        
			rows[0] = image.getWidth();
			columns[0] =image.getHeight();  
               
			imageOutput = new BufferedImage(rows[0], columns[0], TYPE_BYTE_GRAY);              
			WritableRaster wr = image.getRaster();
			wr2 = imageOutput.getRaster();
		
			allPixels = new int[rows[0]][columns[0]];
			
			for (int i = 0; i < rows[0]; i++)
			{
                for (int j = 0; j < columns[0]; j++)
                {
                    allPixels[i][j] = wr.getSample(i, j, 0);
                }
            }
            
			//Send rows and columns to the other threads
		    for(int src=1; src<P; src++)
			{
				MPI.COMM_WORLD.Send(rows, 0, 1, MPI.INT, src, 0) ;
				MPI.COMM_WORLD.Send(columns, 0, 1, MPI.INT, src, 0) ;
			}
        
        }
        if(me > 0)
		{
        MPI.COMM_WORLD.Recv(rows, 0, 1, MPI.INT, 0, 0) ;
        MPI.COMM_WORLD.Recv(columns, 0, 1, MPI.INT, 0, 0) ;
		
        }
	
        int B = rows[0] / P ;
        int begin = me * B ;
        int end = begin + B ;
   
        int[][] pixels = new int[B][columns[0]];
        int[][] output = new int[B][columns[0]];
        double[][] databuffer = new double[N][N];
        int[][] quantizedOutput = new int[N][N];
        int[][] dequantized = new int[N][N];
        int[][] recovered = new int[N][N];
        double temp[][] = new double[N][N];
   
        Compression dct = new Compression(0);
        
      
		
        if(me==0) 
        {
			for(int i=0; i<B; i++)
			{
			for (int j = 0; j < columns[0]; j++)
                {
			   pixels[i][j] = allPixels[i][j];
			   }
			}
			
			int src=1;
			for(int i=B ; i<rows[0] ; i=i+B)
			{
            MPI.COMM_WORLD.Send(allPixels, i, B, MPI.OBJECT, src, 0) ;
			src++;
            }			
        }
        if(me > 0) 
		{
        MPI.COMM_WORLD.Recv(pixels, 0, B, MPI.OBJECT, 0, 0) ; 
		}
		
       long startTime = System.currentTimeMillis();
       

       for (int i = 0; i < B; i=(i+8)) {
            for (int j = 0; j < columns[0]; j=(j+8)) {
                
                for (int k = 0; k < N; k++) {
                    for (int l = 0; l < N; l++) {
                      databuffer[k][l] = (double) pixels[(k+i)][(j+l)];
                    }
                }
	
                    temp = dct.dct(databuffer);
                    quantizedOutput = dct.quantization(temp); 
                    dequantized = dct.deQuantization(quantizedOutput);      
                    recovered = dct.iDCT(dequantized);
                                         
                 for (int k = 0; k < N; k++) {
                   for (int l = 0; l < N; l++) {
                       output[(k+i)][(j+l)] = recovered[(k)][(l)];
                    }
                }
 
            }
           
        }

        if(me > 0) {
				MPI.COMM_WORLD.Send(output, 0, B, MPI.OBJECT, 0, 0) ; 
        }
        else {
		  
          for(int i = begin; i < end ; i++) {
             for(int j = 0 ; j < columns[0] ; j++) {
                allPixels[i-begin][j] = output[i][j];
              }
            }
			
			for(int src = 1 ; src < P ; src++) {
             MPI.COMM_WORLD.Recv(allPixels, src * B, B, MPI.OBJECT, src, 0) ;
           }  
        }
        
        long endTime = System.currentTimeMillis();
      
        if(me==0) {

        for (int i = 0; i < rows[0]; i++) {
              for (int j = 0; j < columns[0]; j++) {                
                  wr2.setSample(i, j, 0, allPixels[i][j]);
             }           
          }
        
        
        File outputfile = new File("output.jpg");
        ImageIO.write(imageOutput, "jpg", outputfile);
        
        System.out.println("Calculated in " +
                             (endTime - startTime) + " milliseconds");
        }
        MPI.Finalize() ;
    
        }
    
}

