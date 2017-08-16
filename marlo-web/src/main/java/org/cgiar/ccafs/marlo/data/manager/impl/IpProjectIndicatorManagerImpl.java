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


import org.cgiar.ccafs.marlo.data.dao.IpProjectIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.IpProjectIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpProjectIndicatorManagerImpl implements IpProjectIndicatorManager {


  private IpProjectIndicatorDAO ipProjectIndicatorDAO;
  // Managers


  @Inject
  public IpProjectIndicatorManagerImpl(IpProjectIndicatorDAO ipProjectIndicatorDAO) {
    this.ipProjectIndicatorDAO = ipProjectIndicatorDAO;


  }

  @Override
  public boolean deleteIpProjectIndicator(long ipProjectIndicatorId) {

    return ipProjectIndicatorDAO.deleteIpProjectIndicator(ipProjectIndicatorId);
  }

  @Override
  public boolean existIpProjectIndicator(long ipProjectIndicatorID) {

    return ipProjectIndicatorDAO.existIpProjectIndicator(ipProjectIndicatorID);
  }

  @Override
  public List<IpProjectIndicator> findAll() {

    return ipProjectIndicatorDAO.findAll();

  }

  @Override
  public IpProjectIndicator getIpProjectIndicatorById(long ipProjectIndicatorID) {

    return ipProjectIndicatorDAO.find(ipProjectIndicatorID);
  }

  @Override
  public List<IpProjectIndicator> getProjectIndicators(int year, long indicator, Long program, long midOutcome) {
    return ipProjectIndicatorDAO.getProjectIndicators(year, indicator, program, midOutcome);
  }

  @Override
  public long saveIpProjectIndicator(IpProjectIndicator ipProjectIndicator) {

    return ipProjectIndicatorDAO.save(ipProjectIndicator);
  }


}
