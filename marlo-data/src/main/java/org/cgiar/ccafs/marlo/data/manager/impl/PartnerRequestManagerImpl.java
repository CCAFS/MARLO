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


import org.cgiar.ccafs.marlo.data.dao.InstitutionDAO;
import org.cgiar.ccafs.marlo.data.dao.InstitutionLocationDAO;
import org.cgiar.ccafs.marlo.data.dao.InstitutionTypeDAO;
import org.cgiar.ccafs.marlo.data.dao.LocElementDAO;
import org.cgiar.ccafs.marlo.data.dao.PartnerRequestDAO;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome.Status;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Christian Garcia
 */
@Named
public class PartnerRequestManagerImpl implements PartnerRequestManager {


  private static final Logger LOG = LoggerFactory.getLogger(PartnerRequestManagerImpl.class);

  private PartnerRequestDAO partnerRequestDAO;
  private InstitutionDAO institutionDAO;
  private InstitutionLocationDAO institutionLocationDAO;
  private InstitutionTypeDAO institutionTypeDAO;
  private LocElementDAO locElementDAO;
  // Managers


  @Inject
  public PartnerRequestManagerImpl(PartnerRequestDAO partnerRequestDAO, InstitutionDAO institutionDAO,
    InstitutionLocationDAO institutionLocationDAO, InstitutionTypeDAO institutionTypeDAO, LocElementDAO locElementDAO) {
    this.partnerRequestDAO = partnerRequestDAO;
    this.institutionDAO = institutionDAO;
    this.institutionLocationDAO = institutionLocationDAO;
    this.institutionTypeDAO = institutionTypeDAO;
    this.locElementDAO = locElementDAO;
  }

  @Override
  @Transactional
  public List<PartnerRequestOutcome> acceptOfficeRequests(List<Long> requestIds, long institutionId) {
    List<PartnerRequestOutcome> outcomes = new ArrayList<>();
    // Ascending id order: two overlapping batches take their locks in the same order, so they cannot deadlock.
    for (Long requestId : ascending(requestIds)) {
      PartnerRequest request = this.partnerRequestDAO.findForUpdate(requestId);
      PartnerRequestOutcome refusal = this.refuseOffice(requestId, request, institutionId);
      if (refusal != null) {
        outcomes.add(refusal);
        continue;
      }
      Institution institution = request.getInstitution();
      request.setAceptedDate(new Date());
      request.setActive(false);
      Status status;
      InstitutionLocation existing =
        this.institutionLocationDAO.findByLocation(request.getLocElement().getId(), institution.getId());
      if (existing == null) {
        this.institutionLocationDAO.save(new InstitutionLocation(institution, request.getLocElement(), false));
        request.setAcepted(Boolean.TRUE);
        status = Status.DONE;
      } else {
        LOG.warn("The InstitutionLocation ID: {} already exists, office request {} is closed without adding it.",
          existing.getId(), requestId);
        request.setAcepted(Boolean.FALSE);
        request.setModificationJustification(
          "The InstitutionLocation ID:" + existing.getId() + " already exist in the system.");
        status = Status.SKIPPED_EXISTING;
      }
      this.partnerRequestDAO.save(request);
      outcomes.add(new PartnerRequestOutcome(requestId, status, request, institution));
    }
    return outcomes;
  }

