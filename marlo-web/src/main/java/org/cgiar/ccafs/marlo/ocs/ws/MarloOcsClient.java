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


package org.cgiar.ccafs.marlo.ocs.ws;

import org.cgiar.ccafs.marlo.ocs.model.AgreementOCS;
import org.cgiar.ccafs.marlo.ocs.model.ResourceInfoOCS;
import org.cgiar.ccafs.marlo.ocs.ws.client.MarloService;
import org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloResStudies;
import org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloResourceInfo;
import org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class MarloOcsClient {

  public static Logger LOG = LoggerFactory.getLogger(MarloOcsClient.class);
  private static final int MYTHREADS = 4;


  private final APConfig apConfig;

  @Inject
  public MarloOcsClient(APConfig apConfig) {
    this.apConfig = apConfig;

  }

  /**
   * @param agreementID the id to search
   * @return AgreementOCS object with all the info
   */
  public AgreementOCS getagreement(String agreementID) {
    AgreementOCS agreementOCS = new AgreementOCS();
    ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
    // Run the services on parallel
    for (int i = 1; i <= 4; i++) {
      Runnable worker = new WsThread(apConfig, i, agreementID, agreementOCS);
      executor.execute(worker);
    }
    executor.shutdownNow();
    // Wait until all threads are finish
    while (!executor.isTerminated()) {
      // LOG.info("Ws OCS waiting");
    }

    if (agreementOCS.getId() == null) {
      return null;
    } else {
      System.out.println(agreementOCS.getDescription());
      return agreementOCS;
    }
  }


  public ResourceInfoOCS getHRInformation(String resourceID) {
    ResourceInfoOCS resourceInfoOCS = new ResourceInfoOCS();
    try {
      List<TWsMarloResourceInfo> resource = this.getWSClient().getMarloResourceInformation(resourceID);
      List<TWsMarloResStudies> rEstudies = this.getWSClient().getMarloResStudies(resourceID);

      if ((resource != null) && (resource.size() > 0)) {
        TWsMarloResourceInfo myResource = resource.get(0);
        resourceInfoOCS.setId(myResource.getResourceId());
        resourceInfoOCS.setFirstName(myResource.getFirstName());
        resourceInfoOCS.setLastName(myResource.getSurname());
        resourceInfoOCS.setGender(myResource.getGenderFx());
        resourceInfoOCS.setCityOfBirth(myResource.getCityOfBirth());
        resourceInfoOCS.setCityOfBirthISO(myResource.getCouOfBirthId());
        resourceInfoOCS.setEmail(myResource.getEMail());
        resourceInfoOCS.setProfession(myResource.getProfession());
        resourceInfoOCS.setSupervisor1(myResource.getSupervisor1());
        resourceInfoOCS.setSupervisor2(myResource.getSupervisor2());
        resourceInfoOCS.setSupervisor3(myResource.getSupervisor3());
      }

      if ((rEstudies != null) && (rEstudies.size() > 0)) {
        resourceInfoOCS.setInstitucion(rEstudies.get(0).getId().getInstitution());
        resourceInfoOCS.setCountryofIntitution(rEstudies.get(0).getId().getCountry());
        resourceInfoOCS.setCountryofIntitutionISO(rEstudies.get(0).getId().getCountryId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getMessage());
    }

    return resourceInfoOCS;

  }

  public WSMarlo getWSClient() {
    MarloService service = new MarloService();
    WSMarlo client = service.getMarloPort();
    Map<String, Object> reqCtx = ((BindingProvider) client).getRequestContext();
    reqCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, apConfig.getOcsLink());
    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    headers.put("username", Collections.singletonList(apConfig.getOcsUser()));
    headers.put("password", Collections.singletonList(apConfig.getOcsPassword()));
    reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

    return client;

  }


}
