
package ndl.ndllib;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.plugin.filter.ThresholdToSelection;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.tinfour.common.IConstraint;
import org.tinfour.common.PolygonConstraint;
import org.tinfour.common.Vertex;
import org.tinfour.common.VertexMergerGroup;
import org.tinfour.interpolation.NaturalNeighborInterpolator;
import org.tinfour.refinement.RuppertRefiner;
import org.tinfour.standard.IncrementalTin;
import org.tinfour.utils.rendering.RendererForTinInspection;


/**
 *
 * @author balaji
 */
public class Natural_NeighInter {

    private Rectangle selbound;

    /**
     * @return the mask
     */
    private ByteProcessor getMask() {
        return mask;
    }
    
    /**
     * @param mask the mask to set
     * @return returns true if it was sucessfull in 
     */
    public boolean setMask(ByteProcessor mask) {
        
            if( mask != null){
                this.mask = mask;
                mask.setThreshold(0,255);
                ThresholdToSelection t2sel = new ThresholdToSelection();
                this.selection = t2sel.convert(mask);
                return true;
            }
            return false;
    }
    
        
    /**
     * @return selection
     */
     public Roi getRoi(){
         return selection;
     }
     public void setRoi(Roi roi){
         this.selection = roi;
     }
    /**
     * @return the FILTER
     */
    public int getFILTER() {
        return FILTER;
    }

    /**
     * @param FILTER the FILTER to set
     */
    public void setFILTER(int FILTER) {
        this.FILTER = FILTER;
    }

    /**
     * @return the BlurRad
     */
    public double getBlurRad() {
        return BlurRad;
    }

    /**
     * @param BlurRad the BlurRad to set
     */
    public void setBlurRad(double BlurRad) {
        this.BlurRad = BlurRad;
    }

    /**
     * @return the Normalise
     */
    public boolean isNormalise() {
        return Normalise;
    }

    /**
     * @param Normalise the Normalise to set
     */
    public void setNormalise(boolean Normalise) {
        this.Normalise = Normalise;
    }

    /**
     * @return the saveTIN
     */
    public boolean isSaveTIN() {
        return saveTIN;
    }

    /**
     * @param saveTIN the saveTIN to set
     */
    public void setSaveTIN(boolean saveTIN) {
        this.saveTIN = saveTIN;
    }

