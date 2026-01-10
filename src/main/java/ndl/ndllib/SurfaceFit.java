package ndl.ndllib;



import Jama.Matrix;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.RoiScaler;
import ij.plugin.filter.GaussianBlur;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.FloatStatistics;
import ij.process.ImageProcessor;
import java.awt.Rectangle;




/**
 *
 * @author balam
 */
public final class SurfaceFit {

    /**
     * @return the assymGauss
     */
    public boolean isAssymGauss() {
        return assymGauss;
    }

    /**
     * @param assymGauss the assymGauss to set
     */
    public void setAssymGauss(boolean assymGauss) {
        this.assymGauss = assymGauss;
    }

    private boolean assymGauss;

    /**
     * @return the GaussRadX
     */
    public double getGaussRadX() {
        return GaussRadX;
    }

    /**
     * @param RadX the GaussRadX to set
     */
    public void setRadX(double RadX) {
        this.GaussRadX = RadX;
    }

    /**
     * @return the GaussRadY
     */
    public double getGaussRadY() {
        return GaussRadY;
    }

    /**
     * @param RadY the GaussRadY to set
     */
    public void setRadY(double RadY) {
        this.GaussRadY = RadY;
    }

    /**
     * @return the preScale
     */
    public boolean isPreScale() {
        return preScale;
    }

    /**
     * @param preScale the preScale to set
     */
    public void setPreScale(boolean preScale) {
        this.preScale = preScale;
    }

    /**
     * @return the GaussFilt
     */
    public boolean isGaussFilt() {
        return GaussFilt;
    }

    /**
     * @param GaussFilt the GaussFilt to set
     */
    public void setGaussFilt(boolean GaussFilt) {
        this.GaussFilt = GaussFilt;
    }

    /**
     * @return the scaleBy
     */
    public double getScaleBy() {
        return scaleBy;
    }

    /**
     * @param scaleBy the scaleBy to set
     */
    public void setScaleBy(double scaleBy) {
        this.scaleBy = scaleBy;
    }

    /**
     * @return the gaussCtrX
     */
    public Integer getGaussCtrX() {
        return gaussCtrX;
    }

    /**
     * @param gaussCtrX the gaussCtrX to set
     */
    public void setGaussCtrX(Integer gaussCtrX) {
        this.gaussCtrX = gaussCtrX;
    }

    /**
     * @return the gaussCtrY
     */
    public Integer getGaussCtrY() {
        return gaussCtrY;
    }

    /**
     * @param gaussCtrY the gaussCtrY to set
     */
    public void setGaussCtrY(Integer gaussCtrY) {
        this.gaussCtrY = gaussCtrY;
    }

    /**
     * @return the gaussRad
     */
    public double getGaussRad() {
        return gaussRad;
    }

    /**
     * @param gaussRad the gaussRad to set
     */
    public void setGaussRad(double gaussRad) {
        this.gaussRad = gaussRad;
    }

    /**
     * @return the gFit
     */
    public double[][] getgFit() {
        return gFit;
    }

    /**
     * @param gFit the gFit to set
     */
    private void setgFit(double[][] gFit) {
        this.gFit = gFit;
    }
    public SurfaceFit(){
        setPolyOrderX(5);
        setPolyOrderY(5);
    }
    public SurfaceFit(int PolyX, int PolyY){
        this.setPolyOrderX(PolyX);
        this.setPolyOrderY(PolyY);
    }
    public SurfaceFit( SurfaceFit fit){
        GaussFilt = fit.GaussFilt;
        OriginalX = fit.OriginalX;
        OriginalY = fit.OriginalY;
        
        this.setGaussCtrX(fit.getGaussCtrX());
        this.setGaussCtrY(fit.getGaussCtrY());
        this.setGaussRad(fit.getGaussRad());
        
        this.setPreScale(fit.isPreScale());
        this.setSelectPixels(fit.isSelectPixels());
        this.setUseSelection(fit.isUseSelection());

        this.setPolyOrderX(fit.getPolyOrderX());
        this.setPolyOrderY(fit.getPolyOrderY());
    }
    private double [][] gFit ;
    /**
     * @return the PolyOrderY
     */
    public synchronized int getPolyOrderY() {
        return PolyOrderY;
    }

