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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AgreementManager;
import org.cgiar.ccafs.marlo.data.model.Agreement;
import org.cgiar.ccafs.marlo.ocs.model.AgreementOCS;
import org.cgiar.ccafs.marlo.ocs.model.CountryOCS;
import org.cgiar.ccafs.marlo.ocs.model.CrpOCS;
import org.cgiar.ccafs.marlo.ocs.model.DonorOCS;
import org.cgiar.ccafs.marlo.ocs.model.PlaOCS;
import org.cgiar.ccafs.marlo.ocs.model.ResearcherOCS;
import org.cgiar.ccafs.marlo.ocs.ws.MarloOcsClient;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Christian Garcia - CIAT/CCAFS
 * @author Julián Rodríguez - CIAT/CCAFS
 */
public class OcsServiceAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -6338578372277010087L;
  /**
   * 
   */
  private String ocsCode;
  private MarloOcsClient ocsClient;
  private AgreementManager agreementManager;

  private AgreementOCS json;


  @Inject
  public OcsServiceAction(APConfig config, MarloOcsClient ocsClient, AgreementManager agreementManager) {
    super(config);
    this.ocsClient = ocsClient;
    this.agreementManager = agreementManager;

  }


  @Override
  public String execute() throws Exception {

    /*
     * Agreement agreement = agreementManager.find(ocsCode);
     * if (agreement != null) {
     * Date today = new Date();
     * if (agreement.getSyncDate().compareTo(today) == 0) {
     * json = this.returnOCS(agreement);
     * } else {
     * json = ocsClient.getagreement(ocsCode);
     * if (json != null) {
     * Agreement theAgreement = this.returnAgreement(json);
     * agreementManager.update(theAgreement);
     * }
     * }
     * } else {
     * json = ocsClient.getagreement(ocsCode);
     * if (json != null) {
     * Agreement theAgreement = this.returnAgreement(json);
     * agreementManager.save(theAgreement);
     * }
     * }
     */
    json = ocsClient.getagreement(ocsCode);


    return SUCCESS;
  }


  public AgreementOCS getJson() {
    return json;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // ocsCode = (StringUtils.trim(((String[]) parameters.get(APConstants.OCS_CODE_REQUEST_ID))[0]));

    Map<String, Parameter> parameters = this.getParameters();
    ocsCode = (StringUtils.trim(parameters.get(APConstants.OCS_CODE_REQUEST_ID).getMultipleValues()[0]));
  }


  /**
   * return an Agreement Object Type given an AgreementOCS type
   * 
   * @author Julián Rodríguez CCAFS/CIAT
   * @param agreementOCS this is the object from the service
   * @since 11/09/2017
   * @return an Agreement Object
   */
  private Agreement returnAgreement(AgreementOCS agreementOCS) {

    Agreement agreement = new Agreement();
    agreement.setId(agreementOCS.getId());
    agreement.setDescription(agreementOCS.getDescription());
    agreement.setShortTitle(agreementOCS.getShortTitle());
    agreement.setObjectives(agreementOCS.getObjectives());
    agreement.setGrantAmmount(agreementOCS.getGrantAmount());
    agreement.setStartDate(agreementOCS.getStartDate());
    agreement.setEndDate(agreementOCS.getEndDate());
    agreement.setExtensionDate(agreementOCS.getExtensionDate());
    agreement.setContractStatus(agreementOCS.getContractStatus());
    agreement.setFundingType(agreementOCS.getFundingType());
    agreement.setOriginalDonorId(agreementOCS.getOriginalDonor().getId());
    agreement.setOriginalDonor(agreementOCS.getOriginalDonor().getName());
    agreement.setDonorId(agreementOCS.getDirectDonor().getId());
    agreement.setDonor(agreementOCS.getDirectDonor().getName());
    agreement.setResearchId(agreementOCS.getResearcher().getId());
    agreement.setReasearchName(agreementOCS.getResearcher().getName());

    Set<CountryOCS> countries = agreementOCS.getCountries().stream().collect(Collectors.toSet());
    agreement.setCountriesAgreements(countries);

    Set<CrpOCS> crps = agreementOCS.getCrps().stream().collect(Collectors.toSet());
    agreement.setCrpsAgreements(crps);

    Set<PlaOCS> plas = agreementOCS.getPlas().stream().collect(Collectors.toSet());
    agreement.setPlasAgreements(plas);

    return agreement;

  }

  /**
   * return an object type AgreementOCS given an Agreement type
   * 
   * @author Julián Rodríguez CCAFS/CIAT
   * @param agreement this is the object store in Database
   * @since 11/09/2017
   * @return an AgreementOCS
   */
  private AgreementOCS returnOCS(Agreement agreement) {

    AgreementOCS agreementOCS = new AgreementOCS();
    agreementOCS.setId(agreement.getId());
    agreementOCS.setDescription(agreement.getDescription());
    agreementOCS.setShortTitle(agreement.getShortTitle());
    agreementOCS.setObjectives(agreement.getObjectives());
    agreementOCS.setGrantAmount(agreement.getGrantAmmount());
    agreementOCS.setStartDate(agreement.getStartDate());
    agreementOCS.setEndDate(agreement.getEndDate());
    agreementOCS.setExtensionDate(agreement.getExtensionDate());
    agreementOCS.setContractStatus(agreement.getContractStatus());
    agreementOCS.setFundingType(agreement.getFundingType());

    DonorOCS originalDonor = new DonorOCS();
    originalDonor.setId(agreement.getOriginalDonorId());
    originalDonor.setName(agreement.getOriginalDonor());

    DonorOCS donor = new DonorOCS();
    donor.setId(agreement.getDonorId());
    donor.setName(agreement.getDonor());

    agreementOCS.setOriginalDonor(originalDonor);
    agreementOCS.setDirectDonor(donor);

    ResearcherOCS researcher = new ResearcherOCS();
    researcher.setId(agreement.getResearchId());
    researcher.setName(agreement.getReasearchName());
    agreementOCS.setResearcher(researcher);


    List<CountryOCS> countries = new ArrayList<CountryOCS>();
    countries.addAll(agreement.getCountriesAgreements());
    agreementOCS.setCountries(countries);

    List<CrpOCS> crps = new ArrayList<CrpOCS>();
    crps.addAll(agreement.getCrpsAgreements());
    agreementOCS.setCrps(crps);

    List<PlaOCS> plas = new ArrayList<PlaOCS>();
    plas.addAll(agreement.getPlasAgreements());
    agreementOCS.setPlas(plas);


    return agreementOCS;
  }

  public void setJson(AgreementOCS json) {
    this.json = json;
  }


}
