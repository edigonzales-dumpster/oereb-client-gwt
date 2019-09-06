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
import gwt.material.design.client.constants.Color;
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
import ol.Map;
import ol.MapOptions;
import ol.OLFactory;
import ol.View;
import ol.ViewOptions;
import ol.control.Control;
import ol.control.Rotate;
import ol.control.ScaleLine;
import ol.control.Zoom;
import ol.interaction.KeyboardPan;
import ol.interaction.KeyboardZoom;
import ol.layer.Image;
import ol.layer.LayerOptions;
import ol.layer.Tile;
import ol.proj.Projection;
import ol.proj.ProjectionOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;
import ol.source.Osm;
import ol.source.Wmts;
import ol.source.WmtsOptions;
import ol.source.XyzOptions;
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

        // material card for the controls
        MaterialCard card = new MaterialCard();
        card.setTitle("gaga");
        card.setBackgroundColor(Color.BROWN_LIGHTEN_2);
        card.setHeight("200px");
        card.getElement().getStyle().setProperty("transition", "height 1s");

        MaterialCardTitle cardTitle = new MaterialCardTitle();
        cardTitle.setText("Fubar");
        card.add(cardTitle);
//        columnLeft.add(card);

        UserOracle userOracle = new UserOracle();

        MaterialAutoComplete autocomplete = new MaterialAutoComplete(userOracle);

        autocomplete.setType(AutocompleteType.TEXT);
        autocomplete.setPlaceholder("Suche");
        autocomplete.setAutoSuggestLimit(5);
        // FIXME: remove text on select

        autocomplete.addValueChangeHandler(event -> {
            GWT.log(autocomplete.getItemBox().getText());
            MaterialToast.fireToast(autocomplete.getItemBox().getText());
        });

        MaterialCardContent cardContent = new MaterialCardContent();
        cardContent.add(autocomplete);
        card.add(cardContent);

        MaterialRow buttonRow = new MaterialRow();

        MaterialButton button1 = new MaterialButton();
        button1.setText("PDF");
        // button1.setMargin(5.0);
        button1.setMarginRight(5.0);
        buttonRow.add(button1);

        MaterialButton button2 = new MaterialButton();
        button2.setText("FUBAR");
        button2.setBackgroundColor(Color.WHITE);
        button2.setTextColor(Color.RED_DARKEN_4);
        buttonRow.add(button2);

////        Div div1 = new Div();
////        div1.add(button1);
////        
////        Div div2 = new Div();
////        div2.add(button2);
//
        // columnLeft.add(autocomplete);
//        columnLeft.add(buttonRow);
////        columnLeft.add(div1);
////        columnLeft.add(div2);
//
//        

//        card.add(autocomplete);

        button2.addClickHandler(event -> {
            GWT.log("push the button");
        });

//        RootPanel.get().add(row);
        
        mapDiv.getElement().getStyle().setProperty("height", "100%");

        MaterialCard card1 = new MaterialCard();
        card1.setTitle("gaga");
        card1.setBackgroundColor(Color.BROWN_LIGHTEN_2);
//        card1.setHeight("200px");
        card1.getElement().getStyle().setProperty("transition", "height 1s");
        card1.getElement().getStyle().setProperty("position", "absolute");
        card1.getElement().getStyle().setProperty("top", "10px");
        card1.getElement().getStyle().setProperty("left", "10px");
        card1.getElement().getStyle().setProperty("width", "500px");
        card1.getElement().getStyle().setProperty("height", "300px");
        
        MaterialCardTitle cardTitle1 = new MaterialCardTitle();
        cardTitle1.setText("Fubar");
        card1.add(cardTitle1);

        MaterialCardContent cardContent1 = new MaterialCardContent();
        MaterialLabel label = new MaterialLabel();
        label.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \n" + 
                "\n" + 
                "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.   \n" + 
                "\n" + 
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.   \n" + 
                "\n" + 
                "Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer");
        cardContent1.add(label);
        card1.add(cardContent1);
        
        card1.getElement().getStyle().setProperty("overflowY", "scroll");

        
//        Div fadeoutDiv = new Div();
//        fadeoutDiv.getElement().getStyle().setProperty("position", "sticky");
//        fadeoutDiv.getElement().getStyle().setProperty("bottom", "0");
//        fadeoutDiv.getElement().getStyle().setProperty("width", "100%");
//        fadeoutDiv.getElement().getStyle().setProperty("padding", "30px 0");
//        fadeoutDiv.getElement().getStyle().setProperty("backgroundImage", "linear-gradient(rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 1) 100%)");
//        card1.add(fadeoutDiv);
        
        
        
        RootPanel.get().add(mapDiv);
        RootPanel.get().add(card1);

        

        class ExtractHandler implements ClickHandler, KeyUpHandler {
            public void onClick(ClickEvent event) {
                sendEgridToServer();
                MaterialLoader.loading(true);
            }

            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendEgridToServer();
                }
            }

//             CH158782774974
//             CH944982786913
//             CH938278494529

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

                        card.setHeight("400px");

                    }
                });
            }
        }

        ExtractHandler handler = new ExtractHandler();
        button2.addClickHandler(handler);

        // Openlayers

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
        mapOptions.setTarget(mapDiv.getId());
        mapOptions.setView(view);
        mapOptions.setControls(new Collection<Control>());
        
        Map map = new Map(mapOptions);

        // add layers
        map.addLayer(wmtsLayer);



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

        /*
         * RPC stuff
         */
