package ch.sebastianfiechter.testingowl;

import ch.sebastianfiechter.testingowl.Issues.IssueType
import static org.junit.Assert.*;

import java.awt.event.ActionEvent

import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class InProgressWindowTest {

	@Autowired
	InProgressWindow window
	

	
	@Test
	public void testHide() {
		window.show("Saving, please wait...");
		
		sleep 10000
		
		window.setProgressValue(5)
		
		sleep 1000
		
		window.setProgressValue(90)
		
		sleep 1000
		
		window.hide()
		
	}
	
	@Test
	public void testWaitForConfirm() {
		window.show("Saving, please wait...");
		
		sleep 1000
		
		window.setProgressValue(5)
		
		sleep 1000
		
		window.setProgressValue(90)
		
		sleep 1000
		
		window.waitForConfirm()
		println ("returned from dialog")
		
		sleep 10000
		
	}

}
