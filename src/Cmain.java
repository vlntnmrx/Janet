import janet.*;

import java.io.File;
import java.io.IOException;

public class Cmain {
    public static void main(String[] args) throws IOException {
        Network netz = new Network(28 * 28, 16, 16, 10);
        ImgLoader daten = new ImgLoader("MNIST/train-images.idx3-ubyte", "MNIST/train-labels.idx1-ubyte");
        ImgLoader testDaten = new ImgLoader("MNIST/t10k-images.idx3-ubyte", "MNIST/t10k-labels.idx1-ubyte");
        System.out.println("---------START!---------");
        //netz.restoreFile(new File("saves/dumb_784161610_5000.dmp"));
        netz.lr = 0.001;
        netz.batchTrainer(daten,200,32);
        //netz.singleTrainer(daten, 6000);
        netz.test(testDaten, 1000, false);
        //netz.dumbFile("saves/dumb_784161610_150-10");
        System.out.println("----------ENDE!----------");
    }
}
