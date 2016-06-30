/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.action.crp.admin;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpParameterManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramCountryManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpSitesLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpsSiteIntegrationManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpParameter;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;
import org.cgiar.ccafs.marlo.data.model.CrpsSiteIntegration;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * This action is part of the CRP admin backend.
 * 
 * @author Christian Garcia
 */
public class CrpProgamRegionsAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;

  // Managers
  private RoleManager roleManager;
  private UserRoleManager userRoleManager;
  private CrpProgramManager crpProgramManager;
  private CrpManager crpManager;
  private CrpParameterManager crpParameterManager;
  // Variables
  private Crp loggedCrp;
  private Role rolePmu;
  private long pmuRol;
  private List<LocElement> countriesList;
  private List<CrpProgram> regionsPrograms;
  private List<CrpParameter> parameters;
  private LocElementManager locElementManger;
  private CrpProgramLeaderManager crpProgramLeaderManager;
  private CrpProgramCountryManager crpProgramCountryManager;
  private CrpSitesLeaderManager crpSitesLeaderManager;
  private CrpsSiteIntegrationManager crpsSiteIntegrationManager;
  private UserManager userManager;
  private Role rplRole;

  private Long slRoleid;
  private Role slRole;


  @Inject
  public CrpProgamRegionsAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpProgramManager crpProgramManager, CrpManager crpManager, CrpParameterManager crpParameterManager,
    CrpProgramLeaderManager crpProgramLeaderManager, UserManager userManager, LocElementManager locElementManger,
    CrpProgramCountryManager crpProgramCountryManager, CrpSitesLeaderManager crpSitesLeaderManager,
    CrpsSiteIntegrationManager crpsSiteIntegrationManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.crpProgramManager = crpProgramManager;
    this.crpParameterManager = crpParameterManager;
    this.userManager = userManager;
    this.locElementManger = locElementManger;
    this.crpProgramLeaderManager = crpProgramLeaderManager;
    this.crpProgramCountryManager = crpProgramCountryManager;
    this.crpSitesLeaderManager = crpSitesLeaderManager;
    this.crpsSiteIntegrationManager = crpsSiteIntegrationManager;
  }

  private void deleteSiteIntegration(CrpProgramCountry crpProgramCountry) {
    for (CrpsSiteIntegration siteIntegration : crpProgramCountry.getLocElement().getCrpsSitesIntegrations().stream()
      .filter(si -> si.getCrp().equals(loggedCrp) && si.isActive()).collect(Collectors.toList())) {


      for (CrpSitesLeader crpSitesLeader : siteIntegration.getCrpSitesLeaders()) {

        crpSitesLeaderManager.deleteCrpSitesLeader(crpSitesLeader.getId());
        User user = userManager.getUser(crpSitesLeader.getUser().getId());

        List<CrpSitesLeader> existsUserLeader =
          user.getCrpSitesLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());

        if (existsUserLeader == null || existsUserLeader.isEmpty()) {

          if (crpSitesLeader.getCrpsSiteIntegration().equals(siteIntegration)) {
            List<UserRole> slUserRoles =
              user.getUserRoles().stream().filter(ur -> ur.getRole().equals(slRole)).collect(Collectors.toList());
            if (slUserRoles != null) {
              for (UserRole userRole : slUserRoles) {
                userRoleManager.deleteUserRole(userRole.getId());
              }
            }
          }
        }
        LocElement element = siteIntegration.getLocElement();
        element.setIsSiteIntegration(false);
        locElementManger.saveLocElement(element);
        crpsSiteIntegrationManager.deleteCrpsSiteIntegration(siteIntegration.getId());
      }
    }
  }

  private void deleteSiteIntegrationLeader(CrpProgramCountry crpProgramCountry, User user) {

    for (CrpSitesLeader sitesLeader : user.getCrpSitesLeaders().stream().filter(sl -> sl.isActive())
      .collect(Collectors.toList())) {
      Long locElementSL = sitesLeader.getCrpsSiteIntegration().getLocElement().getId();
      Long locElementCP = crpProgramCountry.getLocElement().getId();
      if (locElementSL == locElementCP) {
        crpSitesLeaderManager.deleteCrpSitesLeader(sitesLeader.getId());

        List<CrpSitesLeader> existsSiteLeader =
          user.getCrpSitesLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());

        if (existsSiteLeader == null || existsSiteLeader.isEmpty()) {
          List<UserRole> slUserRoles =
            user.getUserRoles().stream().filter(ur -> ur.getRole().equals(slRole)).collect(Collectors.toList());
          if (slUserRoles != null) {
            for (UserRole userRole : slUserRoles) {
              userRoleManager.deleteUserRole(userRole.getId());
            }
          }
        }
      }
    }
  }


  public List<LocElement> getCountriesList() {
    return countriesList;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public long getPmuRol() {
    return pmuRol;
  }


  public List<CrpProgram> getRegionsPrograms() {
    return regionsPrograms;
  }

  public Role getRolePmu() {
    return rolePmu;
  }

  public Role getRplRole() {
    return rplRole;
  }


  @Override
  public void prepare() throws Exception {

    // Get the Users list that have the pmu role in this crp.
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    pmuRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    rolePmu = roleManager.getRoleById(pmuRol);
    loggedCrp.setProgramManagmenTeam(new ArrayList<UserRole>(rolePmu.getUserRoles()));
    String params[] = {loggedCrp.getAcronym()};
    if (this.getSession().containsKey(APConstants.CRP_RPL_ROLE)) {
      rplRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_RPL_ROLE)));
    }

    if (this.getSession().containsKey(APConstants.CRP_SL_ROLE)) {
      slRoleid = Long.parseLong((String) this.getSession().get(APConstants.CRP_SL_ROLE));
      slRole = roleManager.getRoleById(slRoleid);
    }

    List<LocElement> locs =
      locElementManger.findAll().stream().filter(c -> c.getLocElementType().getId() == 2).collect(Collectors.toList());
    Collections.sort(locs, (l1, l2) -> l1.getName().compareTo(l2.getName()));
    countriesList = locs;
    // Get the Flagship list of this crp
    regionsPrograms = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());

    for (CrpProgram crpProgram : regionsPrograms) {
      crpProgram
        .setLeaders(crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      List<String> countriesSelected = new ArrayList<>();
      for (CrpProgramCountry crpProgramCountry : crpProgram.getCrpProgramCountries().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        countriesSelected.add(crpProgramCountry.getLocElement().getIsoAlpha2());
      }
      crpProgram.setSelectedCountries(countriesSelected);

    }

    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      loggedCrp.getProgramManagmenTeam().clear();

      regionsPrograms.clear();
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {

      List<CrpProgram> rgProgramsRewiev =
        crpProgramManager.findCrpProgramsByType(loggedCrp.getId(), ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
      // Removing crp flagship program type
      if (rgProgramsRewiev != null) {
        for (CrpProgram crpProgram : rgProgramsRewiev) {
          if (!regionsPrograms.contains(crpProgram)) {
            CrpProgram crpProgramBD = crpProgramManager.getCrpProgramById(crpProgram.getId());

            if (crpProgramBD.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList())
              .isEmpty()
              && crpProgramBD.getCrpProgramCountries().stream().filter(c -> c.isActive()).collect(Collectors.toList())
                .isEmpty()) {
              for (CrpProgramCountry crpProgramCountry : crpProgram.getCrpProgramCountries().stream()
                .filter(pc -> pc.isActive()).collect(Collectors.toList())) {
                this.deleteSiteIntegration(crpProgramCountry);
              }
              crpProgramManager.deleteCrpProgram(crpProgram.getId());
            }
          }
        }
      }
      // Add crp flagship program type
      for (CrpProgram crpProgram : regionsPrograms) {
        if (crpProgram.getId() == null) {
          crpProgram.setCrp(loggedCrp);
          crpProgram.setActive(true);
          crpProgram.setCreatedBy(this.getCurrentUser());
          crpProgram.setModifiedBy(this.getCurrentUser());
          crpProgram.setModificationJustification("");
          crpProgram.setActiveSince(new Date());
          crpProgram.setProgramType(ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
          crpProgramManager.saveCrpProgram(crpProgram);
        } else {
          CrpProgram crpProgramDb = crpProgramManager.getCrpProgramById(crpProgram.getId());
          crpProgram.setCrp(loggedCrp);
          crpProgram.setActive(true);
          crpProgram.setCreatedBy(crpProgramDb.getCreatedBy());
          crpProgram.setModifiedBy(this.getCurrentUser());
          crpProgram.setModificationJustification("");
          crpProgram.setActiveSince(crpProgramDb.getActiveSince());
          crpProgram.setProgramType(ProgramType.REGIONAL_PROGRAM_TYPE.getValue());
          crpProgramManager.saveCrpProgram(crpProgram);
        }
      }

      for (CrpProgram crpProgram : regionsPrograms) {
        CrpProgram crpProgramPrev = crpProgramManager.getCrpProgramById(crpProgram.getId());
        for (CrpProgramLeader leaderPreview : crpProgramPrev.getCrpProgramLeaders().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {

          if (crpProgram.getLeaders() == null) {
            crpProgram.setLeaders(new ArrayList<>());
          }
          if (!crpProgram.getLeaders().contains(leaderPreview)) {
            crpProgramLeaderManager.deleteCrpProgramLeader(leaderPreview.getId());
            User user = userManager.getUser(leaderPreview.getUser().getId());

            for (CrpProgramCountry crpProgramCountry : crpProgramPrev.getCrpProgramCountries().stream()
              .filter(pc -> pc.isActive()).collect(Collectors.toList())) {
              this.deleteSiteIntegrationLeader(crpProgramCountry, user);
            }

            List<CrpProgramLeader> existsUserLeader = user.getCrpProgramLeaders().stream()
              .filter(u -> u.isActive() && u.getCrpProgram().getProgramType() == crpProgramPrev.getProgramType())
              .collect(Collectors.toList());

            if (existsUserLeader == null || existsUserLeader.isEmpty()) {

              if (crpProgramPrev.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
                List<UserRole> fplUserRoles =
                  user.getUserRoles().stream().filter(ur -> ur.getRole().equals(rplRole)).collect(Collectors.toList());
                if (fplUserRoles != null) {
                  for (UserRole userRole : fplUserRoles) {
                    userRoleManager.deleteUserRole(userRole.getId());
                  }
                }
              }
            }
          }
        }

        if (crpProgram.getLeaders() != null) {
          for (CrpProgramLeader crpProgramLeader : crpProgram.getLeaders()) {
            if (crpProgramLeader.getId() == null) {
              crpProgramLeader.setActive(true);
              crpProgramLeader.setCrpProgram(crpProgram);
              crpProgramLeader.setCreatedBy(this.getCurrentUser());
              crpProgramLeader.setModifiedBy(this.getCurrentUser());
              crpProgramLeader.setModificationJustification("");
              crpProgramLeader.setActiveSince(new Date());
              CrpProgram crpProgramPrevLeaders = crpProgramManager.getCrpProgramById(crpProgram.getId());
              if (crpProgramPrevLeaders.getCrpProgramLeaders().stream()
                .filter(c -> c.isActive() && c.getCrpProgram().equals(crpProgramLeader.getCrpProgram())
                  && c.getUser().equals(crpProgramLeader.getUser()))
                .collect(Collectors.toList()).isEmpty()) {
                crpProgramLeaderManager.saveCrpProgramLeader(crpProgramLeader);
                for (CrpProgramCountry crpProgramCountry : crpProgramPrevLeaders.getCrpProgramCountries()) {
                  this.saveSiteLeader(crpProgramCountry, crpProgramLeader.getUser());
                }

              }

              User user = userManager.getUser(crpProgramLeader.getUser().getId());
              UserRole userRole = new UserRole();
              userRole.setUser(user);

              if (crpProgramPrevLeaders.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
                userRole.setRole(rplRole);
              }

              if (!user.getUserRoles().contains(userRole)) {
                userRoleManager.saveUserRole(userRole);
              }
            }
          }
        }

        if (crpProgram.getSelectedCountries() != null) {
          CrpProgram crpProgramPrevLeaders = crpProgramManager.getCrpProgramById(crpProgram.getId());

          List<CrpProgramCountry> crpProgramCountriesPreview = crpProgramPrevLeaders.getCrpProgramCountries().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList());
          for (CrpProgramCountry crpProgramCountry : crpProgramCountriesPreview) {
            String alpha2 = crpProgramCountry.getLocElement().getIsoAlpha2();
            if (!crpProgram.getSelectedCountries().contains(alpha2)) {
              this.deleteSiteIntegration(crpProgramCountry);
              crpProgramCountryManager.deleteCrpProgramCountry(crpProgramCountry.getId());
            }
          }

          for (String isoCode : crpProgram.getSelectedCountries()) {
            LocElement locElement = locElementManger.getLocElementByISOCode(isoCode);

            if (crpProgramPrevLeaders.getCrpProgramCountries().stream()
              .filter(c -> c.isActive() && c.getLocElement().getId().equals(locElement.getId()))
              .collect(Collectors.toList()).isEmpty()) {
              CrpProgramCountry crpProgramCountry = new CrpProgramCountry();
              crpProgramCountry.setActive(true);
              crpProgramCountry.setLocElement(locElement);
              crpProgramCountry.setCrpProgram(crpProgram);
              crpProgramCountry.setCreatedBy(this.getCurrentUser());
              crpProgramCountry.setModifiedBy(this.getCurrentUser());
              crpProgramCountry.setModificationJustification("");
              crpProgramCountry.setActiveSince(new Date());
              crpProgramCountryManager.saveCrpProgramCountry(crpProgramCountry);
              this.saveSiteIntegration(locElement, crpProgramPrevLeaders);
            }

          }
        }
      }

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  private void saveSiteIntegration(LocElement locElement, CrpProgram crpProgram) {
    CrpsSiteIntegration crpsSiteIntegration = new CrpsSiteIntegration();
    crpsSiteIntegration.setCrp(loggedCrp);
    crpsSiteIntegration.setLocElement(locElement);
    crpsSiteIntegration.setActive(true);
    crpsSiteIntegration.setModifiedBy(this.getCurrentUser());
    crpsSiteIntegration.setCreatedBy(this.getCurrentUser());
    crpsSiteIntegration.setModificationJustification("");
    crpsSiteIntegration.setActiveSince(new Date());
    crpsSiteIntegration.setRegional(true);

    Long newSiteIntegrationID = crpsSiteIntegrationManager.saveCrpsSiteIntegration(crpsSiteIntegration);

    locElement.setIsSiteIntegration(true);
    locElementManger.saveLocElement(locElement);

    if (crpProgram.getCrpProgramLeaders() != null) {
      for (CrpProgramLeader programLeader : crpProgram.getCrpProgramLeaders()) {
        User userSiteLeader = userManager.getUser(programLeader.getUser().getId());
        CrpsSiteIntegration crpSiteIntegration =
          crpsSiteIntegrationManager.getCrpsSiteIntegrationById(newSiteIntegrationID);
        CrpSitesLeader sitesLeader = new CrpSitesLeader();
        sitesLeader.setCrpsSiteIntegration(crpSiteIntegration);
        sitesLeader.setUser(userSiteLeader);
        sitesLeader.setActive(true);
        sitesLeader.setModifiedBy(this.getCurrentUser());
        sitesLeader.setCreatedBy(this.getCurrentUser());
        sitesLeader.setModificationJustification("");
        sitesLeader.setActiveSince(new Date());
        sitesLeader.setRegional(true);
        crpSitesLeaderManager.saveCrpSitesLeader(sitesLeader);

        UserRole userRole = new UserRole(slRole, userSiteLeader);
        if (!userSiteLeader.getUserRoles().contains(userRole)) {
          userRoleManager.saveUserRole(userRole);
        }
      }
    }
  }

  private void saveSiteLeader(CrpProgramCountry crpProgramCountry, User user) {
    for (CrpsSiteIntegration siteIntegration : crpProgramCountry.getLocElement().getCrpsSitesIntegrations()) {
      CrpSitesLeader sitesLeader = new CrpSitesLeader();
      sitesLeader.setCrpsSiteIntegration(siteIntegration);
      sitesLeader.setUser(user);
      sitesLeader.setActive(true);
      sitesLeader.setModifiedBy(this.getCurrentUser());
      sitesLeader.setCreatedBy(this.getCurrentUser());
      sitesLeader.setModificationJustification("");
      sitesLeader.setActiveSince(new Date());
      sitesLeader.setRegional(true);
      crpSitesLeaderManager.saveCrpSitesLeader(sitesLeader);

      UserRole userRole = new UserRole(slRole, user);
      if (!user.getUserRoles().contains(userRole)) {
        userRoleManager.saveUserRole(userRole);
      }
    }
  }

  public void setCountriesList(List<LocElement> countriesList) {
    this.countriesList = countriesList;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPmuRol(long pmuRol) {
    this.pmuRol = pmuRol;
  }

  public void setRegionsPrograms(List<CrpProgram> regionsPrograms) {
    this.regionsPrograms = regionsPrograms;
  }

  public void setRolePmu(Role rolePmu) {
    this.rolePmu = rolePmu;
  }

  public void setRplRole(Role fplRole) {
    this.rplRole = fplRole;
  }
}
