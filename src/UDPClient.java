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
		//TODO: tratar as perguntas
		String dados = Data.receberDados();
		String[] pergunta = dados.split("#");
		System.out.println(pergunta[0]);

		System.out.println("a - " + pergunta[1]);

		System.out.println("b - " + pergunta[2]);

		System.out.println("c - " + pergunta[3]);
	}

	public static int menuInicial() {
		System.out.println("------Bem Vindo------");
		System.out.println("1 - Jogar");
		System.out.println("2 - Sair");
		Scanner scanner = new Scanner(System.in);
		int opcao = scanner.nextInt();
		return opcao;
	}

	public static String selecionarDificuldade() {
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
