package amigooculto.hud;

import amigooculto.bancoDeDados.CRUD;
import amigooculto.entidades.Grupo;
import amigooculto.entidades.Sugestao;
import amigooculto.entidades.Usuario;
import amigooculto.entidades.Convite;
import amigooculto.entidades.Participacao;
import amigooculto.indices.ArvoreBMais_Int_Int;
import amigooculto.indices.ArvoreBMais_ChaveComposta_String_Int;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Jonathan
 */
public class Interface {

    private String nomeAplicacao;
    private String versaoAplicacao;
    private CRUD<Usuario> crudUsuario;
    private CRUD<Sugestao> crudSugestao;
    private CRUD<Grupo> crudGrupos;
    private CRUD<Convite> crudConvites;
    private CRUD<Participacao> crudParticipacao;
    private final Scanner in;
    private boolean isLogged;
    public static Usuario usuario;
    private final HashMap<Short, String> opcoesMenuInicial;
    private final HashMap<Short, String> opcoesMenuPrincipal;
    private final HashMap<Short, String> opcoesMenuSugestoes;
    private final HashMap<Short, String> opcoesMenuGrupos;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos_Grupos;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos_Convites;
    private final HashMap<Short, String> opcoesMenuGerencimentoDeGrupos_Participantes;
    Tela ultimaTela;
    private ArvoreBMais_Int_Int indiceRelacionamentoSugestaoUsuario;
    private ArvoreBMais_Int_Int indiceRelacionamentoGrupoUsuario;
    private ArvoreBMais_Int_Int indiceRelacionamentoGrupoConvite;
    private ArvoreBMais_Int_Int indiceRelacionamentoGrupoParticipacao;
    private ArvoreBMais_Int_Int indiceRelacionamentoUsuarioParticipacao;
    private ArvoreBMais_ChaveComposta_String_Int listaInvertidaEmailConvite;
    private int quantConvites;

    public enum Tela {
        MENU_INICIAL, MENU_PRINCIPAL, MENU_SUGESTOES, MENU_GRUPOS, MENU_GERENCIAMENTODEGRUPOS,
        MENU_GERENCIAMENTODEGRUPOS_GRUPOS, MENU_GERENCIAMENTODEGRUPOS_CONVITES, MENU_CONVITES,
        MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES;
    }

