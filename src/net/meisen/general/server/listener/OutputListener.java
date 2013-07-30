package net.meisen.general.server.listener;

import java.io.PrintStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.genmisc.types.Classes;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.server.api.impl.BaseListener;
import net.meisen.general.server.exceptions.OutputListenerException;
import net.meisen.general.server.settings.pojos.Connector;
import net.meisen.general.server.settings.pojos.Extension;

/**
 * Listener which sends the
 * 
 * @author pmeisen
 * 
 */
public class OutputListener extends BaseListener {

	/**
	 * The name of this <code>Listener</code> used when defined it, instead of the
	 * class-name
	 */
	public static final String NAME = "OUTPUT";

	private PrintStream out = System.out;

	@Autowired
	@Qualifier("exceptionRegistry")
	private IExceptionRegistry exceptionRegistry;

	@Autowired
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	@Override
	public void initialize(final Connector c) {
		super.initialize(c);

		// check if we have a special PrintStream
		final Extension output = c.getExtension("output");

		String value;
		if (output == null) {
			// do nothing
		} else if ((value = output.<String> getProperty("class")) != null) {

			// get the class
			final Class<?> clazz = Classes.getClass(value);
			if (clazz != null && PrintStream.class.isAssignableFrom(clazz)) {
				try {
					setPrintStream((PrintStream) clazz.newInstance());
				} catch (final Exception e) {
					exceptionRegistry.throwException(OutputListenerException.class, 1000,
							e, clazz.getName());
				}
			} else {
				exceptionRegistry.throwException(OutputListenerException.class, 1001,
						value);
			}
		} else if ((value = output.<String> getProperty("ref")) != null) {

			// get the defined module
			final Object instance = configuration.getModule(value);
			if (instance == null || instance instanceof PrintStream == false) {
				exceptionRegistry.throwException(OutputListenerException.class, 1002,
						value);
			} else {
				setPrintStream((PrintStream) instance);
			}
		}
	}

	/**
	 * Defines the <code>PrintStream</code> to write the received messages to.
	 * 
	 * @param out
	 *          the <code>PrintStream</code> to write the received messages to
	 */
	public void setPrintStream(final PrintStream out) {
		this.out = out;
	}

	@Override
	protected String handleInput(final String input) {
		if (input != null) {
			out.println(input);
		}

		return "";
	}

	@Override
	public String toString() {
		return NAME + (getPort() == -1 ? "" : " (" + getPort() + ")");
	}
}