    /**
     * @param PolyOrderY the PolyOrderY to set
     */
    public synchronized void setPolyOrderY(int PolyOrderY) {
        this.PolyOrderY = PolyOrderY;
    }

    /**
     * @return the PolyOrderX
     */
    public synchronized int getPolyOrderX() {
        return PolyOrderX;
    }

    /**
     * @param PolyOrderX the PolyOrderX to set
     */
    public synchronized void setPolyOrderX(int PolyOrderX) {
        this.PolyOrderX = PolyOrderX;
    }
    
    private int PolyOrderX;
    private int PolyOrderY;
    
    private boolean preScale = false;
    private boolean GaussFilt = false;
    private boolean UseSelection = true;
    private boolean SelectPixels = false;
    
    private double  scaleBy = 1.0;
    
    private Integer   gaussCtrX = null;
    private Integer  gaussCtrY = null;
    private double  gaussRad = 1.0;
    private double GaussRadX = 1.0;
    private double GaussRadY = 1.0;
    
    private int OriginalX;
    private int OriginalY;
    
public  double[][] FitSurfaceCoeff( double[][] TheImage )
{
    int Nrows = TheImage.length;
    int Ncols = TheImage[0].length;
    int Npixels = Nrows*Ncols;
    int r, c, cnt, i, j, k, nCol;
    int Dim1, Dim2;
    int PO_2xp1 = Math.max((2 * getPolyOrderX() + 1), (2 * getPolyOrderY() + 1));
    int MatSize = (getPolyOrderX()+1)*(getPolyOrderY()+1);

    // Create the x, y, and z arrays from which the image to be fitted
    double []X = new double[Npixels];
    double []Y = new double[Npixels];
    double []Z = new double[Npixels];
    cnt = 0;
    double zVal;
   // System.out.print("X_Order_: "+getPolyOrderX()+" Y Order : " + getPolyOrderY());      
    for(r=0; r<Nrows; r++) {
        for(c=0; c<Ncols; c++) {
            
            zVal = TheImage[r][c];
            if (/*zVal != Float.NaN && */!Double.isNaN(zVal)){
                X[cnt] = c;
                Y[cnt] = r;
                Z[cnt] = zVal;
                /*
                    Check here if the col has enough data points otherwise ignore the col
                    increment the cnt counter and add to sum only if the no of data points condition is satisfied.
                
                */
                cnt++;
            }
        }
    }
    /**
     * Check if the cnt > the number of free parameters (degrees of freedom) of a polynomial surface. If not 
     * do not consider this data set.
     */
//System.out.println("Number of non NaN is :"+cnt+" Out of: "+Nrows*Ncols);
    // Notation:
    //  1)  The matrix [XY] is made up of sums (over all the pixels) of the
    //      row & column indices raised to various powers.  For example,
    //      sum( y^3 * x^2 ).  It turns out, within [XY] there are 
    //      patterns to the powers and these patterns are computed
    //      in the matrices [nnx] and [nny].
    //  2)  [Sxyz] represents all of the possible sums that will be used to
    //      create [XY] and [Z].  We compute all of these sums even though 
    //      some of them might not be utilized... it's just easier.
    double [][]XY_mat = new double[MatSize][MatSize];
    int [][]nnx = new int[MatSize][MatSize];
    int [][]nny = new int[MatSize][MatSize];
    int []aRow = new int[MatSize];

    // Create all the possible sums, Sxyz[][][]
    //IJ.showProgress(1,6);
    //IJ.showStatus("Preparing sums matrix");
    double[][][] Sxyz = new double[PO_2xp1][PO_2xp1][2];
    double x, y, z;
    double powx, powy, powz;
    int nx, ny, nz;
    // Initialize all of the sums to zero
    for(nx=0; nx<PO_2xp1; nx++) {
        for(ny=0; ny<PO_2xp1; ny++) {
            for(nz=0; nz<2; nz++) {
                Sxyz[nx][ny][nz] = 0.0;
            }
        }
    }
    // Produce the sums
    for( i=0; i<Npixels; i++) {
        x = X[i]; y = Y[i]; z = Z[i];
        for(nx=0; nx<PO_2xp1; nx++) {
            powx = java.lang.Math.pow(x,(double)nx);
            for(ny=0; ny<PO_2xp1; ny++) {
                powy = java.lang.Math.pow(y,(double)ny);
                for(nz=0; nz<2; nz++) {
                    powz = java.lang.Math.pow(z,(double)nz);
                    Sxyz[nx][ny][nz] += powx * powy * powz;
                }
            }
        }
    }

    // Create the patterns of "powers" for the X (horizontal) pixel indices
    //IJ.showProgress(2,6);
    int iStart = 2 * getPolyOrderX();
    Dim1 = 0;
    while(Dim1<MatSize) {
        for(i=0; i<(getPolyOrderY()+1); i++) {
            // A row of nnx[][] consists of an integer that starts with a value iStart and
            //  1) is repeated (PolyOrderX+1) times
            //  2) decremented by 1
            //  3) Repeat steps 1 and 2 for a total of (PolyOrderY+1) times
            nCol = 0;
            for(j=0; j<(getPolyOrderX()+1); j++ ) {
                for(k=0; k<(getPolyOrderY()+1); k++) {
                    aRow[nCol] = iStart - j;
                    nCol++;
                }
            }
            // Place this row into the nnx matrix
            for(Dim2=0; Dim2<MatSize; Dim2++ ) {
                nnx[Dim1][Dim2] = aRow[Dim2];
            }
            Dim1++;
        }
        iStart--;
    }
    
    // Create the patterns of "powers" for the Y (vertical) pixel indices
    //IJ.showProgress(3,6);
    Dim1 = 0;
    while(Dim1<MatSize) {
        iStart = 2 * getPolyOrderY();
        for(i=0; i<(getPolyOrderY()+1); i++) {
            // A row of nny[][] consists of an integer that starts with a value iStart and
            //  1) place in matrix
            //  2) decremented by 1
            //  3) 1 thru 2 are repeated for a total of (PolyOrderX+1) times
            //  4) 1 thru 3 are repeat a total of (PolyOrderY+1) times
            nCol = 0;
            for(j=0; j<(getPolyOrderX()+1); j++ ) {
                for(k=0; k<(getPolyOrderY()+1); k++) {
                    aRow[nCol] = iStart - k;
                    nCol++;
                }
            }
            // Place this row into the nnx matrix
            for(Dim2=0; Dim2<MatSize; Dim2++ ) {
                nny[Dim1][Dim2] = aRow[Dim2];
            }
            Dim1++;
            iStart--;
        }
    }

    // Put together the [XY] matrix
	for(r=0; r<MatSize; r++) {
		for(c=0; c<MatSize; c++) {
			nx = nnx[r][c];
			ny = nny[r][c];
			XY_mat[r][c] = Sxyz[nx][ny][0];
		}
	}

    // Put together the [Z] vector
    //IJ.showProgress(4,6);
	double[] Z_mat = new double[MatSize];
    c = 0;
    for(i=getPolyOrderX(); i>=0; i--) {
		for(j=getPolyOrderY(); j>=0; j--) {
            Z_mat[c] = Sxyz[i][j][1];
            c++;
        }
    }

    // Solve the linear system [XY] [P] = [Z] using the Jama.Matrix routines
	// 	[A_mat] [x_vec] = [b_vec]
	// (see example at   http://math.nist.gov/javanumerics/jama/doc/Jama/Matrix.html)
    //IJ.showProgress(5,6);
    //IJ.showStatus("Solving linear system of equations");
	Matrix A_mat = new Matrix(XY_mat);
	Matrix b_vec = new Matrix(Z_mat, MatSize);
	Matrix x_vec = A_mat.solve(b_vec);

	// Place the Least Squares Fit results into the array Pfit
	double[] Pfit = new double[MatSize];
	for(i=0; i<MatSize; i++) {
		Pfit[i] = x_vec.get(i, 0);
	}

	// Reformat the results into a 2-D array where the array indices
    // specify the power of pixel indices.  For example,
    // z =    (G[2][3] y^2 + G[1][3] y^1 + G[0][3] y^0) x^3
    //      + (G[2][2] y^2 + G[1][2] y^1 + G[0][2] y^0) x^2
    //      + (G[2][1] y^2 + G[1][1] y^1 + G[0][1] y^0) x^1
    //      + (G[2][0] y^2 + G[1][0] y^1 + G[0][0] y^0) x^0
    double[][] Gfit = new double[getPolyOrderY() + 1][getPolyOrderX() + 1];
    c = 0;
    for(i=getPolyOrderX(); i>=0; i--) {
		for(j=getPolyOrderY(); j>=0; j--) {
            Gfit[j][i] = Pfit[c];
            c++;
        }
    }
    this.setgFit(Gfit);
   // System.out.println("Xlen= "+Gfit.length);
    //System.out.println("Length of Y = "+ Gfit[0].length);
    
    return ( Gfit );
} 
/** * It is the workhorse of class.This prepares the data for fitting a polynomial and calls the  FitSurfaceCoef()
 to obtain the fit parameters.In this case these are the coefficients of the polynomial.The polyOrder needs to be set or default of order 5 is assumed. 
 *
 * @param sp The image processor (ImageJ) for the image    
 * @param sel   An Roi object (ImageJ) that represents the area of interest (read on for the options and its meaning)
 * @return It returns a float processor representing the image that is fit
 * 
 * Specific Cases: If sel is null false square/rectangle region of interest as such 
 * sel has a Roi and selPixels is false square/rectangle region of interest with 0 for pixels of unmasked
 * sel has Roi and selpixels is true just the pixels that are selected by roi mask
 **/
public FloatProcessor FitSurface(ImageProcessor sp, Roi sel){
    double mean ;
    ImageProcessor ip = sp.duplicate();// = (this.isPreScale())? scale(sp): sp.convertToFloatProcessor();
    if(isPreScale()){       
        ip = scale(sp);
        sel = scaleRoi(sel); 
        //ip.setRoi(sel);
    }
    ip = (this.isGaussFilt())?gaussSmooth(ip,sel): ip;
    var fp = new FloatStatistics(ip);
    mean = fp.mean;
    var orgMean = mean;
    int rx, ry, rw, rh;
    byte[] maskData = null; 
    double selVal = 0;
    if(!this.isUseSelection()&& !this.SelectPixels)
        ip.resetRoi();
    if(sel != null && isUseSelection()){
        ip.setRoi(sel);
        mean = new FloatStatistics(ip).mean;
        var bRect =sel.getBounds();
        rx = bRect.x;
        ry = bRect.y;
        rw = bRect.width;
        rh = bRect.height;
         //javax.swing.JOptionPane.showConfirmDialog(null, "Now is the test");
        if(isGaussFilt() && sel.getType() != Roi.RECTANGLE){            
            ImageProcessor mask = sel.getMask();
            if(mask != null){
               mask.blurGaussian(this.getGaussRad());
               ByteProcessor byteMask = mask.convertToByteProcessor(true);
               byteMask.setThreshold(1,255);
               if(!byteMask.isThreshold())
                  byteMask.threshold(1, 255);
               ImageProcessor newmask = byteMask.createMask();
               maskData = (byte[])newmask.getPixels();
            }
        }else
            maskData = ip.getMaskArray();
        selVal = (this.isSelectPixels())? Double.NaN : 0;   //roi is provided but unsampled space inside the rect sele is filled with 0
    }else{                                   //selection is not provided by the user        
        Rectangle rectRoi = ip.getRoi();  
        rx = rectRoi.x;
        ry = rectRoi.y;
        rw = rectRoi.width;
        rh = rectRoi.height;
     //   selVal = (selPixels)? Double.NaN:0;
        //sel = new Roi(rx,ry,rw,rh);
    }
    double[][] surface = new double[rh][rw];
    mean *= -1;
    ip.add(mean);
    
    System.out.println("Mean to be added: "+mean+" Mean on entry: "+orgMean*-1+" Selection provided:  "+ ((sel != null) && this.isUseSelection()) +" Mask is "+(ip.getMask()!= null));
    System.out.println("The width is :" + rw +", "+rx+"The height is "+ rh +"," +ry);
    //System.out.println("The difference in expanding selection is "  );
//    ImagePlus imp = new ImagePlus("Aft Mean sub");
//    imp.setProcessor(ip);
//    imp.setTitle("Aft Mean sub");
//    imp.show();
    
//    float[] pixelData ;// = new float[ip.getPixelCount()];
//    pixelData = (float [])ip.getPixelsCopy();
    
    
    
    //double unSelval = 0;//(selPixels)? Double.NaN : 0;                  //if pixel level selection is required 
    //int idx = 0;
   // System.out.println("ArraySize "+ pixelData.length + "MaskSize "+maskData.length +"width="+width+" height= "+height + "");
    if(!this.isSelectPixels() || sel == null || !this.isUseSelection()){
        for(int row = ry, my = 0 ; row < (ry+rh) ; my++, row++){
           // idx = row*width + rx;
            //int midx = my*rw;
            for(int col = rx ,mx = 0 ; col < (rw+rx) ; mx++, col++){   
                    surface[my][mx] = (double)ip.getPixelValue(col, row);//(maskData == null || maskData[midx++] != 0) ?(double) ip.getPixelValue(col,row): unSelval;      
            }
        }
      //  System.out.println("Pixel level selection is off: The dimensions are (X x Y) " + surface[0].length + " , "+ surface.length);
    }else{
       // int maskPixCounter = 0;
        for(int row = ry, my = 0 ; row < ry+rh ; my++, row++){
           // idx = row*width + rx;
            int midx = my*rw;
            for(int col = rx, mx = 0 ; col < rx+rw ; mx++, col++){   
                    surface[my][mx] = ( maskData[midx++] != 0) ?(double) ip.getPixelValue(col,row): selVal;/*unSelval*/ 
                   
            }
        }
//        System.out.println("No of selected pixels: "+maskPixCounter);
    }
    double[][] SurfFit = FitSurfaceCoeff(surface);
//    int Idx = 0;
//    for(double [] coe :SurfFit)
//        for(double val : coe)
//            System.out.print(++Idx + " _= "+ val);
    
    FloatProcessor fitSurface = new FloatProcessor(rw,rh);
    //float[][] pixelVal = new float[rw][rh] ;
    double ytemp, dtemp;
    int Ny = rh ;//sel.getBounds().height;                        // selection height 
    int Nx = rw ;//sel.getBounds().width;                         // selection width
    //double[][] Svh = new double[Ny][Nx];
    mean *= -1;
        for(int iy=0; iy<Ny; iy++) {
            for(int ix=0; ix<Nx; ix++) {
                
                dtemp = 0;
                // Determine the value of the fit at pixel iy,ix
                for(int powx=PolyOrderX; powx>=0; powx--) {
                    ytemp = 0;
                    for(int powy=PolyOrderY; powy>=0; powy--) {
                        ytemp += SurfFit[powy][powx] * Math.pow((double)iy,(double)powy);
                    }
                    dtemp += ytemp * Math.pow((double)ix,(double)powx);
                }
                // Remember to add back the mean image value
                //Svh[iy][ix] = dtemp + mean;
                var pVal = (true)? dtemp+mean : 0;
                fitSurface.putPixelValue(ix, iy, pVal);
                //pixelVal[ix][iy] = (float) pVal;
                
            }
        } 
        //FloatProcessor fitSurface = new FloatProcessor(pixelVal);
//        ImagePlus fitTst = new ImagePlus("Tst");
//        fitTst.setProcessor(fitSurface);
//        fitTst.show();
    if(this.isPreScale())
       fitSurface = (FloatProcessor) this.rescale(fitSurface);
//        FloatProcessor fitSurface = new FloatProcessor(pixelVal);
        ImagePlus fitTst = new ImagePlus("Tst");
        fitTst.setProcessor(fitSurface);
       // fitTst.show();
  return fitSurface;
 }

