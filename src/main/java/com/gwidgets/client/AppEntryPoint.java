package com.gwidgets.client;


import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import com.gwidgets.shared.ExtractResponse;
import com.gwidgets.shared.ExtractService;
import com.gwidgets.shared.ExtractServiceAsync;
import com.gwidgets.shared.SettingsResponse;
import com.gwidgets.shared.SettingsService;
import com.gwidgets.shared.SettingsServiceAsync;

import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
import gwt.material.design.addins.client.autocomplete.constants.AutocompleteType;
import gwt.material.design.client.constants.ButtonSize;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialPreLoader;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialSpinner;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.html.Div;
import ol.Collection;
import ol.Coordinate;
import ol.Extent;
import ol.Feature;
import ol.FeatureOptions;
import ol.Map;
import ol.MapOptions;
import ol.OLFactory;
import ol.Overlay;
import ol.View;
import ol.ViewOptions;
import ol.control.Control;
import ol.control.Rotate;
import ol.control.ScaleLine;
import ol.control.Zoom;
import ol.format.Wkt;
import ol.geom.Geometry;
import ol.interaction.KeyboardPan;
import ol.interaction.KeyboardZoom;
import ol.layer.Base;
import ol.layer.Image;
import ol.layer.LayerOptions;
import ol.layer.Tile;
import ol.layer.VectorLayerOptions;
import ol.proj.Projection;
import ol.proj.ProjectionOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;
import ol.source.Osm;
import ol.source.Vector;
import ol.source.VectorOptions;
import ol.source.Wmts;
import ol.source.WmtsOptions;
import ol.source.XyzOptions;
import ol.style.Stroke;
import ol.style.Style;
import ol.color.*;
import ol.tilegrid.TileGrid;
import ol.tilegrid.WmtsTileGrid;
import ol.tilegrid.WmtsTileGridOptions;
import proj4.Proj4;

public class AppEntryPoint implements EntryPoint {
    private final ExtractServiceAsync extractService = GWT.create(ExtractService.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);

    private String SEARCH_SERVICE_URL;
    private HashMap<String,String> WMS_LAYER_MAPPINGS;
    private String baseUrl = "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?limit=15&query=egr+";

    private Map map;
    
