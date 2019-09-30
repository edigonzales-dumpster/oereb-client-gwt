package ch.so.agi.oereb.webclient.server;

import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

import ch.ehi.oereb.schemas.gml._3_2.Interior;
//import ch.ehi.oereb.schemas.gml._3_2.LinearRing;
import ch.ehi.oereb.schemas.gml._3_2.MultiSurfacePropertyTypeType;
import ch.ehi.oereb.schemas.gml._3_2.MultiSurfaceTypeType;
//import ch.ehi.oereb.schemas.gml._3_2.Polygon;
import ch.ehi.oereb.schemas.gml._3_2.Pos;
import ch.ehi.oereb.schemas.gml._3_2.SurfaceMember;

public class Gml32ToJts {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public MultiPolygon convertMultiSurface(MultiSurfacePropertyTypeType multiSurfacePropertyTypeType) {
        PrecisionModel precisionModel = new PrecisionModel(1000);
        GeometryFactory factory = new GeometryFactory(precisionModel);
        
        MultiSurfaceTypeType multiSurfaceTypeType = multiSurfacePropertyTypeType.getMultiSurface().getValue();
        logger.debug(String.valueOf(multiSurfaceTypeType.getSurfaceMember().size()));
        
        ArrayList<Polygon> polygonList = new ArrayList<Polygon>();
        for (SurfaceMember surfaceMember : multiSurfaceTypeType.getSurfaceMember()) {
            logger.debug(String.valueOf(surfaceMember.getValue().getAbstractSurface().toString()));
            
            ch.ehi.oereb.schemas.gml._3_2.Polygon gmlPolygon = (ch.ehi.oereb.schemas.gml._3_2.Polygon) surfaceMember.getValue().getAbstractSurface();
            
            // exterior ring
            ch.ehi.oereb.schemas.gml._3_2.LinearRing gmlLinearRing = (ch.ehi.oereb.schemas.gml._3_2.LinearRing) gmlPolygon.getValue().getExterior().getValue().getAbstractRing();
            ArrayList<Coordinate> exteriorCoordinates = new ArrayList<Coordinate>();
            for (int i=0; i<gmlLinearRing.getValue().getPosOrPointPropertyOrPointRep().size(); i++) {
                Pos pos = (Pos) gmlLinearRing.getValue().getPosOrPointPropertyOrPointRep().get(i);
//                logger.info(pos.getValue().getValue().get(0).toString());
//                logger.info(pos.getValue().getValue().get(1).toString());
                
                double x = pos.getValue().getValue().get(0);
                double y = pos.getValue().getValue().get(1);
                
                Coordinate coordinate = new Coordinate(x, y);
                exteriorCoordinates.add(coordinate);
            }
            
            LinearRing exteriorRing = factory.createLinearRing(new CoordinateArraySequence((Coordinate[]) exteriorCoordinates.toArray(new Coordinate[0])));
            logger.debug(exteriorRing.toText());
            
            // interior ring(s)
            List<Interior> interiors = gmlPolygon.getValue().getInterior();
            ArrayList<LinearRing> interiorRings = new ArrayList<LinearRing>();
            for (Interior interior : interiors) {
                ch.ehi.oereb.schemas.gml._3_2.LinearRing gmlInteriorRing = (ch.ehi.oereb.schemas.gml._3_2.LinearRing) interior.getValue().getAbstractRing();
                ArrayList<Coordinate> interiorCoordinates = new ArrayList<Coordinate>();
                for (int i=0; i<gmlInteriorRing.getValue().getPosOrPointPropertyOrPointRep().size(); i++) {
                    Pos pos = (Pos) gmlInteriorRing.getValue().getPosOrPointPropertyOrPointRep().get(i);
//                    logger.info(pos.getValue().getValue().get(0).toString());
//                    logger.info(pos.getValue().getValue().get(1).toString());

                    double x = pos.getValue().getValue().get(0);
                    double y = pos.getValue().getValue().get(1);

                    Coordinate coordinate = new Coordinate(x, y);
                    interiorCoordinates.add(coordinate);
                }    
                LinearRing interiorRing = factory.createLinearRing(new CoordinateArraySequence((Coordinate[]) interiorCoordinates.toArray(new Coordinate[0])));
                logger.debug(interiorRing.toText());

                // This is for you Clemens:
                if (interiorRing.equals(exteriorRing)) {
                    continue;
                }
                interiorRings.add(interiorRing);
            }
            
            Polygon polygon = factory.createPolygon(exteriorRing, interiorRings.toArray(new LinearRing[0]));
            logger.debug(polygon.toText());
            
            polygonList.add(polygon);
        }
        
        MultiPolygon multiPolygon = factory.createMultiPolygon(polygonList.toArray(new Polygon[0]));
        return multiPolygon;
    }
}
