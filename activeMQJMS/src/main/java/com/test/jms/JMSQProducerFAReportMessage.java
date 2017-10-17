

/**
 * Created by benko on 21/05/2015.
 */

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

public class JMSQProducerFAReportMessage {

    // Connection details
    private static final String BINDING = "jms/RemoteConnectionFactory";
    private static final String THIS_INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String THIS_PROVIDER_URL = "remote://10.155.0.10:8180";
    private static final String THIS_SECURITY_CREDENTIALS = "testpassword";
    private static final String THIS_SECURITY_PRINCIPAL = "fluxq";
    private static final String THIS_URL_PKG_PREFIXES = "org.jboss.naming.remote.client";

    // Queue details
    private static final String JMS_JNDI_QUEUE = "jms/queue/bridge";

    // Message details
    private static final int NUMBER_OF_MESSAGES = 10;
    private static final String CONNECTOR_ID = "JMS Business AP1";
    private static final String FLUX_ENV_AD = "BEL:ISOFT2";
    private static final String FLUX_ENV_DF = "urn:un:unece:uncefact:fisheries:FLUX:FA:EU:2";
    private static final int FLUX_ENV_TO = 60;

    // Business procedure signature
    private static final String BUSINESS_PROCEDURE_PREFIX = "BP";

    // Example of posting a FLUX TL JMS message under WildFly AS 7.2.0 platform, with HornetQ integrated
    public static void main(String[] args) throws Exception {
        System.out.println("start");
        ConnectionFactory cf;
        Connection connection = null;

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.PROVIDER_URL,THIS_PROVIDER_URL);
        env.put(Context.INITIAL_CONTEXT_FACTORY, THIS_INITIAL_CONTEXT_FACTORY);
        env.put(Context.URL_PKG_PREFIXES, THIS_URL_PKG_PREFIXES);
        env.put(Context.SECURITY_PRINCIPAL, THIS_SECURITY_PRINCIPAL);
        env.put(Context.SECURITY_CREDENTIALS, THIS_SECURITY_CREDENTIALS);

        System.out.println("create initial context");
        Context ic = new InitialContext(env);
        System.out.println(" initial context created..");
        try {
            System.out.println("lookup binding..");
            cf = (ConnectionFactory) ic.lookup(BINDING);

            System.out.println("lookup jndi queue..");
            Queue queue = (Queue) ic.lookup(JMS_JNDI_QUEUE);

            System.out.println("create connection..");
            connection = cf.createConnection(THIS_SECURITY_PRINCIPAL, THIS_SECURITY_CREDENTIALS);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);

            connection.start();

            // Prepare unique Business Process ID
            Date curDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("ddHHmmss");

            for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {

                // Prepare the message for sending
                TextMessage onemsg = session.createTextMessage();
                onemsg.setText(getFLUXFAReportMessage());

                // Set minimal set of attributes, required by FLUX TL JMS
                onemsg.setStringProperty("CONNECTOR_ID", CONNECTOR_ID);
                onemsg.setStringProperty("AD", FLUX_ENV_AD);
                onemsg.setIntProperty("TO", FLUX_ENV_TO);
                GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
                gcal.setTime(new Date(System.currentTimeMillis() + 1000000));
                XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

                onemsg.setStringProperty("TODT", xgcal.toString());
                onemsg.setStringProperty("DF", FLUX_ENV_DF);


                // BUSINESS_UUID has a prefix, a date-time combination and a serial - thus it is semi unique
                onemsg.setStringProperty("BUSINESS_UUID", BUSINESS_PROCEDURE_PREFIX + format.format(curDate) + String.format("%02d", i));
                System.out.println("Sending a message : " + (i + 1) + " /w ID: " + onemsg.getStringProperty("BUSINESS_UUID"));
                producer.send(onemsg);
            }

            // Close the connection
            connection.close(); connection = null;
            ic.close();ic = null;
            System.out.println("done..");
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

    public static String getFLUXFAReportMessage() throws IOException {
        File xmlFile = new File("C:\\workspace\\testProject\\s011a_REP010_TRA.xml");

        String fluxFAReportMessage ="";
        Reader fileReader = null;
        try {
            fileReader = new FileReader(xmlFile);
            BufferedReader bufReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();

            while (line != null) {
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
             fluxFAReportMessage = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            fileReader.close();
        }


        return fluxFAReportMessage;
    }




}
