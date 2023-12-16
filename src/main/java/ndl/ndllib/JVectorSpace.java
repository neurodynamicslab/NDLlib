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
public class JVectorSpace {

    /**
     * @return the projection
     */
    public JVectorSpace getProjection() {
        return projection;
    }

    /**
     * @return the projectionStatus
     */
    public boolean isProjectionStatus() {
        return projectionStatus;
    }

    /**
     * @param projectionStatus the projectionStatus to set
     */
    public void setProjectionStatus(boolean projectionStatus) {
        this.projectionStatus = projectionStatus;
    }

    /**
     * @return the prjTarget
     */
    public JVector getPrjTarget() {
        return prjTarget;
    }

    /**
     * @param prjTarget the prjTarget to set
     */
    public void setPrjTarget(JVector prjTarget) {
        this.prjTarget = prjTarget;
    }

    /**
     * @param nComp the nComp to set
     */
    private void setnComp(int nComp) {
        this.nComp = nComp;
    }

    /**
     * @return the nComp
     */
    public int getnComp() {
        return nComp;
    }

    /**
     * @return the space
     */
    public ArrayList<OrdXYData> getSpace() {
        return space;
    }

    /**
     * @return the vectors
     */
    public ArrayList<JVector> getVectors() {
        return vectors;
    }

    /**
     * @return the xRes
     */
    public int getxRes() {
        return xRes;
    }

    /**
     * @param xRes the xRes to set
     */
    public void setxRes(int xRes) {
        this.xRes = xRes;
    }

    /**
     * @return the yRes
     */
    public int getyRes() {
        return yRes;
    }

    /**
     * @param yRes the yRes to set
     */
    public void setyRes(int yRes) {
        this.yRes = yRes;
    }

    /**
     * @return the xMax
     */
    public int getxMax() {
        return xMax;
    }

    /**
     * @param xMax the xMax to set
     */
    public void setxMax(int xMax) {
        this.xMax = xMax;
    }

    /**
     * @return the yMax
     */
    public int getyMax() {
        return yMax;
    }

    /**
     * @param yMax the yMax to set
     */
    public void setyMax(int yMax) {
        this.yMax = yMax;
    }

    /**
     * @return the resMismatch
     */
    public boolean isResMismatch() {
        return resMismatch;
    }

    /**
     * @param resMismatch the resMismatch to set
     */
    public void setResMismatch(boolean resMismatch) {
        this.resMismatch = resMismatch;
    }
    
