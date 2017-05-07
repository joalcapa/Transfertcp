package transfertcp;

import java.applet.Applet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author JoseCaceres
 */
public class MasterGraphics extends Applet implements Runnable {
    private View view;
    private Thread hilo;
    private boolean running, activo;
    private ListaDescarga listaDescarga;
    
    public MasterGraphics(ListaDescarga listaDescarga, int y){ 
        listaDescarga = listaDescarga;
        view = new View(listaDescarga);
        running = activo = true;
        view.setBounds(0, 40, 620, 150);
        listaDescarga.getPanel().add(view);
        listaDescarga.getPanel().setBounds(0,y,620,500);
        hilo = new Thread(this);
        hilo.setPriority(Thread.MAX_PRIORITY);
        hilo.start();
    }
    
    @Override
    public void run() {
        while(running){
            if(activo)
            view.repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(MasterGraphics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public boolean getActivo() {
        return activo;
    }
    
    public void setRunning(boolean running){
        this.running = running;
    }
    
    public JPanel getView(){
        return view;
    }
}


