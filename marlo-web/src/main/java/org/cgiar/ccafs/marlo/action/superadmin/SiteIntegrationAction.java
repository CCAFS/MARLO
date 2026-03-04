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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpSitesLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpsSiteIntegrationManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class SiteIntegrationAction extends BaseAction {

  private static final Logger LOG = LoggerFactory.getLogger(SiteIntegrationAction.class);
  private static final long serialVersionUID = -793652591843623397L;

  private final GlobalUnitManager crpManager;
  private final LocElementManager locElementManager;
  private final CrpsSiteIntegrationManager crpsSiteIntegrationManager;
  private final CrpSitesLeaderManager crpSitesLeaderManager;
  private final UserManager userManager;

  private GlobalUnit loggedCrp;
  private List<LocElement> countriesList;

  @Inject
  public SiteIntegrationAction(APConfig config, GlobalUnitManager crpManager, LocElementManager locElementManager,
    CrpsSiteIntegrationManager crpsSiteIntegrationManager, CrpSitesLeaderManager crpSitesLeaderManager,
    UserManager userManager) {
    super(config);
    this.crpManager = crpManager;
    this.locElementManager = locElementManager;
    this.crpsSiteIntegrationManager = crpsSiteIntegrationManager;
    this.crpSitesLeaderManager = crpSitesLeaderManager;
    this.userManager = userManager;
  }

  public List<LocElement> getCountriesList() {
    return countriesList;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  private void loadData() {
    if (loggedCrp == null) {
      return;
    }

    if (this.isHttpPost()) {
      if (loggedCrp.getSiteIntegrations() == null) {
        loggedCrp.setSiteIntegrations(new ArrayList<>());
      }
      return;
    }

    if (loggedCrp.getCrpsSitesIntegrations() == null) {
      loggedCrp.setSiteIntegrations(new ArrayList<>());
      return;
    }

    List<CrpsSiteIntegration> integrations = loggedCrp.getCrpsSitesIntegrations().stream()
      .filter(siteIntegration -> siteIntegration != null && siteIntegration.isActive() && loggedCrp.equals(siteIntegration.getCrp()))
      .toList();

    for (CrpsSiteIntegration integration : integrations) {
      List<CrpSitesLeader> leaders = integration.getCrpSitesLeaders().stream()
        .filter(leader -> leader != null && leader.isActive())
        .toList();
      integration.setSiteLeaders(leaders);
    }

    loggedCrp.setSiteIntegrations(integrations);
  }


  @Override
  public void prepare() throws Exception {
    Object currentCrp = this.getSession().get(APConstants.SESSION_CRP);
    if (currentCrp != null) {
      loggedCrp = crpManager.getGlobalUnitById(((GlobalUnit) currentCrp).getId());
    }

    this.loadData();

    countriesList = locElementManager.findAll().stream().filter(locElement -> locElement.getLocElementType() != null
      && locElement.getLocElementType().getId() != null && locElement.getLocElementType().getId() == 2)
      .sorted((countryA, countryB) -> countryA.getName().compareTo(countryB.getName())).toList();

    if (this.isHttpPost() && loggedCrp != null && loggedCrp.getSiteIntegrations() == null) {
      loggedCrp.setSiteIntegrations(new ArrayList<>());
    }
  }

  private List<CrpsSiteIntegration> findPreviousIntegrations() {
    return crpsSiteIntegrationManager.findAll().stream()
      .filter(siteIntegration -> siteIntegration.getCrp() != null && siteIntegration.getCrp().equals(loggedCrp)).toList();
  }

  private void deleteLeader(CrpSitesLeader leader) {
    if (leader != null && leader.getId() != null) {
      crpSitesLeaderManager.deleteCrpSitesLeader(leader.getId());
    }
  }

  private void deleteAllLeaders(CrpsSiteIntegration integration) {
    for (CrpSitesLeader leader : integration.getCrpSitesLeaders()) {
      this.deleteLeader(leader);
    }
  }

  private CrpsSiteIntegration findUpdatedIntegration(CrpsSiteIntegration previousIntegration) {
    return loggedCrp.getSiteIntegrations().stream().filter(previousIntegration::equals).findFirst().orElse(null);
  }

  private void deleteRemovedLeaders(CrpsSiteIntegration previousIntegration, CrpsSiteIntegration updatedIntegration) {
    if (updatedIntegration == null || updatedIntegration.getSiteLeaders() == null) {
      this.deleteAllLeaders(previousIntegration);
      return;
    }

    for (CrpSitesLeader previousLeader : previousIntegration.getCrpSitesLeaders()) {
      if (!updatedIntegration.getSiteLeaders().contains(previousLeader)) {
        this.deleteLeader(previousLeader);
      }
    }
  }

  private void siteIntegrationPreviousData() {
    for (CrpsSiteIntegration previousIntegration : this.findPreviousIntegrations()) {
      if (!loggedCrp.getSiteIntegrations().contains(previousIntegration)) {
        this.deleteAllLeaders(previousIntegration);
        crpsSiteIntegrationManager.deleteCrpsSiteIntegration(previousIntegration.getId());
      } else {
        this.deleteRemovedLeaders(previousIntegration, this.findUpdatedIntegration(previousIntegration));
      }
    }
  }

  private boolean isValidSiteIntegration(CrpsSiteIntegration siteIntegration) {
    return siteIntegration != null && siteIntegration.getLocElement() != null
      && siteIntegration.getLocElement().getIsoAlpha2() != null;
  }

  private CrpsSiteIntegration saveSiteIntegration(CrpsSiteIntegration siteIntegration, LocElement locElement) {
    if (siteIntegration.getId() == null) {
      siteIntegration.setCrp(loggedCrp);
      siteIntegration.setLocElement(locElement);
      return crpsSiteIntegrationManager.saveCrpsSiteIntegration(siteIntegration);
    }
    return siteIntegration;
  }

  private void saveLeaderIfNeeded(CrpsSiteIntegration siteIntegration, CrpSitesLeader siteLeader) {
    if (siteLeader == null || siteLeader.getId() != null || siteLeader.getUser() == null || siteLeader.getUser().getId() == null) {
      return;
    }

    User userSiteLeader = userManager.getUser(siteLeader.getUser().getId());
    CrpsSiteIntegration persistedIntegration = crpsSiteIntegrationManager.getCrpsSiteIntegrationById(siteIntegration.getId());
    siteLeader.setCrpsSiteIntegration(persistedIntegration);
    siteLeader.setUser(userSiteLeader);
    crpSitesLeaderManager.saveCrpSitesLeader(siteLeader);
  }

  private void saveLeaders(CrpsSiteIntegration siteIntegration) {
    if (siteIntegration.getSiteLeaders() == null) {
      return;
    }

    for (CrpSitesLeader siteLeader : siteIntegration.getSiteLeaders()) {
      this.saveLeaderIfNeeded(siteIntegration, siteLeader);
    }
  }

  private void siteIntegrationNewData() {
    for (CrpsSiteIntegration siteIntegration : loggedCrp.getSiteIntegrations()) {
      if (this.isValidSiteIntegration(siteIntegration)) {
        LocElement locElement = locElementManager.getLocElementByISOCode(siteIntegration.getLocElement().getIsoAlpha2());
        if (locElement != null) {
          CrpsSiteIntegration persistedIntegration = this.saveSiteIntegration(siteIntegration, locElement);
          this.saveLeaders(persistedIntegration);
        }
      }
    }
  }


  @Override
  public String save() {
    if (!this.canAccessSuperAdmin()) {
      return NOT_AUTHORIZED;
    }

    if (loggedCrp == null) {
      this.addActionError(this.getText("siteIntegration.notSelected"));
      return INPUT;
    }

    if (loggedCrp.getSiteIntegrations() == null) {
      loggedCrp.setSiteIntegrations(new ArrayList<>());
    }

    this.siteIntegrationPreviousData();
    this.siteIntegrationNewData();
    this.loadData();

    this.addActionMessage(this.getText("saving.saved"));
    return SUCCESS;
  }

  public void setCountriesList(List<LocElement> countriesList) {
    this.countriesList = countriesList;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  private int extractIndex(String key) {
    int startIdx = key.indexOf('[') + 1;
    int endIdx = key.indexOf(']');
    return Integer.parseInt(key.substring(startIdx, endIdx));
  }

  private int extractSecondIndex(String key) {
    int firstClose = key.indexOf(']');
    int secondStart = key.indexOf('[', firstClose) + 1;
    int secondEnd = key.indexOf(']', firstClose + 1);
    return Integer.parseInt(key.substring(secondStart, secondEnd));
  }

  private void ensureIntegrationWithIndex(int index) {
    CrpsSiteIntegration integration = loggedCrp.getSiteIntegrations().get(index);
    if (integration == null) {
      integration = new CrpsSiteIntegration();
      integration.setSiteLeaders(new ArrayList<>());
      loggedCrp.getSiteIntegrations().set(index, integration);
    }
    if (integration.getLocElement() == null) {
      integration.setLocElement(new LocElement());
    }
  }

  private void bindCountryIso(String key, String[] values) {
    int index = extractIndex(key);
    if (index < loggedCrp.getSiteIntegrations().size() && values != null && values.length > 0
      && values[0] != null && !values[0].trim().isEmpty()) {
      this.ensureIntegrationWithIndex(index);
      loggedCrp.getSiteIntegrations().get(index).getLocElement().setIsoAlpha2(values[0].trim());
    }
  }

  private void ensureLeaderWithIndex(CrpsSiteIntegration integration, int leaderIndex) {
    if (integration.getSiteLeaders() == null) {
      integration.setSiteLeaders(new ArrayList<>());
    }
    while (integration.getSiteLeaders().size() <= leaderIndex) {
      CrpSitesLeader leader = new CrpSitesLeader();
      leader.setUser(new User());
      integration.getSiteLeaders().add(leader);
    }
  }

  private void bindLeaderUser(String key, String[] values) {
    int integrationIndex = extractIndex(key);
    int leaderIndex = extractSecondIndex(key);
    if (integrationIndex < loggedCrp.getSiteIntegrations().size() && values != null && values.length > 0
      && values[0] != null && !values[0].trim().isEmpty()) {
      this.ensureIntegrationWithIndex(integrationIndex);
      CrpsSiteIntegration integration = loggedCrp.getSiteIntegrations().get(integrationIndex);
      this.ensureLeaderWithIndex(integration, leaderIndex);
      integration.getSiteLeaders().get(leaderIndex).getUser().setId(Long.parseLong(values[0].trim()));
    }
  }

  private void bindSiteIntegrationParameter(String key, String[] values) {
    if (key.matches("loggedCrp\\.siteIntegrations\\[\\d+\\]\\.locElement\\.isoAlpha2")) {
      this.bindCountryIso(key, values);
    }
    if (key.matches("loggedCrp\\.siteIntegrations\\[\\d+\\]\\.siteLeaders\\[\\d+\\]\\.user\\.id")) {
      this.bindLeaderUser(key, values);
    }
  }

  private void manualBindingSiteIntegrations() {
    if (this.getRequest() == null || loggedCrp == null || loggedCrp.getSiteIntegrations() == null) {
      return;
    }

    this.getRequest().getParameterMap().forEach((key, values) -> {
      try {
        this.bindSiteIntegrationParameter(key, values);
      } catch (Exception exception) {
        LOG.warn("Error binding parameter {}", key, exception);
      }
    });
  }

  private int findMaxSiteIntegrationIndex() {
    int maxIndex = -1;
    for (String parameter : this.getRequest().getParameterMap().keySet()) {
      if (parameter.startsWith("loggedCrp.siteIntegrations[")) {
        try {
          int index = Integer.parseInt(parameter.substring(parameter.indexOf('[') + 1, parameter.indexOf(']')));
          if (index > maxIndex) {
            maxIndex = index;
          }
        } catch (NumberFormatException exception) {
          LOG.debug("Skipping parameter index parse for {}", parameter);
        }
      }
    }
    return maxIndex;
  }

  private void ensureBindingCapacity(int maxIndex) {
    while (loggedCrp.getSiteIntegrations().size() <= maxIndex) {
      CrpsSiteIntegration integration = new CrpsSiteIntegration();
      integration.setLocElement(new LocElement());
      integration.setSiteLeaders(new ArrayList<>());
      loggedCrp.getSiteIntegrations().add(integration);
    }
  }

  private void validateCountryCodes() {
    for (int i = 0; i < loggedCrp.getSiteIntegrations().size(); i++) {
      CrpsSiteIntegration integration = loggedCrp.getSiteIntegrations().get(i);
      if (integration != null && integration.getLocElement() != null
        && integration.getLocElement().getIsoAlpha2() != null
        && !integration.getLocElement().getIsoAlpha2().trim().isEmpty()) {
        LocElement locElement = locElementManager.getLocElementByISOCode(integration.getLocElement().getIsoAlpha2());
        if (locElement == null) {
          this.addFieldError("loggedCrp.siteIntegrations[" + i + "].locElement.isoAlpha2",
            this.getText("siteIntegration.country.invalid"));
        }
      }
    }
  }


  @Override
  public void validate() {
    if (save && this.isHttpPost() && loggedCrp != null) {
      if (loggedCrp.getSiteIntegrations() == null) {
        loggedCrp.setSiteIntegrations(new ArrayList<>());
      }

      int maxIndex = this.findMaxSiteIntegrationIndex();
      this.ensureBindingCapacity(maxIndex);

      this.manualBindingSiteIntegrations();
      this.validateCountryCodes();
    }
    super.validate();
  }

}