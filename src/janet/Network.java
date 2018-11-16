package janet;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Network {
    List<Layer> layers;
    private int anzLays;
    public double lr = 2.0;//Learning Rate, mutliplikator für die Ableitung (2.0 ist gut)

    public Network(int... lays) {
        this.layers = new ArrayList<>();
        this.layers.add(new Layer(lays[0], null, 0));
        for (int i = 1; i < lays.length; i++) {
            this.layers.add(new Layer(lays[i], layers.get(i - 1), i));
        }
        this.anzLays = layers.size();
    }

    private void passTh() {
        int i;
        for (i = 1; i < this.anzLays; i++) {
            layers.get(i).process(this.layers.get(i - 1));
        }
    }

    void outLayers() {
        int i;
        for (i = 1; i < this.anzLays; i++) {
            System.out.println("Layer " + i + ":");
            layers.get(i).outValues();
        }
    }

    private void learnSingle(double[] exp) {
        this.passTh();
        int i;
        for (i = 1; i < this.layers.size(); i++) {
            this.layers.get(i).learnSingle(this, exp);
        }
    }

    private void learnBatch(double[] exp) {
        this.passTh();
        int i;
        for (i = 1; i < this.layers.size(); i++) {
            this.layers.get(i).learnBatch(this, exp);
        }
    }

    private void applyLearning() {
        for (int i = 1; i < this.layers.size(); i++) {
            this.layers.get(i).applyLearning();
        }
    }

    public void singleTrainer(ImgLoader imgs, int cycles) throws IOException {
        double[] exp = new double[10];
        int lab;
        int right = 0, wrong = 0, ist;
        double fehler;
        for (int i = 0; i < cycles; i++) {
            lab = imgs.readLabel();
            Arrays.fill(exp, 0);
            exp[lab] = 1.0;
            this.prep(imgs);
            this.learnSingle(exp);
            fehler = 0;
            for (int j = 0; j < this.layers.get(this.layers.size() - 1).net.size(); j++) {
                fehler += Math.pow(exp[j] - this.layers.get(this.layers.size() - 1).net.get(j).value, 2);
            }
            ist = (this.layers.get(this.layers.size() - 1).getBiggest()).id;
            if (lab == ist) {
                System.out.println(i + " - -----CORRECT-----\t\t F:" + fehler + " - S:" + lab + "/I:" + ist);
                right++;
            } else {
                wrong++;
                System.out.println(i + " - " + (double) right / (double) wrong + " \t F:" + fehler + " - S:" + lab + "/I:" + ist);
            }
        }
        System.out.println("Verhältnis:" + (double) right / (double) wrong);

    }

    public void batchTrainer(ImgLoader imgs, int cycles, int size) throws IOException {
        double[] exp = new double[10];
        int lab;
        int right = 0, wrong = 0, ist;
        double fehler;
        for (int i = 0; i < cycles; i++) {
            for (int j = 0; j < size; j++) {
                lab = imgs.readLabel();
                Arrays.fill(exp, 0);
                exp[lab] = 1.0;
                this.prep(imgs);
                this.learnBatch(exp);
                fehler = 0;
                for (int k = 0; k < this.layers.get(this.layers.size() - 1).net.size(); k++) {
                    fehler += Math.pow(exp[k] - this.layers.get(this.layers.size() - 1).net.get(k).value, 2);
                }
                ist = (this.layers.get(this.layers.size() - 1).getBiggest()).id;
                if (lab == ist) {
                    System.out.println(i + "." + j + " - ------CORRECT-----\t\t F:" + fehler + " - S:" + lab + "/I:" + ist);
                    right++;
                } else {
                    wrong++;
                    System.out.println(i + "." + j + " - " + (double) right / (double) wrong + " \t F:" + fehler + " - S:" + lab + "/I:" + ist);
                }
            }
            System.out.println("+++ Applying Batch +++");
            this.applyLearning();
        }
    }

    void prep(ImgLoader img) throws IOException {
        int i;
        for (i = 0; i < 784; i++) {
            layers.get(0).net.get(i).value = ((double) img.readPixel() / 255);
        }
    }

    public void restoreFile(File dat) throws FileNotFoundException, IOException {
        DataInputStream inp = new DataInputStream(new FileInputStream(dat));
        System.out.println("Read Config from File...");
        System.out.println("Magic Number: " + inp.readInt());
        int i = 0;
        boolean fail = false;
        int inpi = inp.readInt();
        while (inpi != 0) {
            System.out.print("File Layer " + i + ": " + inpi);
            if (inpi == this.layers.get(i).net.size()) {
                System.out.println(" - OK");
            } else {
                System.err.println(" - FAIL");
                fail = true;
            }
            inpi = inp.readInt();
            i++;
        }
        if (!fail) {
            //Es werde, für alle...
            //...für alle Layer...
            for (i = 0; i < this.layers.size(); i++) {
                //...für alle Neuronen...
                for (int j = 0; j < this.layers.get(i).net.size(); j++) {
                    //...für alle Weights...
                    for (int k = 0; k < this.layers.get(i).net.get(j).weights.length; k++) {
                        //...lese sie aus der Datei!
                        this.layers.get(i).net.get(j).weights[k] = inp.readDouble();
                    }
                    //Und häng noch den Bias hinten dran
                    this.layers.get(i).net.get(j).bias = inp.readDouble();
                }
            }
        } else {
            System.err.println("Layer aus Datei nicht gleich Netzwerk");
        }

        //Stream wieder schliessen
        inp.close();
    }

    public void test(ImgLoader testDaten, int cycles, boolean print) throws IOException {
        System.out.println("*****TESTING*****");
        double[] exp = new double[10];
        int lab;
        int right = 0, wrong = 0;
        double fehler;
        for (int i = 0; i < cycles; i++) {
            lab = testDaten.readLabel();
            Arrays.fill(exp, 0);
            exp[lab] = 1.0;
            this.prep(testDaten);
            this.passTh();
            fehler = 0;
            for (int j = 0; j < this.layers.get(this.layers.size() - 1).net.size(); j++) {
                fehler += Math.pow(exp[j] - this.layers.get(this.layers.size() - 1).net.get(j).value, 2);
            }
            if (lab == (this.layers.get(this.layers.size() - 1).getBiggest()).id) {
                if (print)
                    System.out.println(i + " - -----CORRECT-----\t\t F:" + fehler);
                right++;
            } else {
                wrong++;
                if (print)
                    System.out.println(i + " - " + (double) right / (double) wrong + " \t F:" + fehler);
            }
        }
        System.out.println("Treffsicherheit: " + ((double) right / (double) (right + wrong)) * 100.0 + "%");
    }

    public void dumbFile(String name) throws IOException {
        File dumb = new File(name + ".dmp");
        dumb.createNewFile();
        FileOutputStream dumbstr = new FileOutputStream(dumb);
        DataOutputStream dumbdat = new DataOutputStream(dumbstr);
        dumbdat.writeInt(172); //Magic Number

        //Speichere alle Layergrößen als erstes
        for (int i = 0; i < this.layers.size(); i++) {
            dumbdat.writeInt(this.layers.get(i).net.size());
        }
        dumbdat.writeInt(0);

        //Es werde, für alle...
        //...für alle Layer...
        for (int i = 0; i < this.layers.size(); i++) {
            //...für alle Neuronen...
            for (int j = 0; j < this.layers.get(i).net.size(); j++) {
                //...für alle Weights...
                for (int k = 0; k < this.layers.get(i).net.get(j).weights.length; k++) {
                    //...speichere sie in der Datei!
                    dumbdat.writeDouble(this.layers.get(i).net.get(j).weights[k]);
                    //Ende des Gedichts :)
                }
                //Und häng noch den Bias hinten dran
                dumbdat.writeDouble(this.layers.get(i).net.get(j).bias);
            }
        }

        dumbdat.close();
        dumbstr.close();

    }
}
