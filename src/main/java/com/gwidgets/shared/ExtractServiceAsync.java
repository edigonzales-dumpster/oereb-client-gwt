package com.gwidgets.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ExtractService</code>.
 */
public interface ExtractServiceAsync {
    void extractServer(String egrid, AsyncCallback<ExtractResponse> callback)
            throws IllegalArgumentException;
}
