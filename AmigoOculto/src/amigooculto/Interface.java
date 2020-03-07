package amigooculto;

import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jonathan
 */
public class Interface {

    private String nomeAplicacao;
    private String versaoAplicacao;
    private CRUD crud;
    private final Scanner in;
    private boolean isLogged;
    private Usuario usuario;
    private final HashMap<Short, String> opcoesMenuInicial;
    private final HashMap<Short, String> opcoesMenuPrincipal;

    public Interface(String nome, String versao) {
        this.nomeAplicacao = nome;
        this.versaoAplicacao = versao;
        this.isLogged = false;

        try {
            crud = new CRUD("BancoDeDados");
        } catch (Exception ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }

        in = new Scanner(System.in);

        opcoesMenuInicial = new HashMap<>();
        opcoesMenuInicial.put((short) 1, "Entrar");
        opcoesMenuInicial.put((short) 2, "Criar novo usuário (primeiro acesso)");
        opcoesMenuInicial.put((short) 3, "Recuperar senha");
        opcoesMenuInicial.put((short) 4, "Sair");

        opcoesMenuPrincipal = new HashMap<>();
        opcoesMenuPrincipal.put((short) 1, "Deslogar");
    }

    public void loopExec() throws Exception {
        boolean exec = true;
        do {
            if (!isLogged) {
                exec = showMenuInicial();
            } else {
                exec = showMenuPrincipal();
            }

            clearScreen();
        } while (exec);
    }

    private void showHeader() {
        System.out.println("--------------------------");
        System.out.println(nomeAplicacao + " " + versaoAplicacao);
        System.out.println("Desenvolvido pelo Grupo 3");
        System.out.println("--------------------------");
        if (isLogged){
            System.out.println("Você está logado como " + usuario.getNome());
            System.out.println("--------------------------");
        }
        
    }

    public boolean showMenuInicial() throws Exception {
        boolean exec = true;

        showHeader();

        showOpcoes((short) 1);

        exec = procOption(getOption((short) 1), (short) 1);

        return exec;
    }

    public boolean showMenuPrincipal() throws Exception {
        boolean exec = true;

        showHeader();

        showOpcoes((short) 2);

        procOption(getOption((short) 2), (short) 2);

        return exec;
    }

    /**
     * menu = 1 -> menu inicial menu = 2 -> menu principal
     *
     * @param menu
     */
    private void showOpcoes(short menu) {
        switch (menu) {
            case 1:
                for (Short s : opcoesMenuInicial.keySet()) {
                    System.out.println(s + ")" + opcoesMenuInicial.get(s));
                }
                break;
            case 2:
                for (Short s : opcoesMenuPrincipal.keySet()) {
                    System.out.println(s + ")" + opcoesMenuPrincipal.get(s));
                }
                break;
            default:
                System.out.println("ERRO: Não foi possível exibir o menu desejado.");
                break;
        }

    }

