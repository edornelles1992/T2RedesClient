import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class UDPClient extends DataClient {

	public static void enviarArquivo(File file) {
		try {
			DataClient.conectarServidor();
			byte[] documento = Files.readAllBytes(file.toPath());
			List<Pacote> pacotes = quebrarArquivo(documento);

			slowStart(pacotes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void slowStart(List<Pacote> pacotes) {
		int initialValue = 1;
		int index = 1;

		//enviar primeiro pacote
		enviarPacotes(pacotes, 0, 1);

		while (index < pacotes.size()) {
			int numeroDePacotes = (int) Math.pow(2, initialValue);

			enviarPacotes(pacotes, index, numeroDePacotes);

			index = index + numeroDePacotes;

			initialValue++;
		}
	}

	private static void enviarPacotes(List<Pacote> pacotes, int index, int amount) {

		int size = index + amount;
		if (size >= pacotes.size()) {
			size = pacotes.size();
		}

		for (int i = index; i < size; i++) {
			pacotes.get(i).seq = i;
			DataClient.enviarDados(pacotes.get(i));

			Pacote pacoteResposta = DataClient.receberDados();

			System.out.println("SEQ: " + pacotes.get(i).seq + ", ACK: " + pacoteResposta.ack);

		}

		System.out.println("--------");
	}

	/**
	 * Quebra o arquivo em pacotes criando uma lista de Pacotes
	 */
	private static List<Pacote> quebrarArquivo(byte[] documento) {
		ArrayList<Pacote> lista = new ArrayList<>();
		for (int i = 0; i < documento.length; i += DataClient.dataSize) {
			Pacote pacote = new Pacote();
			byte[] parte;
			if ((i + DataClient.dataSize) > documento.length) { // tamanho parte final > dataSize
				parte = pegarParteDados(i, documento.length, documento);
				pacote.size = documento.length - i;
				pacote.ultimo = 1;
			} else {
				parte = pegarParteDados(i, i + DataClient.dataSize, documento);
				pacote.ultimo = 0;
				pacote.size = DataClient.dataSize;
			}
			pacote.dados = parte;
			lista.add(pacote);
		}
		return lista;
	}

	/**
	 * Pega a parte selecionada do array e passa para um novo array separado com o
	 * tamanho exato daquela parte.
	 */
	private static byte[] pegarParteDados(int posIni, int posFim, byte[] documento) {
		byte[] dados = new byte[posFim - posIni];
		int cont = posIni;
		for (int i = 0; i < (posFim - posIni); i++) {
			dados[i] = documento[cont];
			cont++;
		}
		return dados;
	}
	
}
