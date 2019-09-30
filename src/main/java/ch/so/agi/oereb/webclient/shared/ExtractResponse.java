package ch.so.agi.oereb.webclient.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import ch.so.agi.oereb.webclient.shared.models.Extract;

public class ExtractResponse implements IsSerializable {
    private Extract extract;
        
    public Extract getExtract() {
        return extract;
    }

    public void setExtract(Extract extract) {
        this.extract = extract;
    }
}
