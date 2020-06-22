import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * UI simples com um botão para submeter o arquivo, label para informar o md5sum
 * e um texto informando o arquivo enviado.
 */
public class ClientUploadUI extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	static private final String newline = "\n";
	JButton openButton;
	JLabel labelHash;
	JTextArea log;
	JFileChooser fc;

	public ClientUploadUI() {
		super(new BorderLayout());

		// Create the log first, because the action listeners
		// need to refer to it.
		log = new JTextArea(6, 30);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);
		// Create a file chooser
		fc = new JFileChooser();

		openButton = new JButton("Selecionar Arquivo..");
		openButton.addActionListener(this);

		labelHash = new JLabel("Hash");
		labelHash.setText("md5Sum: ");
		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(openButton);
		buttonPanel.add(labelHash);

		// Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(labelHash, BorderLayout.CENTER);
		add(logScrollPane, BorderLayout.PAGE_END);
	}

	public void actionPerformed(ActionEvent e) {
		// Handle open button action.
		if (e.getSource() == openButton) {
			int returnVal = fc.showOpenDialog(ClientUploadUI.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				// This is where a real application would open the file.
				log.append("Arquivo Selecionado: " + file.getName() + "." + newline);
				UDPClient.enviarArquivo(file); // Pega o arquivo selecionado e envia para UDPClient enviar para o
												// servidor...
				labelHash.setText("md5Sum: " + UDPClient.arquivoHashEnviado);
			} else {
				log.append("Comando de abrir cancelado." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
			// Handle save button action.
		}
	}

	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = ClientUploadUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Upload de Arquivo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new ClientUploadUI());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

}
