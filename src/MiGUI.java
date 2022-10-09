import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.swing.UIManager.LookAndFeelInfo;

public class MiGUI extends JFrame{
    private JPanel pnlFondo;
    private JTextArea taPathTutorial;
    private JButton btnCompresion;

    Path pathRaiz = null;

    public MiGUI(){
        setTitle("ApzTool");
        setSize(300, 100);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(pnlFondo);

        btnCompresion.addActionListener( e -> {
            pathRaiz = Paths.get(taPathTutorial.getText());
            System.out.println(pathRaiz.normalize().toString().replace("\\","/"));
        } );
    }

    public static void main(String[] args) throws IOException {
        //********** ver si file lista.txt existe
        File listaTXT = new File("D:/miFolder/lista.txt");

        //********** lista.txt ∄ ⇒ crearlo y llenar de mp4 paths
        if(!listaTXT.isFile()){
            System.out.println("esta cosa NO existe");
        }
        //********** lista.txt ∃ leerlo y efectuar operaciones
        else{
            System.out.println("esta cosa SI existe");
        }




        //Path esteDirectorio = Paths.get("D:\\miFolder");
        Path esteDirectorio = Paths.get("D:/miFolder");

        BiPredicate<Path, BasicFileAttributes> matcher = (elpath, atributo)-> String.valueOf(elpath).contains(".mp4");
        Stream<Path> lCosas = Files.find(esteDirectorio, 2, matcher);
        //lCosas.forEach(System.out::println);
        //lCosas.forEach(x -> System.out.println(x));

//        FileWriter escribidor = new FileWriter("D:/miFolder/lista.txt");
//        lCosas.forEach(x -> {
//            try {
//                escribidor.write(x + "\n");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        escribidor.close();

//        List<String> lNombres = new ArrayList<String>();
//        lNombres = Files.readAllLines(Paths.get("D:/miFolder/lista.txt"));
//
//        String[] lCadenas = lNombres.toArray(new String[0]);
//        for (String cadena : lCadenas){
//            System.out.println(cadena);
//        }


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
