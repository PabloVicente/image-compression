/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parallelcompression;

import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Pablo
 * 
 *       
 */
public class ParallelCompresion {

    /**
     * @param args the command line arguments
     *         
     */
    
    final static int P = 4;
    
        
    public static void main(String[] args) throws Exception {        
        // TODO code application logic here
        
        
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("medium.jpg"));
        } catch (IOException e) {
            System.out.println("Image not found!");
        }        
        int rows = image.getWidth();
        int columns =image.getHeight();  
               
        BufferedImage imageOutput = new BufferedImage(rows, columns, TYPE_BYTE_GRAY);              
        WritableRaster wr = image.getRaster();
        WritableRaster wr2 = imageOutput.getRaster();
               
        int[][] pixels = new int[rows][columns];
        int me,temp;

        if(rows%8!=0 || columns%8!=0)
          System.out.println("The dimension is not valid.");
        else if((rows/8)%P != 0)
          System.out.println("The number of processes must be a multiple of 8.");
        else
        {
        
        //Obtenemos el valor en escala de grises para toda la imagem
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                //Almacenamos el color del píxel
                pixels[i][j] = wr.getSample(i, j, 0); 
            }
        }
   
        long startTime = System.currentTimeMillis();

        Threads [] threads = new Threads [P] ;
        for( me = 0 ; me < P ; me++) {
            threads [me] = new Threads(me, rows,columns,pixels,P);
            threads [me].start() ;
        }

        for( me = 0 ; me < P ; me++) {
            threads[me].join();
        }
       
        long endTime = System.currentTimeMillis();
        
        
        me=0;      
        temp=threads[me].b;
        
        for (int i = 0; i < rows; i++) {
           if(i == temp){
               me++;
               temp+=threads[me].b;
           }
              for (int j = 0; j < columns; j++) {       
                  wr2.setSample(i, j, 0, threads[me].output[i-me*threads[me].b][j]);
             }           
          }
        
        
        File outputfile = new File("output.jpg");
        ImageIO.write(imageOutput, "jpg", outputfile);
        
        System.out.println("Calculated in " +
                             (endTime - startTime) + " milliseconds");
        
        }
    }
     
    
}

