package transfertcp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author JoseCaceres
 */
public class View extends JPanel{
    private ListaDescarga listaDescarga;
    private int cont, contV;
    private double estado;
    private boolean relucir;
    
    public View(ListaDescarga listaDescarga){
        this.listaDescarga = listaDescarga;
        cont = 60;
        estado = 0f;
        contV = 0;
        relucir = true;
        setBackground(Color.BLACK);
    }
    
    public void scene2D(){
        nodoDescarga recor = listaDescarga.getCab();
        int j = 0, rect = 40;
        while(recor != null){
            recor = recor.getSuc();
            j = j + 65;
        }
        setBounds(0,40,620,j);   
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        
        if(relucir) {
            cont = cont + 5;
            if(cont == 260) {
                relucir = false;
                cont = 255;
            }
        } else {
            cont = cont - 5;
            if(cont == 55) {
                relucir = true;
                cont = 60;
            }
        }
        
        int ii = 1;
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        nodoDescarga recor = listaDescarga.getCab();
        int j = 18, rect = 25;
        while(recor != null) {
            int conAn = recor.getConAn();
            int contador = recor.getContador2();
            int tam = recor.getTamBytes();
            g.setColor(Color.white);
            double vv = (double) ( (double)(contador - conAn) / 200);
            recor.setContadorAnterior(contador);
            
            if(!recor.isDescargado()) {
                if(contV == 0) 
                    estado = vv;
            
                contV++;
                if(contV == 3)
                    contV = 0;
             
                g.drawString("      -  particion " + Integer.toString(ii) + "      " + Double.toString(estado) + " Kb/s", 0, j );
            } else
                g.drawString("      -  particion " + Integer.toString(ii), 0, j );
            
            if(listaDescarga.getTipo() == 0)
                g.setColor(new Color(0,37,46));
            
            if(listaDescarga.getTipo() == 2)
                g.setColor(new Color(41,17,0));
            
            if(listaDescarga.getTipo() == 1)
                g.setColor(new Color(0,31,0));
            
            for(int i=rect; i<rect+ 40; i++)
                g.drawLine(10, i, 610, i);
            
            if(listaDescarga.getTipo() == 0)
                g.setColor(new Color(0,cont-10,cont));
            if(listaDescarga.getTipo() == 2)
                g.setColor(new Color(cont,0,0));
            if(listaDescarga.getTipo() == 1)
                g.setColor(new Color(0,cont,0));
            
            String m = Integer.toString(contador);
            String mm = Integer.toString(tam);
            
            float porcentaje = 0;
            
            if(!recor.isDescargado())
                porcentaje = (float) ((Float.parseFloat(m)) / (Float.parseFloat(mm)));
            else
                porcentaje = 1f;
            
            float colorear = (float) (porcentaje * (605 - 15));
            for(int i=rect+5; i<rect+ 35; i++)
                g.drawLine(15, i,(int) colorear + 15, i);
            
            rect = rect + 65;
            j = j + 65;
            ii++;
            recor = recor.getSuc();
        }
        setBounds(0,40,620,j);   
    }
}


