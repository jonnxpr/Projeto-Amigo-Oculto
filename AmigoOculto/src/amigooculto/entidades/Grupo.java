package amigooculto.entidades;

import amigooculto.interfaces.Registro;
import java.io.IOException;

/**
 *
 * @author Jonathan
 */
public class Grupo implements Registro{
    private int idGrupo;
    private int idUsuario;
    private String nome;
    long momentoSorteio;
    float valor;
    long momentoEncontro;
    String localEncontro;
    String observacoes;
    boolean sorteado;
    boolean ativo;
    
    public Grupo(){
        this.idGrupo = -1;
        this.idUsuario = -1;
        this.nome = "";
        this.momentoSorteio = -1;
        this.valor = -1;
        this.momentoEncontro = -1;
        this.localEncontro = "";
        this.observacoes = "";
        this.sorteado = false;
        this.ativo = true;
    }

    public Grupo(String nome, long momentoSorteio, float valor, long momentoEncontro, String localEncontro, String observacoes) {
        this.nome = nome;
        this.momentoSorteio = momentoSorteio;
        this.valor = valor;
        this.momentoEncontro = momentoEncontro;
        this.localEncontro = localEncontro;
        this.observacoes = observacoes;
        this.sorteado = false;
        this.ativo = true;
    }

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

    public long getMomentoSorteio() {
        return momentoSorteio;
    }

    public void setMomentoSorteio(long momentoSorteio) {
        this.momentoSorteio = momentoSorteio;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public long getMomentoEncontro() {
        return momentoEncontro;
    }

    public void setMomentoEncontro(long momentoEncontro) {
        this.momentoEncontro = momentoEncontro;
    }

    public String getLocalEncontro() {
        return localEncontro;
    }

    public void setLocalEncontro(String localEncontro) {
        this.localEncontro = localEncontro;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isSorteado() {
        return sorteado;
    }

    public void setSorteado(boolean sorteado) {
        this.sorteado = sorteado;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    
   
    @Override
    public int getId() {
        return this.idGrupo;
    }

    @Override
    public void setId(int id) {
        this.idGrupo = id;
    }

    @Override
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.nome;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        return null;
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        
    }
    
}
