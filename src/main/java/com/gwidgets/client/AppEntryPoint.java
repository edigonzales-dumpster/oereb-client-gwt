package com.gwidgets.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
//import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwidgets.client.User.Position;
import com.gwidgets.shared.FieldVerifier;
import com.gwidgets.shared.GreetingResponse;
import com.gwidgets.shared.GreetingService;
import com.gwidgets.shared.GreetingServiceAsync;

import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
import gwt.material.design.addins.client.autocomplete.constants.AutocompleteType;
import gwt.material.design.addins.client.combobox.MaterialComboBox;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.events.HandlerRegistry;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialDropDown;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialNavBrand;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialSearch;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.html.Div;
import gwt.material.design.client.ui.html.Option;
import gwt.material.design.jquery.client.api.JQueryElement;


import static gwt.material.design.addins.client.combobox.js.JsComboBox.$;

//import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;

public class AppEntryPoint implements EntryPoint {

    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    
    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

    private MaterialSearch search;
    private TextBox searchTextBox;
//    private List<SearchObject> objects;

    private String baseUrl = "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?limit=15&query=egr+";
    
	public void onModuleLoad() {
	    MaterialRow row = new MaterialRow();

	    MaterialColumn columnLeft = new MaterialColumn();
	    columnLeft.setGrid("s4");
	    columnLeft.setBackgroundColor(Color.WHITE);
	    columnLeft.add(new Label("Controls belong here."));

	    MaterialColumn columnRight = new MaterialColumn();
	    columnRight.setGrid("s8");
	    columnRight.setBackgroundColor(Color.LIGHT_GREEN);
	    columnRight.add(new Label("Map belongs here."));
   
	    row.add(columnLeft);
        row.add(columnRight);
//       
//        UserOracle userOracle = new UserOracle();
//
//        MaterialAutoComplete autocomplete = new MaterialAutoComplete(userOracle);
//        autocomplete.setType(AutocompleteType.TEXT);
//        autocomplete.setPlaceholder("Suche");
////        autocomplete.setLimit(5);
//        autocomplete.setAutoSuggestLimit(5);
//        
//        // FIXME: remove text on select            
//        
//        MaterialButton button = new MaterialButton();
//        button.setText("PDF");
//        
//        Div div = new Div();
//        div.add(autocomplete);
//        div.add(button);
//
//        columnLeft.add(div);
//        
//

        
//	    MaterialNavBar navBar = new MaterialNavBar();
//        
//	    MaterialNavBrand navBarBrand = new MaterialNavBrand();
//	    navBarBrand.setText("GWT Material");
//	    navBar.add(navBarBrand);
//	    
//	    MaterialLink link = new MaterialLink();
//	    link.setIconType(IconType.SEARCH);
//	    link.setFloat(Style.Float.RIGHT);
//	    navBar.add(link);
//	    
//	    MaterialNavBar navBarSearch = new MaterialNavBar();
//	    navBarSearch.setVisible(false);
//	    
//	    search = new MaterialSearch();
//	    search.setPlaceholder("Suche");
//	    search.setIconColor(Color.BLACK);
//	    search.setBackgroundColor(Color.WHITE);
//	    search.setActive(true);
//	    search.setShadow(1);
//	    navBarSearch.add(search);
//	    
//        RootPanel.get().add(navBar);
//        RootPanel.get().add(navBarSearch);
//        
//        link.addClickHandler(event -> {
//            search.open();
//        });
//        
//        search.addOpenHandler(event -> {
//            navBar.setVisible(false);
//            navBarSearch.setVisible(true);
//        });
//        
//        search.addCloseHandler(new CloseHandler<String>() {
//            @Override
//            public void onClose(CloseEvent<String> event) {
//                navBar.setVisible(true);
//                navBarSearch.setVisible(false);
//            }
//        });
//                
//        searchTextBox = new TextBox();
//        List<Widget> list = search.getChildrenList();
//        for (Widget widget : list) {
//            if (widget instanceof com.google.gwt.user.client.ui.TextBox) {
//                searchTextBox = (TextBox) widget;
//            }
//        }
//        
////        List<SearchObject> objects = new ArrayList<>();
////        String searchText = textBox.getText().toLowerCase();
//        
////        search.setListSearches(new ArrayList<SearchObject>());
//        
//        // You can use a addKeyPressHandler but then 
//        // pasting does not work.
//        // If you use a lambda expression instead
//        // of the for loop it throws a compilation
//        // error with the custom InputHandler.
//        searchTextBox.addDomHandler(new InputHandler() {
//            @Override
//            public void onInput(InputEvent event) {
//                GWT.log("************************");
//                GWT.log("InputHandler");
////                GWT.log(searchTextBox.getText().toLowerCase());
//                String searchText = searchTextBox.getText().toLowerCase();
//
//                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, baseUrl + searchText);
//                builder.setHeader("content-type", "application/json");
//
//
//                try {
//                    GWT.log(searchTextBox.getText().toLowerCase());                    
//                    Request response = builder.sendRequest("", new RequestCallback() {
//                        List<SearchObject> objects = new ArrayList<SearchObject>();
//
//                        @Override
//                        public void onResponseReceived(Request request, Response response) {
////                            List<SearchObject> objects = new ArrayList<>();
//                            GWT.log("clear1");
////                            search.getSearchResultPanel().clear();
//                            GWT.log("clear2");
//
//                            int statusCode = response.getStatusCode();
//
//                            if (statusCode == Response.SC_OK) {
//                                String responseBody = response.getText();
//
//                                JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
//                                JSONObject rootObj = responseObj.isObject();
//                                JSONArray featuresArray = rootObj.get("features").isArray();
//
////                                List<SearchObject> objects = new ArrayList<>();
//                                search.getListSearches().clear();
//                                for (int i = 0; i < featuresArray.size(); i++) {
//                                    JSONObject properties = featuresArray.get(i).isObject().get("properties").isObject();
//
//                                    // generisches Suchresultat. Enum (egrid, etc..
////                                    Egrid egrid = new Egrid();
////                                    egrid.setEgrid(properties.get("label").toString().replace("(EGRID)", "")
////                                            .replaceAll("^.|.$", ""));
////                                    egrid.setLabel(properties.get("label").toString().replaceAll("^.|.$", ""));
////                                    list.add(new EgridSuggestion(egrid));
//                                    
//                                    String egrid = properties.get("label").toString().replace("(EGRID)", "").replaceAll("^.|.$", "");
//                                    String egridLabel = properties.get("label").toString().replaceAll("^.|.$", "");
//                                    EgridSearchObject searchObject = new EgridSearchObject(GWT.getHostPageBaseURL()+"/ch.so.agi.av.grundstuecke.rechtskraeftig.svg", egrid, egridLabel);
////                                    objects.add(searchObject);
//                                    search.getListSearches().add(searchObject);
//                                }
////                                search.getListSearches().clear();
////                                List<SearchObject> objects = new ArrayList<>();
////                                for (Hero hero : DataHelper.getAllHeroes()) {
//////                                    GWT.log("add heroes");
////                                    objects.add(hero);
////                                    //search.getListSearches().add(hero);
////                                }
//                                
//                                GWT.log(String.valueOf(search.getSearchResultPanel().isVisible()));
////                                search.getListSearches().clear();
////                                GWT.log("vorher" + String.valueOf(search.getListSearches().size()));
////                                search.setListSearches(objects);
//                                GWT.log("nachher" + String.valueOf(search.getListSearches().size()));
//                                GWT.log("nachher1: " + String.valueOf(search.getSearchResultPanel().getWidgetCount()));
//                                
//                                for (int i=0; i < search.getSearchResultPanel().getWidgetCount(); i++) {
//                                    Widget w = search.getSearchResultPanel().getWidget(i);
////                                    GWT.log(w.getClass().toString());
//                                }
//                                
//                                search.addKeyUpHandler(event -> {
//                                   GWT.log(new Date().toString()); 
//                                });
//                                
//                                
////                                for (SearchObject obj : objects) {
////                                    GWT.log(obj.getKeyword());
////                                }
//
//                                search.getSearchResultPanel().setVisibility(Visibility.VISIBLE);
//
////                                GWT.log("size: " + String.valueOf(objects.size()));
//                                
////                              List<SearchObject> objects = new ArrayList<>();
////                              for(Hero hero : DataHelper.getAllHeroes()) {
////                                  GWT.log("add heroes");
////                                  objects.add(hero);
////                              }
//
////                                search.setListSearches(objects);
////                                search.getSearchResultPanel()..asWidget().setVisible(true);
//
////                                Response resp = new Response();
////                                if (list.isEmpty()) {
////                                    resp.setSuggestions(null);
////                                    callback.onSuggestionsReady(suggestRequest, resp);
////                                    return;
////                                }
////
////                                resp.setSuggestions(list);
////                                callback.onSuggestionsReady(suggestRequest, resp);
//                            } else {
//                                GWT.log("error");
//                                GWT.log(String.valueOf(statusCode));
//                                GWT.log(response.getStatusText());
//                            }
//                        }
//
//                        @Override
//                        public void onError(Request request, Throwable exception) {
//                            GWT.log("error actually sending the request, never got sent");
//                        }
//                    });
//                } catch (RequestException e) {
//                    GWT.log("request exception");
//                    GWT.log(e.getMessage());
//                    e.printStackTrace();
//                }
//                
//
////                List<SearchObject> objects = new ArrayList<>();
////                for(Hero hero : DataHelper.getAllHeroes()){
////                    objects.add(hero);
////                }
//                
//                
////                search.setListSearches(objects);
//                GWT.log("------------------------");
//
//            }
//        }, InputEvent.getType());

        
        
        
//        search.getChildren().forEach(widget -> {
//            GWT.log(widget.getClass().toString());
//            
//            if (widget instanceof com.google.gwt.user.client.ui.TextBox) {
//                TextBox textBox = (TextBox) widget;
////                textBox.addChangeHandler(event -> {
////                    GWT.log("change");
////                });
//                
//                GWT.log(textBox.getClass().toString());
//                
//                textBox.addKeyPressHandler(event -> {
//                    GWT.log("addKeyPressHandler");
//                });
//                
////                textBox.addDomHandler(new InputHandler() {
////
////                    @Override
////                    public void onInput(InputEvent event) {
////                        GWT.log("InputHandler");
////                    }
////                }, InputEvent.getType());
//                
//            }
//        });

        
//        TextBox textBox = new TextBox();
//        textBox.addDomHandler(new InputHandler() {
//
//            @Override
//            public void onInput(InputEvent event) {
//                GWT.log("InputHandler");
//            }
//        }, InputEvent.getType());
//
//        
//        GWT.log(textBox.getClass().toString());
//
//        RootPanel.get().add(textBox);

        
//        MaterialSearch search = new MaterialSearch();
//        columnLeft.add(search);
        


        
        MaterialComboBox combobox = new MaterialComboBox();
        combobox.setPlaceholder("Suche");
        combobox.setAllowClear(true);
        combobox.addItem("", "");
//        combobox.addItem("foo", "foo");
//        combobox.addItem("bar", "bar");
//        combobox.setId("oereb-search");
        combobox.setMultiple(false);
        
        combobox.getListbox().addKeyPressHandler(event -> {
           GWT.log("foo"); 
        });
        
        GWT.log(String.valueOf(combobox.getChildrenList().size()));
        
        combobox.forEach(widget -> {
          GWT.log("foo");
          GWT.log(widget.getParent().getClass().toString());
      });
        
        
        combobox.addClickHandler(event -> {
           GWT.log("open");
           
           GWT.log(String.valueOf(RootPanel.get().getWidget(0).toString()));
           
           
           GWT.log(combobox.getDropdownContainerElement().asElement().getInnerHTML());

           
           Element element = combobox.getDropdownResultElement().asElement().getParentElement().getParentElement();
           GWT.log(element.getInnerHTML());
           
           NodeList<Element> elements = element.getElementsByTagName("input");
           GWT.log(String.valueOf(elements.getLength()));
           
           InputElement inputElement = (InputElement) elements.getItem(0);
           
           Event.sinkEvents(inputElement, Event.KEYEVENTS);
           Event.setEventListener(inputElement, new EventListener() {

               @Override
                public void onBrowserEvent(Event event) {
                    if (Event.ONKEYUP == event.getTypeInt()) {
                        GWT.log("foo2");
                        GWT.log(inputElement.getValue());

                        String searchText = inputElement.getValue();

                        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, baseUrl + searchText);
                        builder.setHeader("content-type", "application/json");

                        try {
                            Request response = builder.sendRequest("", new RequestCallback() {

                                @Override
                                public void onResponseReceived(Request request, Response response) {

                                    int statusCode = response.getStatusCode();

                                    if (statusCode == Response.SC_OK) {
                                        String responseBody = response.getText();

                                        JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
                                        JSONObject rootObj = responseObj.isObject();
                                        JSONArray featuresArray = rootObj.get("features").isArray();

                                        List list = new ArrayList();
                                        for (int i = 0; i < featuresArray.size(); i++) {
                                            JSONObject properties = featuresArray.get(i).isObject().get("properties").isObject();

                                            // generisches Suchresultat. Enum (egrid, etc..
//                                          Egrid egrid = new Egrid();
//                                          egrid.setEgrid(properties.get("label").toString().replace("(EGRID)", "")
//                                                  .replaceAll("^.|.$", ""));
//                                          egrid.setLabel(properties.get("label").toString().replaceAll("^.|.$", ""));
//                                          list.add(new EgridSuggestion(egrid));

                                            String egrid = properties.get("label").toString().replace("(EGRID)", "").replaceAll("^.|.$", "");
                                            String egridLabel = properties.get("label").toString().replaceAll("^.|.$","");
                                            list.add(egrid);
                                            
                                            
                                            GWT.log(egrid);
                                            
                                            
//                                            EgridSearchObject searchObject = new EgridSearchObject(
//                                                    GWT.getHostPageBaseURL()
//                                                            + "/ch.so.agi.av.grundstuecke.rechtskraeftig.svg",
//                                                    egrid, egridLabel);
//                                          objects.add(searchObject);
//                                            search.getListSearches().add(searchObject);
                                        }
                                        combobox.addItems(list);
                                        combobox.setSingleValue(searchText);

                                        combobox.reload();
                                        combobox.open();

//                                      Response resp = new Response();
//                                      if (list.isEmpty()) {
//                                          resp.setSuggestions(null);
//                                          callback.onSuggestionsReady(suggestRequest, resp);
//                                          return;
//                                      }
                                        //
//                                      resp.setSuggestions(list);
//                                      callback.onSuggestionsReady(suggestRequest, resp);
                                    } else {
                                        GWT.log("error");
                                        GWT.log(String.valueOf(statusCode));
                                        GWT.log(response.getStatusText());
                                    }
                                }

                                @Override
                                public void onError(Request request, Throwable exception) {
                                    GWT.log("error actually sending the request, never got sent");
                                }
                            });
                        } catch (RequestException e) {
                            GWT.log("request exception");
                            GWT.log(e.getMessage());
                            e.printStackTrace();
                        }

                    }

                }
            });           
           
//           inputElement.
           
//           GWT.log(String.valueOf(element.getChildCount()));
//           GWT.log(combobox.getDropdownResultElement().asElement().getParentElement().getParentElement().getElementsByTagName("input").toString());
//           GWT.log(combobox.getDropdownParent().asElement().getInnerHTML().toString());

           
           
//           GWT.log(String.valueOf(combobox.getChildrenList().size()));
           
//           combobox.getListbox().forEach(widget -> {
//               GWT.log(widget.getClass().toString());
//               GWT.log(String.valueOf(widget.getAbsoluteTop()));
//               GWT.log(String.valueOf(widget.getStyleName()));
//               
//               GWT.log(widget.getElement().getParentElement().getParentElement().getInnerHTML().toString());
//               
//               
////               Option option = (Option) widget;
//////               GWT.log(String.valueOf(option.getAbsoluteTop()));
//////               GWT.log(String.valueOf(option.getText()));
////               
////               option.addKeyPressHandler(event2 -> {
////                  GWT.log("key press"); 
////               });
//               
//           });
           
        });
        columnLeft.add(combobox);

