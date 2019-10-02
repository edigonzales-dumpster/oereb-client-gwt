package ch.so.agi.oereb.webclient.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import ch.so.agi.oereb.webclient.shared.ExtractResponse;
import ch.so.agi.oereb.webclient.shared.ExtractService;
import ch.so.agi.oereb.webclient.shared.ExtractServiceAsync;
import ch.so.agi.oereb.webclient.shared.SettingsResponse;
import ch.so.agi.oereb.webclient.shared.SettingsService;
import ch.so.agi.oereb.webclient.shared.SettingsServiceAsync;
import ch.so.agi.oereb.webclient.shared.models.ConcernedTheme;
import ch.so.agi.oereb.webclient.shared.models.Extract;
import ch.so.agi.oereb.webclient.shared.models.NotConcernedTheme;
import ch.so.agi.oereb.webclient.shared.models.Office;
import ch.so.agi.oereb.webclient.shared.models.RealEstateDPR;
import ch.so.agi.oereb.webclient.shared.models.ReferenceWMS;
import ch.so.agi.oereb.webclient.shared.models.Restriction;
import ch.so.agi.oereb.webclient.shared.models.ThemeWithoutData;

import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.ButtonSize;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.IconPosition;
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
import gwt.material.design.client.ui.html.Span;
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
    private String SMALL_FONT_SIZE = "12px";
    
    private String ID_ATTR_NAME = "id";
    private String BACKGROUND_LAYER_ID = "ch.so.agi.hintergrundkarte_sw";
    private String REAL_ESTATE_VECTOR_LAYER_ID = "real_estate_vector_layer";
    private String REAL_ESTATE_VECTOR_FEATURE_ID = "real_estate_fid";
    private String REAL_ESTATE_DATAPRODUCT_ID = "ch.so.agi.av.grundstuecke.rechtskraeftig"; // TODO -> settings
    private String ADDRESS_DATAPRODUCT_ID = "ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge"; // TODO -> settings
    
    private String OEREB_SERVICE_URL;
    private String SEARCH_SERVICE_URL;
    private String DATA_SERVICE_URL;
    private HashMap<String, String> WMS_HOST_MAPPING;

    private NumberFormat fmtDefault = NumberFormat.getDecimalFormat();
    private NumberFormat fmtPercent = NumberFormat.getFormat("#0.0");

    private MaterialAutoComplete autocomplete;
    private Map map;
    private MaterialCard controlsCard;
    private MaterialCard resultCard;
    private MaterialRow buttonRow;
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
                OEREB_SERVICE_URL = (String) result.getSettings().get("OEREB_SERVICE_URL");
                SEARCH_SERVICE_URL = (String) result.getSettings().get("SEARCH_SERVICE_URL");
                DATA_SERVICE_URL = (String) result.getSettings().get("DATA_SERVICE_URL");
                WMS_HOST_MAPPING = (HashMap<String, String>) result.getSettings().get("WMS_HOST_MAPPING");
