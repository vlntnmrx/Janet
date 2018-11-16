package janet;

import java.util.ArrayList;
import java.util.List;

//TODO: Dokumentation einfügen

public class Layer {
    List<Neuron> net;
    private int id; //Layer Nummer, angefangen mit 0
    public int anzahl;

    Layer(int anz, Layer topLayer, int id) {
        this.id = id;
        net = new ArrayList<>();
        this.anzahl = anz;
        if (topLayer == null) {
            for (int i = 0; i < anz; i++) {
                net.add(new Neuron(i, 0, this.id));
            }
        } else {
            for (int i = 0; i < anz; i++) {
                net.add(new Neuron(i, topLayer.anzahl, this.id));
            }
        }
        if (net.size() != anz) {
            System.err.println("Fehler: Array unvollständig!");
        }
    }


    void process(Layer top) {
        int i;
        for (i = 0; i < this.net.size(); i++) {
            net.get(i).doit(top);
        }
    }

    Neuron getBiggest() {
        int i;
        Neuron ret = net.get(0); //Mit Neuron 0 Initialisieren
        double big = 0;
        for (i = 0; i < anzahl; i++) {
            if (net.get(i).value > big) {
                ret = net.get(i);
                big = net.get(i).value;
            }
        }
        return ret;
    }

    void learnSingle(Network netz, double[] exp) {
        //Lerne alle Neuronen des eigenen Layers ein
        for (int i = 0; i < this.net.size(); i++) {
            this.net.get(i).learnRec(netz, exp);
            this.net.get(i).addDeltas();
        }
    }

    void learnBatch(Network netz, double[] exp) {
        for (int i = 0; i < this.net.size(); i++) {
            this.net.get(i).learnRec(netz, exp);
        }
    }

    void applyLearning() {
        for (int i = 0; i < this.net.size(); i++) {
            this.net.get(i).addDeltas();
        }
    }

    void outValues() {
        int i;
        for (i = 0; i < anzahl; i++) {
            System.out.println(net.get(i).value);
        }
    }

}
