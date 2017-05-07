package transfertcp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author JoseCaceres
 */
public class Server extends JFrame implements MouseListener, Runnable, MouseMotionListener {
    private Server frame;
    private ServerSocket servidor;
    private Socket socket;
    private ListaClienteServidor listaClienteServidor;
    private Thread hilo; 
    private JPanel panelSuperior;
    private JPanel panelInferior;
    private JPanel panelScroll, scroll;
    private JPanel contenido;
    private JPanel contenidoEspecial;
    private JLabel labelBarraSuperior = new JLabel(new ImageIcon(getClass().getResource("img/barraSuperior.png")));
    private JLabel labelBarraInferior = new JLabel(new ImageIcon(getClass().getResource("img/barraInferior.png")));
    private JLabel fondo = new JLabel(new ImageIcon(getClass().getResource("img/fondo.png")));
    private JLabel btnCerrar = new JLabel(new ImageIcon(getClass().getResource("img/btnCerrar.png")));
    private JLabel tittle = new JLabel("Server - administracion");
    private JLabel msjLocal = new JLabel(new ImageIcon(getClass().getResource("img/ops2.png")));
    private JLabel btnA = new JLabel(new ImageIcon(getClass().getResource("img/btnA.png")));
    private ListaRecurso listaRecurso;
    private int posY, scrollY, tamScroll, escalar;
    private final int port = 5000;
    private boolean listen, analizandoDatos;
    private HiloServerUpdate hiloServerUpdate;
    
    public Server() {
        super("");
        setUndecorated(true);
        setBounds(0,0,720,520);
        setLocationRelativeTo(null);
        setLayout(null);
        frame = this;
        listen = true;
        analizandoDatos = false;
        posY = 0;
        scrollY = 0;
        escalar = 0;
        try {
         Class clazz =  Class.forName("com.sun.awt.AWTUtilities");
         Method method = clazz.getMethod("setWindowOpaque", java.awt.Window.class, Boolean.TYPE);
         method.invoke(clazz, frame ,false);  
        } catch (Exception e){} 
        inicializarRecursos();
        implementarListeners();
        hiloServerUpdate = new HiloServerUpdate(this);
        recalcularScroll();
        hilo = new Thread(this);
        hilo.start();
        setVisible(true);  
    }

    public JLabel getMsjLocal() {
        return msjLocal;
    }
    
    public void inicializarServer(){
        try {
            servidor = new ServerSocket(port);
            listaClienteServidor = new ListaClienteServidor();
            socket = null;
            while(listen) {
                socket = servidor.accept();
                if(socket != null){
                nodoClienteServidor cliente = new nodoClienteServidor(socket, listaRecurso, this);
                listaClienteServidor.add(cliente);
                }  
            }
        } catch (IOException ex) {}
    }
   
    public static void main(String[] args) {
        Server servidor = new Server();
    }
    
    public void inicializarRecursos() {
        panelSuperior = new JPanel();
        panelSuperior.setBounds(0,10,720,50);
        panelSuperior.setLayout(null);
        panelSuperior.setOpaque(false);
        msjLocal.setBounds(-25,0,660,390);
        btnCerrar.setBounds(645,12,60,40);
        panelScroll = new JPanel();
        panelScroll.setBounds(630,0,30,390);
        panelScroll.setLayout(null);
        panelScroll.setBackground(Color.BLACK);
        panelScroll.addMouseMotionListener(this);
        scroll = new JPanel();
        scroll.setLayout(null);
        scroll.setBackground(new Color(0,204,255));
        panelScroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelInferior = new JPanel();
        panelInferior.setBounds(0,450,720,70);
        panelInferior.setLayout(null);
        btnA.setBounds(230,340,200,50);
        btnA.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelInferior.setOpaque(false);
        btnA.addMouseListener(this);
        contenido = new JPanel();
        contenido.setBounds(35,60,660,390);
        contenido.setLayout(null);
        contenido.setBackground(Color.red);
        contenido.setOpaque(false);
        contenidoEspecial = new JPanel();
        contenidoEspecial.setLayout(null);
        contenidoEspecial.setOpaque(false);
        listaRecurso = new ListaRecurso(contenidoEspecial);
        labelBarraSuperior.setBounds(15,0,690,50);
        labelBarraInferior.setBounds(15,0,690,50);
        fondo.setBounds(0,0,720,520);
        tittle.setFont(new Font("Arial", Font.PLAIN, 20));
        tittle.setForeground(Color.WHITE);
        tittle.setBounds(50,27,690,20);
        panelSuperior.add(tittle);
        panelSuperior.add(btnCerrar);
        panelSuperior.add(labelBarraSuperior);
        panelInferior.add(labelBarraInferior);
        add(panelSuperior);
        add(panelInferior);
        add(contenido);
        add(fondo); 
        contenido.add(btnA);
        contenido.add(msjLocal);
        contenido.add(contenidoEspecial);
        contenido.add(panelScroll);
        panelScroll.add(scroll); 
    }
    
   
    public void recalcularScroll(){
        double k = (double) listaRecurso.getTam();
        double por = (double) (390.0 / k);
        tamScroll = (int) (por * (390));
        scroll.setBounds(0, escalar, 30, tamScroll);
        if(k - 200 <= 390)
            scroll.setVisible(false);
        else
            scroll.setVisible(true);
    }
    
    
    public void implementarListeners() {
        btnCerrar.addMouseListener(this);
        panelSuperior.addMouseMotionListener(this);
        panelSuperior.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == btnCerrar){
            listen = false;
            try {
                servidor.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.listaClienteServidor.cerrarConexiones();
            frame.dispose();
        }
        
        if(e.getSource() == btnA) {
            btnA.setCursor(null);
            btnA.setIcon(new ImageIcon(getClass().getResource("img/btnA2.png")));
            hiloServerUpdate.ejecutarHilo();
        }
    }

