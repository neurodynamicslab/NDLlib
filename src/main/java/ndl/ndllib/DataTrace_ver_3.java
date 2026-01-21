/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**Edited on 27th Apr2022, BJ
 * A class for storing X, Y data along with error bars. The class is defined using java generics so that it can accept any numeric such as byte,int,long float or double 
 * as input data. This class is written as an extension to OrdXYData which stores just the data without error bars.
 * on 01st May, 2022 BJ: Added method to return the array of X values or y values
 * 14th May 2022: Added a member function populateData(string fName)to populate the raw data directly from file. 
 * @author Balaji
 */

package ndl.ndllib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import java.util.*;
/*import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.CurveFitter;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import java.util.*;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.*;*/

/*class OrdXYData<X extends Number, Y extends Number> extends Object{
    
    int serialNo;
    X xDataPt;
    Y yDataPt;
    public OrdXYData(int serial, X x, Y y){
        xDataPt = x;
        yDataPt = y;
        serialNo = serial; 
    }
    public  X getX(){
        return xDataPt;
    }
    public Y getY(){
        return yDataPt;
    }
    public ArrayList<? extends Number> getXY(){
        ArrayList dataArray = new ArrayList(2);
        dataArray.add(xDataPt);
        dataArray.add(yDataPt);
        return dataArray;
    }
    public int getSerial(){
        return serialNo;
    }
}*/

/**
 * This class built around arrayList to mimic a container class with x - axis and y - axis values along with 
 * Error bars. Elements of the list are objects of the class OrdXYErrData. The class can be used to perform basic operations
 * of binning, derivative and adjacent averaging (yet to be implemented as 01st May, 2022). 
 * @author balam
 */
public class DataTrace_ver_3 extends ArrayList<OrdXYErrData>{

    /**
     * Call this function to read the data directly from text file.The data is supposed to be in the format of x and y co-ordinates listed in a time series stored as text(ascii) file.(i.e.x1 \t y1 \n x2 \t y2\n....EOF).x1,y1 co -respond to co-ordinates at time t1, x2, y2 at time t2.tn+1 is the time sample immediately after tn of an uniformly sampled data.
     * The above is an example for data with no error bars (nDataSegments = 2), when the error bar is present then
 format would be (i.e. x1 \t y1 \t xEr \t yEr \t nPts \n ...EOF). xEr, yEr are the error bars and nPts are the 
 number points over which the mean is calculated. 
     * @param fileName is the name of text file from which to read the data.
     * @param separator is the data separator used in the text file to separate x,y, xerr, yerr and no of points.     
     * @param lineSeparator "\n" separates next line.
     * @param nDataSegments is the number of data segments present in a line
     * @param SD set it to true if the error bars represent standard deviation ignored if no error bars are used
     * @return returns true if import of data from file is successful false otherwise.
     */
   
