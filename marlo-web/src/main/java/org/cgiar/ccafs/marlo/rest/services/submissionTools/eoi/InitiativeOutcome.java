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

package org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi;

import java.io.Serializable;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class InitiativeOutcome implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String toc_result_id;
  private String result_title;


  public String getResult_title() {
    return result_title;
  }

  public String getToc_result_id() {
    return toc_result_id;
  }

  public void setResult_title(String result_title) {
    this.result_title = result_title;
  }

  public void setToc_result_id(String toc_result_id) {
    this.toc_result_id = toc_result_id;
  }

}
