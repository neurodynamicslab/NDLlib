/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Balaji
 */
package ndl.ndllib;

import java.util.*;
/*import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.CurveFitter;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.*;*/



public class DataTrace_ver1 extends ArrayList<OrdXYData>{
    
      
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
    /** Binning Data
     * 
     */
    boolean binInY = true;
    double binWnd  = 0;
    Iterator dataIterator;
    
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
    public DataTrace_ver1(){
        //rawData = new ArrayList<OrdXYData>();
        //dataIterator = rawData.iterator();
        
    }
   public <B extends Number>DataTrace_ver1(int datalength, B binWidth, boolean binInX){
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
                                             // But this ensures modularity. If any change to way we add elements we need to 
                                             // modify one function and in one place i.e addData()              
            }
        }
    }
   
   public <X extends Number,Y extends Number> void addData(X xData,Y yData){  
       DataLength++;
       OrdXYData <X , Y> dataPt = new OrdXYData(DataLength,xData,yData);
                this.add(dataPt);
        
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
   * Differentiates the trace data and generates the differential of current data
   * will overwrite the current data with the differentiated data
   */
  public void differentiate(){
      differentiate(true);
  }
  /**
   * Differentiate the trace data and return the float array. 
   * if Overwrite is true then the current data will be replaced by the differentiated data
   * @param Overwrite
   * @return 
   */
  public DataTrace_ver1 differentiate(boolean Overwrite){
      DataTrace_ver1 difData = new DataTrace_ver1();
      //difData = null;
      return difData;
  }
  
  
  
  public <X extends Number, Y extends Number> DataTrace_ver1 binData(double binWidth, boolean binInX, boolean restoreSeq){
      
     DataTrace_ver1 binnedData = new DataTrace_ver1();
     this.sortData(binInX);
     double binStart = (double)((binInX) ? this.get(0).getX() : this.get(0).getY());
     double binEnd = binStart + binWidth;
     double halfbinWidth = binWidth/2;
     double binCtr = binStart + halfbinWidth;
     double sum = (this.get(0).getY()).doubleValue();
     int count = 1;
     
     for(OrdXYData data : this){
        
         double curX = ((double)data.getX());
         double curY = ((double)data.getY());
         
         if( curX >= binStart && curX < binEnd){
             sum += curY;
             count++;
         }else{
             double yData = sum/count ;
             binnedData.addData(binCtr,(sum/count));
             sum = curY;
             count = 1;
             binStart = curX;
             binCtr = binStart + halfbinWidth;
             binEnd = binStart + binWidth;
         }
     }
      
      return binnedData;
  }
  public<X extends Number, Y extends Number> DataTrace_ver1 sortXYData(DataTrace_ver1 XYData,boolean inX){
        
        X[] x = (X [])(XYData.getX().toArray());
        Y[] y = (Y [])(XYData.getY().toArray());
        
        DataTrace_ver1 sortedData = new DataTrace_ver1();
               
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

}
