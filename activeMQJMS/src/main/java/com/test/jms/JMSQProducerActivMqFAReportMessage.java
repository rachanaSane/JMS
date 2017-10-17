import org.apache.activemq.ActiveMQConnection;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

/**
 * Created by sanera on 02/06/2016.
 */
public class JMSQProducerActivMqFAReportMessage {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    // Example of posting a FLUX TL JMS message under WildFly AS 7.2.0 platform, with HornetQ integrated
    public static void main(String[] args) {
        try {
            System.out.println("start: ");
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://127.0.0.1:61616");
            javax.naming.Context ctx = new InitialContext(props);
            System.out.println("context created ");
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("ConnectionFactory");

            System.out.println("connection factory created ");
            Connection connection = connectionFactory.createConnection();
            connection.start();

         //   Destination destination = (Destination) ctx.lookup("dynamicQueues/ERSPlugin");
           // Destination destination = (Destination)ctx.lookup("dynamicQueues/UVMSactivityEvent");
            Destination destination = (Destination)ctx.lookup("dynamicQueues/UVMSActivityEvent");
            System.out.println("destination created ");
            // JMS messages are sent and received using a Session. We will
            // create here a non-transactional session object. If you want
            // to use transactions you should set the first parameter to 'true'
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);


            MessageProducer producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage();


            // read xml file

          //  File xmlFile = new File("C:\\workspace\\testProject\\fluxFaReportMessage.xml");
           // File xmlFile = new File("C:\\workspace\\testProject\\fa_flux_message.xml");
            File xmlFile = new File("C:\\workspace\\testProject\\fa_flux_message.xml");


            Reader fileReader = new FileReader(xmlFile);


            BufferedReader bufReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();

            while (line != null) {
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
            String fluxFAReportMessage = sb.toString();


            message.setText(fluxFAReportMessage);
            message.setStringProperty("DF","FLUXFAReportMessage");

            // send message
            System.out.println("sending xml message ");
            producer.send(message);

            System.out.println("Sent: " + message.getText());


            connection.close();
        } catch (Exception e) {
            System.out.println("exception: " );
            e.printStackTrace();
        }
    }

}
