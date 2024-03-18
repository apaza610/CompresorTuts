import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hilo extends Thread {
    String pathRAIZ;
    String currentMP4 = "";
    JLabel lblDebug;
    Stream<Path> lRecursiva;
    List<Path> lPathsMP4s = new ArrayList<>();  // [D:\miFolder\deltaTime.mp4, D:\miFolder\interno\MiVideo.mp4]
    Path[] lPathsTmp;   //se ira achicando con cada conversion
    int operacion;
    float RPDZ;

    public Hilo(String cadena, JLabel lblDebug, int op, String rpdz){
        pathRAIZ = cadena;
        this.lblDebug = lblDebug;
        this.operacion = op;
        this.RPDZ = Float.parseFloat(rpdz);
    }
    @Override public void run(){
        //********** ver si file lista.txt existe
        File listaTXT = new File(pathRAIZ + "/lista.txt");

        //********** lista.txt ∄ ⇒ crearlo y llenar de mp4 paths
        if(!listaTXT.isFile()) {
            try {
                filtrarListaMP4s();
                if (arePathsClean){
                    escribirEnTXT();
                    lblDebug.setText("lista.txt ha sido creada");
                }
            } catch (IOException e) { System.out.println("ha ocurrido un error"); }
        }
        //********** lista.txt ∃ leerlo y efectuar operaciones
        else{
            System.out.println("trabajando en el folder: " + pathRAIZ);
            try {
                leerListaTXT();
            } catch (IOException e) { throw new RuntimeException(e); }
            operarMP4s();
            lblDebug.setText("fin: " + currentMP4);
            listaTXT.delete();          //borrar cuando quede vacio
            Toolkit.getDefaultToolkit().beep();
        }
    }

    boolean arePathsClean = true;
    private void filtrarListaMP4s() throws IOException {
        BiPredicate<Path, BasicFileAttributes> matcher = (elpath, atributo)-> String.valueOf(elpath).contains(".mp4");
        lRecursiva = Files.find(Path.of(pathRAIZ), 3, matcher);
        lPathsMP4s = lRecursiva.collect(Collectors.toList());
        for (Object n: lPathsMP4s){
            if (n.toString().contains(" ")){
                lblDebug.setText("Error: blank spaces in names!!");
                arePathsClean = false;
            }
        }
        lPathsTmp = lPathsMP4s.toArray(new Path[0]);
    }

    private void escribirEnTXT() throws IOException {
        FileWriter escribidor = new FileWriter(pathRAIZ + "/lista.txt");
        for (Path x: lPathsTmp) {
            try {
                escribidor.write(x + "\n");
            } catch (IOException e) { throw new RuntimeException(e); }
        }
        escribidor.close();
    }

    private void leerListaTXT() throws IOException {
        lPathsMP4s.clear();
        List<String> temp = Files.readAllLines(Paths.get("%s/lista.txt".formatted(pathRAIZ)));
        temp.forEach(x-> lPathsMP4s.add(Paths.get(x)) );
        lPathsTmp = lPathsMP4s.toArray(new Path[0]);
    }

    private void operarMP4s() {
        System.out.println("--------------------------***-------------------------");
        lPathsMP4s.forEach(x-> {
            File file = new File(x.toUri());
            currentMP4 = file.getName();
            lblDebug.setText(currentMP4.length() > 30 ? currentMP4.substring(0,30) : currentMP4 );  //cortar pa que no empuje al boton
            String xIN = file.getAbsolutePath();        //D:\miFolder\deltaTime.mp4
            String xOUT = xIN.replace(".mp4","OUT.mp4");

            Process proc = null;
            try {
                if(this.operacion == 1){
                    proc = Runtime.getRuntime().exec(String.format("ffmpeg -i %s -vf scale=-1:720 -codec:v libx264 -codec:a aac -crf 25 -preset slow %s",xIN,xOUT));
                } else if (this.operacion == 2) {
                    proc = Runtime.getRuntime().exec(String.format("ffmpeg -i %s -vf \"setpts=%s*PTS\" -af \"atempo=%s\" %s",xIN,1/this.RPDZ,this.RPDZ,xOUT));
                }
                InputStream stderr = proc.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                BufferedReader bfrdr = new BufferedReader(isr);
                String linea = null;
                while ((linea = bfrdr.readLine()) != null) {
                    System.out.println(linea);
                }
                int exitval = proc.waitFor();
                System.out.println("valor exit: " + exitval);
            } catch (Exception ex) { throw new RuntimeException(ex); }
            file.delete();
            renombrarFile(xOUT, xIN);
            actualizarTXT();
        });
    }

    private void actualizarTXT(){
        lPathsTmp = Arrays.copyOfRange(lPathsTmp,1,lPathsTmp.length);
        try {
            escribirEnTXT();
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    private void renombrarFile(String fileOUT, String fileIN){
        File f = new File(fileOUT);
        f.renameTo(new File(fileIN));
    }
}
