import org.hornetq.jms.client.HornetQConnectionFactory;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class TestJMS {

    private static final String FILE_PATH = "C:\\workspace\\TestJMS\\s001c_REP001-responding-to-QUE001_TRA.xml";

    // Connection objects.
    private HornetQConnectionFactory connectionFactory = null;
    private Context context       = null;
    private Connection connection = null;
    private Session session       = null;

    // Queues (remote).
    private Queue bridgeDestination = null;
    private Queue mdroutDestination = null;

    // Producers.
    private MessageProducer bridgeProducer = null;
    private MessageProducer mdrinProducer  = null;

    // Consumers. (we don't need bridgeConsumer.. but if for some reason we do, its there)
    private MessageConsumer mdrinConsumer  = null;
    private MessageConsumer bridgeConsumer = null;

    private final boolean printMessageProperties = true;
    private final boolean printSentMessage       = true;


    @Test
    public void testRemoteJmsSendingAndReceiving() throws NamingException, JMSException {
        try {
            // Initializes a connection, session and all the needed producers / consumers for FLUX TL;
            initializeConnection();

            // Send INDEX Message to bridge queue.
            sendFluxFaReportMessage();

            // Sending messages to bridge remote queue.
            //int counter = sendStructureMessagesForList();

            // Consume the responses from mdrin remote queue.
            int counter = 100; // reset for experiment purposes
            //consumeMessagesFromMdrOutJmsQueue(counter);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

    }

    /**
     * Consumes "counter" number of messages from the mdrout queue.
     *
     * @param counter
     * @throws InterruptedException
     * @throws JMSException
     */
    private void consumeMessagesFromMdrOutJmsQueue(int counter) throws InterruptedException, JMSException {
        TextMessage receivedMessage;
        int counter2 = 0;
        do {
            Thread.sleep(200);
            receivedMessage = (TextMessage) mdrinConsumer.receiveNoWait();
            try{
                System.out.println("\n\nMessage nr."+counter2+++" Received : \n" + receivedMessage.getText());
            } catch(NullPointerException ex){
                System.out.println("\n\nMessage nr."+counter2+" Received (Empty): \n" + null);
            }
        } while (receivedMessage != null || counter2 <= counter);
    }

    private int sendStructureMessagesForList() throws InterruptedException, JMSException {
        List<String> listOfStructureMessages = getListOfStructureMessages();
        int counter = 1;
        for(String messageStr : listOfStructureMessages){
            // send request to flux mdrin queue.
            Thread.sleep(100);
            bridgeProducer.send(prepareMessage(messageStr, session));
            System.out.println("\n\nMessage nr." + counter++ + " sent successfully.");
        }
        System.out.println("\nAll messages were successfully sent to the configured queue.\n\n");
        return counter;
    }

    private void sendIndexMessage() throws JMSException {
        TextMessage textMessage = prepareMessage(getINDEXStringMessage(), session);
        //JMSContext createContext = connectionFactory.createContext("fluxq", "testpassword", JMSContext.AUTO_ACKNOWLEDGE);
        bridgeProducer.send(textMessage);
        System.out.println("The following message was sent : " + textMessage.getText()+"\n\n");
    }



    private void sendFluxFaReportMessage() throws JMSException, IOException {
        TextMessage textMessage = prepareMessage(getFluxFAReportMessage(), session);
        //JMSContext createContext = connectionFactory.createContext("fluxq", "testpassword", JMSContext.AUTO_ACKNOWLEDGE);
        bridgeProducer.send(textMessage);
        System.out.println("The following message was sent : " + textMessage.getText()+"\n\n");
    }

    /**
     * Initializes a connection, session and all the needed producers / consumers for FLUX TL;
     *
     * @throws NamingException
     * @throws JMSException
     */
    private void initializeConnection() throws NamingException, JMSException {
        // Starting the JMS Connection.
        context = new InitialContext(prepareEnvProperties());
        connectionFactory = (HornetQConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
        //createContext = connectionFactory.createContext("fluxq", "testpassword", JMSContext.AUTO_ACKNOWLEDGE);
        System.out.println("Created context / connectionFactory. lookup: jms/RemoteConnectionFactory successful!");

        // Create queues.
        bridgeDestination = (Queue) context.lookup(FluxConnectionConstants.MDROUT_QUEUE_WITHOUT_COLON_JAVA );
        mdroutDestination = (Queue) context.lookup(FluxConnectionConstants.MDROUT_QUEUE_WITHOUT_COLON_JAVA);

        // Create connection and session objects.
        System.out.println("Creating connection and session!");
        connection = connectionFactory.createConnection("fluxq", "testpassword");
        session    = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create producers.
        bridgeProducer = session.createProducer(bridgeDestination);
        mdrinProducer  = session.createProducer(mdroutDestination);

        // Create consumers.
        bridgeConsumer = session.createConsumer(bridgeDestination);
        mdrinConsumer  = session.createConsumer(mdroutDestination);

        // Start connection.
        connection.start();
        System.out.println("Successfully created the needed connection, session, producers, consumers ecc..\n\n\n");
    }

    private Properties prepareEnvProperties() {
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FluxConnectionConstants.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, FluxConnectionConstants.URL_W_PORT_TO_REMOTE_JMS);
        env.put(Context.SECURITY_PRINCIPAL, FluxConnectionConstants.FLUX_ID);
        env.put(Context.SECURITY_CREDENTIALS, FluxConnectionConstants.FLUXP);
        env.put("http-upgrade-enabled","true");
        env.put("http-upgrade-endpoint", "http-acceptor");
        return env;
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
    private TextMessage prepareMessage(String textMessage, Session session) throws JMSException {
        TextMessage fluxMsg = session.createTextMessage();
        fluxMsg.setText(textMessage);
        fluxMsg.setStringProperty(FluxConnectionConstants.CONNECTOR_ID,  FluxConnectionConstants.CONNECTOR_ID_VAL);
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_AD,   FluxConnectionConstants.FLUX_ENV_AD_VAL);
        //fluxMsg.setStringProperty(FluxConnectionConstants.RE,            FluxConnectionConstants.RE_VAL);
        //fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_TO,   FluxConnectionConstants.FLUX_ENV_TO_VAL);
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_DF,   FluxConnectionConstants.FLUX_ENV_DF_VAL);
        fluxMsg.setStringProperty(FluxConnectionConstants.BUSINESS_UUID, createBusinessUUID());
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_TODT, createStringDate());
        fluxMsg.setStringProperty(FluxConnectionConstants.FLUX_ENV_AR,   FluxConnectionConstants.FLUX_ENV_AR_VAL);
        fluxMsg.setStringProperty("FR", "XEU");
        printMessageProperties(fluxMsg);
        printMessageContent(fluxMsg);
        return fluxMsg;
    }

    private void printMessageContent(TextMessage fluxMsg) {
        if(printSentMessage){
            try {
                System.out.println("\n\nThe following message was sent : \n\n" + fluxMsg.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private void printMessageProperties(TextMessage fluxMsg) throws JMSException {
        if(printMessageProperties) {
            System.out.print("Prepared message with the following properties  : \n\n");
            int i = 0;
            Enumeration propertyNames = fluxMsg.getPropertyNames();
            String propName;
            while (propertyNames.hasMoreElements()) {
                i++;
                propName = (String) propertyNames.nextElement();
                System.out.println(i + ". " + propName + " : " + fluxMsg.getStringProperty(propName));
            }
        }
    }


    private String getFluxFAReportMessage() throws IOException {
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

    /**
     * Prepares the request for index service.
     *
     * @return
     */
    public String getINDEXStringMessage() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns3:FLUXMDRQueryMessage xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" \n" +
                "\t\t\t\t\t\t xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" \n" +
                "\t\t\t\t\t\t xmlns:ns3=\"urn:un:unece:uncefact:data:standard:FLUXMDRQueryMessage:5\">\n" +
                "    <ns3:MDRQuery>\n" +
                "        <ID schemeID=\"UUID\">"+ UUID.randomUUID().toString()+"</ID>\n" +
                "        <SubmittedDateTime>\n" +
                "            <ns2:DateTime>2017-03-21T10:25:28.236Z</ns2:DateTime>\n" +
                "        </SubmittedDateTime>\n" +
                "        <TypeCode listID=\"FLUX_MDR_QUERY_TYPE\">INDEX</TypeCode>\n" +
                "        <ContractualLanguageCode>EN</ContractualLanguageCode>\n" +
                "        <SubmitterFLUXParty>\n" +
                "            <ID>HUN</ID>\n" +
                "        </SubmitterFLUXParty>\n" +
                "        <SubjectMDRQueryIdentity>\n" +
                "            <ID schemeID=\"INDEX\">INDEX</ID>\n" +
                "        </SubjectMDRQueryIdentity>\n" +
                "    </ns3:MDRQuery>\n" +
                "</ns3:FLUXMDRQueryMessage>";
    }


    /**
     * Prepares Structure requests messages for FLUX TL.
     * The list of acronyms is as you can see the mocket one from cedric email.
     *
     * @return list of messages
     */
    public List<String> getListOfStructureMessages(){
        final List<String> acronymsList = Arrays.asList("EFFORT_ZONE", "FA_BAIT_TYPE", "FA_BFT_SIZE_CATEGORY", "FA_BR",
                "FA_CATCH_TYPE", "FA_CHARACTERISTIC", "FA_FISHERY", "FA_GEAR_CHARACTERISTIC", "FA_GEAR_PROBLEM", "FA_GEAR_RECOVERY",
                "FA_GEAR_ROLE", "FA_QUERY_TYPE", "FA_QUERY_PARAMETER", "FA_REASON_ARRIVAL", "FA_REASON_DEPARTURE", "FA_REASON_ENTRY",
                "FA_REASON_DISCARD", "FA_VESSEL_ROLE", "FAO_AREA", "FAO_SPECIES", "FARM", "FISH_FRESHNESS",
                "FISH_PACKAGING", "FISH_PRESENTATION", "FISH_PRESERVATION", "FISH_SIZE_CLASS", "FISHING_TRIP_TYPE", "FLAP_ID_TYPE",
                "FLUX_CONTACT_ROLE", "FLUX_FA_FMC", "FLUX_FA_REPORT_TYPE", "FLUX_FA_TYPE", "FLUX_GP_PARTY", "FLUX_GP_PURPOSE",
                "FLUX_GP_RESPONSE", "FLUX_GP_VALIDATION_LEVEL", "FLUX_GP_VALIDATION_TYPE", "FLUX_LOCATION_CHARACTERISTIC",
                "FLUX_LOCATION_TYPE", "FLUX_PROCESS_TYPE", "FLUX_UNIT", "GEAR_TYPE", "GFCM_GSA", "GFCM_STAT_RECTANGLE", "ICES_STAT_RECTANGLE",
                "LOCATION", "RFMO", "TERRITORY", "VESSEL_ACTIVITY", "VESSEL_STORAGE_TYPE", "WEIGHT_MEANS"
        );
        List<String> messagesList = new ArrayList<>();
        for(String acronym : acronymsList){
            messagesList.add( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<ns3:FLUXMDRQueryMessage xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" " +
                    "xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" " +
                    "xmlns:ns3=\"urn:un:unece:uncefact:data:standard:FLUXMDRQueryMessage:5\">\n" +
                    "    <ns3:MDRQuery>\n" +
                    "        <ID schemeID=\"UUID\">"+ UUID.randomUUID().toString()+"</ID>\n" +
                    "        <SubmittedDateTime>\n" +
                    "            <ns2:DateTime>"+"2017-03-21T18:25:28.236Z"+"</ns2:DateTime>\n" +
                    "        </SubmittedDateTime>\n" +
                    "        <TypeCode listID=\"FLUX_MDR_QUERY_TYPE\">OBJ_DESC</TypeCode>\n" +
                    "        <ContractualLanguageCode>EN</ContractualLanguageCode>\n" +
                    "        <SubmitterFLUXParty>\n" +
                    "            <ID>HUN</ID>\n" +
                    "        </SubmitterFLUXParty>\n" +
                    "        <SubjectMDRQueryIdentity>\n" +
                    "            <ID schemeID=\"INDEX\">"+acronym+"</ID>\n" +
                    "        </SubjectMDRQueryIdentity>\n" +
                    "    </ns3:MDRQuery>\n" +
                    "</ns3:FLUXMDRQueryMessage>");
        }
        return messagesList;
    }

    private static String createSubmitedDate() {
        XMLGregorianCalendar date = null;
        try {
            date = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return date.toString();
    }

    /**
     *  BUSINESS_UUID has a prefix, a date-time combination and a serial - thus it is semi unique
     * @return String
     */
    private String createBusinessUUID(){
        return UUID.randomUUID().toString();
    }


    private String createStringDate() {
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

    /**
     * Closes a JMS connection;
     * Disconnects from the actual connection if it is still active;
     *
     * @throws NamingException
     * @throws JMSException
     */
    protected void closeConnection() {
        try {
            if(session != null){
                System.out.println("\n\nClosing session.");
                session.close();
            }
            if (connection != null) {
                System.out.println("Closing connection.");
                connection.stop();
                connection.close();
            }
            System.out.println("Succesfully closed the connection and/or session.");
        } catch (JMSException e) {
            System.out.println("Error when stopping or closing connection." + e.getMessage());
        }
    }

}