    public void onModuleLoad() {
        // Get the needed settings from the server with an async call.
        settingsService.settingsServer(new AsyncCallback<SettingsResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log("error: " + caught.getMessage());
                MaterialToast.fireToast(caught.getMessage());
            }

            @Override
            public void onSuccess(SettingsResponse result) {
                SEARCH_SERVICE_URL = result.getSettings().get("SEARCH_SERVICE_URL");
                init();
            }
        });
    }

    private void init() {
        GWT.log(SEARCH_SERVICE_URL);
        
        // div for ol3 map
        Div mapDiv = new Div();
        mapDiv.setId("map");
        mapDiv.getElement().getStyle().setProperty("height", "100%");
        
        // Dummy button for testing with a hardcode egrid.
        MaterialButton dummyButton = new MaterialButton();
        dummyButton.setType(ButtonType.FLOATING);
        dummyButton.setSize(ButtonSize.LARGE);
        dummyButton.setIconType(IconType.SENTIMENT_VERY_DISSATISFIED);
        dummyButton.getElement().getStyle().setProperty("position", "absolute");
        dummyButton.getElement().getStyle().setProperty("top", "40px");
        dummyButton.getElement().getStyle().setProperty("right", "40px");
        


        
        // A material card for the controls.
        MaterialCard controlsCard = new MaterialCard();
        controlsCard.setBackgroundColor(Color.GREY_LIGHTEN_5);
        controlsCard.getElement().getStyle().setProperty("transition", "height 0.5s");
        controlsCard.getElement().getStyle().setProperty("position", "absolute");
        controlsCard.getElement().getStyle().setProperty("marginTop", "15px");
        controlsCard.getElement().getStyle().setProperty("marginLeft", "15px");
        controlsCard.getElement().getStyle().setProperty("top", "0px");
        controlsCard.getElement().getStyle().setProperty("left", "0px");
        controlsCard.getElement().getStyle().setProperty("width", "500px");
        controlsCard.getElement().getStyle().setProperty("height", "200px");
        controlsCard.getElement().getStyle().setProperty("overflowY", "auto");

        MaterialCardContent controlsCardContent = new MaterialCardContent();
        controlsCardContent.getElement().getStyle().setProperty("padding", "15px");

        MaterialRow logoRow = new MaterialRow();
        
        com.google.gwt.user.client.ui.Image plrImage = new com.google.gwt.user.client.ui.Image();
        plrImage.setUrl("https://geoview.bl.ch/main/oereb/logos/logo_oereb_small.png");
        plrImage.setWidth("200px");
        
        MaterialColumn plrLogoColumn =  new MaterialColumn();
        plrLogoColumn.setGrid("s6");
        plrLogoColumn.getElement().getStyle().setProperty("margin", "0px");
        plrLogoColumn.getElement().getStyle().setProperty("padding", "0px");
        plrLogoColumn.add(plrImage);
        
        com.google.gwt.user.client.ui.Image cantonImage = new com.google.gwt.user.client.ui.Image();
        cantonImage.setUrl("https://so.ch/typo3conf/ext/sfptemplate/Resources/Public/Images/Logo.png");
        cantonImage.setWidth("200px");

        MaterialColumn cantonLogoColumn =  new MaterialColumn();
        cantonLogoColumn.setGrid("s6");
        cantonLogoColumn.getElement().getStyle().setProperty("margin", "0px");
        cantonLogoColumn.getElement().getStyle().setProperty("padding", "0px");
        cantonLogoColumn.getElement().getStyle().setProperty("textAlign", "right");
        cantonLogoColumn.add(cantonImage);

        logoRow.add(plrLogoColumn);
        logoRow.add(cantonLogoColumn);
        controlsCardContent.add(logoRow);
        
//        MaterialLabel label = new MaterialLabel();
//        label.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \n" + 
//                "\n" + 
//                "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.   \n" + 
//                "\n" + 
//                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.   \n" + 
//                "\n" + 
//                "Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer");
//        controlsCardContent.add(label);
        
        
        MaterialRow searchRow = new MaterialRow();
        
        // TODO: rename
        UserOracle userOracle = new UserOracle();

        MaterialAutoComplete autocomplete = new MaterialAutoComplete(userOracle);
        autocomplete.setType(AutocompleteType.TEXT);
        autocomplete.setPlaceholder("Suche");
//        autocomplete.setTextColor(Color.RED_DARKEN_4);
        autocomplete.setAutoSuggestLimit(5);
        // FIXME: remove text on select

        autocomplete.addValueChangeHandler(event -> {
            GWT.log(autocomplete.getItemBox().getText());
            MaterialToast.fireToast(autocomplete.getItemBox().getText());
        });

        searchRow.add(autocomplete);
        controlsCardContent.add(searchRow);
      
        controlsCard.add(controlsCardContent);
        

        
//        Div fadeoutDiv = new Div();
//        fadeoutDiv.getElement().getStyle().setProperty("position", "sticky");
//        fadeoutDiv.getElement().getStyle().setProperty("bottom", "0");
//        fadeoutDiv.getElement().getStyle().setProperty("width", "100%");
//        fadeoutDiv.getElement().getStyle().setProperty("padding", "30px 0");
//        fadeoutDiv.getElement().getStyle().setProperty("backgroundImage", "linear-gradient(rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 1) 100%)");
//        card1.add(fadeoutDiv);
        
        // Add all the widgets to the body.
        RootPanel.get().add(dummyButton);
        RootPanel.get().add(mapDiv);
        RootPanel.get().add(controlsCard);

        // Add click event to the dummy button.
        dummyButton.addClickHandler(event -> {
            GWT.log("push the button");
            
            MaterialLoader.loading(true);
            sendEgridToServer();
        });
        
        // Initialize openlayers map.
        initMap(mapDiv.getId());

//      ImageWmsParams imageWMSParams = OLFactory.createOptions();
//      imageWMSParams.setLayers("ch.swisstopo.geologie-geotechnik-gk500-gesteinsklassierung,ch.bafu.schutzgebiete-paerke_nationaler_bedeutung");
//
//      ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
//      imageWMSOptions.setUrl("http://wms.geo.admin.ch/");
//      imageWMSOptions.setParams(imageWMSParams);
//      imageWMSOptions.setRatio(1.5f);
//
//      ImageWms imageWMSSource = new ImageWms(imageWMSOptions);
//
//      LayerOptions layerOptions = OLFactory.createOptions();
//      layerOptions.setSource(imageWMSSource);
//
//      Image wmsLayer = new Image(layerOptions);
//
//      // create a projection
//      ProjectionOptions projectionOptions = OLFactory.createOptions();
//      projectionOptions.setCode("EPSG:21781");
//      projectionOptions.setUnits("m");
//
//      Projection projection = new Projection(projectionOptions);
//      // projection.setExtent()...
//
//      // create a view
//      ViewOptions viewOptions = OLFactory.createOptions();
//      viewOptions.setProjection(projection);
//      View view = new View(viewOptions);
//     
//      Coordinate centerCoordinate = new Coordinate(660000, 190000);
//
//      view.setCenter(centerCoordinate);
//      view.setZoom(9);
//
//      // create the map
//      MapOptions mapOptions = OLFactory.createOptions();
//      mapOptions.setTarget(mapDiv.getId());
//      mapOptions.setView(view);
//      mapOptions.setControls(new Collection<Control>());
//
//      Map map = new Map(mapOptions);
//
//      map.addLayer(wmsLayer);
    }
    
