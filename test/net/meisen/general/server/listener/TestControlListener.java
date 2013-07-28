package net.meisen.general.server.listener;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.server.Server;
import net.meisen.general.server.control.ServerController;
import net.meisen.general.server.listener.control.ControlListener;
import net.meisen.general.server.settings.pojos.Connector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@RunWith(JUnitConfigurationRunner.class)
@ContextClass(Server.class)
public class TestControlListener {

	@Autowired(required = true)
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	@Test
	public void test() throws InterruptedException {
		final Connector conn = new Connector();
		conn.setEnable(true);
		conn.setId("TEST");
		conn.setListener(ControlListener.class.getName());
		conn.setPort(10000);

		ControlListener listener = configuration
				.createInstance(ControlListener.class);
		listener.initialize(conn);

		listener.open();

		ServerController controller = new ServerController();
		controller.lala(conn.getPort());

		ServerController controller2 = new ServerController();
		controller2.lala(conn.getPort());

		listener.close();

		Thread.sleep(10000);
	}
}
