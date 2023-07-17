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
/**
 * This class is an abstraction of XY lab datapoint. Class has been written using generics (Number) so
 * that it can be used with wide range of numeric datatypes (e.g. float,double,int,long or any class
 * that is derived from Number.  In a typical tabular data of the format with column title
 * S.No | (X) Data | (Y) Data : This class represents one line in such a table. The corresponding
 * fields are serialNo, X, Y.
 * 
 * @author balaji
 * @param <X> numeric data corresponding to X (any datatype that is derived from Number)
 * @param <Y> numeric data corresponding to X (any datatype that is derived from Number)
 */

class OrdXYData_1<X extends Number, Y extends Number> extends Object{
    
    int serialNo;                       //Place holder for storing the serial number of the datapoint. 
    X xDataPt;                          
    Y yDataPt;
    /**
     * The constructor
     * @param serial
     * @param x
     * @param y 
     */
    public OrdXYData_1(int serial, X x, Y y){
        xDataPt = x;
        yDataPt = y;
        serialNo = serial; 
    }
    /**
     * The member function that returns the field X
     * @return X (Number)
     */
    public  X getX(){
        return xDataPt;
    }
     /**
     * The member function that returns the field Y
     * @return Y (Number)
     */
    public Y getY(){
        return yDataPt;
    }
    /**
     * Member function that returns both X and Y Data in the 
     * format of members of an array list. 
     * @return ArrayList of size 2 with X (@ index = 0) data followed by Y (@ index = 1)
     */
    public ArrayList<? extends Number> getXY(){
        ArrayList dataArray = new ArrayList(2);
        dataArray.add(xDataPt);
        dataArray.add(yDataPt);
        return dataArray;
    }
     /**
     * The member function that returns the field serialNo
     * @return serial number (int)
     */
    public int getSerial(){
        return serialNo;
    }
}

public class DataTrace extends Object{
    
      
    ArrayList<OrdXYData> rawData;
    
    
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
   public DataTrace(){
        rawData = new ArrayList<OrdXYData>();
        dataIterator = rawData.iterator();
        
    }
   public <B extends Number>DataTrace(int datalength, B binWidth, boolean binInX){
       this.binWnd = binWidth.doubleValue();
       rawData = new ArrayList(datalength);
       dataIterator = rawData.iterator();
   }
   public<X extends Number,Y extends Number> void addData( X[] xData, Y[] yData){
        this.dataIterator = rawData.iterator();
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
                rawData.add(dataPt);
        
    }
   
   public ArrayList getNextXYData(){
     if (dataIterator.hasNext())
        return ((OrdXYData) dataIterator.next()).getXY();
     else 
         return null;
 }
 
   public int getDataLength(){
       return rawData.size();
   }
   public<N extends Number> ArrayList getX(){
       ArrayList <N> x = new ArrayList();    
       rawData.forEach((Data) -> {
           x.add((N) Data.getX());
        });
       return x;
   }
   public <N extends Number> ArrayList getY(){
      ArrayList <N> y = new ArrayList();
       rawData.forEach((Data) -> {
           y.add((N) Data.getY());
        });
       return y;
   }
  
  
 
  public <X extends Number, Y extends Number> void resetStat(){

        rawData.forEach((Data)->{
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
    rawData.clear();
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
   * @param Overwrite type boolean set this to true to overwrite the current data with differentiated data
   * @return returns the differentiated data 
   */
  public DataTrace differentiate(boolean Overwrite){
      DataTrace difData = new DataTrace();
      //difData = null;
      return difData;
  }
  
  
  
  public <X extends Number, Y extends Number> DataTrace binData(double binWidth, boolean binInX, boolean restoreSeq){
      
     DataTrace binnedData = new DataTrace();
     this.sortData(binInX);
     double binStart = (double)((binInX) ? rawData.get(0).getX() : rawData.get(0).getY());
     double binEnd = binStart + binWidth;
     double halfbinWidth = binWidth/2;
     double binCtr = binStart + halfbinWidth;
     double sum = binStart;
     int count = 1;
     
     for(OrdXYData data : rawData){
        
         double curX = ((double)data.getX());
         double curY = ((double)data.getX());
         
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
  public<X extends Number, Y extends Number> DataTrace sortXYData(DataTrace XYData,boolean inX){
        
        X[] x = (X [])(XYData.getX().toArray());
        Y[] y = (Y [])(XYData.getY().toArray());
        
        DataTrace sortedData = new DataTrace();
               
        sortedData.addData(x, y);
        
        sortedData.sortData(inX);
      return sortedData;
  }
  private void sortData(boolean inX){
      compareXofXYData xCmp = new compareXofXYData();
      compareYofXYData yCmp = new compareYofXYData();
      this.rawData.sort((inX) ? xCmp : yCmp);
  }
  private void resetOrder(){
      compareSerialofXYData cmp = new compareSerialofXYData();
      this.rawData.sort(cmp);
  }
  class compareXofXYData implements Comparator<OrdXYData>{
      
        @Override
        public int compare(OrdXYData t, OrdXYData t1) {
            return (int)( t.getX().doubleValue() - t1.getX().doubleValue());
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      
  }
  class compareYofXYData implements Comparator<OrdXYData>{
      @Override
      public int compare(OrdXYData t, OrdXYData t1) {
            return (int)( t.getX().doubleValue() - t1.getX().doubleValue());
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      
  }
  class compareSerialofXYData implements Comparator<OrdXYData>{
      @Override
      public int compare(OrdXYData t, OrdXYData t1){
          return (t.getSerial() - t1.getSerial());
      }
  }

}