        RootPanel.get().add(row);

        
//        combobox.getListbox().addDomHandler(new InputHandler() {
//            @Override
//            public void onInput(InputEvent event) {
//                GWT.log("***********");
//            }
//        }, InputEvent.getType());
        
//        combobox.addOpenHandler(foo -> {
//            MaterialToast.fireToast("Event: ValueChange State : " + combobox.getSingleValue() + " Value: " + foo);
//            
//            
//            GWT.log($(combobox.getElement()).html());
//
//        });
        
//        combobox.addValueChangeHandler(foo -> {
//            MaterialToast.fireToast("Event: asdfasdf : " + combobox.getSingleValue() + " Value: " + foo);
//        });
        
//        GWT.log(String.valueOf(combobox.getChildrenList().size()));
//        GWT.log(String.valueOf(combobox.getListbox().getWidgetCount()));
        
        
//        combobox.getListbox().forEach(widget -> {
//            GWT.log("foo");
//            GWT.log(widget.getParent().getClass().toString());
//        });
//        
//        Element element = combobox.getListbox().getElement();
        //GWT.log(String.valueOf(element.get));
        
        
//        Element input = combobox.getListbox().getElement();
//        DOM.sinkBitlessEvent(input, "input");
//        DOM.setEventListener(input, event -> GWT.log("Event!"));

        
        
//        combobox.get.getListbox().addDomHandler(event -> {
//            MaterialToast.fireToast("Event: ClickEvent : " + combobox.getSingleValue() + " Value: " + event);
//        }, ClickEvent.getType());
      
//        combobox.getListbox().addDomHandler(event -> {
//            MaterialToast.fireToast("Event: ChangeEvent : " + combobox.getSingleValue() + " Value: " + event);
//        }, ChangeEvent.getType());        
        
        
//        combobox.getListbox().getChildren().forEach(widget -> {
//            GWT.log(widget.getClass().toString());
//            Option option = (gwt.material.design.client.ui.html.Option) widget;
//            GWT.log(option.getLabel());
//            
//            Element input = widget.getElement();
//            DOM.sinkBitlessEvent(input, "input");
//            DOM.setEventListener(input, event -> GWT.log("Event!"));
//        });

//        combobox..forEach(widget -> {
//            GWT.log(widget.getClass().toString());
////            Option option = (gwt.material.design.client.ui.html.Option) widget;
////            GWT.log(option.getLabel());
//            
////            Element input = widget.getElement();
////            DOM.sinkBitlessEvent(input, "input");
////            DOM.setEventListener(input, event -> GWT.log("Event!"));
//          
//            
//            
//        });        
        
//        combobox.getListbox().addHandler(new ChangeHandler() {
//            public void onChange(ChangeEvent event) {
//                // Get the index of the fselected Item
//                Window.alert("foo");
//              }}, ChangeEvent.getType());
        
//        columnLeft.add(combobox);
        
//        RootPanel.get().add(row);

	    
	    
/*
 * RPC stuff
 * */	    
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
	    
	   

		//RootPanel.get().add(new Label("zakaria. Hallo Welt."));
		
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
        //suggestionBox.setWidth("200");

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

}
