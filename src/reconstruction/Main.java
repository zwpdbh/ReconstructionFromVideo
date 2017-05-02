package reconstruction;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;


public class Main extends Application {



    @Override
    public void start(Stage primaryStage) {

        try {

            Parent root =FXMLLoader.load(getClass().getResource("main.fxml"));
            Scene scene = new Scene(root, 800, 600);

            primaryStage.setTitle("Java OpenCV");
            primaryStage.setScene(scene);
            primaryStage.show();

            Controller controller = new Controller();

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.setClosed();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
