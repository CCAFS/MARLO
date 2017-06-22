/**
 * WSMarlo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.cgiar.ciat.abw.control.logic;

public interface WSMarlo extends java.rmi.Remote {
    public org.cgiar.ciat.abw.control.logic.TWsMarloAgree[] getMarloAgreements(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ciat.abw.control.logic.TWsMarloAgreeCrp[] getMarloAgreeCrp(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ciat.abw.control.logic.TWsMarloAgreeCountry[] getMarloAgreeCountry(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ciat.abw.control.logic.TWsMarloPla[] getMarloPla(java.lang.String agreementId) throws java.rmi.RemoteException;
    public org.cgiar.ciat.abw.control.logic.TWsMarloPlaCountry[] getMarloPlaCountry(java.lang.String plaId) throws java.rmi.RemoteException;
    public org.cgiar.ciat.abw.control.logic.FileTransfer[] getMarloAgreeDocument(java.lang.String agreementId) throws java.rmi.RemoteException;
}
