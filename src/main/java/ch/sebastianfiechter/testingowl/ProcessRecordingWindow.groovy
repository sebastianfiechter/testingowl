package ch.sebastianfiechter.testingowl

import java.awt.BorderLayout

import javax.swing.*

import java.awt.*
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener
import org.springframework.web.util.UriUtils

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.player.JPlayer
import com.wet.wired.jsr.recorder.JRecorder
import groovy.util.logging.*

@Slf4j
@Component
class ProcessRecordingWindow {

	@Autowired
	OwlIcons owl
	
	@Autowired
	JPlayer player
	
	@Autowired
	JRecorder recorder

	JDialog dialog
	JFrame parent

	JProgressBar progressBar

	def synchronized setProgressValue(int val) {
		progressBar.setValue(val)
	}
	
	def showOpen(int progressValue=0, int progressMaxValue=100, String filePath) {
		show(progressValue, progressMaxValue, filePath, "Opening recording from:", player);
	}
	
	def showSavingInPlayer(int progressValue=0, int progressMaxValue=100, String filePath) {
		show(progressValue, progressMaxValue, filePath, "Saving recording to:", player);
	}
	
	def showSavingInRecorder(int progressValue=0, int progressMaxValue=100, String filePath) {
		show(progressValue, progressMaxValue, filePath, "Saving recording to:", recorder);
	}

	def show(int progressValue=0, int progressMaxValue=100, String filePath, String messag, JFrame parentFrame) {
		
		log.info("will show processing dialog")
		parent = parentFrame;
		parent.setEnabled(false);
		
		JOptionPane optionPane = new JOptionPane("TestingOwl Please wait...",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
				null, new Object[0], null);

		def message = "${messag}<BR>${filePath}<BR>"
			
		JLabel label = new JLabel("<html><body><center>${message}</center></body></html>", 
			SwingConstants.CENTER);
		progressBar = new JProgressBar(0, progressMaxValue)
		progressBar.setValue(progressValue)
		progressBar.setStringPainted(true)
		

		Object[] complexMsg = [label, progressBar];
		optionPane.setMessage(complexMsg);

		dialog = new JDialog(parent, false)
		dialog.setAlwaysOnTop(true)
		dialog.setUndecorated(true)

		dialog.getContentPane().setBorder(BorderFactory.createRaisedBevelBorder())
		dialog.setLayout(new BorderLayout());
		dialog.add(optionPane);

		dialog.pack();
		dialog.setLocationRelativeTo(null);
		
		dialog.setVisible(true)
	}

	def hide() {
		//ensure user sees 100% value
		progressBar.value = progressBar.maximum
		sleep 1000
		
		dialog.setVisible(false)
		parent.setEnabled(true)
	}

}
