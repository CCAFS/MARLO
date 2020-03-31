/*****************************************************************
 * \ * This file is part of Managing Agricultural Research for Learning &
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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.QAToken;

import org.cgiar.ccafs.marlo.data.manager.QATokenAuthManager;
import org.cgiar.ccafs.marlo.rest.dto.QATokenAuthDTO;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.ResponseEntity;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
@Named
public class QATokenItem<T> {

  // Managers
  private QATokenAuthManager qATokenManager;

  @Inject
  public QATokenItem(QATokenAuthManager qATokenManager) {
    this.qATokenManager = qATokenManager;
  }

  /**
   * Generate a QA TokenAuth
   * 
   * @param name
   * @param username
   * @param email
   * @param smoCode
   * @return a QATokenAuthDTO with the QATokenItem Item
   */
  public ResponseEntity<QATokenAuthDTO> getToken(String name, String username, String email, String smoCode) {
    return null;
  }

}
