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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projectPage.ProjectPageItem;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
// @ApiIgnore
@RestController
@Api(tags = "Project Page")
public class ProjectPage {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectPage.class);
  private ProjectPageItem<ProjectPage> projectPageItem;
  @Autowired
  private Environment env;

  @Inject
  public ProjectPage(ProjectPageItem<ProjectPage> projectPageItem) {
    this.projectPageItem = projectPageItem;
  }

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/projectpageList", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ProjectPageDTO>
    findAllProjectPage(@ApiParam(value = "CGIAR entity", required = true) @PathVariable String CGIAREntity) {

    List<ProjectPageDTO> pplist = projectPageItem.findAllProjectPage(CGIAREntity);


    return pplist;
  }

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/projectpage/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectPageDTO> findProjectById(
    @ApiParam(value = "CGIAR entity", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "Project Id", required = true) @PathVariable Long id) {

    try {
      ResponseEntity<ProjectPageDTO> response = projectPageItem.findProjectPageById(id, CGIAREntity);
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        // TODO add correctly documentation

        throw new NotFoundException("404", "Not Found");
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

}
