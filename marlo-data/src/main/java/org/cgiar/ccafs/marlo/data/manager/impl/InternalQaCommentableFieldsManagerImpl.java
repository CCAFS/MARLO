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


import org.cgiar.ccafs.marlo.data.dao.InternalQaCommentableFieldsDAO;
import org.cgiar.ccafs.marlo.data.manager.InternalQaCommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class InternalQaCommentableFieldsManagerImpl implements InternalQaCommentableFieldsManager {


  private InternalQaCommentableFieldsDAO internalQaCommentableFieldsDAO;
  // Managers


  @Inject
  public InternalQaCommentableFieldsManagerImpl(InternalQaCommentableFieldsDAO internalQaCommentableFieldsDAO) {
    this.internalQaCommentableFieldsDAO = internalQaCommentableFieldsDAO;


  }

  @Override
  public void deleteInternalQaCommentableFields(long internalQaCommentableFieldsId) {

    internalQaCommentableFieldsDAO.deleteInternalQaCommentableFields(internalQaCommentableFieldsId);
  }

  @Override
  public boolean existInternalQaCommentableFields(long internalQaCommentableFieldsID) {

    return internalQaCommentableFieldsDAO.existInternalQaCommentableFields(internalQaCommentableFieldsID);
  }

  @Override
  public List<FeedbackQACommentableFields> findAll() {

    return internalQaCommentableFieldsDAO.findAll();

  }

  @Override
  public FeedbackQACommentableFields getInternalQaCommentableFieldsById(long internalQaCommentableFieldsID) {

    return internalQaCommentableFieldsDAO.find(internalQaCommentableFieldsID);
  }

  @Override
  public FeedbackQACommentableFields saveInternalQaCommentableFields(FeedbackQACommentableFields feedbackQACommentableFields) {

    return internalQaCommentableFieldsDAO.save(feedbackQACommentableFields);
  }


}
