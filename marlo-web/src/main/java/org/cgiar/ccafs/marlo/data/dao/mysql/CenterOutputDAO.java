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

import org.cgiar.ccafs.marlo.data.dao.ICenterOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterOutputDAO extends AbstractMarloDAO<CenterOutput, Long> implements ICenterOutputDAO {


  @Inject
  public CenterOutputDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteResearchOutput(long researchOutputId) {
    CenterOutput researchOutput = this.find(researchOutputId);
    researchOutput.setActive(false);
    this.save(researchOutput);
  }

  @Override
  public boolean existResearchOutput(long researchOutputID) {
    CenterOutput researchOutput = this.find(researchOutputID);
    if (researchOutput == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterOutput find(long id) {
    return super.find(CenterOutput.class, id);

  }

  @Override
  public List<CenterOutput> findAll() {
    String query = "from " + CenterOutput.class.getName();
    List<CenterOutput> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterOutput> getResearchOutputsByUserId(long userId) {
    String query = "from " + CenterOutput.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterOutput save(CenterOutput researchOutput) {
    if (researchOutput.getId() == null) {
      super.saveEntity(researchOutput);
    } else {
      researchOutput = super.update(researchOutput);
    }
    return researchOutput;
  }

  @Override
  public CenterOutput save(CenterOutput output, String actionName, List<String> relationsName) {
    if (output.getId() == null) {
      super.saveEntity(output, actionName, relationsName);
    } else {
      output = super.update(output, actionName, relationsName);
    }
    return output;
  }


}