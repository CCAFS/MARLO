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


import org.cgiar.ccafs.marlo.data.dao.ITargetGroupDAO;
import org.cgiar.ccafs.marlo.data.manager.ITargetGroupService;
import org.cgiar.ccafs.marlo.data.model.TargetGroup;

import java.util.List;

import javax.inject.Inject;


/**
 * @author Christian Garcia
 */
public class TargetGroupService implements ITargetGroupService {


  private ITargetGroupDAO targetGroupDAO;

  // Managers


  @Inject
  public TargetGroupService(ITargetGroupDAO targetGroupDAO) {
    this.targetGroupDAO = targetGroupDAO;


  }

  @Override
  public void deleteTargetGroup(long targetGroupId) {

    targetGroupDAO.deleteTargetGroup(targetGroupId);
  }

  @Override
  public boolean existTargetGroup(long targetGroupID) {

    return targetGroupDAO.existTargetGroup(targetGroupID);
  }

  @Override
  public List<TargetGroup> findAll() {

    return targetGroupDAO.findAll();

  }

  @Override
  public TargetGroup getTargetGroupById(long targetGroupID) {

    return targetGroupDAO.find(targetGroupID);
  }

  @Override
  public List<TargetGroup> getTargetGroupsByUserId(Long userId) {
    return targetGroupDAO.getTargetGroupsByUserId(userId);
  }

  @Override
  public TargetGroup saveTargetGroup(TargetGroup targetGroup) {

    return targetGroupDAO.save(targetGroup);
  }

  @Override
  public TargetGroup saveTargetGroup(TargetGroup targetGroup, String actionName, List<String> relationsName) {
    return targetGroupDAO.save(targetGroup, actionName, relationsName);
  }


}
