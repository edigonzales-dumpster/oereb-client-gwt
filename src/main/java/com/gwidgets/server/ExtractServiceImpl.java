package com.gwidgets.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.collectingAndThen;

import javax.servlet.ServletException;
import javax.xml.transform.stream.StreamSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.gwidgets.shared.ExtractResponse;
import com.gwidgets.shared.ExtractService;
import com.gwidgets.shared.models.AbstractTheme;
import com.gwidgets.shared.models.ConcernedTheme;
import com.gwidgets.shared.models.Document;
import com.gwidgets.shared.models.Extract;
import com.gwidgets.shared.models.NotConcernedTheme;
import com.gwidgets.shared.models.RealEstateDPR;
import com.gwidgets.shared.models.ReferenceWMS;
import com.gwidgets.shared.models.Restriction;
import com.gwidgets.shared.models.ThemeWithoutData;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentBaseType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExtractType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RealEstateDPRType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RestrictionOnLandownershipType;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ExtractServiceImpl extends RemoteServiceServlet implements ExtractService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Value("${app.oerebWebServiceUrl}")
    private String oerebWebServiceUrl;
    
    @Autowired
    Jaxb2Marshaller marshaller;
    
    private List<String> themesOrderingList = Stream.of(
            "LandUsePlans",
            "ch.SO.NutzungsplanungGrundnutzung", 
            "ch.SO.NutzungsplanungUeberlagernd", 
            "ch.SO.NutzungsplanungSondernutzungsplaene", 
            "ch.SO.Baulinien",
            "MotorwaysProjectPlaningZones",
            "MotorwaysBuildingLines",
            "RailwaysProjectPlanningZones",
            "RailwaysBuildingLines",
            "AirportsProjectPlanningZones",
            "AirportsBuildingLines",
            "AirportsSecurityZonePlans",
            "ContaminatedSites",
            "ContaminatedMilitarySites",
            "ContaminatedCivilAviationSites",
            "ContaminatedPublicTransportSites",
            "GroundwaterProtectionZones",
            "GroundwaterProtectionSites",
            "NoiseSensitivityLevels",
            "ForestPerimeters",
            "ForestDistanceLines")
            .collect(Collectors.toList());
    
    // see: https://stackoverflow.com/questions/51874785/gwt-spring-boot-autowired-is-not-working
    @Override
    public void init() throws ServletException {
         super.init();
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

    @Override
    public ExtractResponse extractServer(String egrid) throws IllegalArgumentException, IOException {
        logger.info(egrid);
        logger.info(oerebWebServiceUrl.toString());

        // TODO: handle empty file / no extract returned
        File xmlFile = Files.createTempFile("data_extract_", ".xml").toFile();
        
//        URL url = new URL(oerebWebServiceUrl + "/reduced/xml/geometry/" + egrid);
//        URL url = new URL("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH533287066291.xml");
        URL url = new URL("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH368132060914.xml");
        logger.info(url.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        InputStream initialStream = connection.getInputStream();
        java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        logger.info("File downloaded: " + xmlFile.getAbsolutePath());

        
        StreamSource xmlSource = new StreamSource(xmlFile);
        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
        ExtractType xmlExtract = obj.getValue().getExtract().getValue();
        
        Extract extract = new Extract();
        extract.setExtractIdentifier(xmlExtract.getExtractIdentifier());

        LinkedList<ThemeWithoutData> themesWithoutData = xmlExtract.getThemeWithoutData().stream()
                .map(theme -> { 
                    ThemeWithoutData themeWithoutData = new ThemeWithoutData();
                    themeWithoutData.setCode(theme.getCode());
                    themeWithoutData.setName(theme.getText().getText());
                    return themeWithoutData; 
                })
                .collect(collectingAndThen(toList(), LinkedList<ThemeWithoutData>::new));
        themesWithoutData.sort(compare);
        
        LinkedList<NotConcernedTheme> notConcernedThemes = xmlExtract.getNotConcernedTheme().stream()
                .map(theme -> { 
                    NotConcernedTheme notConcernedTheme = new NotConcernedTheme();
                    notConcernedTheme.setCode(theme.getCode());
                    notConcernedTheme.setName(theme.getText().getText());
                    return notConcernedTheme; 
                })
                .collect(collectingAndThen(toList(), LinkedList<NotConcernedTheme>::new));
        notConcernedThemes.sort(compare);
        
        RealEstateDPRType xmlRealEstate = xmlExtract.getRealEstate();

        RealEstateDPR realEstate = new RealEstateDPR();
        realEstate.setEgrid(xmlRealEstate.getEGRID());
        realEstate.setFosnNr(xmlRealEstate.getFosNr());
        realEstate.setMunicipality(xmlRealEstate.getMunicipality());
        realEstate.setCanton(xmlRealEstate.getCanton().value());
        realEstate.setNumber(xmlRealEstate.getNumber());
        realEstate.setSubunitOfLandRegister(xmlRealEstate.getSubunitOfLandRegister());
        realEstate.setLandRegistryArea(xmlRealEstate.getLandRegistryArea());
        realEstate.setLimit(new Gml32ToJts().convertMultiSurface(xmlRealEstate.getLimit()).toText());
        realEstate.setThemesWithoutData(themesWithoutData);
        realEstate.setNotConcernedThemes(notConcernedThemes);
                
        // Create a map with all restrictions grouped by theme text.
        Map<String, List<RestrictionOnLandownershipType>> groupedXmlRestrictions = xmlRealEstate.getRestrictionOnLandownership().stream()
            .collect(Collectors.groupingBy(r -> r.getTheme().getText().getText()));
        
        logger.info("*********************************************");
        logger.info("*********************************************");
        
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // We create one ConcernedTheme object per theme with all restrictions belonging to the same theme
        // since this is the way we present the restriction in the GUI.
        LinkedList<ConcernedTheme> concernedThemesList = new LinkedList<ConcernedTheme>();
        for (Map.Entry<String, List<RestrictionOnLandownershipType>> entry : groupedXmlRestrictions.entrySet()) {
            logger.info("*********************************************");
            logger.info("ConcernedTheme: " + entry.getKey());
            logger.info("---------------------------------------------");
            
            List<RestrictionOnLandownershipType> xmlRestrictions = entry.getValue();
            
            // Create a map with one simplified restriction for each type code.
            // We cannot use groupingBy because this will return a list of
            // restriction per type code.
            // Afterwards will add more information to the restriction.
            Map<String, Restriction> restrictionsMap = xmlRestrictions.stream()
                    .filter(distinctByKey(RestrictionOnLandownershipType::getTypeCode)).map(r -> {
                        Restriction restriction = new Restriction();
                        restriction.setInformation(r.getInformation().getLocalisedText().get(0).getText());
                        restriction.setTypeCode(r.getTypeCode());
                        restriction.setSymbol(r.getSymbol());
                        return restriction;
                    }).collect(Collectors.toMap(Restriction::getTypeCode, Function.identity()));
            
            logger.info(restrictionsMap.toString());
            
            // Calculate sum of the shares for each type code.
            Map<String, Integer> sumAreaShare = xmlRestrictions.stream()
                    .filter(r -> r.getAreaShare() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingInt(r -> r.getAreaShare())));
            
            Map<String, Integer> sumLengthShare = xmlRestrictions.stream()
                    .filter(r -> r.getLengthShare() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingInt(r -> r.getLengthShare())));
            
            Map<String, Integer> sumNrOfPoints = xmlRestrictions.stream()
                    .filter(r -> r.getNrOfPoints() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingInt(r -> r.getNrOfPoints())));

            logger.info("sumAreaShare: " + sumAreaShare.toString());
            logger.info("sumLengthShare: " + sumLengthShare.toString());
            logger.info("sumNrOfPoints: " + sumNrOfPoints.toString());
            
            // Assign the sum to the simplified restriction.
            // And add the restriction to the final restrictons list.
            List<Restriction> restrictionsList = new ArrayList<Restriction>();
            for (Map.Entry<String, Restriction> restrictionEntry : restrictionsMap.entrySet()) {
                String typeCode = restrictionEntry.getKey();
                // I think this helps to find out which one to print in the client.
                // Only one of the shares is not null.
                if (sumAreaShare.get(typeCode) != null) {
                    restrictionEntry.getValue().setAreaShare(sumAreaShare.get(typeCode));
                    logger.info(String.valueOf(restrictionEntry.getValue().getAreaShare()));
                }
                if (sumLengthShare.get(typeCode) != null) {
                    restrictionEntry.getValue().setLengthShare(sumLengthShare.get(typeCode));
                    logger.info(String.valueOf(restrictionEntry.getValue().getLengthShare()));
                }
                if (sumNrOfPoints.get(typeCode) != null) {
                    restrictionEntry.getValue().setNrOfPoints(sumNrOfPoints.get(typeCode));
                    logger.info(String.valueOf(restrictionEntry.getValue().getNrOfPoints()));
                }
                restrictionsList.add(restrictionEntry.getValue());
            }
            
            // Get legal provisions and laws.
            List<Document> legalProvisionsList = new ArrayList<Document>();
            List<Document> lawsList = new ArrayList<Document>();
            
            for (RestrictionOnLandownershipType xmlRestriction : xmlRestrictions) {
                List<DocumentBaseType> xmlLegalProvisions = xmlRestriction.getLegalProvisions();
                for (DocumentBaseType xmlDocumentBase : xmlLegalProvisions) {
                    DocumentType xmlLegalProvision =  (DocumentType) xmlDocumentBase;
                    Document legalProvision = new Document();
                    legalProvision.setTitle(xmlLegalProvision.getTitle().getLocalisedText().get(0).getText());
                    legalProvision.setAbbreviation(xmlLegalProvision.getAbbreviation().getLocalisedText().get(0).getText());
                    legalProvision.setTextAtWeb(xmlLegalProvision.getTextAtWeb().getLocalisedText().get(0).getText());
                    legalProvisionsList.add(legalProvision);
                    
                    List<DocumentType> xmlLaws = xmlLegalProvision.getReference();
                    for (DocumentType xmlLaw : xmlLaws) {
                        Document law = new Document();
                        law.setTitle(xmlLaw.getTitle().getLocalisedText().get(0).getText());
                        law.setAbbreviation(xmlLaw.getAbbreviation().getLocalisedText().get(0).getText());
                        law.setTextAtWeb(xmlLaw.getTextAtWeb().getLocalisedText().get(0).getText());
                        lawsList.add(law);
                    }
                }
            }

            // Because restrictions can share the same legal provision and laws,
            // we need to distinct them.
            List<Document> distinctLegalProvisionsList = legalProvisionsList.stream()
                    .filter(distinctByKey(Document::getTextAtWeb)).collect(Collectors.toList());

            List<Document> distinctLawsList = lawsList.stream().filter(distinctByKey(Document::getTextAtWeb)).collect(Collectors.toList());

            // WMS
            double layerOpacity = xmlRestrictions.get(0).getMap().getLayerOpacity();
            String wmsUrl = xmlRestrictions.get(0).getMap().getReferenceWMS();

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(wmsUrl).build();
            String schema = uriComponents.getScheme();
            String host = uriComponents.getHost();
            String path = uriComponents.getPath();
            String layers = uriComponents.getQueryParams().getFirst("LAYERS"); // FIXME case insensitivity
            String imageFormat = uriComponents.getQueryParams().getFirst("FORMAT"); // FIXME case insensitivity
            
            StringBuilder baseUrlBuilder = new StringBuilder();
            baseUrlBuilder.append(schema).append("://").append(host);
            if (uriComponents.getPort() != -1) {
                baseUrlBuilder.append(String.valueOf(uriComponents.getPort()));
            }
            baseUrlBuilder.append(path);
            String baseUrl = baseUrlBuilder.toString();
 
            ReferenceWMS referenceWMS = new ReferenceWMS();
            referenceWMS.setBaseUrl(baseUrl);
            referenceWMS.setLayers(layers);
            referenceWMS.setLayerOpacity(layerOpacity);
            referenceWMS.setImageFormat(imageFormat);
            
            // LegendAtWeb
            String legendAtWeb = xmlRestrictions.get(0).getMap().getLegendAtWeb().getValue();
                        
            // Finally we create the concerned theme with all
            // the information.
            ConcernedTheme concernedTheme =  new ConcernedTheme();
            concernedTheme.setRestrictions(restrictionsList);
            concernedTheme.setLegalProvisions(distinctLegalProvisionsList);
            concernedTheme.setLaws(distinctLawsList);
            concernedTheme.setReferenceWMS(referenceWMS);
            concernedTheme.setReferenceWMS(referenceWMS);
            concernedTheme.setLegendAtWeb(legendAtWeb);
            concernedTheme.setCode(xmlRestrictions.get(0).getTheme().getCode());
            concernedTheme.setName(xmlRestrictions.get(0).getTheme().getText().getText());
            
            concernedThemesList.add(concernedTheme);
        }
        
        concernedThemesList.sort(compare);
                
        realEstate.setConcernedThemes(concernedThemesList);
        extract.setRealEstate(realEstate);
        extract.setPdfLink(oerebWebServiceUrl + "/reduced/pdf/geometry/" + egrid);
               
        ExtractResponse response = new ExtractResponse();
        response.setExtract(extract);
//        response.setEgrid("lilalaunebär");
//        response.setExtract(extract);
        
        
        return response;
    }
    
    // TODO: test if it works with AbstractTheme!
    // But themesOrderingList contains already new codes.
    Comparator<AbstractTheme> compare = new Comparator<AbstractTheme>() {
        public int compare(AbstractTheme t1, AbstractTheme t2) {
            return themesOrderingList.indexOf(t1.getCode()) - themesOrderingList.indexOf(t2.getCode());
        }
   };

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
