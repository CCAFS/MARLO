/**
 * WSMarlo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.cgiar.ccafs.marlo.ocs.ws.client;

public interface WSMarlo extends java.rmi.Remote {
    public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[] getMarloAgreements(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[] getMarloAgreeCrp(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[] getMarloAgreeCountry(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[] getMarloPla(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[] getMarloPlaCountry(java.lang.String plaId) throws java.rmi.RemoteException;
    public org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[] getMarloAgreeDocument(java.lang.String agreementId) throws java.rmi.RemoteException;
}
