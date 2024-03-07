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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.TipParametersDAO;
import org.cgiar.ccafs.marlo.data.model.TipParameters;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class TipParametersMySQLDAO extends AbstractMarloDAO<TipParameters, Long> implements TipParametersDAO {

  @Inject
  public TipParametersMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteTipParameters(long tipParametersId) {
    TipParameters tipParameters = this.find(tipParametersId);
    this.delete(tipParameters);
  }

  @Override
  public boolean existTipParameters(long tipParametersID) {
    TipParameters tipParameters = this.find(tipParametersID);
    if (tipParameters == null) {
      return false;
    }
    return true;
  }

  @Override
  public TipParameters find(long id) {
    return super.find(TipParameters.class, id);

  }

  @Override
  public List<TipParameters> findAll() {
    String query = "from " + TipParameters.class.getName();
    List<TipParameters> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public TipParameters save(TipParameters tipParameters) {
    if (tipParameters.getId() == null) {
      super.saveEntity(tipParameters);
    } else {
      tipParameters = super.update(tipParameters);
    }
    return tipParameters;
  }
}