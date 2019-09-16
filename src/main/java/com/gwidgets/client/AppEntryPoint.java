package com.gwidgets.client;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.gwidgets.shared.ExtractResponse;
import com.gwidgets.shared.ExtractService;
import com.gwidgets.shared.ExtractServiceAsync;
import com.gwidgets.shared.SettingsResponse;
import com.gwidgets.shared.SettingsService;
import com.gwidgets.shared.SettingsServiceAsync;
import com.gwidgets.shared.models.Extract;
import com.gwidgets.shared.models.RealEstateDPR;

import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
import gwt.material.design.addins.client.autocomplete.constants.AutocompleteType;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.ButtonSize;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
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
import ol.MapBrowserEvent;
import ol.MapOptions;
import ol.OLFactory;
import ol.Overlay;
import ol.OverlayOptions;
import ol.View;
import ol.ViewOptions;
import ol.control.Control;
import ol.control.Rotate;
import ol.control.ScaleLine;
import ol.control.Zoom;
import ol.event.EventListener;
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

    private String ID_ATTR_NAME = "id";
    private String BACKGROUND_LAYER_ID = "ch.so.agi.hintergrundkarte_sw";
    private String REAL_ESTATE_VECTOR_LAYER_ID = "real_estate_vector_layer";
    private String REAL_ESTATE_VECTOR_FEATURE_ID = "real_estate_fid";

    private String REAL_ESTATE_DATAPRODUCT_ID = "ch.so.agi.av.grundstuecke.rechtskraeftig";
    private String ADDRESS_DATAPRODUCT_ID = "ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge";

    private String SEARCH_SERVICE_URL;
    private String DATA_SERVICE_URL;
    private HashMap<String, String> WMS_LAYER_MAPPINGS;

    private NumberFormat fmt = NumberFormat.getDecimalFormat();

    private MaterialAutoComplete autocomplete;
    private Map map;
    private MaterialCard controlsCard;
    private MaterialCard resultCard;
    private MaterialCardContent controlsCardContent;
    private MaterialCardContent resultCardContent;
    private Div resultDiv;

    public void onModuleLoad() {
        // Get the settings from the server with an async call.
        settingsService.settingsServer(new AsyncCallback<SettingsResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log("error: " + caught.getMessage());
                MaterialToast.fireToast(caught.getMessage());
            }

            @Override
            public void onSuccess(SettingsResponse result) {
                SEARCH_SERVICE_URL = result.getSettings().get("SEARCH_SERVICE_URL");
                DATA_SERVICE_URL = result.getSettings().get("DATA_SERVICE_URL");
                init();
            }
        });
    }

    private void init() {
        // TODO: get extract by url
//        String egridRequestValue = Window.Location.getParameter("egrid");        
//        GWT.log(Window.Location.getHost());
//        GWT.log(Window.Location.getHostName());
//        GWT.log(Window.Location.getPath());
//        GWT.log(Window.Location.getQueryString());
//        GWT.log(Window.Location.getProtocol());
//        GWT.log(Window.Location.getHash());
//        GWT.log(Window.Location.getHref());

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

        // A material card for the search.
        controlsCard = new MaterialCard();
        controlsCard.setBackgroundColor(Color.GREY_LIGHTEN_5);
        controlsCard.getElement().getStyle().setProperty("transition", "height 0.5s");
        controlsCard.getElement().getStyle().setProperty("position", "absolute");
        controlsCard.getElement().getStyle().setProperty("marginTop", "0px");
        controlsCard.getElement().getStyle().setProperty("marginLeft", "0px");
        controlsCard.getElement().getStyle().setProperty("marginBottom", "0px");
        controlsCard.getElement().getStyle().setProperty("top", "15px");
        controlsCard.getElement().getStyle().setProperty("left", "15px");
        controlsCard.getElement().getStyle().setProperty("width", "500px");
        controlsCard.getElement().getStyle().setProperty("height", "200px");
        controlsCard.getElement().getStyle().setProperty("overflowY", "auto");

        controlsCardContent = new MaterialCardContent();
        controlsCardContent.getElement().getStyle().setProperty("padding", "15px");

        MaterialRow logoRow = new MaterialRow();

        com.google.gwt.user.client.ui.Image plrImage = new com.google.gwt.user.client.ui.Image();
        plrImage.setUrl("https://geoview.bl.ch/main/oereb/logos/logo_oereb_small.png");
        plrImage.setWidth("200px");

        MaterialColumn plrLogoColumn = new MaterialColumn();
        plrLogoColumn.setGrid("s6");
        plrLogoColumn.getElement().getStyle().setProperty("margin", "0px");
        plrLogoColumn.getElement().getStyle().setProperty("padding", "0px");
        plrLogoColumn.add(plrImage);

        com.google.gwt.user.client.ui.Image cantonImage = new com.google.gwt.user.client.ui.Image();
        cantonImage.setUrl("https://so.ch/typo3conf/ext/sfptemplate/Resources/Public/Images/Logo.png");
        cantonImage.setWidth("200px");

        MaterialColumn cantonLogoColumn = new MaterialColumn();
        cantonLogoColumn.setGrid("s6");
        cantonLogoColumn.getElement().getStyle().setProperty("margin", "0px");
        cantonLogoColumn.getElement().getStyle().setProperty("padding", "0px");
        cantonLogoColumn.getElement().getStyle().setProperty("textAlign", "right");
        cantonLogoColumn.add(cantonImage);

        logoRow.add(plrLogoColumn);
        logoRow.add(cantonLogoColumn);
        controlsCardContent.add(logoRow);

        MaterialRow searchRow = new MaterialRow();

        SearchOracle searchOracle = new SearchOracle(SEARCH_SERVICE_URL);
        autocomplete = new MaterialAutoComplete(searchOracle);
        // It's not possible to get the object with type=text
        // you only the the text then. But we definitely
        // need the object.
        // The chip can be made invisible with CSS. But the size
        // must be also set to zero.
//        autocomplete.setType(AutocompleteType.TEXT);
        autocomplete.setPlaceholder("Suche: Grundstücke und Adressen");
        autocomplete.setAutoSuggestLimit(5);
        autocomplete.setLimit(1);
        autocomplete.addValueChangeHandler(new SearchValueChangeHandler());

        searchRow.add(autocomplete);
        controlsCardContent.add(searchRow);

        controlsCard.add(controlsCardContent);

        // A material card for the result.
        resultCard = new MaterialCard();
        resultCard.setBackgroundColor(Color.GREY_LIGHTEN_5);
        resultCard.getElement().getStyle().setProperty("transition", "height 0.5s");
        resultCard.getElement().getStyle().setProperty("position", "absolute");
        resultCard.getElement().getStyle().setProperty("marginTop", "0px");
        resultCard.getElement().getStyle().setProperty("marginLeft", "0px");
        resultCard.getElement().getStyle().setProperty("marginBottom", "0px");
        resultCard.getElement().getStyle().setProperty("top", "230px");
        resultCard.getElement().getStyle().setProperty("left", "15px");
        resultCard.getElement().getStyle().setProperty("width", "500px");
        resultCard.getElement().getStyle().setProperty("height", "calc(100% - 245px)");
        resultCard.getElement().getStyle().setProperty("overflowY", "auto");
        resultCard.getElement().getStyle().setProperty("visibility", "hidden");

        resultCardContent = new MaterialCardContent();
        resultCardContent.getElement().getStyle().setProperty("padding", "15px");
        resultCard.add(resultCardContent);

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
        RootPanel.get().add(resultCard);

        // Add click event to the dummy button.
        dummyButton.addClickHandler(new DummyButtonClickHandler());

        // Initialize openlayers map with background wmts layer.
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

    private ArrayList<JSONObject> parseRealEstateFeatures(JSONObject obj) {
        JSONObject rootObj = obj.isObject();
        JSONArray resultsArray = rootObj.get("features").isArray();

        GWT.log(resultsArray.toString());

        if (resultsArray.size() == 0) {
            return null;
        }

        ArrayList<JSONObject> parcels = new ArrayList<JSONObject>();
        for (int i = 0; i < resultsArray.size(); i++) {
            JSONObject properties = resultsArray.get(i).isObject().get("properties").isObject();
//            GWT.log(properties.toString());
            parcels.add(properties);
//            SearchResult searchResult = new SearchResult();
//            searchResult.setDisplay(properties.get("display").toString().trim().replaceAll("^.|.$", ""));
//            searchResult.setDataproductId(properties.get("dataproduct_id").toString().trim().replaceAll("^.|.$", ""));
//            searchResult.setFeatureId(properties.get("feature_id").toString().trim());
//            searchResult.setIdFieldName(properties.get("id_field_name").toString().trim().replaceAll("^.|.$", ""));
//            list.add(new SearchSuggestion(searchResult));
        }
        return parcels;
    }

    private String getBboxFromPointFeature(JSONObject obj) {
        JSONObject rootObj = obj.isObject();
        JSONArray resultsArray = rootObj.get("features").isArray();

        if (resultsArray.size() == 0) {
            return null;
        }

        JSONObject geometry = resultsArray.get(0).isObject().get("geometry").isObject();
        JSONArray coordinates = geometry.get("coordinates").isArray();
        String bbox = coordinates.get(0) + "," + coordinates.get(1) + "," + coordinates.get(0) + ","
                + coordinates.get(1);

        return bbox;
    }

//  CH158782774974
//  CH944982786913
//  CH938278494529

// CH533287066291 (SO)
    private void sendEgridToServer() {
        extractService.extractServer("CH533287066291", new AsyncCallback<ExtractResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialLoader.loading(false);
                GWT.log("error: " + caught.getMessage());
                MaterialToast.fireToast(caught.getMessage());
            }

            @Override
            public void onSuccess(ExtractResponse result) {
                MaterialLoader.loading(false);

                Extract extract = result.getExtract();
                RealEstateDPR realEstate = extract.getRealEstate();
                String number = realEstate.getNumber();
                String municipality = realEstate.getMunicipality();
                String subunitOfLandRegister = realEstate.getSubunitOfLandRegister();
                String canton = realEstate.getCanton();
                String egrid = realEstate.getEgrid();
                int area = realEstate.getLandRegistryArea();

                GWT.log(result.getExtract().getExtractIdentifier().toString());
//                GWT.log(result.getExtract().getExtractIdentifier());
//                GWT.log(result.getExtract().getReferenceWMS().getBaseUrl());
//                GWT.log(result.getExtract().getReferenceWMS().getLayers());
//                GWT.log(result.getExtract().getGeometry());

                // Remove all oereb layers from the map.
                // TODO: They are removed already when requesting the extract
                // from the server.
                removeOerebLayers();

                // FIXME do i need this anymore?
//                ol.layer.Vector vlayer = (ol.layer.Vector) getLayerById(REAL_ESTATE_VECTOR_LAYER_ID);
//                if (vlayer != null) {
//                    map.removeLayer(vlayer);
//                }

                // create the vector layer for highlighting the real estate
                ol.layer.Vector vlayer = createRealEstateVectorLayer(result.getExtract().getRealEstate().getLimit());

                // set new extent and center according the real estate
                Geometry geometry = new Wkt().readGeometry(result.getExtract().getRealEstate().getLimit());
                Extent extent = geometry.getExtent();

                View view = map.getView();
                double resolution = view.getResolutionForExtent(extent);
                view.setZoom(Math.floor(view.getZoomForResolution(resolution)) - 1);

                double x = extent.getLowerLeftX() + extent.getWidth() / 2;
                double y = extent.getLowerLeftY() + extent.getHeight() / 2;

                view.setCenter(new Coordinate(x, y));

//                ImageWmsParams imageWMSParams = OLFactory.createOptions();
//                imageWMSParams.setLayers(result.getExtract().getReferenceWMS().getLayers());
//
//                ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
//                imageWMSOptions.setUrl(result.getExtract().getReferenceWMS().getBaseUrl());
//                imageWMSOptions.setParams(imageWMSParams);
//                imageWMSOptions.setRatio(1.5f);
//
//                ImageWms imageWMSSource = new ImageWms(imageWMSOptions);
//
//                LayerOptions layerOptions = OLFactory.createOptions();
//                layerOptions.setSource(imageWMSSource);
//
//                Image wmsLayer = new Image(layerOptions);
//                wmsLayer.set("id", result.getExtract().getReferenceWMS().getLayers());              
//              
////              map.addOverlay(overlay);
//                map.addLayer(wmsLayer);
//
                // add vector layer for hightlighting the real estate
                map.addLayer(vlayer);
//                
//                Collection<Base> layers = map.getLayers();
//                for (int i = 0; i < layers.getLength(); i++) {
//                    Base item = layers.item(i);
//                    GWT.log(item.toString());
//                    // GWT.log(item.get("id").toString());
//                    try {
//                        GWT.log(item.get("id").toString());
//                    } catch (Exception e) {
//                    }
//
//                }              

                // TODO
//                controlsCard.setHeight("400px");

//                if (generalInfoDiv != null) {
//                    controlsCardContent.remove(generalInfoDiv);
//                }

//                controlsCard.setHeight("400px");
//                controlsCard.setHeight("100%");
//                controlsCard.getElement().getStyle().setProperty("height", "calc(100% - 30px)");

                resultDiv = new Div();

                MaterialRow buttonRow = new MaterialRow();
//                buttonRow.setGrid("s12");
//                buttonRow.getElement().getStyle().setProperty("margin", "0px");

                MaterialColumn pdfButtonColumn = new MaterialColumn();
                pdfButtonColumn.setGrid("s6");

                MaterialButton pdfButton = new MaterialButton();
                pdfButton.setIconType(IconType.INSERT_DRIVE_FILE);
                pdfButton.setType(ButtonType.FLOATING);
                pdfButton.setTooltip("Auszug als PDF anfordern");
                pdfButton.setTooltipPosition(Position.TOP);
                pdfButtonColumn.add(pdfButton);
                buttonRow.add(pdfButtonColumn);

                pdfButton.addClickHandler(event -> {
//                    Window.open("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH857632820629_layer_ordering.pdf", "_target", "enabled");
                    Window.open(
                            "https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH857632820629_layer_ordering.pdf",
                            "_blank", null);
                });

                MaterialColumn deleteExtractButtonColumn = new MaterialColumn();
                deleteExtractButtonColumn.setGrid("s6");
                deleteExtractButtonColumn.getElement().getStyle().setProperty("textAlign", "right");

                MaterialButton deleteExtractButton = new MaterialButton();
                deleteExtractButton.setIconType(IconType.CLOSE);
                deleteExtractButton.setType(ButtonType.FLOATING);
                deleteExtractButton.setTooltip("Auszug schliessen");
                deleteExtractButton.setTooltipPosition(Position.TOP);
                deleteExtractButtonColumn.add(deleteExtractButton);
                buttonRow.add(deleteExtractButtonColumn);

                deleteExtractButton.addClickHandler(event -> {
                    resetGui();
                });

                resultDiv.add(buttonRow);

                MaterialRow generalInfoRow = new MaterialRow();
                generalInfoRow.getElement().getStyle().setProperty("marginBottom", "10px");

                MaterialColumn generalInfoTitleColumn = new MaterialColumn();
                generalInfoTitleColumn.setGrid("s12");
                generalInfoTitleColumn.getElement().getStyle().setProperty("margin", "0px");
                generalInfoTitleColumn.getElement().getStyle().setProperty("padding", "0px");
                generalInfoTitleColumn.getElement().getStyle().setProperty("fontSize", "16px");
                generalInfoTitleColumn.getElement().getStyle().setProperty("fontWeight", "700");

                String lbl = "Grundstück " + number + " in " + municipality;
                if (!municipality.contains("(")) {
                    lbl += " (" + canton + ")";
                }
                generalInfoTitleColumn.add(new Label(lbl));
                generalInfoRow.add(generalInfoTitleColumn);

                MaterialRow egridInfoRow = new MaterialRow();
                egridInfoRow.getElement().getStyle().setProperty("margin", "0px");

                MaterialColumn egridInfoKeyColumn = new MaterialColumn();
                egridInfoKeyColumn.setGrid("s2");
                egridInfoKeyColumn.getElement().getStyle().setProperty("margin", "0px");
                egridInfoKeyColumn.getElement().getStyle().setProperty("padding", "0px");
                egridInfoKeyColumn.getElement().getStyle().setProperty("fontSize", "14px");
                egridInfoKeyColumn.getElement().getStyle().setProperty("fontWeight", "700");
                egridInfoKeyColumn.add(new Label("EGRID:"));
                egridInfoRow.add(egridInfoKeyColumn);

                MaterialColumn egridInfoValueColumn = new MaterialColumn();
                egridInfoValueColumn.setGrid("s10");
                egridInfoValueColumn.getElement().getStyle().setProperty("margin", "0px");
                egridInfoValueColumn.getElement().getStyle().setProperty("padding", "0px");
                egridInfoValueColumn.getElement().getStyle().setProperty("fontSize", "14px");
                egridInfoValueColumn.getElement().getStyle().setProperty("fontWeight", "400");
                egridInfoValueColumn.add(new Label(egrid));
                egridInfoRow.add(egridInfoValueColumn);

                MaterialRow areaInfoRow = new MaterialRow();
                areaInfoRow.getElement().getStyle().setProperty("margin", "0px");

                MaterialColumn areaInfoKeyColumn = new MaterialColumn();
                areaInfoKeyColumn.setGrid("s2");
                areaInfoKeyColumn.getElement().getStyle().setProperty("margin", "0px");
                areaInfoKeyColumn.getElement().getStyle().setProperty("padding", "0px");
                areaInfoKeyColumn.getElement().getStyle().setProperty("fontSize", "14px");
                areaInfoKeyColumn.getElement().getStyle().setProperty("fontWeight", "700");
                areaInfoKeyColumn.add(new Label("Fläche:"));
                areaInfoRow.add(areaInfoKeyColumn);

                MaterialColumn areaInfoValueColumn = new MaterialColumn();
                areaInfoValueColumn.setGrid("s10");
                areaInfoValueColumn.getElement().getStyle().setProperty("margin", "0px");
                areaInfoValueColumn.getElement().getStyle().setProperty("padding", "0px");
                areaInfoValueColumn.getElement().getStyle().setProperty("fontSize", "14px");
                areaInfoValueColumn.getElement().getStyle().setProperty("fontWeight", "400");

                areaInfoValueColumn.add(new HTML(fmt.format(area) + " m<sup>2</sup>"));
                areaInfoRow.add(areaInfoValueColumn);

                resultDiv.add(generalInfoRow);
                resultDiv.add(egridInfoRow);
                resultDiv.add(areaInfoRow);
                resultCardContent.add(resultDiv);
                resultCard.getElement().getStyle().setProperty("visibility", "visible");
            }
        });
    }

    private void removeOerebLayers() {
        Collection<Base> layers = map.getLayers();
        for (int i = 0; i < layers.getLength(); i++) {
            Base item = layers.item(i);
            try {
                String layerId = item.get(ID_ATTR_NAME);
                // do not delete background layer from map
                if (item.get(ID_ATTR_NAME).toString().equalsIgnoreCase(BACKGROUND_LAYER_ID)) {
                    continue;
                }
                map.removeLayer(item);
            } catch (Exception e) {
            }
        }
    }

    private ol.layer.Vector createRealEstateVectorLayer(String geometry) {
        FeatureOptions featureOptions = OLFactory.createOptions();
        Geometry realEstateGeometry = new Wkt().readGeometry(geometry);
        featureOptions.setGeometry(realEstateGeometry);

        Feature feature = new Feature(featureOptions);
        feature.setId(REAL_ESTATE_VECTOR_FEATURE_ID);

        Style style = new Style();
        Stroke stroke = new Stroke();
        stroke.setWidth(8);
        stroke.setColor(new ol.color.Color(230, 0, 0, 0.6));
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
        vectorLayer.set(ID_ATTR_NAME, REAL_ESTATE_VECTOR_LAYER_ID);

        return vectorLayer;
    }

    private void initMap(String id) {
        Proj4.defs("EPSG:2056",
                "+proj=somerc +lat_0=46.95240555555556 +lon_0=7.439583333333333 +k_0=1 +x_0=2600000 +y_0=1200000 +ellps=bessel +towgs84=674.374,15.056,405.346,0,0,0,0 +units=m +no_defs");

        ProjectionOptions projectionOptions = OLFactory.createOptions();
        projectionOptions.setCode("EPSG:2056");
        projectionOptions.setUnits("m");
        projectionOptions.setExtent(new Extent(2420000, 1030000, 2900000, 1350000));

        Projection projection = new Projection(projectionOptions);

        WmtsOptions wmtsOptions = OLFactory.createOptions();
        wmtsOptions.setUrl("https://geo.so.ch/api/wmts/1.0.0/{Layer}/default/2056/{TileMatrix}/{TileRow}/{TileCol}");
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
        wmtsLayer.set(ID_ATTR_NAME, BACKGROUND_LAYER_ID);

        ViewOptions viewOptions = OLFactory.createOptions();
        viewOptions.setProjection(projection);
        viewOptions.setResolutions(new double[] { 4000.0, 2000.0, 1000.0, 500.0, 250.0, 100.0, 50.0, 20.0, 10.0, 5.0,
                2.5, 1.0, 0.5, 0.25, 0.1 });
        View view = new View(viewOptions);

        Coordinate centerCoordinate = new Coordinate(2616491, 1240287);

        view.setCenter(centerCoordinate);
        view.setZoom(6);

        MapOptions mapOptions = OLFactory.createOptions();
        mapOptions.setTarget(id);
        mapOptions.setView(view);
        mapOptions.setControls(new Collection<Control>());

        map = new Map(mapOptions);

        map.addLayer(wmtsLayer);

        map.addSingleClickListener(new MapSingleClickListener());
    }

    private TileGrid createWmtsTileGrid(Projection projection) {
        WmtsTileGridOptions wmtsTileGridOptions = OLFactory.createOptions();

        double resolutions[] = new double[] { 4000.0, 2000.0, 1000.0, 500.0, 250.0, 100.0, 50.0, 20.0, 10.0, 5.0, 2.5,
                1.0, 0.5, 0.25, 0.1 };
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
        Collection<Base> layers = map.getLayers();
        for (int i = 0; i < layers.getLength(); i++) {
            Base item = layers.item(i);

            try {
                String layerId = item.get(ID_ATTR_NAME);
                if (item.get(ID_ATTR_NAME).toString().equalsIgnoreCase(id)) {
                    GWT.log("FOUND");
                    return item;
                }
            } catch (Exception e) {
            }

        }
        return null;
    }

    private void resetGui() {
        removeOerebLayers();

        if (resultDiv != null) {
            resultCardContent.remove(resultDiv);
        }

//      controlsCard.setHeight(INIT_CONTROLS_CART_HEIGHT);

        resultCard.getElement().getStyle().setProperty("visibility", "hidden");
    }

    public class DummyButtonClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            MaterialLoader.loading(true);

            resetGui();

            sendEgridToServer();
        }
    }

    public class SearchValueChangeHandler implements ValueChangeHandler {
        @Override
        public void onValueChange(ValueChangeEvent event) {
            resetGui();

            // We only allow one result in the autocomplete widget.
            List<? extends SuggestOracle.Suggestion> values = (List<? extends Suggestion>) event.getValue();
            SearchSuggestion searchSuggestion = (SearchSuggestion) values.get(0);
            SearchResult searchResult = searchSuggestion.getSearchResult();

            String dataproductId = searchResult.getDataproductId();
            String featureId = searchResult.getFeatureId();
            String idFieldName = searchResult.getIdFieldName();

            // Remove the chip from the text field. Even if it is not
            // visible.
            autocomplete.reset();

            // We need to find out the egrid. This is done by using the data service
            // extensively.
            if (dataproductId.equalsIgnoreCase(REAL_ESTATE_DATAPRODUCT_ID)) {
                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, DATA_SERVICE_URL + dataproductId
                        + "/?filter=[[\"" + idFieldName + "\",\"=\"," + featureId + "]]");
                builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

                try {
                    builder.sendRequest("", new RequestCallback() {
                        @Override
                        public void onResponseReceived(com.google.gwt.http.client.Request request,
                                com.google.gwt.http.client.Response response) {
                            int statusCode = response.getStatusCode();
                            if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                                String responseBody = response.getText();
                                JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
//                                String egrid = getEgridFromRealEstateFeature(responseObj);
                                ArrayList<JSONObject> features = parseRealEstateFeatures(responseObj);
                                String egrid = features.get(0).get("egrid").toString().trim().replaceAll("^.|.$", "");

//                                GWT.log("get extract for: " + egrid);

                                // TODO: make rpc request.

                                return;
                            } else {
                                GWT.log("error from request");
                                GWT.log(String.valueOf(statusCode));
                                GWT.log(response.getStatusText());
                            }
                        }

                        @Override
                        public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                            GWT.log("error actually sending the request, never got sent");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, DATA_SERVICE_URL + dataproductId
                        + "/?filter=[[\"" + idFieldName + "\",\"=\"," + featureId + "]]");
                builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

                try {
                    builder.sendRequest("", new RequestCallback() {
                        @Override
                        public void onResponseReceived(com.google.gwt.http.client.Request request,
                                com.google.gwt.http.client.Response response) {
                            int statusCode = response.getStatusCode();
                            if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                                String responseBody = response.getText();
                                JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
                                String bbox = getBboxFromPointFeature(responseObj);

                                // Get the real estate with a bbox request to get the egrid.
                                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                                        DATA_SERVICE_URL + REAL_ESTATE_DATAPRODUCT_ID + "/?bbox=" + bbox);
                                builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

                                try {
                                    builder.sendRequest("", new RequestCallback() {
                                        @Override
                                        public void onResponseReceived(com.google.gwt.http.client.Request request,
                                                com.google.gwt.http.client.Response response) {
                                            int statusCode = response.getStatusCode();
                                            if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                                                String responseBody = response.getText();
                                                JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));

                                                // TODO: test it
                                                // Features can have multiple objects since we searched an adress.
                                                // But we don't want to show a selection. Try to force the "Liegenschaft"
                                                String egrid = "";
                                                ArrayList<JSONObject> features = parseRealEstateFeatures(responseObj);
                                                for (JSONObject feature : features) {
                                                    egrid = feature.get("egrid").toString().trim().replaceAll("^.|.$", "");
                                                    String type = feature.get("art_txt").toString().trim().replaceAll("^.|.$", "");
                                                    if (type.equalsIgnoreCase("Liegenschaft")) {
                                                        continue;
                                                    }
                                                }

                                                // TODO egrid can be null or ""
                                                GWT.log("get extract for: " + egrid);

                                                // TODO: make rpc request.

                                                return;
                                            } else {
                                                GWT.log("error from request");
                                                GWT.log(String.valueOf(statusCode));
                                                GWT.log(response.getStatusText());
                                            }
                                        }

                                        @Override
                                        public void onError(com.google.gwt.http.client.Request request,
                                                Throwable exception) {
                                            GWT.log("error actually sending the request, never got sent");
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                GWT.log("error from request");
                                GWT.log(String.valueOf(statusCode));
                                GWT.log(response.getStatusText());
                            }
                        }

                        @Override
                        public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                            GWT.log("error actually sending the request, never got sent");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final class MapSingleClickListener implements EventListener<MapBrowserEvent> {
        @Override
        public void onEvent(MapBrowserEvent event) {
            Coordinate coordinate = event.getCoordinate();
            String bbox = coordinate.getX() + "," + coordinate.getY() + "," + coordinate.getX() + ","
                    + coordinate.getY();

            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                    DATA_SERVICE_URL + REAL_ESTATE_DATAPRODUCT_ID + "/?bbox=" + bbox);
            builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

            try {
                builder.sendRequest("", new RequestCallback() {
                    @Override
                    public void onResponseReceived(com.google.gwt.http.client.Request request,
                            com.google.gwt.http.client.Response response) {
                        int statusCode = response.getStatusCode();
                        if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                            String responseBody = response.getText();
                            JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
//                            String egrid = getEgridFromRealEstateFeature(responseObj);
                            ArrayList<JSONObject> features = parseRealEstateFeatures(responseObj);

                            if (features.size() > 1) {
                                MaterialWindow window = new MaterialWindow();
                                window.setTitle("gaga");
                                window.setMarginLeft(0);
                                window.setMarginRight(0);
                                window.setWidth("300px");
                                window.setToolbarColor(Color.RED_DARKEN_2);

                                MaterialIcon maximizeIcon = window.getIconMaximize();
                                maximizeIcon.getElement().getStyle().setProperty("visibility", "hidden");

                                window.setMaximize(false);
                                window.setTop(event.getPixel().getY());
                                window.setLeft(event.getPixel().getX());
                                window.open();

//                                DivElement overlay = Document.get().createDivElement();
//                                overlay.setClassName("overlay-realestate-list");
//                                overlay.setInnerText("Created with GWT SDK " + GWT.getVersion());
                                //
//                                Element windowOverlayElement = window.getElement();
//                                windowOverlayElement.setClassName("window open");
//                                
//                                OverlayOptions overlayOptions = OLFactory.createOptions();
//                                overlayOptions.setElement(windowOverlayElement);
//                                overlayOptions.setPosition(coordinate);
//                                overlayOptions.setOffset(OLFactory.createPixel(0, 0));
//                                map.addOverlay(new Overlay(overlayOptions));
                            } else {
                                String egrid = features.get(0).get("egrid").toString().trim().replaceAll("^.|.$", "");
                                GWT.log("get extract from click for: " + egrid);

                            }

//                            MaterialLink myLink = new MaterialLink();
//                            myLink.setText("foobar");
//                            controlsCardContent.add(myLink);

                            // TODO: make rpc request.

                            return;
                        } else {
                            GWT.log("error from request");
                            GWT.log(String.valueOf(statusCode));
                            GWT.log(response.getStatusText());
                        }
                    }

                    @Override
                    public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                        GWT.log("error actually sending the request, never got sent");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}