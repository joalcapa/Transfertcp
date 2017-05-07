package transfertcp;

import java.io.DataOutputStream;

/**
 *
 * @author JoseCaceres
 */
public class nodoUpdate {
    private int tipo, pos;
    private nodoUpdate suc;
    private ListaRecurso listaRecurso;

    public nodoUpdate(int tipo, int pos, ListaRecurso listaRecurso){
        this.tipo = tipo;
        this.suc = null;
        this.listaRecurso = listaRecurso;
        this.pos = pos;
    }
    
    public nodoUpdate getSuc() {
        return suc;
    }
    
    public void setSuc(nodoUpdate suc) {
        this.suc = suc;
    }
    
    public void actualizar(DataOutputStream salida) {
        if(tipo == 1)
            listaRecurso.actualizarTodo(salida, tipo);
        else
            listaRecurso.actualizarUno(salida, pos, tipo);
    }
}

