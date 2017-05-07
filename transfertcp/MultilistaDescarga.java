package transfertcp;

/**
 *
 * @author JoseCaceres
 */
public class MultilistaDescarga {
    private ListaDescarga cab, recor, fin;
    
    public MultilistaDescarga() {
        cab = fin = null;
    }
    
    public void add(ListaDescarga nuevo){
      if(cab==null)
          cab= fin = nuevo;      
      else{
          recor=cab;
          while(recor != null)
              if(recor.getSuc()==null)
                  break;
              else
                  recor= recor.getSuc();
          recor.setSuc(nuevo); 
          fin = nuevo;
      }     
    }
    
    public synchronized void tratarMaster(boolean estado) {
        ListaDescarga recor2 = cab;
        while(recor2!= null) {
            if(recor2.getMasterG() != null)
            recor2.getMasterG().setActivo(estado);
            recor2 = recor2.getSuc();
        }
    }

    public ListaDescarga getFin() {
        return fin;
    }
    
    public  void eliminarDescargas () {
        recor = cab;
        while(recor != null){
            if(recor.getDescargado() || recor.isInterrupcion())
                break;
            else    
          recor=recor.getSuc();
        }
        
        if(recor!= null) {
            if(recor.getPre()==null)
                cab=recor.getSuc();
            else
                recor.getPre().setSuc(recor.getSuc());
        
            if(recor.getSuc()==null)
                fin=recor.getPre();
            else
                recor.getSuc().setPre(recor.getPre());

            recor=null;
            }
    }
}

