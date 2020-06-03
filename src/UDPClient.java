import java.util.Scanner;

public class UDPClient extends Data implements Opcoes {

	private static String dificuldade;
	
	/**
	 * Menu inicial que da a opção de jogar ou de sair do jogo.
	 * Seleciona a dificuldade para iniciar a partida conectando ao servidor.
	 */
	public static void iniciarJogo() {
		while (true) {
			int opcao = 0;
			opcao = menuInicial();
			if (opcao != JOGAR) { // sair do jogo
				break;
			} else { // jogar
				gerenciarPartida();
			}
		}
		System.out.println("------Obrigado por Jogar! Até Mais!------");
	}

	/**
	 * Gerencia o fluxo sequencial da partida:
	 * 1 - Seleciona Dificuldade.
	 * 2 - Inicia a conexão com o servidor.
	 * 3 - Envia o nivel de dificuldade escolhido para o serviddor.
	 * 4 - Executa o fluxo das trocas de perguntas e respostas entre cliente/servidor.
	 * 5 - Recebe o resultado para mostrar ao usuário.
	 * 6 - Finaliza a conexão com o servidor
	 */
	private static void gerenciarPartida() {
		selecionarDificuldade();
		Data.conectarServidor(Data.porta1);
		System.out.println("Conectado com sucesso!");
		Data.enviarDados(dificuldade);
		fazerPerguntas();
		resultado();
		Data.desconectarServidor();
	}

	/**
	 * Recebe os dados com o resultado das perguntas e mostra
	 * em tela para o usuário.
	 */
	private static void resultado(){
		String resultado = Data.receberDados();
		while (resultado.equals("ERRO: Slot Ocupado") || resultado.equals("ERRO")) {
			try {
				Thread.sleep(1500l); //da uma folga no looping pra poder acompanhar as tentativas...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Data.enviarDados(dificuldade);
			resultado = Data.receberDados();
		}
		
		System.out.println("Fim das perguntas, total de pontos: " + resultado);
		System.out.println("Retornando para o menu inicial...");
	}
	
	/**
	 * Executa o looping de perguntas e respostas entre o cliente e o servidor.
	 * Solicita a pergunta ao servidor, recebe e disponibiliza para o usuário responser.
	 * Por fim valida com o servidor se a resposta selecionada está correta e informa ao usuário.
	 */
	private static void fazerPerguntas() {
		for (int i = 0; i < 3; i++) {
			String[] pergunta = receberPerguntaDoServidor();
			mostrarPerguntaEOpcoes(pergunta);
			String resposta = capturarRespostaDoUsuario(pergunta);
			validarResposta(resposta);
		}
	}

	/**
	 * Mostra na tela para o usuário a pergunta e as opções.
	 * @param pergunta
	 */
	private static void mostrarPerguntaEOpcoes(String[] pergunta) {
		System.out.println(pergunta[QUESTAO]);
		System.out.println("a - " + pergunta[OPCAO_A]);
		System.out.println("b - " + pergunta[OPCAO_B]);
		System.out.println("c - " + pergunta[OPCAO_C]);
	}

	/**
	 * Recebe a pergunta do servidor e quebra os dados em um array,
	 * separando a pergunta das opções no array pelo delimitador. 
	 * Também valida se não ocorreu um erro de falta de slot no servidor.
	 * Caso sim, ele fica em looping até conseguir um slot vago no servidor.
	 * @return os dados necessários para efetuar a pergunta ao usuário
	 */
	private static String[] receberPerguntaDoServidor() {
		String dados = Data.receberDados(); // recebe a quantidade de perguntas
		while (dados.equals("ERRO: Slot Ocupado") || dados.equals("ERRO") || dados.split(DELIMITADOR).length == 1) {
			try {
				Thread.sleep(1500l); //da uma folga no looping pra poder acompanhar as tentativas...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Data.enviarDados(dificuldade);
			dados = Data.receberDados();
		}
		String[] pergunta = dados.split(DELIMITADOR);
		return pergunta;
	}

	/**
	 * Envia para o servidor a resposta selecionada e aguarda
	 * pelo retorno do servidor, mostrando na tela
	 * a mensagem de sucesso ou erro retornada.
	 * @param resposta
	 */
	private static void validarResposta(String resposta) {
		Data.enviarDados(resposta);
		String resultado = Data.receberDados();
		while (resultado.equals("ERRO: Slot Ocupado") || resultado.equals("ERRO")) {
			try {
				Thread.sleep(1500l); //da uma folga no looping pra poder acompanhar as tentativas...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Data.enviarDados(dificuldade);
			resultado = Data.receberDados();
		}
		System.out.println(resultado);
	}

	/**
	 * Captura e retorna a resposta que o usuario escolheu no terminal.
	 * Recebe o array com os dados da pergunta e valida se o usuário inseriu
	 * uma opção valida. Solicita que o usuário insira uma opção correta caso 
	 * ele tenha inserido uma opção inválida.
	 * @param pergunta
	 * @return resposta selecionada.
	 */
	private static String capturarRespostaDoUsuario(String[] pergunta) {
		Scanner scanner = new Scanner(System.in);
		String resposta = scanner.next();
		while (true) {
			if (resposta.equals("a")) {
				return pergunta[OPCAO_A];
			} else if (resposta.equals("b")) {
				return pergunta[OPCAO_B];
			} else if (resposta.equals("c")) {
				return pergunta[OPCAO_C];
			} else {
				System.out.println("Opção incorreta, escolha 'a' 'b' ou 'c'.");
				resposta = scanner.next();
			}
		}
	}

	/**
	 * Monta no terminal o menu inicial ao iniciar a aplicação.
	 * Aguarda o usuário escolher jogar ou sair para seguir o fluxo da
	 * aplicação.
	 * @return opção escolhida.
	 */
	private static int menuInicial() {
		System.out.println("------Bem Vindo------");
		System.out.println("1 - Jogar");
		System.out.println("2 - Sair");
		Scanner scanner = new Scanner(System.in);
		int opcao = scanner.nextInt();
		return opcao;
	}

	/**
	 * Solicita ao usuário a dificuldade que ele quer jogar, aguarda ele 
	 * informar e armazena a difuldade escolhida.
	 * @return dificuldade selecionada
	 */
	private static void selecionarDificuldade() {
		System.out.println("Informe o nível de dificuldade (1 - Normal, 2 - Dificil)");
		Scanner scanner = new Scanner(System.in);
		dificuldade = scanner.next();	
	}

	public static void main(String args[]) throws Exception {
		iniciarJogo();
	}

}
