/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amigooculto.interfaces;

import java.io.IOException;

/**
 *
 * @author Jonathan
 */
public interface Registro {

    public int getId();

    public void setId(int id);

    public String chaveSecundaria();

    public byte[] toByteArray() throws IOException;

    public void fromByteArray(byte[] bytes) throws IOException;
}
