/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parallelcompression;


/**
 *
 * @author Pablo
 */
public class Threads extends Thread{
    
    int me;
    int b;
    int P;
    int rows, columns;
    final static int N=8;
    int[][] pixels;
    int[][] output;
    
    public Threads(int me,  int rows, int columns, int p[][], int P) {
        this.me=me;
        this.rows=rows;
        this.columns=columns;
        this.P=P;       
        b = rows/P;  // block size
        
        int begin = me * b ;
        int end = begin + b ; 
        pixels = new int[b][columns];
        
        for (int i = begin; i < end; i++) {
            for (int j = 0; j < columns; j++) {
                pixels[i-begin][j] = p [i][j];
            }
        }

        
        
    }
    
    
    @Override
      public void run() {
       
        double[][] databuffer = new double[N][N];
        double[][]  temp= new double[N][N];
        int[][] quantizedOutput = new int[N][N];
        int[][] dequantized = new int[N][N];
        int[][] recovered = new int[N][N];
        output = new int[b][columns];
        Compression dct = new Compression(0);
        
        

        for (int i = 0; i < b; i=(i+8)) {
            for (int j = 0; j < columns; j=(j+8)) {
                
                for (int k = 0; k < N; k++) {
                    for (int l = 0; l < N; l++) {
                        databuffer[k][l] = pixels[(k+i)][(j+l)];
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

          
      }
}
