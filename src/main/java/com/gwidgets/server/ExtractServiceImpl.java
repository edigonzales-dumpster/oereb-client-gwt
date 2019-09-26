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
import com.gwidgets.shared.models.Office;
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

import org.apache.xerces.impl.dv.util.Base64;
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
            //"LandUsePlans",
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
            "ForestDistanceLines",
            "ch.SO.Einzelschutz")
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
        logger.info(getRequestModuleBasePath());
        

        // TODO: handle empty file / no extract returned
        File xmlFile = Files.createTempFile("data_extract_", ".xml").toFile();
        
        URL url = new URL(oerebWebServiceUrl + "/reduced/xml/geometry/" + egrid);
//        URL url = new URL("https://geo-t.so.ch/api/oereb/v1/extract/" + "/reduced/xml/geometry/" + egrid);
//        URL url = new URL("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH533287066291.xml");
//        URL url = new URL("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH368132060914.xml");
//        URL url = new URL("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH857632820629.xml");
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

        ArrayList<ThemeWithoutData> themesWithoutData = xmlExtract.getThemeWithoutData().stream()
                .map(theme -> { 
                    ThemeWithoutData themeWithoutData = new ThemeWithoutData();
                    themeWithoutData.setCode(theme.getCode());
                    themeWithoutData.setName(theme.getText().getText());
                    return themeWithoutData; 
                })
                .collect(collectingAndThen(toList(), ArrayList<ThemeWithoutData>::new));
        themesWithoutData.sort(compare);
        
        ArrayList<NotConcernedTheme> notConcernedThemes = xmlExtract.getNotConcernedTheme().stream()
                .map(theme -> { 
                    NotConcernedTheme notConcernedTheme = new NotConcernedTheme();
                    notConcernedTheme.setCode(theme.getCode());
                    notConcernedTheme.setName(theme.getText().getText());
                    return notConcernedTheme; 
                })
                .collect(collectingAndThen(toList(), ArrayList<NotConcernedTheme>::new));
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
        
        // We create one ConcernedTheme object per theme with all restrictions belonging to the same theme
        // since this is the way we present the restriction in the GUI.
        ArrayList<ConcernedTheme> concernedThemesList = new ArrayList<ConcernedTheme>();
        for (Map.Entry<String, List<RestrictionOnLandownershipType>> entry : groupedXmlRestrictions.entrySet()) {
            logger.info("*********************************************");
            logger.info("ConcernedTheme: " + entry.getKey());
            logger.info("---------------------------------------------");
            
            List<RestrictionOnLandownershipType> xmlRestrictions = entry.getValue();
            
            // Create a map with one (and only one) simplified restriction for each type code.
            // We cannot use groupingBy because this will return a list of
            // restriction per type code.
            // Afterwards will add more information to the restriction.
            // FIXME: Auch hier besteht das Problem, dass 'nur' über den
            // TypeCode gruppiert wird. Das reicht nicht immer.
            Map<String, Restriction> restrictionsMap = xmlRestrictions.stream()
                    .filter(distinctByKey(RestrictionOnLandownershipType::getTypeCode))
                    .map(r -> {
                        Restriction restriction = new Restriction();
                        restriction.setInformation(r.getInformation().getLocalisedText().get(0).getText());
                        restriction.setTypeCode(r.getTypeCode());
                        if (r.getSymbol() != null) {
                            String encodedImage = Base64.encode(r.getSymbol());
                            encodedImage = "data:image/png;base64,"+encodedImage;
                            restriction.setSymbol(encodedImage);                                                    
                        } else if (r.getSymbolRef() != null) {
                            restriction.setSymbolRef(r.getSymbolRef());
                        }
                        return restriction;
                    }).collect(Collectors.toMap(Restriction::getTypeCode, Function.identity()));
//                    }).collect(Collectors.toMap(r -> {
//                        return r.getTypeCode();
//                      }, Function.identity()));
            
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

            Map<String, Double> sumAreaPercentShare = xmlRestrictions.stream()
                    .filter(r -> r.getPartInPercent() != null)
                    .collect(Collectors.groupingBy(r -> r.getTypeCode(), Collectors.summingDouble(r -> r.getPartInPercent().doubleValue())));

            
            logger.info("sumAreaShare: " + sumAreaShare.toString());
            logger.info("sumLengthShare: " + sumLengthShare.toString());
            logger.info("sumNrOfPoints: " + sumNrOfPoints.toString());
            logger.info("sumAreaPercentShare: " + sumAreaPercentShare.toString());
            
            // Assign the sum to the simplified restriction.
            // And add the restriction to the final restrictons list.
            List<Restriction> restrictionsList = new ArrayList<Restriction>();
            for (Map.Entry<String, Restriction> restrictionEntry : restrictionsMap.entrySet()) {
                String typeCode = restrictionEntry.getKey();
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
                if (sumAreaPercentShare.get(typeCode) != null) {
                    restrictionEntry.getValue().setPartInPercent(sumAreaPercentShare.get(typeCode));
                    logger.info(String.valueOf(restrictionEntry.getValue().getPartInPercent()));
                }
                restrictionsList.add(restrictionEntry.getValue());
            }
            
            // Collect responsible offices
            // Distinct by office url.
            ArrayList<Office> officeList = (ArrayList<Office>) xmlRestrictions.stream()
                    .filter(distinctByKey(r -> {
                        String officeName = r.getResponsibleOffice().getOfficeAtWeb().getValue();
                        return officeName;
                    }))
                    .map(r -> {
                        Office office = new Office();
                        if (r.getResponsibleOffice().getName() != null) {
                            office.setName(r.getResponsibleOffice().getName().getLocalisedText().get(0).getText());
                        }
                        office.setOfficeAtWeb(r.getResponsibleOffice().getOfficeAtWeb().getValue());
                        return office;
                    }).collect(Collectors.toList());

            logger.info("size of office: " + officeList.size());
            
            // Get legal provisions and laws.
            List<Document> legalProvisionsList = new ArrayList<Document>();
            List<Document> lawsList = new ArrayList<Document>();
            
            for (RestrictionOnLandownershipType xmlRestriction : xmlRestrictions) {
                List<DocumentBaseType> xmlLegalProvisions = xmlRestriction.getLegalProvisions();
                for (DocumentBaseType xmlDocumentBase : xmlLegalProvisions) {
                    DocumentType xmlLegalProvision =  (DocumentType) xmlDocumentBase;
                    Document legalProvision = new Document();
                    if (xmlLegalProvision.getTitle() != null) {
                        legalProvision.setTitle(xmlLegalProvision.getTitle().getLocalisedText().get(0).getText());
                    }
                    if (xmlLegalProvision.getOfficialTitle() != null) {
                        legalProvision.setOfficialTitle(xmlLegalProvision.getOfficialTitle().getLocalisedText().get(0).getText());
                    }
                    legalProvision.setOfficialNumber(xmlLegalProvision.getOfficialNumber());
                    if (xmlLegalProvision.getAbbreviation() != null) {
                        legalProvision.setAbbreviation(xmlLegalProvision.getAbbreviation().getLocalisedText().get(0).getText());
                    }
                    if (xmlLegalProvision.getTextAtWeb() != null) {
                        legalProvision.setTextAtWeb(xmlLegalProvision.getTextAtWeb().getLocalisedText().get(0).getText());
                    }
                    legalProvisionsList.add(legalProvision);
                    
                    List<DocumentType> xmlLaws = xmlLegalProvision.getReference();
                    for (DocumentType xmlLaw : xmlLaws) {
                        Document law = new Document();
                        if (xmlLaw.getTitle() != null) {
                            law.setTitle(xmlLaw.getTitle().getLocalisedText().get(0).getText());
                        }
                        if (xmlLaw.getOfficialTitle() != null) {
                            law.setOfficialTitle(xmlLaw.getOfficialTitle().getLocalisedText().get(0).getText());
                        }
                        law.setOfficialNumber(xmlLaw.getOfficialNumber());
                        if (xmlLaw.getAbbreviation() != null) {
                            law.setAbbreviation(xmlLaw.getAbbreviation().getLocalisedText().get(0).getText());
                        }
                        if (xmlLaw.getTextAtWeb() != null) {
                            law.setTextAtWeb(xmlLaw.getTextAtWeb().getLocalisedText().get(0).getText());
                        }
                        lawsList.add(law);
                    }
                }
            }

            // Because restrictions can share the same legal provision and laws,
            // we need to distinct them.
            List<Document> distinctLegalProvisionsList = legalProvisionsList.stream()
                    .filter(distinctByKey(Document::getTextAtWeb)).collect(Collectors.toList());
            
//            distinctLegalProvisionsList.stream().forEach(d -> {
//                logger.info(d.getAbbreviation());
//            }); 
//            List<Document> sortedLegalProvisionsList = new ArrayList<Document>();
//            if (distinctLegalProvisionsList.size() > 1) {
//                sortedLegalProvisionsList = distinctLegalProvisionsList.stream().sorted((d1, d2) -> d1.getAbbreviation().compareTo(d2.getAbbreviation())).collect(Collectors.toList());  
//            }

            List<Document> distinctLawsList = lawsList.stream().filter(distinctByKey(Document::getTextAtWeb)).collect(Collectors.toList());

            // WMS
            double layerOpacity = xmlRestrictions.get(0).getMap().getLayerOpacity();
            int layerIndex = xmlRestrictions.get(0).getMap().getLayerIndex();
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
                baseUrlBuilder.append(":"+String.valueOf(uriComponents.getPort()));
            }
            baseUrlBuilder.append(path);
            String baseUrl = baseUrlBuilder.toString();
 
            ReferenceWMS referenceWMS = new ReferenceWMS();
            referenceWMS.setBaseUrl(baseUrl);
            referenceWMS.setLayers(layers);
            referenceWMS.setImageFormat(imageFormat);
            referenceWMS.setLayerOpacity(layerOpacity);
            referenceWMS.setLayerIndex(layerIndex);
            
            // Bundesthemen haben Stand heute keine LegendeImWeb 
            String legendAtWeb = null;
            if (xmlRestrictions.get(0).getMap().getLegendAtWeb() != null) {
                legendAtWeb = xmlRestrictions.get(0).getMap().getLegendAtWeb().getValue();
            }             
                        
            // Finally we create the concerned theme with all
            // the information.
            ConcernedTheme concernedTheme =  new ConcernedTheme();
            concernedTheme.setRestrictions(restrictionsList);
            concernedTheme.setLegalProvisions(distinctLegalProvisionsList);
            concernedTheme.setLaws(distinctLawsList);
            concernedTheme.setReferenceWMS(referenceWMS);
            concernedTheme.setLegendAtWeb(legendAtWeb);
            concernedTheme.setCode(xmlRestrictions.get(0).getTheme().getCode());
            concernedTheme.setName(xmlRestrictions.get(0).getTheme().getText().getText());
            concernedTheme.setSubtheme(xmlRestrictions.get(0).getSubTheme());
            concernedTheme.setResponsibleOffice(officeList);
            
            concernedThemesList.add(concernedTheme);
        }
        
        concernedThemesList.sort(compare);
                
        realEstate.setConcernedThemes(concernedThemesList);
        extract.setRealEstate(realEstate);
        extract.setPdfLink(oerebWebServiceUrl + "/reduced/pdf/geometry/" + egrid);
               
        Office plrCadastreAuthority = new Office();
        plrCadastreAuthority.setName(xmlExtract.getPLRCadastreAuthority().getName().getLocalisedText().get(0).getText());
        plrCadastreAuthority.setOfficeAtWeb(xmlExtract.getPLRCadastreAuthority().getOfficeAtWeb().getValue());
        plrCadastreAuthority.setStreet(xmlExtract.getPLRCadastreAuthority().getStreet());
        plrCadastreAuthority.setNumber(xmlExtract.getPLRCadastreAuthority().getNumber());
        plrCadastreAuthority.setPostalCode(xmlExtract.getPLRCadastreAuthority().getPostalCode());
        plrCadastreAuthority.setCity(xmlExtract.getPLRCadastreAuthority().getCity());
        extract.setPlrCadastreAuthority(plrCadastreAuthority);
        
        ExtractResponse response = new ExtractResponse();
        response.setExtract(extract);
                
        return response;
    }
    
    Comparator<AbstractTheme> compare = new Comparator<AbstractTheme>() {
        public int compare(AbstractTheme t1, AbstractTheme t2) {            
            if (t1.getSubtheme() != null && t2.getSubtheme() == null) {
                return themesOrderingList.indexOf(t1.getSubtheme()) - themesOrderingList.indexOf(t2.getCode());
            }
            
            if (t2.getSubtheme() != null && t1.getSubtheme() == null) {
                return themesOrderingList.indexOf(t1.getCode()) - themesOrderingList.indexOf(t2.getSubtheme());
            }
            
            if (t1.getSubtheme() != null && t2.getSubtheme() != null) {
                return themesOrderingList.indexOf(t1.getSubtheme()) - themesOrderingList.indexOf(t2.getSubtheme());                
            }
            return themesOrderingList.indexOf(t1.getCode()) - themesOrderingList.indexOf(t2.getCode());
        }
   };

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
