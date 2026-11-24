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
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome.Status;
import org.cgiar.ccafs.marlo.data.model.User;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CHG-PARTREQ-001-T01: the refusal rules and the write set of the six {@code @Transactional} methods of
 * {@link PartnerRequestManagerImpl} (FN-004 to FN-008, DA-001). The DAOs are hand-written doubles over an in-memory
 * table of requests: a lookup answers the fixture row, never the argument, and every double fails on a call or an
 * argument it does not expect. This suite does not prove the row lock or the rollback; the live probe does (T04).
 */
public class PartnerRequestManagerGuardTest {

  private static final long COPY_ID = 20L;
  private static final long ORIGINAL_ID = 10L;
  private static final long OFFICE_INSTITUTION_ID = 500L;
  private static final long OTHER_INSTITUTION_ID = 501L;
  private static final User APPROVER = user(88L);

  private Map<Long, PartnerRequest> table;
  private List<Long> locks;
  private List<PartnerRequest> savedRequests;
  private List<Institution> savedInstitutions;
  private List<InstitutionLocation> savedLocations;
  private List<String> lookups;
  private Map<String, InstitutionLocation> existingLocations;
  private PartnerRequestManagerImpl manager;

  private PartnerRequest original;
  private PartnerRequest copy;
  private LocElement kenya;
  private LocElement ghana;
  private InstitutionType university;
  private Institution officeInstitution;