    public boolean populateData(String fileName, String separator,char lineSeparator, int nDataSegments, boolean SD){ 
        FileReader reader;
        boolean warning = false;
        
        if(nDataSegments != 2 && nDataSegments != 5) {     
            javax.swing.JOptionPane.showMessageDialog(null, "Data format mismatch I can not understand the format I see " + nDataSegments +" segments");
            return false;
        }
          File dataFile = new File(fileName);
        try {  
            reader = new FileReader(dataFile);
            
            double xData ;//Double.parseDouble(dataSeg[0]);
            double yData ; //Double.parseDouble(dataSeg[1]);
            double xErr ; //Double.parseDouble(dataSeg[2]);
            double yErr ; //Double.parseDouble(dataSeg[3]);
            int nPts ; //Integer.parseInt(dataSeg[4])
            String currLine = "";
            String [] dataSeg;
            int c;
            System.out.println( " #Separator is " + Integer.toHexString(separator.toCharArray()[0]));
            switch (nDataSegments){
                case 2:
                    while ( (c = reader.read()) != -1){
                        if ( c != lineSeparator){
                            currLine += (char)c;
                        }else{
                            dataSeg = currLine.split((""+separator));
                            if(dataSeg.length != nDataSegments){
                                char sepC = separator.charAt(0);
                                System.out.print( " Separator is " + Integer.toHexString(sepC) + " Error reading data: Format mismatch "+ currLine );
                                return false;
                            }
                            xData = Double.parseDouble(dataSeg[0]);
                            yData = Double.parseDouble(dataSeg[1]);
                            
                            this.addData(xData, yData);
                            currLine = "";
                        }
                    }
                    break;
                case 4:
                    while ((c = reader.read()) != -1){
                        if (c != '\n'){
                            currLine += (char)c;                           
                        }else{
                            dataSeg = currLine.split("\t");
                            
                            xData = Double.parseDouble(dataSeg[0]);
                            yData = Double.parseDouble(dataSeg[1]);
                            xErr = Double.parseDouble(dataSeg[2]);
                            yErr = Double.parseDouble(dataSeg[3]);
                            nPts = Integer.parseInt(dataSeg[4]);
                            
                            this.addData(xData, yData, xErr, yErr, SD, nPts);
                            currLine = "";
                        }
                    }
                    break;
                default :
                    warning = true;
                    break; 
            }
            
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(DataTrace_ver_inwrks.class.getName()).log(Level.SEVERE, null, ex);
            //javax.swing.JOptionPane.showMessageDialog(null, " Not able to find the file : "+fileName);
            System.out.println( " Not able to find the file : "+fileName);
            return false;
            
        }
        catch (IOException ex){
            //javax.swing.JOptionPane.showMessageDialog(null, " Not able to read the file : "+fileName);
            System.out.println( " File found but not able to read the file : "+fileName);
            return false;
        }     
        if(warning)
            //javax.swing.JOptionPane.showMessageDialog(null,"Warning: The number data elements do not match");
            System.out.println("Warning: Unrecogonised charecter ignored");
        return true;
    }
    /**Overloaded function of populateData() with data separator set to '\t' and line separator set to '\n' and
     * data segment number is set to 2. 
     * @param fileName : Name of the text file with ascii data.
     * @return :True when data is successfully read false when it fails
     */
     public boolean populateData(String fileName){
        return populateData(fileName,2,false);
    }
     /**Overloaded function of populateData() with with data separator set to '\t' and line separator set to '\n'.
      * 
      * @param fileName : Name of the text file with the ascii data.
      * @param nDataSegments : Number of data segments  2 for x and Y , 5 for x, y, xerr, yerr, number of points.
      * @param SD : True for error bar being standard deviation and false for SEM
      * @return :True when data is successfully read false when it fails
      */  
    public boolean populateData(String fileName,int nDataSegments,boolean SD){
        return populateData(fileName, ""+'\t','\n',nDataSegments,SD);
    }
    public DataTrace_ver_3 normalise(int choice){
        
        DataTrace_ver_3 NData = new DataTrace_ver_3();
        switch(choice){
            case 1:     //Norm Y  maximum to 1
                double maximum = this.getYMax();
                double minimum = this.getYMin();
                double range = maximum - minimum;
                for(var curData : this){
                   var dataY =  (curData.getY().doubleValue() - minimum)/range;
                   NData.addData(curData.getX(), dataY);
                }
            break;
            case 2:
            case 3:
            default :
                
        }
        return NData;
    }
    /**
     * @return the xRoundoff
     */
    public boolean isxRoundoff() {
        return xRoundoff;
    }

    /**
     * @param xRoundoff the xRoundoff to set
     */
    public void setxRoundoff(boolean xRoundoff) {
        this.xRoundoff = xRoundoff;
    }

    /**
     * @return the yRoundoff
     */
    public boolean isyRoundoff() {
        return yRoundoff;
    }

    /**
     * @param yRoundoff the yRoundoff to set
     */
    public void setyRoundoff(boolean yRoundoff) {
        this.yRoundoff = yRoundoff;
    }

    /**
     * @return the LXLimit
     */
    public double getLXLimit() {
        return LXLimit;
    }

    /**
     * @param LXLimit the LXLimit to set
     */
    public void setLXLimit(double LXLimit) {
        this.LXLimit = LXLimit;
    }

    /**
     * @return the LYLimit
     */
    public double getLYLimit() {
        return LYLimit;
    }

    /**
     * @param LYLimit the LYLimit to set
     */
    public void setLYLimit(double LYLimit) {
        this.LYLimit = LYLimit;
    }

    /**
     * @return the HXLimit
     */
    public double getHXLimit() {
        return HXLimit;
    }

    /**
     * @param HXLimit the HXLimit to set
     */
    public void setHXLimit(double HXLimit) {
        this.HXLimit = HXLimit;
    }

    /**
     * @return the HYLimit
     */
    public double getHYLimit() {
        return HYLimit;
    }