  @Override
  @Transactional
  public PartnerRequestOutcome acceptPartnerRequest(long requestId, User approver) {
    if (approver == null || approver.getId() == null) {
      // institutions.created_by is NOT NULL: without an approver the insert below would fail half way through.
      throw new IllegalArgumentException("Partner request " + requestId + " cannot be accepted without an approver.");
    }
    PartnerRequest copy = this.partnerRequestDAO.findForUpdate(requestId);
    PartnerRequestOutcome refusal = this.refusePartner(requestId, copy);
    if (refusal != null) {
      return refusal;
    }
    if (copy.getInstitutionType() == null || copy.getLocElement() == null) {
      // A request without a type or a country cannot become an institution: fail before anything is written.
      throw new IllegalStateException("Partner request " + requestId + " has no institution type or country.");
    }
    // The institution is built from the copy, which the superadmin may have edited, never from the original.
    Institution institution = new Institution();
    institution.setName(copy.getPartnerName());
    institution.setAcronym(copy.getAcronym());
    institution.setWebsiteLink(copy.getWebPage());
    institution.setInstitutionType(copy.getInstitutionType());
    institution.setAdded(new Date());
    institution.setCreatedBy(approver.getId());
    this.institutionDAO.save(institution);

    InstitutionLocation headquarters = new InstitutionLocation();
    headquarters.setInstitution(institution);
    headquarters.setLocElement(copy.getLocElement());
    headquarters.setHeadquater(Boolean.TRUE);
    this.institutionLocationDAO.save(headquarters);

    copy.setAcepted(Boolean.TRUE);
    copy.setAceptedDate(new Date());
    copy.setActive(false);
    copy.setInstitution(institution);
    this.partnerRequestDAO.save(copy);

    PartnerRequest original = copy.getPartnerRequest();
    original.setActive(false);
    this.partnerRequestDAO.save(original);
    return new PartnerRequestOutcome(requestId, Status.DONE, copy, institution);
  }

  @Override
  @Transactional
  public PartnerRequest createPartnerRequest(PartnerRequest original, PartnerRequest copy) {
    PartnerRequest savedOriginal = this.partnerRequestDAO.save(original);
    copy.setPartnerRequest(savedOriginal);
    copy.setModified(Boolean.FALSE);
    return this.partnerRequestDAO.save(copy);
  }

  @Override
  @Transactional
  public void deletePartnerRequest(long partnerRequestId) {

    partnerRequestDAO.deletePartnerRequest(partnerRequestId);
  }

  @Override
  @Transactional
  public PartnerRequestOutcome editPartnerRequest(long requestId, String name, String acronym, String webPage,
    Long institutionTypeId, String countryIso, String justification) {
    PartnerRequest copy = this.partnerRequestDAO.findForUpdate(requestId);
    PartnerRequestOutcome refusal = this.refusePartner(requestId, copy);
    if (refusal != null) {
      return refusal;
    }
    InstitutionType type = null;
    if (institutionTypeId != null && institutionTypeId.longValue() != 0) {
      type = this.institutionTypeDAO.find(institutionTypeId);
    }
    LocElement country = null;
    if (countryIso != null && !countryIso.isEmpty()) {
      country = this.locElementDAO.findISOCode(countryIso);
    }
    boolean edited = isFilled(name) || isFilled(acronym) || isFilled(webPage) || type != null || country != null
      || isFilled(justification);
    if (edited) {
      // The edit form sends every field, so an empty text field means the superadmin cleared it.
      copy.setPartnerName(isFilled(name) ? name : "");
      copy.setAcronym(isFilled(acronym) ? acronym : "");
      copy.setWebPage(isFilled(webPage) ? webPage : "");
      if (type != null) {
        copy.setInstitutionType(type);
      }
      if (country != null) {
        copy.setLocElement(country);
      }
      if (isFilled(justification)) {
        copy.setModificationJustification(justification);
      }
      copy.setModified(Boolean.TRUE);
      this.partnerRequestDAO.save(copy);
    }
    return new PartnerRequestOutcome(requestId, Status.DONE, copy, null);
  }

  @Override
  public boolean existPartnerRequest(long partnerRequestID) {

    return partnerRequestDAO.existPartnerRequest(partnerRequestID);
  }

  @Override
  public List<PartnerRequest> findAll() {

    return partnerRequestDAO.findAll();

  }

  @Override
  public PartnerRequest getPartnerRequestById(long partnerRequestID) {

    return partnerRequestDAO.find(partnerRequestID);
  }

