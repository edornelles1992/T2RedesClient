import java.io.Serializable;
/**
 * Pacote com 512 bytes divididos entre seus atributos
 */
public class Pacote implements Serializable {
	public byte[] dados; // 500bytes dados
	public int size; //4 bytes -> Tamanho da area de dados
	public int ack; //4 bytes
	public int ultimo; //4bytes -> 0 false 1 true // ultimo pacote
}
