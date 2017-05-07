package transfertcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JoseCaceres
 */
public class nodoDescarga implements Runnable {
    private boolean running, listen, interrumpido, operacionBoton, descargado, isSleep, isMuerto, onBoton;
    private Socket socket;
    private Thread hilo;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private byte [] data;
    private boolean [] data3;
    private int inicio, tamBytes, contador, contador2, cantidadT, velocidad, conAn;
    private String nombre;
    private nodoDescarga suc,pre;
    private ListaDescarga lista;

    public nodoDescarga(int inicio, int tamBytes, String nombre,ListaDescarga lista, boolean levantar){
        running = true;
        this.nombre = nombre;
        interrumpido = operacionBoton = isSleep = isMuerto = onBoton =  false;
        listen = descargado = false;
        this.inicio = inicio;
        this.tamBytes = tamBytes;
        data = new byte[tamBytes];
        contador2=0;
        conAn = 0;
        velocidad = 0;
        data3 = new boolean [tamBytes];
        
        for(int i=0; i< tamBytes; i++)
            data3[i] = false;
            
        this.lista=lista;
        contador = inicio;
       
        suc = pre = null;
        try {
            socket = new Socket("localhost", 5000);
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(nodoDescarga.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(levantar)
            levantar();
    }

    public String getNombre() {
        return nombre;
    }

    public synchronized  boolean isSleep() {
        return isSleep;
    }
    
    public void setIsMuerto(boolean isMuerto) {
        this.isMuerto = isMuerto;
    }

    public synchronized void setIsSleep(boolean isSleep) {
        this.isSleep = isSleep;
    }
    
    public void setContadorAnterior(int conAn) {
        this.conAn = conAn;
    }
    
    public int cantidadNoDescargados() {
        int datosND = 0;
        for(int i = 0; i< data3.length; i++)
            if(!data3[i])
                datosND++;
        return datosND;
    }

    public int getConAn() {
        return conAn;
    }
    
    @Override
    public void run() {
        while(this.isRunning()) {
                try {
                    if(!listen) 
                       write();
                    else
                       read();   
            } catch (IOException ex) {
                Logger.getLogger(nodoDescarga.class.getName()).log(Level.SEVERE, null, ex);
            }
         
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(nodoDescarga.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }
        
        if(!isMuerto)  
            if(!interrumpido) {
                if(descargado)
                    lista.evaluar();
            } else {
                if(!onBoton)
                    acordarInterrupcion();
            }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(nodoDescarga.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setHilo(null);
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public void setOnBoton(boolean onBoton) {
        this.onBoton = onBoton;
    }

    public synchronized void setOperacionBoton(boolean operacionBoton) {
        this.operacionBoton = operacionBoton;
    }

    public synchronized boolean isOperacionBoton() {
        return operacionBoton;
    }
    
    public void dividirDescarga() {
        int mitad = (int) (data3.length / 2);
        byte [] dataT = new byte[mitad];
        boolean [] dataT3 = new boolean[mitad];
      
        for(int i=0; i< mitad; i++) {
            if(data3[i])
                dataT[i] = data[i];
            dataT3[i] = data3[i];
        }
      
        byte [] AUXdataT = new byte[data3.length - mitad];
        boolean [] AUXdataT3 = new boolean[data3.length - mitad];
      
        for(int i=mitad; i< data3.length; i++) {
            if(data3[i])
                AUXdataT[i - mitad] = data[i]; 
            AUXdataT3[i - mitad] = data3[i];
        }
       
        data = null;
        data3 = null;
        data = new byte[dataT3.length];
        data3 = new boolean[dataT3.length];
      
        for(int i=0; i< dataT3.length; i++) {
            if(dataT3[i])
                data[i] = dataT[i];
            data3[i] = dataT3[i];
        }
      
        nodoDescarga nuevo = new nodoDescarga(inicio + mitad,AUXdataT.length,this.nombre,lista, false);
        nuevo.acomodarData(AUXdataT, AUXdataT3);
      
        if(suc != null) {
            nuevo.setSuc(suc);
            suc.setPre(nuevo);
        }
      
        suc = nuevo;
        nuevo.setPre(this);
        AUXdataT = null;
        AUXdataT3 = null;
        dataT = null;
        dataT3 = null;
        tamBytes = data.length;
        nuevo.levantar();
    }
    
    public void levantar() {
        hilo = new Thread(this);
        hilo.start();
        hilo.setPriority(4);
    }
    
    public void acomodarData(byte [] dataA, boolean [] dataB) {
        for(int i = 0; i<dataB.length; i++) {
            if(dataB[i])
                data[i] = dataA[i]; 
            data3[i] = dataB[i];
        }
    }
    
    public void acordarInterrupcion(){
        lista.evaluarInterrupcion();
    }
    
    public synchronized void continuar() {
        tamBytes = data.length;

        try {
            socket = new Socket("localhost", 5000);
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(nodoDescarga.class.getName()).log(Level.SEVERE, null, ex);
        }

        listen = false;
        running = true;
        operacionBoton = false;
        onBoton = false;
        descargado = false;
        conAn = 0;
        contador2 = 0;
        for(int i=0; i<data3.length; i++)
            if(data3[i])
                contador2++;
        
        if(contador2 == data3.length)
            descargado = true;
        
        this.levantar();
    }
    
    

    public synchronized Thread getHilo() {
        return hilo;
    }

    public synchronized void setHilo(Thread hilo) {
        this.hilo = hilo;
    }

    public int getVelocidad() {
        return velocidad;
    }
    
    public void read(){
        try {
            velocidad = 0;
            byte [] prueba = new byte[cantidadT+1];

            entrada.readFully(prueba);
         
            if(prueba != null) {
                velocidad = prueba.length;
                for(int i=0; i< prueba.length; i++) {
                   data[contador] = prueba[i];
                   data3[contador] = true;
                   contador++;  
                }
            }
         
            int contador2z = 0;
            for(int i=0; i<data3.length; i++)
                if(data3[i])
                    contador2z++;
         
            this.setContador2(contador2z);
         
            listen = false;
            prueba = null;
            System.gc();
        } catch (IOException ex) {
            Logger.getLogger(nodoDescarga.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public synchronized void setContador2(int contador2) {
        this.contador2 = contador2;
    }
    
    public synchronized  int getContador2(){
        return contador2;
    }
    
    public void concatenarDescarga() {
        byte [] dataTemporal = new byte[tamBytes];
        boolean [] dataTemporal3 = new boolean[tamBytes];
        
        for(int i=0; i< tamBytes; i++) {
            if(data3[i])
                dataTemporal[i] = data[i];
            dataTemporal3 [i] = data3[i];
        }
        
        data = null;
        data3 = null;
        data = new byte[suc.getTamBytes() + tamBytes];
        data3 = new boolean[suc.getTamBytes() + tamBytes];
        
        for(int i=0; i< tamBytes; i++) {
            if(dataTemporal3[i])
                data[i] = dataTemporal[i];   
            data3[i] = dataTemporal3[i];
        }
        
        int c = tamBytes;

        for(int i=0; i< suc.getTamBytes(); i++) {
            if(suc.getData3()[i])
                data[c] = suc.getData()[i];
                
            data3[c] = suc.getData3()[i];
            c++;
        }
        
        dataTemporal = null;
        dataTemporal3 = null; 
    }

    public byte[] getData() {
        return data;
    }

    public boolean[] getData3() {
        return data3;
    }

    public int getTamBytes() {
        return tamBytes;
    }

    public synchronized void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }

    public void write() throws IOException{
        boolean solicitar = true;
        contador = 0;
        cantidadT = 0;
        
        while(data3[contador] != false){
            contador++;
            
            if(data3.length == contador) {
                solicitar = false;
                running = false;
                lista.evaluar();
                this.setDescargado(true);
                
                break;
            }
        }
        
        if(solicitar && running) {
            int c2 = contador;
            
            if(c2 + 1 != data3.length) {
                while(data3[c2 + 1] == false && cantidadT < 4500) {
                cantidadT++;
                c2++;
                    if(data3.length == c2 + 1)
                        break;
                }
            }
        }
        
        if(solicitar)
            salida.writeUTF("DD****" + nombre + "****" + Integer.toString(inicio + contador) + "****" + Integer.toString(cantidadT));
        
        listen = true;
    }

    public synchronized boolean isDescargado() {
        return descargado;
    }

    public nodoDescarga getSuc() {
        return suc;
    }

    public void setSuc(nodoDescarga suc) {
        this.suc = suc;
    }

    public nodoDescarga getPre() {
        return pre;
    }

    public void setPre(nodoDescarga pre) {
        this.pre = pre;
    }
    
    public void concatenar(ListaDescarga listaDescarga, byte vector[]){
        for(int i =0 ; i<data.length; i++){
            vector[listaDescarga.getPos()]=data[i];
            listaDescarga.setPos(listaDescarga.getPos() + 1);
        }
    }
    
    public int getInicio(){
        return inicio;
    }
    
    public int getTam(){
        return this.tamBytes;
    }
}


