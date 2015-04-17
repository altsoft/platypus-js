/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.client.AppConnection;
import com.eas.client.dataflow.FlowProvider;
import com.eas.client.dataflow.FlowProviderFailedException;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class PlatypusFlowProvider implements FlowProvider {

    private static final String ROWSET_MISSING_IN_RESPONSE = "Rowset response hasn't returned any rowset. May be dml query is executed as select query.";

    protected PlatypusClient serverProxy;
    protected Fields expectedFields;
    protected boolean procedure;
    protected String entityName;
    protected AppConnection conn;

    public PlatypusFlowProvider(PlatypusClient aClient, String aEntityName, Fields aExpectedFields) {
        super();
        serverProxy = aClient;
        conn = serverProxy.getConn();
        entityName = aEntityName;
        expectedFields = aExpectedFields;
    }

    @Override
    public JSObject nextPage(Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws FlowProviderFailedException {
        throw new FlowProviderFailedException("Method \"nextPage()\" is not supported in three-tier mode.");
    }

    @Override
    public void close() throws Exception {
        // no op in three-tier mode
    }

    @Override
    public JSObject refresh(Parameters aParams, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws FlowProviderFailedException {
        ExecuteQueryRequest request = new ExecuteQueryRequest(entityName, aParams, expectedFields);
        if (onSuccess != null) {
            try {
                conn.<ExecuteQueryRequest.Response>enqueueRequest(request, (ExecuteQueryRequest.Response aResponse) -> {
                    if (aResponse.getRowset() == null) {
                        if (onFailure != null) {
                            onFailure.accept(new FlowProviderFailedException(ROWSET_MISSING_IN_RESPONSE));
                        }
                    } else {
                        onSuccess.accept(aResponse.getRowset());
                    }
                }, (Exception aException) -> {
                    if (onFailure != null) {
                        onFailure.accept(aException);
                    }
                });
                return null;
            } catch (Exception ex) {
                throw new FlowProviderFailedException(ex);
            }
        } else {
            try {
                ExecuteQueryRequest.Response response = conn.executeRequest(request);
                if (response.getRowset() == null) {
                    throw new FlowProviderFailedException(ROWSET_MISSING_IN_RESPONSE);
                }
                return response.getRowset();
            } catch (Exception ex) {
                throw new FlowProviderFailedException(ex);
            }
        }
    }

    @Override
    public boolean isProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(boolean aProcedure) {
        procedure = aProcedure;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public int getPageSize() {
        throw new UnsupportedOperationException("Not supported yet."); //NOI18N
    }

    @Override
    public void setPageSize(int aValue) {
    }
}
