package com.gwidgets.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.transform.stream.StreamSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.gwidgets.shared.ExtractResponse;
import com.gwidgets.shared.ExtractService;
import com.gwidgets.shared.models.Extract;
import com.gwidgets.shared.models.ReferenceWMS;

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
        
        URL url = new URL(oerebWebServiceUrl + egrid);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        try (FileOutputStream xmlOutputStream = new FileOutputStream(xmlFile)) {
            xmlOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        logger.info("File downloaded: " + xmlFile.getAbsolutePath());

        StreamSource xmlSource = new StreamSource(xmlFile);
        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);

        List<ThemeType> concernedThemes = obj.getValue().getExtract().getValue().getConcernedTheme();
        
        String wmsUrl = "";
        ReferenceWMS referenceWMS = new ReferenceWMS();
        List<RestrictionOnLandownershipType> restrictions = obj.getValue().getExtract().getValue().getRealEstate().getRestrictionOnLandownership();
        for (RestrictionOnLandownershipType restriction : restrictions) {
//            logger.info(restriction.getMap().getReferenceWMS());
            
            if (restriction.getTheme().getCode().contains("LandUsePlans")) {
                wmsUrl = URLDecoder.decode(restriction.getMap().getReferenceWMS(), StandardCharsets.UTF_8.name());
                logger.info(wmsUrl);
                
                referenceWMS.setBaseUrl("https://geoview.bl.ch/main/oereb/mapservproxy?");
                referenceWMS.setLayers("LandUsePlans");
                referenceWMS.setImageFormat("image%2Fpng");
            }
        }
        
        MultiSurfacePropertyTypeType multiSurfacePropertyTypeType = obj.getValue().getExtract().getValue().getRealEstate().getLimit();
        MultiPolygon realEstatePolygon = new Gml32ToJts().convertMultiSurface(multiSurfacePropertyTypeType);
        logger.info(realEstatePolygon.toText());
        
        Extract extract = new Extract();
        extract.setExtractIdentifier(obj.getValue().getExtract().getValue().getExtractIdentifier());
        extract.setReferenceWMS(referenceWMS);
        extract.setGeometry(new WKTWriter(3).write(realEstatePolygon));
       
        ExtractResponse response = new ExtractResponse();
        response.setEgrid("lilalaunebär");
        response.setExtract(extract);
        
        
        return response;
    }

}
