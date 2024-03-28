import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MiGUI extends JFrame{
    private JPanel pnlFondo;
    private JTextField tfPathTutorial;
    private JButton btnCompresion;
    private JLabel lblDebug;
    private JTextField tfHayasa;
    private JCheckBox checkBox;
    private JLabel lblHayasa;
    private Path pathRAIZ;       // D:\miFolder

    public MiGUI(){
        setTitle("CompresorTutsMP4");
        setSize(360, 130);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(pnlFondo);

        checkBox.addActionListener( e->{
            if ( checkBox.isSelected() ){
                lblHayasa.setEnabled(true);
                tfHayasa.setEnabled(true);
            }else{
                lblHayasa.setEnabled(false);
                tfHayasa.setEnabled(false);
            }
        });

        btnCompresion.addActionListener( e -> {
            pathRAIZ = Paths.get(tfPathTutorial.getText());
            if (checkBox.isSelected()){
                Hilo hilo1 = new Hilo(pathRAIZ.toString(), lblDebug, 2, tfHayasa.getText());
                hilo1.start();
            }
            else{
                Hilo hilo1 = new Hilo(pathRAIZ.toString(), lblDebug, 1, tfHayasa.getText());
                hilo1.start();
            }
        } );
    }

    public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        MiGUI migui = new MiGUI();
    }
}
