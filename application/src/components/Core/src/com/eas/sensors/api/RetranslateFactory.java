/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.sensors.api;

/**
 *
 * @author Andrew
 */
public interface RetranslateFactory {
    
    public void send(Packet aPacket, String aURL);
    
    public void send(Object aPacket, String aURL);
}