import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class Data {

	private static DatagramSocket clientSocket;
	private final static String endereco = "localhost";
	private final static Integer porta = 50000;

	protected static void conectarServidor() {
		try {
			System.out.println("Conectando ao jogo para buscar as perguntas...");
			clientSocket.connect(InetAddress.getByName(endereco), porta);
			System.out.println("Conectado com sucesso!");
		} catch (UnknownHostException e) {
			System.out.println("Erro ao conectar no servidor!");
			// TODO: Tratativa para erro de conexão
			e.printStackTrace();
		}
	}

	protected static void desconectarServidor() {
		clientSocket.disconnect();
	}

	protected static void iniciarSocket() throws SocketException {
		clientSocket = new DatagramSocket();
	}

	protected static void fecharSocket() {
		clientSocket.close();
	}

	protected static void enviarDados(String dados) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(dados.getBytes(), dados.getBytes().length);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO: Tratativa para erro de envio/conexão
			e.printStackTrace();
		}
	}

	protected static String receberDados() {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receiveDatagram);
			return new String(receiveDatagram.getData());
		} catch (IOException e) {
			// TODO: Tratativa para erro de envio/conexão
			e.printStackTrace();
			return null;
		}
	}

}