    /**
     * menu = 1 -> menu inicial menu = 2 -> menu principal
     *
     * @param menu
     * @return opcao escolhida
     */
    private short getOption(short menu) {
        short option;
        System.out.print("Opção: ");
        option = in.nextShort();

        switch (menu) {
            case 1:
                while (!opcoesMenuInicial.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case 2:
                while (!opcoesMenuPrincipal.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            default:
                System.out.println("ERRO: Opção inexistente.");
                break;
        }

        return option;
    }

    private boolean procOption(short option, short menu) throws Exception {

        if (menu == 1) {
            switch (option) {
                case 1:
                    usuario = procLogin();
                    return true;
                case 2:
                    procCadastro();
                    return true;
                case 3:
                    procRecSenha();
                    return true;
                case 4:
                    showMensagemSaida();
                    return false;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    return false;
            }
        } else if (menu == 2) {
            switch (option) {
                case 1:
                    setIsLogged(false);
                    return true;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        }

        return true;
    }

    private void showMensagemSaida() {
        System.out.println("\nObrigado por utilizar esta aplicação! Volte sempre.");
    }

    private Usuario procLogin() throws Exception {
        String email;
        String senha;
        Usuario usuario;

        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();

        if ((usuario = crud.read(email)) == null) {
            System.out.println("\nERRO: O email inserido é inválido.");
            pressToContinue();
            return null;
        }

        System.out.print("\nDigite sua senha: ");
        senha = in.nextLine();

        if (!usuario.getSenha().equals(senha)) {
            System.out.println("\nERRO: A senha inserida é inválida.");
            pressToContinue();
            return null;
        }

        System.out.println("\nLogin realizado com sucesso!\n\n\n");

        setIsLogged(true);
        
        return usuario;
    }

    private void procCadastro() throws Exception {
        Usuario usuario;
        String email;
        String nome;
        String apelido;
        String senha;
        String codRec;
        String confirma;

        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();

        if (crud.read(email) != null || !isValidEmailAddressRegex(email)) {
            System.out.println("\nERRO: O email inserido já pertence a outro usuário ou é inválido.");
            pressToContinue();
            return;
        }

        System.out.print("\nDigite seu nome: ");
        nome = in.nextLine();

        while (nome == null || nome.length() < 1) {
            System.out.println("ERRO: O nome inserido é inválido. Insira novamente.");
            System.out.print("\nDigite seu nome: ");
            nome = in.nextLine();
        }

        System.out.print("\nDigite seu apelido: ");
        apelido = in.nextLine();

        while (apelido == null || apelido.length() < 1) {
            System.out.println("ERRO: O apelido inserido é inválido. Insira novamente.");
            System.out.print("\nDigite seu apelido: ");
            apelido = in.nextLine();
        }

        System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
        senha = in.nextLine();

        while (senha == null || senha.length() < 5) {
            System.out.println("ERRO: A senha inserida é inválida. Insira novamente.");
            System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
            senha = in.nextLine();
        }

        System.out.print("\nDigite seu código de recuperação de senha (no mínimo 5 caracteres): ");
        codRec = in.nextLine();

        while (codRec == null || codRec.length() < 5) {
            System.out.println("ERRO: O código de recuperação é inválido. Insira novamente.");
            System.out.print("\nDigite seu código de recuperação de senha (no mínimo 5 caracteres): ");
            codRec = in.nextLine();
        }

        usuario = new Usuario(nome, apelido, email, senha, codRec);

        System.out.println("\nVocê confirma a criação de um usuário com estes dados? (Digite sim ou nao)\n");
        System.out.println(usuario.toString());

        System.out.print("\nConfirma: ");
        confirma = in.nextLine();

        while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
            System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
            System.out.print("Confirma: ");
            confirma = in.nextLine();
        }

        if (confirma.equals("sim")) {
            crud.create(usuario);
            System.out.println("\nUsuário cadastrado com sucesso.");
            pressToContinue();
        }
        if (confirma.equals("nao")) {
            System.out.println("\nCadastro de usuário abortado.");
            pressToContinue();
        }
    }

    private void procRecSenha() throws Exception {
        String email;
        Usuario usuario;
        String novaSenha;
        String codRec;

        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();

        if ((usuario = crud.read(email)) == null) {
            System.out.println("\nERRO: Não existe usuário cadastrado com este email.");
            pressToContinue();
        } else {
            System.out.print("\nDigite seu código de recuperação: ");
            codRec = in.nextLine();

            if (codRec.equals(usuario.getCodigoDeRecuperacao())) {
                System.out.print("\nDigite e nova senha (no mínimo 5 caracteres): ");
                novaSenha = in.nextLine();

                while (novaSenha == null || novaSenha.length() < 5) {
                    System.out.println("ERRO: A senha inserida é inválida. Insira novamente.");
                    System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
                    novaSenha = in.nextLine();
                }

                usuario.setSenha(novaSenha);
                crud.update(usuario);

                System.out.println("\nSenha atualizada com sucesso!");
            } else {
                System.out.println("\nERRO: Código de recuperação inválido.");
                pressToContinue();
            }

        }
    }

    public static boolean isValidEmailAddressRegex(String email) {
        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }

    public String getNomeAplicacao() {
        return nomeAplicacao;
    }

    public void setNomeAplicacao(String nomeAplicacao) {
        this.nomeAplicacao = nomeAplicacao;
    }

    public String getVersaoAplicacao() {
        return versaoAplicacao;
    }

    public void setVersaoAplicacao(String versaoAplicacao) {
        this.versaoAplicacao = versaoAplicacao;
    }

    public boolean isIsLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    private void clearScreen() {
        System.out.print("\n\n\n");
    }
    
    private void pressToContinue(){
        System.out.println("Pressione qualquer tecla para continuar...");
        in.nextLine();
    }
}
