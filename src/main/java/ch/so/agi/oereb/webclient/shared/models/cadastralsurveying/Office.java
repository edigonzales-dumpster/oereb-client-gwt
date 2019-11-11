package ch.so.agi.oereb.webclient.shared.models.cadastralsurveying;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Office implements IsSerializable {
    String name;
    
    String officeAtWeb;
    
    Address postalAddress;

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

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }
}
