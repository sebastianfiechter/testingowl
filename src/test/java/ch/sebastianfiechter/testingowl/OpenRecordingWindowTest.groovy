package ch.sebastianfiechter.testingowl;

import ch.sebastianfiechter.testingowl.Issues.IssueType
import static org.junit.Assert.*;

import java.awt.event.ActionEvent
import javax.swing.JFrame

import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class OpenRecordingWindowTest {

	@Autowired
	ProcessRecordingWindow window
	
	@Test
	public void testHide() {
		window.showOpen("C:/filepath/filepath");
		
		sleep 1000
		
		window.setProgressValue(5)
		
		sleep 1000
		
		window.setProgressValue(90)
		
		sleep 1000
		
		window.hide()
		
	}
	
}
