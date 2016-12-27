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

import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageCompression {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        BufferedImage image = null;       
        try {
        image = ImageIO.read(new File("image.jpg"));
        } catch (IOException e) {
            System.out.println("The image could not be read.");        
        }
        int rows = image.getWidth();
        int columns =image.getHeight();        
        int N=8;
        Compression dct = new Compression(0);
        int[][] pixels = new int[rows][columns];
        double[][] databuffer = new double[N][N];
        double[][] temp = new double[N][N];
        int[][]  output= new int[rows][columns];
        int[][] quantizedOutput = new int[N][N];
        int[][] dequantized = new int[N][N];
        int[][] recovered = new int[N][N];
        
        
        BufferedImage imageOutput = new BufferedImage(rows, columns, TYPE_BYTE_GRAY);              
        WritableRaster wr = image.getRaster();
        WritableRaster wr2 = imageOutput.getRaster();
       
 
        //Get the pixels values of the image
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                pixels[i][j] = wr.getSample(i, j, 0);
            }
              
        }
   
        long startTime = System.currentTimeMillis();

     
        for (int i = 0; i < rows; i=(i+8)) {
            for (int j = 0; j < columns; j=(j+8)) {
                
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
        
        long endTime = System.currentTimeMillis();
        
        for (int k = 0; k < rows; k++) {
            for (int l = 0; l < columns; l++) {
                wr2.setSample(k, l, 0, output[k][l]);
            }
        }
                 
        File outputfile = new File("output.jpg");
        ImageIO.write(imageOutput, "jpg", outputfile);
        
        System.out.println("Calculated in " +
                             (endTime - startTime) + " milliseconds");
    }
    
}
