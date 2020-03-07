package amigooculto;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Jonathan
 */
public class CRUD {

    public final String diretorio = "dados";

    public RandomAccessFile arquivo;
    public HashExtensivel indiceDireto;
    public ArvoreBMais_String_Int indiceIndireto;

    public CRUD(String nomeArquivo) throws Exception {

        File d = new File(this.diretorio);
        if (!d.exists()) {
            d.mkdir();
        }

        arquivo = new RandomAccessFile(this.diretorio + "/" + nomeArquivo + ".db", "rw");
        if (arquivo.length() < 4) {
            arquivo.writeInt(0);  // cabeçalho do arquivo
        }
        indiceDireto = new HashExtensivel(10,
                this.diretorio + "/diretorio." + nomeArquivo + ".idx",
                this.diretorio + "/cestos." + nomeArquivo + ".idx");

        indiceIndireto = new ArvoreBMais_String_Int(10,
                this.diretorio + "/arvoreB." + nomeArquivo + ".idx");
    }
    
    //Métodos de utilização do CRUD
    
    public int create(Usuario novoUsuario) throws IOException, Exception{
        arquivo.seek(0);
        int ultimoId = arquivo.readInt();
        ultimoId++;
        arquivo.seek(0);
        arquivo.writeInt(ultimoId);
        
        novoUsuario.setIdUsuario(ultimoId);
        long enderecoInsercao = arquivo.length();
        arquivo.seek(enderecoInsercao);
        arquivo.writeBoolean(true);
        arquivo.writeInt(novoUsuario.toByteArray().length);
        arquivo.write(novoUsuario.toByteArray());
 
        indiceDireto.create(ultimoId, enderecoInsercao);
        indiceIndireto.create(novoUsuario.chaveSecundaria(), ultimoId);
        
        return novoUsuario.getIdUsuario();
    }
    
    public Usuario read(int idUsuario) throws Exception{
        long endereco = indiceDireto.read(idUsuario);
        //System.out.println("EnderecoREADID = " + endereco);
        
        if (endereco == -1){
            return null;
        }
        
        arquivo.seek(endereco);
        boolean lapide = arquivo.readBoolean();
        
        if (!lapide){
            return null;
        }
        
        int tamanho = arquivo.readInt();
        byte[] array = new byte[tamanho];
        
        arquivo.read(array);
        Usuario usuario = new Usuario();
        usuario.fromByteArray(array);
        usuario.setIdUsuario(idUsuario);
        
        return usuario;
    }
    
    public Usuario read(String email) throws IOException, Exception{
        int id = indiceIndireto.read(email);
        return read(id);
    }
    
    public boolean update(Usuario usuarioNovo) throws Exception{
        Usuario usuarioAntigo = read(usuarioNovo.getIdUsuario());
        long endereco = indiceDireto.read(usuarioAntigo.getIdUsuario());
        arquivo.seek(endereco+5);
        int tamanhoAtual = usuarioNovo.toByteArray().length;
        int tamanhoAnterior = usuarioAntigo.toByteArray().length;
        
        if (tamanhoAtual <= tamanhoAnterior){
            arquivo.write(usuarioNovo.toByteArray());
        } else {
            arquivo.seek(endereco);
            arquivo.writeBoolean(false);
            long enderecoInsercao = arquivo.length();
            arquivo.seek(enderecoInsercao);
            byte[] array = usuarioNovo.toByteArray();
            arquivo.writeBoolean(true);
            arquivo.writeInt(array.length);
            arquivo.write(array);
            indiceDireto.update(usuarioNovo.getIdUsuario(), enderecoInsercao);
        }
        
        if (!usuarioAntigo.chaveSecundaria().equals(usuarioNovo.chaveSecundaria())){
            indiceIndireto.delete(usuarioAntigo.chaveSecundaria());
            indiceIndireto.create(usuarioNovo.chaveSecundaria(), usuarioNovo.getIdUsuario());
        }
        
        return true;
    }
    
    public boolean delete(int idUsuario) throws Exception{
        Usuario usuario = read(idUsuario);
        long enderecoDelecao = indiceDireto.read(idUsuario);
        arquivo.seek(enderecoDelecao);
        arquivo.writeBoolean(false);
        indiceIndireto.delete(usuario.chaveSecundaria());
        indiceDireto.delete(idUsuario);
        return true;
    }
}
