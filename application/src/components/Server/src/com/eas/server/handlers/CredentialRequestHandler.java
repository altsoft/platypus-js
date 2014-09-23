/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.login.DbPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class CredentialRequestHandler extends SessionRequestHandler<CredentialRequest, CredentialRequest.Response> {

    public CredentialRequestHandler(PlatypusServerCore aServerCore, CredentialRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<CredentialRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        String startAppElement = null;
        PlatypusPrincipal principal = aSession.getPrincipal();
        if (principal instanceof DbPlatypusPrincipal) {
            startAppElement = ((DbPlatypusPrincipal) principal).getStartAppElement();
        }
        if (startAppElement == null) {
            startAppElement = getServerCore().getDefaultAppElement();
        }
        if (onSuccess != null) {
            onSuccess.accept(new CredentialRequest.Response(startAppElement, principal.getName()));
        }
    }
}