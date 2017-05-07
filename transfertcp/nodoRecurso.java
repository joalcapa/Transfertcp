package transfertcp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author JoseCaceres
 */
public class nodoRecurso implements MouseListener, Runnable {
    private JPanel panel;
    private JLabel etiqueta;
    private JLabel tittle;
    private JLabel etiqueta2;
    private JLabel buff = new JLabel(new ImageIcon(getClass().getResource("img/buff.png")));
    private JLabel npMas;
    private JLabel npMenos;
    private Thread hilo;
    private nodoRecurso suc, pre;
    private byte[] data;
    private int tipo, length, pos, y;
    private String nombre;
    private boolean server, on, disponible, descargado, descargando, descargar, bajar, subir, running;
    private boolean pause, inicio;
    private Server servidor;
    private Cliente cliente;
    private nodoRecurso nodo;
    private ListaDescarga listaDescarga;
    private int altura;

    public nodoRecurso(String nombre, int tipo, boolean server, int length, Server servidor, Cliente cliente) {
        suc = pre = null;
        pause = false;
        listaDescarga = null;
        running = true;
        inicio = true;
        this.nombre = nombre;
        this.tipo = tipo;
        this.server = server;
        hilo = null;
        this.length = length;
        altura = 40;
        nodo = this;
        this.cliente = cliente;
        on = descargado = descargar = descargando = bajar = subir = false;
        
        if(server)
            disponible = true;
        else
            disponible = false;
        
        inicializarRecursosGraficos();
        this.servidor = servidor;
    }
    
    public void cambiarLogo(){
        etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/descargando.png")));
        descargando = true;
    }
    
    public int getPos(){
        return pos;
    }
    
    public void setPos(int pos) {
        this.pos = pos;
    }
    
    public int getLength() {
        return length;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public nodoRecurso getPre() {
        return pre;
    }
    
    public void setPre(nodoRecurso pre) {
        this.pre = pre;
    }
    
    public void setData(byte[] data){
        this.data = data;
        
    }
    
    public void setAltura(int h){
        altura = h;
    }
    
    public void inicializarRecursosGraficos() {
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.BLACK);
        
        tittle = new JLabel(nombre);
        tittle.setFont(new Font("Arial", Font.PLAIN, 15));
        tittle.setForeground(Color.WHITE);
        tittle.setBounds(80,0,620,40);
        
        if(tipo == 0) {
            etiqueta = new JLabel(new ImageIcon(getClass().getResource("img/audio.png")));
            if(servidor == null) {
            npMas = new JLabel(new ImageIcon(getClass().getResource("img/ba.png")));
            npMenos = new JLabel(new ImageIcon(getClass().getResource("img/bd.png"))); 
            }
        } else {
            if(tipo == 1){
                etiqueta = new JLabel(new ImageIcon(getClass().getResource("img/imagen.png")));
                if(servidor == null) {
                npMas = new JLabel(new ImageIcon(getClass().getResource("img/ga.png")));
            npMenos = new JLabel(new ImageIcon(getClass().getResource("img/gd.png")));
                }
            } else {
                etiqueta = new JLabel(new ImageIcon(getClass().getResource("img/otro.png")));
                if(servidor == null) {
                npMas = new JLabel(new ImageIcon(getClass().getResource("img/ra.png")));
            npMenos = new JLabel(new ImageIcon(getClass().getResource("img/rd.png")));
                }
            }
        }
        
        if(server)
            etiqueta2 = new JLabel(new ImageIcon(getClass().getResource("img/off.png")));
        else 
            etiqueta2 = new JLabel(new ImageIcon(getClass().getResource("img/descargar.png")));
        
        
        etiqueta.setBounds(0,0,60,40); 
        etiqueta2.setBounds(560,0,60,40);
        
        etiqueta2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        etiqueta2.addMouseListener(this);
            
        if(servidor == null) {
            buff.setBounds(360,0,200,40);
            npMas.setBounds(500,0,40,40);
            npMenos.setBounds(440,0,40,40);
        
            npMas.addMouseListener(this);
            npMenos.addMouseListener(this);
        
            npMas.setCursor(new Cursor(Cursor.HAND_CURSOR));
            npMenos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
            buff.setVisible(false);
            npMas.setVisible(false);
            npMenos.setVisible(false);
            panel.add(npMas);
            panel.add(npMenos);
            panel.add(buff);
        }

        panel.add(etiqueta2);
        panel.add(tittle);
        panel.add(etiqueta);
    }
    
    public void iniciarHilo() {
        if(hilo == null) {
            running = true;
        hilo = new Thread(this);
        hilo.start();
        }
    }
    
    public nodoRecurso getSuc(){
        return suc;
    }
    
    public void setSuc(nodoRecurso suc){
        this.suc = suc;
    }
    
    public void setPosition(int j, int altura){
        y = j;
        this.altura = altura;
        panel.setBounds(0,j,620,altura);
    }
    
    public int getY(){
        return y;
    }
    
    public JPanel getPanel(){
        return panel;
    }
    
    public void setListaDescarga(ListaDescarga listaDescarga){
        this.listaDescarga = listaDescarga;
    }
    
    public boolean getOn() {
        return on;
    }
    
