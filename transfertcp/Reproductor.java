package transfertcp;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import static sun.audio.AudioPlayer.player;
import sun.audio.AudioPlayer;

/**
 *
 * @author JoseCaceres
 */
public class Reproductor {
    public BasicPlayer player;
    public JLayer play;
    public Reproductor(){
        player= new BasicPlayer();
    }

    public void abrir(){
        File nuevo = new File("escrito sada.docx"); 
        FileInputStream fileInputStream=null;
        byte[] bFile = new byte[(int) nuevo.length()];
        try {
          fileInputStream = new FileInputStream(nuevo);
        } catch (FileNotFoundException ex) {
          Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            fileInputStream.read(bFile);
        } catch (IOException ex) {
            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{ 
            File tempFile = File.createTempFile("documento","docx", null);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bFile);
            Runtime.getRuntime().exec("cmd /c start "+tempFile.getAbsolutePath());
        }catch(IOException e){
            e.printStackTrace();
        }catch(IllegalArgumentException e){
            JOptionPane.showMessageDialog(null, "No se pudo encontrar el archivo","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }  
}
    
 public void play() throws Exception {
  player.play();
}
 
public void open(InputStream data)  {
    try {
        player.open(data);
    } catch (BasicPlayerException ex) {
        System.out.println("Problema al aopen");
    }
}
 
public void pause() throws Exception {
    player.pause();
}
 
public void continuar() throws Exception {
    player.resume();
}
 
public void stop() throws Exception {
    player.stop();
}  
}


