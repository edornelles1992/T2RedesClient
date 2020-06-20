import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Classe contendo os m�todos de manipula��o do socket e dos
 * datagrams no lado do CLIENTE
 */
public abstract class DataClient implements Parametros {

	private static DatagramSocket clientSocket;
	private final static String endereco = "localhost";
	protected final static Integer porta1 = 50000;
	private static Integer timeout = 1500;

	/**
	 * Cria a conex�o do socket com base no endereco e porta configurados.
	 */
	protected static void conectarServidor() {
		try {
			iniciarSocket();
			clientSocket.connect(InetAddress.getByName(endereco), porta1);
		} catch (UnknownHostException e) {
			System.out.println("Erro ao conectar no servidor!");
			System.out.println("Tentando conectar novamente...");
			conectarServidor();
		}
	}

	/**
	 * Fecha a conex�o e o socket.
	 */
	protected static void desconectarServidor() {
		System.out.println("Desconectando da partida...");
		clientSocket.close();
		clientSocket.disconnect();
		System.out.println("Desconectando com sucesso!");
	}

	/**
	 * Inicia o socket atribuindo um limite de tempo (timeout
	 * para receber dados.
	 */
	private static void iniciarSocket() {
		try {
			clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(timeout);
		} catch (SocketException e) {
			System.out.println("Erro ao iniciar socket");
			e.printStackTrace();
		}
	}


	protected static void enviarDados(Pacote pacote) {
		try {
			pacote.valor_crc = calcularCRC32DoPacote(pacote);
			byte[] serialized = pacoteToByteArray(pacote);
			DatagramPacket sendPacket = new DatagramPacket(serialized, serialized.length);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("Houve um problema na comunica��o com o servidor...");
			System.out.println("Tentando restabelecer a conex�o...");
			enviarDados(pacote);
		}
	}

	protected static Pacote receberDados() {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receiveDatagram);
			byte[] recBytes = receiveDatagram.getData();
			Pacote pacote  = byteArrayToPacote(recBytes);
			return pacote;
		} catch (IOException e) {
			return receberDados();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Converte o objeto pacote para um byteArray
	 * @param pacote
	 * @return
	 */
	private static byte[] pacoteToByteArray(Pacote pacote) {
		try {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream);
		oo.writeObject(pacote);
		oo.close();
		return bStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Converte de byteArray para o objeto pacote.
	 * @param pacote
	 * @return
	 */
	private static Pacote byteArrayToPacote(byte[] pacote) {
		try {
			ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(pacote));
			Pacote pacoteObj = (Pacote) iStream.readObject();
			iStream.close();
			return pacoteObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected static long calcularCRC32DoPacote(Pacote pacote) {
		try {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream);
		oo.writeObject(pacote);
		oo.close();
		byte[] serialized = bStream.toByteArray();
		Checksum checksum = new CRC32();
		// update the current checksum with the specified array of bytes
		checksum.update(pacote.dados, 0, pacote.dados.length);		 
		// get the current checksum value
		long checksumValue = checksum.getValue();	 		
		return checksumValue;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
 	}

	private static String tratarResposta(String dados) {
		if (dados.equals("ERRO: Slot Ocupado")) {
		//	conectarServidor(clientSocket.getPort() == porta1 ? porta2 : porta1); //tenta conectar no outro slot
			System.out.println("Procurando vaga para continuar...");
			return dados; //retorna o erro para poder enviar a solicita��o novamente.
		} else {
			return dados; //retorna os dados recebidos
		}
	}

}