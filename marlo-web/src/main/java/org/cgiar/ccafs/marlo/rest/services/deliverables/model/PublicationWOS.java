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

package org.cgiar.ccafs.marlo.rest.services.deliverables.model;

import java.io.Serializable;
import java.util.List;

public class PublicationWOS implements Serializable {

  private static final long serialVersionUID = 9076914114141491832L;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  private String doi;

  private String publication_type;

  private String volume;

  private String issue;

  private String is_oa;

  private String oa_link;

  private String source;

  private String publication_year;

  private String is_isi;

  private String start_end_pages;

  private String title;

  private String journal_name;

  private List<PublicationAuthorWOS> authors;


  private List<PublicationInstitutionWOS> organizations;

  private PublicationAltmetricsWOS altmetric;

  private PublicationGardianWOS gardian;


  public PublicationWOS() {
    super();
  }


  public PublicationAltmetricsWOS getAltmetric() {
    return altmetric;
  }


  public List<PublicationAuthorWOS> getAuthors() {
    return authors;
  }


  public String getDoi() {
    return doi;
  }


  public PublicationGardianWOS getGardian() {
    return gardian;
  }

  public String getIs_isi() {
    return is_isi;
  }

  public String getIs_oa() {
    return is_oa;
  }


  public String getIssue() {
    return issue;
  }


  public String getJournal_name() {
    return journal_name;
  }


  public String getOa_link() {
    return oa_link;
  }


  public List<PublicationInstitutionWOS> getOrganizations() {
    return organizations;
  }


  public String getPublication_type() {
    return publication_type;
  }


  public String getPublication_year() {
    return publication_year;
  }


  public String getSource() {
    return source;
  }


  public String getStart_end_pages() {
    return start_end_pages;
  }


  public String getTitle() {
    return title;
  }


  public String getVolume() {
    return volume;
  }


  public void setAltmetric(PublicationAltmetricsWOS altmetric) {
    this.altmetric = altmetric;
  }


  public void setAuthors(List<PublicationAuthorWOS> authors) {
    this.authors = authors;
  }


  public void setDoi(String doi) {
    this.doi = doi;
  }


  public void setGardian(PublicationGardianWOS gardian) {
    this.gardian = gardian;
  }


  public void setIs_isi(String is_isi) {
    this.is_isi = is_isi;
  }


  public void setIs_oa(String is_oa) {
    this.is_oa = is_oa;
  }


  public void setIssue(String issue) {
    this.issue = issue;
  }


  public void setJournal_name(String journal_name) {
    this.journal_name = journal_name;
  }


  public void setOa_link(String oa_link) {
    this.oa_link = oa_link;
  }


  public void setOrganizations(List<PublicationInstitutionWOS> organizations) {
    this.organizations = organizations;
  }


  public void setPublication_type(String publication_type) {
    this.publication_type = publication_type;
  }


  public void setPublication_year(String publication_year) {
    this.publication_year = publication_year;
  }


  public void setSource(String source) {
    this.source = source;
  }


  public void setStart_end_pages(String start_end_pages) {
    this.start_end_pages = start_end_pages;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setVolume(String volume) {
    this.volume = volume;
  }


}
