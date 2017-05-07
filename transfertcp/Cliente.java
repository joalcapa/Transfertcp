package transfertcp;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author JoseCaceres
 */
public class Cliente extends JFrame implements MouseListener, MouseMotionListener{
    private Cliente frame;
    private nodoRecurso reproduciendo;
    private Reproductor basicPlayer;
    private JPanel panelSuperior;
    private PanelDescargado panelAux;
    private JPanel panelImagen;
    private JPanel panelInferior;
    private JPanel contenido;
    private JPanel panelScroll, scroll;
    private JPanel contenidoEspecial;
    private JPanel panlNombre;
    private JLabel nombreDes = new JLabel();
    private JLabel labelBarraSuperior = new JLabel(new ImageIcon(getClass().getResource("img/barraSuperior.png")));
    private JLabel labelBarraInferior = new JLabel(new ImageIcon(getClass().getResource("img/barraInferior.png")));
    private JLabel fondo = new JLabel(new ImageIcon(getClass().getResource("img/fondo.png")));
    private JLabel btnCerrar = new JLabel(new ImageIcon(getClass().getResource("img/btnCerrar.png")));
    private JLabel tittle = new JLabel("Aplicacion Cliente");
    private JLabel popUp = new JLabel(new ImageIcon(getClass().getResource("img/popup.png")));
    private JLabel fragmentosTxt = new JLabel("En cuantos fragmentos deseas descargar el archivo");
    private JLabel nombreTxt = new JLabel("");
    private JLabel ooo = new JLabel(new ImageIcon(getClass().getResource("img/ooo.png")));
    private JLabel conexion = new JLabel("estableciendo conexion con el servidor ...");
    private JLabel msjLocal = new JLabel(new ImageIcon(getClass().getResource("img/ops.png")));
    private JLabel btnCerrarPopUp = new JLabel(new ImageIcon(getClass().getResource("img/bcpu.png")));
    private JLabel btnCerrarPopUp2 = new JLabel(new ImageIcon(getClass().getResource("img/bcpu.png")));
    private JLabel btnD = new JLabel(new ImageIcon(getClass().getResource("img/btnD.png")));
    private JLabel c2 = new JLabel(new ImageIcon(getClass().getResource("img/cortina2.png")));
    private ListaRecurso listaRecurso;
    private JTextArea areaFragmentos;
    private nodoRecurso nodoSeleccionado;
    private int posY, scrollY, tamScroll;;
    private MultilistaDescarga multiDescargas;
    private Comunicacion socket;
    private boolean popUpActivo;

    public Cliente() {
        super("");
        setUndecorated(true);
        setBounds(0,0,720,520);
        setLocationRelativeTo(null);
        setLayout(null);
        popUpActivo = false;
        frame = this;
        posY = 0;
        scrollY = 0;
        tamScroll = 0;      
        try {
         Class clazz =  Class.forName("com.sun.awt.AWTUtilities");
         Method method = clazz.getMethod("setWindowOpaque", java.awt.Window.class, Boolean.TYPE);
         method.invoke(clazz, frame ,false);  
        } catch (Exception e) {}
        inicializarRecursos();
        implementarListeners();
        multiDescargas = new MultilistaDescarga();
        socket = new Comunicacion(frame);
        recalcularScroll();
        setVisible(true);
    }
    
