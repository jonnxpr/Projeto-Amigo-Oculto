package amigooculto.bancoDeDados;

//Importações
import amigooculto.indices.ArvoreBMais_String_Int;
import amigooculto.indices.HashExtensivel;
import amigooculto.interfaces.Registro;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

/**
 * Classe CRUD
 * Feita de forma genêrica
 * @author Jonathan
 * @param <T>
 */
public class CRUD<T extends Registro> {
    
    //Atributos da classe
    Constructor<T> construtor;
    //nome do diretorio onde serão armazenados os dados
    public final String diretorio = "dados";
    
    //variável de acesso aos arquivos permitindo escrita e leitura
    private final RandomAccessFile arquivo;
    //Índices
    private final HashExtensivel indiceDireto;
    private final ArvoreBMais_String_Int indiceIndireto;
    
    
    /**
     * Construtor da Classe CRUD
     * @param nomeArquivo
     * @param construtor
     * @throws Exception 
     */
    public CRUD(String nomeArquivo, Constructor<T> construtor) throws Exception {
        this.construtor = construtor; //inicia o construtor genêrico
        
        //abre o arquivo no diretorio especificado
        File d = new File(this.diretorio);
        //verifica se já existe o diretório, senão cria
        if (!d.exists()) {
            d.mkdir();
        }
        
        //abre o arquivo em modo leitura e escrita
        arquivo = new RandomAccessFile(this.diretorio + "/" + nomeArquivo + ".db", "rw");
        
        //caso o tamanho do arquivo seja inferior a 4 bytes escreve o cabeçalho do arquivo com
        //um inteiro de valor 0
        if (arquivo.length() < 4) {
            arquivo.writeInt(0);  // cabeçalho do arquivo
        }
        
        //inicializa os índices
        indiceDireto = new HashExtensivel(10,
                this.diretorio + "/diretorio." + nomeArquivo + ".idx",
                this.diretorio + "/cestos." + nomeArquivo + ".idx");

        indiceIndireto = new ArvoreBMais_String_Int(10,
                this.diretorio + "/indiceIndireto." + nomeArquivo + ".idx");
    }

    //Métodos de utilização do CRUD
    public int create(T novaInstancia) throws IOException, Exception {
        arquivo.seek(0); //posiciona o ponteiro no inicio do arquivo
        int ultimoId = arquivo.readInt(); //obtem o ultimo id gravado
        ultimoId++; //incrementa o id
        arquivo.seek(0); //reposiciona o ponteiro no inicio
        arquivo.writeInt(ultimoId); //grava o ultimo id registrado

        novaInstancia.setId(ultimoId); //cria nova instância com o valor do último id registrado
        long enderecoInsercao = arquivo.length(); //marca o endereço de inserção
        arquivo.seek(enderecoInsercao); //posiciona o ponteiro no endereço de inserção
        arquivo.writeBoolean(true); //escreve a lápide
        arquivo.writeInt(novaInstancia.toByteArray().length); //escreve o indicador de tamanho do registro
        arquivo.write(novaInstancia.toByteArray()); //escreve os dados do registro

        indiceDireto.create(ultimoId, enderecoInsercao); //registra no índice direto a posição em que se encontra o registro adicioando
        indiceIndireto.create(novaInstancia.chaveSecundaria(), ultimoId); //registra no índice indireto 

        return novaInstancia.getId(); //retorna o id do registro no arquivo
    }

    public T read(int id) throws Exception {
        long endereco = indiceDireto.read(id); //obtém o endereço do registro no arquivo
        //System.out.println("EnderecoREADID = " + endereco);
        
        //verifica a não existência desse endereço
        if (endereco == -1) {
            return null;
        }

        arquivo.seek(endereco); //posiciona o ponteiro no endereço para leitura
        boolean lapide = arquivo.readBoolean(); //lê a lápide
        
        //verifica se a lápide é válida
        if (!lapide) {
            return null;
        }
        
        //obtem o indicador de tamanho do registro
        int tamanho = arquivo.readInt();
        byte[] array = new byte[tamanho];

        arquivo.read(array); //lê o array bytes do arquivo (dados do registro)
        T instancia = this.construtor.newInstance(); //cria de forma genêrica o objeto
        instancia.fromByteArray(array); //introduz os dados no objeto
        instancia.setId(id); //seta o id 
        
        //retorna a instância recuperada
        return instancia;
    }
    
    /**
     * Faz a mesma coisa do read acima só que utiliza a chave secundaria para obter
     * o id que será utilizado para realizar a leitura
     * @param email
     * @return
     * @throws IOException
     * @throws Exception 
     */
    public T read(String email) throws IOException, Exception {
        int id = indiceIndireto.read(email);
        return read(id);
    }

    public boolean update(T novaInstancia) throws Exception {
        T instanciaAntiga = read(novaInstancia.getId()); //recupera a instância antiga a partir de seu id
        long endereco = indiceDireto.read(instanciaAntiga.getId()); //obtém o endereço do registro no arquivo
        arquivo.seek(endereco + 5); //pula o cabeçalho
        int tamanhoAtual = novaInstancia.toByteArray().length; //tamanho do novo registro
        int tamanhoAnterior = instanciaAntiga.toByteArray().length; //tamanho anterior do registro
        
        //trecho de verificação da necessidade de reescrever o registro numa nova posição
        //caso tenha aumentado de tamanho e o escreve na mesma posição caso tamanho seja
        //menor ou igual
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
        
        //atualiza o índice indireto em caso de mudança
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
        arquivo.writeBoolean(false); //seta a lápide como falso invalidando o registro
        //faz as deleções necessárias nos índices
        indiceIndireto.delete(instancia.chaveSecundaria());
        indiceDireto.delete(id);

        return true;
    }

    //getter e setter
    public String getDiretorio() {
        return diretorio;
    }
}
