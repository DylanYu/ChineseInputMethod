package origin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class InputGUI extends JFrame implements Def{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel 		jlblTitle 		= 	new JLabel("please input:");
	private JTextArea 	jtaInputArea 	= 	new JTextArea();
	private JLabel 		jlblInput 		= 	new JLabel("input");
	private JLabel 		jlblSelect 		= 	new JLabel("select");

	private void processInput(char ch) {
		Inputer inputer = Inputer.getInputer();
		inputer.process(ch);
		jlblInput.setText(inputer.getInputStr_().toString());
		jlblSelect.setText(inputer.getSelectiveListInfo());
		//press enter to input English
		if(inputer.isEnglish()){
			StringBuffer text = new StringBuffer(jtaInputArea.getText());
			text.delete(text.length() - 1, text.length());
			jtaInputArea.setText(text.toString());
			inputer.setIsEnglishFlase();
		}
		else{
			StringBuffer text = new StringBuffer(jtaInputArea.getText());
			if(inputer.getDeleteLen() > 1 && inputer.getState_() == INITATE) {
				if(text.length() - inputer.getDeleteLen() >= 0){
					text.delete(text.length() - inputer.getDeleteLen(), text.length());
					text.append(inputer.getOutput_());
					jtaInputArea.setText(text.toString());
				}
			}
		}
	}

	public InputGUI() {
		this.jtaInputArea.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char ch = e.getKeyChar();
				processInput(ch);
			}
		});
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		jp.add(jlblTitle, BorderLayout.NORTH);
		jp.add(jtaInputArea, BorderLayout.CENTER);
		JPanel jp2 = new JPanel();
		jp2.setLayout(new GridLayout(2,1));
		jp2.add(jlblInput);
		jp2.add(jlblSelect);
		setLayout(new BorderLayout());
		add(jp, BorderLayout.CENTER);
		add(jp2, BorderLayout.SOUTH);
		this.setTitle("Chinese Input");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setSize(600, 300);
	}

	public static void main(String[] args) {
		new InputGUI();
	}
}
