
package ndl.ndllib;

import java.util.ArrayList;

/**
 * Class to represent the vectors in 2D in polar form. The constructor class specifically takes the angle and 
 * magnitude. The class can also be initialized using Cartesian co-ordinates in that case the JVector Object is used 
 * to initialize this class. For e.g. vect2DPolar  = new  J2DVectorPolar (new JVector(Xcord, Ycord}).
 *  
 * @author balam
 */
public class J2DVectorPolar extends JVector{
    
    public J2DVectorPolar(double R, double T){
        super(new Double[]{R,T});       
    }
  
    
    public void setCartCord(JVector vect){
        
        double X = (double)vect.getComponent(0);
        double Y = (double)vect.getComponent(1);
        
        var r = Math.sqrt(X*X + Y*Y);
        var t = Math.acos(X/r);
        
        Components.add(r);
        Components.add(t);
                
    }
    
    /**
     * Returns the Cartesian Co-Ordinates of this vector
     * @return : X and Y co-ordinates as array list.
     */
    public ArrayList getCartForm(){
        ArrayList comp = new ArrayList<>();
        
        var r = (double)getComponent(0); 
        var t = (double)getComponent(1);
        
        comp.add(0,r*Math.cos(t));
        comp.add(1,r*Math.sin(t));
        
        return comp;
    }
    
    public ArrayList getPolarForm(){
        ArrayList comp = new ArrayList<>();
        
        comp.add(0, this.getComponent(0));
        comp.add(1,this.getComponent(1));
        
        return comp;
    }
    
    public JVector getCartVect(){
        return (new JVector(this.getCartForm()));
    }
    
}
