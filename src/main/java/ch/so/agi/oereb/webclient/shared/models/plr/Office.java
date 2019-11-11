package ch.so.agi.oereb.webclient.shared.models.plr;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Office implements IsSerializable {
    String name;
    
    String officeAtWeb;
    
    String street;
    
    String number;
    
    String postalCode;
    
    String city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficeAtWeb() {
        return officeAtWeb;
    }

    public void setOfficeAtWeb(String officeAtWeb) {
        this.officeAtWeb = officeAtWeb;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
