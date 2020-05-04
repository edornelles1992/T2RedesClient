import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;

public class UDPClient {
	public static void main(String args[]) throws Exception {
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// declara socket cliente
		DatagramSocket clientSocket = new DatagramSocket();

		// obtem endere�o IP do servidor com o DNS
		InetAddress IPAddress = InetAddress.getByName("192.168.0.15");

	    //File file = new File("documento1500.txt");
	   File file = new File("documento10000.txt");

	    byte[] documento = Files.readAllBytes(file.toPath());

		// cria pacote com o dado, o endere�o do server e porta do servidor
		DatagramPacket sendPacket = new DatagramPacket(documento, documento.length, IPAddress, 50002);

		// envia o pacote
		clientSocket.send(sendPacket);
		System.out.println("Arquivo enviado com sucesso!");
		// fecha o cliente
		clientSocket.close();
	}
}
