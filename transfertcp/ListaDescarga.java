package transfertcp;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author JoseCaceres
 */
public class ListaDescarga {
    private nodoDescarga cab,fin;
    private ListaDescarga suc, pre;
    private int cantidad,descargados, interrumpidos;
    private byte data[];
    private String nombre;
    private Cliente cliente;
    private int pos;
    private ListaDescarga listaDescarga;
    private MasterGraphics masterG;
    private JPanel panel;
    private int y, tipo;
    private boolean interrupcion, descargado, parado;

    public ListaDescarga(int tam,String nom, Cliente c, JPanel panel, int y, int tipo) {
        cab = null;
        suc = pre =  null;
        this.tipo = tipo;
        this.y = y;
        interrupcion = descargado = parado = false;
        this.panel = panel;
        fin=null; cantidad=descargados = interrumpidos =0;
        data= new byte[tam];  
        this.nombre=nom;
        this.listaDescarga = this;
        this.cliente=c;
        pos = 0;
        masterG = new MasterGraphics(listaDescarga, y);
    }
    
    public synchronized void evaluarInterrupcion() {
        interrumpidos++;
        if(cantidad==interrumpidos){
            this.interrumpir();
            
        }
    }

    public boolean isParado() {
        int cont=0, contD=0;
        nodoDescarga recorD =cab;
        while(recorD != null) {
            if(recorD.isSleep())
                contD++;
            cont++;
            recorD = recorD.getSuc();
        }
        
        if(contD == cont)
            parado = true;
        else 
            parado = false;

        return parado;
    }
    
    public boolean isContinuar() {
        int cont=0, contD=0;
        nodoDescarga recorD =cab;
        while(recorD != null) {
            if(!recorD.isSleep())
                contD++;
            cont++;
            recorD = recorD.getSuc();
        }
        
        if(contD == cont)
            return true;
        else
            return  false;
    }
    
    public int getCantidad() {
        return cantidad;
    }

    public void setPre(ListaDescarga pre) {
        this.pre = pre;
    }

    public ListaDescarga getPre() {
        return pre;
    }

    public ListaDescarga getSuc() {
        return suc;
    }

    public void setSuc(ListaDescarga suc) {
        this.suc = suc;
    }
    
    public void interrumpir(){
        masterG.setRunning(false);
        masterG = null;
        nodoDescarga eliminar;
        nodoDescarga recor = cab;
        while(recor != null){
            eliminar = recor;
            recor = recor.getSuc();
            eliminar = null;
        }
        cab = fin = null; 
        interrupcion = true;
    }

    public boolean isInterrupcion() {
        return interrupcion;
    }
    
    public int getTipo(){
        return tipo;
    }
    
    public JPanel getPanel(){
        return panel;
    }
    
    public void add(nodoDescarga nuevo){
        cantidad++;
        nodoDescarga recor=null;
      if(cab==null){
          cab=nuevo;
          fin=nuevo;
      } else {
          recor=cab;
          while(recor != null){
              if(recor.getSuc()==null)
                  break;
              else
                  recor= recor.getSuc();
          }
          recor.setSuc(nuevo);
          nuevo.setPre(recor);
          fin=nuevo;  
      }    
    }
    
    public synchronized void evaluar(){
        int cont=0, contD=0;
        nodoDescarga recorD =cab;
        while(recorD != null) {
            if(recorD.isDescargado())
                contD++;
            cont++;
            recorD = recorD.getSuc();
        }
        if(contD == cont){
            this.concatenar();
            this.descargado = true;
        }
    }
    
    public boolean getDescargado() {
        return descargado;
    }
    
    public void concatenar(){
         nodoDescarga  recor=cab, eliminar = null;
         while(recor != null){
          recor.concatenar(listaDescarga,data);
          eliminar = recor;
          recor=recor.getSuc();
          eliminar = null;  
         }
         cliente.recibirData(data,nombre,masterG.getView());
         masterG.setRunning(false);
         masterG = null;
    }

    public MasterGraphics getMasterG() {
        return masterG;
    }
    
    public int getPos(){
        return pos;
    }
    
    public void setPos(int pos){
        this.pos = pos;
    }
    
    public nodoDescarga getCab() {
        return cab;
    }

    public void aumentarDescargas() {
        nodoDescarga ejecutar = null;
           nodoDescarga recorW = cab;
           int mayor = -88, dato=0;
           while(recorW != null) {
               dato = recorW.cantidadNoDescargados();
               if(dato >= mayor) {
                   mayor = dato;
                   ejecutar = recorW;
               }
               recorW = recorW.getSuc();
           }
           
           if(ejecutar != null) {
               ejecutar.dividirDescarga();
               cantidad++;
           }

           if(ejecutar != null)
               continuar2(ejecutar.getSuc());
           else
               continuar();
    }
    
    public void continuar() {
        nodoDescarga recor2 = cab;
            while(recor2 != null) {
                recor2.continuar();
                recor2 = recor2.getSuc();
            }
            
            cliente.recalcularVistas();
            if(masterG != null)
            masterG.setActivo(true);
    }

    public void continuar2(nodoDescarga ejecutar) {
        nodoDescarga recor2 = cab;
            while(recor2 != null) {
                if(recor2 != ejecutar) 
                    recor2.continuar();
                recor2 = recor2.getSuc();
            }
            
            cliente.recalcularVistas();
            if(masterG != null)
                masterG.setActivo(true);
    }

    public void pararDescargas() {
        if(masterG != null) {
            nodoDescarga  recor5=cab;
            masterG.setActivo(false);
            
            while(recor5 != null) {
                recor5.setOnBoton(true);
                recor5.setRunning(false);
                
                while(recor5.getHilo() != null) {
                    System.out.println("JAJA");
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ListaDescarga.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    recor5.setOnBoton(true);
                recor5.setRunning(false);
                }
                recor5 = recor5.getSuc();
            }

            int cont = 0;
            recor5 = cab;
            while(recor5 != null) {
                
                if(recor5.getHilo() == null)
                    cont++;

                recor5 = recor5.getSuc();
            }
            if(cont == cantidad)
                System.out.println("Estan Parados todos");
            else
                System.out.println("No Estan Parados todos  cantidad: " + cantidad +"  Cont: "+ cont);
        }       
    }

    public void setParado(boolean parado) {
        this.parado = parado;
    }
    
    public void disminuirDescargas() {
        cab.concatenarDescarga();
        nodoDescarga eliminar = cab.getSuc();
            
        if(cab.getSuc().getSuc() != null) { 
            cab.setSuc(cab.getSuc().getSuc());
            cab.getSuc().setPre(cab);
        } else 
            cab.setSuc(null);
            
            eliminar = null;
            cantidad--;
            continuar();
    } 
}
