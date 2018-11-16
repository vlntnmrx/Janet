package janet;

import java.util.Random;

//TODO: Dokumentation einfügen

class Neuron {
    double value;
    double bias;
    private double bdelta;
    int id; //Die eigene Nummer im Layer
    private int layerId;
    double[] weights;
    private double[] deltas;
    private static final boolean DIFFBIAS = true;


    Neuron(int id, int topLayer, int layerId) {
        Random rd = new Random();
        bias = 0;//2 - rd.nextInt(4);
        this.id = id;
        this.layerId = layerId;
        weights = new double[topLayer];
        deltas = new double[topLayer];
        for (int i = 0; i < topLayer; i++) {
            weights[i] = ((double) rd.nextInt(100) / 100.0);// - 0.5;
        }
        resetDeltas();
    }

    private void resetDeltas() {
        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = 0.0;
        }
        bdelta = 0.0;
    }

    void addDeltas() {
        for (int i = 0; i < deltas.length; i++) {
            weights[i] += deltas[i];
        }
        bias += bdelta;
        resetDeltas();
    }

    void doit(Layer top) {
        int i;
        double sum = 0.0;
        for (i = 0; i < top.anzahl; i++) {
            sum += top.net.get(i).value * this.weights[i];
        }
        this.value = act(sum + bias);
    }

    void learnRec(Network netz, double[] exp) {
        //System.out.println("Es lernt N:" + this.id + "aus L:" + this.layerId);
        //Für alle Weights, lerne...
        double delta;
        int layerAnz = netz.layers.size();
        //Sonderfall, falls dieses Neuron im Ausgabelayer liegt:
        if (this.layerId == layerAnz - 1) {
            for (int w = 0; w < this.weights.length; w++) {
                //Leite alle Elemte der Fehlerfunktion ab
                delta = (exp[id] - this.value) * actdiff(this.value) * netz.layers.get(this.layerId - 1).net.get(w).value;
                this.deltas[w] += netz.lr * delta;
            }
            //Bias anpassen:
            this.bdelta += netz.lr * (exp[id] - this.value) * actdiff(this.value);
        } else {
            //Normalfall, dass dieses Neuron tiefer liegt
            for (int w = 0; w < this.weights.length; w++) {
                //Leite alle Elemte der Fehlerfunktion ab
                delta = 0;
                for (int f = 0; f < netz.layers.get(layerAnz - 1).net.size(); f++) {
                    delta += (exp[f] - netz.layers.get(layerAnz - 1).net.get(f).value) * netz.layers.get(layerAnz - 1).net.get(f).derive(netz, this.layerId, this.id, w);
                }
                this.deltas[w] += netz.lr * delta;
                //System.out.println("Für " + this.id + " in " + this.layerId + " weight " + w + " mit Delta: " + delta);
            }
            //Bias Ableiten
            //Leite alle Elemte der Fehlerfunktion ab
            delta = 0;
            for (int f = 0; f < netz.layers.get(layerAnz - 1).net.size(); f++) {
                delta += (exp[f] - netz.layers.get(layerAnz - 1).net.get(f).value) * netz.layers.get(layerAnz - 1).net.get(f).derive(netz, this.layerId, this.id, 0, DIFFBIAS);
            }
            this.bdelta += netz.lr * delta;
        }
    }


    double derive(Network netz, int layer, int id, int weight) {
        return derive(netz, layer, id, weight, false);
    }

    double derive(Network netz, int layer, int id, int weight, boolean bias) {
        double delta = 0;
        //Wenn das gesuchte Gewicht NICHT in diesem oder dem Folgenden Layer liegt:
        if (layer != this.layerId && layer != this.layerId - 1) {
            //Für alle Weights leite ab danach
            delta = 0;
            for (int w = 0; w < this.weights.length; w++) {
                delta += this.weights[w] * netz.layers.get(this.layerId - 1).net.get(w).derive(netz, layer, id, weight, bias);
            }
            delta = delta * actdiff(this.value);
        }
        //Wenn das gesuchte Gewicht IM FOLGENDEN Layer liegt
        if (layer == this.layerId - 1) {
            delta = actdiff(this.value) * this.weights[id] * netz.layers.get(this.layerId - 1).net.get(id).derive(netz, layer, id, weight, bias);
        }
        //Wenn das gesuchte Gewicht in diesem Layer liegt
        if (layer == this.layerId) {
            delta = actdiff(this.value);
            if (!bias) {
                delta = delta * netz.layers.get(this.layerId - 1).net.get(weight).value;
            }
        }
        return delta;
    }

    double act(double eing) {
        switch (1) {
            case 1:
                return relu(eing);
            default:
                return sigm(eing);
        }
    }

    double actdiff(double eing) {
        switch (1) {
            case 1:
                return reludiff(eing);
            default:
                return sigmdiff(eing);
        }
    }


    double sigm(double eing) {
        return (1.0 / (1.0 + Math.exp(0.0 - eing)));
    }

    double sigmdiff(double eing) {
        return act(eing) * (1.0 - act(eing));
    }

    double relu(double eing) {
        return eing <= 0 ? 0.0 : eing;
    }

    double reludiff(double eing) {
        return eing <= 0 ? 0.0 : 1;
    }


}
