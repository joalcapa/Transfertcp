package transfertcp;

/**
 *
 * @author JoseCaceres
 */
public class HiloServerUpdate implements Runnable {
    private Thread hilo;
    private boolean running;
    private Server server;
    public HiloServerUpdate(Server server) {
        running = true;
        hilo = null;
        this.server = server;
    }
    
    @Override
    public void run() {
        while(running) {
            server.setAnalizandoDatos(true);
            server.analizarDatos();
            running = false;
            server.setAnalizandoDatos(false);
        }
    }
    
    public void ejecutarHilo() {
        if(hilo == null) {
            running = true;
            hilo = new Thread(this);
            hilo.start();
        }
    } 
}

