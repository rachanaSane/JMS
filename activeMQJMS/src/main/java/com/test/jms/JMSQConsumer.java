

/**
 * Created by benko on 21/05/2015.
 */
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class JMSQConsumer {

    // Connection details
    private static final String BINDING = "jms/RemoteConnectionFactory";
    private static final String THIS_INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String THIS_PROVIDER_URL = "remote://10.130.0.68:4448";
    private static final String THIS_SECURITY_CREDENTIALS = "testpassword";
    private static final String THIS_SECURITY_PRINCIPAL = "fluxq";
    private static final String THIS_URL_PKG_PREFIXES = "org.jboss.naming.remote.client";

    // Queue details
    private static final String JMS_JNDI_QUEUE = "jms/queue/bridge";

    // Example of posting a FLUX TL JMS message under WildFly AS 7.2.0 platform, with HornetQ integrated
    public static void main(String[] args) throws Exception {
        ConnectionFactory cf ;
        Connection connection = null;
        Message recvdMsg;
        TextMessage txtMsg;
        String businessID;
        int msg_cntr = 0;

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.PROVIDER_URL,THIS_PROVIDER_URL);
        env.put(Context.INITIAL_CONTEXT_FACTORY, THIS_INITIAL_CONTEXT_FACTORY);
        env.put(Context.URL_PKG_PREFIXES, THIS_URL_PKG_PREFIXES);
        env.put(Context.SECURITY_PRINCIPAL, THIS_SECURITY_PRINCIPAL);
        env.put(Context.SECURITY_CREDENTIALS, THIS_SECURITY_CREDENTIALS);

        Context ic = new InitialContext(env);
        try {
            cf = (ConnectionFactory) ic.lookup(BINDING);

            Queue queue = (Queue) ic.lookup(JMS_JNDI_QUEUE);

            connection = cf.createConnection(THIS_SECURITY_PRINCIPAL, THIS_SECURITY_CREDENTIALS);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(queue);

            connection.start();

            // Read a first available message
            recvdMsg = consumer.receiveNoWait();

            while (recvdMsg != null) {
                if (recvdMsg instanceof TextMessage) {
                    txtMsg = (TextMessage) recvdMsg;

                    // Printout the ID of the message
                    businessID = txtMsg.getStringProperty("BUSINESS_UUID");
                    if (businessID == null) businessID = "ID N/A";
                    if (businessID.equals("null")) businessID = "ID N/A";
                    System.out.println("Received message : " + businessID);
                    msg_cntr++;
                }

                // Read the next available message
                recvdMsg = consumer.receiveNoWait();
            }

            System.out.println("Total number of messages received : " + new Integer(msg_cntr).toString());

            // Close the connection
            connection.close(); connection = null;
            ic.close();ic = null;
        } finally {

            // ALWAYS reconfirm closing the connection and a context in a finally block to avoid leaks.
            // Closing the connection also takes care of closing its related objects e.g. sessions.
            if (ic != null) {
                try {
                    ic.close();
                } catch (Exception e) {
                    throw e;
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }
}
