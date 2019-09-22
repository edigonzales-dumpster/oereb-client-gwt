package com.gwidgets.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
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
import com.gwidgets.shared.models.ConcernedTheme;
import com.gwidgets.shared.models.Extract;
import com.gwidgets.shared.models.NotConcernedTheme;
import com.gwidgets.shared.models.RealEstateDPR;
import com.gwidgets.shared.models.ReferenceWMS;
import com.gwidgets.shared.models.ThemeWithoutData;

import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.ButtonSize;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialChip;
import gwt.material.design.client.ui.MaterialCollapsible;
import gwt.material.design.client.ui.MaterialCollapsibleBody;
import gwt.material.design.client.ui.MaterialCollapsibleHeader;
import gwt.material.design.client.ui.MaterialCollapsibleItem;
import gwt.material.design.client.ui.MaterialCollection;
import gwt.material.design.client.ui.MaterialCollectionItem;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRange;
import gwt.material.design.client.ui.MaterialRow;
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
import ol.View;
import ol.ViewOptions;
import ol.control.Control;
import ol.event.EventListener;
import ol.format.Wkt;
import ol.geom.Geometry;
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
import ol.source.Vector;
import ol.source.VectorOptions;
import ol.source.Wmts;
import ol.source.WmtsOptions;
import ol.style.Stroke;
import ol.style.Style;
import ol.tilegrid.TileGrid;
import ol.tilegrid.WmtsTileGrid;
import ol.tilegrid.WmtsTileGridOptions;
import proj4.Proj4;

public class AppEntryPoint implements EntryPoint {
    private PLRMessages messages = GWT.create(PLRMessages.class);
    private final ExtractServiceAsync extractService = GWT.create(ExtractService.class);
    private final SettingsServiceAsync settingsService = GWT.create(SettingsService.class);

    private String HEADER_FONT_SIZE = "18px";
    private String SUB_HEADER_FONT_SIZE = "16px";
    private String BODY_FONT_SIZE = "14px";
    
    private String ID_ATTR_NAME = "id";
    private String BACKGROUND_LAYER_ID = "ch.so.agi.hintergrundkarte_sw";
    private String REAL_ESTATE_VECTOR_LAYER_ID = "real_estate_vector_layer";
    private String REAL_ESTATE_VECTOR_FEATURE_ID = "real_estate_fid";

    // TODO: move to settings
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
    private MaterialWindow realEstateWindow;
    private MaterialCollapsible collapsibleConcernedTheme;
    private MaterialCollapsible collapsibleNotConcernedTheme;
    private MaterialCollapsible collapsibleThemesWithoutData;
    private MaterialCollapsible collapsibleGeneralInformation;

    private ArrayList<String> concernedWmsLayers = new ArrayList<String>();
    
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
                SEARCH_SERVICE_URL = (String) result.getSettings().get("SEARCH_SERVICE_URL");
                DATA_SERVICE_URL = (String) result.getSettings().get("DATA_SERVICE_URL");
                WMS_LAYER_MAPPINGS = (HashMap) result.getSettings().get("WMS_LAYER_MAPPINGS");
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

        GWT.log(WMS_LAYER_MAPPINGS.toString());
        
        GWT.log(GWT.getModuleBaseURL());
        GWT.log(GWT.getHostPageBaseURL());
        GWT.log(GWT.getModuleBaseForStaticFiles());
        
        // div for ol3 map
        Div mapDiv = new Div();
        mapDiv.setId("map");
        mapDiv.getElement().getStyle().setProperty("height", "100%");

        // Dummy button for testing with a hardcode egrid.
        MaterialButton dummyButton = new MaterialButton();
        dummyButton.setType(ButtonType.FLOATING);
        dummyButton.setSize(ButtonSize.LARGE);
        dummyButton.setIconType(IconType.HELP_OUTLINE);
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
//        cantonImage.setUrl("https://so.ch/typo3conf/ext/sfptemplate/Resources/Public/Images/Logo.png");
        // TODO: does this work in production?
        cantonImage.setUrl(GWT.getHostPageBaseURL()+"Logo.png");
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
        // It's not possible to get the object with AutocompleteType.TEXT
        // you only the the text then. But we definitely
        // need the object.
        // The chip can be made invisible with CSS. But the size
        // must be also set to zero.
        autocomplete.setPlaceholder(messages.searchPlaceholder());
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
//        resultCard.add(fadeoutDiv);

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

        if (resultsArray.size() == 0) {
            return null;
        }

