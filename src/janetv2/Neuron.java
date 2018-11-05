package janetv2;

import java.util.Random;

class Neuron {
    double value;
    double bias;
    int id; //Die eigene Nummer im Layer
    private int layerId;
    double[] weights;
    private static final boolean DIFFBIAS = true;

    /**
     * Constructor für ein Neuron
     *
     * @param id
     * @param topLayer
     * @param layerId
     */
    Neuron(int id, int topLayer, int layerId) {
        Random rd = new Random();
        bias = 2 - rd.nextInt(4);
        this.id = id;
        this.layerId = layerId;
        weights = new double[topLayer];
        for (int i = 0; i < topLayer; i++) {
            weights[i] = ((double) rd.nextInt(100) / 100.0) - 0.5;
        }
    }

    /**
     * Propagate foreward
     *
     * @param top
     */
    void doit(Layer top) {
        int i;
        double sum = 0.0;
        for (i = 0; i < top.anzahl; i++) {
            sum += top.net.get(i).value * this.weights[i];
        }
        this.value = sig(sum + bias);
    }

    /**
     * Lenre Rekursiv alle Weights ein
     *
     * @param netz
     * @param exp
     */
    void learnRec(Network netz, double[] exp) {
        //System.out.println("Es lernt N:" + this.id + "aus L:" + this.layerId);
        //Für alle Weights, lerne...
        double delta;
        int layerAnz = netz.layers.size();
        //Sonderfall, falls dieses Neuron im Ausgabelayer liegt:
        if (this.layerId == layerAnz - 1) {
            for (int w = 0; w < this.weights.length; w++) {
                //Leite alle Elemte der Fehlerfunktion ab
                delta = netz.lr * (exp[id] - this.value) * sigdiff(this.value) * netz.layers.get(this.layerId - 1).net.get(w).value;
                this.weights[w] += delta;
            }
            //Bias anpassen:
            this.bias += netz.lr * (exp[id] - this.value) * sigdiff(this.value);
        } else {
            //Normalfall, dass dieses Neuron tiefer liegt
            for (int w = 0; w < this.weights.length; w++) {
                //Leite alle Elemte der Fehlerfunktion ab
                delta = 0;
                for (int f = 0; f < netz.layers.get(layerAnz - 1).net.size(); f++) {
                    delta += (exp[f] - netz.layers.get(layerAnz - 1).net.get(f).value) * netz.layers.get(layerAnz - 1).net.get(f).derive(netz, this.layerId, this.id, w);
                }
                this.weights[w] += netz.lr * delta;
                //System.out.println("Für " + this.id + " in " + this.layerId + " weight " + w + " mit Delta: " + delta);
            }
            //Bias Ableiten
            //Leite alle Elemte der Fehlerfunktion ab
            delta = 0;
            for (int f = 0; f < netz.layers.get(layerAnz - 1).net.size(); f++) {
                delta += (exp[f] - netz.layers.get(layerAnz - 1).net.get(f).value) * netz.layers.get(layerAnz - 1).net.get(f).derive(netz, this.layerId, this.id, 0, DIFFBIAS);
            }
            this.bias += netz.lr * delta;
        }
    }

    /**
     * Leite nach einem WEIGHT ab und setzte bias auf False
     *
     * @param netz
     * @param layer
     * @param id
     * @param weight
     * @return
     */
    double derive(Network netz, int layer, int id, int weight) {
        return derive(netz, layer, id, weight, false);
    }

    /**
     * Leitet nach einem Weight ab, ist dabei Rekursiv
     *
     * @param netz
     * @param layer
     * @param id
     * @param weight
     * @param bias
     * @return
     */
    double derive(Network netz, int layer, int id, int weight, boolean bias) {
        double delta = 0;
        //Wenn das gesuchte Gewicht NICHT in diesem oder dem Folgenden Layer liegt:
        if (layer != this.layerId && layer != this.layerId - 1) {
            //Für alle Weights leite ab danach
            delta = 0;
            for (int w = 0; w < this.weights.length; w++) {
                delta += this.weights[w] * netz.layers.get(this.layerId - 1).net.get(w).derive(netz, layer, id, weight, bias);
            }
            delta = delta * sigdiff(this.value);
        }
        //Wenn das gesuchte Gewicht IM FOLGENDEN Layer liegt
        if (layer == this.layerId - 1) {
            delta = sigdiff(this.value) * this.weights[id] * netz.layers.get(this.layerId - 1).net.get(id).derive(netz, layer, id, weight, bias);
        }
        //Wenn das gesuchte Gewicht in diesem Layer liegt
        if (layer == this.layerId) {
            delta = sigdiff(this.value);
            if (!bias) {
                delta = delta * netz.layers.get(this.layerId - 1).net.get(weight).value;
            }
        }
        return delta;
    }

    /**
     * Die Logistische Funktion
     *
     * @param eing
     * @return
     */
    double sig(double eing) {
        return (1.0 / (1.0 + Math.exp(0.0 - eing)));
    }

    /**
     * Die Ableitung der Logistischen Funktion
     *
     * @param eing
     * @return
     */
    double sigdiff(double eing) {
        return sig(eing) * (1.0 - sig(eing));
    }
}
