/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.ocs.ws.client;

public class WSMarloProxy implements org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo {

  private String _endpoint = null;
  private org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo wSMarlo = null;

  public WSMarloProxy() {
    this._initWSMarloProxy();
  }

  public WSMarloProxy(String endpoint) {
    _endpoint = endpoint;
    this._initWSMarloProxy();
  }

  private void _initWSMarloProxy() {
    try {
      wSMarlo = (new org.cgiar.ccafs.marlo.ocs.ws.client.MarloServiceLocator()).getMarloPort();
      if (wSMarlo != null) {
        if (_endpoint != null) {
          ((javax.xml.rpc.Stub) wSMarlo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        } else {
          _endpoint = (String) ((javax.xml.rpc.Stub) wSMarlo)._getProperty("javax.xml.rpc.service.endpoint.address");
        }
      }

    } catch (javax.xml.rpc.ServiceException serviceException) {
    }
  }

  public String getEndpoint() {
    return _endpoint;
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[] getMarloAgreeCountry(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (wSMarlo == null) {
      this._initWSMarloProxy();
    }
    return wSMarlo.getMarloAgreeCountry(agreementId);
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[] getMarloAgreeCrp(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (wSMarlo == null) {
      this._initWSMarloProxy();
    }
    return wSMarlo.getMarloAgreeCrp(agreementId);
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[] getMarloAgreeDocument(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (wSMarlo == null) {
      this._initWSMarloProxy();
    }
    return wSMarlo.getMarloAgreeDocument(agreementId);
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[] getMarloAgreements(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (wSMarlo == null) {
      this._initWSMarloProxy();
    }
    return wSMarlo.getMarloAgreements(agreementId);
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[] getMarloPla(java.lang.String agreementId)
    throws java.rmi.RemoteException {
    if (wSMarlo == null) {
      this._initWSMarloProxy();
    }
    return wSMarlo.getMarloPla(agreementId);
  }

  @Override
  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[] getMarloPlaCountry(java.lang.String plaId)
    throws java.rmi.RemoteException {
    if (wSMarlo == null) {
      this._initWSMarloProxy();
    }
    return wSMarlo.getMarloPlaCountry(plaId);
  }

  public org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo getWSMarlo() {
    if (wSMarlo == null) {
      this._initWSMarloProxy();
    }
    return wSMarlo;
  }

  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSMarlo != null) {
      ((javax.xml.rpc.Stub) wSMarlo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    }

  }


}