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

import org.cgiar.ccafs.marlo.data.dao.InternalQaCommentableFieldsDAO;
import org.cgiar.ccafs.marlo.data.model.InternalQaCommentableFields;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class InternalQaCommentableFieldsMySQLDAO extends AbstractMarloDAO<InternalQaCommentableFields, Long>
  implements InternalQaCommentableFieldsDAO {


  @Inject
  public InternalQaCommentableFieldsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteInternalQaCommentableFields(long internalQaCommentableFieldsId) {
    InternalQaCommentableFields internalQaCommentableFields = this.find(internalQaCommentableFieldsId);
    this.delete(internalQaCommentableFields);
  }

  @Override
  public boolean existInternalQaCommentableFields(long internalQaCommentableFieldsID) {
    InternalQaCommentableFields internalQaCommentableFields = this.find(internalQaCommentableFieldsID);
    if (internalQaCommentableFields == null) {
      return false;
    }
    return true;

  }

  @Override
  public InternalQaCommentableFields find(long id) {
    return super.find(InternalQaCommentableFields.class, id);

  }

  @Override
  public List<InternalQaCommentableFields> findAll() {
    String query = "from " + InternalQaCommentableFields.class.getName() + " where is_active=1";
    List<InternalQaCommentableFields> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public InternalQaCommentableFields save(InternalQaCommentableFields internalQaCommentableFields) {
    if (internalQaCommentableFields.getId() == null) {
      super.saveEntity(internalQaCommentableFields);
    } else {
      internalQaCommentableFields = super.update(internalQaCommentableFields);
    }


    return internalQaCommentableFields;
  }


}