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

import org.cgiar.ccafs.marlo.data.dao.BiParametersDAO;
import org.cgiar.ccafs.marlo.data.model.BiParameters;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
@Named
public class BiParametersMySQLDAO extends AbstractMarloDAO<BiParameters, Long> implements BiParametersDAO {


  @Inject
  public BiParametersMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteBiParameters(long biParametersId) {
    BiParameters biParameters = this.find(biParametersId);
    this.delete(biParameters);
  }

  @Override
  public boolean existBiParameters(long biParametersID) {
    BiParameters biParameters = this.find(biParametersID);
    if (biParameters == null) {
      return false;
    }
    return true;

  }

  @Override
  public BiParameters find(long id) {
    return super.find(BiParameters.class, id);

  }

  @Override
  public List<BiParameters> findAll() {
    String query = "from " + BiParameters.class.getName();
    List<BiParameters> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public BiParameters save(BiParameters biParameters) {
    if (biParameters.getId() == null) {
      super.saveEntity(biParameters);
    } else {
      biParameters = super.update(biParameters);
    }


    return biParameters;
  }


}