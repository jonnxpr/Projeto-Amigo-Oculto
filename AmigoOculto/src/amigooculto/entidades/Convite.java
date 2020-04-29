/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amigooculto.entidades;

import amigooculto.interfaces.Registro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Jonathan
 */
public class Convite implements Registro {

    private int idConvite;
    private int idGrupo;
    private String email;
    private long momentoConvite;
    private byte estado;

    public Convite() {
        this.idConvite = -1;
        this.idGrupo = -1;
        this.email = "";
        this.momentoConvite = -1;
        this.estado = 0;
    }

    public Convite(String email, long momentoConvite, byte estado) {
        this.idConvite = -1;
        this.idGrupo = -1;
        this.email = email;
        this.momentoConvite = momentoConvite;
        this.estado = estado;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getMomentoConvite() {
        return momentoConvite;
    }

    public void setMomentoConvite(long momentoConvite) {
        this.momentoConvite = momentoConvite;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    @Override
    public int getId() {
        return this.idConvite;
    }

    @Override
    public void setId(int id) {
        this.idConvite = id;
    }

    @Override
    public String chaveSecundaria() {
        return this.idGrupo + "|" + this.email;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idConvite);
        saida.writeInt(this.idGrupo);
        saida.writeUTF(this.email);
        saida.writeLong(this.momentoConvite);
        saida.writeByte(this.estado);

        return dados.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
        DataInputStream entrada = new DataInputStream(dados);

        this.idConvite = entrada.readInt();
        this.idGrupo = entrada.readInt();
        this.email = entrada.readUTF();
        this.momentoConvite = entrada.readLong();
        this.estado = entrada.readByte();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm");

        String conviteString = this.email;
        conviteString += " (" + formatter.print(momentoConvite) + " - " + decodificarEstado() + ")";

        return conviteString;
    }

    private String decodificarEstado() {
        String estado;
        switch (this.estado) {
            case 0:
                estado = "pendente";
                break;
            case 1:
                estado = "aceito";
                break;
            case 2:
                estado = "recusado";
                break;
            case 3:
                estado = "cancelado";
                break;
            default:
                estado = "Não identificado";
                break;
        }
        return estado;
    }
}
