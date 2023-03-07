package ndl.ndllib;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author balam
 * @param <N>
 */
public class JVector<N extends Number> {
    
    protected ArrayList<N> Components;
    /***
     * Convenience constructor for creating 2D cartesian vector objects. 
     * @param Xcord
     * @param Ycord 
     */
    public JVector(N Xcord, N Ycord){
        Components = new ArrayList<>();
        Components.add(Xcord);
        Components.add(Ycord);
        this.calculateNorm();
    }
    public JVector(JVector vector){
        Components = new ArrayList();
        Components.addAll(vector.Components);
        this.setNorm(vector.getL1Norm(),vector.getL2Norm(),vector.getMaxNorm());
//        System.out.printf("L1:%f,L2:%f,Mx:%f\n",getL1Norm(),getL2Norm(),getMaxNorm());
        setNormsReady(vector.isNormsReady());
    }
    public JVector(N [] C){
        Components = new ArrayList<>();
        Components.addAll(Arrays.asList(C));
        this.calculateNorm();
    }
   public JVector(ArrayList<N> C){
       Components = new ArrayList<>();
       for(N c : C)
            Components.add(c);
       this.calculateNorm();
   }
   public void addMoreComp(ArrayList<N> C){
       for(N c : C)
            Components.add(c);
       this.calculateNorm();
   }
    /**
     * @return the normsReady
     */
    public boolean isNormsReady() {
        return normsReady;
    }

    /**
     * @param normsReady the normsReady to set
     */
    private void setNormsReady(boolean normsReady) {
        this.normsReady = normsReady;
    }

    /**
     * @return the L1Norm
     */
    public double getL1Norm() {
        if(! isNormsReady() )
            this.calculateNorm();   
        return L1Norm;
    }

    /**
     * @return the L2Norm
     */
    public double getL2Norm() {
        if(! isNormsReady() )
            this.calculateNorm();
        return  L2Norm ;
    }

    /**
     * @return the maxNorm
     */
    public double getMaxNorm() {
        if(! isNormsReady() )
            this.calculateNorm();
        return maxNorm ;
    }

    private void setNorm(double absNorm, double sqNorm, double maxNorm) {
        this.L1Norm = absNorm;
        this.L2Norm = sqNorm;
        this.maxNorm = maxNorm;
        this.setNormsReady(true);
    }   
    
    public Number getComponent(int idx){
           return Components.get(idx);  
    }
    public int getNComponents(){
        return this.Components.size();
    }
    public double[] getNorm(){       
      if(!this.isNormsReady())
           this.calculateNorm();
      double [] norms = new double[3];
      norms[1] = this.getL1Norm();
      norms[2] = this.getL2Norm();
      norms[3] = this.getMaxNorm();
      return norms; 
    }
    private void calculateNorm(){     
        double sumSq = 0;
        double absSum = 0;
        double max = 0;
        //int nComponents = 0;
        for(Number N : Components){
           
                
            Double comp = N.doubleValue();
            
            absSum += java.lang.Math.abs(comp);
            sumSq += (comp * comp);
            max = comp > max ? comp : max ;
         //nComponents++;
        }
         this.setNorm(absSum,java.lang.Math.sqrt(sumSq),max);
    }
    public double dotProduct(JVector secondVector){
        double product = 1.0;
        int idx = 0;
        if(this.getNComponents() == secondVector.getNComponents()){
            for(Number N : Components){
                product += (N.doubleValue() * secondVector.getComponent(idx).doubleValue());
                idx++;
            }
            return product;
        }else {
            System.out.println("Mismatch in components of vectors:\t"+this.getNComponents()+"\t"+secondVector.getNComponents());
            return -1;
        }
    }
    public double findAngle(JVector targetVector){
        double angle; 
        if(!isNormsReady())
            this.calculateNorm();
        angle = java.lang.Math.acos(this.dotProduct(targetVector)/(this.L2Norm * targetVector.getL2Norm()));
        return angle;
    }
    public static JVector add( JVector vect1, JVector vect2){
        if(vect1.getNComponents() != vect2.getNComponents()){
            System.out.println("No of components of Vect1("+vect1.getNComponents()+") does not match with that of Vect2("+vect2.getNComponents()+")");
            return null;
        }
        JVector sum = new JVector(vect1.Components);
        int Idx =0;
        ArrayList<Number> Comp = new ArrayList();
        for(var N1 : vect1.Components) 
            Comp.add((Double)N1 + (Double)vect2.getComponent(Idx++));
        
        sum.addMoreComp(Comp);
        return sum;
    }
    
    public void add2this(JVector vect){
        JVector sumVect;
        sumVect = add(this,vect);
        this.Components = sumVect.Components;
        calculateNorm();
    }
    public  JVector getScaledVector(N scale){
        JVector scaledVect;
        ArrayList<Number> scaledC = new ArrayList();
        int Idx = 0;
       for(Number x : Components){
           //scaledC.add(Idx, (Double)Components.get(Idx)*(Double)scale);
           scaledC.add(x.doubleValue() * scale.doubleValue());
           Idx++;
       }
        scaledVect = new JVector(scaledC);
        return scaledVect;
    }
    public JVector getScaledVector(JVector scalingVect){
        if(scalingVect.getNComponents() != this.getNComponents())
            return null;
        JVector scaledVect;
        ArrayList<Number> scaledC = new ArrayList();
        int Idx  = 0;
        for(Number x : Components){
           //scaledC.add(Idx, (Double)Components.get(Idx)*(Double)scale);
           scaledC.add(x.doubleValue() * scalingVect.getComponent(Idx).doubleValue());
           Idx++;
       }
       scaledVect = new JVector(scaledC);
       return scaledVect;
    }
    public JVector getCalibratedVector(JVector max, JVector min, double maxUnits,double minUnits){
        
        ArrayList calibComp = new ArrayList();
        double unitsRange =  maxUnits - minUnits ;
        int compIdx = 0;
        double maxC,minC,currC;
        for(Number N : Components){
            
            maxC = max.getComponent(compIdx).doubleValue();
            minC = min.getComponent(compIdx).doubleValue();
            
            var unit = unitsRange/(maxC - minC);
            currC = (N.doubleValue()- minC)*unit + minUnits;
            calibComp.add(compIdx,currC);
            
//            System.out.printf("MaxC: %f, MinC: %f, currC: %f \n",maxC,minC,currC);        
            compIdx++;          
        }
        
        return new JVector(calibComp);
    }
    public void scale(N scale){
        Components = getScaledVector(scale).Components;
        this.calculateNorm();
    }
    //private ArrayList <Number> Components;
    private double L1Norm,L2Norm,maxNorm;
    private boolean normsReady;
}
