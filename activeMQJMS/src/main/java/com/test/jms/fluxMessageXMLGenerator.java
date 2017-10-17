import javax.xml.bind.JAXBException;

/**
 * Created by sanera on 27/05/2016.
 */
public class fluxMessageXMLGenerator {

    public static void main(String[] args) throws JAXBException {

     /*   System.out.println("start");
        // Simulate message coming from flux.
        RequestType requestType = new RequestType();
        requestType.setAD("AD");
        requestType.setDF("df");
        requestType.setAR(false);
        requestType.setON("on");
        requestType.setTO(1);
        requestType.setVB(VerbosityType.DEBUG);

//// generate FLUXFAReportMessage

       FLUXFAReportMessage fluxFAReportMessage = new FLUXFAReportMessage();

        // create fluxFAReportDocument
        FLUXReportDocument FLUXReportDocument=new FLUXReportDocument();
        FLUXReportDocument.setCreationDateTime(new DateTimeType());
        FLUXReportDocument.setOwnerFLUXParty(new FLUXParty());
        TextType purpose=new TextType();
        purpose.setValue("purpose");
        FLUXReportDocument.setPurpose(purpose);
        CodeType PurposeCodeType =new CodeType();
        PurposeCodeType.setName("PURPOSE_CODE_TYPE");
        PurposeCodeType.setValue("102");
        FLUXReportDocument.setPurposeCode(PurposeCodeType);
        IDType referenceIDType =new IDType();
        referenceIDType.setValue("referenceid");
        referenceIDType.setSchemeID("referenceScheme");
        referenceIDType.setSchemeName("schemeName");
        FLUXReportDocument.setReferencedID(referenceIDType);
        CodeType typeCodeType =new CodeType();
        typeCodeType.setName("PURPOSE_TYPE_TYPE");
        typeCodeType.setValue("101");
        FLUXReportDocument.setTypeCode(typeCodeType);

        fluxFAReportMessage.setFLUXReportDocument(FLUXReportDocument);

        List<FAReportDocument> faReportDocuments = new ArrayList<FAReportDocument>();

        FAReportDocument FAReportDocument =new FAReportDocument();
        CodeType faReportDocCodeType =new CodeType();
        faReportDocCodeType.setName("FA_REPORT_DOC_1");
        faReportDocCodeType.setValue("101");
        FAReportDocument.setTypeCode( faReportDocCodeType);
        FAReportDocument.setAcceptanceDateTime(new DateTimeType());
        CodeType fmcCodeType =new CodeType();
        fmcCodeType.setName("FA_REPORT_DOC_FMC");
        fmcCodeType.setValue("101");
        FAReportDocument.setFMCMarkerCode(fmcCodeType);

        faReportDocuments.add(FAReportDocument);

        fluxFAReportMessage.setFAReportDocuments(faReportDocuments);




        JAXBContext context = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Marshaller marshaller = context.createMarshaller();
        DOMResult res = new DOMResult();
        marshaller.marshal(fluxFAReportMessage, res);

        Element elt = ((Document) res.getNode()).getDocumentElement();

        requestType.setAny(elt);



        try {

            File file = new File("C:\\fluxRequestFile.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(RequestType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(requestType, file);
            jaxbMarshaller.marshal(requestType, System.out);
            System.out.println("done");

        } catch (JAXBException e) {
            e.printStackTrace();
        }*/

    }
}
