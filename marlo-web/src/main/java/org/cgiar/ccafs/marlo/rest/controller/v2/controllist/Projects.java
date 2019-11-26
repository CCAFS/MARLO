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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projects.ProjectItem;
import org.cgiar.ccafs.marlo.rest.dto.ProjectDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import javax.inject.Inject;
import javax.inject.Named;

import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@ApiIgnore
@RestController
@Named
public class Projects {

  private static final Logger LOG = LoggerFactory.getLogger(Projects.class);
  private ProjectItem<Projects> projectItem;

  @Inject
  public Projects(ProjectItem<Projects> projectItem) {
    this.projectItem = projectItem;
  }

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/project/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectDTO> findProjectById(
    @ApiParam(value = "CGIAR entity", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "Project Id", required = true) @PathVariable Long id) {
    ResponseEntity<ProjectDTO> response = projectItem.findProjectById(id, CGIAREntity);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      // TODO add correctly documentation
      throw new NotFoundException("404", "Not Found");
    }
    return response;
  }

}
