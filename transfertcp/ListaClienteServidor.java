package transfertcp;

/**
 *
 * @author JoseCaceres
 */
public class ListaClienteServidor {
    private nodoClienteServidor cab, recor;

    public ListaClienteServidor(){
        cab = null;    
    }
    
    public void add(nodoClienteServidor dato){
        if(cab == null)
            cab = dato;
        else {
            recor = cab;
            while(recor.getSuc() != null){
                recor = recor.getSuc();
            }
            recor.setSuc(dato);
        }
    }
    
    public synchronized void eliminarDescarga(String nombre) {
      recor = cab;
      nodoClienteServidor eliminar;
      while(recor != null){
          if(recor.getTipo().compareTo("DD") == 0) {
              if(recor.getNombre().compareTo(nombre) == 0) {
                  recor.setInterrumpir(true);
                  eliminar = recor; 
              }
          }
           recor = recor.getSuc();
           eliminar = null;
      }
    }
    
    public synchronized void cerrarConexiones(){
        if(cab != null){
            recor = cab;
            while(recor != null){
                recor.cerrarConexion();
                recor = recor.getSuc();
            }
        }
    }
    
    public synchronized void eliminarDescargados(){
        recor = cab;
        nodoClienteServidor eliminar= null;
        while(recor != null){
            if(recor.isDescargado()) {
                eliminar = recor;
               
            }
            
            if(recor == cab && eliminar == recor) {
                cab = recor.getSuc();
            }
            
            recor = recor.getSuc();
            
            
            eliminar = null;
        }
    }

    public void update(int pos) {
        if(cab != null){
            recor = cab;
            while(recor != null){
                if(recor.getTipo() != null) {
                    if(recor.getTipo().compareTo("AA") == 0)
                    recor.update(pos);
                }
                recor = recor.getSuc();
            }
        }
    }
}


