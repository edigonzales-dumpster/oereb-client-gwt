package ch.so.agi.oereb.webclient.shared;

import com.google.gwt.user.client.rpc.SerializableException;

@SuppressWarnings("deprecation")
public class ExtractServiceException extends SerializableException {
    private static final long serialVersionUID = 1L;
    
    public ExtractServiceException() {}

    public ExtractServiceException(String message) {
        super(message);
    }
}
