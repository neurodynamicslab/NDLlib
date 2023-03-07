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
public class OrdXYData<X extends Number, Y extends Number> extends Object{
    
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
    public void scaleY(double scaler) {
        Number scaledY =  this.getY().doubleValue()*scaler;
        this.yDataPt = (Y) scaledY;
    }
}