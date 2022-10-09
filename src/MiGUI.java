import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import javax.swing.UIManager.LookAndFeelInfo;

public class MiGUI extends JFrame{
    private JPanel pnlFondo;
    private JTextArea taPathTutorial;
    private JButton btnCompresion;
    private JLabel lblDebug;

    private Path pathRAIZ;       // D:\miFolder
    private ArrayList<String> lDePathsMP4s = new ArrayList<>();

    public MiGUI(){
        setTitle("ApzTool Compresor Tuts");
        setSize(300, 100);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(pnlFondo);

        btnCompresion.addActionListener( e -> {
            pathRAIZ = Paths.get(taPathTutorial.getText());

            //********** ver si file lista.txt existe
            File listaTXT = new File(pathRAIZ + "/lista.txt");

            //********** lista.txt ∄ ⇒ crearlo y llenar de mp4 paths
            if(!listaTXT.isFile()){
                try {
                    filtrarListaMP4s();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //********** lista.txt ∃ leerlo y efectuar operaciones
            else{
                try {
                    leerListaTXT();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        } );
    }

    private void filtrarListaMP4s() throws IOException {
        //Path esteDirectorio = Paths.get("D:\\miFolder");

        BiPredicate<Path, BasicFileAttributes> matcher = (elpath, atributo)-> String.valueOf(elpath).contains(".mp4");
        Stream<Path> lCosas = Files.find(pathRAIZ, 2, matcher);
//        lCosas.forEach(x -> System.out.println(x));
        lCosas.forEach(x -> lDePathsMP4s.add(x.toString()));
        //System.out.println(lDePathsMP4s);             //[D:\miFolder\deltaTime.mp4, D:\miFolder\interno\MiVideo.mp4, D:\miFolder\laMacaaarena.mp4]
        guardarEnTXT();
        lblDebug.setText("lista.txt ha sido creado");
    }
    private void guardarEnTXT() throws IOException {
        FileWriter escribidor = new FileWriter(pathRAIZ + "/lista.txt");
        lDePathsMP4s.forEach(x -> {
            try {
                escribidor.write(x + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        escribidor.close();
    }
    private void leerListaTXT() throws IOException {
        lDePathsMP4s.clear();
        lDePathsMP4s.addAll(Files.readAllLines(Paths.get(pathRAIZ + "/lista.txt")));

        String[] lCadenas = lDePathsMP4s.toArray(new String[0]);
        for (String cadena : lCadenas){
            comprimirMP4(cadena);
            lDePathsMP4s.remove(0);
            guardarEnTXT();
        }
    }
    private void comprimirMP4(String elMP4) throws IOException {
        System.out.println("Operando sobre " + elMP4.substring(elMP4.lastIndexOf('\\')+1));         //Operando sobre D:\miFolder\laMacaaarena.mp4
        lblDebug.setText(elMP4.substring(elMP4.lastIndexOf('\\')+1));
        Runtime runtime = Runtime.getRuntime();

        String argOUT= elMP4.replace(".mp4", "NEW.mp4");
        runtime.exec(new String[]{"ffmpeg","-i",elMP4,"-vf","scale=-1:720","-codec:v","libx264","-codec:a","aac","-crf","25","-preset","slow",argOUT});

//        Scanner s = new Scanner(System.in);
//        s.next();             // pausa espera por keyboard
    }

    public static void main(String[] args) throws IOException {
//        Predicate<? super Path> predicado = path -> String.valueOf(path).contains(".mp4");
//        Stream<Path> lista = Files.walk(esteDirectorio, 1).filter(predicado);
//        lista.forEach(System.out::println);

//        Stream<Path> lista = Files.list(esteDirectorio);
//        lista.forEach(System.out::println);

        try {
            for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels()){
                if ("Nimbus".equals(info.getName())){  UIManager.setLookAndFeel(info.getClassName());  break; }
            }
        }catch (Exception e){
            System.out.println(e);
        }

        MiGUI migui = new MiGUI();
    }
}
