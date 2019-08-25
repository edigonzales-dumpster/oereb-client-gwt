package com.gwidgets.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.typeahead.client.base.StringDataset;
import org.gwtbootstrap3.extras.typeahead.client.base.Suggestion;
import org.gwtbootstrap3.extras.typeahead.client.base.SuggestionCallback;
import org.gwtbootstrap3.extras.typeahead.client.ui.Typeahead;
import org.gwtbootstrap3.extras.typeahead.client.base.Dataset;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwidgets.client.User.Position;
import com.gwidgets.shared.FieldVerifier;
import com.gwidgets.shared.GreetingResponse;
import com.gwidgets.shared.GreetingService;
import com.gwidgets.shared.GreetingServiceAsync;

//import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
//import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;
//import gwt.material.design.addins.client.autocomplete.constants.AutocompleteType;
//import gwt.material.design.addins.client.combobox.MaterialComboBox;
//import gwt.material.design.client.base.SearchObject;
//import gwt.material.design.client.constants.Color;
//import gwt.material.design.client.constants.IconType;
//import gwt.material.design.client.events.HandlerRegistry;
//import gwt.material.design.client.ui.MaterialButton;
//import gwt.material.design.client.ui.MaterialColumn;
//import gwt.material.design.client.ui.MaterialDropDown;
//import gwt.material.design.client.ui.MaterialLink;
//import gwt.material.design.client.ui.MaterialNavBar;
//import gwt.material.design.client.ui.MaterialNavBrand;
//import gwt.material.design.client.ui.MaterialPanel;
//import gwt.material.design.client.ui.MaterialRow;
//import gwt.material.design.client.ui.html.Div;




public class AppEntryPoint implements EntryPoint {

    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    
    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
    
	public void onModuleLoad() {
	    
	    Button button = new Button();
	    button.setType(ButtonType.DEFAULT);
	    button.setIcon(IconType.STAR);
	    button.setText("PDF");
	    
	    
	    Container container = new Container();
	    Row row = new Row();
	    
        Column columnLeft = new Column("MD_4");
        Column columnRight = new Column("MD_8");
	    
        columnLeft.add(new Label("controls belongs here"));
        columnRight.add(new Label("map belongs here"));

	    
        
        final List<Person> persons = new ArrayList<Person>();
        persons.add(new Person("Bill", 50));
        persons.add(new Person("Bob", 38));
        persons.add(new Person("Bobak", 24));
        persons.add(new Person("Dawton", 27));
        persons.add(new Person("Dinkelstein", 66));
        persons.add(new Person("Eumon", 13));
        persons.add(new Person("Gene", 42));
        persons.add(new Person("Gus", 31));
        persons.add(new Person("Jebediah", 57));
        persons.add(new Person("Kirrim", 73));
        persons.add(new Person("Linus", 103));
        persons.add(new Person("Mortimer", 7));
        persons.add(new Person("Walt", 15));
        persons.add(new Person("Wernher", 52));

        List<String> names = new ArrayList<String>();
        for (Person person : persons) {
          names.add(person.name);
        }

//        StringDataset dataset = new StringDataset(names);
//        Typeahead<String> typeahead = new Typeahead<String>(dataset);
//        typeahead.setPlaceholder("Enter a name");
//        typeahead.setWidth("300px");
        
        
        Typeahead<Person> typeahead = new Typeahead<Person>(new Dataset<Person>() {
            @Override
            public void findMatches(String query, SuggestionCallback<Person> callback) {
                
                
                
                
                List<Suggestion<Person>> suggestions = new ArrayList<Suggestion<Person>>();
                String queryLower = query.toLowerCase();
                for (Person person : persons) {
                    String name = person.getName();
                    if (name.toLowerCase().contains(queryLower)) {
                        Suggestion<Person> s = Suggestion.create(name, person, this);
                        suggestions.add(s);
                    }
                }
                callback.execute(suggestions);
            }
        });
        typeahead.setPlaceholder("Enter a name");
        
        
        
        
        columnLeft.add(typeahead);
	    
//	    columnLeft.add(button);
	    
	    
	    
        row.add(columnLeft);
        row.add(columnRight);
	    
        RootPanel.get().add(row);
	    
	    
//	    MaterialRow row = new MaterialRow();
//
//	    MaterialColumn columnLeft = new MaterialColumn();
//	    columnLeft.setGrid("s4");
//	    columnLeft.setBackgroundColor(Color.WHITE);
//	    columnLeft.add(new Label("Controls belong here."));
//
//	    MaterialColumn columnRight = new MaterialColumn();
//	    columnRight.setGrid("s8");
//	    columnRight.setBackgroundColor(Color.LIGHT_GREEN);
//	    columnRight.add(new Label("Map belongs here."));
//   
//	    row.add(columnLeft);
//        row.add(columnRight);
//       
//        UserOracle userOracle = new UserOracle();
//
//        MaterialAutoComplete autocomplete = new MaterialAutoComplete(userOracle);
//        autocomplete.setType(AutocompleteType.TEXT);
//        autocomplete.setPlaceholder("Suche");
//        autocomplete.setAutoSuggestLimit(5);
//        // FIXME: remove text on select 
//        
////        autocomplete.addKeyUpHandler(event -> {
//////           GWT.log("key down"); 
////           GWT.log(autocomplete.getSuggestBox().getText());
////           if (autocomplete.getSuggestBox().getText().trim().equalsIgnoreCase("")) {
//////               GWT.log("emptY");
//////               userOracle.clear();
////               MaterialSuggestionOracle oracle = (MaterialSuggestionOracle) autocomplete.getSuggestions();
////               oracle.clear();
////               
////           }
//////           userOracle.clear();
////        });
//        
//        MaterialRow buttonRow = new MaterialRow();
//        
//        MaterialButton button1 = new MaterialButton();
//        button1.setText("PDF");
//        //button1.setMargin(5.0);
//        button1.setMarginRight(5.0);
//        buttonRow.add(button1);
//        
//        MaterialButton button2 = new MaterialButton();
//        button2.setText("FOO");
//        button2.setBackgroundColor(Color.WHITE);
//        button2.setTextColor(Color.RED_DARKEN_4);
//        buttonRow.add(button2);
//        
//
////        Div div1 = new Div();
////        div1.add(button1);
////        
////        Div div2 = new Div();
////        div2.add(button2);
//
//        columnLeft.add(autocomplete);
//        columnLeft.add(buttonRow);
////        columnLeft.add(div1);
////        columnLeft.add(div2);
//
//        
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
