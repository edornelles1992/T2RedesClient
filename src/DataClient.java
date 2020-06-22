import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Classe contendo os métodos de manipulação do socket e dos datagrams no lado
 * do CLIENTE
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
	 * Cria a conexão do socket com base no endereço e porta configurados.
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
	 * Fecha a conexão com o socket.
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

	/**
	 * Método que envia o pacote de dados para o servidor. Antes de mandar ele
	 * realiza o calculo do crc e adiciona ao pacote para ser enviado.
	 */
	protected static void enviarDados(Pacote pacote) {
		try {
			pacote.valor_crc = calcularCRC32DoPacote(pacote);
			byte[] serialized = pacoteToByteArray(pacote);
			DatagramPacket sendPacket = new DatagramPacket(serialized, serialized.length);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("Houve um problema na comunicação com o servidor...");
			System.out.println("Tentando restabelecer a conexão...");
			enviarDados(pacote);
		}
	}

	/**
	 * Aguarda para receber o ACK de confirmação do servidor. Caso ocorra um timeout
	 * ele lança uma exceção que sera capturada posteriormente para reiniciar o
	 * algoritmo de slowstart, indicando apartir de qual pacote é para recomeçar.
	 */
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

	/**
	 * Método que envia uma lista de pacotes recebendo a lista e quais pacotes dela é pra ser enviado. Cada pacote enviado
	 * é aguardado seu ACK e posteriormente é validado para ver se esse ack já não recebido para tratar acks duplicados. 
	 */
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

	/**
	 * Trata os ACKs recebidos durante o envio de pacotes / recebimento de acks. A cada pacote valida se não ocorre
	 * um problema na transmissão gerando acks duplicados. Caso acumule 3, ele lança uma exceção para reiniciar o slowStart
	 * apartir da onde ocorreu o problema, caso não ele reinicia o acumulador de acks.
	 */
	private static int validaAckRecebido(int ackAtual, int i, Pacote pacoteResposta) throws IOException {
		if (i == 0) { // pega o primeiro ack retornado para compara��o
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

	/**
	 * Método que realiza o calculo do CRC dos dados do pacote e
	 * retorna o valor.
	 */
	protected static long calcularCRC32DoPacote(Pacote pacote) {
		try {
			Checksum checksum = new CRC32();
			checksum.update(pacote.dados, 0, pacote.dados.length);
			long checksumValue = checksum.getValue();
			return checksumValue;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Realiza o cálculo do md5sum baseando num string hash recebido
	 */
	public static String md5sum(String hash) {
		String s = hash;
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			return new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
