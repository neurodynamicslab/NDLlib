
package ndl.ndllib;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.measure.Measurements;
import ij.process.FloatProcessor;
import ij.process.FloatStatistics;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.tinfour.common.IConstraint;
import org.tinfour.common.LinearConstraint;
import org.tinfour.common.PolygonConstraint;
import org.tinfour.common.Vertex;
import org.tinfour.common.VertexMergerGroup;
import org.tinfour.gwr.GwrTinInterpolator;
import org.tinfour.interpolation.InverseDistanceWeightingInterpolator;
import org.tinfour.interpolation.NaturalNeighborInterpolator;
import org.tinfour.refinement.RuppertRefiner;
import org.tinfour.standard.IncrementalTin;
import org.tinfour.utils.rendering.RendererForTinInspection;


/**
 *
 * @author balaji
 */
public class Natural_NeighInter {

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

    private int FILTER = 0;
    private double BlurRad = 1;
    private boolean Normalise = true;
    private boolean saveTIN = true;

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
    public Natural_NeighInter(ImagePlus imp){
        
        setxRes(imp.getWidth());
        setyRes(imp.getHeight());
        
        outImage = new double[xRes][yRes];
        inImage = new double[xRes][yRes];
        interpolated = false;
        tin.setResolutionRuleForMergedVertices(VertexMergerGroup.ResolutionRule.MeanValue);
        if(imp.hasImageStack()){
           inImg = new ImagePlus();
           inImg.setProcessor(imp.getImageStack().getProcessor(0));
           
        }else
            inImg = imp;
        double z;
        //inImg.setProcessor(new FloatProcessor(new float[xRes][yRes]));
        ImageProcessor ip = inImg.getProcessor();
        ImageProcessor dip = ip.duplicate();
        switch(FILTER){
            case 1 : //Gaussian
                dip.blurGaussian(BlurRad);
                break;
            case 2: //Median
                dip.filter(ImageProcessor.MEDIAN_FILTER);
                break;
            case 3: //Maximum
                dip.filter(ImageProcessor.MAX);
                break;
        }
        //Normalise
       if(Normalise){
            ImageStatistics stat = ImageStatistics.getStatistics(dip);
            double intDensity = stat.area *stat.mean;
            dip.multiply(1/intDensity);
       }   
        System.out.println("Starting the sample pts gathering ..."+ xRes + "\t" + yRes);
        for ( int x = 0 ; x < xRes ; x++){
            System.out.println("Started reading "+ x +"row" + " of total" + xRes);
            for(int y = 0 ; y < yRes ; y++){
//              
                z  = ip.getPixelValue(x, y);
//                
//                inImage[x][y] = z;
               if (z > 0) {
                    double pixel = dip.getPixelValue(x, y);
                    samplePts.add(new Vertex(x,y,pixel)); 
                    tin.add(new Vertex (x,y,pixel));
                    inImage[x][y] = pixel;
                    xOrd.add(x);
                    yOrd.add(y);
               }
            }
            System.out.println("Finished reading "+ x +"row" + " of total" + xRes);
        }
        System.out.println("Finished gathering: Gathered "+ samplePts.size() + " points for interpolation");
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
                    nVertices +"vertices \n");
        }
        System.out.println("Starting TIN");
        //boolean status = tin.add(samplePts, null);
       
        bound = tin.getBounds();
        int xExt = (int)bound.getWidth();
        int yExt = (int)bound.getHeight();
        
        
        
        //set the trajectory as constraints
        
        PolygonRoi inRoi = new PolygonRoi(xOrd.stream().mapToInt(i->i.intValue()).toArray(), yOrd.stream().mapToInt(i->i.intValue()).toArray(),xOrd.size(),Roi.POLYGON);
       
        convexHull = inRoi.getConvexHull();
        int[] convexX = convexHull.xpoints;
        int[] convexY = convexHull.ypoints;
        ArrayList<Vertex> verLst = new ArrayList();
        int idx = 0;
        for( int x : convexX)
            verLst.add(new Vertex(x,convexY[idx++],1.0));
        PolygonConstraint cons = new PolygonConstraint(verLst);
        //ArrayList consList = new ArrayList();
//        LinearConstraint cons = new LinearConstraint(samplePts);
        ArrayList<IConstraint> consList = new ArrayList();
        consList.add(cons);
        tin.addConstraints(consList,true);
        
        //if(!status)
          //  System.out.println("Failed generating TIN");
        if(!tin.isBootstrapped()){
            System.out.print("tin is not ready: TIN Bootstrapping failed \n");
            return;
        }
        else
            System.out.print("TIN ready \t" + xExt +"\t"+ yExt +"\n");
        
        RuppertRefiner refiner = new RuppertRefiner(tin,5);
        boolean status = refiner.refine();
        if(!status)
            System.out.println("Refinement failed");
        else
            System.out.println("Refinement sucessfull");
        RendererForTinInspection renderer = new RendererForTinInspection(tin);
        
        NaturalNeighborInterpolator inter = new NaturalNeighborInterpolator(tin);
        //InverseDistanceWeightingInterpolator inter = new InverseDistanceWeightingInterpolator(tin,20,true);
        //GwrTinInterpolator inter = new GwrTinInterpolator(tin);
        
        if(isSaveTIN()){
            outImg = new ImagePlus("Tin",renderer.renderImage(xRes, yRes, 5));
            FileSaver fs = new FileSaver(outImg);
            fs.saveAsTiff(path+"Tin");
            outImg.close();
        }
        outImg = new ImagePlus();
        //outImg.show();
         
        FloatProcessor outImgip = new FloatProcessor(new float[xRes][yRes] );
        outImg.setProcessor(outImgip);
        double z = 0;
        int xMin = (int) bound.getMinX();
        int yMin = (int) bound.getMinY();
        int xMax = (int) bound.getMaxX();
        int yMax = (int) bound.getMaxY();
        System.out.println("xMin:"+xMin+"\t"+"yMin:"+yMin+"\t"+"xMax"+xMax+"\t"+"yMax"+yMax);
        for (int x = xMin; x < xMax ; x ++ ){
            
            for(int y = yMin ; y < yMax ; y ++){
                
                    z = (convexHull.contains(x,y)) ? 
                            inter.interpolate(x,y,null) :
                                0 ;/*replace with bgd value*/
                    outImg.getProcessor().putPixelValue(x, y, z);  
                    outImage[x][y] = z ;  
                
            }
            if(  (x % 100)  == 0)
                System.out.println("Col"+x);
        }
        
        
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
