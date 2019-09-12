package com.gwidgets.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.collectingAndThen;

import javax.servlet.ServletException;
import javax.xml.transform.stream.StreamSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.gwidgets.shared.ExtractResponse;
import com.gwidgets.shared.ExtractService;
import com.gwidgets.shared.models.Extract;
import com.gwidgets.shared.models.NotConcernedTheme;
import com.gwidgets.shared.models.RealEstateDPR;
import com.gwidgets.shared.models.ReferenceWMS;
import com.gwidgets.shared.models.ThemeWithoutData;

import ch.ehi.oereb.schemas.gml._3_2.Coordinates;
import ch.ehi.oereb.schemas.gml._3_2.LinearRing;
import ch.ehi.oereb.schemas.gml._3_2.LinearRingTypeType;
import ch.ehi.oereb.schemas.gml._3_2.MultiSurface;
import ch.ehi.oereb.schemas.gml._3_2.MultiSurfaceProperty;
import ch.ehi.oereb.schemas.gml._3_2.MultiSurfacePropertyTypeType;
import ch.ehi.oereb.schemas.gml._3_2.MultiSurfaceTypeType;
import ch.ehi.oereb.schemas.gml._3_2.PolygonTypeType;
import ch.ehi.oereb.schemas.gml._3_2.Pos;
import ch.ehi.oereb.schemas.gml._3_2.PosList;
import ch.ehi.oereb.schemas.gml._3_2.SurfaceMember;
import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExtractType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RealEstateDPRType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RestrictionOnLandownershipType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ThemeType;

import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTWriter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

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
        
//        URL url = new URL(oerebWebServiceUrl + egrid);
//        URL url = new URL("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH533287066291.xml");
        URL url = new URL("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH368132060914.xml");
        logger.info(url.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        InputStream initialStream = connection.getInputStream();
        java.nio.file.Files.copy(initialStream, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        logger.info("File downloaded: " + xmlFile.getAbsolutePath());

        Extract extract = new Extract();
        
        StreamSource xmlSource = new StreamSource(xmlFile);
        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
        ExtractType xmlExtract = obj.getValue().getExtract().getValue();
        
        extract.setExtractIdentifier(xmlExtract.getExtractIdentifier());

        // FIXME: sorting?!
        LinkedList<ThemeWithoutData> themesWithoutData = xmlExtract.getThemeWithoutData().stream()
                .map(theme -> { 
                    ThemeWithoutData themeWithoutData = new ThemeWithoutData();
                    themeWithoutData.setCode(theme.getCode());
                    themeWithoutData.setName(theme.getText().getText());
                    return themeWithoutData; 
                })
                .collect(collectingAndThen(toList(), LinkedList<ThemeWithoutData>::new));

        LinkedList<NotConcernedTheme> notConcernedThemes = xmlExtract.getNotConcernedTheme().stream()
                .map(theme -> { 
                    NotConcernedTheme notConcernedTheme = new NotConcernedTheme();
                    notConcernedTheme.setCode(theme.getCode());
                    notConcernedTheme.setName(theme.getText().getText());
                    return notConcernedTheme; 
                })
                .collect(collectingAndThen(toList(), LinkedList<NotConcernedTheme>::new));

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
        
        xmlRealEstate.getRestrictionOnLandownership().stream().forEach(restriction -> {
            logger.info(restriction.getTheme().getText().getText());
            logger.info(restriction.getTheme().getCode());
        });
        
//        String wmsUrl = "";
//        ReferenceWMS referenceWMS = new ReferenceWMS();
//        List<RestrictionOnLandownershipType> restrictions = obj.getValue().getExtract().getValue().getRealEstate().getRestrictionOnLandownership();
//        for (RestrictionOnLandownershipType restriction : restrictions) {
////            logger.info(restriction.getMap().getReferenceWMS());
//            
//            if (restriction.getTheme().getCode().contains("LandUsePlans")) {
//                wmsUrl = URLDecoder.decode(restriction.getMap().getReferenceWMS(), StandardCharsets.UTF_8.name());
//                logger.info(wmsUrl);
//                
//                referenceWMS.setBaseUrl("https://geoview.bl.ch/main/oereb/mapservproxy?");
//                referenceWMS.setLayers("LandUsePlans");
//                referenceWMS.setImageFormat("image%2Fpng");
//            }
//        }
//        
//        MultiSurfacePropertyTypeType multiSurfacePropertyTypeType = obj.getValue().getExtract().getValue().getRealEstate().getLimit();
//        MultiPolygon realEstatePolygon = new Gml32ToJts().convertMultiSurface(multiSurfacePropertyTypeType);
//        logger.info(realEstatePolygon.toText());
        
//        Extract extract = new Extract();
//        extract.setExtractIdentifier(obj.getValue().getExtract().getValue().getExtractIdentifier());
//        extract.setReferenceWMS(referenceWMS);
//        extract.setGeometry(new WKTWriter(3).write(realEstatePolygon));
       
        ExtractResponse response = new ExtractResponse();
        response.setEgrid("lilalaunebär");
//        response.setExtract(extract);
        
        
        return response;
    }

}
