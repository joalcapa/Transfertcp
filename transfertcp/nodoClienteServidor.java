package transfertcp;

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
public class nodoClienteServidor implements Runnable {
    private Socket socket;
    private Thread hilo;
    private boolean running, listen, update;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private nodoClienteServidor suc;
    private ListaUpdate listaUpdate;
    private boolean actualizacion, interrumpir, descargado, interrumpir2, perdidaSignal;
    private String tipo;
    private ListaRecurso listaRecurso;
    private int inicio,tambytes,posicion, cantidad;
    private String nombre;
    private Server server;
    
    public nodoClienteServidor(Socket socket, ListaRecurso listaRecurso, Server server){
        this.socket = socket;
        this.server = server;
        suc = null;
        tipo = null;
        interrumpir = descargado = interrumpir2 = perdidaSignal = false;
        running = listen = true;
        this.listaRecurso = listaRecurso;
        this.posicion=0;
        nodoUpdate nodo = new nodoUpdate(1, 0, listaRecurso);
        listaUpdate = new ListaUpdate();
        listaUpdate.add(nodo);
        try {
             entrada = new DataInputStream(socket.getInputStream());
             salida = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(nodoClienteServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        hilo = new Thread(this);
        hilo.setPriority(4);
        hilo.start();
    }

    public boolean isInterrumpir2() {
        return interrumpir2;
    }

    public void setPerdidaSignal(boolean perdidaSignal) {
        this.perdidaSignal = perdidaSignal;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public nodoClienteServidor getSuc(){
        return suc;
    }
    
    public boolean getUpdate(){
        return update;
    }
    
    public void setSuc(nodoClienteServidor suc){
        this.suc = suc;
    }

    @Override
    public void run() {
         while(running){  
             if(listen)
                read();
             else
                write();
         }
         
        if(interrumpir && !descargado)
            acordarInterrupcion();
         
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(nodoClienteServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(this.perdidaSignal) {
            System.out.println("Aqui nodoClienteServidor perdida de señal");
        }  
    }
    
    public void write() {
        if(tipo != null){
            if(tipo.compareTo("AA") == 0)
                actualizar();
            else
                descargar();
        }
    }
    
    public void acordarInterrupcion(){
        try {
            salida.writeUTF("out");
        } catch (IOException ex) {
            Logger.getLogger(nodoClienteServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void actualizar(){
        if(listaUpdate.getCab() != null)
            listaUpdate.ejecutar(salida);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
           this.interrumpir2 = true;
        }
    }
    
    public void update(int pos) {
        nodoUpdate dato = new nodoUpdate(2, pos, listaRecurso);
        listaUpdate.add(dato);
    }
    
    public String getTipo(){
        return tipo;
    }
    
    public void setInterrumpir(boolean interrumpir){
        this.interrumpir = interrumpir;
        running = false;
    }
    
    public void read(){
        try {
            String cadena = entrada.readUTF();
            if(cadena != null) {
                StringTokenizer token = new StringTokenizer(cadena, "****");
                tipo = token.nextToken();
  
                if(tipo.compareTo("DD") == 0) {
                    procesoDescarga(token);
                    hilo.setPriority(8);
                    listen = false;
                } else {
                    hilo.setPriority(Thread.MAX_PRIORITY);
                    listen = false;   
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(nodoClienteServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cerrarConexion(){
        running = false;
    }
    
    public void procesoDescarga(StringTokenizer token){
        nombre= token.nextToken();
        posicion= Integer.parseInt(token.nextToken());
        cantidad = Integer.parseInt(token.nextToken());
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void descargar(){
        nodoRecurso recor = listaRecurso.getCab();
        while(recor != null) {
           if(recor.getNombre().compareTo(nombre)==0)
               break;
           else
               recor=recor.getSuc();    
        }
        recor.descargarByte(posicion, cantidad,salida, this);
        listen = true;
    }

    public boolean isDescargado() {
        return descargado;
    }
}



