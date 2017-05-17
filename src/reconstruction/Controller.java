package reconstruction;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import utils.Utils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.Button;

public class Controller {


    @FXML
    private Button button;
    @FXML
    private ImageView currentFrame;

    private VideoCapture capture = new VideoCapture();

    private ScheduledExecutorService timer;

    private boolean cameraActive = false;

    private static int cameraID = 0;

    private Mat previousFrame = new Mat();
    private Mat opticalFlowMat;
    private Mat frame;
    private Mat originalFrame;
    private Mat mask;
    private Mat bg = new Mat(1, 65, CvType.CV_64FC1);
    private Mat fg = new Mat(1, 65, CvType.CV_64FC1);

    @FXML
    protected void startCamera(ActionEvent event) {
        if (!this.cameraActive) {
//            this.capture.open(cameraID);
            this.capture.open("/Users/zw/code/Java_Projects/ReconstructionFromVideo/data/vtest.mp4");

            if (this.capture.isOpened()) {
                this.cameraActive = true;


                Runnable frameGrabber = new Runnable() {
                    @Override
                    public void run() {
                        Mat frame = grabFrame();
                        Mat grayedImage = new Mat(frame.size(), CvType.CV_8UC1);
                        // if the frame is not empty, process it
                        if (!frame.empty()) {
                            Imgproc.cvtColor(frame, grayedImage, Imgproc.COLOR_BGR2GRAY);
                        }

                        if (!previousFrame.empty()) {
                            Size frameSize = previousFrame.size();
                            opticalFlowMat = new Mat(frameSize, CvType.CV_32FC2);

                            // calculate optical flow
                            Video.calcOpticalFlowFarneback(previousFrame, grayedImage, opticalFlowMat, 0.5,
                                    3, 12, 2, 8, 1.2, 0);

                            if (!opticalFlowMat.empty()) {
                                // use grab cut
                                mask = initializeMaskWithOpticalFlow(opticalFlowMat);

                                Rect rect = new Rect(0, 0, frame.width(), frame.height());
                                Imgproc.grabCut(frame, mask, rect, bg, fg, 1, Imgproc.GC_INIT_WITH_MASK);
//                                frame = updateMatWithMask(mask, frame);
                            }
                        }

                        grayedImage.copyTo(previousFrame);

                        Image imageToShow = Utils.mat2Image(frame);
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                this.button.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open the camera connection ... ");
            }
        } else {
            this.cameraActive = false;
            this.button.setText("Start Camera");
            this.stopAcquisition();
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

            } catch (Exception e) {
                // log the error
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;

    }

    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    protected void setClosed() {
        this.stopAcquisition();
    }


    private Mat initializeMaskWithOpticalFlow(Mat opticalFlowMat) {
        Mat mask = new Mat(opticalFlowMat.size(), CvType.CV_8U);
        for (int i = 0; i < opticalFlowMat.rows(); i++) {
            for (int j = 0; j < opticalFlowMat.cols(); j++) {
                double u = opticalFlowMat.get(i, j)[0];
                double v = opticalFlowMat.get(i, j)[1];
                double dis = Math.sqrt(Math.pow(u, 2) + Math.pow(v, 2));
                if (dis >= 3) {
                    mask.put(i, j, 1);
                } else if (dis > 0 && dis < 2) {
                    mask.put(i, j, 3);
                } else if (dis == 0) {
                    mask.put(i, j, 0);
                } else {
                    mask.put(i, j, 2);
                }

            }
        }
        return mask;
    }

    private Mat updateMatWithMask(Mat mask, Mat image) {
        for (int i = 0; i < mask.cols(); i++) {
            for (int j = 0; j < mask.rows(); j++) {
                if (mask.get(i, j)[0] == 0 || mask.get(i, j)[0] == 2) {
                    double[] color = {0, 0, 0};
                    image.put(i, j, color);
                }
            }
        }

        return image;
    }
}

