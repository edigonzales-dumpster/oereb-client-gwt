package com.gwidgets.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwidgets.client.User.Position;
import com.gwidgets.shared.FieldVerifier;
import com.gwidgets.shared.GreetingResponse;
import com.gwidgets.shared.GreetingService;
import com.gwidgets.shared.GreetingServiceAsync;

import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
import gwt.material.design.addins.client.autocomplete.constants.AutocompleteType;
import gwt.material.design.addins.client.combobox.MaterialComboBox;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialDropDown;
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

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public void onModuleLoad() {
//	    MaterialPanel panel = new MaterialPanel();
//	    panel.setContainerEnabled(true);
//	    panel.setBackgroundColor(Color.GREEN);
//	    panel.setGrid("s6 l3");

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
       
        UserOracle userOracle = new UserOracle();

        MaterialAutoComplete autocomplete = new MaterialAutoComplete(userOracle);
        autocomplete.setType(AutocompleteType.TEXT);
        autocomplete.setPlaceholder("Suche");
//        autocomplete.setLimit(5);
        autocomplete.setAutoSuggestLimit(5);
        
//        columnLeft.add(autocomplete);
        
        MaterialButton button = new MaterialButton();
        button.setText("PDF");
//        columnLeft.add(button);
        
        
        
        Div div = new Div();
        div.add(autocomplete);
        div.add(button);

        columnLeft.add(div);
        
//        MaterialSearch search = new MaterialSearch();
//        columnLeft.add(search);
        
//        MaterialComboBox combobox = new MaterialComboBox();
//        combobox.setPlaceholder("Suche");
//        combobox.setAllowClear(true);
//        combobox.addItem("", "");
//        combobox.addItem("foo", "foo");
//        combobox.addItem("bar", "bar");
//        
//        combobox.getListbox().addDomHandler(new InputHandler() {
//            @Override
//            public void onInput(InputEvent event) {
//                GWT.log("***********");
//            }
//        }, InputEvent.getType());
//        
//        columnLeft.add(combobox);
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
        
        RootPanel.get().add(row);
//        RootPanel.get().add(column);

	    
	    //https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?_dc=1566232303797&limit=15&query=egr+CH1070080
	 
//	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET /*.POST or .PUT or .DELETE*/, someStringUrl);

	    
	    
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
