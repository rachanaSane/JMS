/**
 * Created by sanera on 02/06/2016.
 */
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;


public class XMLAsString {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("start");
        // our XML file for this example
        File xmlFile = new File("C:\\workspace\\testProject\\fluxFaReportMessage.xml");
        // Let's get XML file as String using BufferedReader
        // FileReader uses platform's default character encoding
        // if you need to specify a different encoding, use InputStreamReader
        Reader fileReader = new FileReader(xmlFile);


        BufferedReader bufReader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();

        while( line != null){ sb.append(line).append("\n"); line = bufReader.readLine(); }
        String xml2String = sb.toString();
        System.out.println("XML to String using BufferedReader : ");
        System.out.println(xml2String); bufReader.close();
     /*   // parsing XML file to get as String using DOM Parser
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document xmlDom = docBuilder.parse(xmlFile);
        String xmlAsString = xmlDom.toString();
        // this will not print what you want
        System.out.println("XML as String using DOM Parser : ");
        System.out.println(xmlAsString);
      */

    }



}