    /**
     * @param HYLimit the HYLimit to set
     */
    public void setHYLimit(double HYLimit) {
        this.HYLimit = HYLimit;
    }
    
      
    //ArrayList<OrdXYData> rawData;
    
    
    double x_Max = Double.MIN_VALUE;
    double y_Max = Double.MIN_VALUE;
    double x_Min = Double.MAX_VALUE;
    double y_Min = Double.MAX_VALUE;
    double x_Sum = 0;
    double y_Sum = 0;
    int CurrPos = 0;
    int DataLength = 0;
    int ActLength = 0;                       //Needs comment : to say what is the difference between DataLength and ActLength
                                             // Actlength - the number of datapoints that are non zero ?
                                             // Datalength - the capacity of the Data ie) the maximum number of data pts that can be held in the object
    //boolean Y_Only = false;
    private boolean xRoundoff = true;
    private boolean yRoundoff = true;
    private double LXLimit = Double.MIN_VALUE;
    private double LYLimit = Double.MIN_VALUE;
    private double HXLimit = Double.MAX_VALUE;
    private double HYLimit = Double.MAX_VALUE;
    
    private double yScaler = 1.0;
    
    
    /** Binning Data
     * 
     */
    boolean binInY = true;
    double binWnd  = 0;
    
    int typOfBinning = 0;       //typOfBinning = 0 - linear, 1 - ln, 2 - log, 3 - power of 2

    //Iterator dataIterator;
    
    
    
   // ArrayList<Double[]> BinnedData;
    
   //public DataTrace( int length){
       // rawData = new ArrayList<>(length);
        /*if (length > 0){
            DataLength = length;
            xData = new double[DataLength];
            yData = new double[DataLength];
        }
        XData = new ArrayList<>();
        YData = new ArrayList<>();*/
    //}
    public void setScaleType(int stype){
        typOfBinning = stype;
    }
    public int getScaleType(){
        return typOfBinning;    //SCALE = 0 - linear, 1 - ln, 2 - log, 3 - power of 2
    }
    public DataTrace_ver_3(){
        //rawData = new ArrayList<OrdXYData>();
        //dataIterator = rawData.iterator();
        
    }
   public void scaleYaxis(double scaler){
       this.setyScaler(scaler);
       //ArrayList <Number> y = new ArrayList();
       DataTrace_ver_3 scaledData = new DataTrace_ver_3();
       int x,y,count = 0;
       this.forEach((Data) -> {
           Data.scaleY(scaler);
       });
   }
   public <B extends Number>DataTrace_ver_3(int datalength, B binWidth, boolean binInX){
       this.binWnd = binWidth.doubleValue();
       //rawData = new ArrayList(datalength);
       //dataIterator = rawData.iterator();
   }
   public<X extends Number,Y extends Number> void addData( X[] xData, Y[] yData){
        //this.dataIterator = rawData.iterator();
        if( xData != null && yData != null && xData.length == yData.length){          
            int idx = 0;          
            for(X x : xData){
               addData(x,yData[idx++]); // It is  more efficient to add it directly to rawdata instead of calling addData.
                                             // But this ensures modularity. If any change in the way we add elements we need to 
                                             // modify one function and in one place i.e addData()              
            }
        }
    }
   
   public <X extends Number,Y extends Number, Xr extends Number, Yr extends Number> void addData(X xData,Y yData, Xr xError, Yr yError, boolean SD, int nPts){  
       DataLength++;
       OrdXYErrData dataPt = new OrdXYErrData(DataLength,xData,yData,xError,yError,SD,nPts);
                this.add(dataPt);
        
    }
   public <X extends Number, Y extends Number> void addData(X xData,Y yData){
       this.addData(xData, yData, -1, -1, true,-1);
   }
   public <X extends Number,Y extends Number, Xr extends Number, Yr extends Number> void addData(X xData,Y yData, Xr xError, Yr yError, boolean SD){
       
       this.addData(xData, yData, xError, yError, SD,0);
   }
   
 /*public ArrayList getNextXYData(){
      
     
     /*if (dataIterator.hasNext())
        return ((OrdXYData) dataIterator.next()).getXY();
     else 
         return null;*/
 //}
 
   public int getDataLength(){
       return this.size();
   }
   
