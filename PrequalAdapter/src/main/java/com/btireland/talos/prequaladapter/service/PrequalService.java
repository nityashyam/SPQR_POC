package com.btireland.talos.prequaladapter.service;

import com.btireland.talos.prequaladapter.soap.ObjectFactory;
import com.btireland.talos.prequaladapter.soap.SOAPConnector;
import com.btireland.talos.prequaladapter.soap.SPQRRESPONSE;
import com.btireland.talos.prequaladapter.soap.SPQRRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PrequalService {

    @Autowired
    SOAPConnector soapConnector;

    @Autowired
    ResourceLoader resourceLoader;

    /**
     *
     * @param SPQRRequest
     * @return SPQRResponse
     *
     */
    public SPQRRESPONSE getSPQRfromPrequal(SPQRRequest request) {
        JAXBElement<SPQRRESPONSE> jaxbSpqrResponse =(JAXBElement<SPQRRESPONSE>) soapConnector.callWebService("http://localhost:8181/api/prequal", new ObjectFactory().createSPQRRequest(request));
        SPQRRESPONSE spqrResponse = jaxbSpqrResponse.getValue();
         return spqrResponse;
    }

    public SPQRRESPONSE getSPQRDetailsfromPrequal(SPQRRequest request) throws JAXBException, IOException {
        String fileName = "Notification.xml";
        ClassLoader classLoader = getClass().getClassLoader();
       /* File f= new File(classLoader.getResource(fileName).getFile());
        InputStream resource = classLoader.getResourceAsStream(fileName);
*/
        Resource resource = resourceLoader.getResource("classpath:"+fileName);
        InputStream inputStream = resource.getInputStream();

        JAXBContext jaxbContext;
        SPQRRESPONSE spqrResponse=null;
        try {
            jaxbContext = JAXBContext.newInstance(SPQRRESPONSE.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(getClass().getResource("/notification_dummy.xsd"));
            jaxbUnmarshaller.setSchema(schema);
            spqrResponse = (SPQRRESPONSE) jaxbUnmarshaller.unmarshal(inputStream);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return spqrResponse;
    }

}