//        final Button sendButton = new Button("Send");
//        final TextBox nameField = new TextBox();
//        nameField.setText("GWT User Foo");
//        final Label errorLabel = new Label();
//        
//        GWT.log("Hallo Stefan.");
//
//        // We can add style names to widgets
//        sendButton.addStyleName("sendButton");
//
//        // Add the nameField and sendButton to the RootPanel
//        // Use RootPanel.get() to get the entire body element
//        RootPanel.get("nameFieldContainer").add(nameField);
//        RootPanel.get("sendButtonContainer").add(sendButton);
//        RootPanel.get("errorLabelContainer").add(errorLabel);
//
//        // Focus the cursor on the name field when the app loads
//        nameField.setFocus(true);
//        nameField.selectAll();
//
//        // Create the popup dialog box
//        final DialogBox dialogBox = new DialogBox();
//        dialogBox.setText("Remote Procedure Call");
//        dialogBox.setAnimationEnabled(true);
//        final Button closeButton = new Button("Close");
//        // We can set the id of a widget by accessing its Element
//        closeButton.getElement().setId("closeButton");
//        final Label textToServerLabel = new Label();
//        final HTML serverResponseLabel = new HTML();
//        VerticalPanel dialogVPanel = new VerticalPanel();
//        dialogVPanel.addStyleName("dialogVPanel");
//        dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
//        dialogVPanel.add(textToServerLabel);
//        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
//        dialogVPanel.add(serverResponseLabel);
//        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
//        dialogVPanel.add(closeButton);
//        dialogBox.setWidget(dialogVPanel);
//
//        // Add a handler to close the DialogBox
//        closeButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                dialogBox.hide();
//                sendButton.setEnabled(true);
//                sendButton.setFocus(true);
//            }
//        });
//
//        // Create a handler for the sendButton and nameField
//        class MyHandler implements ClickHandler, KeyUpHandler {
//            /**
//             * Fired when the user clicks on the sendButton.
//             */
//            public void onClick(ClickEvent event) {
//                sendNameToServer();
//            }
//
//            /**
//             * Fired when the user types in the nameField.
//             */
//            public void onKeyUp(KeyUpEvent event) {
//                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//                    sendNameToServer();
//                }
//            }
//
//            /**
//             * Send the name from the nameField to the server and wait for a response.
//             */
//            private void sendNameToServer() {
//                // First, we validate the input.
//                errorLabel.setText("");
//                String textToServer = nameField.getText();
//                if (!FieldVerifier.isValidName(textToServer)) {
//                    errorLabel.setText("Please enter at least four characters");
//                    return;
//                }
//
//                // Then, we send the input to the server.
//                sendButton.setEnabled(false);
//                textToServerLabel.setText(textToServer);
//                serverResponseLabel.setText("");
//                                
//                greetingService.greetServer(textToServer,
//                        new AsyncCallback<GreetingResponse>() {
//                            public void onFailure(Throwable caught) {
//                                // Show the RPC error message to the user
//                                dialogBox
//                                        .setText("Remote Procedure Call - Failure");
//                                serverResponseLabel
//                                        .addStyleName("serverResponseLabelError");
//                                serverResponseLabel.setHTML(SERVER_ERROR);
//                                dialogBox.center();
//                                closeButton.setFocus(true);
//                            }
//
//                            public void onSuccess(GreetingResponse result) {
//                                dialogBox.setText("Remote Procedure Call");
//                                serverResponseLabel
//                                        .removeStyleName("serverResponseLabelError");
//                                serverResponseLabel.setHTML(new SafeHtmlBuilder()
//                                        .appendEscaped(result.getGreeting())
//                                        .appendHtmlConstant("<br><br>I am running ")
//                                        .appendEscaped(result.getServerInfo())
//                                        .appendHtmlConstant(".<br><br>It looks like you are using:<br>")
//                                        .appendEscaped(result.getUserAgent())
//                                        .toSafeHtml());
//                                dialogBox.center();
//                                closeButton.setFocus(true);
//                            }
//                        });
//            }
//        }
//
//        // Add a handler to send the name to the server
//        MyHandler handler = new MyHandler();
//        sendButton.addClickHandler(handler);
//        nameField.addKeyUpHandler(handler);

        // RootPanel.get().add(new Label("zakaria. Hallo Welt."));

//        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
//        oracle.add("A");
//        oracle.add("AB");
//        oracle.add("ABC");
//        oracle.add("ABCD");
//        oracle.add("B");
//        oracle.add("BC");
//        oracle.add("BCD");
//        oracle.add("BCDE");
//        oracle.add("C");
//        oracle.add("CD");
//        oracle.add("CDE");
//        oracle.add("CDEF");
//        oracle.add("D");
//        oracle.add("DE");
//        oracle.add("DEF");
//        oracle.add("DEFGH");

        // create the suggestion box and pass it the data created above
//        SuggestBox suggestionBox = new SuggestBox(oracle);
//        MySuggestBox suggestionBox = new MySuggestBox();

        // set width to 200px.
        // suggestionBox.setWidth("200");

        // Add suggestionbox to the root panel.
//        VerticalPanel panel = new VerticalPanel();
//        panel.add(suggestionBox);
//
//        RootPanel.get().add(panel);

//	    List<User> users = new ArrayList<User>();
//	    users.add(new User("picture", Position.CEO, true, "Ziegler Stefan", "email", "password", "contactNo", "address", "AGI"));
//	    users.add(new User("picture", Position.CEO, true, "Foo Bar", "email", "password", "contactNo", "address", "AGI"));
//	    
//	    UserOracle userOracle = new UserOracle();
//	    userOracle.addContacts(users);
//		
//        MaterialAutoComplete autoComplete = new MaterialAutoComplete(userOracle); 
//        //autoComplete.setLabel("Suche");
//        autoComplete.setPlaceholder("Suche");
//
//        VerticalPanel panel = new VerticalPanel();
//        panel.add(autoComplete);
//
//        RootPanel.get().add(panel);

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
}