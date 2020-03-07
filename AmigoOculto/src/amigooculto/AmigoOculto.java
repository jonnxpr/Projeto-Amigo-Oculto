package amigooculto;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan
 */
public class AmigoOculto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Interface i = new Interface("AMIGO OCULTO", "1.0");
        try {
            i.loopExec();
        } catch (Exception ex) {
            Logger.getLogger(AmigoOculto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
