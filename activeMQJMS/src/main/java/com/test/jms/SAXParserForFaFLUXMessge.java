import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * Created by sanera on 16/05/2017.
 */
public class SAXParserForFaFLUXMessge extends DefaultHandler {
    private String uuid;
    private boolean isFLUXReportDocumentStart;
    private boolean isIDStart;
    private boolean isUUID;
    private String uuidValue;


    public SAXParserForFaFLUXMessge(){

    }

    public void parseDocument() throws SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
         //   parser.parse("testSaxFAReport.xml", this);

            parser.parse("s011a_REP010_TRA.xml", this);
        //    parser.parse("Activity_RQ_RS1_Test.xml", this);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public boolean isFLUXReportDocumentStart() {
        return isFLUXReportDocumentStart;
    }

    public void setFLUXReportDocumentStart(boolean FLUXReportDocumentStart) {
        isFLUXReportDocumentStart = FLUXReportDocumentStart;
    }

    public boolean isIDStart() {
        return isIDStart;
    }

    public void setIDStart(boolean IDStart) {
        isIDStart = IDStart;
    }

    public boolean isUUID() {
        return isUUID;
    }

    public void setUUID(boolean UUID) {
        isUUID = UUID;
    }

    public String getUuidValue() {
        return uuidValue;
    }

    public void setUuidValue(String uuidValue) {
        this.uuidValue = uuidValue;
    }

    public static void main(String[] args)  {
        System.out.println("Start Program :------>");
        SAXParserForFaFLUXMessge saxParserForFaFLUXMessge = new SAXParserForFaFLUXMessge();
        try {
            saxParserForFaFLUXMessge.parseDocument();
        } catch (SAXException e) {
           // e.printStackTrace();
            System.out.println("************************************************");
            System.out.println("UUID found:"+saxParserForFaFLUXMessge.getUuidValue());

            System.out.println("************************************************");
        }


        System.out.println("-----End---- :");
    }


    @Override

    public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {

      if("rsm:FLUXReportDocument".equals(elementName)){
          isFLUXReportDocumentStart =true;
      }

      if("ram:ID".equals(elementName) && isFLUXReportDocumentStart){
          isIDStart =true;
          String value =attributes.getValue("schemeID");
          if("UUID".equals(value)){
              isUUID =true;
          }
      }
        System.out.println("startElement :"+elementName);
    }

    @Override

    public void endElement(String s, String s1, String element) throws SAXException {
        System.out.println("endElement :"+element);

        if("rsm:FLUXReportDocument".equals(element)){
            isFLUXReportDocumentStart =false;
        }

        if("ram:ID".equals(element)){
            isIDStart =false;
            isUUID =false;
        }
    }

    @Override
    public void characters(char[] ac, int i, int j) throws SAXException {

      String  tmpValue = new String(ac, i, j);

        if(isUUID){
            uuidValue = tmpValue;
            System.out.println("UUID value found:"+tmpValue);
            throw new SAXException("Found the required value . so, stop parsing entire document");
        }


    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
