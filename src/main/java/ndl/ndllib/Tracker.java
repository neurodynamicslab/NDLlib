package ndl.ndllib;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.lang.Thread;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Tracker extends Thread {
    protected String dataFileName;                           // Path to Video File
    protected int[] fieldROI = new float[]{0, 0, 0, 0, 0}; // Roi of tracking; {Type, x1, y1, x2, y2}; Type:1->Rectangle, 2->Circle, 0->uninitialized
    protected int[] objectROI = new float[]{0, 0, 0, 0};   // Roi of object in current frame; Parameters same as above, excluding type;always rect
    protected int inclusionRadius = 0;                     // Radius within which the object must be in next frame
    protected int fps;                                       // Frame per second of current
    protected int startFrame, endFrame;
    protected int[][] currFrame;
    protected int[] threshold = new int[]{0, 0};
    protected TrackerEventHandler eventHandler;
    
    protected List<Mat> contours = new ArrayList<>();
    public boolean tracking = false, playing = false;
    protected VideoCapture cap = null;
    protected Mat bg = null, frame = null;
    protected String file_path;
    protected int[] lastLocation;
    protected boolean lastLocationSet = false;
    
    protected
    
    static {
        nu.pattern.OpenCV.loadShared();
    }
    
    public Tracker(TrackerEventHandler eh) {
        this.file_path = file_path;
        this.eventHandler = eh;
    }
    
    public static int[][] findPointsWithinRadius(int[] point, int[][] points, int inclusionRadius) {
        // Create an array to hold the distances between each point and the given point
        double[] distances = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            int[] currPoint = points[i];
            distances[i] = Math.sqrt(Math.pow(point[0]-currPoint[0], 2) + Math.pow(point[1]-currPoint[1], 2));
        }
        
        // Sort the points and their indices by distance from the given point
        int[][] pointIndices = new int[points.length][2];
        for (int i = 0; i < points.length; i++) {
            pointIndices[i][0] = i;
            pointIndices[i][1] = distances[i] <= inclusionRadius ? 1 : 0;
        }
        Arrays.sort(pointIndices, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                if (distances[a[0]] < distances[b[0]]) {
                    return -1;
                } else if (distances[a[0]] > distances[b[0]]) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        
        // Create an array of the points and their indices within the inclusion radius
        int numPointsWithinRadius = 0;
        for (int i = 0; i < points.length; i++) {
            if (pointIndices[i][1] == 1) {
                numPointsWithinRadius++;
            }
        }
        int[][] pointsWithinRadius = new int[numPointsWithinRadius][2];
        int j = 0;
        for (int i = 0; i < points.length; i++) {
            if (pointIndices[i][1] == 1) {
                pointsWithinRadius[j++] = points[pointIndices[i][0]];
            }
        }
        
        return pointsWithinRadius;
    }

    public static Mat findNearestContour(Mat image, Point point) {
        // Convert the image to grayscale and apply binary thresholding
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Mat thresh = new Mat();
        int threshCode = Imgproc.THRESH_BINARY;
        if (this.threshold[1]-this.threshold[0] == 255) {
            threshCode+=Imgproc.THRESH_OTSU;
        }
        Imgproc.threshold(gray, thresh, this.threshold[0], this.threshold[1], 255, threshCode);
        
        // Find the contours in the thresholded image
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        
        // Convert the point to an int array
        int[] pointArray = {(int)point.x, (int)point.y};
        
        // Find the nearest contour to the given point
        int[][] contourPoints = new int[contours.size()][2];
        for (int i = 0; i < contours.size(); i++) {
            // Get the centroid of the contour
            Moments m = Imgproc.moments(contours.get(i));
            int cx = (int) (m.get_m10() / m.get_m00());
            int cy = (int) (m.get_m01() / m.get_m00());
            contourPoints[i][0] = cx;
            contourPoints[i][1] = cy;
        }
        int[][] nearestContourPoints = findPointsWithinRadius(pointArray, contourPoints, this.inclusionRadius);
        if (nearestContourPoints.length == 0) {
            return image;
        }
        int nearestContour = nearestContourPoints[0][0];
        
        // Draw the nearest contour on a copy of the original image
        Mat result = image.clone();
        Imgproc.drawContours(result, contours, nearestContour, new Scalar(0, 255, 0), 2);
        
        return result;
    }


    private Mat generateContours(Mat image) {
        if (this.bg==null) return null;
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(gray, gray, this.threshold[0], this.threshold[1], Imgproc.THRESH_BINARY);
        Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat output = image.clone();
        for (Mat cnt : contours) {
            Mat circle = new Mat();
            Imgproc.minEnclosingCircle(cnt, new Point(), circle);
            double[] c = circle.get(0, 0);
            Point center = new Point(c[0], c[1]);
            int radius = (int) c[2];
            Imgproc.circle(output, center, radius, new Scalar(255, 255, 255), 2);
        }
        return output;
    }

    private void track() {
        if (cap==null) {
            throw new RuntimeException("Must call setDataFileName before tracking.");
        }
        double fps = cap.get(5);
        double spf = 1 / fps;
        boolean cont = true;
        frame = nextFrame();
        while (cap.isOpened()) {
            if (frame == null) break;
            cont = this.eventHandler.loopCall(frame, cont);
            try {
                Thread.sleep((long) (spf));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!cont) continue;
            frame = nextFrame();
        }
        cap.release();
    }

    private Mat nextFrame() {
        Mat frame = new Mat();
        boolean ret = cap.read(frame);
        if (!ret) return null;
        if (fframe == null) fframe = frame.clone();
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(frame, frame, new Size(frame.cols() / 2, frame.rows() / 2));
        if (tracking) frame = findNearestContour(frame);
        return frame;
    }
    
    public void run() {
        this.track();
    }
   
   /* -------------------------------------Getters and Setters-----------------------------------------------*/
    
   public synchronized void setBackgroundFrame() {
       this.bg = this.frame;
   }
   
   public synchronized DataTrace getPath() {}
   
   public synchronized boolean setStartLocation(float[] location) {
       if (this.lastLocationSet) return false;
       this.lastLocation = location;
       this.lastLocationSet = true;
       return true;
   }
   
   // Getters and setters for fields
   // Some Setters may not be seen
   // This is intentional, as they should not be changed after initialisation
   public synchronized void setDataFileName(String fileName) {
       this.dataFileName = fileName;
       cap = new VideoCapture(file_path);
   }
   
   public synchronized String getDataFileName() {
       return dataFileName;
   }
   
   public synchronized int[] getFieldROI() {
       return this.fieldROI;
   }
   
   public synchronized void setFieldROI(int[] ROI) {
       this.fieldROI = ROI;
   }
   
   public synchronized int[] getObjectROI() {
       return this.objectROI;
   }
   
   private synchronized void setObjectROI(int[] ROI) {
       this.objectROI = ROI;
   }
   
   public synchronized int getInclusuionRadius() {
       return this.inclusionRadius;
   }
   
   public synchronized void setInclusuionRadius(int ir) {
       this.inclusionRadius = ir;
   }
   
   public synchronized float getFPS() {
       return this.fps;
   }
   
   public synchronized float[] getFrameRange() {
       return new float[]{this.startFrame, this.endFrame};
   }
   
   public synchronized void setStartFrame(int frame) {
       this.startFrame = frame;
   }
   
   public synchronized void setEndFrame(int frame) {
       this.endFrame = frame;
   }
   
   public synchronized void setThresholding(int low, int high) {
       this.threshold[0] = low;
       this.threshold[1] = high;
   }
   
   public static interface TrackerEventHandler {
        boolean loopCall(Mat im, boolean playing);
   }
}