  private static List<Long> ascending(List<Long> ids) {
    TreeSet<Long> sorted = new TreeSet<>();
    if (ids != null) {
      for (Long id : ids) {
        if (id != null) {
          sorted.add(id);
        }
      }
    }
    return new ArrayList<>(sorted);
  }

  private static boolean isFilled(String value) {
    return value != null && !value.isEmpty();
  }

  /** Pending: still active and not decided (accepted is null, true or false once decided). */
  private static boolean isPending(PartnerRequest request) {
    return request.isActive() && request.getAcepted() == null;
  }

  /**
   * Why a partner request (a copy) cannot be decided, or null when it can. The order is fixed: a row that does not
   * exist, then a row of the wrong kind, then a row somebody already decided.
   */
  private PartnerRequestOutcome refusePartner(long requestId, PartnerRequest request) {
    if (request == null) {
      return new PartnerRequestOutcome(requestId, Status.NOT_FOUND, null, null);
    }
    // A copy has a parent and is not an office row; the original and the office rows are not decided here.
    if (request.getPartnerRequest() == null || request.isOffice()) {
      return new PartnerRequestOutcome(requestId, Status.WRONG_KIND, request, null);
    }
    if (!isPending(request)) {
      return new PartnerRequestOutcome(requestId, Status.ALREADY_HANDLED, request, null);
    }
    return null;
  }

  /** Same as {@link #refusePartner}, for an office row of the given institution that names a country. */
  private PartnerRequestOutcome refuseOffice(long requestId, PartnerRequest request, long institutionId) {
    if (request == null) {
      return new PartnerRequestOutcome(requestId, Status.NOT_FOUND, null, null);
    }
    boolean ofInstitution = request.isOffice() && request.getInstitution() != null
      && request.getInstitution().getId() != null && request.getInstitution().getId().longValue() == institutionId
      && request.getLocElement() != null && request.getLocElement().getId() != null;
    if (!ofInstitution) {
      return new PartnerRequestOutcome(requestId, Status.WRONG_KIND, request, null);
    }
    if (!isPending(request)) {
      return new PartnerRequestOutcome(requestId, Status.ALREADY_HANDLED, request, null);
    }
    return null;
  }

  @Override
  @Transactional
  public List<PartnerRequestOutcome> rejectOfficeRequests(List<Long> requestIds, long institutionId,
    String justification, User user) {
    List<PartnerRequestOutcome> outcomes = new ArrayList<>();
    for (Long requestId : ascending(requestIds)) {
      PartnerRequest request = this.partnerRequestDAO.findForUpdate(requestId);
      PartnerRequestOutcome refusal = this.refuseOffice(requestId, request, institutionId);
      if (refusal != null) {
        outcomes.add(refusal);
        continue;
      }
      this.markRejected(request, justification, user);
      this.partnerRequestDAO.save(request);
      outcomes.add(new PartnerRequestOutcome(requestId, Status.DONE, request, request.getInstitution()));
    }
    return outcomes;
  }

  @Override
  @Transactional
  public PartnerRequestOutcome rejectPartnerRequest(long requestId, String justification, User user) {
    PartnerRequest copy = this.partnerRequestDAO.findForUpdate(requestId);
    PartnerRequestOutcome refusal = this.refusePartner(requestId, copy);
    if (refusal != null) {
      return refusal;
    }
    this.markRejected(copy, justification, user);
    this.partnerRequestDAO.save(copy);
    PartnerRequest original = copy.getPartnerRequest();
    original.setActive(false);
    this.partnerRequestDAO.save(original);
    return new PartnerRequestOutcome(requestId, Status.DONE, copy, null);
  }

  private void markRejected(PartnerRequest request, String justification, User user) {
    request.setAcepted(Boolean.FALSE);
    request.setActive(false);
    request.setRejectedBy(user);
    request.setRejectJustification(justification);
    request.setRejectedDate(new Date());
  }

  @Override
  @Transactional
  public PartnerRequest savePartnerRequest(PartnerRequest partnerRequest) {

    return partnerRequestDAO.save(partnerRequest);
  }


}
