/**
 * MarloService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.cgiar.ccafs.marlo.ocs.ws.client;

public interface MarloService extends javax.xml.rpc.Service {
    public java.lang.String getMarloPortAddress();

    public org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo getMarloPort() throws javax.xml.rpc.ServiceException;

    public org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo getMarloPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
