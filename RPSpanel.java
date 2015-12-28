import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RPSpanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private String choice = null;
	private JTextArea textArea;
	
	public RPSpanel(){
		setLayout(new BorderLayout(10, 10));
		
		JPanel btnPanel = new JPanel();
		add(btnPanel, BorderLayout.NORTH);
		btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnRock = new JButton("Rock");
		btnRock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choice = "r";
				RockPaperSocketGUI.choiceMade();
			}});
		btnPanel.add(btnRock);
		
		JButton btnPaper = new JButton("Paper");
		btnPaper.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choice = "p";
				RockPaperSocketGUI.choiceMade();
			}});
		btnPanel.add(btnPaper);
		
		JButton btnScissors = new JButton("Scissors");
		btnScissors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choice = "s";
				RockPaperSocketGUI.choiceMade();
			}});
		btnPanel.add(btnScissors);
		
		textArea = new JTextArea();
		textArea.setText("\n\n\n\n\n\n\n");
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		add(textArea, BorderLayout.CENTER);
	}
	
	public void addLine(String s) {
		textArea.setText(textArea.getText() + s + "\n");
	}
	
	public void clearText() {
		textArea.setText("");
	}
	
	public String getChoice() {
		return choice;
	}
}
