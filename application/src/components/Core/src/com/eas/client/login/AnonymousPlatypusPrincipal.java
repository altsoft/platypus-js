/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

/**
 *
 * @author vv
 */
public class AnonymousPlatypusPrincipal extends PlatypusPrincipal {

    public AnonymousPlatypusPrincipal(String aName) {
        super(aName);
    }
    
    @Override
    public boolean hasRole(String string) throws Exception {
        return false;
    }
    
}
