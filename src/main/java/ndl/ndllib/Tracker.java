package NDL_JavaClassLib;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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
    protected float[] fieldROI = new float[]{0, 0, 0, 0, 0}; // Roi of tracking; {Type, x1, y1, x2, y2}; Type:1->Rectangle, 2->Circle, 0->uninitialized
    protected float[] objectROI = new float[]{0, 0, 0, 0};   // Roi of object in current frame; Parameters same as above, excluding type;always rect
    protected float inclusionRadius = 0;                     // Radius within which the object must be in next frame
    protected int fps;                                       // Frame per second of current
    protected int startFrame, endFrame;
    protected int[][] currFrame;
    protected TrackerEventHandler eventHandler;
    
    List<Mat> contours = new ArrayList<>();
    boolean tracking = false, playing = false;
    VideoCapture cap = null;
    Mat fframe = null;
    String file_path;
    
    static {
        nu.pattern.OpenCV.loadShared();
    }
    
    public Tracker(String file_path, TrackerEventHandler eh) {
        this.file_path = file_path;
        this.eventHandler = eh;
    }

    private Mat generateContours(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(gray, gray, 20, 255, Imgproc.THRESH_BINARY);
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
        cap = new VideoCapture(file_path);
        double fps = cap.get(5);
        double spf = 1 / fps;
        boolean cont = true;
        Mat frame;
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
        if (tracking) frame = generateContours(frame);
        return frame;
    }
   
   /* -------------------------------------Getters and Setters-----------------------------------------------*/
    
   public void setBackgroundFrame() {}
   
   public DataTrace getPath() {}
    
    public void run() {
        this.track()
    }
   
   
   // Getters and setters for fields
   // Some Setters may not be seen
   // This is intentional, as they should not be changed after initialisation
   public String getDataFileName() {
       return dataFileName;
   }
   
   public float[] getFieldROI() {
       return this.fieldROI;
   }
   
   public void setFieldROI(float[] ROI) {
       this.setFieldROI(Arrays.copyOfRange(ROI, 1, 5);, ROI[0])
   }
   
   public float[] getObjectROI() {
       return this.objectROI;
   }
   
   private void setObjectROI(float[] ROI) {
       this.objectROI = ROI;
   }
   
   public float getInclusuionRadius() {
       return this.inclusionRadius;
   }
   
   public void setInclusuionRadius(float ir) {
       this.inclusionRadius = ir;
   }
   
   public float getFPS() {
       return this.fps;
   }
   
   public float[] getFrameRange() {
       return new float[]{this.startFrame, this.endFrame};
   }
   public void setStartFrame(int frame) {
       this.startFrame = frame;
   }
   public void setEndFrame(int frame) {
       this.endFrame = frame;
   }
   
   public interface TrackerEventHandler (
        boolean loopCall(Mat im, boolean playing);
   )
}