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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Deliverables;

import org.cgiar.ccafs.marlo.rest.dto.InstitutionsWOSDTO;
import org.cgiar.ccafs.marlo.rest.dto.PublicationsWOSDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class DeliverablesWOSItem<T> {

  public ResponseEntity<PublicationsWOSDTO> validateDeliverable(String url) {
    PublicationsWOSDTO publication = new PublicationsWOSDTO();
    publication.setUrl(url);
    publication.setIs_isi(false);
    publication.setJournalName("Testing journal Name");
    publication.setIs_oa(true);
    List<InstitutionsWOSDTO> instutions = new ArrayList<InstitutionsWOSDTO>();
    instutions.add(new InstitutionsWOSDTO(new Long(1), "Wageningen University and Research Centre"));
    instutions.add(new InstitutionsWOSDTO(new Long(2), "Mikocheni Agricultural Research Institute"));
    instutions.add(new InstitutionsWOSDTO(null, "Int Ctr Agr Res Dry Areas ICARDA"));
    publication.setOrganizations(instutions);
    publication.setPages("2-10");
    publication.setPublicationType("Article");
    publication.setIssue("NA");
    publication.setVolume("2");
    publication.setPublicationYear(2020);
    publication.setTitle("Testing publication Title");
    List<String> authors = new ArrayList<String>();
    authors.add("Perez, Diego");
    authors.add("Wang, Weiyan");
    authors.add("Gao, Siman");
    publication.setAuthors(authors);
    return Optional.ofNullable(publication).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