   public<N extends Number> ArrayList getX(){
       ArrayList <N> x = new ArrayList();    
       this.forEach((Data) -> {
           x.add((N) Data.getX());
        });
       return x;
   }
   public <N extends Number> ArrayList getY(){
      ArrayList <N> y = new ArrayList();
       this.forEach((Data) -> {
           y.add((N) Data.getY());
        });
       return y;
   }
  
   public <N extends Number> ArrayList getXErrs(){
      ArrayList <N> Xerr = new ArrayList();
       this.forEach((Data) -> {
           Xerr.add((N) Data.getXError());
        });
       return Xerr;
   }
    public <N extends Number> ArrayList getYErrs(){
      ArrayList <N> Yerr = new ArrayList();
       this.forEach((Data) -> {
           Yerr.add((N) Data.getYError());
        });
       return Yerr;
   }
    public  ArrayList getNpts(){
      ArrayList <Integer> Pts = new ArrayList();
       this.forEach((Data) -> {
           Pts.add((Integer) Data.getNumberofPoints());
        });
       return Pts;
   }
  
 
   public <X extends Number, Y extends Number> void resetStat(){

        this.forEach((Data)->{
            double x = ((X)Data.getX()).doubleValue();
            double y = ((Y)Data.getY()).doubleValue();
            
            x_Max = (x_Max > x ) ? x_Max : x ;
            y_Max = (y_Max > y ) ? y_Max : y ;

            x_Min = (x_Min < x ) ? x_Min  : x ;
            y_Min = (y_Min < y ) ? y_Min : y ;

            x_Sum += x;
            y_Sum += y;
        });
  };
private <X extends Number, Y extends Number> void setStat(X xData,Y yData){
    
            double x = xData.doubleValue();
            double y = yData.doubleValue();
            
            x_Max = (x_Max > x ) ? x_Max : x ;
            y_Max = (y_Max > y ) ? y_Max : y ;

            x_Min = (x_Min < x ) ? x_Min  : x ;
            y_Min = (y_Min < y ) ? y_Min : y ;

            x_Sum += x;
            y_Sum += y;
  }

  
  public double getXMax(){
      return x_Max;
  }
  public double getYMax(){
      return y_Max;
  }
  public double getXMin(){
      return x_Min;
  }
  public double getYMin(){
      return y_Min;
  }
  public double getXSum(){
      return x_Sum;
  }
  public double getYSum(){
      return y_Sum;
  }
  public double getYPk(){
      return y_Max - y_Min ;
  }
  public double getXPk(){
      return x_Max - x_Min;
  }
  public void resetTrace(){
    this.clear();
  }
  /***
   * Differentiates the trace data and generates the differential of current data.
   * This function will overwrite the current data with the differentiated data
   */
  public void differentiate(){
      differentiate(true);
  }
  
  
  /**
   * Differentiates the trace data and returns the new DataTrace object containing differentiated data. . 
   * If Overwrite is true then the current data will be replaced by the differentiated data
   * @param Overwrite
   * @return  returns the differentiated data as DataTrace_ver_3 object.
   */
  public DataTrace_ver_3 differentiate(boolean Overwrite){
      DataTrace_ver_3 difData = new DataTrace_ver_3();
      //difData = null;
     double prevY = 0.0, currY ;
     double prevX = 0.0, currX ;
     for(OrdXYErrData data : this){
         currY = (double) data.getY();
         currX = (double) data.getX();
         double diffY = currY - prevY;
         double diffX = currX - prevX;
         difData.addData(diffX, diffY);               //At presnt the error bars are not propagated
         prevY = currY;
         prevX = currX;
     }
     difData.remove(0); //delete the head end that does not have a data to find the difference
                        //tail end is taken care of by the loop. The loop terminates.
     if(Overwrite){
         this.clear();
         difData.forEach((data) -> {
             this.addData(data.getX(), data.getY());
          });
         
     }
      return difData;
  }
  
  
  
