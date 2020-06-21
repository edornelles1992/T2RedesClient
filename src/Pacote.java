import java.io.Serializable;

/**
 * Pacote com 512 bytes divididos entre seus atributos dados = 512 - (4+4+4+4+8)
 * = 488
 */
public class Pacote implements Serializable {
	public byte[] dados; // 488bytes dados
	public int size; // 4 bytes -> Tamanho da area de dados
	public int seq; // 4 bytes -> Número sequencial
	public int ack; // 4 bytes
	public int ultimo; // 4bytes -> 0 false 1 true // ultimo pacote
	public long valor_crc;// 8 bytes

	public byte[] getDados() {
		return dados;
	}

	public void setDados(byte[] dados) {
		this.dados = dados;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getUltimo() {
		return ultimo;
	}

	public void setUltimo(int ultimo) {
		this.ultimo = ultimo;
	}

	public long getValor_crc() {
		return valor_crc;
	}

	public void setValor_crc(long valor_crc) {
		this.valor_crc = valor_crc;
	}
}
