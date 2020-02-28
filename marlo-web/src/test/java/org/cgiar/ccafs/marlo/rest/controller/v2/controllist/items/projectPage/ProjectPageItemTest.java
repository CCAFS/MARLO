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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projectPage;

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.ProjectPage;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertTrue;


public class ProjectPageItemTest {

  private ProjectPageItem<ProjectPage> projectPageItem;

  @Test
  public void findProjectPageById() {
    ResponseEntity<ProjectPageDTO> response = projectPageItem.findProjectPageById(new Long(2), "CCAFS");

    assertTrue(response.getStatusCode() != HttpStatus.NOT_FOUND);
  }

}
