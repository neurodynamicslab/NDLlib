package ndl.ndllib;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author balam
 */

public class OrdXYErrData< XErr extends Number, YErr extends Number, X extends Number, Y extends Number> extends OrdXYData<X,Y>{

    /** Checks for the flag setting to determine if the error bar is standard deviation
     * Note: This flag needs to be set by the user during the data input stage.
     * @return the isSD
     */
    public boolean isIsSD() {
        return isSD;
        }

    /**Set the flag to determine if the error bar in the present in this data is standard deviation(true) or standard error(false).
     * @param isSD the isSD to set
     */
    public void setIsSD(boolean isSD) {
        this.isSD = isSD;
    }

    /**Returns the lowest value that can be stored in X data
     * 
     * @return the LXLimit
     */
    public double getLXLimit() {
        return LXLimit;
    }

    /**Sets the lowest value that can be stored in X data. Default is Double.MINVALUE
     * @param LXLimit the LXLimit to set
     */
    public void setLXLimit(double LXLimit) {
        this.LXLimit = LXLimit;
    }

    /**
     * Returns the lowest value that can be stored in Y data
     * @return the LYLimit
     */
    public double getLYLimit() {
        return LYLimit;
    }

    /**Sets the lowest value that can be stored in Y data. Default is Double.MINVALUE
     * @param LYLimit the LYLimit to set
     */
    public void setLYLimit(double LYLimit) {
        this.LYLimit = LYLimit;
    }

    /**
     * Returns the highest value that can be stored in X data. 
     * @return the HXLimit
     */
    public double getHXLimit() {
        return HXLimit;
    }

    /**
     * Sets the highest value that can be stored in X data. Default is Double.MAXVALUE
     * @param HXLimit the HXLimit to set
     */
    public void setHXLimit(double HXLimit) {
        this.HXLimit = HXLimit;
    }

    /**
     * Returns the highest value that can be stored in X data. 
     * @return the HYLimit
     */
    public double getHYLimit() {
        return HYLimit;
    }

    /**
     * Sets the highest value that can be stored in X data. Default is Double.MAXVALUE
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
    
    /**
     * 
     * @param serial : Serial number of the data point. Can be used to restore the order of data points to the input after sorting.
     *                 for instance one might sort on y values then and perform the desired operation on the data set and then sort on
     *                  serial number to restore to the input order. 
     * @param x      : x value it can be any of data type sub classed from Number
     * @param y      : y value  it can be any of data type sub classed from Number
     * @param xError    : value of error in x axis typically it is either standard deviation or standard error. Data type is one of the sub
     *                    classes of Number
     * @param yError  : value of error in x axis typically it is either standard deviation or standard error. Data type is one of the sub
     *                    classes of Number
     * @param SD      : Boolean for if the error bar is SD (True). False would mean the error bar is SEM
     * @param NoofPoints : Number of data points used to arrive at the SD or SE for this particular point. 
     *                     It is assumed that number of points averaged to arrive at error bar is same for X and Y.
     */
    public OrdXYErrData(int serial, X x, Y y, XErr xError, YErr yError, boolean SD, int NoofPoints){
        //super.xDataPt = (Number) x;
        //super.yDataPt = (Number) y;
        super(serial,x,y);
        /**
         * @ xErrorBar yErrorBar
         * The value of the error bar for x axis and y axis
         */
        xErrorBar = xError;                         
        yErrorBar = yError;
        /**
         * @isSD 
         * True if the error bar is standard deviation false if it is standard error of the mean.
         */
        isSD = SD;          
        /** 
         * @nPts 
         * Number of data points used to arrive at the SD or SE for this particular point. 
         * It is assumed that number of points averaged to arrive at error bar is same for X and Y.
        */
        nPts = NoofPoints;                  
    }
    public OrdXYErrData(int serial, X x, Y y){
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
    /**
     * 
     * @return  X errorBar value co-responding to this data point
     */
    public  XErr getXError(){
        return this.xErrorBar;
    }
    /**
     * 
     * @return Y errorBar value co-responding to this data point
     */
    public YErr getYError(){
        return this.yErrorBar;
    }
    /**
     * 
     * @return true if the error bar is SD false if SEM (This is set by the user at the time of initialization.
     */
    public boolean getifSD(){
        return isIsSD();
    }
    /**
     * 
     * @param SD call it with True to interpret the error bar at SD False for interpreting it as SEM. 
     *        This is assumed to be same for Y and X error bar.
     * Note : It is not calculated by the class or any members of this class (You can not do that as all you have is 'a' value for 
     * X, 'a' value for Y.
     * @return 
     */
    public boolean setifSD(boolean SD){
        setIsSD(SD);
        return isIsSD();
    }
    /**
     * Use this function to get the number points that is used to average and get the X and Y mean and error bar associated with it. 
     * Use the return value to calculate SEM from SD or vice versa. 
     * @return 
     */
    
    public int getNumberofPoints(){
        return nPts;
    }
    /**
     * Use this function to set the number points that is used to average and get the X and Y mean and error bar associated with it. 
     *@param noPoints : number of points over which the error bar is calculated. Provided to go from SD to SEM and vice versa. 
     */
    
    public void setNumberofPoints(int noPoints){
        nPts = noPoints;
    }
}
