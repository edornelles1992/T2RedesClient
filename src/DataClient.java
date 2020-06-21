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
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Classe contendo os mï¿½todos de manipulaï¿½ï¿½o do socket e dos datagrams no
 * lado do CLIENTE
 */
public abstract class DataClient implements Parametros {

	private static DatagramSocket clientSocket;
	private final static String endereco = "localhost";
	protected final static Integer porta1 = 50000;
	private static Integer timeout = 1500;
	protected static int pacoteFalhadoIndex = -1;
	protected static int ackAcumulado = 1;
	protected static int ackDuplicadoIndex = -1;

	/**
	 * Cria a conexï¿½o do socket com base no endereco e porta configurados.
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
	 * Fecha a conexï¿½o e o socket.
	 */
	protected static void desconectarServidor() {
		System.out.println("Desconectando da partida...");
		clientSocket.close();
		clientSocket.disconnect();
		System.out.println("Desconectando com sucesso!");
	}

	/**
	 * Inicia o socket atribuindo um limite de tempo (timeout para receber dados.
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
			System.out.println("Houve um problema na comunicaï¿½ï¿½o com o servidor...");
			System.out.println("Tentando restabelecer a conexï¿½o...");
			enviarDados(pacote);
		}
	}

	protected static Pacote receberACK(int pacoteIndex, Pacote pacote) throws IOException {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receiveDatagram);
			byte[] recBytes = receiveDatagram.getData();
			Pacote pacoteAck = byteArrayToPacote(recBytes);
			pacoteFalhadoIndex = -1;
			return pacoteAck;
		} catch (IOException e) {
			pacoteFalhadoIndex = pacoteIndex;
			System.out.println("timeout!! reiniciando slowStart...");
			throw new IOException("timeout!! reiniciando slowStart...");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static int enviarPacotes(List<Pacote> pacotes, int index, int amount, int ackAtual) throws IOException {

		int size = index + amount;
		if (size >= pacotes.size()) {
			size = pacotes.size();
		}

		for (int i = index; i < size; i++) {
			pacotes.get(i).seq = i;
			DataClient.enviarDados(pacotes.get(i));
			Pacote pacoteResposta = DataClient.receberACK(i, pacotes.get(i)); // aguarda ack de confirmação
			System.out.println("SEQ: " + pacotes.get(i).seq + ", ACK: " + pacoteResposta.ack);
			ackAtual = validaAckRecebido(ackAtual, i, pacoteResposta);
		}
		
		System.out.println("--------");
		return ackAtual;
	}

	private static int validaAckRecebido(int ackAtual, int i, Pacote pacoteResposta) throws IOException {
		if (i == 0) { // pega o primeiro ack retornado para comparação
			ackAtual = pacoteResposta.ack;
		} else if (ackAtual == pacoteResposta.ack) { // ack duplicado
			ackAcumulado++;
			if (ackAcumulado == 3) { // ack ocorreu 3 vezes
				System.out.println("3 Acks duplicados! reiniciando slowStart...");
				ackDuplicadoIndex = i;
				throw new IOException("3 Acks duplicados! reiniciando slowStart...");
			}
		} else { // ack diferente, reinicia o contador e armazena o prox ack
			ackAcumulado = 1;
			ackDuplicadoIndex = -1;
			ackAtual = pacoteResposta.ack;
		}
		return ackAtual;
	}

	/**
	 * Converte o objeto pacote para um byteArray
	 * 
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
	 * 
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

}
