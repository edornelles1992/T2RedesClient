import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class Data {

	protected static DatagramSocket clientSocket;

	protected static void conectarServidor() throws IOException {
		clientSocket.connect(InetAddress.getByName("localhost"), 50000);
		String acessoConexao = "123456";
		DatagramPacket sendPacket = new DatagramPacket(acessoConexao.getBytes(), acessoConexao.length());
		clientSocket.send(sendPacket);
	}
	
	protected static void desconectarServidor() {
		clientSocket.disconnect();
	}

	protected static void fecharSocket() {
		clientSocket.close();
	}

	protected static void enviarDados(byte[] datas) {

	}

	protected static byte[] receberDados() {
		return null;
	}
	
	protected static void iniciarSocket() throws SocketException {	
		clientSocket = new DatagramSocket();
	}
}
