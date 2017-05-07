package transfertcp;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JoseCaceres
 */
public class Comunicacion implements Runnable{
    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private Thread hilo;
    private Cliente cliente;
    private boolean running, conectado;
    
    public Comunicacion(Cliente cliente){
        running = true;
        conectado = false;
        this.cliente = cliente;
        hilo = new Thread(this);
        hilo.setPriority(Thread.MAX_PRIORITY);
        hilo.start();
    }

    public Socket getSocket(){
        return socket;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while(!conectado) {
        try {
            socket = new Socket("localhost", 5000); 
            salida=new  DataOutputStream(socket.getOutputStream());
            entrada=new  DataInputStream(socket.getInputStream());
            conectado = true;
            cliente.getConexion().setText("conectado al servidor");
            cliente.getConexion().setForeground(Color.GREEN);
        } catch (IOException ex) {}
        }
        write();
        while(running){
            try {
                read();
            } catch (IOException ex) {
                Logger.getLogger(Comunicacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Comunicacion.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }

    public DataOutputStream getSalida() {
        return salida;
    }

    public DataInputStream getEntrada() {
        return entrada;
    }

    public boolean isRunning() {
        return running;
    }
    
    public void read() throws IOException {
        String cadena = entrada.readUTF();
        if(cadena != null) {  
           StringTokenizer token = new StringTokenizer(cadena, "****");
           String tipo = token.nextToken();
           if(tipo.compareTo("AA") == 0)
               this.update(token);
        }
    }
    
    public void update(StringTokenizer token) {
        int pos = Integer.parseInt(token.nextToken());
        int tipo = Integer.parseInt(token.nextToken());
        String archivo = token.nextToken();
        int length = Integer.parseInt(token.nextToken());
        int agregar = Integer.parseInt(token.nextToken());
        cliente.actualizarNodoRecurso(archivo, length, tipo, agregar);
    }
    
    public void write() {
        try {
            salida.writeUTF("AA");
        } catch (IOException ex) {
            Logger.getLogger(Comunicacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}


