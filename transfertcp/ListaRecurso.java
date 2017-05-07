package transfertcp;

import java.io.DataOutputStream;
import java.io.IOException;
import javax.swing.JPanel;

/**
 *
 * @author JoseCaceres
 */
public class ListaRecurso {
    private nodoRecurso cab, recor, recorA, fin, anterior;
    private JPanel panel;
    private int j, tam, y;
    public ListaRecurso(JPanel panel){
        cab = fin = recor = anterior = null;
        j = 50;
        tam = 0; y = 0;
        this.panel = panel;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public synchronized boolean add(nodoRecurso dato){
        if(!this.getRepetido(dato.getNombre())){
            this.insertar(dato);
            return true;
        }else {
            dato = null; 
            return false;
        }
    }
    
    public void insertar(nodoRecurso dato) {
        if(cab == null)
            cab = fin = dato;
        else {
            if(cab.getNombre().compareToIgnoreCase(dato.getNombre()) >= 0)
            {
                dato.setSuc(cab);
                cab.setPre(dato);
                cab = dato;
            }
            else
            {
                recor = cab;
                anterior = cab;
                while(recor.getNombre().compareToIgnoreCase(dato.getNombre()) < 0)
                {
                    anterior = recor;
                    recor = recor.getSuc();
                    
                    if(recor == null)
                        break;
                }
                 if(fin != anterior)
                {
                    dato.setSuc(anterior.getSuc());
                    anterior.getSuc().setPre(dato);
                }
                else
                    fin = dato;

                anterior.setSuc(dato);
                dato.setPre(anterior);  
            }  
        } 
        panel.add(dato.getPanel());
        recalcular();
    }
   
    public boolean getRepetido(String nombre) {
        recor = cab;    
        while(recor!=null) {
            if(recor.getNombre().compareToIgnoreCase(nombre) == 0)
            return true;
            else
            recor = recor.getSuc();
        }    
        return false;
    }
    
    public int getJ(){
        return j;
    }

    public int getTam() {
        return tam;
    }
    
    public nodoRecurso getCab(){
        return cab;
    }
    
    public void cambiarNodo(int pos, boolean estado) {
        recor = cab;
        for(int i=0; i<pos; i++)
            recor = recor.getSuc();
        recor.cambiar(estado);
    }
    
    public synchronized void actualizarTodo(DataOutputStream salida, int tipo) {
        recorA = cab;
        while(recorA != null) {
            if(recorA.getOn())
                this.datagram(salida, recorA.getTipo(),1);
            else
                this.datagram(salida, recorA.getTipo(),0);
            recorA = recorA.getSuc();
        }
    }
    
    public synchronized void actualizarUno(DataOutputStream salida, int pos, int tipo){
        recorA = cab;
        for(int i=0; i<pos; i++)
            recorA = recorA.getSuc();
        if(recorA.getOn())
            datagram(salida, recorA.getTipo(),1);
        else
            datagram(salida, recorA.getTipo(),0);
    }
    
    public void datagram(DataOutputStream salida, int tipo, int agregar){
        try {
            salida.writeUTF("AA****" + Integer.toString(recorA.getPos()) + "****" + Integer.toString(tipo) + "****" + recorA.getNombre() + "****" + Integer.toString(recorA.getLength()) + "****" + Integer.toString(agregar));
        } catch (IOException ex) { System.out.println("Se ha desconectado el cliente"); }
    }
    
    public void delete(String archivo) {
        recor = cab;
        int pos=0;
        nodoRecurso suc = null, pre = null, eliminar = null; 
        while(recor != null){
            if(recor.getNombre().compareTo(archivo)!=0) 
                recor=recor.getSuc();
            else
                break;        
        }
        
        if(recor!= null) 
            if(!recor.getDescargado()) {
                if(recor.getPre()==null)
                    cab=recor.getSuc();
                else
                    recor.getPre().setSuc(recor.getSuc());
                
                if(recor.getSuc()==null)
                    fin=recor.getPre();
                else
                    recor.getSuc().setPre(recor.getPre());
    
                panel.setVisible(false);
                panel.remove(recor.getPanel());  
                recor=null;
                panel.setVisible(true);
                recalcular();
            } 
    }
    
    public void recalcular(){
        int h = 40;
        j = 50; 
        if(cab != null) {
            nodoRecurso recor2 = cab;
            int pos = 0;
            int cont=0;
            while(recor2!=null) {
                h = 40;
                cont=0;
                if(recor2.getDescargando()) {
                    nodoDescarga recor = recor2.getListaDescarga().getCab();
                    while(recor != null){
                        recor = recor.getSuc();
                        cont++;   
                    }
                    h = (65*cont)+58;   
                }
                recor2.setPosition(j, h);
                recor2.setPos(pos);
                j = j + 10 + h;
                pos++;
                recor2 = recor2.getSuc();
            }
        }
        panel.setBounds(0,y,630,j+200);
        tam = j + 200;
    }
    
    public synchronized void ingresarData(byte [] vector,String nombre, JPanel panel){ 
        recor=cab;
        if(recor != null){
                while(recor != null)
                    if(recor.getNombre().compareTo(nombre)==0)
                        break;
                    else
                        recor=recor.getSuc(); 
                recor.concatenar(vector, panel);
                recalcular();
                recor.getCliente().recalcularScroll();
        }
    }

    public int getY() {
        return y;
    }
}