    private ArrayList<OrdXYData> space;
    private ArrayList<JVector> vectors;
    private JVector maxVector, minVector;       //vector contructed using maximum and minimum componenets from the vector space. Note
                                                //this is a fictious vector and may not co-respond to any one of the added vectors found in vector space.
    private int xRes, yRes,xMax,yMax;
    private boolean resMismatch;
    private int nComp; //Helps to keep track of dimensionality of the vectors space (i.e all vectors have same number of components 
    private boolean projectionStatus = false;
    private JVector prjTarget;
    private JVectorSpace projection;
    private boolean useTan2 = true;
    private boolean chkMinMaxandAdd = false;
 public double[] getCompArray(int Idx){
    double[] pixels;
    if( getSpace().isEmpty() || getVectors().isEmpty()){
        javax.swing.JOptionPane.showMessageDialog(null, "There are no vectors to retrive");
        return null;
    }
    if( getSpace().size() != getVectors().size()){
        javax.swing.JOptionPane.showMessageDialog(null, "Vector count and pixel mismatch");
        return null;
    }
    if( getxRes() == 0 || getyRes() == 0){
        javax.swing.JOptionPane.showMessageDialog
                    (null, "Please assign resolution of the image/array first\n");
        return null;
    }     
    int maxIdx = getxRes() * getyRes();
    pixels = new double[maxIdx];
    


    double[][] tempArray = new double[getxRes()][getyRes()];
    //float [][] nData = new float[getxRes()][getyRes()];
    int currX, currY;
    int dataIdx = 0;
    for(OrdXYData curPixel : getSpace()){
        currX = (int)Math.round((double)curPixel.getX());
        currY = (int)Math.round((double)curPixel.getY());
        tempArray[currX][currY] += getVectors().get(dataIdx).getComponent(Idx).doubleValue();
        
        dataIdx++;
    }
    int nRow = 0, nCol = 0;
    int arrayIdx;
    //float no = 0;

    for(double[] Cols: tempArray){  
        for(double Val : Cols){ 
            arrayIdx = nCol + nRow*getxRes();
            
            if(arrayIdx < maxIdx){
                pixels[arrayIdx] = Val ;
                nRow++;
            }
            else{
                System.out.print("Length of rows is: "+ Cols.length);
                System.out.println("VectorSpace:"+"xLoc of :"+nRow +"yLoc of :" + nCol + "exceeds the xRes x yRes of:" + xRes +" X "+yRes);
            }
                
        }
        nCol ++;
        nRow = 0;
    }
    return pixels;
  }
 public JVectorSpace (int xRes, int yRes){
     this.xRes = xRes;
     this.yRes = yRes;
     this.xMax = this.yMax = 0;
     resMismatch = false;
     space = new ArrayList<>();
     vectors = new ArrayList<>();
 } 
 public JVectorSpace(int xRes, int yRes, boolean resAuto, OrdXYData[] spaceArray,JVector[] vectorArray ){
     this.xRes = xRes;
     this.yRes = yRes;
     this.xMax = this.yMax = 0;
     resMismatch = false;
     space = new ArrayList<>();
     vectors = new ArrayList<>();
     
     if(spaceArray.length != vectorArray.length || spaceArray.length == 0){
         space = null;
         vectors = null;
     }else{
         space = new ArrayList<> ();
         vectors = new ArrayList<> ();
         int Idx = 0;
         for(OrdXYData coord : spaceArray){
             addVector(coord,vectorArray[Idx],resAuto);
             Idx++;
         }
     }
 }
 public JVectorSpace(JVectorSpace newVS){
   this.space = newVS.getSpace();
   this.vectors = newVS.getVectors();
   this.xRes = newVS.getxRes();
   this.yRes = newVS.getyRes();
   this.nComp = newVS.getnComp();
   this.xMax = newVS.getxMax();
   this.yMax = newVS.getyMax();
   this.resMismatch = newVS.isResMismatch();
   
 }
 private void addVector(OrdXYData coordinates, JVector vector, boolean resAuto){
     
     int currX = (int) Math.round((double)coordinates.getX()); 
     int currY = (int) Math.round((double)coordinates.getY());
     int currComp = vector.getNComponents();
        if(this.vectors.isEmpty()){
            this.setnComp(vector.getNComponents());
            this.maxVector = new JVector(vector);
            this.minVector = new JVector(vector);
        }
        if(currComp != getnComp()){
            javax.swing.JOptionPane.showMessageDialog(null, "Found vector component mismatch");
            return;
        }
                
        setxMax(currX > getxMax() ? currX : getxMax());
        setyMax(currY > getyMax() ? currY : getyMax());
        setResMismatch(getxRes() > getxMax() || getyRes() > getyMax());
     
     if (resAuto && isResMismatch()){
            setxRes(currX >= getxRes() ? currX : getxRes());    
            setyRes(currY >= getyRes() ? currY : getyRes());  
            setResMismatch(false);
     }
        if(this.isChkMinMaxandAdd())
            checkAndsetMinMax(vector);
        
        getSpace().add(coordinates);
        getVectors().add(vector);
 }
 public void fillSpace(ArrayList<OrdXYData> coordLst,ArrayList<JVector>vectorLst,boolean resAuto){
     
     if(coordLst.isEmpty() || (vectorLst.size() != coordLst.size())){
         space = null;
         vectors = null;
     }else{
         int Idx = 0;
         for(OrdXYData coord : coordLst){
             addVector(coord,vectorLst.get(Idx),resAuto);
             Idx++;
         }
     }
     
 }
 public void fillSpace(OrdXYData[] spaceArray,JVector[] vectorArray,boolean resAuto){
     
     if(spaceArray.length != vectorArray.length || spaceArray.length == 0){
         space = null;
         vectors = null;
     }else{
         int Idx = 0;
         for(OrdXYData coord : spaceArray){
             addVector(coord,vectorArray[Idx],resAuto);
             Idx++;
         }
     }
 
 }
 
