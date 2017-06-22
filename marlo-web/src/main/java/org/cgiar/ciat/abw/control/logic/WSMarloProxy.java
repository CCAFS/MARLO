package org.cgiar.ciat.abw.control.logic;

public class WSMarloProxy implements org.cgiar.ciat.abw.control.logic.WSMarlo {
  private String _endpoint = null;
  private org.cgiar.ciat.abw.control.logic.WSMarlo wSMarlo = null;
  
  public WSMarloProxy() {
    _initWSMarloProxy();
  }
  
  public WSMarloProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSMarloProxy();
  }
  
  private void _initWSMarloProxy() {
    try {
      wSMarlo = (new org.cgiar.ciat.abw.control.logic.MarloServiceLocator()).getMarloPort();
      if (wSMarlo != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSMarlo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSMarlo)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSMarlo != null)
      ((javax.xml.rpc.Stub)wSMarlo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.cgiar.ciat.abw.control.logic.WSMarlo getWSMarlo() {
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo;
  }
  
  public org.cgiar.ciat.abw.control.logic.TWsMarloAgree[] getMarloAgreements(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreements(agreementId);
  }
  
  public org.cgiar.ciat.abw.control.logic.TWsMarloAgreeCrp[] getMarloAgreeCrp(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreeCrp(agreementId);
  }
  
  public org.cgiar.ciat.abw.control.logic.TWsMarloAgreeCountry[] getMarloAgreeCountry(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreeCountry(agreementId);
  }
  
  public org.cgiar.ciat.abw.control.logic.TWsMarloPla[] getMarloPla(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloPla(agreementId);
  }
  
  public org.cgiar.ciat.abw.control.logic.TWsMarloPlaCountry[] getMarloPlaCountry(java.lang.String plaId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloPlaCountry(plaId);
  }
  
  public org.cgiar.ciat.abw.control.logic.FileTransfer[] getMarloAgreeDocument(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreeDocument(agreementId);
  }
  
  
}