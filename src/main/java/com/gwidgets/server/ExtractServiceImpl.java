package com.gwidgets.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.gwidgets.shared.ExtractResponse;
import com.gwidgets.shared.ExtractService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ExtractServiceImpl extends RemoteServiceServlet implements ExtractService {

    @Override
    public ExtractResponse extractServer(String egrid) throws IllegalArgumentException {
        ExtractResponse response = new ExtractResponse();
        response.setEgrid("lilalaunebär");
        
        
        return response;
    }

}
