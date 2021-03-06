package net.meisen.general.server.testutilities;

import net.meisen.general.server.Server;
import net.meisen.general.server.api.IServerSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.fail;

/**
 * Helper class for tests.
 *
 * @author pmeisen
 */
public class TestHelper {

    /**
     * Helper to get the <code>Server</code> instance created by the specified
     * <code>extension</code>.
     *
     * @param extension the spring configuration to be loaded
     * @return the <code>Server</code> instance created by the specified
     * <code>extension</code>
     */
    public static Server getServer(final String extension) {
        return Server.createServer(extension);
    }

    /**
     * Get the <code>IServerSettings</code> instance, created by the specified
     * <code>extension</code>.
     *
     * @param extension the spring configuration to be loaded
     * @return the <code>IServerSettings</code> instance created by the
     * specified <code>extension</code>
     */
    public static IServerSettings getSettings(final String extension) {
        final Server server = getServer(extension);
        return server.getServerSettings();
    }

    /**
     * Helper method to send a message to the specified <code>host</code> on the
     * specified <code>port</code>.
     *
     * @param msg  the message to be send
     * @param host the host to send to
     * @param port the port to send to
     * @return the answer received on the <code>Socket</code>
     */
    public static String sendMessage(final String msg, final String host,
                                     final int port) {
        try {
            final Socket socket = new Socket(host, port);
            final String rcv = sendMessage(msg, socket);

            // close the Socket
            socket.close();

            return rcv;
        } catch (final IOException e) {
            fail(e.getMessage());
        }

        fail("Reached something unreachable...");
        return null;
    }

    /**
     * Helper method to send a message to the specified <code>host</code> on the
     * specified <code>port</code>.
     *
     * @param msg    the message to be send
     * @param socket the socket to send to
     * @return the answer received on the <code>Socket</code>
     * @throws IOException if the data cannot be send
     */
    public static String sendMessage(final String msg, final Socket socket)
            throws IOException {

        final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        final BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // send the message
        out.println(msg);

        // get the answer - right now we don't care
        return in.readLine();
    }
}
