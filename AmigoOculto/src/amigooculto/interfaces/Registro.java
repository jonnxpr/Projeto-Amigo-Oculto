
package amigooculto.interfaces;

//Importações
import java.io.IOException;

/**
 * Interface Registro
 * Contém métodos interessantes para a criação de objetos genêricos e que eventualmente
 * facilitarão a escrita e leitura dos dados dos objetos no arquivo
 * @author Jonathan
 */
public interface Registro {

    public int getId();

    public void setId(int id);

    public String chaveSecundaria();

    public byte[] toByteArray() throws IOException;

    public void fromByteArray(byte[] bytes) throws IOException;
}