 public JVectorSpace(int xRes, int yRes,boolean resAuto, ArrayList<OrdXYData> coordLst,ArrayList<JVector> vectorLst ){
   
     this.xRes = xRes;
     this.yRes = yRes;
     this.xMax = this.yMax = 0;
     resMismatch = false;
     space = new ArrayList<>();
     vectors = new ArrayList<>();
     
     if(coordLst.isEmpty() || (vectorLst.size() != coordLst.size())){
         space = null;
         vectors = null;
     }else{
         int Idx = 0;
         for(OrdXYData coord : coordLst){
             addVector(coord,vectorLst.get(Idx),resAuto);
             Idx++;
         }
     }
 }
 public JVectorSpace scaleVectors(Number[][] scalingMat){
     
     
     if(scalingMat[0].length != yRes && scalingMat.length != xRes){
         System.out.println("The dimension of the scaling Matrix in (x X y)/(width X height) "+ scalingMat.length + " X " 
                                            + scalingMat[0].length + "is not same as vector space" + this.xRes + " X " + this.yRes);
         return null;
     }
     //if(this.isResMismatch()) Do a reset of res or inform the user to set it
     
     JVectorSpace scaledSpace = new JVectorSpace(this.xRes,this.yRes);
     int xIdx, yIdx;
     double scale = 1;
     int Idx = 0;
     
     for(var XYCord : space){
         xIdx = (int) Math.round((Double)XYCord.getX());
         yIdx = (int) Math.round((Double)XYCord.getY());
         try{
            scale = scalingMat[xIdx][yIdx].doubleValue(); 
         }
         catch(Exception e){
             System.out.println(e.getMessage()+ " Resolution mismatch !");
         }
         scaledSpace.addVector(XYCord,vectors.get(Idx++).getScaledVector(scale), resMismatch);
     }
     return scaledSpace;
 }
 
 public JVectorSpace normaliseVectors(double[][]normMat){
     if(normMat[0].length != yRes && normMat.length != xRes){
         System.out.println("The dimension of the norm Matrix in (x X y)/(width X height) "+ normMat.length + " X " 
                                            + normMat[0].length + "is not same as vector space" + this.xRes + " X " + this.yRes);
         return null;
     }
     //if(this.isResMismatch()) Do a reset of res or inform the user to set it
     
     JVectorSpace scaledSpace = new JVectorSpace(this.xRes,this.yRes);
     int xIdx, yIdx;
     double scale = 1;
     int Idx = 0;
     
     for(var XYCord : space){
         xIdx = (int) Math.round((Double)XYCord.getX());
         yIdx = (int) Math.round((Double)XYCord.getY());
         try{
            var val = normMat[xIdx][yIdx];
            scale = val != 0 ? 1.0/val : 0; 
         }
         catch(Exception e){
             System.out.println(e.getMessage()+ " Resolution mismatch !");
         }
         scaledSpace.addVector(XYCord,vectors.get(Idx++).getScaledVector(scale), resMismatch);
     }
     return scaledSpace;
 }
 public JVectorSpace scaleVectors(double scale){
     
     JVectorSpace scaledSpace = new JVectorSpace(this.xRes,this.yRes);
     
     int Idx = 0;
     
     for(var XYCord : space)         
         scaledSpace.addVector(XYCord,vectors.get(Idx++).getScaledVector(scale), resMismatch);
     
     return scaledSpace;
 }
 public JVectorSpace calibrateVectors(double maxUnit, double minUnit){
     JVectorSpace calibSpace = new JVectorSpace(this.xRes,this.yRes);
     JVector calibVector;
     if(!this.isChkMinMaxandAdd())
         this.findMinandMax();
     int elementIdx = 0;
     for(OrdXYData coOrd : this.space){
        calibVector = this.vectors.get(elementIdx).getCalibratedVector(maxVector, minVector, maxUnit, minUnit);
        calibSpace.addVector(coOrd,calibVector,false);
     }
     
     return calibSpace;
 }
 public JVectorSpace getProjections2point( JVector Vector, boolean along){
     makeProjections2point(Vector, along);
     this.setPrjTarget(Vector);
     this.setProjectionStatus(true);
     return getProjection();
 }
 