    public Interface(String nome, String versao) {
        this.nomeAplicacao = nome;
        this.versaoAplicacao = versao;
        this.isLogged = false;
        ultimaTela = Tela.MENU_INICIAL;
        usuario = null;

        try {
            crudUsuario = new CRUD("BDUsuario", Usuario.class.getConstructor());
            crudSugestao = new CRUD("BDSugestao", Sugestao.class.getConstructor());
            crudGrupos = new CRUD("BDGrupos", Grupo.class.getConstructor());
            crudConvites = new CRUD("BDConvites", Convite.class.getConstructor());
            crudParticipacao = new CRUD("BDParticipacao", Participacao.class.getConstructor());

            indiceRelacionamentoSugestaoUsuario = new ArvoreBMais_Int_Int(10,
                    this.crudSugestao.getDiretorio() + "/indiceRelacionamento." + "Sugestao" + ".idx");
            indiceRelacionamentoGrupoUsuario = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "Grupo" + ".idx");
            indiceRelacionamentoGrupoConvite = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "Convite" + ".idx");
            listaInvertidaEmailConvite = new ArvoreBMais_ChaveComposta_String_Int(10, this.crudConvites.getDiretorio() + "/listaInvertida_EmailConvite."
                    + "Convite" + ".idx");
            indiceRelacionamentoGrupoParticipacao = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "GrupoParticipacao" + ".idx");
            indiceRelacionamentoUsuarioParticipacao = new ArvoreBMais_Int_Int(10,
                    this.crudGrupos.getDiretorio() + "/indiceRelacionamento." + "UsuarioParticipacao" + ".idx");

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
        opcoesMenuPrincipal.put((short) 3, "Convites: " + quantConvites);
        opcoesMenuPrincipal.put((short) 4, "Deslogar");

        opcoesMenuSugestoes = new HashMap<>();
        opcoesMenuSugestoes.put((short) 1, "Listar");
        opcoesMenuSugestoes.put((short) 2, "Incluir");
        opcoesMenuSugestoes.put((short) 3, "Alterar");
        opcoesMenuSugestoes.put((short) 4, "Excluir");
        opcoesMenuSugestoes.put((short) 5, "Retornar ao menu anterior");

        opcoesMenuGrupos = new HashMap<>();
        opcoesMenuGrupos.put((short) 1, "Criação e gerenciamento de grupos");
        opcoesMenuGrupos.put((short) 2, "Participação nos grupos");
        opcoesMenuGrupos.put((short) 3, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos.put((short) 1, "Grupos");
        opcoesMenuGerencimentoDeGrupos.put((short) 2, "Convites");
        opcoesMenuGerencimentoDeGrupos.put((short) 3, "Participantes");
        opcoesMenuGerencimentoDeGrupos.put((short) 4, "Sorteio");
        opcoesMenuGerencimentoDeGrupos.put((short) 5, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos_Grupos = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 1, "Listar");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 2, "Incluir");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 3, "Alterar");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 4, "Desativar");
        opcoesMenuGerencimentoDeGrupos_Grupos.put((short) 5, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos_Convites = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 1, "Listagem dos convites");
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 2, "Emissão de convites");
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 3, "Cancelamento de convites");
        opcoesMenuGerencimentoDeGrupos_Convites.put((short) 4, "Retornar ao menu anterior");

        opcoesMenuGerencimentoDeGrupos_Participantes = new HashMap<>();
        opcoesMenuGerencimentoDeGrupos_Participantes.put((short) 1, "Listagem");
        opcoesMenuGerencimentoDeGrupos_Participantes.put((short) 2, "Remoção");
        opcoesMenuGerencimentoDeGrupos_Participantes.put((short) 3, "Retornar ao menu anterior");

    }

    public void loopExec() throws Exception {
        boolean exec;
        do {
            if (isLogged) {
                int[] convites = listaInvertidaEmailConvite.read(usuario.chaveSecundaria());

                setQuantConvites(convites.length);
                opcoesMenuPrincipal.remove(3);
                opcoesMenuPrincipal.put((short) 3, "Convites: " + getQuantConvites());
            }
            exec = showMenu(ultimaTela);
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
        System.out.println();
    }

    public boolean showMenu(Tela menu) throws Exception {
        boolean exec;

        showHeader();

        showOpcoes(menu);

        exec = procOption(getOption(menu), menu);

        return exec;
    }

    /*
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

    public void showMenuGrupos() throws Exception {
        clearScreen();
        showHeader();
        showOpcoes(Tela.MENU_GRUPOS);
        procOption(getOption(Tela.MENU_GRUPOS), Tela.MENU_GRUPOS);
    }

    public void showMenuGerenciamentoDeGrupos() throws Exception {
        clearScreen();
        showHeader();
        showOpcoes(Tela.MENU_GERENCIAMENTODEGRUPOS);
        procOption(getOption(Tela.MENU_GERENCIAMENTODEGRUPOS), Tela.MENU_GERENCIAMENTODEGRUPOS);
    }

    public void showMenuGerenciamentoDeGrupos_Grupos() throws Exception {
        clearScreen();
        showHeader();
        showOpcoes(Tela.MENU_GERENCIAMENTODEGRUPOS_GRUPOS);
        procOption(getOption(Tela.MENU_GERENCIAMENTODEGRUPOS_GRUPOS), Tela.MENU_GERENCIAMENTODEGRUPOS_GRUPOS);
    }

    public void showMenuGerenciamentoDeGrupos_Convites() throws Exception {
        clearScreen();
        showHeader();
        showOpcoes(Tela.MENU_GERENCIAMENTODEGRUPOS_CONVITES);
        procOption(getOption(Tela.MENU_GERENCIAMENTODEGRUPOS_CONVITES), Tela.MENU_GERENCIAMENTODEGRUPOS_CONVITES);
    }
    
    public void showMenuGerenciamentoDeGrupos_Participacao() throws Exception {
        clearScreen();
        showHeader();
        showOpcoes(Tela.MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES);
        procOption(getOption(Tela.MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES), Tela.MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES);
    }*/
    /**
     * @param menu
     */
    private void showOpcoes(Tela menu) {
        switch (menu) {
            case MENU_INICIAL:
                System.out.println("ACESSO\n");
                for (Short s : opcoesMenuInicial.keySet()) {
                    System.out.println(s + ")" + opcoesMenuInicial.get(s));
                }
                break;
            case MENU_PRINCIPAL:
                System.out.println("INÍCIO\n");
                for (Short s : opcoesMenuPrincipal.keySet()) {
                    System.out.println(s + ")" + opcoesMenuPrincipal.get(s));
                }
                break;
            case MENU_SUGESTOES:
                System.out.println("INÍCIO > SUGESTÕES\n");
                for (Short s : opcoesMenuSugestoes.keySet()) {
                    System.out.println(s + ")" + opcoesMenuSugestoes.get(s));
                }
                break;
            case MENU_GRUPOS:
                System.out.println("INÍCIO > GRUPOS\n");
                for (Short s : opcoesMenuGrupos.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGrupos.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS:
                System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_GRUPOS:
                System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS > GRUPOS\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos_Grupos.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos_Grupos.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_CONVITES:
                System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS > CONVITES\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos_Convites.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos_Convites.get(s));
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES:
                System.out.println("INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS > PARTICIPANTES\n");
                for (Short s : opcoesMenuGerencimentoDeGrupos_Participantes.keySet()) {
                    System.out.println(s + ")" + opcoesMenuGerencimentoDeGrupos_Participantes.get(s));
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
            case MENU_GRUPOS:
                while (!opcoesMenuGrupos.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS:
                while (!opcoesMenuGerencimentoDeGrupos.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_GRUPOS:
                while (!opcoesMenuGerencimentoDeGrupos_Grupos.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_CONVITES:
                while (!opcoesMenuGerencimentoDeGrupos_Convites.containsKey(option)) {
                    System.out.print("\nOpção inválida. Insira novamente: ");
                    option = in.nextShort();
                }
                break;
            case MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES:
                while (!opcoesMenuGerencimentoDeGrupos_Participantes.containsKey(option)) {
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
                    if (!(usuario == null)) {
                        ultimaTela = Tela.MENU_PRINCIPAL;
                    }

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
                    ultimaTela = Tela.MENU_SUGESTOES;
                    return true;
                case 2:
                    ultimaTela = Tela.MENU_GRUPOS;
                    return true;
                case 3:
                    procConvitesPendentes();
                    ultimaTela = Tela.MENU_PRINCIPAL;
                    return true;
                case 4:
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
        } else if (menu == Tela.MENU_GRUPOS) {
            switch (option) {
                case 1:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS;
                    break;
                case 2:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES;
                    break;
                case 3:
                    ultimaTela = Tela.MENU_PRINCIPAL;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS) {
            switch (option) {
                case 1:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS_GRUPOS;
                    break;
                case 2:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS_CONVITES;
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    ultimaTela = Tela.MENU_GRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS_GRUPOS) {
            switch (option) {
                case 1:
                    procListagemGrupos();
                    break;
                case 2:
                    procCadastroGrupo();
                    break;
                case 3:
                    procAlteracaoGrupo();
                    break;
                case 4:
                    procDesativarGrupo();
                    break;
                case 5:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS_CONVITES) {
            switch (option) {
                case 1:
                    procListagemConvites();
                    break;
                case 2:
                    procEmissaoConvites();
                    break;
                case 3:
                    procCancelamentoConvites();
                    break;
                case 4:
                    ultimaTela = Tela.MENU_GERENCIAMENTODEGRUPOS;
                    break;
                default:
                    System.out.println("ERRO: Opção inexistente.");
                    break;
            }
        } else if (menu == Tela.MENU_GERENCIAMENTODEGRUPOS_PARTICIPANTES) {
            switch (option) {
                case 1:
                    procListagemParticipantes();
                    break;
                case 2:
                    procRemocaoParticipantes();
                    break;
                case 3:
                    ultimaTela = Tela.MENU_GRUPOS;
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
        System.out.println(us);

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
        int[] listaIdSugestoes = indiceRelacionamentoSugestaoUsuario.read(usuario.getId());

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]));
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

            System.out.print("\nDigite o valor: R$ ");
            valor = in.nextFloat();

            while (valor <= 0) {
                System.out.println("ERRO: O valor inserido é inválido. Insira novamente.");
                System.out.print("\nDigite o valor: R$ ");
                valor = in.nextFloat();
            }

            in.nextLine();
            sg = new Sugestao(produto, loja, valor, observacoes);

            System.out.println("\nVocê confirma a criação de uma sugestão com estes dados? (Digite sim ou nao)\n");
            System.out.println(sg);

            System.out.print("\nConfirma: ");
            confirma = in.nextLine();

            while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                System.out.print("Confirma: ");
                confirma = in.nextLine();
            }

            if (confirma.equals("sim")) {
                sg.setIdUsuario(usuario.getId());
                int id = crudSugestao.create(sg);
                indiceRelacionamentoSugestaoUsuario.create(usuario.getId(), id);
                System.out.println("\nSugestão cadastrada com sucesso.");
            }

            if (confirma.equals("nao")) {
                System.out.println("\nCadastro de sugestão abortado.");
            }

            pressToContinue();
        }
    }

    private void procAlteracaoSugestao() throws Exception {
        int[] listaIdSugestoes = indiceRelacionamentoSugestaoUsuario.read(usuario.getId());
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
                System.out.println(crudSugestao.read(listaIdSugestoes[i]));
                System.out.println();
            }

            System.out.print("Qual sugestão deseja alterar: ");
            sgEscolhida = in.nextLine();

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
                System.out.println(escolhida);

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

                System.out.print("\nDigite o novo valor: R$ ");
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
                    System.out.println(escolhida);

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
        int[] listaIdSugestoes = indiceRelacionamentoSugestaoUsuario.read(usuario.getId());
        String sgEscolhida;
        Sugestao escolhida;
        String confirma;

        in.nextLine();
        clearScreen();

        if (listaIdSugestoes.length != 0) {
            System.out.println("Minhas sugestões: \n");
            for (int i = 0; i < listaIdSugestoes.length; i++) {
                System.out.print((i + 1) + ". ");
                System.out.println(crudSugestao.read(listaIdSugestoes[i]));
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
                System.out.println(escolhida);

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
                    indiceRelacionamentoSugestaoUsuario.delete(usuario.getId(), escolhida.getId());
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

    private void procListagemGrupos() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    contador++;
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }

        pressToContinue();
    }

    private void procCadastroGrupo() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm");
        DateTimeZone timeZone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime dateTimeSorteio, dateTimeEncontro;
        Grupo grp;
        String nome, localEncontro, observacoes, dataSorteio, dataEncontro;
        String dia, mes, ano, hora, minuto;
        long momentoSorteio;
        float valor;
        long momentoEncontro;
        String confirma;
        int anoAtual = DateTime.now().year().get();
        int maiorMesPossivel = DateTime.now().monthOfYear().getMaximumValue();

        in.nextLine();
        System.out.print("\nDigite o nome do grupo: ");
        nome = in.nextLine();

        if (!nome.equals("")) {
            System.out.println("\nDigite a data do sorteio: ");
            System.out.print("Dia: ");
            dia = in.nextLine();
            while (!dia.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o dia: ");
                dia = in.nextLine();
            }

            dia = (dia.length() == 1) ? "0" + dia : dia;

            System.out.print("Mês: ");
            mes = in.nextLine();
            while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o mês: ");
                mes = in.nextLine();
            }

            mes = (mes.length() == 1) ? "0" + mes : mes;

            System.out.print("Ano: ");
            ano = in.nextLine();
            while (!ano.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(ano) < anoAtual) {
                System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o ano: ");
                ano = in.nextLine();
            }

            System.out.print("Hora: ");
            hora = in.nextLine();
            while (!hora.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                System.out.print("\nDigite a hora: ");
                hora = in.nextLine();
            }

            hora = (hora.length() == 1) ? "0" + hora : hora;

            System.out.print("Minuto: ");
            minuto = in.nextLine();
            while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o minuto: ");
                minuto = in.nextLine();
            }

            minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

            dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
            dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
            momentoSorteio = dateTimeSorteio.getMillis();

            while (momentoSorteio < DateTime.now().getMillis()) {
                System.out.println("Data do sorteio inferior a data atual. Insira a data do sorteio novamente.\n");
                System.out.println("\nDigite a data do sorteio: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(ano) < anoAtual) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
                momentoSorteio = dateTimeSorteio.getMillis();
            }

            System.out.print("\nDigite o valor: R$ ");
            valor = in.nextFloat();

            while (valor <= 0) {
                System.out.println("ERRO: O valor inserido é inválido. Insira novamente.");
                System.out.print("\nDigite o valor: R$ ");
                valor = in.nextFloat();
            }

            in.nextLine();
            System.out.println("\nDigite a data do encontro: ");
            System.out.print("Dia: ");
            dia = in.nextLine();
            while (!dia.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o dia: ");
                dia = in.nextLine();
            }

            dia = (dia.length() == 1) ? "0" + dia : dia;

            System.out.print("Mês: ");
            mes = in.nextLine();
            while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o mês: ");
                mes = in.nextLine();
            }

            mes = (mes.length() == 1) ? "0" + mes : mes;

            System.out.print("Ano: ");
            ano = in.nextLine();
            while (!ano.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(ano) < anoAtual) {
                System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o ano: ");
                ano = in.nextLine();
            }

            System.out.print("Hora: ");
            hora = in.nextLine();
            while (!hora.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                System.out.print("\nDigite a hora: ");
                hora = in.nextLine();
            }

            hora = (hora.length() == 1) ? "0" + hora : hora;

            System.out.print("Minuto: ");
            minuto = in.nextLine();
            while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                System.out.print("\nDigite o minuto: ");
                minuto = in.nextLine();
            }

            minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

            dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
            dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
            momentoEncontro = dateTimeEncontro.getMillis();

            while (momentoEncontro < momentoSorteio) {
                System.out.println("Data do encontro inferior a data do sorteio. Insira a data do encontro novamente.\n");
                System.out.println("\nDigite a data do encontro: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
                momentoEncontro = dateTimeEncontro.getMillis();
            }

            System.out.print("\nDigite o local do encontro: ");
            localEncontro = in.nextLine();

            while (localEncontro.equals("")) {
                System.out.println("ERRO: Texto inválido. Insira novamente.");
                System.out.print("\nDigite o local do encontro: ");
                localEncontro = in.nextLine();
            }

            System.out.print("\nDigite as observações: ");
            observacoes = in.nextLine();

            while (observacoes.equals("")) {
                System.out.println("ERRO: Texto inválido. Insira novamente.");
                System.out.print("\nDigite as observações: ");
                observacoes = in.nextLine();
            }

            grp = new Grupo(nome, momentoSorteio, valor, momentoEncontro, localEncontro, observacoes);

            System.out.println("\nVocê confirma a criação de um grupo com estes dados? (Digite sim ou nao)\n");
            System.out.println(grp);

            System.out.print("\nConfirma: ");
            confirma = in.nextLine();

            while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                System.out.print("Confirma: ");
                confirma = in.nextLine();
            }

            if (confirma.equals("sim")) {
                grp.setIdUsuario(usuario.getId());
                int id = crudGrupos.create(grp);
                indiceRelacionamentoGrupoUsuario.create(usuario.getId(), id);
                Participacao participacao = new Participacao();
                participacao.setIdGrupo(grp.getId());
                participacao.setIdUsuario(usuario.getId());
                int idParticipacao = crudParticipacao.create(participacao);
                indiceRelacionamentoGrupoParticipacao.create(id, idParticipacao);
                indiceRelacionamentoUsuarioParticipacao.create(usuario.getId(), idParticipacao);
                System.out.println("\nGrupo cadastrado com sucesso.");
            }

            if (confirma.equals("nao")) {
                System.out.println("\nCadastro de grupo abortado.");
            }

            pressToContinue();
        }
    }

    private void procAlteracaoGrupo() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm");
        DateTimeZone timeZone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime dateTimeSorteio, dateTimeEncontro;
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        String grpEscolhido;
        Grupo escolhido;
        String nome, localEncontro, observacoes, dataSorteio, dataEncontro;
        String dia, mes, ano, hora, minuto;
        long momentoSorteio;
        String valor;
        long momentoEncontro;
        String confirma;
        int alterado = 0;
        int anoAtual = DateTime.now().year().get();
        int maiorMesPossivel = DateTime.now().monthOfYear().getMaximumValue();

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                }
            }

            System.out.print("Qual grupo deseja alterar: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > listaIdGrupos.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja alterar: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("\n\nOs dados do grupo que deseja alterar são: ");
                System.out.println(escolhido);

                System.out.print("\nDigite o novo nome do grupo: ");
                nome = in.nextLine();

                if (!nome.equals("")) {
                    escolhido.setNome(nome);
                    alterado++;
                }

                System.out.println("\nDigite o novo momento do sorteio: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
                momentoSorteio = dateTimeSorteio.getMillis();

                while (momentoSorteio < DateTime.now().getMillis()) {
                    System.out.println("Data do sorteio inferior a data atual. Insira a data do sorteio novamente.\n");

                    System.out.println("\nDigite o novo momento do sorteio: ");
                    System.out.print("Dia: ");
                    dia = in.nextLine();
                    while (!dia.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o dia: ");
                        dia = in.nextLine();
                    }

                    dia = (dia.length() == 1) ? "0" + dia : dia;

                    System.out.print("Mês: ");
                    mes = in.nextLine();
                    while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                        System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o mês: ");
                        mes = in.nextLine();
                    }

                    mes = (mes.length() == 1) ? "0" + mes : mes;

                    System.out.print("Ano: ");
                    ano = in.nextLine();
                    while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                        System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o ano: ");
                        ano = in.nextLine();
                    }

                    System.out.print("Hora: ");
                    hora = in.nextLine();
                    while (!hora.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                        System.out.print("\nDigite a hora: ");
                        hora = in.nextLine();
                    }

                    hora = (hora.length() == 1) ? "0" + hora : hora;

                    System.out.print("Minuto: ");
                    minuto = in.nextLine();
                    while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o minuto: ");
                        minuto = in.nextLine();
                    }

                    minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                    dataSorteio = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                    dateTimeSorteio = formatter.withZone(timeZone).parseDateTime(dataSorteio);
                    momentoSorteio = dateTimeSorteio.getMillis();
                }

                if (!(momentoSorteio == escolhido.getMomentoSorteio())) {
                    escolhido.setMomentoSorteio(momentoSorteio);
                    alterado++;
                }

                System.out.print("\nDigite o novo valor: R$ ");
                valor = in.nextLine();

                if (!valor.equals("")) {
                    escolhido.setValor(Float.parseFloat(valor));
                    alterado++;
                }

                System.out.println("\nDigite o novo momento do encontro: ");
                System.out.print("Dia: ");
                dia = in.nextLine();
                while (!dia.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o dia: ");
                    dia = in.nextLine();
                }

                dia = (dia.length() == 1) ? "0" + dia : dia;

                System.out.print("Mês: ");
                mes = in.nextLine();
                while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                    System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o mês: ");
                    mes = in.nextLine();
                }

                mes = (mes.length() == 1) ? "0" + mes : mes;

                System.out.print("Ano: ");
                ano = in.nextLine();
                while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                    System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o ano: ");
                    ano = in.nextLine();
                }

                System.out.print("Hora: ");
                hora = in.nextLine();
                while (!hora.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                    System.out.print("\nDigite a hora: ");
                    hora = in.nextLine();
                }

                hora = (hora.length() == 1) ? "0" + hora : hora;

                System.out.print("Minuto: ");
                minuto = in.nextLine();
                while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                    System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                    System.out.print("\nDigite o minuto: ");
                    minuto = in.nextLine();
                }

                minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
                momentoEncontro = dateTimeEncontro.getMillis();

                while (momentoEncontro < momentoSorteio) {
                    System.out.println("Data do encontro inferior a data do sorteio. Insira a data do encontro novamente.\n");
                    System.out.println("\nDigite o novo momento do encontro: ");
                    System.out.print("Dia: ");
                    dia = in.nextLine();
                    while (!dia.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O dia fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o dia: ");
                        dia = in.nextLine();
                    }

                    dia = (dia.length() == 1) ? "0" + dia : dia;

                    System.out.print("Mês: ");
                    mes = in.nextLine();
                    while (!mes.matches("-?(0|[1-9]\\d*)") || Integer.parseInt(mes) > maiorMesPossivel) {
                        System.out.println("ERRO: O mês fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o mês: ");
                        mes = in.nextLine();
                    }

                    mes = (mes.length() == 1) ? "0" + mes : mes;

                    System.out.print("Ano: ");
                    ano = in.nextLine();
                    while (!ano.matches("-?(0|[1-9]\\d*)") || (Integer.parseInt(ano) < anoAtual)) {
                        System.out.println("ERRO: O ano fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o ano: ");
                        ano = in.nextLine();
                    }

                    System.out.print("Hora: ");
                    hora = in.nextLine();
                    while (!hora.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: A hora fornecida é inválida. Insira novamente.");
                        System.out.print("\nDigite a hora: ");
                        hora = in.nextLine();
                    }

                    hora = (hora.length() == 1) ? "0" + hora : hora;

                    System.out.print("Minuto: ");
                    minuto = in.nextLine();
                    while (!minuto.matches("-?(0|[1-9]\\d*)")) {
                        System.out.println("ERRO: O minuto fornecido é inválido. Insira novamente.");
                        System.out.print("\nDigite o minuto: ");
                        minuto = in.nextLine();
                    }

                    minuto = (minuto.length() == 1) ? "0" + minuto : minuto;

                    dataEncontro = dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
                    dateTimeEncontro = formatter.withZone(timeZone).parseDateTime(dataEncontro);
                    momentoEncontro = dateTimeEncontro.getMillis();
                }

                if (!(momentoEncontro == escolhido.getMomentoEncontro())) {
                    escolhido.setMomentoEncontro(momentoEncontro);
                    alterado++;
                }

                System.out.print("\nDigite o novo local do encontro: ");
                localEncontro = in.nextLine();

                if (!localEncontro.equals("")) {
                    escolhido.setLocalEncontro(localEncontro);
                    alterado++;
                }

                System.out.print("\nDigite as novas observações: ");
                observacoes = in.nextLine();

                if (!observacoes.equals("")) {
                    escolhido.setObservacoes(observacoes);
                    alterado++;
                }

                if (alterado != 0) {
                    System.out.println("\nVocê confirma a alteração de um grupo com estes dados? (Digite sim ou nao)\n");
                    System.out.println(escolhido);

                    System.out.print("\nConfirma: ");
                    confirma = in.nextLine();

                    while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                        System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                        System.out.print("Confirma: ");
                        confirma = in.nextLine();
                    }

                    if (confirma.equals("sim")) {
                        crudGrupos.update(escolhido);
                        System.out.println("\nGrupo atualizado com sucesso.");
                    }

                    if (confirma.equals("nao")) {
                        System.out.println("\nAtualização de grupo abortada.");
                    }
                } else {
                    System.out.println("Nenhum dado foi alterado.");
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }
        pressToContinue();
    }

    private void procDesativarGrupo() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        String grpEscolhido;
        Grupo escolhido;
        String confirma;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                }
            }

            System.out.print("Qual grupo deseja desativar: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > listaIdGrupos.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja alterar: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("\n\nOs dados do grupo que deseja alterar são: ");
                System.out.println(escolhido);

                System.out.println("\nVocê confirma a desativação de um grupo com estes dados? (Digite sim ou nao)\n");
                System.out.println(escolhido);

                System.out.print("\nConfirma: ");
                confirma = in.nextLine();

                while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                    System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                    System.out.print("Confirma: ");
                    confirma = in.nextLine();
                }

                if (confirma.equals("sim")) {
                    escolhido.setAtivo(false);
                    crudGrupos.update(escolhido);
                    System.out.println("\nGrupo desativado com sucesso.");
                }

                if (confirma.equals("nao")) {
                    System.out.println("\nDesativação de grupo abortado.");
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }

        pressToContinue();
    }

    private void procListagemConvites() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] listaIdConvites;
        String grpEscolhido;
        Grupo escolhido;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    contador++;
                }
            }

            System.out.print("Qual grupo deseja visualizar os convites: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > listaIdGrupos.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(listaIdGrupos[Integer.parseInt(grpEscolhido) - 1]);

                System.out.println("\n\nCONVITES DO GRUPO " + '"' + escolhido.getNome() + '"' + "\n");

                listaIdConvites = indiceRelacionamentoGrupoConvite.read(escolhido.getId());

                if (listaIdConvites.length == 0) {
                    System.out.println("Não há convites emitidos neste grupo.");
                } else {
                    for (int i = 0; i < listaIdConvites.length; i++) {
                        Convite convite = crudConvites.read(listaIdConvites[i]);
                        System.out.print((i + 1) + ". ");
                        System.out.println(convite);
                    }
                }
            }
        } else {
            System.out.println("\n\nVocê não possui grupos cadastrados.");
        }
        System.out.println();
        pressToContinue();
    }

    private void procEmissaoConvites() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());

        String grpEscolhido, email, confirma;
        Grupo escolhido;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo() && !grupo.isSorteado()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                }
            }

            System.out.print("Qual grupo deseja visualizar os convites: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > listaIdGrupos.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("GRUPO " + '"' + escolhido.getNome() + '"' + "\n");

                System.out.print("Digite um email para emitir o convite: ");
                email = in.nextLine();
                while (!email.isEmpty()) {
                    Convite cv;
                    if (!((cv = crudConvites.read(escolhido.getId() + "|" + email)) == null)) {
                        if (email.equals(usuario.chaveSecundaria())) {
                            clearScreen();
                            System.out.println("ERRO: Não é possível enviar um convite para si mesmo.");
                        } else if (cv.getEstado() == 0 || cv.getEstado() == 1) {
                            clearScreen();
                            System.out.println(cv);
                            System.out.println("\n\nConvite já emitido para este usuário.\n");

                        } else if (cv.getEstado() == 2 || cv.getEstado() == 3) {
                            clearScreen();
                            System.out.println(cv);
                            System.out.println("\n\nConvite já recusado ou cancelado. Deseja reenviar? (Digite sim ou nao)\n");

                            System.out.print("\nConfirma: ");
                            confirma = in.nextLine();

                            while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                                System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                                System.out.print("Confirma: ");
                                confirma = in.nextLine();
                            }

                            if (confirma.equals("sim")) {
                                cv.setEstado((byte) 0);
                                crudConvites.update(cv);
                                System.out.println("\nConvite reenviado com sucesso.");
                            }

                            if (confirma.equals("nao")) {
                                System.out.println("\nReenvio de convite abortado.");
                            }
                        }
                    } else {
                        cv = new Convite(email, DateTime.now().getMillis(), (byte) 0);
                        cv.setIdGrupo(escolhido.getId());
                        int id = crudConvites.create(cv);
                        listaInvertidaEmailConvite.create(email, id);
                        indiceRelacionamentoGrupoConvite.create(escolhido.getId(), id);
                        System.out.println("\n\nConvite enviado para o usuário de email: " + email);
                    }

                    System.out.print("\n\nDigite um email para emitir o convite: ");
                    email = in.nextLine();
                }
            }
        } else {
            System.out.println("\n\nVocê não possui grupos cadastrados.");
        }

        pressToContinue();
    }

    private void procCancelamentoConvites() throws Exception {
        int[] listaIdGrupos = indiceRelacionamentoGrupoUsuario.read(usuario.getId());
        int[] listaIdConvites;
        String grpEscolhido, email, confirma, cvEscolhido;
        Grupo escolhido;
        Convite conviteEscolhido;

        in.nextLine();
        clearScreen();

        if (listaIdGrupos.length != 0) {
            int contador = 1;
            int[] vetorId = new int[listaIdGrupos.length + 1];
            System.out.println("Meus grupos: \n");
            for (int i = 0; i < listaIdGrupos.length; i++) {
                Grupo grupo = crudGrupos.read(listaIdGrupos[i]);
                if (grupo.isAtivo() && !grupo.isSorteado()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                }
            }

            System.out.print("Qual grupo deseja visualizar os convites: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > listaIdGrupos.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);

                System.out.println("GRUPO " + '"' + escolhido.getNome() + '"' + "\n");

                listaIdConvites = indiceRelacionamentoGrupoConvite.read(escolhido.getId());
                contador = 1;
                vetorId = new int[listaIdConvites.length + 1];
                for (int i = 0; i < listaIdConvites.length; i++) {
                    Convite convite = crudConvites.read(listaIdConvites[i]);

                    if (convite.getEstado() == 0) {
                        System.out.print((contador) + ". ");
                        System.out.println(convite);
                        vetorId[contador] = convite.getId();
                        contador++;
                    }
                }

                System.out.print("Qual convite deseja cancelar: ");
                cvEscolhido = in.nextLine();

                if (cvEscolhido.equals("0")) {

                } else {
                    while (cvEscolhido.equals("") || (Integer.parseInt(cvEscolhido) < 0 || Integer.parseInt(cvEscolhido) > listaIdConvites.length)) {
                        System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                        System.out.print("\nQual convite deseja cancelar: ");
                        cvEscolhido = in.nextLine();
                    }

                    if (cvEscolhido.equals("0")) {
                        return;
                    }

                    conviteEscolhido = crudConvites.read(vetorId[Integer.parseInt(cvEscolhido)]);

                    System.out.println("\nVocê confirma o cancelamento do convite com estes dados? (Digite sim ou nao)\n");
                    System.out.println(conviteEscolhido);

                    System.out.print("\nConfirma: ");
                    confirma = in.nextLine();

                    while ((!confirma.equals("sim")) && (!confirma.equals("nao"))) {
                        System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                        System.out.print("Confirma: ");
                        confirma = in.nextLine();
                    }

                    if (confirma.equals("sim")) {
                        conviteEscolhido.setEstado((byte) 3);
                        crudConvites.update(conviteEscolhido);
                        listaInvertidaEmailConvite.delete(conviteEscolhido.getEmail(), conviteEscolhido.getId());
                        System.out.println("\nConvite cancelado com sucesso.");
                    }

                    if (confirma.equals("nao")) {
                        System.out.println("\nCancelamento de convite abortado.");
                    }
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }

        pressToContinue();
    }

    private void procConvitesPendentes() throws Exception {
        int[] listaIdConvites;
        Convite conviteEscolhido;
        String cvEscolhido, confirma;

        listaIdConvites = listaInvertidaEmailConvite.read(usuario.chaveSecundaria());

        in.nextLine();
        clearScreen();

        if (listaIdConvites.length != 0) {
            int contador = 1;
            int[] vetorId = new int[listaIdConvites.length + 1];
            System.out.println("Meus convites: \n");
            for (int i = 0; i < listaIdConvites.length; i++) {
                Convite cv = crudConvites.read(listaIdConvites[i]);
                Grupo grupo = crudGrupos.read(cv.getIdGrupo());
                if (cv.getEstado() == 0) {
                    System.out.print((contador) + ". ");
                    DateTime dateTime = new DateTime(cv.getMomentoConvite());

                    String mensagem = "Convidado em " + dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/"
                            + dateTime.getYear() + ", às " + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay()) + ":"
                            + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour()) + " por " + crudUsuario.read(grupo.getIdUsuario()).getNome();
                    System.out.println(grupo.getNome() + "\n" + mensagem);
                    System.out.println();
                    vetorId[contador] = cv.getId();
                    contador++;
                } else {
                    listaInvertidaEmailConvite.delete(cv.chaveSecundaria(), cv.getId());
                }
            }

            System.out.print("Qual convite deseja aceitar ou recusar: ");
            cvEscolhido = in.nextLine();

            while (!cvEscolhido.equals("0")) {

                while (cvEscolhido.equals("") || (Integer.parseInt(cvEscolhido) < 0 || Integer.parseInt(cvEscolhido) > listaIdConvites.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual convite deseja cancelar: ");
                    cvEscolhido = in.nextLine();
                }

                if (cvEscolhido.equals("0")) {
                    break;
                }

                conviteEscolhido = crudConvites.read(vetorId[Integer.parseInt(cvEscolhido)]);

                System.out.println("\nVocê aceita ou recusa o convite selecionado? (Digite A(para aceitar), R(para recusar) ou V(para voltar))\n");
                DateTime dateTime = new DateTime(conviteEscolhido.getMomentoConvite());
                Grupo grupo = crudGrupos.read(conviteEscolhido.getIdGrupo());

                String mensagem = "Convidado em " + dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/"
                        + dateTime.getYear() + ", às " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour()
                        + " por " + crudUsuario.read(grupo.getIdUsuario()).getNome();

                System.out.println(grupo.getNome() + "\n" + mensagem);

                System.out.print("\nConfirma: ");
                confirma = in.nextLine();

                while ((!confirma.equals("A")) && (!confirma.equals("R")) && (!confirma.equals("V"))) {
                    System.out.println("\nERRO: Confirmação inválida. Digite sim ou nao.");
                    System.out.print("Confirma: ");
                    confirma = in.nextLine();
                }

                if (confirma.equals("A")) {
                    conviteEscolhido.setEstado((byte) 1);
                    crudConvites.update(conviteEscolhido);
                    listaInvertidaEmailConvite.delete(conviteEscolhido.getEmail(), conviteEscolhido.getId());
                    Participacao participacao = new Participacao();
                    participacao.setIdGrupo(conviteEscolhido.getIdGrupo());
                    participacao.setIdUsuario(usuario.getId());
                    int idParticipacao = crudParticipacao.create(participacao);
                    indiceRelacionamentoGrupoParticipacao.create(conviteEscolhido.getIdGrupo(), idParticipacao);
                    indiceRelacionamentoUsuarioParticipacao.create(usuario.getId(), idParticipacao);
                    System.out.println("\nConvite aceito com sucesso.");
                }

                if (confirma.equals("R")) {
                    conviteEscolhido.setEstado((byte) 2);
                    crudConvites.update(conviteEscolhido);
                    listaInvertidaEmailConvite.delete(conviteEscolhido.getEmail(), conviteEscolhido.getId());
                    System.out.println("\nConvite recusado com sucesso.");
                }

                contador = 1;
                vetorId = new int[listaIdConvites.length + 1];
                System.out.println("\n\nMeus convites: \n");
                for (int i = 0; i < listaIdConvites.length; i++) {
                    Convite cv = crudConvites.read(listaIdConvites[i]);
                    grupo = crudGrupos.read(cv.getIdGrupo());
                    if (cv.getEstado() == 0) {
                        System.out.print((contador) + ". ");
                        dateTime = new DateTime(cv.getMomentoConvite());

                        mensagem = "Convidado em " + dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/"
                                + dateTime.getYear() + ", às " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour()
                                + " por " + crudUsuario.read(grupo.getIdUsuario()).getNome();
                        System.out.println(grupo.getNome() + "\n" + mensagem);
                        System.out.println();
                        vetorId[contador] = cv.getId();
                        contador++;
                    } else {
                        listaInvertidaEmailConvite.delete(cv.chaveSecundaria(), cv.getId());
                    }
                }

                System.out.print("Qual convite deseja aceitar ou recusar: ");
                cvEscolhido = in.nextLine();
            }
        } else {
            System.out.println("\n\nVocê não possui convites pendentes.\n\n");
        }
        pressToContinue();
    }

    private void procListagemParticipantes() throws Exception {
        int[] participacoes = indiceRelacionamentoUsuarioParticipacao.read(usuario.getId());
        int[] listaIdParticipacoes;
        String grpEscolhido;
        Grupo escolhido;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy hh:mm");

        in.nextLine();
        clearScreen();

        if (participacoes.length != 0) {
            System.out.println("Meus grupos: \n");
            int contador = 1;
            int[] vetorId = new int[participacoes.length + 1];
            for (int i = 0; i < participacoes.length; i++) {
                Grupo grupo = crudGrupos.read(crudParticipacao.read(participacoes[i]).getIdGrupo());
                if (grupo.isAtivo()) {
                    System.out.print((contador) + ". ");
                    System.out.println(grupo);

                    System.out.println("Sorteio: " + (grupo.isSorteado() ? "Já sorteado em " + formatter.print(grupo.getMomentoSorteio())
                            : "Terá sorteio em " + formatter.print(grupo.getMomentoSorteio())));
                    System.out.println();
                    vetorId[contador] = grupo.getId();
                    contador++;
                }
            }

            System.out.print("Qual grupo deseja gerenciar: ");
            grpEscolhido = in.nextLine();

            if (grpEscolhido.equals("0")) {

            } else {
                while (grpEscolhido.equals("") || (Integer.parseInt(grpEscolhido) < 0 || Integer.parseInt(grpEscolhido) > participacoes.length)) {
                    System.out.println("ERRO: Você fez uma escolha inválida. Insira novamente.");
                    System.out.print("\nQual grupo deseja visualizar os convites: ");
                    grpEscolhido = in.nextLine();
                }

                if (grpEscolhido.equals("0")) {
                    return;
                }

                escolhido = crudGrupos.read(vetorId[Integer.parseInt(grpEscolhido)]);
                listaIdParticipacoes = indiceRelacionamentoGrupoParticipacao.read(escolhido.getId());

                System.out.println("\n\nParticipantes do grupo " + '"' + escolhido.getNome() + '"' + ":\n");

                contador = 1;
                for (int i = 0; i < listaIdParticipacoes.length; i++) {
                    Participacao participacao = crudParticipacao.read(listaIdParticipacoes[i]);
                    System.out.print((contador) + ". ");
                    System.out.println(crudUsuario.read(participacao.getIdUsuario()).getNome());
                    System.out.println();
                    contador++;
                }
            }
        } else {
            System.out.println("Você não possui grupos cadastrados.");
        }
        pressToContinue();
    }

    private void procRemocaoParticipantes() {

    }

    private boolean isValidEmailAddressRegex(String email) {
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

    public int getQuantConvites() {
        return quantConvites;
    }

    public void setQuantConvites(int quantConvites) {
        this.quantConvites = quantConvites;
    }

    private void clearScreen() {
        System.out.print("\n\n\n");
    }

    private void pressToContinue() {
        System.out.println("Pressione qualquer tecla para continuar...");
        in.nextLine();
    }
}
