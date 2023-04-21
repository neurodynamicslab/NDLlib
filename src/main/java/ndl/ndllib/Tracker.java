package ndl.ndllib;

import org.opencv.core.*;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.lang.Thread;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tracker extends Thread {
    protected String dataFileName;                         // Path to Video File
    // Note: for now, only implements rectangle and takes this as shape
    protected int[] fieldROI = new int[]{0, 0, 0, 0, 0}; // Roi of tracking; {Type, x1, y1, x2, y2}; Type:1->Rectangle, 2->Circle, 0->uninitialized
    protected int[] objectROI = new int[]{0, 0, 0, 0};   // Roi of object in current frame; Parameters same as above, excluding type;always rect
    protected int inclusionRadius = 0;                     // Radius within which the object must be in next frame
    protected int fps;                                     // Frame per second of current
    protected int startFrame=0, endFrame=1800, currFrameNo;
    protected Mat currFrame, bg;
    protected int[] threshold = new int[]{0, 0};
    protected TrackerEventHandler eventHandler;
    
    protected List<MatOfPoint> contours;
    public boolean tracking = false;
    protected VideoCapture cap = null;
    protected String file_path;
    protected int[] lastLocation;
    protected boolean lastLocationSet = false;
    protected boolean sleep = true;
    protected List<int[]> path = new ArrayList<>();
    
    protected Mat gray, thresh, hierarchy, result;                 // To prevent creation of new variables at every frame
    
    static {
        nu.pattern.OpenCV.loadShared();
        int RECTANGLE = 0;
        int ELLIPSE = 1;
    }

    /**
     * Default Initializer
     * @param eh a class that implements TrackerEventHandler interface (Member of this class)
     */
    public Tracker(TrackerEventHandler eh) {
        this.eventHandler = eh;
    }

    /**
     * Static function that computes which of the points are within the given radius from a point.
     * @param point The point from which the distance of al other points, given in second argument, is checked. Format: {x, y}
     * @param points An array containing list of points to be checked. Same format as point argument.
     * @param inclusionRadius Maximum distance from param point.
     * @return Returns an array of points from param points which closer than inclusion radius from point.
     * Format of return: {Index in points arg, distance from given point, x-coord of point, y-coord of point}.
     * Return in ascending order of distance from point (Closest to Farthest)
     */
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
        Arrays.sort(pointIndices, Comparator.comparingDouble(a -> distances[a[0]]));
        
        // Create an array of the points and their indices within the inclusion radius
        int numPointsWithinRadius = 0;
        for (int i = 0; i < points.length; i++) {
            if (pointIndices[i][1] == 1) {
                numPointsWithinRadius++;
            }
        }
        int[][] pointsWithinRadius = new int[numPointsWithinRadius][4];
        int j = 0;
        for (int i = 0; i < points.length; i++) {
            if (pointIndices[i][1] == 1) {
                pointsWithinRadius[j++] = new int[]{pointIndices[i][0], pointIndices[i][1], points[pointIndices[i][0]][0], points[pointIndices[i][0]][1]};
            }
        }
        
        return pointsWithinRadius;
    }

    /**
     * Finds the nearest contour to the position in the previous frame and asserts it as the position in current frame
     * @param image The current image of the video, to be tracked.
     * @return Returns the given image, cropped to field ROI(SEE: setFieldROI()), with the nearest contour drawn on it.
     */
    public Mat findNearestContour(Mat image) {
        // Convert the image to grayscale and apply binary thresholding
        Rect rectCrop = new Rect(fieldROI[0], fieldROI[1], fieldROI[2]-fieldROI[0],fieldROI[3]-fieldROI[1]);
        image = new Mat(image, rectCrop);
        gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        thresh = new Mat();
        int threshCode = Imgproc.THRESH_BINARY;
        if (this.threshold[1]-this.threshold[0] == 255) {
            threshCode+=Imgproc.THRESH_OTSU;
        }
        Imgproc.threshold(gray, thresh, this.threshold[0], this.threshold[1], threshCode);
        
        // Find the contours in the thresholded image
        contours = new ArrayList<>();
        hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        
        // Convert the point to an int array
        int[] pointArray = {this.path.get(path.size()-1)[2], this.path.get(path.size()-1)[3]};
        
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
        int nearestContourIndex = nearestContourPoints[0][0];
        
        // Draw the nearest contour on a copy of the original image
        result = image.clone();
        Imgproc.drawContours(result, contours, nearestContourIndex, new Scalar(0, 255, 0), 2);
        
        this.path.add(nearestContourPoints[0]);
        
        return result;
    }
    /*private Mat generateContours(Mat image) {
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
    }*/

    private void track() {
        currFrameNo = 1;
        if (cap==null) {
            throw new RuntimeException("Must call setDataFileName before tracking.");
        }
        double fps = cap.get(5);
        double spf = 1 / fps;
        boolean cont = true;
        while (cap.isOpened() && nextFrame()) {
            if (currFrame == null) break;
            if ((currFrameNo<startFrame)||(currFrameNo>endFrame)) {
                cont = this.eventHandler.loopCall(currFrame, cont);
                try {
                    if (this.sleep) Thread.sleep((long) (spf));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!cont) continue;
            if (!nextFrame()) break;
            currFrameNo++;
        }
        cap.release();
    }

    private synchronized boolean nextFrame() {
        currFrame = new Mat();
        boolean ret = cap.read(currFrame);
        if (!ret) return false;
        if (tracking) currFrame = findNearestContour(currFrame);
        return true;
    }
    
    public void run() {
        this.track();
    }
   
   /* -------------------------------------Getters and Setters-----------------------------------------------*/

    /**
     * Sets the current frame as background frame.
     */
   public synchronized void setBackgroundFrame() {
       this.bg = this.currFrame;
   }

    /**
     * @return Returns a DataTrace Object, containing data about the path which was tracked in the video.
     */
   public synchronized DataTrace_ver_3 getPath() {
       DataTrace_ver_3 dt = new DataTrace_ver_3();
       for (int[] point: this.path) {
           dt.addData(point[2], point[3]);
       }
       return dt;
   }

    /**
     * Setter for the start location.
     * @param location First location of the mouse (on the starting frame).
     * @return Returns whether it succeeded or not. Return True if start location has not been set before;else false;
     * (The function fails if start location has been set before)
     */
   public synchronized boolean setStartLocation(int[] location) {
       if (this.lastLocationSet) return false;
       this.lastLocation = location;
       this.path.add(new int[]{0, 0, location[0], location[1]});
       this.lastLocationSet = true;
       return true;
   }
   
   // Getters and setters for fields
   // Some Setters may not be seen
   // This is intentional, as they should not be changed after initialisation

    /**
     * Set the path to the video to be tracked.
     * Must always be called before start()
     * If called after start() also, then the class assumes that it has to continue tracking onto the next video.
     * @param fileName
     */
   public synchronized void setDataFileName(String fileName) {
       this.dataFileName = fileName;
       cap = new VideoCapture(file_path);
   }

    /**
     * @return Return the path to the video file it is currently tracking.
     */
   public synchronized String getDataFileName() {
       return dataFileName;
   }

    /**
     * SEE ALSO: SetFiledROI()
     * @return Returns the ROI of tracking.
     */
   public synchronized int[] getFieldROI() {
       return this.fieldROI;
   }

    /**
     * Sets the Roi to be tracked
     * @param ROI region of interest to be tracked in the format: {0, x, y, x1, y1}, where x is left coordinate, y is top coordinate,
     *            x1 is right coordinate and y1 is bottom coordinate.
     */
   public synchronized void setFieldROI(int[] ROI) {
       this.fieldROI = ROI;
   }
   
   /*public synchronized int[] getObjectROI() {
       return this.objectROI;
   }
   
   private synchronized void setObjectROI(int[] ROI) {
       this.objectROI = ROI;
   }*/

    /**
     * SEE: setInclusionRadius
     * @return Inclusion radius
     */
   public synchronized int getInclusionRadius() {
       return this.inclusionRadius;
   }

    /**
     * Set the inclusion radius. The mouse(centroid) on the next frame must be within the inclusion radius.
     * @param ir The Inclusion radius.
     */
   public synchronized void setInclusionRadius(int ir) {
       this.inclusionRadius = ir;
   }

    /**
     * @return FPS of current video.
     */
   public synchronized int getFPS() {
       return this.fps;
   }

    /**
     * SEE: setStartFrame(), setEndFrame()
     * @return An int[] array of format [startFrame, endFrame]
     */
   public synchronized int[] getFrameRange() {
       return new int[]{this.startFrame, this.endFrame};
   }

    /**
     * Sets the starting frame (Relative to the first video) of tracking;
     * Frames before this are not tracked
     * @param frame the starting frame Number.
     */
   public synchronized void setStartFrame(int frame) {
       this.startFrame = frame;
   }

    /**
     * Sets the Ending frame (Relative to the first video) of tracking;
     * Frame after this are not tracked
     * @param frame
     */
   public synchronized void setEndFrame(int frame) {
       this.endFrame = frame;
   }

    /**
     * Set the thresholding limit of the tracker; Either mouse or surrounding must be within this threshold;
     * Cannot Have both mouse and surrounding or neither mouse nor surrounding.
     * @param low Lower Thresholding limit
     * @param high Upper Thresholding limit
     */
   public synchronized void setThresholding(int low, int high) {
       this.threshold[0] = low;
       this.threshold[1] = high;
   }

    /**
     * Set whether the tracking should pause at every frame, at the same rate as an actual video, or
     * track as fast as possible
     * @param shouldWait Whether the program should wait at every frame
     */
   public synchronized void setGoAtVideoPace(boolean shouldWait) {
       this.sleep = shouldWait;
   }

    /**
     * @return SEE: setGoAtVideoPace()
     */
   public synchronized boolean getGoAtVideoPace() {
       return this.sleep;
   }

    /**
     * An Interface to handle image at every looping, while allowing the code to run in a separate thread.
     */
   public interface TrackerEventHandler {
        /**
         * Called every time the video moves through a frame.
         * @param im The image after cropping to fieldROI(See setFieldROI()) and drawing the contour nearest to the previous location.
         * @param playing Whether the code is going through the video or has paused.
         * @return Whether the code should continue going through the video or not.
         */
        boolean loopCall(Mat im, boolean playing);
   }
}