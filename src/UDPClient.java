import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;

public class UDPClient extends Data {

	public static void enviarArquivo(File file) {
		try {	
			Data.conectarServidor();
			byte[] documento = Files.readAllBytes(file.toPath());	
			//TODO: quebrar arquivo em pacotes (512 -> Data.dataSize)
			Pacote pacote = new Pacote();
		//	pacote.dados = documento;
			Data.enviarDados(pacote);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