    private int FILTER = -1;
    private double BlurRad = 1;
    private boolean Normalise = false;
    private boolean saveTIN = false;
    private ByteProcessor mask = null;
    private Roi selection;
    private int minAng = 2;
    /**
     * 
     * @param ang Minimum angle used to refine the TIN
     */
    public void setminAng(int ang){
        minAng = ang;
    }
    /**
     * 
     * @return the TIN refinement angle
     */
    public int getminAng(){
        return minAng;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    private int xRes;
    private int yRes;
    private Polygon convexHull;
    private Rectangle2D bound;
    private double inImage [][], outImage[][];
    private ArrayList <Vertex> samplePts = new ArrayList();
    private ArrayList<Integer> xOrd = new ArrayList(), yOrd = new ArrayList();
    IncrementalTin tin = new IncrementalTin();
    private boolean interpolated = false;
    
    ImagePlus inImg, outImg;
    private String path;
    
    public Natural_NeighInter(int xRes, int yRes){
        setxRes(xRes);
        setyRes(yRes);      
        inImage =  new double[xRes][yRes];
        outImage = new double[xRes][yRes];
        interpolated = false;
        inImg = new ImagePlus();
        inImg.setProcessor(new FloatProcessor(new float[xRes][yRes]));
        tin.setResolutionRuleForMergedVertices(VertexMergerGroup.ResolutionRule.MeanValue);
    }
    public ImagePlus imageOutput(){
        ImagePlus  imageOut = null;
        FloatProcessor fp = new FloatProcessor(xRes,yRes);
        for ( int x = 0 ; x < xRes ; x++)
            for(int y = 0 ; y < yRes ; y++)
                fp.putPixelValue(x, y,outImage[x][y]);             
        imageOut = new ImagePlus();
        imageOut.setProcessor(fp);
        return imageOut;
    }
    
    public Natural_NeighInter(ImagePlus imp,Roi sel){
        inImg = imp;
        selection = sel;
        
        
        setxRes(imp.getWidth());
        setyRes(imp.getHeight());
        
        outImage = new double[xRes][yRes];
        inImage = new double[xRes][yRes];
        interpolated = false;
        tin.setResolutionRuleForMergedVertices(VertexMergerGroup.ResolutionRule.MeanValue);
        
        //readImgData(imp);
    }
    public void initialize(){
        if(inImg != null){
           generateMaskData();
           readImgData(inImg);
        }
    }

    public void readImgData(ImagePlus imp) {
        double z;
        ImageProcessor ip = inImg.getProcessor();       //assume the imagePlus has no stack
        if(ip == null){
            System.out.println("Exiting no imagedata to process");
            return;
        }
                //FILTER = 1;
                
                //Normalise = true;
                FloatProcessor dip = (FloatProcessor)ip.duplicate();
                switch(FILTER){
                    case 1 : //Gaussian
                        dip.blurGaussian(this.getBlurRad()/*BlurRad*/);
                        break;
                    case 2: //Median
                        dip.filter(ImageProcessor.MEDIAN_FILTER);
                        
                        break;
                    case 3: //No filter
                        //dip.filter(ImageProcessor.MAX);
                        break;
                    default:
                        break;
                }
        //Normalise
//               if(Normalise){
//                    ImageStatistics stat = ImageStatistics.getStatistics(dip);
//                    double intDensity = stat.area *stat.mean;
//                    if( stat.max != 0) 
//                        dip.multiply(1.0/stat.max/*intDensity*/);
//                    
//                    System.out.println("Mean before normalise :"+
//                            stat.mean+",\t"+ImageStatistics.getStatistics(dip).mean * stat.area+ "\t"+stat.max + "\t" + intDensity + "\t" + yRes);
//               }
        System.out.println("Starting the sample pts gathering ..."+ xRes + "\t" + yRes);
        //Generate maskarray
 

        double sum = 0  ;

        ImagePlus tstImage = new ImagePlus();
        tstImage.setProcessor(mask);
//        tstImage.show();
        FileSaver fs = new FileSaver(tstImage);
        fs.saveAsTiff(path+ "maskImg");
        int yStart,xStart,xStop,yStop;
        if(selection!= null){
            
            yStart = this.selbound.y;
            xStart = this.selbound.x;
            yStop = this.selbound.height + yStart;
            xStop = this.selbound.width + xStart;
        }else{
            yStart = xStart = 0;
            xStop = xRes;
            yStop = yRes;
        }
        System.out.println("Started reading from "+ xStart + ", "+yStart+ "," +"stopping at :"+xStop+"," +yStop);
        for ( int maskY = yStart,y = 0 ; maskY <= yStop ;y++, maskY++){
            //System.out.println("Started reading "+ x +"row" + " of total" + xRes);
            for(int maskX = xStart, x = 0 ; maskX <= xStop ; x++,maskX++){
        //

        //int pIdx = x + y *yRes;
        if (mask.getPixelValue(x, y) == 255 ) {
            z = Float.intBitsToFloat(dip.getPixel(maskX, maskY));
            sum += z;
            samplePts.add(new Vertex(maskX,maskY,z));
            tin.add(new Vertex (maskX,maskY,z));
            inImage[x][y] = z;
            xOrd.add(maskX);
            yOrd.add(maskY);
        }
            }
            //System.out.println("Finished reading "+ x +"row" + " of total" + xRes);
        }
        System.out.println("Finished gathering: Gathered "+ samplePts.size() + " points for interpolation" + "with sum of :"+ sum);
    }

    public void generateMaskData() {
          
            
        if (selection != null){
            //this.inImg.setRoi(selection);
            mask = (ByteProcessor) selection.getMask();
            this.selbound = selection.getBounds();
        }else{
                
            System.out.println("maskData was null so creating one..,");
            ImageProcessor tmpIp = inImg.getProcessor().duplicate();
            tmpIp.convertToByte(true);
            tmpIp.threshold(1);
            
            tmpIp.invert();
            mask = (ByteProcessor)tmpIp;
//            ThresholdToSelection t2s = new ThresholdToSelection();
            //selection = t2s.convert(mask);
        }
        //maskData  = mask.getMaskArray();
        //return maskData;
    }
    public boolean addPoint(int x, int y, double z){
        setInterpolated(false);
        
        if(x < xRes && y < yRes && !(x < 0 || y < 0)){
            inImage[x][y] = z;
            samplePts.add(new Vertex(x,y,z));
            tin.add(new Vertex(x,y,z));
            xOrd.add(x);
            yOrd.add(y);
            inImg.getProcessor().convertToFloat().putPixelValue(x, y, z);
            return true;
        }
        return false;
    }
    public ImagePlus getSurface(){
        finaliseSurface();
        return imageOutput();
    }
    public void finaliseSurface(){
        
        int nVertices = tin.getVertices().size();
        if(samplePts.isEmpty()){
            System.out.print("Can not generate surface with zero sample points \n");
            return;
        }
        else{
            System.out.print("There are " + samplePts.size() +" sample points \t"+
                    nVertices +" vertices \n");
        }
        System.out.println("Starting TIN");
        //boolean status = tin.add(samplePts, null);
       
        bound = tin.getBounds();
        int xExt = (int)bound.getWidth();
        int yExt = (int)bound.getHeight();
        
        
        
        //set the trajectory as constraints
        
        PolygonRoi inRoi = new PolygonRoi(xOrd.stream().mapToInt(i->i).toArray(), yOrd.stream().mapToInt(i->i).toArray(),xOrd.size(),Roi.POLYGON);
       
        convexHull =  (selection != null) ? selection.getConvexHull() : inRoi.getConvexHull();              //inRoi.getConvexHull();
        int[] convexX = convexHull.xpoints;
        int[] convexY = convexHull.ypoints;
        ArrayList<Vertex> verLst = new ArrayList();
        int idx = 0;
        for( int x : convexX)
            verLst.add(new Vertex(x,convexY[idx],inImage[x][convexY[idx++]]));
          PolygonConstraint cons = new PolygonConstraint(verLst);
          cons.complete();
        
          ArrayList<IConstraint> consList = new ArrayList();
            consList.add(cons);
//            tin.addConstraints(consList,true);    
            
//          ArrayList consList = new ArrayList();
////        LinearConstraint cons = new LinearConstraint(samplePts);
        
//        if(!status)
//            System.out.println("Failed generating TIN");
        if(!tin.isBootstrapped()){
            System.out.print("tin is not ready: TIN Bootstrapping failed \n");
            return;
        }
        else
            System.out.print("TIN ready \t" + xExt +"\t"+ yExt +" Bound starts at : "+bound.getX()+" , "+bound.getY()+"\n");
        
        RuppertRefiner refiner = new RuppertRefiner(tin,getminAng());         //TODO : Badly needs a user setting
        boolean status = refiner.refine();
                if(!status)
            System.out.println("Refinement failed");
        else
            System.out.println("Refinement sucessfull");
        //RendererForTinInspection renderer = new RendererForTinInspection(tin);
        
        NaturalNeighborInterpolator inter = new NaturalNeighborInterpolator(tin);
        //InverseDistanceWeightingInterpolator inter = new InverseDistanceWeightingInterpolator(tin,20,true);
        //GwrTinInterpolator inter = new GwrTinInterpolator(tin);
        
//        if(false/*isSaveTIN()*/){
//            outImg = new ImagePlus("Tin",renderer.renderImage(xRes, yRes, 5));
//            FileSaver fs = new FileSaver(outImg);
//            fs.saveAsTiff(path+"Tin");
//            outImg.close();
//        }
        outImg = new ImagePlus();
        //outImg.show();
        
        FloatProcessor outImgip = new FloatProcessor(new float[xRes][yRes] );
        outImg.setProcessor(outImgip);
        double z;
        int xMin = (int) bound.getX();
        int yMin = (int) bound.getY();
        int xMax = xMin +(int) bound.getWidth();
        int yMax = yMin +(int) bound.getHeight();
        System.out.println("xMin:"+xMin+"\t"+"yMin:"+yMin+"\t"+"xMax"+xMax+"\t"+"yMax"+yMax + convexHull.getBounds().width + " " + convexHull.getBounds().height);
//        int pixelCount = 0;
        for (int x = xMin; x < xMax ; x ++ ){
            
            for(int y = yMin ; y < yMax ; y ++){
                
                    z = (convexHull.contains(x,y)) ? 
                            inter.interpolate(x,y,null) :
                               0 ;/*replace with bgd value*/
                    outImg.getProcessor().putPixelValue(x, y, z);  
                    outImage[x][y] = z ;  
//                    if(z == 0)
//                        pixelCount++;
            }
//            if(  (x % 100)  == 0)
//                System.out.println("Col"+x);ssssssssssssssssss
        }
        
        //System.out.println("There are ");
        setInterpolated(true);
        
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
    public final void setxRes(int xRes) {
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
    public final void setyRes(int yRes) {
        this.yRes = yRes;
    }

    /**
     * @return the convexHull
     */
    public Polygon getConvexHull() {
        return convexHull;
    }

    /**
     * @param convexHull the convexHull to set
     */
    public void setConvexHull(Polygon convexHull) {
        this.convexHull = convexHull;
    }

    /**
     * @return the bound
     */
    public Rectangle2D getBound() {
        return bound;
    }

    /**
     * @param bound the bound to set
     */
    public void setBound(Rectangle2D bound) {
        this.bound = bound;
    }

    /**
     * @param inImage the inImage to set
     */
    public void setInImage(double[][] inImage) {
        //add code to initialise the samplepoints
        this.inImage = inImage;
    }

    /**
     * @return the outImage
     */
    public double[][] getOutImage() {
        return (this.isInterpolated())? outImage :null;
    }

    /**
     * @return the interpolated
     */
    public boolean isInterpolated() {
        return interpolated;
    }

    private void setInterpolated(boolean interpolated) {
        this.interpolated = interpolated;
    }
}
