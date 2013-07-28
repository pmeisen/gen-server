package net.meisen.general.server.control;

import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.server.Server;
import net.meisen.general.server.api.IControlMessagesManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@RunWith(JUnitConfigurationRunner.class)
@ContextClass(Server.class)
public class TestDefaultControlMessagesManager {

	@Autowired
	@Qualifier("controlMessagesManager")
	private IControlMessagesManager controlMessagesManager;
	
	@Test
	public void test() {
		
		System.out.println(controlMessagesManager);
		
	}
}
