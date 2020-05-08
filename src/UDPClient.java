import java.util.Scanner;

public class UDPClient extends Data implements Opcoes {

	public static void iniciarJogo() throws Exception {
		while (true) {
			int opcao = 0;
			opcao = menuInicial();
			if (opcao != JOGAR) { // sair do jogo
				break;
			} else { // jogar
				iniciarPartida(selecionarDificuldade());
			}
		}
		System.out.println("------Obrigado por Jogar! Até Mais!------");
	}

	private static void iniciarPartida(String dificuldade) throws Exception {
		Data.iniciarSocket();
		Data.conectarServidor();
		Data.enviarDados(dificuldade);
		fazerPerguntas();
	}

	private static void fazerPerguntas() {
		for (int i = 0; i < 3; i++) {
			String[] pergunta = solicitarPerguntaAoServidor();
			mostrarPerguntaEOpcoes(pergunta);
			String resposta = capturarRespostaDoUsuario(pergunta);
			validarResposta(resposta);
		}
	}

	private static void mostrarPerguntaEOpcoes(String[] pergunta) {
		System.out.println(pergunta[QUESTAO]);
		System.out.println("a - " + pergunta[OPCAO_A]);
		System.out.println("b - " + pergunta[OPCAO_B]);
		System.out.println("c - " + pergunta[OPCAO_C]);
	}

	private static String[] solicitarPerguntaAoServidor() {
		String dados = Data.receberDados(); // recebe a quantidade de perguntas
		String[] pergunta = dados.split(DELIMITADOR);
		return pergunta;
	}

	private static void validarResposta(String resposta) {
		Data.enviarDados(resposta);
		String resultado = Data.receberDados();
		System.out.println(resultado);
	}

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

	private static int menuInicial() {
		System.out.println("------Bem Vindo------");
		System.out.println("1 - Jogar");
		System.out.println("2 - Sair");
		Scanner scanner = new Scanner(System.in);
		int opcao = scanner.nextInt();
		return opcao;
	}

	private static String selecionarDificuldade() {
		System.out.println("Informe o nível de dificuldade (1 - Normal, 2 - Dificil)");
		Scanner scanner = new Scanner(System.in);
		String dificuldade = scanner.next();
		System.out.println(dificuldade);
		return dificuldade;
	}

	public static void main(String args[]) throws Exception {
		iniciarJogo();
	}

}
