import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
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
		System.out.println("Conectando ao jogo para buscar as perguntas...");
		Data.iniciarSocket();
		Data.conectarServidor();
		System.out.println("Conectado!");
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

	public static void testa() throws IOException {
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// declara socket cliente
		DatagramSocket clientSocket = new DatagramSocket();

		// obtem endere�o IP do servidor com o DNS
		InetAddress IPAddress = InetAddress.getByName("localhost");

		// File file = new File("documento1500.txt");
		File file = new File("documento10000.txt");

		byte[] documento = Files.readAllBytes(file.toPath());

		// cria pacote com o dado, o endere�o do server e porta do servidor
		DatagramPacket sendPacket = new DatagramPacket(documento, documento.length, IPAddress, 50000);

		// envia o pacote
		clientSocket.send(sendPacket);
		System.out.println("Arquivo enviado com sucesso!");
		// fecha o cliente
		clientSocket.close();
	}
}
