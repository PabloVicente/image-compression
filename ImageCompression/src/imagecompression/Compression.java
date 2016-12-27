/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package imagecompression;

/**
 *
 * @author Pablo
 */

public class Compression {
    

    // DCT Block Size
     private int N = 8;
    // Image Quality 
     private int QUALITY ;
     // Quantitization Matrix.
     private int Q[][] = new int[N][N];


    /**
     * Creates the quantization matrix.
     * Depending of the value of QUALITY, the compression rate 
     * and the quality will vary.
     *
     */
    public Compression(int QUALITY)
    {
        
        int i;
        int j;

        for (i = 0; i < N; i++)
        {
            for (j = 0; j < N; j++)
            {
                Q[i][j] = (1 + ((1 + i + j) * QUALITY));                
            }
        }   
    }



    /**
     * It applies the DCT to an 8x8 matrix.
     * The result is given in an 8x8 matrix in the 
     * frequency domain.
     * 
     */
    public double[][] dct(double input[][])
    {
        double output[][] = new double[N][N];
        double temp[][] = new double[N][N];
        int i, j;

        for (i = 0; i < N; i++)
        {
            for (j = 0; j < N; j++)
            {               
                temp[i][j] = (input[i][j] - 128.0);                   
            }
           
        }

        
        for (int v=0; v<N; v++)
	for (int u=0; u<N; u++)
	{
		double Cu, Cv, z = 0.0;

                if (v == 0) Cv = 1.0 / Math.sqrt(2.0); else Cv = 1.0;
		if (u == 0) Cu = 1.0 / Math.sqrt(2.0); else Cu = 1.0; 
	         

		for (int y=0; y<N; y++)
		for (int x=0; x<N; x++)
		{
			double s, q;

			s = temp[y][x];

			q = s * Math.cos(((double)(2*x+1) * (double)u * Math.PI)/16.0) *
				Math.cos(((double)(2*y+1) * (double)v * Math.PI)/16.0);

			z += q;
		}

		output[v][u] = 0.25 * Cu * Cv * z;
	}

        return output;
    }

     /**
     * Apply the quantization matrix to an 8x8 matriz.
     * This eliminates the high frequency elements 
     * and reduce the space needed to store the image.
     * 
     */
    
    
    public int[][] quantization(double inputData[][])
    {
        int outputData[][] = new int[N][N];
        double result;

  
            for (int i=0; i<N; i++)
            {
                for (int j=0; j<N; j++)
                {
                    result = inputData[i][j] / Q[i][j];
                    outputData[i][j] = (int)(Math.round(result));
                }
            }
        

        return outputData;
    }
    
    
    /**
     * Multiply a 8x8 matriz for the quantization matrix
     * in order to obtain the dequantized values. 
     *
     * @param inputData
     * @return 
     */
    public int[][] deQuantization(int[][] inputData)
    {


        int outputData[][] = new int[N][N];

        double result;


         for (int i=0; i<8; i++)
         {
                for (int j=0; j<8; j++)
                {
                    result = inputData[i][j] * Q[i][j];
                    outputData[i][j] = (int)(Math.round(result));
                }
            }
        

        return outputData;
    }

  


    /**
     * Apply the inverse DCT to a 8x8 matriz to change the
     * values from the frequency domain to the space domain.
     * 
     */
    public int[][] iDCT(int input[][])
    {
        int output[][] = new int[N][N];
        
        for (int y=0; y<N; y++)
	for (int x=0; x<N; x++)
	{
		double z = 0.0;


		for (int v=0; v<N; v++)
                for (int u=0; u<N; u++)
		{
			double s, q, Cu, Cv;

                        if (v == 0) Cv = 1.0 / Math.sqrt(2.0); else Cv = 1.0;
                        if (u == 0) Cu = 1.0 / Math.sqrt(2.0); else Cu = 1.0; 
	         
			s =  Cu * Cv * input[v][u];

			q = s * Math.cos(((double)(2*x+1) * (double)u * Math.PI)/16.0) *
				Math.cos(((double)(2*y+1) * (double)v * Math.PI)/16.0);

			z += q;
		}
                 
                z=z/4;
                z+=128;
                if (z > 255.0) z = 255.0;
		if (z < 0) z = 0.0;
                
		output[y][x] = (int)z;
	}

        return output;
    }

}