  private static <T> T answering(Class<T> type, Map<String, Function<Object[], Object>> answers) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
      Function<Object[], Object> answer = answers.get(method.getName());
      if (answer != null) {
        return answer.apply(args);
      }
      if (method.getName().equals("toString")) {
        return type.getSimpleName() + " double";
      }
      throw new UnsupportedOperationException(type.getSimpleName() + "." + method.getName());
    }));
  }

  private static PartnerRequest request(long id, boolean office) {
    PartnerRequest request = new PartnerRequest();
    request.setId(id);
    request.setOffice(office);
    return request;
  }

  /** A copy with everything an accept needs, so a missing guard shows up as a write and not as missing data. */
  private PartnerRequest completeCopy(long id) {
    PartnerRequest decided = request(id, false);
    decided.setPartnerRequest(this.original);
    decided.setPartnerName("Decided Name " + id);
    decided.setInstitutionType(this.university);
    decided.setLocElement(this.kenya);
    this.table.put(id, decided);
    return decided;
  }

  private PartnerRequest officeRequest(long id, Institution institution, LocElement country) {
    PartnerRequest request = request(id, true);
    request.setInstitution(institution);
    request.setLocElement(country);
    this.table.put(id, request);
    return request;
  }

  private static Institution institution(long id) {
    Institution institution = new Institution();
    institution.setId(id);
    return institution;
  }

  private static LocElement country(long id, String iso) {
    LocElement country = new LocElement();
    country.setId(id);
    country.setIsoAlpha2(iso);
    return country;
  }

  private static User user(long id) {
    User user = new User();
    user.setId(id);
    return user;
  }

  private void assertNothingWritten() {
    assertTrue("a request was saved", this.savedRequests.isEmpty());
    assertTrue("an institution was saved", this.savedInstitutions.isEmpty());
    assertTrue("a location was saved", this.savedLocations.isEmpty());
  }

  @Before
  public void setUp() {
    this.table = new HashMap<>();
    this.locks = new ArrayList<>();
    this.savedRequests = new ArrayList<>();
    this.savedInstitutions = new ArrayList<>();
    this.savedLocations = new ArrayList<>();
    this.lookups = new ArrayList<>();
    this.existingLocations = new HashMap<>();

    this.kenya = country(110L, "KE");
    this.ghana = country(111L, "GH");
    this.university = new InstitutionType();
    this.university.setId(7L);
    this.officeInstitution = institution(OFFICE_INSTITUTION_ID);

    // The original keeps what the requester typed; the copy is what the superadmin reviewed and edited.
    this.original = request(ORIGINAL_ID, false);
    this.original.setPartnerName("Original Name");
    this.original.setAcronym("ORG");
    this.original.setWebPage("http://original.example");
    this.original.setInstitutionType(null);
    this.original.setLocElement(this.ghana);
    this.copy = request(COPY_ID, false);
    this.copy.setPartnerRequest(this.original);
    this.copy.setPartnerName("Edited Name");
    this.copy.setAcronym("EDT");
    this.copy.setWebPage("http://edited.example");
    this.copy.setInstitutionType(this.university);
    this.copy.setLocElement(this.kenya);
    this.table.put(ORIGINAL_ID, this.original);
    this.table.put(COPY_ID, this.copy);

    Map<String, Function<Object[], Object>> requestDao = new HashMap<>();
    requestDao.put("findForUpdate", args -> {
      this.locks.add((Long) args[0]);
      return this.table.get(args[0]);
    });
    requestDao.put("save", args -> {
      this.savedRequests.add((PartnerRequest) args[0]);
      return args[0];
    });
    Map<String, Function<Object[], Object>> institutionDao = new HashMap<>();
    institutionDao.put("save", args -> {
      this.savedInstitutions.add((Institution) args[0]);
      return args[0];
    });
    Map<String, Function<Object[], Object>> locationDao = new HashMap<>();
    locationDao.put("save", args -> {
      this.savedLocations.add((InstitutionLocation) args[0]);
      return args[0];
    });
    locationDao.put("findByLocation", args -> {
      String key = args[0] + "@" + args[1];
      this.lookups.add(key);
      return this.existingLocations.get(key);
    });
    Map<String, Function<Object[], Object>> typeDao = new HashMap<>();
    typeDao.put("find", args -> {
      assertEquals("the type id the manager looked up", 8L, args[0]);
      InstitutionType type = new InstitutionType();
      type.setId(8L);
      return type;
    });
    Map<String, Function<Object[], Object>> locDao = new HashMap<>();
    locDao.put("findISOCode", args -> {
      assertEquals("the ISO code the manager looked up", "GH", args[0]);
      return this.ghana;
    });

    this.manager = new PartnerRequestManagerImpl(answering(PartnerRequestDAO.class, requestDao),
      answering(InstitutionDAO.class, institutionDao), answering(InstitutionLocationDAO.class, locationDao),
      answering(InstitutionTypeDAO.class, typeDao), answering(LocElementDAO.class, locDao));
  }

  // ---- accept partner (FN-004, FN-005, DA-001) ----

  @Test
  public void acceptBuildsTheInstitutionFromTheCopyAndClosesBothRows() {
    PartnerRequestOutcome outcome = this.manager.acceptPartnerRequest(COPY_ID, APPROVER);

    assertEquals(Status.DONE, outcome.getStatus());
    assertEquals(1, this.savedInstitutions.size());
    Institution created = this.savedInstitutions.get(0);
    assertEquals("Edited Name", created.getName());
    assertEquals("EDT", created.getAcronym());
    assertEquals("http://edited.example", created.getWebsiteLink());
    assertSame(this.university, created.getInstitutionType());
    assertNotNull(created.getAdded());
    assertEquals("the approver is recorded as the creator", Long.valueOf(88L), created.getCreatedBy());
    assertSame(created, outcome.getInstitution());

    assertEquals(1, this.savedLocations.size());
    InstitutionLocation headquarters = this.savedLocations.get(0);
    assertSame(created, headquarters.getInstitution());
    assertSame(this.kenya, headquarters.getLocElement());
    assertTrue(headquarters.isHeadquater());

    assertEquals(Boolean.TRUE, this.copy.getAcepted());
    assertNotNull(this.copy.getAceptedDate());
    assertFalse(this.copy.isActive());
    assertSame(created, this.copy.getInstitution());
    assertFalse(this.original.isActive());
    assertNull("the original keeps no decision", this.original.getAcepted());
    assertEquals(Arrays.asList(this.copy, this.original), this.savedRequests);
    assertEquals(Arrays.asList(COPY_ID), this.locks);
  }

  @Test
  public void acceptRefusesWithoutWritingAnything() {
    this.completeCopy(31L).setAcepted(Boolean.TRUE);
    this.completeCopy(32L).setAcepted(Boolean.FALSE);
    this.completeCopy(33L).setActive(false);
    PartnerRequest office = officeRequest(34L, this.officeInstitution, this.kenya);

    assertEquals(Status.NOT_FOUND, this.manager.acceptPartnerRequest(99L, APPROVER).getStatus());
    assertEquals(Status.WRONG_KIND, this.manager.acceptPartnerRequest(ORIGINAL_ID, APPROVER).getStatus());
    assertEquals(Status.WRONG_KIND, this.manager.acceptPartnerRequest(34L, APPROVER).getStatus());
    assertEquals(Status.ALREADY_HANDLED, this.manager.acceptPartnerRequest(31L, APPROVER).getStatus());
    assertEquals(Status.ALREADY_HANDLED, this.manager.acceptPartnerRequest(32L, APPROVER).getStatus());
    assertEquals(Status.ALREADY_HANDLED, this.manager.acceptPartnerRequest(33L, APPROVER).getStatus());

    assertNothingWritten();
    assertTrue("the original row was changed", this.original.isActive());
    assertTrue("the office row was changed", office.isActive());
    assertNull(office.getAcepted());
    assertEquals(Arrays.asList(99L, ORIGINAL_ID, 34L, 31L, 32L, 33L), this.locks);
  }

  @Test
  public void acceptTwiceCreatesOneInstitution() {
    PartnerRequestOutcome first = this.manager.acceptPartnerRequest(COPY_ID, APPROVER);
    PartnerRequestOutcome replay = this.manager.acceptPartnerRequest(COPY_ID, APPROVER);

    assertEquals(Status.DONE, first.getStatus());
    assertEquals(Status.ALREADY_HANDLED, replay.getStatus());
    assertEquals(1, this.savedInstitutions.size());
    assertEquals(1, this.savedLocations.size());
  }

  @Test
  public void acceptWithoutATypeFailsBeforeWritingAnything() {
    this.copy.setInstitutionType(null);
    try {
      this.manager.acceptPartnerRequest(COPY_ID, APPROVER);
      fail("an IllegalStateException was expected");
    } catch (IllegalStateException expected) {
      assertNothingWritten();
      assertTrue(this.copy.isActive());
      assertNull(this.copy.getAcepted());
    }
  }

  @Test
  public void acceptWithoutAnApproverFailsBeforeWritingAnything() {
    // institutions.created_by is NOT NULL: an accept that cannot name its creator must not start writing
    for (User approver : Arrays.asList(null, new User())) {
      try {
        this.manager.acceptPartnerRequest(COPY_ID, approver);
        fail("an IllegalArgumentException was expected");
      } catch (IllegalArgumentException expected) {
        assertNothingWritten();
        assertTrue("no lock is taken", this.locks.isEmpty());
        assertTrue(this.copy.isActive());
        assertNull(this.copy.getAcepted());
      }
    }
  }

  // ---- reject partner (FN-006) ----

  @Test
  public void rejectRecordsTheDecisionOnTheCopyAndClosesBothRows() {
    User rejecter = user(77L);

    PartnerRequestOutcome outcome =
      this.manager.rejectPartnerRequest(COPY_ID, "Duplicate of an existing partner", rejecter);

    assertEquals(Status.DONE, outcome.getStatus());
    assertSame(this.copy, outcome.getRequest());
    assertEquals(Boolean.FALSE, this.copy.getAcepted());
    assertEquals("Duplicate of an existing partner", this.copy.getRejectJustification());
    assertSame(rejecter, this.copy.getRejectedBy());
    assertNotNull(this.copy.getRejectedDate());
    assertFalse(this.copy.isActive());
    assertFalse(this.original.isActive());
    assertTrue(this.savedInstitutions.isEmpty());
    assertEquals(Arrays.asList(this.copy, this.original), this.savedRequests);
  }

  @Test
  public void rejectRefusesWithoutWritingAnything() {
    PartnerRequest decided = request(31L, false);
    decided.setPartnerRequest(this.original);
    decided.setAcepted(Boolean.TRUE);
    this.table.put(31L, decided);
    PartnerRequest office = officeRequest(34L, this.officeInstitution, this.kenya);

    assertEquals(Status.NOT_FOUND, this.manager.rejectPartnerRequest(99L, "no", user(1L)).getStatus());
    assertEquals(Status.WRONG_KIND, this.manager.rejectPartnerRequest(ORIGINAL_ID, "no", user(1L)).getStatus());
    assertEquals(Status.WRONG_KIND, this.manager.rejectPartnerRequest(34L, "no", user(1L)).getStatus());
    assertEquals(Status.ALREADY_HANDLED, this.manager.rejectPartnerRequest(31L, "no", user(1L)).getStatus());

    assertNothingWritten();
    assertTrue(this.original.isActive());
    assertTrue(office.isActive());
    assertNull(office.getRejectJustification());
    assertNull(this.original.getRejectJustification());
  }

  // ---- edit (FN-007) ----

  @Test
  public void editChangesTheCopyAndLeavesTheOriginalAlone() {
    PartnerRequestOutcome outcome = this.manager.editPartnerRequest(COPY_ID, "Renamed", "RNM",
      "http://renamed.example", 8L, "GH", "Typo in the name");

    assertEquals(Status.DONE, outcome.getStatus());
    assertEquals("Renamed", this.copy.getPartnerName());
    assertEquals("RNM", this.copy.getAcronym());
    assertEquals("http://renamed.example", this.copy.getWebPage());
    assertEquals(Long.valueOf(8L), this.copy.getInstitutionType().getId());
    assertSame(this.ghana, this.copy.getLocElement());
    assertEquals("Typo in the name", this.copy.getModificationJustification());
    assertEquals(Boolean.TRUE, this.copy.getModified());
    assertEquals(Arrays.asList(this.copy), this.savedRequests);
    assertEquals("Original Name", this.original.getPartnerName());
    assertEquals("ORG", this.original.getAcronym());
    assertNull(this.original.getModificationJustification());
    assertTrue(this.original.isActive());
  }

  @Test
  public void editRefusesWithoutWritingAnything() {
    PartnerRequest decided = request(31L, false);
    decided.setPartnerRequest(this.original);
    decided.setPartnerName("Decided Name");
    decided.setAcepted(Boolean.TRUE);
    decided.setActive(false);
    this.table.put(31L, decided);
    PartnerRequest office = officeRequest(34L, this.officeInstitution, this.kenya);

    assertEquals(Status.NOT_FOUND, this.manager.editPartnerRequest(99L, "X", "X", "X", null, null, "x").getStatus());
    assertEquals(Status.WRONG_KIND,
      this.manager.editPartnerRequest(ORIGINAL_ID, "X", "X", "X", null, null, "x").getStatus());
    assertEquals(Status.WRONG_KIND, this.manager.editPartnerRequest(34L, "X", "X", "X", null, null, "x").getStatus());
    assertEquals(Status.ALREADY_HANDLED,
      this.manager.editPartnerRequest(31L, "X", "X", "X", null, null, "x").getStatus());

    assertNothingWritten();
    assertEquals("Decided Name", decided.getPartnerName());
    assertEquals("Original Name", this.original.getPartnerName());
    assertNull(office.getPartnerName());
    assertNull(decided.getModified());
  }

  @Test
  public void editWithNothingGivenWritesNothing() {
    PartnerRequestOutcome outcome = this.manager.editPartnerRequest(COPY_ID, null, "", null, null, null, "");

    assertEquals(Status.DONE, outcome.getStatus());
    assertNothingWritten();
    assertEquals("Edited Name", this.copy.getPartnerName());
    assertEquals("EDT", this.copy.getAcronym());
    assertNull(this.copy.getModified());
  }

  // ---- offices (FN-008) ----

  @Test
  public void officeLocksAreTakenInAscendingIdOrder() {
    officeRequest(9L, this.officeInstitution, this.kenya);
    officeRequest(3L, this.officeInstitution, this.ghana);

    this.manager.acceptOfficeRequests(Arrays.asList(9L, 3L), OFFICE_INSTITUTION_ID);
    assertEquals(Arrays.asList(3L, 9L), this.locks);

    this.locks.clear();
    officeRequest(9L, this.officeInstitution, this.kenya);
    officeRequest(3L, this.officeInstitution, this.ghana);
    this.manager.rejectOfficeRequests(Arrays.asList(9L, 3L, 9L), OFFICE_INSTITUTION_ID, "Not needed", user(1L));
    assertEquals("a repeated id is decided once", Arrays.asList(3L, 9L), this.locks);
  }

  @Test
  public void officeAcceptAddsOnlyTheCountriesThatAreNotThereYet() {
    PartnerRequest ghanaRequest = officeRequest(3L, this.officeInstitution, this.ghana);
    PartnerRequest kenyaRequest = officeRequest(9L, this.officeInstitution, this.kenya);
    InstitutionLocation present = new InstitutionLocation(this.officeInstitution, this.ghana, false);
    present.setId(61L);
    this.existingLocations.put("111@500", present);

    List<PartnerRequestOutcome> outcomes =
      this.manager.acceptOfficeRequests(Arrays.asList(9L, 3L), OFFICE_INSTITUTION_ID);

    assertEquals(2, outcomes.size());
    assertEquals(3L, outcomes.get(0).getRequestId());
    assertEquals(Status.SKIPPED_EXISTING, outcomes.get(0).getStatus());
    assertEquals(9L, outcomes.get(1).getRequestId());
    assertEquals(Status.DONE, outcomes.get(1).getStatus());
    assertTrue(outcomes.get(0).isNotifiable());
    assertTrue(outcomes.get(1).isNotifiable());
    assertEquals(Arrays.asList("111@500", "110@500"), this.lookups);

    assertEquals(1, this.savedLocations.size());
    InstitutionLocation added = this.savedLocations.get(0);
    assertSame(this.kenya, added.getLocElement());
    assertSame(this.officeInstitution, added.getInstitution());
    assertFalse("an office is never the headquarters", added.isHeadquater());

    assertEquals(Boolean.FALSE, ghanaRequest.getAcepted());
    assertEquals("The InstitutionLocation ID:61 already exist in the system.",
      ghanaRequest.getModificationJustification());
    assertFalse(ghanaRequest.isActive());
    assertEquals(Boolean.TRUE, kenyaRequest.getAcepted());
    assertNotNull(kenyaRequest.getAceptedDate());
    assertFalse(kenyaRequest.isActive());
    assertTrue(this.savedInstitutions.isEmpty());
  }

  @Test
  public void officeMethodsRefuseEachIdThatIsNotAPendingOfficeOfTheInstitution() {
    PartnerRequest pending = officeRequest(3L, this.officeInstitution, this.kenya);
    PartnerRequest otherInstitution = officeRequest(4L, institution(OTHER_INSTITUTION_ID), this.ghana);
    PartnerRequest handled = officeRequest(5L, this.officeInstitution, this.ghana);
    handled.setAcepted(Boolean.TRUE);
    handled.setActive(false);
    PartnerRequest noCountry = officeRequest(6L, this.officeInstitution, null);
    List<Long> ids = Arrays.asList(COPY_ID, 99L, 6L, 5L, 4L, 3L);

    List<PartnerRequestOutcome> accepted = this.manager.acceptOfficeRequests(ids, OFFICE_INSTITUTION_ID);

    assertEquals(Arrays.asList(3L, 4L, 5L, 6L, COPY_ID, 99L), this.locks);
    assertEquals(Status.DONE, accepted.get(0).getStatus());
    assertEquals(Status.WRONG_KIND, accepted.get(1).getStatus());
    assertEquals(Status.ALREADY_HANDLED, accepted.get(2).getStatus());
    assertEquals(Status.WRONG_KIND, accepted.get(3).getStatus());
    assertEquals("a partner copy is not an office row", Status.WRONG_KIND, accepted.get(4).getStatus());
    assertEquals(Status.NOT_FOUND, accepted.get(5).getStatus());
    assertNull(accepted.get(5).getRequest());
    assertEquals(99L, accepted.get(5).getRequestId());
    assertFalse(accepted.get(1).isNotifiable());
    assertEquals("only the pending office got a location", 1, this.savedLocations.size());
    assertSame(this.kenya, this.savedLocations.get(0).getLocElement());
    assertEquals(Arrays.asList(pending), this.savedRequests);
    assertTrue(otherInstitution.isActive());
    assertNull(otherInstitution.getAcepted());
    assertTrue(noCountry.isActive());
    assertTrue("the partner copy was changed", this.copy.isActive());
    assertEquals("only the accepted office was looked up", Arrays.asList("110@500"), this.lookups);

    this.savedRequests.clear();
    PartnerRequest pendingAgain = officeRequest(7L, this.officeInstitution, this.kenya);
    List<PartnerRequestOutcome> rejected =
      this.manager.rejectOfficeRequests(Arrays.asList(7L, 4L, 5L, 99L, COPY_ID), OFFICE_INSTITUTION_ID, "Not needed",
        user(1L));
    assertEquals(Status.WRONG_KIND, rejected.get(0).getStatus());
    assertEquals(4L, rejected.get(0).getRequestId());
    assertEquals(Status.ALREADY_HANDLED, rejected.get(1).getStatus());
    assertEquals(Status.DONE, rejected.get(2).getStatus());
    assertEquals(7L, rejected.get(2).getRequestId());
    assertEquals(Status.WRONG_KIND, rejected.get(3).getStatus());
    assertEquals(COPY_ID, rejected.get(3).getRequestId());
    assertEquals(Status.NOT_FOUND, rejected.get(4).getStatus());
    assertEquals(Arrays.asList(pendingAgain), this.savedRequests);
    assertNull(otherInstitution.getRejectJustification());
    assertNull(this.copy.getRejectJustification());
  }

  @Test
  public void officeRejectRecordsTheDecisionOnEveryPendingRequest() {
    PartnerRequest first = officeRequest(3L, this.officeInstitution, this.kenya);
    PartnerRequest second = officeRequest(9L, this.officeInstitution, this.ghana);
    User rejecter = user(77L);

    List<PartnerRequestOutcome> outcomes = this.manager.rejectOfficeRequests(Arrays.asList(3L, 9L),
      OFFICE_INSTITUTION_ID, "Country not served", rejecter);

    assertEquals(Status.DONE, outcomes.get(0).getStatus());
    assertEquals(Status.DONE, outcomes.get(1).getStatus());
    for (PartnerRequest request : Arrays.asList(first, second)) {
      assertEquals(Boolean.FALSE, request.getAcepted());
      assertEquals("Country not served", request.getRejectJustification());
      assertSame(rejecter, request.getRejectedBy());
      assertNotNull(request.getRejectedDate());
      assertFalse(request.isActive());
    }
    assertTrue(this.savedLocations.isEmpty());
  }

  // ---- create ----

  @Test
  public void createSavesTheOriginalThenTheLinkedCopy() {
    PartnerRequest newOriginal = request(40L, false);
    PartnerRequest newCopy = request(41L, false);
    newCopy.setModified(Boolean.TRUE);
    this.table.clear();

    PartnerRequest saved = this.manager.createPartnerRequest(newOriginal, newCopy);

    assertSame(newCopy, saved);
    assertSame(newOriginal, newCopy.getPartnerRequest());
    assertEquals(Boolean.FALSE, newCopy.getModified());
    assertEquals(Arrays.asList(newOriginal, newCopy), this.savedRequests);
    assertNull(newOriginal.getPartnerRequest());
    assertTrue(this.locks.isEmpty());
  }
}
