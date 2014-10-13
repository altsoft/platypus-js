/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.script.NoPublisherException;
import java.util.Collections;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author vv
 */
public class AnonymousPlatypusPrincipal extends PlatypusPrincipal {

    public AnonymousPlatypusPrincipal(String aName) {
        super(aName, null, Collections.emptySet(), null);
    }

    @Override
    public boolean hasRole(String aRole){
        return false;
    }
    
    @Override
    public void logout(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        if (aOnSuccess != null) {
            // async style
            aOnSuccess.call(null, new Object[]{});
        }
        // sync style
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
