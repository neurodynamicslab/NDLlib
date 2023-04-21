package ndl.ndllib;

import org.opencv.core.Mat;

public class TrackerImplement {
    public void sample() {
        Tracker tracker = new Tracker(new eventHandler());
        tracker.setDataFileName("path/to/video/file.mp4");
        tracker.setInclusionRadius(20);
        tracker.start();
    }

    public static class eventHandler implements Tracker.TrackerEventHandler {

        @Override
        public boolean loopCall(Mat im, boolean playing) {
            return true;
        }
    }
}
