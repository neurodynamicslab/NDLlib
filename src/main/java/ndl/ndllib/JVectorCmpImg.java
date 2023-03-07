/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ndl.ndllib;
/**
 *
 * @author balam
 */

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.process.*;
import java.io.File;

public class JVectorCmpImg {
    
    private ImageProcessor[]  heatMapImg;
    //private JVectorSpace vectors;
    
    int xRes, yRes;
    
    public JVectorCmpImg(int xRes, int yRes, int nComps ){
        
        heatMapImg = new FloatProcessor[nComps];
        this.xRes = xRes;
        this.yRes = yRes;
        
       // vectors = null;
        
    }
    public JVectorCmpImg(JVectorSpace vectors){
        heatMapImg = new FloatProcessor[vectors.getnComp()];
        setVectors(vectors);
    }

    private void setVectors(JVectorSpace vectors1) {
        //this.vectors = vectors1;
        int cmp = vectors1.getnComp();
     
        this.xRes = vectors1.getxRes();
        this.yRes = vectors1.getyRes();  
        for (int compCount = 0; compCount < cmp; compCount++) {
            heatMapImg[compCount] = new FloatProcessor(xRes, yRes, vectors1.getCompArray(compCount));
        }
    }
    public void addScalar(JHeatMapArray hmaps){
        int count = 0;
        
                if (xRes != hmaps.getxRes() || yRes != hmaps.getyRes()){     //skip the arrays whose res do not match. 
                    heatMapImg[0] = null;
                    javax.swing.JOptionPane.showConfirmDialog(null,"Resolution mismatch selcted resolution "+xRes +" x " + yRes +
                                                                            "matrix resolution"+ hmaps.getxRes()+ " x " + hmaps.getxRes());
                }
                else{
                    hmaps.convertTimeSeriestoArray(xRes, yRes);
                    heatMapImg[0] = new FloatProcessor(xRes,yRes,hmaps.to1DArray());
                }
                count++;
            
        
    }
    public void saveImages(String folderPath,String prefix){
       
       ImagePlus [] images = getImages();
       FileSaver fs;
       int nCmps = images.length;
       int nDigits = (int) Math.log10(nCmps);
       int curNumber = 0;
       File folder = new File(folderPath);
       if(!folder.exists())
              folder.mkdirs();
       String no="";
       int nFil ;
       for(ImagePlus imp : images){
          //fs = new FileSaver(imp); 
          //fs.saveAsTiff(folderPath+File.separator+prefix+"_Comp");
          nFil = nDigits - (int)Math.log10(curNumber);
          while(nFil > no.length())
              no = no + "0";
          IJ.saveAsTiff(imp, folderPath+File.separator+prefix+"_Comp"+no+curNumber);
          curNumber++;
          //javax.swing.JOptionPane.showMessageDialog(null, "The file save is : "+folderPath+File.separator+prefix+"_Comp");
       }  
       
    }
    public void saveStack(String filename){
        FileSaver fs;
        fs = new FileSaver(new ImagePlus("",getImageStack()));
        fs.saveAsRawStack(filename);
    }
    public ImageStack getImageStack(){
        ImageStack stk;
        stk = new ImageStack(xRes,yRes,heatMapImg.length);
        for(ImageProcessor ip : heatMapImg)
            stk.addSlice(ip);
        return stk;
    }
    public void addVectors(JVectorSpace vectors){
        setVectors(vectors);
    }
    public ImagePlus [] getImages(){
        ImagePlus [] Images = new ImagePlus[heatMapImg.length];
        int count =0;
        for(ImageProcessor ip : heatMapImg){
            Images[count] = new ImagePlus("Cmp"+count,ip);
            count++;
        }
        return Images;
    }
    public ImageProcessor [] getProcessorArray(){
        return heatMapImg;
    }
}
