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
import org.cgiar.ccafs.marlo.ocs.model.CountryOCS;
import org.cgiar.ccafs.marlo.ocs.model.CrpOCS;
import org.cgiar.ccafs.marlo.ocs.model.DonorOCS;
import org.cgiar.ccafs.marlo.ocs.model.PartnerOCS;
import org.cgiar.ccafs.marlo.ocs.model.PlaOCS;
import org.cgiar.ccafs.marlo.ocs.model.ResearcherOCS;
import org.cgiar.ccafs.marlo.ocs.ws.client.MarloService;
import org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgree;
import org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCountry;
import org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloAgreeCrp;
import org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPla;
import org.cgiar.ccafs.marlo.ocs.ws.client.TWsMarloPlaCountry;
import org.cgiar.ccafs.marlo.ocs.ws.client.WSMarlo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsThread implements Runnable {


  public static Logger LOG = LoggerFactory.getLogger(WsThread.class);
  private APConfig apConfig;
  private SimpleDateFormat formatter;
  private int type;
  private String agreementID;
  private AgreementOCS agreementOCS;

  public WsThread(APConfig apConfig, int type, String agreementID, AgreementOCS agreementOCS) {
    this.apConfig = apConfig;
    formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
    this.type = type;
    this.agreementOCS = agreementOCS;
    this.agreementID = agreementID;

  }

  /**
   * Get the client for the ws and authenticated
   * 
   * @return the client to use the service
   */
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

  /**
   * Load the basic info received from WS
   * 
   * @param agreementID agreementID to search
   * @param agreementOCS object AgreementOCS where we are to load the info
   */
  public void loadBasicInfo(String agreementID, AgreementOCS agreementOCS) {
    try {

      List<TWsMarloAgree> agrees = this.getWSClient().getMarloAgreements(agreementID);
      if (agrees != null && agrees.size() > 0) {
        TWsMarloAgree myAgree = agrees.get(0);
        agreementOCS.setId(myAgree.getAgreementId());
        agreementOCS.setObjectives(myAgree.getObjectives());
        agreementOCS.setDescription(myAgree.getDescription());
        DonorOCS donorOCS = new DonorOCS();
        donorOCS.setId(myAgree.getDonor());
        donorOCS.setName(myAgree.getDonorText());
        agreementOCS.setDonor(donorOCS);
        agreementOCS.setEndDate(formatter.parse(myAgree.getEndDate().split("\\+")[0]));
        agreementOCS.setStartDate(formatter.parse(myAgree.getStartDate().split("\\+")[0]));
        agreementOCS.setExtensionDate(formatter.parse(myAgree.getExtentionDate().split("\\+")[0]));
        agreementOCS.setFundingType(myAgree.getFundingType());
        agreementOCS.setGrantAmount(myAgree.getGrantAmount().toString());
        ResearcherOCS researcherOCS = new ResearcherOCS();
        researcherOCS.setId(myAgree.getResearcher());
        researcherOCS.setName(myAgree.getResearcherText());
        agreementOCS.setResearcher(researcherOCS);
        agreementOCS.setContractStatus(myAgree.getStatus());
      }
    } catch (ParseException e) {
      e.printStackTrace();
      LOG.error(e.getMessage());
    }
  }

  /**
   * Load the countries info received from WS
   * 
   * @param agreementID agreementID to search
   * @param agreementOCS object AgreementOCS where we are to load the info
   */
  public void loadCountries(String agreementID, AgreementOCS agreementOCS) {
    List<TWsMarloAgreeCountry> agreesCountries = this.getWSClient().getMarloAgreeCountry(agreementID);
    agreementOCS.setCountries(new ArrayList<CountryOCS>());
    if (agreesCountries != null && agreesCountries.size() > 0) {
      for (TWsMarloAgreeCountry tWsMarloAgreeCountry : agreesCountries) {
        CountryOCS countryOCS = new CountryOCS();
        countryOCS.setCode(tWsMarloAgreeCountry.getId().getCountry());
        countryOCS.setDescription(tWsMarloAgreeCountry.getId().getCountrytext());
        countryOCS.setPercentage(tWsMarloAgreeCountry.getId().getPercentage());
        agreementOCS.getCountries().add(countryOCS);
      }
    }
  }

  /**
   * Load the crps info received from WS
   * 
   * @param agreementID agreementID to search
   * @param agreementOCS object AgreementOCS where we are to load the info
   */
  public void loadCrps(String agreementID, AgreementOCS agreementOCS) {
    List<TWsMarloAgreeCrp> agreesCrps = this.getWSClient().getMarloAgreeCrp(agreementID);
    agreementOCS.setCrps(new ArrayList<CrpOCS>());
    if (agreesCrps != null && agreesCrps.size() > 0) {
      for (TWsMarloAgreeCrp tWsMarloAgreeCrp : agreesCrps) {
        CrpOCS crpOCS = new CrpOCS();
        crpOCS.setId(tWsMarloAgreeCrp.getId().getCrp());
        crpOCS.setDescription(tWsMarloAgreeCrp.getId().getCrpText());
        crpOCS.setPercentage(tWsMarloAgreeCrp.getId().getPercentage());
        agreementOCS.getCrps().add(crpOCS);
      }
    }
  }

  /**
   * Load the plas info received from WS
   * 
   * @param agreementID agreementID to search
   * @param agreementOCS object AgreementOCS where we are to load the info
   */
  public void loadPlas(String agreementID, AgreementOCS agreementOCS) {
    List<TWsMarloPla> agreesPlas = this.getWSClient().getMarloPla(agreementID);
    agreementOCS.setPlas(new ArrayList<PlaOCS>());
    if (agreesPlas != null && agreesPlas.size() > 0) {
      for (TWsMarloPla tWsMarloPla : agreesPlas) {
        PlaOCS plaOCS = new PlaOCS();
        plaOCS.setDescription(tWsMarloPla.getDescription());
        plaOCS.setId(tWsMarloPla.getPlaId());
        plaOCS.setCountries(new ArrayList<>());
        plaOCS.setPartners(new ArrayList<>());
        List<TWsMarloPlaCountry> agreesCountries = this.getWSClient().getMarloPlaCountry(plaOCS.getId());
        if (agreesCountries != null) {
          for (TWsMarloPlaCountry tWsMarloPlaCountry : agreesCountries) {
            CountryOCS countryOCS = new CountryOCS();
            countryOCS.setCode(tWsMarloPlaCountry.getId().getCountry());
            countryOCS.setDescription(tWsMarloPlaCountry.getId().getCountryText());
            countryOCS.setPercentage(tWsMarloPlaCountry.getId().getPercentage());
            plaOCS.getCountries().add(countryOCS);
          }
        }
        PartnerOCS partnerOCS = new PartnerOCS();
        partnerOCS.setId(tWsMarloPla.getPartner());
        partnerOCS.setName(tWsMarloPla.getPartnertext());
        plaOCS.getPartners().add(partnerOCS);
        agreementOCS.getPlas().add(plaOCS);
      }
    }
  }

  @Override
  public void run() {
    switch (type) {
      case 1:
        this.loadBasicInfo(agreementID, agreementOCS);
        break;
      case 2:
        this.loadCountries(agreementID, agreementOCS);
        break;
      case 3:
        this.loadCrps(agreementID, agreementOCS);
        break;
      case 4:
        this.loadPlas(agreementID, agreementOCS);
        break;

      default:
        break;
    }

  }
}