    public boolean getDescargando(){
        return this.descargando;
    }
    
    public ListaDescarga getListaDescarga(){
        return listaDescarga;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void setInicio(boolean inicio) {
        this.inicio = inicio;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == etiqueta2){
           if(server){
               if(!on) {
                   etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/on.png")));
                   on = true;
               } else {
                   servidor.eliminarPosibleDescarga(nombre);
                   etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/off.png")));
                   on = false;
               }
               
               servidor.enviarUpdate(pos);
           } else {
               
               if(!descargado && !descargando) {
               if(descargar) {
                   
                   cliente.procesoParaDescargar(this.nombre, nodo);
                  // etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/descargando.png")));
                   descargar = false;
               } else {
                   
                   etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/descargar.png")));
                   descargar = true;
                   
               }
               }
               
               
               if(descargado) {
                   if(tipo == 1)
                       mostrarImagen();
                   if(tipo == 0)
                       reproducirMusica();
                   if(tipo == 2)
                       abrir();
               }
           }
        }
        
        if(e.getSource() == npMas) {
           
            subir = true;
            npMas.setVisible(false);
        npMenos.setVisible(false);
        this.iniciarHilo();
        }
        
        if(e.getSource() == npMenos){
            bajar = true;
           
            npMas.setVisible(false);
        npMenos.setVisible(false);
        this.iniciarHilo();
        }
    }
    
    public void reproducirMusica() {
        if(!pause)
        etiqueta2.setIcon( new ImageIcon(getClass().getResource("img/pause.png")) );
        else
        etiqueta2.setIcon( new ImageIcon(getClass().getResource("img/play.png")) );
        
     InputStream bytes = new ByteArrayInputStream(data); 
        if(!pause)
            pause = true;
        else
            pause = false;
        
        cliente.prepararMusica(bytes, pause, inicio, this);
        inicio = false; 
    }

    public JLabel getEtiqueta2() {
        return etiqueta2;
    }
   
    
    public void mostrarImagen(){
        cliente.mostrarImagen(data, this.nombre);
         
    }
    
    public void abrir(){  
        StringTokenizer token = new StringTokenizer(nombre, ".");
        String nom = token.nextToken();
        String ext = token.nextToken();      
        File tempFile = null;
        File directorio= new File("C:\\Users\\oscar.parra\\Desktop");
  
        try {
            tempFile = File.createTempFile(nom,"."+ext,null);
        } catch (IOException ex) {
            Logger.getLogger(nodoRecurso.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(nodoRecurso.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fos.write(data);
        } catch (IOException ex) {
            Logger.getLogger(nodoRecurso.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        try {
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(nodoRecurso.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {  
            Desktop.getDesktop().open(tempFile);
        } catch (IOException ex) {
            Logger.getLogger(nodoRecurso.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }

    public JLabel getBuff() {
        return buff;
    }

    public JLabel getNpMas() {
        return npMas;
    }

    public JLabel getNpMenos() {
        return npMenos;
    }
    
    public void interrumpir(JPanel panel){
        this.panel.remove(panel);
        descargando = descargado = false;
        descargar = true;
        etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/descargar.png")));
         buff.setVisible(false);
        npMas.setVisible(false);
        npMenos.setVisible(false);
    }
    
    public int getTipo(){
        return tipo;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    public void cambiar(boolean estado) {
        if(!server && !descargado) {
            on = estado;
            if(!estado) 
                etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/descargar.png")));
        }
    }
    
    public synchronized void descargarByte(int posicion, int cantidad, DataOutputStream salida, nodoClienteServidor nodo){
        try { 
            byte [] dd = new byte[cantidad + 1];
            for(int i=0; i< cantidad+1; i++)
                dd[i] = data[posicion + i];
            salida.write(dd);
            dd= null;
        } catch (IOException ex) {
            nodo.setPerdidaSignal(true);
            nodo.setRunning(false);
        } 
    }

    public byte[] getData() {
        return data;
    }
    
    public synchronized void concatenar(byte vector[], JPanel panel){
        this.panel.remove(panel);
        descargando = false;
        data = new byte[this.length];
        
        for(int i=0 ; i<data.length; i++)
            data[i]=vector[i];
       
        if(tipo != 0)
        etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/go.png")));
        else
        etiqueta2.setIcon(new ImageIcon(getClass().getResource("img/play.png")));   
        setDescargado(true);
        buff.setVisible(false);
        npMas.setVisible(false);
        npMenos.setVisible(false);
    }
    
    public boolean getDescargado(){
        return descargado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    @Override
    public void run() {
        listaDescarga.pararDescargas();

        if(subir)
            listaDescarga.aumentarDescargas();
        if(bajar)
            listaDescarga.disminuirDescargas();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(nodoRecurso.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        bajar = false;
        subir = false;
        hilo = null;
        
        if(listaDescarga != null && !this.isDescargado()) 
            if(listaDescarga.getCantidad() == 1)
                this.npMenos.setVisible(false);
            else
                this.npMenos.setVisible(true);
        
        npMas.setVisible(true);
        listaDescarga.setParado(false);
    }

    public synchronized boolean isDescargado() {
        return descargado;
    }

    public synchronized void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }    
}


