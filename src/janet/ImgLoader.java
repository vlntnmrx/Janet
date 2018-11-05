package janet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImgLoader {
    private File dat;
    private FileInputStream datr;
    int dateiPosition = 0;

    File label;
    FileInputStream labelr;
    int labelPosition = 0;

    public ImgLoader(String datei, String labels) throws FileNotFoundException, IOException {
        dat = new File(datei);
        datr = new FileInputStream(dat);

        label = new File(labels);
        labelr = new FileInputStream(label);
        if (!dat.exists()) {
            System.err.println("Kann Datei nicht finden!");
        }
        if (!dat.canRead()) {
            System.err.println("Kann Datei nicht lesen!");
        }
        this.outImgHeader();
        this.outLabelHeader();
    }

    int readInt(FileInputStream datr) throws IOException {
        return (((datr.read() << 8) | datr.read()) << 16) | ((datr.read() << 8) | datr.read());
    }

    int readPixel() throws IOException {
        dateiPosition++;
        return datr.read();
    }

    byte[] readBlock() throws IOException {
        byte[] block = new byte[784];
        datr.read(block);
        return block;
    }

    int readLabel() throws IOException {
        labelPosition++;
        return labelr.read();
    }

    void outImgHeader() throws IOException {
        System.out.println("--------Images---------");
        System.out.println("Magic Number: ");
        System.out.println(readInt(datr));
        System.out.println("Anzahl Bilder: ");
        System.out.println(readInt(datr));
        System.out.println("Höhe (Pixel):");
        System.out.println(readInt(datr));
        System.out.println("Breite (Pixel):");
        System.out.println(readInt(datr));
        dateiPosition += 16;
    }

    void outLabelHeader() throws IOException {
        System.out.println("--------Labels---------");
        System.out.println("Magic Number: ");
        System.out.println(readInt(labelr));
        System.out.println("Anzahl Labels: ");
        System.out.println(readInt(labelr));
        labelPosition += 8;
    }

}
