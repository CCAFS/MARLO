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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies.ExpectedStudiesOtherItem;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudiesOtherDTO;

import com.opensymphony.xwork2.inject.Inject;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)")
public class ExpectedStudiesOther {

  private static final Logger LOG = LoggerFactory.getLogger(ExpectedStudies.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private ExpectedStudiesOtherItem<ProjectExpectedStudiesOtherDTO> expectedStudiesOtherItem;

  @Inject
  public ExpectedStudiesOther(ExpectedStudiesOtherItem<ProjectExpectedStudiesOtherDTO> expectedStudiesOtherItem,
    UserManager userManager) {
    this.expectedStudiesOtherItem = expectedStudiesOtherItem;
    this.userManager = userManager;
  }
}
