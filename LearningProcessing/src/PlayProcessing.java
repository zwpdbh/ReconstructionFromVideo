import processing.core.PApplet;

/**
 * Created by zw on 22/05/2017.
 */
public class PlayProcessing extends PApplet {
    public void settings() {
        size(300, 300);
    }

    public void draw() {
        background(0);
        ellipse(mouseX, mouseY, 20, 20);
    }

    public static void main(String[] args) {

//        PApplet.main(new String[] {"MyProcessingSketch"});
        PApplet.main("PlayProcessing");
    }

//    //	An array of stripes
//    Stripe[] stripes = new Stripe[50];
//
//    public void settings() {
//        size(200,200);
//
//    }
//
//    public void setup() {
//        // Initialize all "stripes"
//        for (int i = 0; i < stripes.length; i++) {
//            stripes[i] = new Stripe(this);
//        }
//    }
//
//    public void draw() {
//        background(100);
//        // Move and display all "stripes"
//        for (int i = 0; i < stripes.length; i++) {
//            stripes[i].move();
//            stripes[i].display();
//        }
//    }
}
