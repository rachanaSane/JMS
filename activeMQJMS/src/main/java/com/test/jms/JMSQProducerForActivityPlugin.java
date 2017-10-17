package com.test.jms;

import org.apache.activemq.ActiveMQConnection;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by sanera on 02/06/2016.
 */
public class JMSQProducerForActivityPlugin {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static Map<String, JAXBContext> contexts = new HashMap();
    private static final String FILE_PATH = "C:\\workspace\\testProject\\s001c_REP001-responding-to-QUE001_TRA.xml";
  // private static final String FILE_PATH = "C:\\workspace\\testProject\\fluxFaReportMessage.xml";

    // Example of posting a FLUX TL JMS message under WildFly AS 7.2.0 platform, with HornetQ integrated
    public static void main(String[] args) {
        Reader fileReader =null;
        try {
            System.out.println("start: ");
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://10.155.0.12:61616");
            Context ctx = new InitialContext(props);
            System.out.println("context created ");
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("ConnectionFactory");

            System.out.println("connection factory created ");
            Connection connection = connectionFactory.createConnection();
            connection.start();

            //   Destination destination = (Destination) ctx.lookup("dynamicQueues/ERSPlugin");
            // Destination destination = (Destination)ctx.lookup("dynamicQueues/UVMSactivityEvent");
            Destination destination = (Destination)ctx.lookup("dynamicQueues/UVMSFAPluginEvent");
            System.out.println("destination created ");
            // JMS messages are sent and received using a Session. We will
            // create here a non-transactional session object. If you want
            // to use transactions you should set the first parameter to 'true'
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);


            MessageProducer producer = session.createProducer(destination);
          //  TextMessage message = session.createTextMessage();
            TextMessage message = prepareMessage(getFluxFAReportMessage(),session);
            Destination destinationReporting = (Destination)ctx.lookup("dynamicQueues/UVMSReporting");
            message.setJMSReplyTo(destinationReporting);


            // read xml file

            //  File xmlFile = new File("C:\\workspace\\testProject\\fluxFaReportMessage.xml");
           // File xmlFile = new File("C:\\workspace\\testProject\\Activity_RQ_RS1.xml");
            File xmlFile = new File("C:\\workspace\\testProject\\Activity_RQ_RS1_new.xml");
          //  File xmlFile = new File("C:\\workspace\\testProject\\Activity_RQ_RS1_AllFilters.xml");
         //   File xmlFile = new File("C:\\workspace\\testProject\\Activity_RQ_RS1_EmptyLists.xml");

             fileReader = new FileReader(xmlFile);


           BufferedReader bufReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();

            while (line != null) {
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
            String activityERS = sb.toString();


         //   message.setText(activityERS);
        //    message.setStringProperty("DF","FLUXFAReportMessage");

            // send message
            System.out.println("sending xml message ");
            producer.send(message);

            System.out.println("Sent: " + message.getText());


            connection.close();
        } catch (Exception e) {
            System.out.println("exception: " );
            e.printStackTrace();
        }finally{
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Prepare the message for sending and set minimal set of attributes, required by FLUX TL JMS;
     *
     * @param textMessage
     * @return fluxMsg
     *
     * @throws JMSException
     * @throws DatatypeConfigurationException
     */
    private static TextMessage  prepareMessage(String textMessage, Session session) throws JMSException {
        TextMessage fluxMsg = session.createTextMessage();
        fluxMsg.setText(textMessage);
        fluxMsg.setStringProperty(FluxConnectionConstants.CONNECTOR_ID,  FluxConnectionConstants.CONNECTOR_ID_VAL);
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_AD,   FluxConnectionConstants.FLUX_ENV_AD_VAL);
        //fluxMsg.setStringProperty(FluxConnectionConstants.RE,            FluxConnectionConstants.RE_VAL);
        //fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_TO,   FluxConnectionConstants.FLUX_ENV_TO_VAL);
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_DF,   FluxConnectionConstants.FLUX_ENV_DF_VAL);
        fluxMsg.setStringProperty("ON",   "abc@abc.com");
        fluxMsg.setStringProperty(FluxConnectionConstants.BUSINESS_UUID, createBusinessUUID());
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_TODT, createStringDate());
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_AR,   FluxConnectionConstants.FLUX_ENV_AR_VAL);
        fluxMsg.setStringProperty("FR", "XEU");
        System.out.println(fluxMsg);
        System.out.println(fluxMsg);
        return fluxMsg;
    }

    private static String createBusinessUUID(){
        return UUID.randomUUID().toString();
    }

    private static String createStringDate() {
        GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
        gcal.setTime(new Date(System.currentTimeMillis() + 1000000));
        XMLGregorianCalendar xgcal;
        try {
            xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            return xgcal.toString();
        } catch (DatatypeConfigurationException | NullPointerException e) {
            return null;
        }
    }

    private static String getFluxFAReportMessage() throws IOException {
        File xmlFile = new File(FILE_PATH);


        Reader fileReader = null;
        String fluxFAReportMessage =null;
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
