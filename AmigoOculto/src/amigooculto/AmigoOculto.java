package amigooculto;

//Importações
import amigooculto.hud.Interface;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trabalho Amigo Oculto
 * Versão final
 * Disciplina: AEDS 3
 * @author Jonathan Douglas Diego Tavares
 */
public class AmigoOculto {

    public static void main(String[] args) {
        //Instância um objeto do tipo Interface que ira gerenciar o loop principal de execução
        Interface i = new Interface("AMIGO OCULTO", "1.0");
        try {
            //inicia o loop de execução
            i.loopExec();
        } catch (Exception ex) {
            Logger.getLogger(AmigoOculto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
