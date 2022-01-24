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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.SdgDAO;
import org.cgiar.ccafs.marlo.data.manager.SdgManager;
import org.cgiar.ccafs.marlo.data.model.Sdg;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SdgManagerImpl implements SdgManager {

  private SdgDAO sdgDAO;

  @Inject
  public SdgManagerImpl(SdgDAO sdgDAO) {
    super();
    this.sdgDAO = sdgDAO;
  }

  @Override
  public void deleteSdg(Long sdgId) {
    this.sdgDAO.deleteSdg(sdgId.longValue());
  }

  @Override
  public boolean existSdg(Long sdgID) {
    return this.sdgDAO.existSdg(sdgID.longValue());
  }

  @Override
  public List<Sdg> getAll() {
    return this.sdgDAO.getAll();
  }

  @Override
  public Sdg getSdgByFinancialCode(String financialCode) {
    return this.sdgDAO.getSdgByFinancialCode(financialCode);
  }

  @Override
  public Sdg getSdgById(Long id) {
    return this.sdgDAO.getSdgById(id.longValue());
  }

  @Override
  public Sdg getSdgBySmoCode(String smoCode) {
    return this.sdgDAO.getSdgBySmoCode(smoCode);
  }

  @Override
  public Sdg save(Sdg sdg) {
    return this.sdgDAO.save(sdg);
  }

}
