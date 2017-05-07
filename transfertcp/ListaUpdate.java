package transfertcp;

import java.io.DataOutputStream;

/**
 *
 * @author JoseCaceres
 */
public class ListaUpdate {
    private nodoUpdate cab, recor, eliminar;
    
    public ListaUpdate() {
        this.cab = null;
    }
    
    public void add(nodoUpdate dato) {
        if(cab == null)
            cab = dato;
        else {
            recor = cab;
            while(true) {
                if(recor.getSuc() != null)
                    recor = recor.getSuc();
                else
                    break;
            }
            recor.setSuc(dato);
        }
    }
    
    public nodoUpdate getCab() {
        return cab;
    }
    
    public void ejecutar(DataOutputStream salida) {
        cab.actualizar(salida);
        eliminar = cab;
        cab = cab.getSuc();
        eliminar = null;
    }
}

