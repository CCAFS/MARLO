package org.cgiar.ccafs.marlo.ocs.ws.client;

public class WSMarloProxy implements org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo {
  private String _endpoint = null;
  private org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo wSMarlo = null;
  
  public WSMarloProxy() {
    _initWSMarloProxy();
  }
  
  public WSMarloProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSMarloProxy();
  }
  
  private void _initWSMarloProxy() {
    try {
      wSMarlo = (new org.cgiar.ccafs.marlo.ocs.ws.client.MarloServiceLocator()).getMarloPort();
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
  
  public org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo getWSMarlo() {
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo;
  }
  
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[] getMarloAgreements(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreements(agreementId);
  }
  
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[] getMarloAgreeCrp(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreeCrp(agreementId);
  }
  
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[] getMarloAgreeCountry(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreeCountry(agreementId);
  }
  
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[] getMarloPla(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloPla(agreementId);
  }
  
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[] getMarloPlaCountry(java.lang.String plaId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloPlaCountry(plaId);
  }
  
  public org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[] getMarloAgreeDocument(java.lang.String agreementId) throws java.rmi.RemoteException{
    if (wSMarlo == null)
      _initWSMarloProxy();
    return wSMarlo.getMarloAgreeDocument(agreementId);
  }
  
  
}