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

package org.cgiar.ccafs.marlo.action.bi;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.BiParametersManager;
import org.cgiar.ccafs.marlo.data.manager.BiReportsManager;
import org.cgiar.ccafs.marlo.data.model.BiParameters;
import org.cgiar.ccafs.marlo.data.model.BiReports;
import org.cgiar.ccafs.marlo.rest.services.deliverables.AzureClientAPI;
import org.cgiar.ccafs.marlo.rest.services.deliverables.PowerBiClientAPI;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.Datasets;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.Identities;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.PowerBiBody;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.Reports;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class BiReportsAction extends BaseAction {

  private static final long serialVersionUID = 1329042468240291639L;

  private static final Logger LOG = LoggerFactory.getLogger(BiReportsAction.class);

  private final String CLIENT_CREDENCIALS = "client_credentials";

  private final int API_TOKEN_URL = 0;
  private final int AZURE_API_URL = 1;
  private final int TENANT_ID = 2;
  private final int SECRET = 3;
  private final int APP_ID = 4;
  private final int RESOURCE_URL = 5;

  // Managers
  private BiReportsManager biReportsManager;
  private BiParametersManager biParametersManager;

  // Front-end
  private List<BiReports> biReports;
  private List<BiParameters> biParameters;

  @Inject
  public BiReportsAction(BiReportsManager biReportsManager, BiParametersManager biParametersManager) {
    this.biReportsManager = biReportsManager;
    this.biParametersManager = biParametersManager;
  }

  private PowerBiBody generatePowerBiBody(String datasetId, String reportId) {
    PowerBiBody powerBiBody = new PowerBiBody();

    /* Datasets */
    List<Datasets> datasets = new ArrayList<>();
    Datasets dataset = new Datasets();
    dataset.setId(datasetId);
    datasets.add(dataset);
    powerBiBody.setDatasets(datasets);

    /* Reports */
    List<Reports> reports = new ArrayList<>();
    Reports report = new Reports();
    report.setId(reportId);
    reports.add(report);
    powerBiBody.setReports(reports);

    /* Identities */
    List<Identities> identities = new ArrayList<>();
    Identities identitie = new Identities();
    identitie.setUsername(this.getCurrentUser().getUsername());

    /* Roles */
    List<String> roles = new ArrayList<>();
    roles.add(this.getCurrentCrp().getAcronym());
    identitie.setRoles(roles);

    /* Datasets */
    List<String> datasetsIdentities = new ArrayList<>();
    datasetsIdentities.add(datasetId);
    identitie.setDatasets(datasetsIdentities);

    identities.add(identitie);
    powerBiBody.setIdentities(identities);

    return powerBiBody;
  }


  public String generateTokenBI(String datasetId, String reportId) {

    AzureClientAPI azureClientAPI =
      new AzureClientAPI(CLIENT_CREDENCIALS, this.biParameters.get(APP_ID).getParameterValue(),
        this.biParameters.get(SECRET).getParameterValue(), this.biParameters.get(RESOURCE_URL).getParameterValue());

    String azureApiUrl = this.biParameters.get(AZURE_API_URL).getParameterValue().replace("{tenantID}",
      this.biParameters.get(TENANT_ID).getParameterValue());

    String bearerToken = azureClientAPI.generateBearerToken(azureApiUrl);

    if (bearerToken != null) {
      PowerBiClientAPI powerBiClientAPI = new PowerBiClientAPI();

      String token = powerBiClientAPI.generateToken(this.biParameters.get(API_TOKEN_URL).getParameterValue(),
        bearerToken, this.generatePowerBiBody(datasetId, reportId));

      return token;
    }

    return null;
  }

  public List<BiParameters> getBiParameters() {
    return biParameters;
  }


  public List<BiReports> getBiReports() {
    return biReports;
  }


  @Override
  public void prepare() {
    biReports = biReportsManager.findAll();
    biParameters = biParametersManager.findAll();
  }


  public void setBiParameters(List<BiParameters> biParameters) {
    this.biParameters = biParameters;
  }


  public void setBiReports(List<BiReports> biReports) {
    this.biReports = biReports;
  }


}
