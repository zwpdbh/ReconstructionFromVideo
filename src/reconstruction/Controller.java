package reconstruction;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.opencv.core.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
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
    private Mat opticalFlowFrame;

    @FXML
    protected void startCamera(ActionEvent event) {
        if (!this.cameraActive) {
            this.capture.open(cameraID);

            if (this.capture.isOpened()) {
                this.cameraActive = true;



                Runnable frameGrabber = new Runnable() {
                    @Override
                    public void run() {
                        Mat frame = grabFrame();

                        if (!previousFrame.empty()) {
                            Size frameSize = previousFrame.size();
                            opticalFlowFrame = new Mat(frameSize, CvType.CV_32FC2);

                            // calculate optical flow
                            Video.calcOpticalFlowFarneback(previousFrame, frame, opticalFlowFrame, 0.5,
                                    3, 12, 2, 8, 1.2, 0);

                            if (!opticalFlowFrame.empty()) {
                                // process optical flow to visualize it

                                Mat mag = new Mat();
                                Mat angel = new Mat();

//                                Core.cartToPolar(opticalFlowFrame.);
                            }
                        }

                        frame.copyTo(previousFrame);

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

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                }

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

}

