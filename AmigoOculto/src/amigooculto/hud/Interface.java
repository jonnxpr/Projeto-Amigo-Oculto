package amigooculto.hud;

import amigooculto.bancoDeDados.CRUD;
import amigooculto.entidades.Sugestao;
import amigooculto.entidades.Usuario;
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
    private CRUD<Usuario> crudUsuario;
    private CRUD<Sugestao> crudSugestao;
    private final Scanner in;
    private boolean isLogged;
    public static Usuario usuario;
    private final HashMap<Short, String> opcoesMenuInicial;
    private final HashMap<Short, String> opcoesMenuPrincipal;
    private final HashMap<Short, String> opcoesMenuSugestoes;
    Tela ultimaTela;
    
    public enum Tela {
        MENU_INICIAL, MENU_PRINCIPAL, MENU_SUGESTOES;
    }

    public Interface(String nome, String versao) {
        this.nomeAplicacao = nome;
        this.versaoAplicacao = versao;
        this.isLogged = false;
        ultimaTela = Tela.MENU_INICIAL;
        
        try {
            crudUsuario = new CRUD("BDUsuario", Usuario.class.getConstructor());
            crudSugestao = new CRUD("BDSugestao", Sugestao.class.getConstructor());
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
        opcoesMenuPrincipal.put((short) 1, "Sugestão de presentes");
        opcoesMenuPrincipal.put((short) 2, "Grupos");
        opcoesMenuPrincipal.put((short) 3, "Deslogar");

        opcoesMenuSugestoes = new HashMap<>();
        opcoesMenuSugestoes.put((short) 1, "Listar");
        opcoesMenuSugestoes.put((short) 2, "Incluir");
        opcoesMenuSugestoes.put((short) 3, "Alterar");
        opcoesMenuSugestoes.put((short) 4, "Excluir");
        opcoesMenuSugestoes.put((short) 5, "Retornar ao menu anterior");
    }

    public void loopExec() throws Exception {
        boolean exec;
        do {
            if (!isLogged) {
                exec = showMenu(ultimaTela);
            } else {
                exec = showMenu(ultimaTela);
            }

            clearScreen();
        } while (exec);
    }

    private void showHeader() {
        System.out.println("--------------------------");
        System.out.println(nomeAplicacao + " " + versaoAplicacao);
        System.out.println("Desenvolvido pelo Grupo 3");
        System.out.println("--------------------------");
        if (isLogged) {
            System.out.println("Você está logado como " + usuario.getNome());
            System.out.println("--------------------------");

        }
    }
    
    public boolean showMenu(Tela menu) throws Exception {
        boolean exec;

        showHeader();

        showOpcoes(menu);

        exec = procOption(getOption(menu), menu);

        return exec;
    }

    public boolean showMenuInicial() throws Exception {
        boolean exec;

        showHeader();

        showOpcoes(Tela.MENU_INICIAL);

        exec = procOption(getOption(Tela.MENU_INICIAL), Tela.MENU_INICIAL);

        return exec;
    }

    public boolean showMenuPrincipal() throws Exception {
        boolean exec;

        showHeader();

        showOpcoes(Tela.MENU_PRINCIPAL);

        exec = procOption(getOption(Tela.MENU_PRINCIPAL), Tela.MENU_PRINCIPAL);

        return exec;
    }

    public void showMenuSugestoes() throws Exception {
        clearScreen();
        showHeader();
        showOpcoes(Tela.MENU_SUGESTOES);
        procOption(getOption(Tela.MENU_SUGESTOES), Tela.MENU_SUGESTOES);
    }

    /**
     * @param menu
     */
    private void showOpcoes(Tela menu) {
        switch (menu) {
            case MENU_INICIAL:
                for (Short s : opcoesMenuInicial.keySet()) {
                    System.out.println(s + ")" + opcoesMenuInicial.get(s));
                }
                break;
            case MENU_PRINCIPAL:
                for (Short s : opcoesMenuPrincipal.keySet()) {
                    System.out.println(s + ")" + opcoesMenuPrincipal.get(s));
                }
                break;
            case MENU_SUGESTOES:
                for (Short s : opcoesMenuSugestoes.keySet()) {
                    System.out.println(s + ")" + opcoesMenuSugestoes.get(s));
                }
                break;
            default:
                System.out.println("ERRO: Não foi possível exibir o menu desejado.");
                break;
        }
    }

    /**
     * @param menu
     * @return opcao escolhida
     */
    private short getOption(Tela menu) {
        short option;
        System.out.print("Opção: ");
        option = in.nextShort();

        switch (menu) {
            case MENU_INICIAL:
                while (!opcoesMenuInicial.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_PRINCIPAL:
                while (!opcoesMenuPrincipal.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_SUGESTOES:
                while (!opcoesMenuSugestoes.containsKey(option)) {
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

    /**
     * @param option
     * @param menu
     * @return
     * @throws Exception
     */
    private boolean procOption(short option, Tela menu) throws Exception {

        if (menu == Tela.MENU_INICIAL) {
            switch (option) {
                case 1:
                    usuario = procLogin();
                    ultimaTela = Tela.MENU_PRINCIPAL;
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
        } else if (menu == Tela.MENU_PRINCIPAL) {
            switch (option) {
                case 1:
                    showMenuSugestoes();
                    ultimaTela = Tela.MENU_SUGESTOES;
                    return true;
                case 2:
                    return true;
                case 3:
                    ultimaTela = Tela.MENU_INICIAL;
                    setIsLogged(false);
                    return true;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_SUGESTOES) {
            switch (option) {
                case 1:
                    procListagemSugestoes();
                    break;
                case 2:
                    procCadastroSugestao();
                    break;
                case 3:
                    procAlteracaoSugestao();
                    break;
                case 4:
                    procExclusaoSugestao();
                    break;
                case 5:
                    ultimaTela = Tela.MENU_PRINCIPAL;
                    break;
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
        Usuario us;

        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();

        if ((us = crudUsuario.read(email)) == null) {
            System.out.println("\nERRO: Não existe usuário cadastrado para o email inserido.");
            pressToContinue();
            return null;
        }

        System.out.print("\nDigite sua senha: ");
        senha = in.nextLine();

        if (!us.getSenha().equals(senha)) {
            System.out.println("\nERRO: A senha inserida é inválida.");
            pressToContinue();
            return null;
        }

        System.out.println("\nLogin realizado com sucesso!\n\n\n");

        setIsLogged(true);

        return us;
    }

    private void procCadastro() throws Exception {
        Usuario us;
        String email;
        String nome;
        String apelido;
        String senha;
        String codRec;
        String confirma;

        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();

        if (crudUsuario.read(email) != null || !isValidEmailAddressRegex(email)) {
            System.out.println("\nERRO: O email inserido já pertence a outro usuário ou é inválido.");
            pressToContinue();
            return;
        }

        System.out.print("\nDigite seu nome: ");
        nome = in.nextLine();

        while (nome.equals("") || nome.length() < 1) {
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

        while (senha.equals("") || senha.length() < 5) {
            System.out.println("ERRO: A senha inserida é inválida. Insira novamente.");
            System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
            senha = in.nextLine();
        }

        System.out.print("\nDigite seu código de recuperação de senha (no mínimo 5 caracteres): ");
        codRec = in.nextLine();

        while (codRec.equals("") || codRec.length() < 5) {
            System.out.println("ERRO: O código de recuperação é inválido. Insira novamente.");
            System.out.print("\nDigite seu código de recuperação de senha (no mínimo 5 caracteres): ");
            codRec = in.nextLine();
        }

        us = new Usuario(nome, apelido, email, senha, codRec);

        System.out.println("\nVocê confirma a criação de um usuário com estes dados? (Digite sim ou nao)\n");
        System.out.println(us.toString());

        System.out.print("\nConfirma: ");
        confirma = in.nextLine();

        while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
            System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
            System.out.print("Confirma: ");
            confirma = in.nextLine();
        }

        if (confirma.equals("sim")) {
            crudUsuario.create(us);
            System.out.println("\nUsuário cadastrado com sucesso.");
        }
        if (confirma.equals("nao")) {
            System.out.println("\nCadastro de usuário abortado.");
        }

        pressToContinue();
    }

    private void procRecSenha() throws Exception {
        String email;
        Usuario us;
        String novaSenha;
        String codRec;

        in.nextLine();
        System.out.print("\nDigite seu email: ");
        email = in.nextLine();

        if ((us = crudUsuario.read(email)) == null) {
            System.out.println("\nERRO: Não existe usuário cadastrado com este email.");
        } else {
            System.out.print("\nDigite seu código de recuperação: ");
            codRec = in.nextLine();

            if (codRec.equals(us.getCodigoDeRecuperacao())) {
                System.out.print("\nDigite a nova senha (no mínimo 5 caracteres): ");
                novaSenha = in.nextLine();

                while (novaSenha.equals("") || novaSenha.length() < 5) {
                    System.out.println("ERRO: A senha inserida é inválida. Insira novamente.");
                    System.out.print("\nDigite sua senha (no mínimo 5 caracteres): ");
                    novaSenha = in.nextLine();
                }

                us.setSenha(novaSenha);
                crudUsuario.update(us);

                System.out.println("\nSenha atualizada com sucesso!");
            } else {
                System.out.println("\nERRO: Código de recuperação inválido.");
            }
        }

        pressToContinue();
    }

    private void procListagemSugestoes() throws Exception {
        int[] listaIdSugestoes = crudSugestao.getIndiceRelacionamento().read(usuario.getId());

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]).toString());
                System.out.println();
            }
        } else {
            System.out.println("Você não possui sugestões cadastradas.");
        }

        pressToContinue();
    }

    private void procCadastroSugestao() throws Exception {
        Sugestao sg;
        String produto, loja, observacoes;
        float valor;
        String confirma;

        in.nextLine();
        System.out.print("\nDigite o nome do produto: ");
        produto = in.nextLine();

        if (!produto.equals("")) {
            System.out.print("\nDigite o nome da loja: ");
            loja = in.nextLine();

            while (loja.equals("")) {
                System.out.println("ERRO: O nome fornecido para loja é inválido. Insira novamente.");
                System.out.print("\nDigite o nome da loja: ");
                loja = in.nextLine();
            }

            System.out.print("\nDigite as observações: ");
            observacoes = in.nextLine();

            while (observacoes.equals("")) {
                System.out.println("ERRO: Texto inválido. Insira novamente.");
                System.out.print("\nDigite as observações: ");
                observacoes = in.nextLine();
            }

            System.out.print("\nDigite o valor: ");
            valor = in.nextFloat();

            while (valor <= 0) {
                System.out.println("ERRO: O valor inserido é inválido. Insira novamente.");
                System.out.print("\nDigite o valor: ");
                valor = in.nextFloat();
            }

            in.nextLine();
            sg = new Sugestao(produto, loja, valor, observacoes);

            System.out.println("\nVocê confirma a criação de uma sugestão com estes dados? (Digite sim ou nao)\n");
            System.out.println(sg.toString());

            System.out.print("\nConfirma: ");
            confirma = in.nextLine();

            while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                System.out.print("Confirma: ");
                confirma = in.nextLine();
            }

            if (confirma.equals("sim")) {
                int id = crudSugestao.create(sg);
                crudSugestao.getIndiceRelacionamento().create(usuario.getId(), id);
                System.out.println("\nSugestão cadastrada com sucesso.");
            }

            if (confirma.equals("nao")) {
                System.out.println("\nCadastro de sugestão abortado.");
            }

            pressToContinue();
        }
    }

    private void procAlteracaoSugestao() throws Exception {
        int[] listaIdSugestoes = crudSugestao.getIndiceRelacionamento().read(usuario.getId());
        String sgEscolhida;
        Sugestao escolhida;
        int alterado = 0;
        String produto, loja, observacoes;
        String valor;
        String confirma;

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]).toString());
                System.out.println();
            }

            System.out.print("Qual sugestão deseja alterar: ");
            sgEscolhida = in.nextLine();
            //System.out.println("sg = " + sgEscolhida);
            if (sgEscolhida.equals("0")) {

            } else {
                while (sgEscolhida.equals("") || (Integer.parseInt(sgEscolhida) < 0 || Integer.parseInt(sgEscolhida) > listaIdSugestoes.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual sugestão deseja alterar: ");
                    sgEscolhida = in.nextLine();
                }

                if (sgEscolhida.equals("0")) {
                    return;
                }

                escolhida = crudSugestao.read(listaIdSugestoes[Integer.parseInt(sgEscolhida) - 1]);

                System.out.println("\n\nOs dados da sugestão que deseja alterar são: ");
                System.out.println(escolhida.toString());

                System.out.print("\nDigite o novo nome do produto: ");
                produto = in.nextLine();

                if (!produto.equals("")) {
                    escolhida.setProduto(produto);
                    alterado++;
                }

                System.out.print("\nDigite o novo nome da loja: ");
                loja = in.nextLine();

                if (!loja.equals("")) {
                    escolhida.setLoja(loja);
                    alterado++;
                }

                System.out.print("\nDigite o novo valor: ");
                valor = in.nextLine();

                if (!valor.equals("")) {
                    escolhida.setValor(Float.parseFloat(valor));
                    alterado++;
                }

                System.out.print("\nDigite as novas observações: ");
                observacoes = in.nextLine();

                if (!observacoes.equals("")) {
                    escolhida.setObservacoes(observacoes);
                    alterado++;
                }

                if (alterado != 0) {
                    System.out.println("\nVocê confirma a alteração da sugestão com estes dados? (Digite sim ou nao)\n");
                    System.out.println(escolhida.toString());

                    System.out.print("\nConfirma: ");
                    confirma = in.nextLine();

                    while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                        System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                        System.out.print("Confirma: ");
                        confirma = in.nextLine();
                    }

                    if (confirma.equals("sim")) {
                        crudSugestao.update(escolhida);
                        System.out.println("\nSugestão atualizada com sucesso.");
                    }

                    if (confirma.equals("nao")) {
                        System.out.println("\nAtualização de sugestão abortada.");
                    }
                } else {
                    System.out.println("Nenhum dado foi alterado.");
                }
            }
        } else {
            System.out.println("Você não possui sugestões cadastradas.");
        }
        pressToContinue();
    }

    private void procExclusaoSugestao() throws Exception {
        int[] listaIdSugestoes = crudSugestao.getIndiceRelacionamento().read(usuario.getId());
        String sgEscolhida;
        Sugestao escolhida;
        String confirma;

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]).toString());
                System.out.println();
            }

            System.out.print("Qual sugestão deseja excluir: ");
            sgEscolhida = in.nextLine();
            if (sgEscolhida.equals("0")) {

            } else {
                while (sgEscolhida.equals("") || (Integer.parseInt(sgEscolhida) < 0 || Integer.parseInt(sgEscolhida) > listaIdSugestoes.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual sugestão deseja excluir: ");
                    sgEscolhida = in.nextLine();
                }

                if (sgEscolhida.equals("0")) {
                    return;
                }

                escolhida = crudSugestao.read(listaIdSugestoes[Integer.parseInt(sgEscolhida) - 1]);

                System.out.println("\n\nOs dados da sugestão que deseja excluir são: ");
                System.out.println(escolhida.toString());

                System.out.println("\nVocê confirma a exclusão da sugestão acima? (Digite sim ou nao)\n");

                System.out.print("\nConfirma: ");
                confirma = in.nextLine();

                while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                    System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                    System.out.print("Confirma: ");
                    confirma = in.nextLine();
                }

                if (confirma.equals("sim")) {
                    crudSugestao.delete(escolhida.getId());
                    crudSugestao.getIndiceRelacionamento().delete(usuario.getId(), escolhida.getId());
                    System.out.println("\nSugestão excluída com sucesso.");
                }

                if (confirma.equals("nao")) {
                    System.out.println("\nExclusão de sugestão abortada.");
                }
            }
        } else {
            System.out.println("Você não possui sugestões cadastradas.");
        }

        pressToContinue();
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

    public boolean isLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    private void clearScreen() {
        System.out.print("\n\n\n");
    }

    private void pressToContinue() {
        System.out.println("Pressione qualquer tecla para continuar...");
        in.nextLine();
    }
}
