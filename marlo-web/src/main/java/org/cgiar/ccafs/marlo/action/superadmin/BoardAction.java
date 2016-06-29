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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class BoardAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;


  private HashMap<Long, String> idoList;


  private SrfIdoManager srfIdoManager;
  private SrfTargetUnitManager srfTargetUnitManager;


  private List<SrfTargetUnit> targetUnitList;
  private List<SrfIdo> srfIdos;

  @Inject
  public BoardAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager, SrfIdoManager srfIdoManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.srfIdoManager = srfIdoManager;
  }


  public HashMap<Long, String> getIdoList() {
    return idoList;
  }


  public List<SrfIdo> getSrfIdos() {
    return srfIdos;
  }


  public List<SrfTargetUnit> getTargetUnitList() {
    return targetUnitList;
  }


  @Override
  public void prepare() throws Exception {


    targetUnitList = new ArrayList<>();
    if (srfTargetUnitManager.findAll() != null) {
      List<SrfTargetUnit> targetUnits =
        srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (SrfTargetUnit srfTargetUnit : targetUnits) {
        targetUnitList.add(srfTargetUnit);
      }
    }


    idoList = new HashMap<>();
    srfIdos = new ArrayList<>();
    for (SrfIdo srfIdo : srfIdoManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
      idoList.put(srfIdo.getId(), srfIdo.getDescription());
      srfIdo.setSubIdos(srfIdo.getSrfSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      srfIdos.add(srfIdo);
    }

  }


  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
  }


  public void setSrfIdos(List<SrfIdo> srfIdos) {
    this.srfIdos = srfIdos;
  }


  public void setTargetUnitList(List<SrfTargetUnit> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }


  @Override
  public void validate() {
    if (save) {

    }
  }

}