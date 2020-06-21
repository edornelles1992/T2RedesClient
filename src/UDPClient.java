import java.io.File;
import java.io.IOException;
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
		int expoente = 0;
		int index = 0;
		int ackAtual = 1;
		
		while (index < pacotes.size()) {
			int numeroDePacotes = (int) Math.pow(2, expoente);

			try {
				int ack = enviarPacotes(pacotes, index, numeroDePacotes, ackAtual);
				ackAtual = ack;
			} catch (IOException e) {
				// recomeça o slowStart caso tenha falhado alguma confirmação...
				if (e.getMessage().startsWith("timeout")) {// erro de timeout
					// recomeça apartir do pacote que falhou..
					index = pacoteFalhadoIndex;
					expoente = 0;
					continue;
				} else { //erro de 3 acks duplicados
					index = ackDuplicadoIndex;
					expoente = 0;
					continue;
				}
			}

			index = index + numeroDePacotes;
			expoente++;
		}
		System.out.println("----FIM DO ENVIO DO ARQUIVO----");
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
