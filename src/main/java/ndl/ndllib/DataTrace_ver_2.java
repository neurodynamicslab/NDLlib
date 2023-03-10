/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Balaji
 */
package ndl.ndllib;

import static java.lang.Math.exp;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import java.util.*;
/*import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.CurveFitter;
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
class OrdXYErrData_inVer2< XErr extends Number, YErr extends Number, X extends Number, Y extends Number> extends OrdXYData<X,Y>{

    /**
     * @return the isSD
     */
    public boolean isIsSD() {
        return isSD;
    }

    /**
     * @param isSD the isSD to set
     */
    public void setIsSD(boolean isSD) {
        this.isSD = isSD;
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
    //int serialNo;
    XErr xErrorBar;
    YErr yErrorBar;
    private boolean isSD; //SD if true and SEM if false
    int nPts;
    private double LXLimit = Double.MIN_VALUE;
    private double LYLimit = Double.MIN_VALUE;
    private double HXLimit = Double.MAX_VALUE;
    private double HYLimit = Double.MAX_VALUE;
    
    
    public OrdXYErrData_inVer2(int serial, X x, Y y, XErr xError, YErr yError, boolean SD, int NoofPoints){
        //super.xDataPt = (Number) x;
        //super.yDataPt = (Number) y;
        super(serial,x,y);
        
        xErrorBar = xError;
        yErrorBar = yError;
        isSD = SD;
        nPts = NoofPoints;
    }
    public OrdXYErrData_inVer2(int serial, X x, Y y){
       /* xDataPt = x;
        yDataPt = y;
        serialNo = serial; */
        super(serial,x,y);
        xErrorBar = (XErr) Integer.valueOf(-1);
        yErrorBar = (YErr) Integer.valueOf(-1);
        isSD = true;
        nPts = -1;
    }
    @Override
    public  X getX(){
        return (X) super.xDataPt;
    }
    @Override
    public Y getY(){
        return (Y) yDataPt;
    }
    @Override
    public ArrayList<? extends Number> getXY(){
        ArrayList dataArray = new ArrayList(2);
        dataArray.add(xDataPt);
        dataArray.add(yDataPt);
        return dataArray;
    }
    @Override
    public int getSerial(){
        return serialNo;
    }
    
    public ArrayList<? extends Number> getXYErr(){
        ArrayList dataArray = new ArrayList(4);
        dataArray.add(xDataPt);
        dataArray.add(yDataPt);
        dataArray.add(xErrorBar);
        dataArray.add(yErrorBar);
        return dataArray;
    }
    
    public  XErr getXError(){
        return this.xErrorBar;
    }
    public YErr getYError(){
        return this.yErrorBar;
    }
    public boolean getifSD(){
        return isIsSD();
    }
    public boolean setifSD(boolean SD){
        setIsSD(SD);
        return isIsSD();
    }
    public int getNumberofPoints(){
        return nPts;
    }
    public void setNumberofPoints(int noPoints){
        nPts = noPoints;
    }
}
public class DataTrace_ver_2 extends ArrayList<OrdXYErrData_inVer2>{

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
    
    
    /** Binning Data
     * 
     */
    boolean binInY = true;
    double binWnd  = 0;
    
    int SCALE = 0;       //SCALE = 0 - linear, 1 - ln, 2 - log, 3 - power of 2

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
    public void setScaleType(int stype){
        SCALE = stype;
    }
    public int getScaleType(){
        return SCALE;    //SCALE = 0 - linear, 1 - ln, 2 - log, 3 - power of 2
    }
    public DataTrace_ver_2(){
        //rawData = new ArrayList<OrdXYData>();
        //dataIterator = rawData.iterator();
        
    }
   public <B extends Number>DataTrace_ver_2(int datalength, B binWidth, boolean binInX){
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
   
   public <X extends Number,Y extends Number, Xr extends Number, Yr extends Number> void addData(X xData,Y yData, Xr xError, Yr yError, boolean SD, int nPts){  
       DataLength++;
       OrdXYErrData_inVer2 dataPt = new OrdXYErrData_inVer2(DataLength,xData,yData,xError,yError,SD,nPts);
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
   * Differentiates the trace data and generates the differential of current data
   * will overwrite the current data with the differentiated data. Step size is assumed to 
   * be one data point. ie. diff(t) = 1/2 * {(y[t+1]-y(t))/(x[t+1]-x[t]) + y(t}- y{t-1]/(x[t] -x[t-1))}.
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
  public DataTrace_ver_2 differentiate(boolean Overwrite){
      //DataTrace_ver_2 difData = new DataTrace_ver_2();
      //difData = null;
      //difData = 
     
      return differentiate(Overwrite, 1);
  }
  public DataTrace_ver_2 differentiate(boolean Overwrite, int Stepsize){
       DataTrace_ver_2 difData = new DataTrace_ver_2();
       
       
       
      return difData;
  }
  
  
  
  public <X extends Number, Y extends Number, Xerr extends Number, YErr extends Number> DataTrace_ver_2 binData(double binWidth, boolean binInX, boolean restoreSeq){
      //SCALE = 0 - linear, 1 - ln, 2 - log, 3 - power of 2
     DataTrace_ver_2 binnedData = new DataTrace_ver_2();
     this.sortData(binInX);
     int binNumber = 1;
     double sbinwidth = binWidth;
     double halfbinWidth = binWidth/2;
     double binStart = (double)((binInX) ? this.get(0).getX() : this.get(0).getY());
     
    /* switch(this.SCALE){
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
     
     for(OrdXYErrData_inVer2 data : this){
        
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
             switch(this.SCALE){
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
  public<X extends Number, Y extends Number> DataTrace_ver_2 sortXYData(DataTrace_ver_2 XYData,boolean inX){
        
        X[] x = (X [])(XYData.getX().toArray());
        Y[] y = (Y [])(XYData.getY().toArray());
        
        DataTrace_ver_2 sortedData = new DataTrace_ver_2();
               
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
