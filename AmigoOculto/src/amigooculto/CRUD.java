package amigooculto;

import java.io.File;
import java.io.RandomAccessFile;

/**
 *
 * @author Jonathan
 */
public class CRUD {

    public final String diretório = "dados";

    public RandomAccessFile arquivo;
    public HashExtensivel índiceDireto;
    public ArvoreBMais_String_Int índiceIndireto;

    public CRUD(String nomeArquivo) throws Exception {

        File d = new File(this.diretório);
        if (!d.exists()) {
            d.mkdir();
        }

        arquivo = new RandomAccessFile(this.diretório + "/" + nomeArquivo + ".db", "rw");
        if (arquivo.length() < 4) {
            arquivo.writeInt(0);  // cabeçalho do arquivo
        }
        índiceDireto = new HashExtensivel(10,
                this.diretório + "/diretorio." + nomeArquivo + ".idx",
                this.diretório + "/cestos." + nomeArquivo + ".idx");

        índiceIndireto = new ArvoreBMais_String_Int(10,
                this.diretório + "/arvoreB." + nomeArquivo + ".idx");
    }
    
    //Métodos de utilização do CRUD
    
    public int create(Usuario novoUsuario){
        
        return novoUsuario.getIdUsuario();
    }
    
    public Usuario read(int idUsuario){
        return null;
    }
    
    public boolean update(Usuario usuario){
        return true;
    }
    
    public boolean delete(int idUsuario){
        return true;
    }
}