        ArrayList<JSONObject> parcels = new ArrayList<JSONObject>();
        for (int i = 0; i < resultsArray.size(); i++) {
            JSONObject properties = resultsArray.get(i).isObject().get("properties").isObject();
            parcels.add(properties);
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

                // Remove all oereb layers from the map.
                // TODO: They are removed already when requesting the extract
                // from the server.
                removePlrLayers();
                
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

                // Das ist jetzt ziemlich heuristisch...
                // 500 = Breite des Suchresultates
                view.setCenter(new Coordinate(x-(500*view.getResolution())/2, y));
                
                vlayer.setZIndex(1001);
                map.addLayer(vlayer);

                resultDiv = new Div();

                MaterialRow buttonRow = new MaterialRow();
                buttonRow.setMarginBottom(25);

                MaterialColumn pdfButtonColumn = new MaterialColumn();
                pdfButtonColumn.setPadding(0);
                pdfButtonColumn.setGrid("s6");

                MaterialButton pdfButton = new MaterialButton();
                pdfButton.setIconType(IconType.INSERT_DRIVE_FILE);
                pdfButton.setType(ButtonType.FLOATING);
                pdfButton.setTooltip(messages.resultPDFTooltip());
                pdfButton.setTooltipPosition(Position.TOP);
                pdfButtonColumn.add(pdfButton);
                buttonRow.add(pdfButtonColumn);

                // TODO: Request via spring controller (nicht gwt async service). Nicht direkt das PDF aufrufen (dauert zu lange).
                // Problem: context path?
                // Wie schaffe ich es eine Sanduhr zu zeigen, die dann wieder verschwindet?
                // ajax call?
                pdfButton.addClickHandler(event -> {
//                    Window.open("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH857632820629_layer_ordering.pdf", "_target", "enabled");
                    Window.open(
                            "https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH857632820629_layer_ordering.pdf",
                            "_blank", null);
                });

                MaterialColumn deleteExtractButtonColumn = new MaterialColumn();
                deleteExtractButtonColumn.setPadding(0);
                deleteExtractButtonColumn.setGrid("s6");
                deleteExtractButtonColumn.getElement().getStyle().setProperty("textAlign", "right");

                MaterialButton deleteExtractButton = new MaterialButton();
                deleteExtractButton.setIconType(IconType.CLOSE);
                deleteExtractButton.setType(ButtonType.FLOATING);
                deleteExtractButton.setTooltip(messages.resultCloseTooltip());
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
                generalInfoTitleColumn.getElement().getStyle().setProperty("fontSize", HEADER_FONT_SIZE);
                generalInfoTitleColumn.getElement().getStyle().setProperty("fontWeight", "700");

                String lbl = messages.resultHeader(number, municipality);
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
                egridInfoKeyColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                egridInfoKeyColumn.getElement().getStyle().setProperty("fontWeight", "700");
                egridInfoKeyColumn.add(new Label("EGRID:"));
                egridInfoRow.add(egridInfoKeyColumn);

                MaterialColumn egridInfoValueColumn = new MaterialColumn();
                egridInfoValueColumn.setGrid("s10");
                egridInfoValueColumn.getElement().getStyle().setProperty("margin", "0px");
                egridInfoValueColumn.getElement().getStyle().setProperty("padding", "0px");
                egridInfoValueColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                egridInfoValueColumn.getElement().getStyle().setProperty("fontWeight", "400");
                egridInfoValueColumn.add(new Label(egrid));
                egridInfoRow.add(egridInfoValueColumn);

                MaterialRow areaInfoRow = new MaterialRow();
                areaInfoRow.getElement().getStyle().setProperty("margin", "0px");

                MaterialColumn areaInfoKeyColumn = new MaterialColumn();
                areaInfoKeyColumn.setGrid("s2");
                areaInfoKeyColumn.getElement().getStyle().setProperty("margin", "0px");
                areaInfoKeyColumn.getElement().getStyle().setProperty("padding", "0px");
                areaInfoKeyColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                areaInfoKeyColumn.getElement().getStyle().setProperty("fontWeight", "700");
                areaInfoKeyColumn.add(new Label(messages.resultArea()+":"));
                areaInfoRow.add(areaInfoKeyColumn);

                MaterialColumn areaInfoValueColumn = new MaterialColumn();
                areaInfoValueColumn.setGrid("s10");
                areaInfoValueColumn.getElement().getStyle().setProperty("margin", "0px");
                areaInfoValueColumn.getElement().getStyle().setProperty("padding", "0px");
                areaInfoValueColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                areaInfoValueColumn.getElement().getStyle().setProperty("fontWeight", "400");

                areaInfoValueColumn.add(new HTML(fmt.format(area) + " m<sup>2</sup>"));
                areaInfoRow.add(areaInfoValueColumn);

                resultDiv.add(generalInfoRow);
                resultDiv.add(egridInfoRow);
                resultDiv.add(areaInfoRow);
                                
                // TODO: rename everything except the global objects
                {
                    collapsibleConcernedTheme = new MaterialCollapsible();
//                    collapsibleConcernedTheme.setId(COLLAPSIBLE_CONCERNED_ID);
                    collapsibleConcernedTheme.setBackgroundColor(Color.GREY_LIGHTEN_5);
                    collapsibleConcernedTheme.setMarginTop(25);
                    collapsibleConcernedTheme.setShadow(0);
                    
                    collapsibleConcernedTheme.addExpandHandler(event -> {
                        collapsibleNotConcernedTheme.closeAll();
                        collapsibleThemesWithoutData.closeAll();
                        collapsibleGeneralInformation.closeAll();
                    });
                    
                    // TODO: Beim Ausschalten der WMS-Layer eventuell wieder intressant (oder bei den Sub-Collapsibles).
//                    collapsibleConcernedTheme.addCollapseHandler(event -> {
//                        GWT.log("collapsibleConcernedTheme.addCollapseHandler");                        
//                    });

                    MaterialCollapsibleItem collapsibleConcernedThemeItem = new MaterialCollapsibleItem();
                    
                    MaterialCollapsibleHeader collapsibleConcernedThemeHeader = new MaterialCollapsibleHeader();
                    collapsibleConcernedThemeHeader.setBackgroundColor(Color.GREY_LIGHTEN_3);
                    
                    MaterialRow collapsibleConcernedThemeHeaderRow = new MaterialRow();
                    collapsibleConcernedThemeHeaderRow.setMarginBottom(0);
                    
                    MaterialColumn collapsibleConcernedThemeColumnLeft = new MaterialColumn();
                    collapsibleConcernedThemeColumnLeft.setGrid("s10");
                    collapsibleConcernedThemeColumnLeft.setMargin(0);
                    collapsibleConcernedThemeColumnLeft.setPadding(0);
                    MaterialColumn collapsibleConcernedThemeColumnRight = new MaterialColumn();
                    collapsibleConcernedThemeColumnRight.setGrid("s2");
                    collapsibleConcernedThemeColumnRight.setTextAlign(TextAlign.RIGHT);
                    collapsibleConcernedThemeColumnRight.setMargin(0);
                    collapsibleConcernedThemeColumnRight.setPadding(0);

                    MaterialLink collapsibleThemesWithoutHeaderLink = new MaterialLink();
                    collapsibleThemesWithoutHeaderLink.setText(messages.concernedThemes());
                    collapsibleThemesWithoutHeaderLink.setFontWeight(FontWeight.BOLD);
                    collapsibleThemesWithoutHeaderLink.setFontSize(SUB_HEADER_FONT_SIZE);
                    collapsibleThemesWithoutHeaderLink.setTextColor(Color.BLACK);
                    collapsibleConcernedThemeColumnLeft.add(collapsibleThemesWithoutHeaderLink);
                    
                    MaterialChip collapsibleThemesWithoutHeaderChip = new MaterialChip();
                    collapsibleThemesWithoutHeaderChip.setMargin(0);
                    collapsibleThemesWithoutHeaderChip.setText(String.valueOf(realEstate.getConcernedThemes().size()));
                    collapsibleThemesWithoutHeaderChip.setBackgroundColor(Color.GREY_LIGHTEN_1);
//                    collapsibleThemesWithoutHeaderChip.setBackgroundColor(Color.RED_LIGHTEN_1);
                    collapsibleConcernedThemeColumnRight.add(collapsibleThemesWithoutHeaderChip);

                    collapsibleConcernedThemeHeaderRow.add(collapsibleConcernedThemeColumnLeft);
                    collapsibleConcernedThemeHeaderRow.add(collapsibleConcernedThemeColumnRight);

                    collapsibleConcernedThemeHeader.add(collapsibleConcernedThemeHeaderRow);
                    
                    MaterialCollapsibleBody collapsibleConcernedThemeBody = new MaterialCollapsibleBody();
                    collapsibleConcernedThemeBody.setPadding(0);
                    
                    MaterialCollapsible collapsible = new MaterialCollapsible();
                    collapsible.setAccordion(true);
                    int i=0;
                    for (ConcernedTheme theme : realEstate.getConcernedThemes()) {
                        i++;
                        
                        Image wmsLayer = createPlrWmsLayer(theme.getReferenceWMS());
                        map.addLayer(wmsLayer);

                        collapsible.setBackgroundColor(Color.GREY_LIGHTEN_3);
                        collapsible.setMarginTop(0);
                        collapsible.setMarginBottom(0);
                        collapsible.setShadow(0);
                        collapsible.setBorder("0px");

                        MaterialCollapsibleItem item = new MaterialCollapsibleItem();
                        
                        // Cannot use the code since all subthemes share
                        // the same code.
                        String layerId = theme.getReferenceWMS().getLayers();
                        item.setId(layerId);
                        concernedWmsLayers.add(layerId);
                                                
                        MaterialCollapsibleHeader header = new MaterialCollapsibleHeader();
                        header.setBackgroundColor(Color.GREY_LIGHTEN_4);
                        header.setLineHeight(18); // heuristic 
                        header.setDisplay(Display.TABLE);
                        if (i < realEstate.getConcernedThemes().size()) {
                            header.setBorderBottom("1px solid #dddddd");
                        } else {
                            header.setBorderBottom("0px solid #dddddd");
                        }
                        header.setWidth("100%");
                        header.setHeight("45px"); // Firefox
                        
                        MaterialLink link = new MaterialLink();
                        Div aParent = new Div();
                        aParent.setBorder("0px");
                        aParent.setDisplay(Display.TABLE_CELL);
                        aParent.setVerticalAlign(VerticalAlign.MIDDLE);
                        
                        link.setText(theme.getName());
                        link.setFontWeight(FontWeight.BOLD);
                        link.setFontSize(BODY_FONT_SIZE);
                        link.setTextColor(Color.BLACK);
                        link.setBorder("0px");
                        aParent.add(link);
                       
                        header.add(aParent);
                        item.add(header);
                        
                        MaterialCollapsibleBody body = new MaterialCollapsibleBody();
                        body.addMouseOverHandler(event -> {
                            body.getElement().getStyle().setCursor(Cursor.DEFAULT);
                        });
                        body.setBackgroundColor(Color.WHITE);
                        body.setPaddingLeft(15);
                        body.setPaddingRight(15);
                        body.setPaddingTop(5);
                        body.setPaddingBottom(5);
                        if (i < realEstate.getConcernedThemes().size()) {
                            body.setBorderBottom("1px solid #dddddd");
                        } else {
                            body.setBorderBottom("0px solid #dddddd");
                            body.setBorderTop("1px solid #dddddd");
                        }                        
                        
                        MaterialRow sliderRow = new MaterialRow();
                        sliderRow.setMarginBottom(15);
                        
                        MaterialColumn sliderRowLeft = new MaterialColumn();
                        sliderRowLeft.setGrid("s2");
                        MaterialColumn sliderRowRight = new MaterialColumn();
                        sliderRowRight.setGrid("s10");

                        MaterialRange slider = new MaterialRange();
                        slider.setPadding(0);
                        slider.setMin(0);
                        slider.setMax(100);
                        slider.setValue(Double.valueOf((theme.getReferenceWMS().getLayerOpacity() * 100)).intValue());
                        slider.addValueChangeHandler(event -> {
                            double opacity = slider.getValue() / 100.0;
                            wmsLayer.setOpacity(opacity);
                        });
                        sliderRowLeft.add(new Label(messages.resultOpacity() + ":"));
                        sliderRowLeft.setFontSize(BODY_FONT_SIZE);
                        sliderRowLeft.setPadding(0);
                        
                        sliderRowRight.add(slider);

                        sliderRow.add(sliderRowLeft);
                        sliderRow.add(sliderRowRight);
                        body.add(sliderRow);
                        
                        
                        // TODO: eher Tabelle. Das mit den Umbrüchen habe ich nicht wirklich im Griff.
                        MaterialRow informationHeaderRow = new MaterialRow();

                        MaterialColumn typeColumn = new MaterialColumn();
                        typeColumn.setGrid("s6");
                        typeColumn.setPadding(0);
                        typeColumn.setMarginRight(5);
                        typeColumn.setFontSize(BODY_FONT_SIZE);
                        typeColumn.add(new Label(theme.getRestrictions().get(0).getInformation() + theme.getRestrictions().get(0).getInformation()));
                        
                        MaterialColumn symbolColumn = new MaterialColumn();
                        symbolColumn.setPadding(0);
                        symbolColumn.setMarginRight(5);
                        symbolColumn.setGrid("s2");
                        symbolColumn.setTextAlign(TextAlign.RIGHT);
                        
                        com.google.gwt.user.client.ui.Image symbolImage = new com.google.gwt.user.client.ui.Image(theme.getRestrictions().get(0).getSymbol());
                        symbolImage.setWidth("40px");
                        symbolImage.getElement().getStyle().setProperty("border", "1px solid black");
                        symbolColumn.add(symbolImage);

                        MaterialColumn shareColumn = new MaterialColumn();
                        shareColumn.setPadding(0);
                        shareColumn.setGrid("s2");
                        shareColumn.add(new Label("foo"));

                        MaterialColumn sharePercentColumn = new MaterialColumn();
                        sharePercentColumn.setPadding(0);
                        sharePercentColumn.setGrid("s1");
                        sharePercentColumn.add(new Label("foo"));

                        informationHeaderRow.add(typeColumn);
                        informationHeaderRow.add(symbolColumn);
                        informationHeaderRow.add(shareColumn);
                        informationHeaderRow.add(sharePercentColumn);
                        body.add(informationHeaderRow);
                        
                        String baseUrl = theme.getReferenceWMS().getBaseUrl();
                        if (WMS_LAYER_MAPPINGS.get(baseUrl) != null) {
                            baseUrl = WMS_LAYER_MAPPINGS.get(theme.getReferenceWMS().getBaseUrl());
                        }
                        body.add(new Label(baseUrl + "?LAYERS=" + theme.getReferenceWMS().getLayers()));

                        
                        item.add(body);
                        collapsible.add(item);
                    }               
                    
                    collapsible.addExpandHandler(event -> {                       
                       String expandedLayerId = event.getTarget().getId();
                       for (String layerId : concernedWmsLayers) {
                           Image wmsLayer = (Image) getLayerById(layerId);
                           if (layerId.equalsIgnoreCase(expandedLayerId)) {
                               wmsLayer.setVisible(true);
                           } else {
                               wmsLayer.setVisible(false);
                           }
                       }
                    });
                    
                    collapsibleConcernedThemeBody.add(collapsible);

                    collapsibleConcernedThemeItem.add(collapsibleConcernedThemeHeader);
                    collapsibleConcernedThemeItem.add(collapsibleConcernedThemeBody);
                    collapsibleConcernedTheme.add(collapsibleConcernedThemeItem);

                    resultDiv.add(collapsibleConcernedTheme);
                }    

                {
                    collapsibleNotConcernedTheme = new MaterialCollapsible();
                    collapsibleNotConcernedTheme.setBackgroundColor(Color.GREY_LIGHTEN_5);
                    collapsibleNotConcernedTheme.setMarginTop(25);
                    collapsibleNotConcernedTheme.setShadow(0);

                     collapsibleNotConcernedTheme.addExpandHandler(event -> {
                        collapsibleConcernedTheme.close(1);
                        collapsibleThemesWithoutData.closeAll();
                        collapsibleGeneralInformation.closeAll();
                     });
                     
                    MaterialCollapsibleItem collapsibleNotConcernedThemeItem = new MaterialCollapsibleItem();
                    
                    MaterialCollapsibleHeader collapsibleNotConcernedThemeHeader = new MaterialCollapsibleHeader();
                    collapsibleNotConcernedThemeHeader.setBackgroundColor(Color.GREY_LIGHTEN_3);

                    MaterialRow collapsibleNotConcernedThemeHeaderRow = new MaterialRow();
                    collapsibleNotConcernedThemeHeaderRow.setMarginBottom(0);

                    MaterialColumn collapsibleNotConcernedThemeColumnLeft = new MaterialColumn();
                    collapsibleNotConcernedThemeColumnLeft.setGrid("s10");
                    collapsibleNotConcernedThemeColumnLeft.setMargin(0);
                    collapsibleNotConcernedThemeColumnLeft.setPadding(0);
                    MaterialColumn collapsibleNotConcernedThemeColumnRight = new MaterialColumn();
                    collapsibleNotConcernedThemeColumnRight.setGrid("s2");
                    collapsibleNotConcernedThemeColumnRight.setTextAlign(TextAlign.RIGHT);
                    collapsibleNotConcernedThemeColumnRight.setMargin(0);
                    collapsibleNotConcernedThemeColumnRight.setPadding(0);

                    MaterialLink collapsibleNotConcernedHeaderLink = new MaterialLink();
                    collapsibleNotConcernedHeaderLink.setText(messages.notConcernedThemes());
                    collapsibleNotConcernedHeaderLink.setFontWeight(FontWeight.BOLD);
                    collapsibleNotConcernedHeaderLink.setFontSize(SUB_HEADER_FONT_SIZE);
                    collapsibleNotConcernedHeaderLink.setTextColor(Color.BLACK);
                    collapsibleNotConcernedThemeColumnLeft.add(collapsibleNotConcernedHeaderLink);

                    MaterialChip collapsibleNotConcernedHeaderChip = new MaterialChip();
                    collapsibleNotConcernedHeaderChip.setMargin(0);
                    collapsibleNotConcernedHeaderChip.setText(String.valueOf(realEstate.getNotConcernedThemes().size()));
                    collapsibleNotConcernedHeaderChip.setBackgroundColor(Color.GREY_LIGHTEN_1);
                    collapsibleNotConcernedThemeColumnRight.add(collapsibleNotConcernedHeaderChip);

                    collapsibleNotConcernedThemeHeaderRow.add(collapsibleNotConcernedThemeColumnLeft);
                    collapsibleNotConcernedThemeHeaderRow.add(collapsibleNotConcernedThemeColumnRight);

                    collapsibleNotConcernedThemeHeader.add(collapsibleNotConcernedThemeHeaderRow);
                    
                    MaterialCollapsibleBody collapsibleBody = new MaterialCollapsibleBody();
                    collapsibleBody.addMouseOverHandler(event -> {
                        collapsibleBody.getElement().getStyle().setCursor(Cursor.DEFAULT);
                    });                    
                    collapsibleBody.setPadding(0);
                    MaterialCollection collection = new MaterialCollection();

                    for (NotConcernedTheme theme : realEstate.getNotConcernedThemes()) {
                        MaterialCollectionItem item = new MaterialCollectionItem();
                        MaterialLabel label = new MaterialLabel(theme.getName());
                        label.setFontSize(BODY_FONT_SIZE);
                        item.add(label);
                        collection.add(item);
                    }
                    collapsibleBody.add(collection);
 
                    collapsibleNotConcernedThemeItem.add(collapsibleNotConcernedThemeHeader);
                    collapsibleNotConcernedThemeItem.add(collapsibleBody);
                    collapsibleNotConcernedTheme.add(collapsibleNotConcernedThemeItem);
                    
                    resultDiv.add(collapsibleNotConcernedTheme);
                }      
                
                {
                    collapsibleThemesWithoutData = new MaterialCollapsible();
                    collapsibleThemesWithoutData.setBackgroundColor(Color.GREY_LIGHTEN_5);
                    collapsibleThemesWithoutData.setMarginTop(25);
                    collapsibleThemesWithoutData.setShadow(0);
                    
                    collapsibleThemesWithoutData.addExpandHandler(event -> {
//                        collapsibleConcernedTheme.closeAll();
                        collapsibleConcernedTheme.close(1);                    
                        collapsibleNotConcernedTheme.closeAll();
                        collapsibleGeneralInformation.closeAll();
                    });
                    
                    MaterialCollapsibleItem collapsibleThemesWithoutDataItem = new MaterialCollapsibleItem();
                    
                    MaterialCollapsibleHeader collapsibleThemesWithoutDataHeader = new MaterialCollapsibleHeader();
                    collapsibleThemesWithoutDataHeader.setBackgroundColor(Color.GREY_LIGHTEN_3);

                    MaterialRow collapsibleThemesWithoutDataHeaderRow = new MaterialRow();
                    collapsibleThemesWithoutDataHeaderRow.setMarginBottom(0);
                    
                    MaterialColumn collapsibleThemesWithoutDataColumnLeft = new MaterialColumn();
                    collapsibleThemesWithoutDataColumnLeft.setGrid("s10");
                    collapsibleThemesWithoutDataColumnLeft.setMargin(0);
                    collapsibleThemesWithoutDataColumnLeft.setPadding(0);
                    MaterialColumn collapsibleThemesWithoutDataColumnRight = new MaterialColumn();
                    collapsibleThemesWithoutDataColumnRight.setGrid("s2");
                    collapsibleThemesWithoutDataColumnRight.setTextAlign(TextAlign.RIGHT);
                    collapsibleThemesWithoutDataColumnRight.setMargin(0);
                    collapsibleThemesWithoutDataColumnRight.setPadding(0);

                    MaterialLink collapsibleThemesWithoutHeaderLink = new MaterialLink();
                    collapsibleThemesWithoutHeaderLink.setText(messages.themesWithoutData());
                    collapsibleThemesWithoutHeaderLink.setFontWeight(FontWeight.BOLD);
                    collapsibleThemesWithoutHeaderLink.setFontSize(SUB_HEADER_FONT_SIZE);
                    collapsibleThemesWithoutHeaderLink.setTextColor(Color.BLACK);
                    collapsibleThemesWithoutDataColumnLeft.add(collapsibleThemesWithoutHeaderLink);
                    
                    MaterialChip collapsibleThemesWithoutHeaderChip = new MaterialChip();
                    collapsibleThemesWithoutHeaderChip.setMargin(0);
                    collapsibleThemesWithoutHeaderChip.setText(String.valueOf(realEstate.getThemesWithoutData().size()));
                    collapsibleThemesWithoutHeaderChip.setBackgroundColor(Color.GREY_LIGHTEN_1);
                    collapsibleThemesWithoutDataColumnRight.add(collapsibleThemesWithoutHeaderChip);

                    collapsibleThemesWithoutDataHeaderRow.add(collapsibleThemesWithoutDataColumnLeft);
                    collapsibleThemesWithoutDataHeaderRow.add(collapsibleThemesWithoutDataColumnRight);

                    collapsibleThemesWithoutDataHeader.add(collapsibleThemesWithoutDataHeaderRow);
                    
                    MaterialCollapsibleBody collapsibleBody = new MaterialCollapsibleBody();
                    collapsibleBody.addMouseOverHandler(event -> {
                        collapsibleBody.getElement().getStyle().setCursor(Cursor.DEFAULT);
                    });                    
                    collapsibleBody.setPadding(0);
                    MaterialCollection collection = new MaterialCollection();
                    
                    for (ThemeWithoutData theme : realEstate.getThemesWithoutData()) {
                        MaterialCollectionItem item = new MaterialCollectionItem();
                        MaterialLabel label = new MaterialLabel(theme.getName());
                        label.setFontSize(BODY_FONT_SIZE);
                        item.add(label);
                        collection.add(item);
                    }
                    collapsibleBody.add(collection);
                                     
                    collapsibleThemesWithoutDataItem.add(collapsibleThemesWithoutDataHeader);
                    collapsibleThemesWithoutDataItem.add(collapsibleBody);
                    collapsibleThemesWithoutData.add(collapsibleThemesWithoutDataItem);

                    resultDiv.add(collapsibleThemesWithoutData);
                }
                
                {
                    collapsibleGeneralInformation = new MaterialCollapsible();
                    collapsibleGeneralInformation.setBackgroundColor(Color.GREY_LIGHTEN_5);
                    collapsibleGeneralInformation.setMarginTop(25);
                    collapsibleGeneralInformation.setShadow(0);
                    
                    collapsibleGeneralInformation.addExpandHandler(event -> {
                        collapsibleConcernedTheme.close(1);
                        collapsibleNotConcernedTheme.closeAll();
                        collapsibleThemesWithoutData.closeAll();
                     });

                    MaterialCollapsibleItem collapsibleGeneralInformationItem = new MaterialCollapsibleItem();
                    
                    MaterialCollapsibleHeader collapsibleGeneralInformationHeader = new MaterialCollapsibleHeader();
                    collapsibleGeneralInformationHeader.setBackgroundColor(Color.GREY_LIGHTEN_3);
                    
                    MaterialCollapsibleBody body = new MaterialCollapsibleBody();
                    body.addMouseOverHandler(event -> {
                        body.getElement().getStyle().setCursor(Cursor.DEFAULT);
                    });                                        
                    body.setBackgroundColor(Color.WHITE);
                    body.setFontSize(BODY_FONT_SIZE);
                    body.setPaddingLeft(15);
                    body.setPaddingRight(15);
                    body.setPaddingTop(5);
                    body.setPaddingBottom(5);

                    Div div = new Div();
                    
                    HTML infoHtml = new HTML();
                    
                    StringBuilder html = new StringBuilder();
                    html.append("<b>Katasterverantwortliche Stelle</b>");
                    html.append("<br>");
                    html.append(extract.getPlrCadastreAuthority().getName());
                    
                    infoHtml.setHTML(html.toString());
                    body.add(infoHtml);
 
                    MaterialRow collapsibleGeneralInformationHeaderRow = new MaterialRow();
                    collapsibleGeneralInformationHeaderRow.setMarginBottom(0);
                    
                    MaterialColumn collapsibleGeneralInformationColumnLeft = new MaterialColumn();
                    collapsibleGeneralInformationColumnLeft.setGrid("s10");
                    collapsibleGeneralInformationColumnLeft.setMargin(0);
                    collapsibleGeneralInformationColumnLeft.setPadding(0);
    
                    MaterialLink collapsibleThemesWithoutHeaderLink = new MaterialLink();
                    collapsibleThemesWithoutHeaderLink.setText(messages.generalInformation());
                    collapsibleThemesWithoutHeaderLink.setFontWeight(FontWeight.BOLD);
                    collapsibleThemesWithoutHeaderLink.setFontSize(SUB_HEADER_FONT_SIZE);
                    collapsibleThemesWithoutHeaderLink.setTextColor(Color.BLACK);
                    collapsibleGeneralInformationColumnLeft.add(collapsibleThemesWithoutHeaderLink);
                
                    collapsibleGeneralInformationHeaderRow.add(collapsibleGeneralInformationColumnLeft);
                    collapsibleGeneralInformationHeader.add(collapsibleGeneralInformationHeaderRow);
                    
                    collapsibleGeneralInformationItem.add(collapsibleGeneralInformationHeader);
                    collapsibleGeneralInformationItem.add(body);
                    collapsibleGeneralInformation.add(collapsibleGeneralInformationItem);

                    resultDiv.add(collapsibleGeneralInformation);
                }

                resultCardContent.add(resultDiv);
                resultCard.getElement().getStyle().setProperty("visibility", "visible");
            }
        });
    }

    private void removePlrLayers() {
        // I cannot iterate over map.getLayers() and
        // use map.removeLayers(). Seems to get some
        // confusion with the indices or whatever...
        for (String layerId : concernedWmsLayers) {
            Image rlayer = (Image) getLayerById(layerId);
            map.removeLayer(rlayer);
        }

        // Remove vector layer
        Base vlayer = getLayerById(REAL_ESTATE_VECTOR_LAYER_ID);
        map.removeLayer(vlayer);
        
        
        // Empty concernedWmsLayers list.
        concernedWmsLayers.clear();        
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
                    return item;
                }
            } catch (Exception e) {
            }

        }
        return null;
    }

    private void resetGui() {
        removePlrLayers();

        if (resultDiv != null) {
            resultCardContent.remove(resultDiv);
        }
        
        if (realEstateWindow != null) {
            realEstateWindow.removeFromParent();
        }

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

            // Remove the chip from the text field. Even if it is not visible.
            autocomplete.reset();

            // We need to find out the egrid from either the real estate or the address. 
            // This is done by using the data service extensively.
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
                                ArrayList<JSONObject> features = parseRealEstateFeatures(responseObj);
                                String egrid = features.get(0).get("egrid").toString().trim().replaceAll("^.|.$", "");

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
                // Fetch the geometry of the address.
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

                                // Get the real estate (egrid) with a bbox (= geometry of address) request.
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

                                                // Features can have multiple objects since we searched an adress.
                                                // But we don't want to show a selection. Try to force the "Liegenschaft".
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

            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, DATA_SERVICE_URL + REAL_ESTATE_DATAPRODUCT_ID + "/?bbox=" + bbox);
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
                            ArrayList<JSONObject> features = parseRealEstateFeatures(responseObj);

                            if (features.size() > 1) {
                                if (realEstateWindow != null) {
                                    realEstateWindow.removeFromParent();
                                }
                                
                                realEstateWindow = new MaterialWindow();
                                realEstateWindow.setTitle("Grundstücke");
                                realEstateWindow.setFontSize("16px");
                                realEstateWindow.setMarginLeft(0);
                                realEstateWindow.setMarginRight(0);
                                realEstateWindow.setWidth("300px");
                                realEstateWindow.setToolbarColor(Color.RED_LIGHTEN_1); 

                                MaterialIcon maximizeIcon = realEstateWindow.getIconMaximize();
                                maximizeIcon.getElement().getStyle().setProperty("visibility", "hidden");

                                realEstateWindow.setMaximize(false);
                                realEstateWindow.setTop(event.getPixel().getY());
                                realEstateWindow.setLeft(event.getPixel().getX());
                                
                                MaterialPanel realEstatePanel = new MaterialPanel();
                                
                                for (JSONObject feature : features) {
                                    String number = feature.get("nummer").toString().trim().replaceAll("^.|.$", "");
                                    String egrid = feature.get("egrid").toString().trim().replaceAll("^.|.$", "");
                                    String type = feature.get("art_txt").toString().trim().replaceAll("^.|.$", "");

                                    MaterialRow realEstateRow = new MaterialRow();
                                    realEstateRow.setId(egrid);
                                    realEstateRow.setMarginBottom(0);
                                    realEstateRow.setPadding(5);
                                    realEstateRow.add(new Label("GB-Nr.: " + number + " (" + type.substring(type.lastIndexOf(".") + 1) + ")"));
                                   
                                    realEstateRow.addClickHandler(event -> {
                                        
                                        realEstateWindow.removeFromParent();

                                        GWT.log(realEstateRow.getId());
                                        // make rpc call
                                        
                                    });
                                    
                                    realEstateRow.addMouseOverHandler(event -> {
                                        realEstateRow.setBackgroundColor(Color.GREY_LIGHTEN_3);
                                        realEstateRow.getElement().getStyle().setCursor(Cursor.POINTER); 
                                    });
                                    
                                    realEstateRow.addMouseOutHandler(event -> {
                                        realEstateRow.setBackgroundColor(Color.WHITE);
                                        realEstateRow.getElement().getStyle().setCursor(Cursor.DEFAULT); 
                                    });
                                    
                                    realEstatePanel.add(realEstateRow);
                                }

                                realEstateWindow.add(realEstatePanel);
                                realEstateWindow.open();

                                /*
                                DivElement overlay = Document.get().createDivElement();
                                overlay.setClassName("overlay-realestate-list");
                                overlay.setInnerText("Created with GWT SDK " + GWT.getVersion());
                                
                                OverlayOptions overlayOptions = OLFactory.createOptions();
                                overlayOptions.setElement(overlay);
                                overlayOptions.setPosition(coordinate);
                                overlayOptions.setOffset(OLFactory.createPixel(0, 0));
                                map.addOverlay(new Overlay(overlayOptions));
                                */
                            } else {
                                String egrid = features.get(0).get("egrid").toString().trim().replaceAll("^.|.$", "");
                                GWT.log("get extract from click for: " + egrid);

                            }
                            
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
    
    public Image createPlrWmsLayer(ReferenceWMS referenceWms) {
        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(referenceWms.getLayers());

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
        
        String baseUrl = referenceWms.getBaseUrl();
        if (WMS_LAYER_MAPPINGS.get(baseUrl) != null) {
            baseUrl = WMS_LAYER_MAPPINGS.get(referenceWms.getBaseUrl());
        }
        
        imageWMSOptions.setUrl(baseUrl);
        imageWMSOptions.setParams(imageWMSParams);
        imageWMSOptions.setRatio(1.5f);

        ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

        LayerOptions layerOptions = OLFactory.createOptions();
        layerOptions.setSource(imageWMSSource);

        Image wmsLayer = new Image(layerOptions);
        wmsLayer.set(ID_ATTR_NAME, referenceWms.getLayers());
        wmsLayer.setVisible(false);
        wmsLayer.setOpacity(referenceWms.getLayerOpacity());
        // It works for the Grundbuchplan but not for 
        // the Landeskarten. They are not transparent.
//        wmsLayer.setZIndex(referenceWms.getLayerIndex());

        return wmsLayer;
    }
}