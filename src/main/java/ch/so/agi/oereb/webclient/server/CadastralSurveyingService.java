package ch.so.agi.oereb.webclient.server;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import ch.so.agi.oereb.webclient.shared.ExtractServiceException;
import ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Address;
import ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Extract;
import ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Office;
import ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.RealEstateDPR;
import ch.so.geo.schema.agi.cadastralinfo._1_0.extract.CadastralExtract;
import ch.so.geo.schema.agi.cadastralinfo._1_0.extract.GetExtractByIdResponse;

@Service
public class CadastralSurveyingService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cadastral.webServiceUrl}")
    private String webServiceUrl;

    @Autowired
    Jaxb2Marshaller marshaller; 

    public Extract getExtract(String egrid) throws ExtractServiceException {
        File xmlFile;
        try {
            xmlFile = Files.createTempFile("cadastral_extract_", ".xml").toFile();
            URL url = new URL(webServiceUrl + egrid);
            logger.info(url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            if (connection.getResponseCode() == 500) {
                throw new ExtractServiceException("500");
            } else if (connection.getResponseCode() == 406) {
                throw new ExtractServiceException("406");
            } else if (connection.getResponseCode() == 204) {
                throw new ExtractServiceException("204");
            }
            
            InputStream initialStream = connection.getInputStream();
            java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);  
            logger.info("File downloaded: " + xmlFile.getAbsolutePath());
        } catch (Exception e) {
            throw new ExtractServiceException(e.getMessage());
        }
        
        StreamSource xmlSource = new StreamSource(xmlFile);
        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
        CadastralExtract xmlExtract = obj.getCadastralExtract();
        
        logger.info(xmlExtract.getCreationDate().toString());

        ch.so.geo.schema.agi.cadastralinfo._1_0.extract.RealEstateDPR xmlRealEstateDPR = xmlExtract.getRealEstate();
        RealEstateDPR realEstate = new RealEstateDPR();
        realEstate.setEgrid(xmlRealEstateDPR.getEGRID());
        realEstate.setIdentND(xmlRealEstateDPR.getIdentND());
        realEstate.setMunicipality(xmlRealEstateDPR.getMunicipality());
        realEstate.setNumber(xmlRealEstateDPR.getNumber());
        realEstate.setSubunitOfLandRegister(xmlRealEstateDPR.getSubunitOfLandRegister());
        realEstate.setRealEstateType(xmlRealEstateDPR.getType());
        realEstate.setLandRegistryArea(xmlRealEstateDPR.getLandRegistryArea());
        
        ch.so.geo.schema.agi.cadastralinfo._1_0.extract.Office xmlSurveyorOffice = xmlExtract.getRealEstate().getSurveyorOffice();
        Office surveyorOffice = new Office();
        surveyorOffice.setName(xmlSurveyorOffice.getName());
        Address surveyorOfficeAddress = new Address();
        surveyorOfficeAddress.setLine1(xmlSurveyorOffice.getPostalAddress().getLine1());
        surveyorOfficeAddress.setLine2(xmlSurveyorOffice.getPostalAddress().getLine2());
        surveyorOfficeAddress.setStreet(xmlSurveyorOffice.getPostalAddress().getStreet());
        surveyorOfficeAddress.setNumber(xmlSurveyorOffice.getPostalAddress().getNumber());
        surveyorOfficeAddress.setPostalCode(xmlSurveyorOffice.getPostalAddress().getCity());
        surveyorOfficeAddress.setCity(xmlSurveyorOffice.getPostalAddress().getCity());
        surveyorOfficeAddress.setPhone(xmlSurveyorOffice.getPostalAddress().getPhone());
        surveyorOfficeAddress.setWeb(xmlSurveyorOffice.getPostalAddress().getWeb());
        surveyorOfficeAddress.setEmail(xmlSurveyorOffice.getPostalAddress().getEmail());
        realEstate.setSurveyorOffice(surveyorOffice);
        
        ch.so.geo.schema.agi.cadastralinfo._1_0.extract.Office xmlLandRegisterOffice = xmlExtract.getRealEstate().getSurveyorOffice();
        Office landRegisterOffice = new Office();
        landRegisterOffice.setName(xmlLandRegisterOffice.getName());
        Address landRegisterOfficeAddress = new Address();
        landRegisterOfficeAddress.setLine1(xmlLandRegisterOffice.getPostalAddress().getLine1());
        landRegisterOfficeAddress.setLine2(xmlLandRegisterOffice.getPostalAddress().getLine2());
        landRegisterOfficeAddress.setStreet(xmlLandRegisterOffice.getPostalAddress().getStreet());
        landRegisterOfficeAddress.setNumber(xmlLandRegisterOffice.getPostalAddress().getNumber());
        landRegisterOfficeAddress.setPostalCode(xmlLandRegisterOffice.getPostalAddress().getCity());
        landRegisterOfficeAddress.setCity(xmlLandRegisterOffice.getPostalAddress().getCity());
        landRegisterOfficeAddress.setPhone(xmlLandRegisterOffice.getPostalAddress().getPhone());
        landRegisterOfficeAddress.setWeb(xmlLandRegisterOffice.getPostalAddress().getWeb());
        landRegisterOfficeAddress.setEmail(xmlLandRegisterOffice.getPostalAddress().getEmail());
        realEstate.setLandRegisterOffice(landRegisterOffice);

        
        Extract extract = new Extract();
        extract.setRealEstate(realEstate);

        return extract;
    }
}
