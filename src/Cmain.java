
import janet.*;
import java.io.File;
import java.io.IOException;

public class Cmain {
    public static void main(String[] args) throws IOException {
        Network netz = new Network(28 * 28, 32, 16, 10);
        ImgLoader daten = new ImgLoader("MNIST/train-images.idx3-ubyte", "MNIST/train-labels.idx1-ubyte");
        ImgLoader testDaten = new ImgLoader("MNIST/t10k-images.idx3-ubyte", "MNIST/t10k-labels.idx1-ubyte");
        System.out.println("---------START!---------");

        netz.restoreFile(new File("saves/dumb_784321610_1000.dmp"));
        netz.lr = 0.5;
        netz.homeTrainer(daten, 100);
        netz.test(testDaten, 1000);
        //netz.dumbFile("saves/dumb_784321610_1000");
        System.out.println("----------ENDE!----------");
    }
}
