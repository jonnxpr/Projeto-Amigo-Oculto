package amigooculto;

import amigooculto.hud.Interface;
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
        /*String myString = "22/05/1995 12:00";
        String myString2 = "22/05/1995 12:01";
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm");

        DateTimeZone timeZone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime dateTime = formatter.withZone(timeZone).parseDateTime(myString);

        long millisecondsSinceEpoch = dateTime.getMillis();
        System.out.println("tempo em mili = " + millisecondsSinceEpoch);
        
        dateTime = formatter.withZone(timeZone).parseDateTime(myString2);
        millisecondsSinceEpoch = dateTime.getMillis();
        System.out.println("tempo em mili = " + millisecondsSinceEpoch);*/
    }

}
