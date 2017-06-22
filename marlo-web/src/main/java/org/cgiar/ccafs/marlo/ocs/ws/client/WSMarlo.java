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

public interface WSMarlo extends java.rmi.Remote {

  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry[] getMarloAgreeCountry(java.lang.String agreementId)
    throws java.rmi.RemoteException;

  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp[] getMarloAgreeCrp(java.lang.String agreementId)
    throws java.rmi.RemoteException;

  public org.cgiar.ccafs.marlo.ocs.ws.client.FileTransfer[] getMarloAgreeDocument(java.lang.String agreementId)
    throws java.rmi.RemoteException;

  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree[] getMarloAgreements(java.lang.String agreementId)
    throws java.rmi.RemoteException;

  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla[] getMarloPla(java.lang.String agreementId)
    throws java.rmi.RemoteException;

  public org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry[] getMarloPlaCountry(java.lang.String plaId)
    throws java.rmi.RemoteException;
}
