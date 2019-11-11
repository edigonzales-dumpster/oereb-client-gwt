package ch.so.agi.oereb.webclient.shared.models.cadastralsurveying;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Building implements IsSerializable {
    private int egid;
    
    private int edid;
    
    private Address postalAddress;

    public int getEgid() {
        return egid;
    }

    public void setEgid(int egid) {
        this.egid = egid;
    }

    public int getEdid() {
        return edid;
    }

    public void setEdid(int edid) {
        this.edid = edid;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }
}