    private ImageProcessor scale(ImageProcessor sp) {
        
        OriginalX = sp.getWidth();
        OriginalY = sp.getHeight();
        
        ImageProcessor ip = sp.duplicate();
        ip.resetRoi();
        ip.setInterpolationMethod(ImageProcessor.BICUBIC);
        double scaleto = getScaleBy();
        ip.resize((int)Math.round(scaleto*ip.getWidth()),(int)Math.round(scaleto*ip.getHeight()),true);
        
        return ip;
    }
    private ImageProcessor rescale(ImageProcessor ip){
        //ImageProcessor sp = ip.duplicate();
        double rescale = 1/getScaleBy();
        ip.resetRoi();
        ip.setInterpolationMethod(ImageProcessor.BICUBIC);
        //sp = sp.resize((int)Math.round(rescale*sp.getWidth()),(int)Math.round(rescale*sp.getHeight()),true);
        ImageProcessor sp = ip.resize(OriginalX,OriginalY,true);
        System.out.printf(" Rescaled the image by %f\n", rescale);
        System.out.printf(" The new imagewidth is %d x %d\n", sp.getWidth(),sp.getHeight());
        //ip = sp;
        return sp;
//        ImageProcessor selMask  = sel.getMask();
//        selMask.scale(rescale, rescale);
//        selMask.setThreshold(1, 255, 0);
//        Roi scaledRoi = new ThresholdToSelection().convert(selMask);
//        sel = scaledRoi;
//        
//        sp.setRoi(sel);
    }
    private Roi scaleRoi(Roi sel){
        
        return RoiScaler.scale(sel, getScaleBy(), getScaleBy(), false);
        
    }

