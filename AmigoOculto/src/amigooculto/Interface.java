package amigooculto;

import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan
 */
public class Interface {
    private String nomeAplicacao;
    private String versaoAplicacao;
    private CRUD crud;
    private final Scanner in;
    private final HashMap<Short, String> opcoesMenuInicial;
    
    public Interface(String nome, String versao){
        this.nomeAplicacao = nome;
        this.versaoAplicacao = versao;
        
        try {
            crud = new CRUD("BancoDeDados");
        } catch (Exception ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        in = new Scanner(System.in);
        
        opcoesMenuInicial = new HashMap<>();
        opcoesMenuInicial.put((short)1, "Entrar");
        opcoesMenuInicial.put((short)2, "Criar novo usuário (primeiro acesso)");
        opcoesMenuInicial.put((short)3, "Recuperar senha");
        opcoesMenuInicial.put((short)4, "Sair");
    }
    
    public void showMenuInicial(){
        System.out.println("--------------------------");
        System.out.println(nomeAplicacao + " " + versaoAplicacao);
        System.out.println("Desenvolvido pelo Grupo 3");
        System.out.println("--------------------------");
        
        showOpcoes();
        
        procOption(getOption());
    }
    
    private void showOpcoes(){ 
        for (Short s : opcoesMenuInicial.keySet()){
            System.out.println(s + ")" + opcoesMenuInicial.get(s));
        }
    }
    
    private short getOption(){
        short option;
        System.out.print("Opção: ");
        option = in.nextShort();
        
        while(!opcoesMenuInicial.containsKey(option)){
            System.out.print("\nOpção inválida. Insira novamente: ");
            option = in.nextShort();
        }
        return option;
    }
    
    private void procOption(short option){
        switch(option){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                showMensagemSaida();
                break;
            default:
                System.out.println("Opção inexistente.");
        }
    }
    
    private void showMensagemSaida(){
        System.out.println("\nObrigado por utilizar esta aplicação! Volte sempre.");
    }
}
