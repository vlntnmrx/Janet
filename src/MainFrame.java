import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import janet.Network;

public class MainFrame {

    private JTextArea infoArea;
    private JPanel panel;
    private JButton newButton;
    private JButton stopButton;
    Network netz;

    public MainFrame() {
        Thread runThread;
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] l = {28*28,16,16,10};
                netz = new Network(l);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainFrame");
        frame.setContentPane(new MainFrame().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
