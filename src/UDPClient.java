import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class UDPClient extends Data {

	public static void enviarArquivo(File file) {
		try {
			Data.conectarServidor();
			byte[] documento = Files.readAllBytes(file.toPath());
			if (documento.length > Data.dataSize) {
				List pacotes = quebrarArquivo(documento);
			} else {
				Pacote pacote = new Pacote();
				pacote.dados = documento;
				Data.enviarDados(pacote);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Quebra o arquivo em pacotes criando uma lista de Pacotes
	 */
	private static List<Pacote> quebrarArquivo(byte[] documento) {
		Integer qtdPacotes = arredondarCima(documento);
		ArrayList<Pacote> lista = new ArrayList<>();
		for (int i = 0; i < documento.length; i += Data.dataSize) {
			Pacote pacote = new Pacote();
			byte[] parte;
			if ((i + Data.dataSize) > documento.length) { //tamanho parte final > dataSize
				parte = pegarParteDados(i, documento.length, documento);
				pacote.size = documento.length - i;
				pacote.ultimo = 1;
			} else {
				parte = pegarParteDados(i, i + Data.dataSize, documento);
				pacote.ultimo = 0;
				pacote.size = Data.dataSize;
			}
			pacote.dados = parte;
			lista.add(pacote);
		}
		return lista;
	}

	/**
	 * Pega a parte selecionada do array e passa para um novo array separado com o tamanho
	 * exato daquela parte.
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
	
	private static Integer arredondarCima(byte[] documento) {
		double qtd = documento.length / new Double(Data.dataSize);		
		Integer qtdPacotes = new Integer(new BigDecimal(qtd).setScale(0, BigDecimal.ROUND_UP).toString());
		return qtdPacotes;
	}
}
