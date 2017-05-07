package transfertcp;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author JoseCaceres
 */
public class PanelDescargado extends JPanel{
    private ImageIcon img;
    private int ancho, alto;
    private double cc;
    
    public PanelDescargado() {
        setBounds(0,0,400,400);
    }
    
    public void setImage(byte[] data){
        img = new ImageIcon(data);
        ancho = img.getIconWidth();
        alto = img.getIconHeight();
        cc = (double)(((400) / ancho) * alto);   
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.drawImage(img.getImage(), 0, 0, 400, 400, this);    
    }
}