    public boolean isListen() {
        return listen;
    }

    public void recolectarDescargados() {
        listaClienteServidor.eliminarDescargados();
    }

    public void eliminarPosibleDescarga(String nombre) {
        listaClienteServidor.eliminarDescarga(nombre);
    }
    
    
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    public void evaluarPosY(){}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public void setAnalizandoDatos(boolean analizandoDatos) {
        this.analizandoDatos = analizandoDatos;
    }
    
    public void analizarDatos() {
        File file;
        file = new File("public");        
        this.analizarDatosEnDirectorio(file); 
        file = null;
        btnA.setIcon(new ImageIcon(getClass().getResource("img/btnA.png")));
        btnA.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public void analizarDatosEnDirectorio(File file) {
        java.io.File [] vector = file.listFiles();
        int tipo;
        
        for(int i=0; i<vector.length ; i++) 
            if(vector[i].isDirectory())
                this.analizarDatosEnDirectorio(vector[i]);
            else {
                tipo = this.calcularTipo(vector[i].getName());
                if(tipo != 7) {
                    FileInputStream input = null;
                    byte[] data=null;
                    
                    try {
                        input = new FileInputStream(vector[i]);
                    try {
                        data = new byte[(int)vector[i].length()];
                        input.read(data);
                        nodoRecurso nuevo = new nodoRecurso(vector[i].getName().toString(), tipo, true, (int) vector[i].length(), frame, null);
                        nuevo.setData(data);
                        if(listaRecurso.add(nuevo)) {
                            recalcularScroll();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) { Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);}
                        }
                        
                        if(this.listaRecurso.getCab() == null)
                            msjLocal.setVisible(true);
                        else
                            msjLocal.setVisible(false);
                        input.close();
                    } catch (IOException ex) {System.out.println("Error");}
                    } catch (FileNotFoundException ex) { System.out.println("Error");}
                 }
            }  
    }
    
    public int calcularTipo(String nombre){
        String aux, aux2;
        StringTokenizer tokens = new StringTokenizer(nombre, ".");
        aux = tokens.nextToken();
        aux2 = tokens.nextToken();
    
        if(aux2.compareTo("exe")!=0)
        if(aux2.compareTo("mp3") == 0 || aux2.compareTo("au") == 0 || aux2.compareTo("avi") == 0 || aux2.compareTo("midi") == 0 || aux2.compareTo("mpeg") == 0 || aux2.compareTo("wav") == 0)
            return 0;
        else {
            if(aux2.compareTo("png") == 0 || aux2.compareTo("gif") == 0 || aux2.compareTo("jpg") == 0)
                return 1;
            else
                return 2;
        }
        else
            return 7;
    }

    @Override
    public void run() {
        inicializarServer();
    }
    
    public void enviarUpdate(int pos) {
        listaClienteServidor.update(pos);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
       if(e.getSource() == panelSuperior )
            frame.setBounds(e.getXOnScreen()-50, e.getYOnScreen()-30, 720, 550);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!analizandoDatos)
            if(e.getSource() == panelScroll && (listaRecurso.getTam() - 200 > 390))
                if(e.getY() >= 0 && e.getY() <= 390 - tamScroll + 5) {
                    scrollY = e.getY();
                    double proporcion = (double) scrollY / (double) (390.0 - tamScroll + 5);
                    escalar = (int) (proporcion * (listaRecurso.getJ() - 250));
                    escalar = escalar * -1;
                    scroll.setBounds(0, scrollY, 30, tamScroll);
                    listaRecurso.setY(escalar);
                    contenidoEspecial.setBounds(0, listaRecurso.getY(), 650, listaRecurso.getJ()+200);
                }
    }
}

