package amigooculto.bancoDeDados;

import amigooculto.entidades.Sugestao;
import amigooculto.hud.Interface;
import amigooculto.indices.ArvoreBMais_Int_Int;
import amigooculto.interfaces.Registro;
import amigooculto.indices.ArvoreBMais_String_Int;
import amigooculto.indices.HashExtensivel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

/**
 *
 * @author Jonathan
 * @param <T>
 */
public class CRUD<T extends Registro> {

    Constructor<T> construtor;
    public final String diretorio = "dados";

    private RandomAccessFile arquivo;
    private HashExtensivel indiceDireto;
    private ArvoreBMais_String_Int indiceIndireto;
    private ArvoreBMais_Int_Int indiceRelacionamento;

    public CRUD(String nomeArquivo, Constructor<T> construtor) throws Exception {
        this.construtor = construtor;

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
                this.diretorio + "/indiceIndireto." + nomeArquivo + ".idx");
        
        if (construtor.equals(Sugestao.class.getConstructor())) {
            indiceRelacionamento = new ArvoreBMais_Int_Int(10,
                this.diretorio + "/indiceRelacionamento." + nomeArquivo + ".idx");
        }
        
    }
    
    public ArvoreBMais_Int_Int getIndiceRelacionamento(){
        return this.indiceRelacionamento;
    }

    //Métodos de utilização do CRUD
    public int create(T novaInstancia) throws IOException, Exception {
        arquivo.seek(0);
        int ultimoId = arquivo.readInt();
        ultimoId++;
        arquivo.seek(0);
        arquivo.writeInt(ultimoId);

        novaInstancia.setId(ultimoId);
        long enderecoInsercao = arquivo.length();
        arquivo.seek(enderecoInsercao);
        arquivo.writeBoolean(true);
        arquivo.writeInt(novaInstancia.toByteArray().length);
        arquivo.write(novaInstancia.toByteArray());

        indiceDireto.create(ultimoId, enderecoInsercao);
        indiceIndireto.create(novaInstancia.chaveSecundaria(), ultimoId);

        if (novaInstancia.getClass().equals(Sugestao.class)) {
            indiceRelacionamento.create(Interface.usuario.getId(), ultimoId);
        }

        return novaInstancia.getId();
    }

    public T read(int id) throws Exception {
        long endereco = indiceDireto.read(id);
        //System.out.println("EnderecoREADID = " + endereco);

        if (endereco == -1) {
            return null;
        }

        arquivo.seek(endereco);
        boolean lapide = arquivo.readBoolean();

        if (!lapide) {
            return null;
        }

        int tamanho = arquivo.readInt();
        byte[] array = new byte[tamanho];

        arquivo.read(array);
        T instancia = this.construtor.newInstance();
        instancia.fromByteArray(array);
        instancia.setId(id);

        return instancia;
    }

    public T read(String email) throws IOException, Exception {
        int id = indiceIndireto.read(email);
        return read(id);
    }

    public boolean update(T novaInstancia) throws Exception {
        T instanciaAntiga = read(novaInstancia.getId());
        long endereco = indiceDireto.read(instanciaAntiga.getId());
        arquivo.seek(endereco + 5);
        int tamanhoAtual = novaInstancia.toByteArray().length;
        int tamanhoAnterior = instanciaAntiga.toByteArray().length;

        if (tamanhoAtual <= tamanhoAnterior) {
            arquivo.write(novaInstancia.toByteArray());
        } else {
            arquivo.seek(endereco);
            arquivo.writeBoolean(false);
            long enderecoInsercao = arquivo.length();
            arquivo.seek(enderecoInsercao);
            byte[] array = novaInstancia.toByteArray();
            arquivo.writeBoolean(true);
            arquivo.writeInt(array.length);
            arquivo.write(array);
            indiceDireto.update(novaInstancia.getId(), enderecoInsercao);
        }

        if (!instanciaAntiga.chaveSecundaria().equals(novaInstancia.chaveSecundaria())) {
            indiceIndireto.delete(instanciaAntiga.chaveSecundaria());
            indiceIndireto.create(novaInstancia.chaveSecundaria(), novaInstancia.getId());
        }

        return true;
    }

    public boolean delete(int id) throws Exception {
        T instancia = read(id);
        long enderecoDelecao = indiceDireto.read(id);
        arquivo.seek(enderecoDelecao);
        arquivo.writeBoolean(false);
        indiceIndireto.delete(instancia.chaveSecundaria());
        indiceDireto.delete(id);
        
        if (instancia.getClass().equals(Sugestao.class)) {
            indiceRelacionamento.delete(Interface.usuario.getId(), id);
        }
        return true;
    }
}