//  CH158782774974
//  CH944982786913
//  CH938278494529
    private void sendEgridToServer() {
        extractService.extractServer("CH158782774974", new AsyncCallback<ExtractResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialLoader.loading(false);
                GWT.log("error: " + caught.getMessage());
                MaterialToast.fireToast(caught.getMessage());
            }

            @Override
            public void onSuccess(ExtractResponse result) {
                MaterialLoader.loading(false);
                GWT.log(result.getEgrid());
                GWT.log(result.getExtract().getExtractIdentifier());
                GWT.log(result.getExtract().getReferenceWMS().getBaseUrl());
                GWT.log(result.getExtract().getReferenceWMS().getLayers());
                GWT.log(result.getExtract().getGeometry());
                
                FeatureOptions featureOptions = OLFactory.createOptions();
                Geometry realEstateGeometry = new Wkt().readGeometry(result.getExtract().getGeometry());
                featureOptions.setGeometry(realEstateGeometry);
                
                Feature feature = new Feature(featureOptions);
                feature.setId("f1");

                Style style = new Style();
                Stroke stroke = new Stroke();
                stroke.setWidth(5);
//                stroke.setColor(new Color(230,0,0,0.6));
                style.setStroke(stroke);
                feature.setStyle(style);
                
                Collection<Feature> lstFeatures = new Collection<Feature>();
                lstFeatures.push(feature);
                
                VectorOptions vectorSourceOptions = OLFactory.createOptions();
                vectorSourceOptions.setFeatures(lstFeatures);
                Vector vectorSource = new Vector(vectorSourceOptions);

                VectorLayerOptions vectorLayerOptions = OLFactory.createOptions();
                vectorLayerOptions.setSource(vectorSource);
                ol.layer.Vector vectorLayer = new ol.layer.Vector(vectorLayerOptions);
                vectorLayer.set("id", "real_estate");

                
                ImageWmsParams imageWMSParams = OLFactory.createOptions();
                imageWMSParams.setLayers(result.getExtract().getReferenceWMS().getLayers());

                ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
                imageWMSOptions.setUrl(result.getExtract().getReferenceWMS().getBaseUrl());
                imageWMSOptions.setParams(imageWMSParams);
                imageWMSOptions.setRatio(1.5f);

                ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

                LayerOptions layerOptions = OLFactory.createOptions();
                layerOptions.setSource(imageWMSSource);

                Image wmsLayer = new Image(layerOptions);
                wmsLayer.set("id", result.getExtract().getReferenceWMS().getLayers());              
              
//              map.addOverlay(overlay);
                map.addLayer(wmsLayer);
                
                map.addLayer(vectorLayer);

                Collection<Base> layers = map.getLayers();
                for (int i = 0; i < layers.getLength(); i++) {
                    Base item = layers.item(i);
                    GWT.log(item.toString());
                    // GWT.log(item.get("id").toString());
                    try {
                        GWT.log(item.get("id").toString());
                    } catch (Exception e) {
                    }

                }              
              
                // TODO
//                controlsCard.setHeight("400px");

            }
        });
    }
    
    private void initMap(String id) {
        // create a projection
        Proj4.defs("EPSG:2056", "+proj=somerc +lat_0=46.95240555555556 +lon_0=7.439583333333333 +k_0=1 +x_0=2600000 +y_0=1200000 +ellps=bessel +towgs84=674.374,15.056,405.346,0,0,0,0 +units=m +no_defs");

        ProjectionOptions projectionOptions = OLFactory.createOptions();
        projectionOptions.setCode("EPSG:2056");
        projectionOptions.setUnits("m");
        projectionOptions.setExtent(new Extent(2420000, 1030000, 2900000, 1350000));

        Projection projection = new Projection(projectionOptions);

        WmtsOptions wmtsOptions = OLFactory.createOptions();
        wmtsOptions.setUrl(
                "https://geo.so.ch/api/wmts/1.0.0/{Layer}/default/2056/{TileMatrix}/{TileRow}/{TileCol}");
        wmtsOptions.setLayer("ch.so.agi.hintergrundkarte_sw");
        wmtsOptions.setRequestEncoding("REST");
        wmtsOptions.setFormat("image/png");
        wmtsOptions.setMatrixSet("EPSG:2056");
        wmtsOptions.setStyle("default");
        wmtsOptions.setProjection(projection);
        wmtsOptions.setWrapX(true);
        wmtsOptions.setTileGrid(this.createWmtsTileGrid(projection));

        Wmts wmtsSource = new Wmts(wmtsOptions);

        LayerOptions wmtsLayerOptions = OLFactory.createOptions();
        wmtsLayerOptions.setSource(wmtsSource);

        Tile wmtsLayer = new Tile(wmtsLayerOptions);
        wmtsLayer.setOpacity(1.0);
        wmtsLayer.set("id", "ch.so.agi.hintergrundkarte_sw");

        // create a view
        ViewOptions viewOptions = OLFactory.createOptions();
        viewOptions.setProjection(projection);
        viewOptions.setResolutions(new double[] {4000.0, 2000.0, 1000.0, 500.0, 250.0, 100.0, 50.0, 20.0, 10.0, 5.0, 2.5, 1.0, 0.5, 0.25, 0.1 });
        View view = new View(viewOptions);

        Coordinate centerCoordinate = new Coordinate(2616491, 1240287);

        view.setCenter(centerCoordinate);
        view.setZoom(6);

        // create the map
        MapOptions mapOptions = OLFactory.createOptions();
        mapOptions.setTarget(id);
        mapOptions.setView(view);
        mapOptions.setControls(new Collection<Control>());
        
        map = new Map(mapOptions);

        // add layers
        map.addLayer(wmtsLayer);
    }

    private TileGrid createWmtsTileGrid(Projection projection) {
        WmtsTileGridOptions wmtsTileGridOptions = OLFactory.createOptions();

        double resolutions[] = new double[] {4000.0, 2000.0, 1000.0, 500.0, 250.0, 100.0, 50.0, 20.0, 10.0, 5.0, 2.5,
                1.0, 0.5, 0.25, 0.1};
        String[] matrixIds = new String[resolutions.length];

        for (int z = 0; z < resolutions.length; ++z) {
            matrixIds[z] = String.valueOf(z);
        }

        Coordinate tileGridOrigin = projection.getExtent().getTopLeft();
        wmtsTileGridOptions.setOrigin(tileGridOrigin);
        wmtsTileGridOptions.setResolutions(resolutions);
        wmtsTileGridOptions.setMatrixIds(matrixIds);
        
        return new WmtsTileGrid(wmtsTileGridOptions);
    }
    
    private Base getLayerById(String id) {
        return null;
    }
}