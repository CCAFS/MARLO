
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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.GeneralStatusDAO;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusTableManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GeneralStatusTable;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class GeneralStatusManagerImpl implements GeneralStatusManager {


  private GeneralStatusDAO generalStatusDAO;
  private GeneralStatusTableManager generalStatusTableManager;
  // Managers


  @Inject
  public GeneralStatusManagerImpl(GeneralStatusDAO generalStatusDAO,
    GeneralStatusTableManager generalStatusTableManager) {
    this.generalStatusDAO = generalStatusDAO;
    this.generalStatusTableManager = generalStatusTableManager;


  }

  @Override
  public void deleteGeneralStatus(long generalStatusId) {

    this.generalStatusDAO.deleteGeneralStatus(generalStatusId);
  }

  @Override
  public boolean existGeneralStatus(long generalStatusID) {

    return this.generalStatusDAO.existGeneralStatus(generalStatusID);
  }

  @Override
  public List<GeneralStatus> findAllDeliverables(long year) {
    List<GeneralStatus> generalStati = this.generalStatusDAO.findAll();

    generalStati.removeIf(
      gs -> gs == null || gs.getId() == null || DeliverableStatusEnum.getValue(gs.getId().intValue()) == null);

    /*
     * if (year > 2020) {
     * generalStati.removeIf(gs -> gs == null || gs.getId() == null
     * || String.valueOf(gs.getId()).equals(DeliverableStatusEnum.EXTENDED.getStatusId()));
     * generalStati.replaceAll(this::onGoingToReadyToBeReportedOn);
     * }
     */

    if (year < 2021) {
      generalStati.removeIf(gs -> gs == null || gs.getId() == null
        || String.valueOf(gs.getId()).equals(DeliverableStatusEnum.PARTIALLY_COMPLETE.getStatusId()));
    }

    return generalStati;
  }

  @Override
  public List<GeneralStatus> findAllGeneralUse() {
    List<GeneralStatus> generalStati = this.generalStatusDAO.findAll();
    generalStati.removeIf(gs -> gs == null || gs.getId() == null
      || String.valueOf(gs.getId()).equals(DeliverableStatusEnum.PARTIALLY_COMPLETE.getStatusId()));
    return generalStati;
  }

  @Override
  public List<GeneralStatus> findByTable(String tableName) {
    List<GeneralStatusTable> statusTableList = this.generalStatusTableManager.findAll();
    List<GeneralStatus> statusList = statusTableList.stream().filter(c -> c.getTableName().equalsIgnoreCase(tableName))
      .map(result -> result.getGeneralStatus()).filter(res -> res != null && res.getId() != null)
      .sorted((s1, s2) -> s1.getId().compareTo(s2.getId())).collect(Collectors.toList());
    return statusList;
  }

  @Override
  public GeneralStatus getGeneralStatusById(long generalStatusID) {

    return this.generalStatusDAO.find(generalStatusID);
  }

  private GeneralStatus onGoingToReadyToBeReportedOn(GeneralStatus onGoing) {
    if (String.valueOf(onGoing.getId()).equals(DeliverableStatusEnum.ON_GOING.getStatusId())) {
      onGoing.setName("Ready to be reported on");
    }

    return onGoing;
  }

  @Override
  public GeneralStatus saveGeneralStatus(GeneralStatus generalStatus) {

    return this.generalStatusDAO.save(generalStatus);
  }
}