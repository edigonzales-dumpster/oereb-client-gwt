package ch.so.agi.oereb.webclient.shared;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("extract")
public interface ExtractService extends RemoteService {
    ExtractResponse extractServer(String egrid) throws IllegalArgumentException, IOException;
}