    private ImageProcessor gaussSmooth(ImageProcessor sp, Roi sel) {
        if(isAssymGauss())
            gaussSmooth(sp,sel,0.02);
        ImageProcessor ip = sp.duplicate();
//        if(sel != null){
//            ShapeRoi sr = new ShapeRoi(sel);
//            sp.setRoi(sr);
//        }
        GaussianBlur gBlur = new GaussianBlur();
        gBlur.blurGaussian(ip, this.getGaussRad());
        return ip;
    }
    private ImageProcessor gaussSmooth(ImageProcessor sp, Roi sel, double acc){
        if( acc >= 1)
            acc = 0.02;
        ImageProcessor ip = sp.duplicate();
        if(sel != null)
            sp.setRoi(sel);
        GaussianBlur gBlur = new GaussianBlur();
        gBlur.blurGaussian(ip, this.getGaussRadX(),this.getGaussRadY(),acc);
        return ip;
    }

    /**
     * @return the UseSelection
     */
    public boolean isUseSelection() {
        return UseSelection;
    }

    /**
     * @param UseSelection the UseSelection to set
     */
    public void setUseSelection(boolean UseSelection) {
        this.UseSelection = UseSelection;
    }

    /**
     * @return the SelectPixels
     */
    public boolean isSelectPixels() {
        return SelectPixels;
    }

    /**
     * @param SelectPixels the SelectPixels to set
     */
    public void setSelectPixels(boolean SelectPixels) {
        this.SelectPixels = SelectPixels;
    }
}
