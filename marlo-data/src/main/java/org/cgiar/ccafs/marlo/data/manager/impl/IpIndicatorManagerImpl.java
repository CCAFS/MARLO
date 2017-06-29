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


import org.cgiar.ccafs.marlo.data.dao.IpIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.IpIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpIndicatorManagerImpl implements IpIndicatorManager {


  private IpIndicatorDAO ipIndicatorDAO;
  // Managers


  @Inject
  public IpIndicatorManagerImpl(IpIndicatorDAO ipIndicatorDAO) {
    this.ipIndicatorDAO = ipIndicatorDAO;


  }

  @Override
  public boolean deleteIpIndicator(long ipIndicatorId) {

    return ipIndicatorDAO.deleteIpIndicator(ipIndicatorId);
  }

  @Override
  public boolean existIpIndicator(long ipIndicatorID) {

    return ipIndicatorDAO.existIpIndicator(ipIndicatorID);
  }

  @Override
  public List<IpIndicator> findAll() {

    return ipIndicatorDAO.findAll();

  }

  @Override
  public List<IpIndicator> findOtherContributions(long projectID) {

    return ipIndicatorDAO.findOtherContributions(projectID);
  }

  @Override
  public List<IpIndicator> getIndicatorsByElementID(long elementID) {
    return ipIndicatorDAO.getIndicatorsByElementID(elementID);
  }

  @Override
  public List<IpIndicator> getIndicatorsFlagShips() {

    return ipIndicatorDAO.getIndicatorsFlagShips();
  }

  @Override
  public IpIndicator getIpIndicatorById(long ipIndicatorID) {

    return ipIndicatorDAO.find(ipIndicatorID);
  }

  @Override
  public List<IpProjectIndicator> getProjectIndicators(int year, long indicator, long program, long midOutcome) {
    return ipIndicatorDAO.getProjectIndicators(year, indicator, program, midOutcome);
  }

  @Override
  public long saveIpIndicator(IpIndicator ipIndicator) {

    return ipIndicatorDAO.save(ipIndicator);
  }


}