//                OEREB_WEB_SERVICE_HOST_MAPPING = (HashMap<String, String>) result.getSettings().get("OEREB_WEB_SERVICE_HOST_MAPPING");
                init();
            }
        });
    }

    private void init() {        
        GWT.log(OEREB_SERVICE_URL.toString());
        GWT.log(WMS_HOST_MAPPING.toString());
        
        GWT.log(GWT.getModuleBaseURL());
        GWT.log(GWT.getHostPageBaseURL());
        GWT.log(GWT.getModuleBaseForStaticFiles());
        
        //Window.alert(WMS_HOST_MAPPING.get("http://wms:80/wms/oereb"));
        
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
        controlsCard.getElement().getStyle().setProperty("position", "absolute");
        controlsCard.getElement().getStyle().setProperty("marginTop", "0px");
        controlsCard.getElement().getStyle().setProperty("marginLeft", "0px");
        controlsCard.getElement().getStyle().setProperty("marginBottom", "0px");
        controlsCard.getElement().getStyle().setProperty("top", "15px");
        controlsCard.getElement().getStyle().setProperty("left", "15px");
        controlsCard.getElement().getStyle().setProperty("width", "500px");
        controlsCard.getElement().getStyle().setProperty("height", "200px");
        controlsCard.getElement().getStyle().setProperty("overflowY", "auto");
        controlsCard.setMaxWidth("calc(100% - 30px)");

        controlsCardContent = new MaterialCardContent();
        controlsCardContent.getElement().getStyle().setProperty("padding", "15px");

        MaterialRow logoRow = new MaterialRow();

        com.google.gwt.user.client.ui.Image plrImage = new com.google.gwt.user.client.ui.Image();
        plrImage.setUrl("https://geoview.bl.ch/main/oereb/logos/logo_oereb_small.png");
//        plrImage.setWidth("200px");
        plrImage.setWidth("80%");

        MaterialColumn plrLogoColumn = new MaterialColumn();
        plrLogoColumn.setGrid("s6");
        plrLogoColumn.getElement().getStyle().setProperty("margin", "0px");
        plrLogoColumn.getElement().getStyle().setProperty("padding", "0px");
        plrLogoColumn.add(plrImage);

        com.google.gwt.user.client.ui.Image cantonImage = new com.google.gwt.user.client.ui.Image();
//        cantonImage.setUrl("https://so.ch/typo3conf/ext/sfptemplate/Resources/Public/Images/Logo.png");
        // TODO: does this work in production?
        cantonImage.setUrl(GWT.getHostPageBaseURL()+"Logo.png");
//        cantonImage.setWidth("200px");
        cantonImage.setWidth("80%");

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
        autocomplete.setBorder("1px #455a64 solid");
        autocomplete.setPadding(5);
        autocomplete.setBorderRadius("10px");
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
//        MaterialCardContent foo = new MaterialCardContent();
//        foo.add(new Label("test"));
//        controlsCard.add(foo);

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
        resultCard.setMaxWidth("calc(100% - 30px)");
      
        resultCardContent = new MaterialCardContent();
        resultCardContent.getElement().getStyle().setProperty("paddingTop", "0px");
        resultCardContent.getElement().getStyle().setProperty("paddingRight", "15px");
        resultCardContent.getElement().getStyle().setProperty("paddingLeft", "15px");
        resultCardContent.getElement().getStyle().setProperty("paddingBottom", "15px");
//        resultCardContent.getElement().getStyle().setProperty("position", "relative");
//
//        Div hideResultDiv = new Div();
//        hideResultDiv.setWidth("50px");
//        hideResultDiv.setHeight("50px");
//        hideResultDiv.getElement().getStyle().setProperty("position", "absolute");
//        hideResultDiv.getElement().getStyle().setProperty("bottom", "0px");
//        hideResultDiv.getElement().getStyle().setProperty("right", "0px");
//        hideResultDiv.getElement().getStyle().setProperty("backgroundColor", "hotpink");
//        resultCardContent.add(hideResultDiv);
        
        resultCard.add(resultCardContent);
        
        Div fadeoutBottomDiv = new Div();
        fadeoutBottomDiv.getElement().getStyle().setProperty("position", "sticky");
        fadeoutBottomDiv.getElement().getStyle().setProperty("bottom", "0");
        fadeoutBottomDiv.getElement().getStyle().setProperty("width", "100%");
        fadeoutBottomDiv.getElement().getStyle().setProperty("padding", "30px 0");
        fadeoutBottomDiv.getElement().getStyle().setProperty("backgroundImage", "linear-gradient(rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 1) 100%)");
        resultCard.add(fadeoutBottomDiv);
        
        // Add all the widgets to the body.
        RootPanel.get().add(dummyButton);
        RootPanel.get().add(mapDiv);
        RootPanel.get().add(controlsCard);
        RootPanel.get().add(resultCard);

        // Add click event to the dummy button.
        dummyButton.addClickHandler(new DummyButtonClickHandler());

        // Initialize openlayers map with background wmts layer.
        initMap(mapDiv.getId());
        
        // If there is an egrid query parameter in the url,
        // we request the extract without further interaction.
        if (Window.Location.getParameter("egrid") != null) {
            String egrid = Window.Location.getParameter("egrid").toString();
            MaterialLoader.loading(true);
            resetGui();
            sendEgridToServer(egrid);
        }
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
//  CH533287066291 (SO)
    private void sendEgridToServer(String egrid) {
        extractService.extractServer(egrid, new AsyncCallback<ExtractResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                MaterialLoader.loading(false);
                GWT.log("error: " + caught.getMessage());
                MaterialToast.fireToast(caught.getMessage());
            }

            @Override
            public void onSuccess(ExtractResponse result) {
                MaterialLoader.loading(false);
                
                String newUrl = Window.Location.getProtocol() + "//" + Window.Location.getHost() 
                    + Window.Location.getPath() + "?egrid=" + egrid;
                updateURLWithoutReloading(newUrl);
                
                Extract extract = result.getExtract();
                RealEstateDPR realEstate = extract.getRealEstate();
                String number = realEstate.getNumber();
                String municipality = realEstate.getMunicipality();
                String subunitOfLandRegister = realEstate.getSubunitOfLandRegister();
                String canton = realEstate.getCanton();
                String egrid = realEstate.getEgrid();
                int area = realEstate.getLandRegistryArea();

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
                view.setCenter(new Coordinate(x-(resultCard.getWidth()*view.getResolution())/2, y));
                
                vlayer.setZIndex(1001);
                map.addLayer(vlayer);
                
                resultDiv = new Div();

                buttonRow = new MaterialRow();
                buttonRow.setMarginBottom(5);
                buttonRow.setBackgroundColor(Color.GREY_LIGHTEN_5);
                buttonRow.getElement().getStyle().setProperty("position", "sticky");
                buttonRow.getElement().getStyle().setProperty("top", "0px");
                buttonRow.getElement().getStyle().setProperty("paddingTop", "15px");
                buttonRow.getElement().getStyle().setProperty("paddingBottom", "15px");
                buttonRow.getElement().getStyle().setProperty("zIndex", "10");
//                buttonRow.getElement().getStyle().setProperty("backgroundImage", "linear-gradient(rgba(255, 255, 255, 1) 70%, rgba(255, 255, 255, 0) 100%)"); 
                
                MaterialColumn deleteExtractButtonColumn = new MaterialColumn();
                deleteExtractButtonColumn.setPadding(0);
                deleteExtractButtonColumn.setGrid("s6");

                MaterialButton deleteExtractButton = new MaterialButton();
                deleteExtractButton.setIconType(IconType.CLOSE);
                deleteExtractButton.setType(ButtonType.FLOATING);
                deleteExtractButton.setTooltip(messages.resultCloseTooltip());
                deleteExtractButton.setTooltipPosition(Position.TOP);
                deleteExtractButton.setBackgroundColor(Color.RED_LIGHTEN_1);
                
                deleteExtractButtonColumn.add(deleteExtractButton);

                deleteExtractButton.addClickHandler(event -> {
                    resetGui();
                });
                
                MaterialButton minmaxExtractButton = new MaterialButton();
                minmaxExtractButton.setMarginLeft(10);
                minmaxExtractButton.setIconType(IconType.REMOVE);
                minmaxExtractButton.setType(ButtonType.FLOATING);
                minmaxExtractButton.setTooltip(messages.resultMinimizeTooltip());
                minmaxExtractButton.setTooltipPosition(Position.TOP);
                minmaxExtractButton.setBackgroundColor(Color.RED_LIGHTEN_1);
                deleteExtractButtonColumn.add(minmaxExtractButton);
                
                minmaxExtractButton.addClickHandler(event -> {                    
                    if (resultCard.getOffsetHeight() > buttonRow.getOffsetHeight()) {
                        minmaxExtractButton.setIconType(IconType.ADD);
                        minmaxExtractButton.setTooltip(messages.resultMaximizeTooltip());
                        
                        resultCard.getElement().getStyle().setProperty("overflowY", "hidden");
                        resultCard.setHeight(String.valueOf(buttonRow.getOffsetHeight()) + "px");
                        resultDiv.setVisibility(com.google.gwt.dom.client.Style.Visibility.HIDDEN);

                    } else {
                        minmaxExtractButton.setIconType(IconType.REMOVE);
                        minmaxExtractButton.setTooltip(messages.resultMinimizeTooltip());

                        resultDiv.setVisibility(com.google.gwt.dom.client.Style.Visibility.VISIBLE);
                        resultCard.getElement().getStyle().setProperty("overflowY", "auto");
                        resultCard.getElement().getStyle().setProperty("height", "calc(100% - 245px)");
                    }
                });
                
                buttonRow.add(deleteExtractButtonColumn);

                MaterialColumn pdfButtonColumn = new MaterialColumn();
                pdfButtonColumn.setPadding(0);
                pdfButtonColumn.setGrid("s6");
                pdfButtonColumn.getElement().getStyle().setProperty("textAlign", "right");

                MaterialButton pdfButton = new MaterialButton();
                pdfButton.setIconType(IconType.INSERT_DRIVE_FILE);
                pdfButton.setType(ButtonType.FLOATING);
                pdfButton.setTooltip(messages.resultPDFTooltip());
                pdfButton.setTooltipPosition(Position.TOP);
                pdfButton.setBackgroundColor(Color.RED_LIGHTEN_1);                
                pdfButtonColumn.add(pdfButton);
                buttonRow.add(pdfButtonColumn);
                
                // TODO: 
                // Request via spring controller? (nicht gwt rpc)
                // Wie schaffe ich es eine Sanduhr zu zeigen, die dann wieder verschwindet?
                pdfButton.addClickHandler(event -> {
                    GWT.log("height: " + String.valueOf(buttonRow.getOffsetHeight()));

//                    Window.open("https://s3.eu-central-1.amazonaws.com/ch.so.agi.oereb-extract/CH857632820629_layer_ordering.pdf", "_target", "enabled");
                    Window.open(OEREB_SERVICE_URL + "/extract/reduced/pdf/geometry/" + egrid, "_blank", null);
                });

//                resultDiv.add(buttonRow);
                resultCardContent.add(buttonRow);

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
                egridInfoKeyColumn.setGrid("s4");
                egridInfoKeyColumn.getElement().getStyle().setProperty("margin", "0px");
                egridInfoKeyColumn.getElement().getStyle().setProperty("padding", "0px");
                egridInfoKeyColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                egridInfoKeyColumn.getElement().getStyle().setProperty("fontWeight", "700");
                egridInfoKeyColumn.add(new Label("EGRID:"));
                egridInfoRow.add(egridInfoKeyColumn);

                MaterialColumn egridInfoValueColumn = new MaterialColumn();
                egridInfoValueColumn.setGrid("s8");
                egridInfoValueColumn.getElement().getStyle().setProperty("margin", "0px");
                egridInfoValueColumn.getElement().getStyle().setProperty("padding", "0px");
                egridInfoValueColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                egridInfoValueColumn.getElement().getStyle().setProperty("fontWeight", "400");
                egridInfoValueColumn.add(new Label(egrid));
                egridInfoRow.add(egridInfoValueColumn);

                MaterialRow areaInfoRow = new MaterialRow();
                areaInfoRow.getElement().getStyle().setProperty("margin", "0px");

                MaterialColumn areaInfoKeyColumn = new MaterialColumn();
                areaInfoKeyColumn.setGrid("s4");
                areaInfoKeyColumn.getElement().getStyle().setProperty("margin", "0px");
                areaInfoKeyColumn.getElement().getStyle().setProperty("padding", "0px");
                areaInfoKeyColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                areaInfoKeyColumn.getElement().getStyle().setProperty("fontWeight", "700");
                areaInfoKeyColumn.add(new Label(messages.resultArea()+":"));
                areaInfoRow.add(areaInfoKeyColumn);

                MaterialColumn areaInfoValueColumn = new MaterialColumn();
                areaInfoValueColumn.setGrid("s8");
                areaInfoValueColumn.getElement().getStyle().setProperty("margin", "0px");
                areaInfoValueColumn.getElement().getStyle().setProperty("padding", "0px");
                areaInfoValueColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                areaInfoValueColumn.getElement().getStyle().setProperty("fontWeight", "400");

                areaInfoValueColumn.add(new HTML(fmtDefault.format(area) + " m<sup>2</sup>"));
                areaInfoRow.add(areaInfoValueColumn);
                
                MaterialRow subunitInfoRow = new MaterialRow();
                subunitInfoRow.getElement().getStyle().setProperty("margin", "0px");
                
                MaterialColumn subunitInfoKeyColumn = new MaterialColumn();
                subunitInfoKeyColumn.setGrid("s4");
                subunitInfoKeyColumn.getElement().getStyle().setProperty("margin", "0px");
                subunitInfoKeyColumn.getElement().getStyle().setProperty("padding", "0px");
                subunitInfoKeyColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                subunitInfoKeyColumn.getElement().getStyle().setProperty("fontWeight", "700");
                subunitInfoKeyColumn.add(new Label(messages.resultSubunitOfLandRegister()+":"));
                subunitInfoRow.add(subunitInfoKeyColumn);

                MaterialColumn subunitInfoValueColumn = new MaterialColumn();
                subunitInfoValueColumn.setGrid("s8");
                subunitInfoValueColumn.getElement().getStyle().setProperty("margin", "0px");
                subunitInfoValueColumn.getElement().getStyle().setProperty("padding", "0px");
                subunitInfoValueColumn.getElement().getStyle().setProperty("fontSize", SUB_HEADER_FONT_SIZE);
                subunitInfoValueColumn.getElement().getStyle().setProperty("fontWeight", "400");

                subunitInfoValueColumn.add(new Label(subunitOfLandRegister));
                subunitInfoRow.add(subunitInfoValueColumn);
                
                resultDiv.add(generalInfoRow);
                resultDiv.add(egridInfoRow);
                resultDiv.add(areaInfoRow);
                resultDiv.add(subunitInfoRow);
                                
                // TODO: rename everything except the global objects
                {
                    collapsibleConcernedTheme = new MaterialCollapsible();
                    collapsibleConcernedTheme.setBackgroundColor(Color.GREY_LIGHTEN_5);
                    collapsibleConcernedTheme.setMarginTop(25);
                    collapsibleConcernedTheme.setShadow(0);
//                    collapsibleConcernedTheme.setBorderRadius("10px");
                    
                    collapsibleConcernedTheme.addExpandHandler(event -> {
                        collapsibleNotConcernedTheme.closeAll();
                        collapsibleThemesWithoutData.closeAll();
                        collapsibleGeneralInformation.closeAll();
                    });
                    
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
                    if (realEstate.getConcernedThemes().size() > 0 ) {
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
//                            link.setIconPosition(IconPosition.RIGHT);
//                            link.setIconType(IconType.EXPAND_MORE);
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
                            sliderRow.setMarginBottom(0);

                            MaterialColumn sliderRowLeft = new MaterialColumn();
                            sliderRowLeft.setGrid("s3");
                            MaterialColumn sliderRowRight = new MaterialColumn();
                            sliderRowRight.setGrid("s9");
    
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
                            
                            {
                                MaterialRow informationHeaderRow = new MaterialRow();
                                informationHeaderRow.setPaddingTop(10);
                                informationHeaderRow.setPaddingBottom(0);
                                informationHeaderRow.setPaddingLeft(0);
                                informationHeaderRow.setPaddingRight(0);
                                informationHeaderRow.setBorderBottom("1px #bdbdbd solid");
                                informationHeaderRow.setMarginBottom(5);
                                informationHeaderRow.setBorderTop("1px #bdbdbd solid");
                                informationHeaderRow.setMarginTop(15);
                                
                                MaterialColumn typeColumn = new MaterialColumn();
                                typeColumn.setGrid("s6");
                                typeColumn.setPadding(0);
                                typeColumn.setMarginRight(0);
                                typeColumn.setFontSize(SMALL_FONT_SIZE);
                                typeColumn.add(new Label(messages.resultType()));
                                
                                MaterialColumn symbolColumn = new MaterialColumn();
                                symbolColumn.setPadding(0);
                                symbolColumn.setMarginRight(0);
                                symbolColumn.setGrid("s1");
                                symbolColumn.setTextAlign(TextAlign.RIGHT);
                                symbolColumn.add(new HTML("&nbsp;"));
    
                                MaterialColumn shareColumn = new MaterialColumn();
                                shareColumn.setTextAlign(TextAlign.RIGHT);                            
                                shareColumn.setPadding(0);
                                shareColumn.setGrid("s3");
                                shareColumn.add(new Label(messages.resultShare()));
        
                                MaterialColumn sharePercentColumn = new MaterialColumn();
                                sharePercentColumn.setTextAlign(TextAlign.RIGHT);
                                sharePercentColumn.setPadding(0);
                                sharePercentColumn.setGrid("s2");
                                sharePercentColumn.add(new Label(messages.resultShareInPercent()));
        
                                informationHeaderRow.add(typeColumn);
                                informationHeaderRow.add(symbolColumn);
                                informationHeaderRow.add(shareColumn);
                                informationHeaderRow.add(sharePercentColumn);
                                body.add(informationHeaderRow);
                            }
                            
                            {
                                for (Restriction restriction : theme.getRestrictions()) {
                                    if (restriction.getAreaShare() != null) {
                                        MaterialRow informationRow = processRestrictionRow(restriction,
                                                GeometryType.POLYGON);
                                        body.add(informationRow);
                                    }

                                    if (restriction.getLengthShare() != null) {
                                        MaterialRow informationRow = processRestrictionRow(restriction,
                                                GeometryType.LINE);
                                        body.add(informationRow);
                                    }

                                    if (restriction.getNrOfPoints() != null) {
                                        MaterialRow informationRow = processRestrictionRow(restriction,
                                                GeometryType.POINT);
                                        body.add(informationRow);
                                    }
                                }
                                MaterialRow fakeRow = new MaterialRow();
                                fakeRow.setBorderBottom("1px #bdbdbd solid");
                                body.add(fakeRow);
                            }
 
                            if (theme.getLegendAtWeb() != null) 
                            {
                                MaterialRow legendRow = new MaterialRow();
                                legendRow.setBorderTop("1px #bdbdbd solid");
//                                legendRow.setBorderBottom("1px #bdbdbd solid");
                                legendRow.setMarginBottom(10);
    
                                MaterialColumn legendColumn = new MaterialColumn();
                                legendColumn.setPaddingTop(5);
                                legendColumn.setPaddingBottom(0);
                                legendColumn.setPaddingLeft(0);
                                legendColumn.setMarginRight(0);
                                legendColumn.setGrid("s12");
                                legendColumn.setFontSize(BODY_FONT_SIZE);
                                
                                
                                MaterialLink legendLink = new MaterialLink();
                                legendLink.setText(messages.resultShowLegend());
                                legendLink.setTextColor(Color.RED_DARKEN_2);
                                legendLink.addStyleName("result-link");
                                legendColumn.add(legendLink);
                                
                                String legendUrl = theme.getLegendAtWeb();
//                                for (Entry<String, String> entry : WMS_HOST_MAPPING.entrySet()) {
//                                    if (theme.getLegendAtWeb().contains(entry.getKey())) {                                    
//                                        legendUrl = theme.getLegendAtWeb().replace(entry.getKey(), entry.getValue());
//                                    }                                
//                                }
                                                                
                                legendRow.add(legendColumn);
                                body.add(legendRow);
                                
                                com.google.gwt.user.client.ui.Image legendImage = new com.google.gwt.user.client.ui.Image();
                                legendImage.setUrl(legendUrl);
                                legendImage.setVisible(false);
                                body.add(legendImage);
                                
                                MaterialRow fakeRow = new MaterialRow();
                                fakeRow.setBorderBottom("1px #bdbdbd solid");
                                body.add(fakeRow);
                                
                                legendLink.addClickHandler(event -> {                                    
                                    if (legendImage.isVisible()) {
                                        legendImage.setVisible(false);                                                                                
                                        legendLink.setText(messages.resultShowLegend());   
                                    } else {
                                        legendImage.setVisible(true);                                        
                                        legendLink.setText(messages.resultHideLegend());   
                                    }
                                });
                            }
                            
                            {
                                MaterialRow legalProvisionsHeaderRow = new MaterialRow();
                                legalProvisionsHeaderRow.setMarginBottom(5);
                                legalProvisionsHeaderRow.setFontSize(BODY_FONT_SIZE);
                                legalProvisionsHeaderRow.setFontWeight(FontWeight.BOLD);
                                legalProvisionsHeaderRow.add(new Label(messages.legalProvisions()));
                                body.add(legalProvisionsHeaderRow);
                                
                                for (ch.so.agi.oereb.webclient.shared.models.Document legalProvision : theme.getLegalProvisions()) {
                                    MaterialRow row = new MaterialRow();
                                    row.setMarginBottom(0);
                                    row.setFontSize(BODY_FONT_SIZE);
    
                                    MaterialLink legalProvisionLink = new MaterialLink();
                                    
                                    if (legalProvision.getOfficialTitle() != null) {
                                        legalProvisionLink.setText(legalProvision.getOfficialTitle());
                                    } else {
                                        legalProvisionLink.setText(legalProvision.getTitle());
                                    }
                                    legalProvisionLink.setHref(legalProvision.getTextAtWeb());
                                    legalProvisionLink.setTarget("_blank");
                                    legalProvisionLink.setTextColor(Color.RED_DARKEN_2);
                                    legalProvisionLink.addStyleName("result-link");
                                    row.add(legalProvisionLink);
                                    body.add(row);
                                    
                                    MaterialRow additionalInfoRow = new MaterialRow();
                                    additionalInfoRow.setMarginBottom(10);
                                    additionalInfoRow.setFontSize(SMALL_FONT_SIZE); 
                                    
                                    String labelText = legalProvision.getTitle();
                                    if (legalProvision.getOfficialNumber() != null) {
                                        labelText += " Nr. " + legalProvision.getOfficialNumber();
                                    }
                                    Label label = new Label(labelText);
                                    additionalInfoRow.add(label);
                                    body.add(additionalInfoRow);
                                }
                                
                                MaterialRow lawsHeaderRow = new MaterialRow();
                                lawsHeaderRow.setMarginTop(15);
                                lawsHeaderRow.setMarginBottom(5);
                                lawsHeaderRow.setFontSize(BODY_FONT_SIZE);
                                lawsHeaderRow.setFontWeight(FontWeight.BOLD);
                                lawsHeaderRow.add(new Label(messages.laws()));
                                body.add(lawsHeaderRow);
    
                                for (ch.so.agi.oereb.webclient.shared.models.Document law : theme.getLaws()) {
                                    MaterialRow row = new MaterialRow();
                                    row.setMarginBottom(10);
                                    row.setFontSize(BODY_FONT_SIZE);
    
                                    MaterialLink lawLink = new MaterialLink();
                                   
                                    String linkText = "";
                                    if (law.getOfficialTitle() != null) {
                                        linkText = law.getOfficialTitle();
                                    } else {
                                        linkText = law.getTitle();
                                    }
                                    if (law.getAbbreviation() != null) {
                                        linkText += " (" + law.getAbbreviation() + ")";
                                    }
                                    if (law.getOfficialNumber() != null) {
                                        linkText += ", " + law.getOfficialNumber();
                                    }
                                    lawLink.setText(linkText);
                                    lawLink.setHref(law.getTextAtWeb());
                                    lawLink.setTarget("_blank");
                                    lawLink.setTextColor(Color.RED_DARKEN_2);
                                    lawLink.addStyleName("result-link");
                                    row.add(lawLink);
                                    body.add(row);
                                }
                                MaterialRow fakeRow = new MaterialRow();
                                fakeRow.setBorderBottom("1px #bdbdbd solid");
                                body.add(fakeRow);
                            }
                            
                            {
                                MaterialRow responsibleOfficeHeaderRow = new MaterialRow();
                                responsibleOfficeHeaderRow.setMarginBottom(5);
                                responsibleOfficeHeaderRow.setFontSize(BODY_FONT_SIZE);
                                responsibleOfficeHeaderRow.setFontWeight(FontWeight.BOLD);
                                responsibleOfficeHeaderRow.add(new Label(messages.responsibleOffice()));
                                body.add(responsibleOfficeHeaderRow);   
                                
                                for (Office office : theme.getResponsibleOffice()) {
                                    MaterialRow row = new MaterialRow();
                                    row.setMarginBottom(0);
                                    row.setFontSize(BODY_FONT_SIZE);
    
                                    MaterialLink officeLink = new MaterialLink();
                                    officeLink.setText(office.getName());
                                    officeLink.setHref(office.getOfficeAtWeb());
                                    officeLink.setTarget("_blank");
                                    officeLink.setTextColor(Color.RED_DARKEN_2);
                                    officeLink.addStyleName("result-link");
                                    row.add(officeLink);
                                    body.add(row);

                                }
                            }

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
//                            MaterialCollapsibleItem item = event.getTarget();
//                            MaterialCollapsibleHeader header = item.getHeader();
//                            List<Widget> children = header.getChildrenList();
//                            for (Widget child : children) {
//                                if (child instanceof gwt.material.design.client.ui.MaterialLink) {
//                                    MaterialLink link = (MaterialLink) child;
//                                    link.setIconType(IconType.EXPAND_LESS);
//                                }
//                            }
                         });
                         
                         collapsible.addCollapseHandler(event -> {
                            Image wmsLayer = (Image) getLayerById(event.getTarget().getId());
                            wmsLayer.setVisible(false);
//                            MaterialCollapsibleItem item = event.getTarget();
//                            MaterialCollapsibleHeader header = item.getHeader();
//                            List<Widget> children = header.getChildrenList();
//                            for (Widget child : children) {
//                                if (child instanceof gwt.material.design.client.ui.MaterialLink) {
//                                    MaterialLink link = (MaterialLink) child;
//                                    link.setIconType(IconType.EXPAND_MORE);
//                                }
//                            }
                         });
                        
                        collapsible.open(1);
                        
                        collapsibleConcernedThemeBody.add(collapsible);
                    }
                    
                    collapsibleConcernedThemeItem.add(collapsibleConcernedThemeHeader);
                    if (realEstate.getConcernedThemes().size() > 0) {
                        collapsibleConcernedThemeItem.add(collapsibleConcernedThemeBody);
                    }
                    
                    collapsibleConcernedTheme.add(collapsibleConcernedThemeItem);

                    if (realEstate.getConcernedThemes().size() > 0) {
                        collapsibleConcernedTheme.open(1);;
                    }
                    
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
                resultCard.getElement().getStyle().setProperty("height", "calc(100% - 245px)");
                resultCard.getElement().getStyle().setProperty("overflowY", "auto");
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
        
        if (buttonRow != null) {
            buttonRow.removeFromParent();
        }

        resultCard.getElement().getStyle().setProperty("visibility", "hidden");
    }

    public class DummyButtonClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            MaterialLoader.loading(true);
            resetGui();
            //CH870679603216 (KbS ÖV)
            //CH857632820629 (Kappel)
            //CH807306583219 (Messen)
            sendEgridToServer("CH807306583219");
        }
    }

    public class SearchValueChangeHandler implements ValueChangeHandler {
        @Override
        public void onValueChange(ValueChangeEvent event) {
            MaterialLoader.loading(true);
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

                                sendEgridToServer(egrid);
                                return;
                            } else {
                                MaterialLoader.loading(false);
                                GWT.log("error from request");
                                GWT.log(String.valueOf(statusCode));
                                GWT.log(response.getStatusText());
                            }
                        }

                        @Override
                        public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                            MaterialLoader.loading(false);
                            GWT.log("error actually sending the request, never got sent");
                        }
                    });
                } catch (Exception e) {
                    MaterialLoader.loading(true);
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
                                                GWT.log("get extract for (from address): " + egrid);
                                                sendEgridToServer(egrid);
                                                return;
                                            } else {
                                                MaterialLoader.loading(false);
                                                GWT.log("error from request");
                                                GWT.log(String.valueOf(statusCode));
                                                GWT.log(response.getStatusText());
                                            }
                                        }

                                        @Override
                                        public void onError(com.google.gwt.http.client.Request request,
                                                Throwable exception) {
                                            MaterialLoader.loading(false);
                                            GWT.log("error actually sending the request, never got sent");
                                        }
                                    });
                                } catch (Exception e) {
                                    MaterialLoader.loading(false);
                                    e.printStackTrace();
                                }
                            } else {
                                MaterialLoader.loading(false);
                                GWT.log("error from request");
                                GWT.log(String.valueOf(statusCode));
                                GWT.log(response.getStatusText());
                            }
                        }

                        @Override
                        public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                            MaterialLoader.loading(false);
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
            resetGui();
            
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

                            String egrid;
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
                                    egrid = feature.get("egrid").toString().trim().replaceAll("^.|.$", "");
                                    String type = feature.get("art_txt").toString().trim().replaceAll("^.|.$", "");

                                    MaterialRow realEstateRow = new MaterialRow();
                                    realEstateRow.setId(egrid);
                                    realEstateRow.setMarginBottom(0);
                                    realEstateRow.setPadding(5);
                                    realEstateRow.add(new Label("GB-Nr.: " + number + " (" + type.substring(type.lastIndexOf(".") + 1) + ")"));
                                   
                                    realEstateRow.addClickHandler(event -> {                                        
                                        realEstateWindow.removeFromParent();
                                        GWT.log("get extract from click for (multiple result): " + realEstateRow.getId());                                

                                        MaterialLoader.loading(true);
                                        sendEgridToServer(realEstateRow.getId());
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
                                egrid = features.get(0).get("egrid").toString().trim().replaceAll("^.|.$", "");
                                GWT.log("get extract from click for (single result): " + egrid);  
                                
                                MaterialLoader.loading(true);
                                sendEgridToServer(egrid);
                            }
                            return;
                        } else {
                            MaterialLoader.loading(false);
                            GWT.log("error from request");
                            GWT.log(String.valueOf(statusCode));
                            GWT.log(response.getStatusText());
                        }
                    }

                    @Override
                    public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                        MaterialLoader.loading(false);
                        GWT.log("error actually sending the request, never got sent");                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private Image createPlrWmsLayer(ReferenceWMS referenceWms) {
        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(referenceWms.getLayers());

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
        
        String baseUrl = referenceWms.getBaseUrl();
//        if (WMS_HOST_MAPPING.get(baseUrl) != null) {
//            baseUrl = WMS_HOST_MAPPING.get(referenceWms.getBaseUrl());
//        }
        
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
        wmsLayer.setZIndex(referenceWms.getLayerIndex());

        return wmsLayer;
    }
    
    private MaterialRow processRestrictionRow(Restriction restriction, GeometryType type) {
        MaterialRow informationRow = new MaterialRow();
        informationRow.setMarginBottom(10);

        MaterialColumn typeColumn = new MaterialColumn();
        typeColumn.setGrid("s6");
        typeColumn.setPadding(0);
        typeColumn.setMarginRight(0);
        typeColumn.setFontSize(BODY_FONT_SIZE);
        typeColumn.add(new Label(restriction.getInformation()));

        MaterialColumn symbolColumn = new MaterialColumn();
        symbolColumn.setPadding(0);
        symbolColumn.setMarginRight(0);
        symbolColumn.setGrid("s1");
        symbolColumn.setTextAlign(TextAlign.CENTER);
        symbolColumn.setVerticalAlign(VerticalAlign.MIDDLE);

        Span helper = new Span();
        helper.setDisplay(Display.INLINE_BLOCK);
        helper.setVerticalAlign(VerticalAlign.MIDDLE);
        symbolColumn.add(helper);

        com.google.gwt.user.client.ui.Image symbolImage;
        if (restriction.getSymbol() != null) {
            symbolImage = new com.google.gwt.user.client.ui.Image(restriction.getSymbol());
        } else {
            // FIXME
            // Temporary
            if (restriction.getSymbolRef().contains("http://geo-t.so.ch/symbol/")) {
                restriction.setSymbolRef(restriction.getSymbolRef().replace("http://geo-t.so.ch/symbol/", "https://geo-t.so.ch/api/oereb/v1/symbol/"));
            }
            symbolImage = new com.google.gwt.user.client.ui.Image(UriUtils.fromSafeConstant(restriction.getSymbolRef()));
        }
        symbolImage.setWidth("30px");
        symbolImage.getElement().getStyle().setProperty("border", "1px solid black");
        symbolImage.getElement().getStyle().setProperty("verticalAlign", "middle");
        symbolColumn.add(symbolImage);

        MaterialColumn shareColumn = new MaterialColumn();
        shareColumn.setTextAlign(TextAlign.RIGHT);
        shareColumn.setPadding(0);
        shareColumn.setGrid("s3");
        shareColumn.setFontSize(BODY_FONT_SIZE);
        
        if (type == GeometryType.POLYGON) {
            HTML htmlArea;
            if (restriction.getAreaShare() < 0.1) {
                htmlArea = new HTML("< 0.1 m<sup>2</sup>");
            } else {
                htmlArea = new HTML(fmtDefault.format(restriction.getAreaShare()) + " m<sup>2</sup>");
            }
            shareColumn.add(htmlArea);
        }
        if (type == GeometryType.LINE) {
            HTML htmlLength;
            if (restriction.getLengthShare() < 0.1) {
                htmlLength = new HTML("< 0.1 m");
            } else {
                htmlLength = new HTML(fmtDefault.format(restriction.getLengthShare()) + " m");
            }
            shareColumn.add(htmlLength);
        }
        if (type == GeometryType.POINT) {
            HTML htmlPoints = new HTML(fmtDefault.format(restriction.getNrOfPoints()));
            shareColumn.add(htmlPoints);
        }
        
        MaterialColumn sharePercentColumn = new MaterialColumn();
        sharePercentColumn.setTextAlign(TextAlign.RIGHT);
        sharePercentColumn.setPadding(0);
        sharePercentColumn.setGrid("s2");
        sharePercentColumn.setFontSize(BODY_FONT_SIZE);
                                        
        if (type == GeometryType.POLYGON && restriction.getPartInPercent() != null) {
            HTML htmlArea;
            if (restriction.getPartInPercent() < 0.1) {
                htmlArea = new HTML("< 0.1");
            } else {
                htmlArea = new HTML(fmtPercent.format(restriction.getPartInPercent()));
            }
            sharePercentColumn.add(htmlArea);
        } else {
            sharePercentColumn.add(new HTML("&nbsp;"));
        }

        informationRow.add(typeColumn);
        informationRow.add(symbolColumn);
        informationRow.add(shareColumn);
        informationRow.add(sharePercentColumn);

        return informationRow;
    }
    
    private static native void updateURLWithoutReloading(String newUrl) /*-{
        $wnd.history.pushState(newUrl, "", newUrl);
    }-*/;
}