 public void makeProjections2point(JVector positionVector, boolean along){  
     double dotProd,mag,angle;
     int idx = 0,compCount;
     JVector tarVector, prjVector;
     ArrayList<Number> tarCord = new ArrayList(), curCord ;//= new Number[space.size()];
     ArrayList projVects = new ArrayList();
     double result;
     for (var vect : this.vectors){      
           curCord = space.get(idx++).getXY();
           compCount = 0;
           tarCord = new ArrayList();
           for(Number N : curCord){
               result = positionVector.getComponent(compCount).doubleValue() - N.doubleValue(); 
               tarCord.add(compCount,(Double)result);
               compCount++;
           }
           tarVector = new JVector(tarCord);   
           
           dotProd = vect.dotProduct(tarVector);
           mag = dotProd/tarVector.getL2Norm(); //The following could be optimised for speed by checking out of this loop calling separate functions 
           angle =  (this.isUseTan2()) ? Math.atan2(tarVector.getComponent(1).doubleValue(),tarVector.getComponent(0).doubleValue())
                                            : Math.atan(tarVector.getComponent(1).doubleValue()/tarVector.getComponent(0).doubleValue());                 
          // angle *= -1;               // Needed as the origin in our image is at top left corner and y axis
                                        // increases downwards. The angle returned by tan2 is angle from x - axis 
                                        // and inreases towards the positive direction of y axis. Thus in our system 
                                        // this returns the negtive of the angle. 
           prjVector = new J2DVectorPolar(mag,angle).getCartVect();
           projVects.add(prjVector);
//         System.out.println(positionVector.getComponent(0)+"\t"+positionVector.getComponent(1));
//         System.out.println(prjVector.getComponent(0)+"\t"+prjVector.getComponent(1));
     }
     projection = new JVectorSpace(this);
     projection.vectors = projVects;
     System.out.println("Finished the dataset projections\n");
 }

    /**
     * @return the useTan2
     */
    public boolean isUseTan2() {
        return useTan2;
    }

    /**
     * @param useTan2 the useTan2 to set
     */
    public void setUseTan2(boolean useTan2) {
        this.useTan2 = useTan2;
    }

    /**
     * @return the maxVector
     */
    public JVector getMaxVector() {
        return maxVector;
    }

    /**
     * @param maxVector the maxVector to set
     */
    public void setMaxVector(JVector maxVector) {
        this.maxVector = maxVector;
    }

    /**
     * @return the minVector
     */
    public JVector getMinVector() {
        return minVector;
    }

    /**
     * @param minVector the minVector to set
     */
    public void setMinVector(JVector minVector) {
        this.minVector = minVector;
    }

    private void checkAndsetMinMax(JVector vector) {
        
        int nComps  = vector.getNComponents();
        Number currComp;
        for( int compCnt = 0 ; compCnt < nComps; compCnt++){
            
            currComp = vector.getComponent(compCnt);
            var maxComp = maxVector.getComponent(compCnt);
            var minComp = minVector.getComponent(compCnt);
            
            maxComp = (maxComp.doubleValue() > currComp.doubleValue())? maxComp : currComp;
            minComp = (minComp.doubleValue() < currComp.doubleValue())? minComp : currComp;
            
            maxVector.Components.add(compCnt, maxComp);
            minVector.Components.add(compCnt,minComp);
        }
    }
    
    public void findMinandMax(){
        for(JVector currVector : vectors){
            this.checkAndsetMinMax(currVector);
        }
    }

    /**
     * @return the chkMinMaxandAdd
     */
    public boolean isChkMinMaxandAdd() {
        return chkMinMaxandAdd;
    }

    /**
     * @param chkMinMaxandAdd the chkMinMaxandAdd to set
     */
    public void setChkMinMaxandAdd(boolean chkMinMaxandAdd) {
        if(chkMinMaxandAdd)
            this.findMinandMax();
        this.chkMinMaxandAdd = chkMinMaxandAdd;
    }

}
