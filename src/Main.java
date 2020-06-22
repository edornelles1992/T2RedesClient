import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.Scanner;

public class Main implements Parametros {

	/**
	 * Inicia a aplicação criando a interface do usuário
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				ClientUploadUI.createAndShowGUI();
			}
		});
	}
}