  public <X extends Number, Y extends Number, Xerr extends Number, YErr extends Number> DataTrace_ver_3 binData(double binWidth, boolean binInX, boolean restoreSeq){
      //SCALE = 0 - linear, 1 - ln, 2 - log, 3 - power of 2
     DataTrace_ver_3 binnedData = new DataTrace_ver_3();
     this.sortData(binInX);
     int binNumber = 1;
     double sbinwidth = binWidth;
     double halfbinWidth = binWidth/2;
     double binStart = (double)((binInX) ? this.get(0).getX() : this.get(0).getY());
     
    /* switch(this.typOfBinning){
                 case 0:
                     break;
                 case 1:
                     binWidth = exp(sbinwidth*binNumber)*(exp(sbinwidth)-1); 
                     halfbinWidth = binWidth/2;
                     break;
                 case 2: 
                     binWidth = pow(10,sbinwidth*binNumber)*(pow(10,sbinwidth)-1); 
                     halfbinWidth = binWidth/2;
                     break;
                 case 3:
                     binWidth = pow(2,sbinwidth*binNumber)*(pow(2,sbinwidth)-1); 
                     halfbinWidth = binWidth/2;
                     break;
                 case 4:
                     binWidth = (binStart/binWidth);
                     System.out.print( "\t binwidths =" + binWidth);
                     halfbinWidth = binWidth/2;
                     break;
             }
     
     */
     
     
     double binEnd = binStart + binWidth;
     
     double binCtr = binStart + halfbinWidth;
     double sum = (this.get(0).getY()).doubleValue();
     
     double sumSq = sum * sum;
     
     int count = 1;
     
     for(OrdXYErrData data : this){
        
         double curX = ((double)data.getX());
         double curY = ((double)data.getY());
         if(curX <= this.getLXLimit()  || curY <= this.getLYLimit() ){
           if( isxRoundoff() || isyRoundoff()){
             if(curX <= this.getLXLimit() ) curX = 0;
             if(curY <= this.getLYLimit()) curY = 0;
           }   
           else
               continue;   
         }
            
         
         if( curX >= binStart && curX < binEnd){
             sum += curY;
             sumSq += (curY*curY);
             count++;
         }else{
             double yData = sum/count ;
             double sem  = pow((sumSq/count) - (yData*yData), 0.5)/pow(count,0.5);
             binnedData.addData(binCtr,(sum/count),0,sem,false,count);
             sum = curY;
             sumSq = sum * sum ;
             count = 1;
             switch(this.typOfBinning){
                 case 0:
                     break;
                 case 1:
                     binWidth = exp(sbinwidth*binNumber)*(exp(sbinwidth)-1); 
                     halfbinWidth = binWidth/2;
                     break;
                 case 2: 
                     binWidth = pow(10,sbinwidth*binNumber)*(pow(10,sbinwidth)-1); 
                     halfbinWidth = binWidth/2;
                     break;
                 case 3:
                     binWidth = pow(2,sbinwidth*binNumber)*(pow(2,sbinwidth)-1); 
                     halfbinWidth = binWidth/2;
                     break;
                 
             }
             binStart = curX;
             binCtr = binStart + halfbinWidth;
             binEnd = binStart + binWidth;
         }
     }
      
      return binnedData;
  }
  public<X extends Number, Y extends Number> DataTrace_ver_3 sortXYData(DataTrace_ver_3 XYData,boolean inX){
        
        X[] x = (X [])(XYData.getX().toArray());
        Y[] y = (Y [])(XYData.getY().toArray());
        
        DataTrace_ver_3 sortedData = new DataTrace_ver_3();
               
        sortedData.addData(x, y);
        
        sortedData.sortData(inX);
      return sortedData;
  }
  private void sortData(boolean inX){
      compareXofXYData xCmp = new compareXofXYData();
      compareYofXYData yCmp = new compareYofXYData();
      this.sort((inX) ? xCmp : yCmp);
  }
  private void resetOrder(){
      compareSerialofXYData cmp = new compareSerialofXYData();
      this.sort(cmp);
  }
  class compareXofXYData implements Comparator<OrdXYData>{
      
        @Override
        public int compare(OrdXYData t, OrdXYData t1) {
            return Double.compare(t.getX().doubleValue(), t1.getX().doubleValue());
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      
  }
  class compareYofXYData implements Comparator<OrdXYData>{
      @Override
      public int compare(OrdXYData t, OrdXYData t1) {
            return Double.compare(t.getY().doubleValue(), t1.getY().doubleValue());
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      
  }
  class compareSerialofXYData implements Comparator<OrdXYData>{
      @Override
      public int compare(OrdXYData t, OrdXYData t1){
          return Integer.compare(t.getSerial(), t1.getSerial());
      }
  }

    /**
     * @return the yScaler
     */
    public double getyScaler() {
        return yScaler;
    }

    /**
     * @param yScaler the yScaler to set
     */
    public void setyScaler(double yScaler) {
        this.yScaler = yScaler;
    }

}
