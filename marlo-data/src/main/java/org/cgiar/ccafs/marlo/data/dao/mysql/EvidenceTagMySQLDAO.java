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

import org.cgiar.ccafs.marlo.data.dao.EvidenceTagDAO;
import org.cgiar.ccafs.marlo.data.model.EvidenceTag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class EvidenceTagMySQLDAO extends AbstractMarloDAO<EvidenceTag, Long> implements EvidenceTagDAO {


  @Inject
  public EvidenceTagMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteEvidenceTag(long evidenceTagId) {
    EvidenceTag evidenceTag = this.find(evidenceTagId);
    this.delete(evidenceTag);
  }

  @Override
  public boolean existEvidenceTag(long evidenceTagID) {
    EvidenceTag evidenceTag = this.find(evidenceTagID);
    if (evidenceTag == null) {
      return false;
    }
    return true;

  }

  @Override
  public EvidenceTag find(long id) {
    return super.find(EvidenceTag.class, id);

  }

  @Override
  public List<EvidenceTag> findAll() {
    String query = "from " + EvidenceTag.class.getName();
    List<EvidenceTag> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public EvidenceTag save(EvidenceTag evidenceTag) {
    if (evidenceTag.getId() == null) {
      super.saveEntity(evidenceTag);
    } else {
      evidenceTag = super.update(evidenceTag);
    }


    return evidenceTag;
  }


}