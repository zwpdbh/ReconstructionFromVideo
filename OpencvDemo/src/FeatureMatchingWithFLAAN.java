import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.xfeatures2d.SURF;
import org.opencv.xfeatures2d.Xfeatures2d;

import java.beans.FeatureDescriptor;

/**
 * Created by zw on 28/04/2017.
 */
public class FeatureMatchingWithFLAAN {
    public static void main(String[] args) {
        String imageName1 = "/Users/zw/code/Java_Projects/ReconstructionFromVideo/data/box.png";
        String imageName2 = "/Users/zw/code/Java_Projects/ReconstructionFromVideo/data/box_in_scene.png";

        Mat img1 = Imgcodecs.imread(imageName1, Imgcodecs.IMREAD_GRAYSCALE);
        Mat img2 = Imgcodecs.imread(imageName2, Imgcodecs.IMREAD_GRAYSCALE);

        if (img1.empty() || img2.empty()) {
            System.out.println("No data is available.");
        } else {
            int minHessian = 400;

        }
    }
}
