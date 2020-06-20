import java.io.Serializable;
/**
 * Pacote com 512 bytes divididos entre seus atributos
 * dados = 512 - (4+4+4+4+8) = 488
 */
public class Pacote implements Serializable {
	public byte[] dados; // 488bytes dados
	public int size; //4 bytes -> Tamanho da area de dados
	public int seq; //4 bytes -> Número sequencial
	public int ack; //4 bytes
	public int ultimo; //4bytes -> 0 false 1 true // ultimo pacote
	public long valor_crc;// 8 bytes
}
