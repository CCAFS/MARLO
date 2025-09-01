/*
 * This file is part of Managing Agricultural Research for Learning&*Outcomes Platform(MARLO).
 ** MARLO is free software:you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation,either version 3 of the License,or*at your option)any later version.
 ** MARLO is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See
 * the
 ** GNU General Public License for more details.
 ** You should have received a copy of the GNU General Public License
 ** along with MARLO.If not,see<http:// www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PortfolioManager;
import org.cgiar.ccafs.marlo.data.manager.PortfolioPhaseManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Portfolio;
import org.cgiar.ccafs.marlo.data.model.PortfolioPhase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PortfolioManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private final Logger logger = LoggerFactory.getLogger(PortfolioManagementAction.class);

  private List<Portfolio> portfolios;
  private List<Phase> phases;

  private final PortfolioManager portfolioManager;
  private final PortfolioPhaseManager portfolioPhaseManager;
  private final PhaseManager phaseManager;


  @Inject
  public PortfolioManagementAction(APConfig config, PortfolioManager portfolioManager,
    PortfolioPhaseManager portfolioPhaseManager, PhaseManager phaseManager) {
    super(config);
    this.portfolioManager = portfolioManager;
    this.portfolioPhaseManager = portfolioPhaseManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public void prepare() throws Exception {
    portfolios = new ArrayList<>();
    phases = new ArrayList<>();

    try {

      long globalUnitId = this.getCurrentGlobalUnit().getId();

      portfolios = portfolioManager.getPortfoliosByGlobalUnitId(globalUnitId);
      phases = phaseManager.findAll().stream()
        .filter(phase -> phase.getCrp() != null && phase.getCrp().getId() == globalUnitId)
        .sorted((p1, p2) -> p1.getId().compareTo(p2.getId())).collect(Collectors.toList());

      try {
        if (portfolios == null || portfolios.isEmpty()) {
          return;
        }

        for (Portfolio p : portfolios) {
          if (p == null) {
            continue;
          }

          final Long id = p.getId();
          if (id == null) {
            p.setPortfolioPhases(Collections.emptyList());
            continue;

          }

          try {
            final List<PortfolioPhase> phases = portfolioPhaseManager.getPortfolioPhasesByPortfolioID(id);
            p.setPortfolioPhases((phases == null || phases.isEmpty()) ? Collections.emptyList() : phases);
          } catch (Exception ex) {
            logger.error("Error fetching phases for portfolioId={}", id, ex);
            p.setPortfolioPhases(Collections.emptyList());
          }

          if (p.getSelectedPhases() == null || p.getSelectedPhases().isEmpty()) {
            p.setSelectedPhases(extractPhaseIds(p.getPortfolioPhases()));
          }
        }

      } catch (Exception e) {
        logger.error("Error fetching portfolio phases: {}", e.getMessage(), e);
      }


    } catch (

    Exception e) {
    }

    if (this.isHttpPost()) {
      if (portfolios != null) {
        portfolios.clear();
      }
      if (phases != null) {
        phases.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      savePortfolios();

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (this.getInvalidFields() != null && !this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          // this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        this.addActionMessage("message:" + this.getText("saving.saved"));
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }
  }

  /**
   * This method saves the portfolios and their associated phases.
   */
  public void savePortfolios() {
    if (portfolios != null) {

      List<Long> inputIds =
        portfolios.stream().map(Portfolio::getId).filter(Objects::nonNull).collect(Collectors.toList());

      List<Portfolio> allExisting = portfolioManager.getPortfoliosByGlobalUnitId(this.getCurrentGlobalUnit().getId());

      if (allExisting != null && !allExisting.isEmpty()) {
        allExisting.stream().filter(existing -> existing.getId() != null && !inputIds.contains(existing.getId()))
          .forEach(portfolioToDelete -> {
            try {
              portfolioManager.deletePortfolio(portfolioToDelete.getId());
            } catch (Exception e) {
              logger.error("Error deleting portfolio with ID: {}", portfolioToDelete.getId(), e);
            }
          });
      }

      if (portfolios != null && !portfolios.isEmpty()) {
        List<Long> newIds = new ArrayList<>();

        for (Portfolio portfolio : portfolios) {
          try {

            Portfolio portfolioToSave =
              (portfolio.getId() != null) ? portfolioManager.getPortfolioById(portfolio.getId()) : new Portfolio();

            boolean isNew = portfolio.getId() == null;

            portfolioToSave.setName(portfolio.getName());
            portfolioToSave.setStartDate(portfolio.getStartDate());
            portfolioToSave.setEndDate(portfolio.getEndDate());
            portfolioToSave.setGlobalUnit(
              portfolio.getGlobalUnit() != null ? portfolio.getGlobalUnit() : this.getCurrentGlobalUnit());

            portfolioToSave = portfolioManager.savePortfolio(portfolioToSave);

            savePortFolioPhases(portfolio, portfolioToSave);

            if (isNew && portfolioToSave.getId() != null) {
              newIds.add(portfolioToSave.getId());
            }
          } catch (Exception e) {
            logger.error("Error saving PortfolioPhase: {}", e.getMessage(), e);
          }
        }

        if (!newIds.isEmpty()) {
          this.getRequest().getSession().setAttribute("recentlyCreatedFRP", newIds);
        }
      }
    }
  }

  /**
   * This method saves the phases associated with a portfolio.
   *
   * @param portfolio The portfolio containing the selected phases to be saved.
   * @param portfolioToSave The portfolio entity to which the phases will be associated.
   */
  public void savePortFolioPhases(Portfolio portfolio, Portfolio portfolioToSave) {
    List<Long> selected = (portfolio.getSelectedPhases() == null) ? Collections.emptyList()
      : portfolio.getSelectedPhases().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());

    List<PortfolioPhase> existing = portfolioPhaseManager.getPortfolioPhasesByPortfolioID(portfolioToSave.getId());
    if (existing == null) {
      existing = Collections.emptyList();
    }

    final java.util.Map<Long, PortfolioPhase> existingByPhaseId =
      existing.stream().filter(Objects::nonNull).filter(pp -> pp.getPhase() != null && pp.getPhase().getId() != null)
        .collect(Collectors.toMap(pp -> pp.getPhase().getId(), pp -> pp, (a, b) -> a));

    List<Long> keptPhaseIds = new ArrayList<>();
    for (Long phaseId : selected) {
      Phase phase = phaseManager.getPhaseById(phaseId);
      if (phase == null) {
        continue;
      }

      PortfolioPhase toSave = existingByPhaseId.get(phaseId);
      if (toSave == null) {
        toSave = new PortfolioPhase();
      }
      toSave.setPortfolio(portfolioToSave);
      toSave.setPhase(phase);
      portfolioPhaseManager.savePortfolioPhase(toSave);
      keptPhaseIds.add(phaseId);
    }


    try {
      for (PortfolioPhase pp : existing) {
        Long phaseId = (pp != null && pp.getPhase() != null) ? pp.getPhase().getId() : null;
        if (phaseId != null && !keptPhaseIds.contains(phaseId)) {
          if (pp.getId() != null) {
            portfolioPhaseManager.deletePortfolioPhase(pp.getId());
          }
        }
      }
    } catch (Exception ex) {
      logger.warn("Could not delete removed PortfolioPhases for portfolioId={}", portfolioToSave.getId(), ex);
    }
  }

  private List<Long> extractPhaseIds(List<PortfolioPhase> list) {
    if (list == null || list.isEmpty()) {
      return Collections.emptyList();
    }
    List<Long> ids = new ArrayList<>();
    for (PortfolioPhase pp : list) {
      if (pp != null && pp.getPhase() != null && pp.getPhase().getId() != null) {
        ids.add(pp.getPhase().getId());
      }
    }
    return ids;
  }

  public List<Portfolio> getPortfolios() {
    return portfolios;
  }

  public void setPortfolios(List<Portfolio> portfolios) {
    this.portfolios = portfolios;
  }

  @Override
  public List<Phase> getPhases() {
    return phases;
  }

  public void setPhases(List<Phase> phases) {
    this.phases = phases;
  }

  @Override
  public void validate() {
    if (save) {
    }
  }
}