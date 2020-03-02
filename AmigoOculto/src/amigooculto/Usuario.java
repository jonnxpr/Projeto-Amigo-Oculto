package amigooculto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Jonathan
 */
public class Usuario {

    //Atributos
    private int idUsuario;
    private String nome;
    private String apelido;
    private String email;
    private String senha;
    private String telefone;
    
    
    public Usuario(){
        
    }
    
    public Usuario(String nome, String apelido, String email, String senha, String telefone) {
        this.nome = nome;
        this.apelido = apelido;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }
    
    //Getter e Setter

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    //Métodos auxiliares
    public String chaveSecundaria() {
        return this.email;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);
        
        saida.writeInt(this.idUsuario);
        saida.writeUTF(this.nome);
        saida.writeUTF(this.apelido);
        saida.writeUTF(this.email);
        saida.writeUTF(this.senha);
        saida.writeUTF(this.telefone);
       
        return dados.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);
        
        this.idUsuario = entrada.readInt();
        this.nome = entrada.readUTF();
        this.apelido = entrada.readUTF();
        this.email = entrada.readUTF();
        this.senha = entrada.readUTF();
        this.telefone = entrada.readUTF();
    }
    
    @Override
    public String toString(){
        String userString = "Nome: " + this.nome;
        userString += "\nApelido: " + this.apelido;
        userString += "\nEmail: " + this.email;
        userString += "\nTelefone: " + this.telefone;        
        return userString;
    }
}
