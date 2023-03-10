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


public class TraceData extends Object{
    double[] xData = null;
    double[] yData = null;
    
    ArrayList<Double> XData;
    ArrayList<Double> YData;
    
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
    
   // ArrayList<Double[]> BinnedData;
    
   public TraceData( int length){
        if (length > 0){
            DataLength = length;
            xData = new double[DataLength];
            yData = new double[DataLength];
        }
        XData = new ArrayList<>();
        YData = new ArrayList<>();
    }
   public TraceData( double[] x, double[] y){
        if( x != null && y != null && x.length == y.length){
            xData = (double[])x.clone();
            yData = (double[])y.clone();
            DataLength = Math.min(xData.length,yData.length);
            int idx = 0;
            for(double d : x){
                XData.add(d);
                idx++;
                YData.add(y[idx]);
            }
        }
    }
   public boolean addData(double x, double y){
        if (CurrPos >= DataLength){
            //showMessage("OOPS! I am full you can not add anymore to me");
            return false;
        }

        xData[CurrPos] = x;
        yData[CurrPos] = y;
        CurrPos++;
        ActLength =  CurrPos > ActLength ? CurrPos : ActLength;
        
        XData.add(x);
        YData.add(y);

        /* Update the stat parameters: This is the only entry point of the data */

        setStat( CurrPos-1);

        return true;
    }
   public double getX(int pos){
       return XData.get(pos);
       /*if(pos < DataLength)
           return xData[pos];
       return xData[DataLength];*/
   }
   public double getY(int pos){
      return YData.get(pos);
       /*if(pos < DataLength) return yData[pos];
      return yData[DataLength];*/
   }
   public double[] getXY(int pos){
       double[] XY = new double[2];
       XY[0] = XData.get(pos);
       XY[1] = YData.get(pos);
       
      /* if (pos < DataLength){
            XY[1] = xData[pos];
            XY[2] = yData[pos];
       }
       else{
            XY[1] = xData[DataLength];
            XY[2] = yData[DataLength];
       }*/
       return XY;
   }
   public boolean setPosition(int pos){
       /*if(pos < DataLength){
           CurrPos = pos;
           return true;
       }*/
       CurrPos = pos;
     return true ;
   }
   public int getPosition(){
       return CurrPos;
   }
   public int getDataLength(){
       DataLength = XData.size();
       return DataLength;
   }
   public double[] getX(){
       double [] x = new double[XData.size()];
       int idx = 0;
       for(Double d : XData){
           x[idx] = d;
           idx++;
       }
       return x;
   }
   public double[] getY(){
      double [] y = new double[YData.size()];
       int idx = 0;
       for(Double d : YData){
           y[idx] = d;
           idx++;
       }
       return y;
   }
   public double[] getX(boolean trimmed){
       return Arrays.copyOf(xData, ActLength);
   }
   public double[] getY(boolean trimmed){
       return Arrays.copyOf(yData, ActLength);
   }
   public boolean setLength(int length){
       if (DataLength != 0)
           return false;            // The object is holding a data array. In order to reset one needs to use the override method (just an extra protection)
        if (length > 0){
            DataLength = length;
            xData = new double[DataLength];
            yData = new double[DataLength];
            CurrPos = ActLength = 0;
            x_Min = y_Min = Double.MAX_VALUE;
            x_Max= y_Max = x_Sum = y_Sum = 0;
            return true;
        }
       return false;
   }
   /**
    * is deprecated
    * This function in irrelevant in the modern day scenario. 
    * @param length set this to Zero to reset the list.
    */
   public void OverrideLength(int length){
       XData.clear();
       YData.clear();
       XData.ensureCapacity(length);
       YData.ensureCapacity(length);
       /*if (length == 0){
           xData = null;
           yData = null;
           x_Min = y_Min = Double.MAX_VALUE;
            x_Max= y_Max = x_Sum = y_Sum = 0;
            
           return;
       }
       DataLength = length;
       xData = new double[DataLength];
       yData = new double[DataLength];
       CurrPos = ActLength = 0;
       x_Min = y_Min = Double.MAX_VALUE;
       x_Max= y_Max = x_Sum = y_Sum = 0;
       return;*/
   }
  void  setStat(double x, double y){
      //Seems erroneous : Doesn't seem to integrate with rest of data.
       x_Max = (x_Max > x ) ? x_Max : x ;
        y_Max = (y_Max > y ) ? y_Max : y ;

        x_Min = (x_Min < x ) ? x_Min  : x ;
        y_Min = (y_Min < y ) ? y_Min : y ;

        x_Sum += x;
        y_Sum += y;
   }
  void setStat(int pos){

        double x = getX(pos);
        double y = getY(pos);

        x_Max = (x_Max > x ) ? x_Max : x ;
        y_Max = (y_Max > y ) ? y_Max : y ;

        x_Min = (x_Min < x ) ? x_Min  : x ;
        y_Min = (y_Min < y ) ? y_Min : y ;

        x_Sum += x;
        y_Sum += y;
  }

  void setStat(boolean all){
      if(all){
          double x =0;
          double y = 0;
          for(int i = 0 ; i < XData.size() ; i++){
            x = getX(i);
            y = getY(i) ;
            
            x_Max = (x_Max > x ) ? x_Max : x ;
            y_Max = (y_Max > y ) ? y_Max : y ;

            x_Min = (x_Min < x ) ? x_Min  : x ;
            y_Min = (y_Min < y ) ? y_Min : y ;

            x_Sum += x;
            y_Sum += y;
          }
      }else{
          setStat(this.getPosition());
      }
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
    XData.clear();
    YData.clear();
  }
  /***
   * Differentiates the trace data and generates the differential of current data
   * will overwrite the current data with the differentiated data
   */
  public void differentiate(){
      
      
  }
  /**
   * Differentiate the trace data and return the float array. 
   * if Overwrite is true then the current data will be replaced by the differentiated data
   * @param Overwrite
   * @return 
   */
  public TraceData differentiate(boolean Overwrite){
      TraceData difData = new TraceData(this.getDataLength());
      //difData = null;
      return difData;
  }
  
  
  
  public TraceData binData(double binWidth, boolean binInX){
      
     throw new UnsupportedOperationException("Not yet supported yet");
      
      
      
  }
  public TraceData sortXYData(TraceData XYData){
      
      
      
      return null;
      
  }
  class compareXYData implements Comparator<ArrayList<Double[]>>{

        @Override
        public int compare(ArrayList<Double[]> t, ArrayList<Double[]> t1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      
  }

}