    public void prepararMusica(InputStream stream, boolean pause, boolean primeraVez, nodoRecurso reproduciendo) {
        if(this.reproduciendo != null && primeraVez) {
            this.reproduciendo.setPause(false);
            this.reproduciendo.setInicio(true);
            this.reproduciendo.getEtiqueta2().setIcon( new ImageIcon(getClass().getResource("img/play.png")) );
        }  
        if(primeraVez) {
            try {
                basicPlayer.stop();
            } catch (Exception ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.reproduciendo = reproduciendo;
            basicPlayer.open(stream);
            try {
                basicPlayer.play();
            } catch (Exception ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            
            if(pause)
                basicPlayer.continuar();
            else
                basicPlayer.pause();
            
        } catch (Exception ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void stop(){
        try {
            basicPlayer.stop();
        } catch (Exception ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void play() {
        try {
            basicPlayer.play();
        } catch (Exception ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void pause() {
        try {
            basicPlayer.pause();
        } catch (Exception ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void eliminarDescargas() {
        this.multiDescargas.eliminarDescargas();
    }

    public void actualizarNodoRecurso(String archivo, int length, int tipo, int agregar) {
        if(agregar == 1)
            listaRecurso.add(new nodoRecurso(archivo, tipo, false, length, null, frame));
        else
            listaRecurso.delete(archivo);
        this.recalcularScroll();
    }
    
    public static void main(String[] args) {
        Cliente cliente = new Cliente();
    }

    public JLabel getConexion() {
        return conexion;
    }
    
    public void mostrarImagen(byte [] data, String n2){
        multiDescargas.tratarMaster(false);
        c2.setVisible(true);
        nombreDes.setText("   " +n2);
        panelAux.setImage(data);
        panelImagen.setVisible(true);
    }
    
    public void inicializarRecursos() {
        basicPlayer = new Reproductor();
        panelSuperior = new JPanel();
        panelSuperior.setBounds(0,10,720,50);
        panelSuperior.setLayout(null);
        panelSuperior.setOpaque(false);
        panelImagen = new JPanel();
        panelImagen.setLayout(null);
        panelImagen.setBackground(new Color(14,14,14));
        panelImagen.setBounds(130,0,400,390);
        reproduciendo = null;
        panlNombre = new JPanel();
        panlNombre.setBounds(0, 350, 400, 40);
        ooo.setBounds(0,0,400,40);
        panlNombre.setBackground(Color.BLACK);
        panlNombre.setLayout(null);
        panlNombre.setOpaque(false);
        panelAux = new PanelDescargado();
        nombreDes.setBounds(0,0,400,40);
        nombreDes.setFont(new Font("Arial", Font.PLAIN, 14));
        nombreDes.setForeground(Color.WHITE);
        panlNombre.add(nombreDes);
        panlNombre.add(ooo);   
        panelScroll = new JPanel();
        panelScroll.setBounds(630,0,30,390);
        panelScroll.setLayout(null);
        panelScroll.setBackground(Color.BLACK);
        panelScroll.addMouseMotionListener(this);
        scroll = new JPanel();
        scroll.setBounds(0,0,30,10);
        scroll.setLayout(null);
        scroll.setBackground(new Color(0,204,255));
        scroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelInferior = new JPanel();
        panelInferior.setBounds(0,450,720,70);
        panelInferior.setLayout(null);
        panelInferior.setOpaque(false);      
        conexion.setBounds(30, 5, 400, 30);
        conexion.setFont(new Font("Arial", Font.PLAIN, 14));
        conexion.setForeground(Color.RED);
        msjLocal.setBounds(-25,0,660,390); 
        contenido = new JPanel();
        contenido.setBounds(35,60,660,390);
        contenido.setLayout(null);
        contenido.setOpaque(false);
        contenidoEspecial = new JPanel();
        contenidoEspecial.setLayout(null);
        contenidoEspecial.setBackground(Color.red);
        contenidoEspecial.setOpaque(false);
        listaRecurso = new ListaRecurso(contenidoEspecial);
        c2.setBounds(0,0,660,390);
        c2.setVisible(false);
        popUp.setBounds(130,95,400,200);
        popUp.setVisible(false);
        fragmentosTxt.setFont(new Font("Arial", Font.PLAIN, 15));
        fragmentosTxt.setForeground(new Color(18,18,18));
        fragmentosTxt.setBounds(150,127,400,50);
        fragmentosTxt.setVisible(false);
        areaFragmentos = new JTextArea();
        areaFragmentos.setFont(new Font("Arial", Font.PLAIN, 33));
        areaFragmentos.setForeground(Color.WHITE);
        areaFragmentos.setBackground(Color.BLACK);
        areaFragmentos.setBounds(155,200,350,40);
        areaFragmentos.setVisible(false);
        areaFragmentos.setText("1");
        btnD.setBounds(260,245,150,50);
        btnD.setVisible(false);
        btnD.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarPopUp.setBounds(480,95,50,50);
        btnCerrarPopUp.setVisible(false);
        btnCerrarPopUp.setCursor(new Cursor(Cursor.HAND_CURSOR));   
        btnCerrarPopUp2.setBounds(350,0,50,50);
        btnCerrarPopUp2.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        nombreTxt.setFont(new Font("Arial", Font.PLAIN, 12));
        nombreTxt.setForeground(new Color(18,18,18));
        nombreTxt.setBounds(150,140,400,70);
        nombreTxt.setVisible(false); 
        labelBarraSuperior.setBounds(15,0,690,50);
        labelBarraInferior.setBounds(15,0,690,50);
        fondo.setBounds(0,0,720,520);   
        btnCerrar.setBounds(645,12,60,40);
        tittle.setFont(new Font("Arial", Font.PLAIN, 20));
        tittle.setForeground(Color.WHITE);
        tittle.setBounds(50,27,690,20); 
        panelSuperior.add(tittle);
        panelSuperior.add(btnCerrar);
        panelSuperior.add(labelBarraSuperior);
        panelInferior.add(conexion);
        panelInferior.add(labelBarraInferior); 
        add(panelSuperior);
        add(panelInferior);
        add(contenido);
        add(fondo);  
        panelImagen.add(btnCerrarPopUp2);
        panelImagen.add(panlNombre);
        panelImagen.add(panelAux);
        panelImagen.setVisible(false);
        contenido.add(panelImagen);
        contenido.add(msjLocal);
        contenido.add(btnD);
        contenido.add(areaFragmentos);
        contenido.add(nombreTxt);
        contenido.add(fragmentosTxt);
        contenido.add(btnCerrarPopUp);
        contenido.add(popUp);
        contenido.add(c2);  
        contenido.add(contenidoEspecial);
        contenido.add(panelScroll); 
        panelScroll.add(scroll);
    }
    
    
    public void implementarListeners() {
        btnCerrar.addMouseListener(this);
        panelSuperior.addMouseMotionListener(this);
        btnCerrarPopUp.addMouseListener(this);
        btnCerrarPopUp2.addMouseListener(this);
        btnD.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == btnCerrar) {
            frame.dispose();
            socket.setRunning(false);
            try {
                socket.getEntrada().close();
                socket.getSalida().close();
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(e.getSource() == this.btnCerrarPopUp) 
            this.cerrarPopUp();
        
        if(e.getSource() == this.btnCerrarPopUp2) {
            this.multiDescargas.tratarMaster(true);
            this.panelImagen.setVisible(false);
            c2.setVisible(false); 
        }
        
        if(e.getSource() == this.btnD ) {
            if(evaluarCampo()) {
            this.prepararDescarga();
                this.cerrarPopUp();
            }else
            this.areaFragmentos.setText("1");
        }
    }
    
   
    
    public void prepararDescarga() {
       nodoSeleccionado.cambiarLogo();
       int tamtotal=(int) nodoSeleccionado.getLength()/(Integer.parseInt(areaFragmentos.getText()));
       int cantidad=nodoSeleccionado.getLength();
       int inicio=0;
       ListaDescarga listaDescarga= new ListaDescarga(
                                        cantidad,
                                        nodoSeleccionado.getNombre(),
                                        frame, nodoSeleccionado.getPanel(),
                                        nodoSeleccionado.getY(),
                                        nodoSeleccionado.getTipo()
                                    );
       
       for(int i=0; i< Integer.parseInt(areaFragmentos.getText()); i++) {
           if(i< Integer.parseInt(areaFragmentos.getText())-1){
               listaDescarga.add(new nodoDescarga(inicio, tamtotal, nodoSeleccionado.getNombre(),listaDescarga, true));
               inicio=inicio+tamtotal;
           }else{
               cantidad= cantidad-(tamtotal*(i));
               listaDescarga.add(new nodoDescarga(inicio, cantidad, nodoSeleccionado.getNombre(),listaDescarga, true)); 
           }
       }
       
       multiDescargas.add(listaDescarga);
       nodoSeleccionado.setListaDescarga(multiDescargas.getFin());
       
       if(listaDescarga.getCantidad() == 1)     
           nodoSeleccionado.getNpMenos().setVisible(false);
       else
           nodoSeleccionado.getNpMenos().setVisible(true);
       
       nodoSeleccionado.getNpMas().setVisible(true);
       nodoSeleccionado.getBuff().setVisible(true);
       recalcularVistas();   
    }
    
    public void recalcularVistas() {
       listaRecurso.recalcular();
       recalcularScroll();
    }
    
    public boolean evaluarCampo() {
        String particiones = areaFragmentos.getText();
        if(particiones.length() != 0) {
            char [] vector = particiones.toCharArray();
            for(int i = 0; i < vector.length; i++)
                if(!((int) vector[i] >= 48 && (int) vector[i] <= 57)) 
                    return false;
            
            if(Integer.parseInt(particiones) == 0)
                return false;
        else
            return true;
        } else 
            return false;
    }
    
    public void cerrarPopUp() {
        popUp.setVisible(false);
        fragmentosTxt.setVisible(false);
        nombreTxt.setVisible(false);
        areaFragmentos.setVisible(false);
        btnCerrarPopUp.setVisible(false);
        btnD.setVisible(false);
        popUpActivo = false;
        c2.setVisible(false);
        areaFragmentos.setText("1");
        this.multiDescargas.tratarMaster(true);
    }
    
    public void procesoParaDescargar(String nombre, nodoRecurso nodoSeleccionado) {
        popUp.setVisible(true);
        fragmentosTxt.setVisible(true);
        nombreTxt.setText(nombre);
        nombreTxt.setVisible(true);
        areaFragmentos.setVisible(true);
        btnCerrarPopUp.setVisible(true);
        btnD.setVisible(true);
        popUpActivo = true;
        c2.setVisible(true);
        this.nodoSeleccionado = nodoSeleccionado;
        this.multiDescargas.tratarMaster(false);
        
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {       }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if(e.getSource() == panelSuperior )
            frame.setBounds(e.getXOnScreen()-50, e.getYOnScreen()-30, 720, 550);
    }

    public void recalcularScroll(){
        double k = (double) listaRecurso.getTam();
        double por = (double) (390.0 / k);
        tamScroll = (int) (por * (390));
        scroll.setBounds(0, 0, 30, tamScroll);
        if(k - 200 <= 390) {
           listaRecurso.setY(0);
           contenidoEspecial.setBounds(0, this.listaRecurso.getY(), 650, listaRecurso.getJ()+200);
           scroll.setVisible(false);
        } else
           scroll.setVisible(true);
        
        if(k == 250 || k == 0) 
            msjLocal.setVisible(true);
         else
            msjLocal.setVisible(false);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if(!popUpActivo) 
            if(e.getSource() == this.panelScroll && (this.listaRecurso.getTam() - 200 > 390)) 
                if(e.getY() >= 0 && e.getY() <= 390 - tamScroll + 5) {
                    scrollY = e.getY();
                    double proporcion = (double) scrollY / (double) (390.0 - tamScroll + 5);
                    int escalar = (int) (proporcion * (listaRecurso.getJ() - 250));
                    escalar = escalar * -1;
                    scroll.setBounds(0, scrollY, 30, tamScroll);
                    listaRecurso.setY(escalar);
                    contenidoEspecial.setBounds(0, this.listaRecurso.getY(), 650, listaRecurso.getJ()+200);
           }
    }
    
    public void recibirData(byte vector[],String nombre, JPanel panel){
        listaRecurso.ingresarData(vector,nombre, panel); 
    }  
}


