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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.json.superadmin.EditPartnerRequestAction;
import org.cgiar.ccafs.marlo.action.json.superadmin.RejectPartnerRequestAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.CountryOfficePOJO;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome.Status;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.EnvelopeConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.RecordingSendMail;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.Sent;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PARTREQ-001-T03: the POST guard and the email rules of the review actions of {@link PartnerRequestAction}
 * (SEC-002, FN-004, FN-005, FN-008, FN-010, OPS-001). The manager is a recording double: a test asserts how many
 * times it was called and with which arguments, so a guard that never reaches it cannot pass as a decision that
 * worked. The mail sender records the arguments of every send.
 */
public class PartnerRequestReviewGuardTest {

  private static final String NOTIFY = "notify@unit.test";
  private static final String APPROVER = "approver@unit.test";
  private static final String CDN = "https://cdn.test";
  private static final long INSTITUTION_ID = 500L;

  /** The partner request manager: every call recorded as "method(args)", answers from the scenario by method name. */
  private static final class ManagerDouble {

    final List<String> calls = new ArrayList<>();
    final List<Object[]> arguments = new ArrayList<>();
    final Map<String, Function<Object[], Object>> answers = new HashMap<>();

    PartnerRequestManager proxy() {
      return (PartnerRequestManager) Proxy.newProxyInstance(PartnerRequestManager.class.getClassLoader(),
        new Class<?>[] {PartnerRequestManager.class}, (proxy, method, args) -> {
          this.calls.add(method.getName());
          this.arguments.add(args);
          Function<Object[], Object> answer = this.answers.get(method.getName());
          if (answer == null) {
            throw new UnsupportedOperationException("PartnerRequestManager." + method.getName());
          }
          return answer.apply(args);
        });
    }
  }

  /** The action with everything session-bound answered by the scenario. */
  private static final class DrivenAction extends PartnerRequestAction {

    private static final long serialVersionUID = 1L;
    private final String httpMethod;
    private final Map<String, org.apache.struts2.dispatcher.Parameter> requestParameters;

    DrivenAction(PartnerRequestManager manager, GlobalUnitManager units, RecordingSendMail mail, String httpMethod,
      Map<String, org.apache.struts2.dispatcher.Parameter> requestParameters) {
      super(new EnvelopeConfig("pmu@unit.test", NOTIFY, "https://marlo.test"), manager, null, null, null, null,
        units, mail);
      this.httpMethod = httpMethod;
      this.requestParameters = requestParameters;
    }

    @Override
    public String getBaseUrlCdn() {
      return CDN;
    }

    @Override
    public User getCurrentUser() {
      return user(9L, "Admin", APPROVER);
    }

    @Override
    public Map<String, org.apache.struts2.dispatcher.Parameter> getParameters() {
      return this.requestParameters;
    }

    @Override
    public HttpServletRequest getRequest() {
      return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
        new Class<?>[] {HttpServletRequest.class}, (proxy, method, args) -> {
          if (method.getName().equals("getMethod")) {
            return this.httpMethod;
          }
          throw new UnsupportedOperationException("HttpServletRequest." + method.getName());
        });
    }

    /** The messages are asserted by key: the texts of the page are the business of the page task. */
    @Override
    public String getText(String key) {
      return key;
    }

    @Override
    public String getText(String key, String[] args) {
      return key;
    }
  }

  private ManagerDouble manager;
  private RecordingSendMail mail;
  private Map<Long, GlobalUnit> units;
  private GlobalUnit unit;
  private Institution institution;
  private Logger actionLogger;
  private Logger editLogger;
  private Logger rejectLogger;
  private Level originalLevel;
  private Level editOriginalLevel;
  private Level rejectOriginalLevel;
  private ListAppender<ILoggingEvent> logged;

  private static User user(Long id, String first, String email) {
    User user = new User();
    user.setId(id);
    user.setFirstName(first);
    user.setLastName("Test");
    user.setEmail(email);
    return user;
  }

  private static GlobalUnit unit(long id, boolean notificationsOn) {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(id);
    unit.setAcronym("U" + id);
    if (notificationsOn) {
      unit.setCustomParameters(Collections.emptySet());
    } else {
      Parameter parameter = new Parameter();
      parameter.setKey(APConstants.CRP_EMAIL_NOTIFICATIONS);
      CustomParameter off = new CustomParameter();
      off.setParameter(parameter);
      off.setValue("false");
      unit.setCustomParameters(Collections.singleton(off));
    }
    return unit;
  }

  private static PartnerRequest request(long id, GlobalUnit unit, User creator, String country, String partnerName) {
    PartnerRequest request = new PartnerRequest();
    request.setId(id);
    request.setCrp(unit);
    request.setCreatedBy(creator);
    request.setPartnerName(partnerName);
    LocElement location = new LocElement();
    location.setName(country);
    request.setLocElement(location);
    return request;
  }

  @Before
  public void setUp() {
    this.manager = new ManagerDouble();
    this.mail = new RecordingSendMail();
    this.units = new HashMap<>();
    this.unit = unit(1L, true);
    this.units.put(1L, this.unit);
    this.units.put(2L, unit(2L, false));
    this.institution = new Institution();
    this.institution.setId(INSTITUTION_ID);
    this.institution.setAcronym("ACR");
    this.institution.setName("Acme Institute");
    this.actionLogger = (Logger) LoggerFactory.getLogger(PartnerRequestAction.class);
    this.originalLevel = this.actionLogger.getLevel();
    this.actionLogger.setLevel(Level.TRACE);
    this.logged = new ListAppender<>();
    this.logged.start();
    this.actionLogger.addAppender(this.logged);
    this.editLogger = (Logger) LoggerFactory.getLogger(EditPartnerRequestAction.class);
    this.editOriginalLevel = this.editLogger.getLevel();
    this.editLogger.setLevel(Level.TRACE);
    this.editLogger.addAppender(this.logged);
    this.rejectLogger = (Logger) LoggerFactory.getLogger(RejectPartnerRequestAction.class);
    this.rejectOriginalLevel = this.rejectLogger.getLevel();
    this.rejectLogger.setLevel(Level.TRACE);
    this.rejectLogger.addAppender(this.logged);
  }

  @After
  public void tearDown() {
    this.actionLogger.detachAppender(this.logged);
    this.actionLogger.setLevel(this.originalLevel);
    this.editLogger.detachAppender(this.logged);
    this.editLogger.setLevel(this.editOriginalLevel);
    this.rejectLogger.detachAppender(this.logged);
    this.rejectLogger.setLevel(this.rejectOriginalLevel);
  }

  /** The one logged line at the level that contains the needle; fails when there is none or more than one. */
  private String loggedLine(Level level, String needle) {
    List<String> matches = new ArrayList<>();
    for (ILoggingEvent event : this.logged.list) {
      if (event.getLevel() == level && event.getFormattedMessage().contains(needle)) {
        matches.add(event.getFormattedMessage());
      }
    }
    assertEquals("expected exactly one " + level + " line with '" + needle + "' in " + this.logged.list, 1,
      matches.size());
    return matches.get(0);
  }

  private DrivenAction action(String httpMethod) {
    return this.action(httpMethod, new HashMap<>());
  }

  private DrivenAction action(String httpMethod, Map<String, org.apache.struts2.dispatcher.Parameter> parameters) {
    GlobalUnitManager unitManager =
      EmailSendDoubles.answering(GlobalUnitManager.class, "getGlobalUnitById", args -> this.units.get(args[0]));
    return new DrivenAction(this.manager.proxy(), unitManager, this.mail, httpMethod, parameters);
  }

  private static CountryOfficePOJO officeSelection(Institution institution, String ids) {
    CountryOfficePOJO selection = new CountryOfficePOJO();
    selection.setInstitution(institution);
    selection.setIds(ids);
    return selection;
  }

  private static Map<String, org.apache.struts2.dispatcher.Parameter> parameters(String justification,
    String notify) {
    Map<String, org.apache.struts2.dispatcher.Parameter> parameters = new HashMap<>();
    parameters.put(APConstants.JUSTIFICATION_REQUEST,
      new org.apache.struts2.dispatcher.Parameter.Request(APConstants.JUSTIFICATION_REQUEST, justification));
    parameters.put(APConstants.PARTNER_REQUEST_SEND_NOTIFICATION,
      new org.apache.struts2.dispatcher.Parameter.Request(APConstants.PARTNER_REQUEST_SEND_NOTIFICATION, notify));
    return parameters;
  }

  private static void assertAllStrings(Map<String, String> outcomes) {
    for (Object value : ((Map<?, ?>) outcomes).values()) {
      assertTrue("outcomes must hold strings only, but held " + value.getClass(), value instanceof String);
    }
  }

  // ---- A GET writes nothing (SEC-002) ------------------------------------------------------------------------

  @Test
  public void testGetToAddPartnerNeverReachesTheManager() {
    DrivenAction action = this.action("GET");
    action.setRequestID(20L);

    assertEquals("success", action.addPartner());

    assertEquals("the manager must not be called on a GET", Collections.emptyList(), this.manager.calls);
    assertEquals("notAllowed", action.getOutcome());
    assertEquals(Arrays.asList("marloRequestInstitution.ui.outcome.notAllowed"),
      new ArrayList<>(action.getActionErrors()));
    assertTrue("a GET must not send mail", this.mail.sent.isEmpty());
  }

  @Test
  public void testGetToTheOfficeActionsNeverReachesTheManager() {
    DrivenAction accept = this.action("GET");
    accept.setCountryOfficePOJO(officeSelection(this.institution, "31,32"));
    assertEquals("success", accept.addCountryOffices());

    DrivenAction reject = this.action("GET", parameters("No reason", "true"));
    reject.setCountryOfficePOJO(officeSelection(this.institution, "31,32"));
    assertEquals("success", reject.rejectCountryOffices());

    assertEquals("the manager must not be called on a GET", Collections.emptyList(), this.manager.calls);
    for (DrivenAction action : Arrays.asList(accept, reject)) {
      assertFalse(action.isSuccess());
      assertEquals("notAllowed", action.getOutcome());
      assertTrue(action.getOutcomes().isEmpty());
    }
    assertTrue("a GET must not send mail", this.mail.sent.isEmpty());
  }

  // ---- Accept partner: one email, only for DONE (FN-004, FN-005) ---------------------------------------------

  @Test
  public void testAlreadyHandledAcceptSendsNoEmailAndTellsTheSuperadmin() {
    PartnerRequest copy = request(20L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme Seeds Ltd");
    this.manager.answers.put("acceptPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], Status.ALREADY_HANDLED, copy, null));
    DrivenAction action = this.action("POST");
    action.setRequestID(20L);

    assertEquals("success", action.addPartner());

    assertEquals(Arrays.asList("acceptPartnerRequest"), this.manager.calls);
    assertEquals("the manager must receive the id of the page", 20L, this.manager.arguments.get(0)[0]);
    assertEquals("alreadyHandled", action.getOutcome());
    assertEquals(Arrays.asList("marloRequestInstitution.ui.outcome.alreadyHandled"),
      new ArrayList<>(action.getActionErrors()));
    assertTrue("a request that was already handled must not be mailed again", this.mail.sent.isEmpty());
  }

  @Test
  public void testAcceptedPartnerMailsTheRequesterOnceWithTheNewInstitutionName() {
    PartnerRequest copy = request(20L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme Seeds Ltd");
    this.manager.answers.put("acceptPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], Status.DONE, copy, this.institution));
    DrivenAction action = this.action("POST");
    action.setRequestID(20L);

    assertEquals("success", action.addPartner());

    assertEquals("done", action.getOutcome());
    assertEquals(Arrays.asList("marloRequestInstitution.ui.outcome.done"),
      new ArrayList<>(action.getActionMessages()));
    assertEquals(1, this.mail.sent.size());
    Sent sent = this.mail.sent.get(0);
    assertEquals("ana@unit.test", sent.to);
    assertEquals(APPROVER, sent.cc);
    assertEquals(NOTIFY, sent.bcc);
    assertEquals("[MARLO] Partner verification - Approved Acme Seeds Ltd", sent.subject);
    assertTrue(sent.body, sent.body.contains("Dear Ana,"));
    assertTrue(sent.body, sent.body.contains("ACR - Acme Institute"));
    assertTrue(sent.html);
  }

  @Test
  public void testExternalRequesterIsMailedAndTheCreatorIsCopied() {
    PartnerRequest copy = request(20L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme Seeds Ltd");
    copy.setExternalUserMail("clarisa@external.test");
    copy.setExternalUserName("Clara");
    this.manager.answers.put("acceptPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], Status.DONE, copy, this.institution));
    DrivenAction action = this.action("POST");
    action.setRequestID(20L);

    action.addPartner();

    assertEquals(1, this.mail.sent.size());
    Sent sent = this.mail.sent.get(0);
    assertEquals("clarisa@external.test", sent.to);
    assertEquals(APPROVER + ", ana@unit.test", sent.cc);
    assertTrue(sent.body, sent.body.contains("Dear Clara,"));
  }

  @Test
  public void testSwitchOffSendsNothingEvenWhenTheDecisionWasApplied() {
    PartnerRequest copy = request(20L, this.units.get(2L), user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme");
    this.manager.answers.put("acceptPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], Status.DONE, copy, this.institution));
    DrivenAction action = this.action("POST");
    action.setRequestID(20L);

    action.addPartner();

    assertEquals(Arrays.asList("acceptPartnerRequest"), this.manager.calls);
    assertEquals("done", action.getOutcome());
    assertTrue("the unit's notification switch is off", this.mail.sent.isEmpty());
  }

  @Test
  public void testAcceptThatThrowsIsReportedAsFailedAndSendsNothing() {
    this.manager.answers.put("acceptPartnerRequest", args -> {
      throw new IllegalStateException("copy has no country");
    });
    DrivenAction action = this.action("POST");
    action.setRequestID(20L);

    assertEquals("success", action.addPartner());

    assertEquals(Arrays.asList("acceptPartnerRequest"), this.manager.calls);
    assertEquals("failed", action.getOutcome());
    assertEquals(Arrays.asList("marloRequestInstitution.ui.outcome.failed"),
      new ArrayList<>(action.getActionErrors()));
    assertTrue(this.mail.sent.isEmpty());
  }

  // ---- Offices (FN-008) --------------------------------------------------------------------------------------

  private List<PartnerRequestOutcome> officeResults() {
    PartnerRequest done = request(31L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "x");
    PartnerRequest skipped = request(32L, this.unit, user(12L, "Bruno", "bruno@unit.test"), "Ghana", "x");
    PartnerRequest handled = request(33L, this.unit, user(13L, "Carla", "carla@unit.test"), "Peru", "x");
    return Arrays.asList(new PartnerRequestOutcome(31L, Status.DONE, done, this.institution),
      new PartnerRequestOutcome(32L, Status.SKIPPED_EXISTING, skipped, this.institution),
      new PartnerRequestOutcome(33L, Status.ALREADY_HANDLED, handled, null),
      new PartnerRequestOutcome(34L, Status.NOT_FOUND, null, null));
  }

  @Test
  public void testAcceptOfficesMailsDoneAndSkippedExistingRequestersOnlyAndPublishesStrings() {
    this.manager.answers.put("acceptOfficeRequests", args -> this.officeResults());
    DrivenAction action = this.action("POST");
    action.setCountryOfficePOJO(officeSelection(this.institution, "31, 32,33,34"));

    assertEquals("success", action.addCountryOffices());

    assertEquals(Arrays.asList("acceptOfficeRequests"), this.manager.calls);
    assertEquals(Arrays.asList(31L, 32L, 33L, 34L), this.manager.arguments.get(0)[0]);
    assertEquals(INSTITUTION_ID, this.manager.arguments.get(0)[1]);
    assertAllStrings(action.getOutcomes());
    Map<String, String> expected = new LinkedHashMap<>();
    expected.put("31", "done");
    expected.put("32", "skippedExisting");
    expected.put("33", "alreadyHandled");
    expected.put("34", "notFound");
    assertEquals(expected, action.getOutcomes());
    assertEquals("done", action.getOutcome());
    assertTrue(action.isSuccess());

    assertEquals("one email per notified requester, none for the refused ones", 2, this.mail.sent.size());
    Sent first = this.mail.sent.get(0);
    Sent second = this.mail.sent.get(1);
    assertEquals("ana@unit.test", first.to);
    assertEquals("bruno@unit.test", second.to);
    for (Sent sent : this.mail.sent) {
      assertEquals(APPROVER, sent.cc);
      assertEquals(NOTIFY, sent.bcc);
      assertEquals("[MARLO] Country office(s) verification - Approved ACR - Acme Institute", sent.subject);
      assertTrue(sent.body, sent.body.contains("Kenya, Ghana"));
      assertFalse("the refused request's country is not announced", sent.body.contains("Peru"));
    }
    assertTrue(first.body, first.body.contains("Dear Ana,"));
    assertTrue(second.body, second.body.contains("Dear Bruno,"));
  }

  @Test
  public void testAcceptOfficesThatThrowsPublishesFailedAndSendsNothing() {
    this.manager.answers.put("acceptOfficeRequests", args -> {
      throw new IllegalStateException("boom");
    });
    DrivenAction action = this.action("POST");
    action.setCountryOfficePOJO(officeSelection(this.institution, "31"));

    assertEquals("success", action.addCountryOffices());

    assertEquals(Arrays.asList("acceptOfficeRequests"), this.manager.calls);
    assertFalse(action.isSuccess());
    assertEquals("failed", action.getOutcome());
    assertTrue(action.getOutcomes().isEmpty());
    assertTrue(this.mail.sent.isEmpty());
  }

  @Test
  public void testMissingSelectionIsCheckedBeforeAnythingIsUsed() {
    DrivenAction withoutSelection = this.action("POST");
    assertEquals("success", withoutSelection.addCountryOffices());

    DrivenAction withoutInstitution = this.action("POST", parameters("Reason", "true"));
    withoutInstitution.setCountryOfficePOJO(officeSelection(null, "31"));
    assertEquals("success", withoutInstitution.rejectCountryOffices());

    assertEquals("nothing to decide, so the manager is not called", Collections.emptyList(), this.manager.calls);
    for (DrivenAction action : Arrays.asList(withoutSelection, withoutInstitution)) {
      assertFalse(action.isSuccess());
      assertEquals("failed", action.getOutcome());
    }
    assertTrue(this.mail.sent.isEmpty());
  }

  @Test
  public void testRejectOfficesPassesTheJustificationAndUserAndMailsOnlyWhenAsked() {
    PartnerRequest done = request(31L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "x");
    this.manager.answers.put("rejectOfficeRequests",
      args -> Arrays.asList(new PartnerRequestOutcome(31L, Status.DONE, done, this.institution)));

    DrivenAction quiet = this.action("POST", parameters("Not a real office", "false"));
    quiet.setCountryOfficePOJO(officeSelection(this.institution, "31"));
    quiet.rejectCountryOffices();
    assertTrue("no notification was asked for", this.mail.sent.isEmpty());

    DrivenAction notifying = this.action("POST", parameters("Not a real office", "true"));
    notifying.setCountryOfficePOJO(officeSelection(this.institution, "31"));
    notifying.rejectCountryOffices();

    assertEquals(Arrays.asList("rejectOfficeRequests", "rejectOfficeRequests"), this.manager.calls);
    Object[] args = this.manager.arguments.get(1);
    assertEquals(Arrays.asList(31L), args[0]);
    assertEquals(INSTITUTION_ID, args[1]);
    assertEquals("Not a real office", args[2]);
    assertEquals(9L, ((User) args[3]).getId().longValue());
    assertEquals("done", notifying.getOutcome());
    assertEquals(1, this.mail.sent.size());
    Sent sent = this.mail.sent.get(0);
    assertEquals("ana@unit.test", sent.to);
    assertEquals("[MARLO] Country office(s) verification - Rejected ACR - Acme Institute", sent.subject);
    assertTrue(sent.body, sent.body.contains("Not a real office"));
    assertTrue(sent.body, sent.body.contains("Kenya"));
  }

  @Test
  public void testRejectOfficesRefusedByTheManagerSendsNothing() {
    PartnerRequest handled = request(31L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "x");
    this.manager.answers.put("rejectOfficeRequests",
      args -> Arrays.asList(new PartnerRequestOutcome(31L, Status.ALREADY_HANDLED, handled, null)));
    DrivenAction action = this.action("POST", parameters("Reason", "true"));
    action.setCountryOfficePOJO(officeSelection(this.institution, "31"));

    action.rejectCountryOffices();

    assertEquals(Arrays.asList("rejectOfficeRequests"), this.manager.calls);
    assertEquals("alreadyHandled", action.getOutcome());
    assertFalse("nothing was applied", action.isSuccess());
    assertTrue(this.mail.sent.isEmpty());
  }

  // ---- Failure lines name the action and the request ids (OPS-001) -------------------------------------------

  @Test
  public void testFailedAcceptOfficesLogsTheActionTheRequestIdsAndTheInstitution() {
    this.manager.answers.put("acceptOfficeRequests", args -> {
      throw new IllegalStateException("boom");
    });
    DrivenAction action = this.action("POST");
    action.setCountryOfficePOJO(officeSelection(this.institution, "31, 32"));

    action.addCountryOffices();

    assertEquals(Arrays.asList("acceptOfficeRequests"), this.manager.calls);
    assertEquals("failed", action.getOutcome());
    assertEquals("addCountryOffices: could not accept the office requests 31, 32 of institution 500",
      this.loggedLine(Level.ERROR, "addCountryOffices"));
  }

  @Test
  public void testFailedRejectOfficesLogsTheActionTheRequestIdsAndTheInstitution() {
    this.manager.answers.put("rejectOfficeRequests", args -> {
      throw new IllegalStateException("boom");
    });
    DrivenAction action = this.action("POST", parameters("Reason", "true"));
    action.setCountryOfficePOJO(officeSelection(this.institution, "31,32"));

    action.rejectCountryOffices();

    assertEquals(Arrays.asList("rejectOfficeRequests"), this.manager.calls);
    assertEquals("failed", action.getOutcome());
    assertEquals("rejectCountryOffices: could not reject the office requests 31,32 of institution 500",
      this.loggedLine(Level.ERROR, "rejectCountryOffices"));
  }

  @Test
  public void testUnparsableIdsAreLoggedRawWithoutLineBreaks() {
    DrivenAction action = this.action("POST");
    action.setCountryOfficePOJO(officeSelection(this.institution, "31,x\nFAKE"));

    action.addCountryOffices();

    assertTrue("the manager is not reached when the ids cannot be read", this.manager.calls.isEmpty());
    assertEquals("failed", action.getOutcome());
    assertEquals("addCountryOffices: could not accept the office requests 31,x FAKE of institution 500",
      this.loggedLine(Level.ERROR, "addCountryOffices"));
  }

  @Test
  public void testMissingInstitutionWarningNamesTheActionAndTheIdsThatWereSent() {
    DrivenAction action = this.action("POST", parameters("Reason", "true"));
    action.setCountryOfficePOJO(officeSelection(null, "31"));

    action.rejectCountryOffices();

    assertEquals("rejectCountryOffices: nothing decided, the posted selection has no institution (request ids 31)",
      this.loggedLine(Level.WARN, "rejectCountryOffices"));
  }

  @Test
  public void testManagerReturningNoOutcomeIsReportedAsFailed() {
    this.manager.answers.put("acceptPartnerRequest", args -> null);
    DrivenAction action = this.action("POST");
    action.setRequestID(20L);

    assertEquals("success", action.addPartner());

    assertEquals("failed", action.getOutcome());
    assertTrue(this.mail.sent.isEmpty());
    this.loggedLine(Level.ERROR, "Could not accept partner request 20");
  }

  @Test
  public void testExternalRequesterWithoutNameIsGreetedByTheCreatorsFirstName() {
    PartnerRequest copy = request(20L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme Seeds Ltd");
    copy.setExternalUserMail("clarisa@external.test");
    this.manager.answers.put("acceptPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], Status.DONE, copy, this.institution));
    DrivenAction action = this.action("POST");
    action.setRequestID(20L);

    action.addPartner();

    assertEquals(1, this.mail.sent.size());
    assertTrue(this.mail.sent.get(0).body, this.mail.sent.get(0).body.contains("Dear Ana,"));
  }

  // ---- Edit and reject JSON actions (CHG-PARTREQ-001-T05: FN-006, FN-007, SEC-002, OPS-001) -------------------------

  private static HttpServletRequest requestOf(String httpMethod) {
    return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
      new Class<?>[] {HttpServletRequest.class}, (proxy, method, args) -> {
        if (method.getName().equals("getMethod")) {
          return httpMethod;
        }
        throw new UnsupportedOperationException("HttpServletRequest." + method.getName());
      });
  }

  /** The posted fields of the review page, one value per name. */
  private static Map<String, org.apache.struts2.dispatcher.Parameter> posted(String... namesAndValues) {
    Map<String, org.apache.struts2.dispatcher.Parameter> parameters = new HashMap<>();
    for (int i = 0; i < namesAndValues.length; i += 2) {
      parameters.put(namesAndValues[i],
        new org.apache.struts2.dispatcher.Parameter.Request(namesAndValues[i], namesAndValues[i + 1]));
    }
    return parameters;
  }

  private static Map<String, org.apache.struts2.dispatcher.Parameter> editFields(String requestId) {
    return posted(APConstants.PARTNER_REQUEST_ID, requestId, APConstants.INSTITUTION_NAME, " New Name ",
      "institutionAcronym", "NN", "institutionWebPage", "https://new.test", APConstants.INSTITUTION_TYPE_REQUEST_ID,
      "7", APConstants.COUNTRY_REQUEST_ID, "KE", APConstants.JUSTIFICATION_REQUEST, "Fixed the name");
  }

  private static Map<String, org.apache.struts2.dispatcher.Parameter> rejectFields(String requestId,
    String notify) {
    return posted(APConstants.PARTNER_REQUEST_ID, requestId, APConstants.JUSTIFICATION_REQUEST, "Not an institution",
      APConstants.PARTNER_REQUEST_SEND_NOTIFICATION, notify);
  }

  private final class DrivenEdit extends EditPartnerRequestAction {

    private static final long serialVersionUID = 1L;
    private final String httpMethod;
    private final Map<String, org.apache.struts2.dispatcher.Parameter> requestParameters;

    DrivenEdit(String httpMethod, Map<String, org.apache.struts2.dispatcher.Parameter> requestParameters) {
      super(new EnvelopeConfig("pmu@unit.test", NOTIFY, "https://marlo.test"),
        PartnerRequestReviewGuardTest.this.manager.proxy(),
        EmailSendDoubles.answering(InstitutionTypeManager.class, "none", args -> null),
        EmailSendDoubles.answering(LocElementManager.class, "none", args -> null));
      this.httpMethod = httpMethod;
      this.requestParameters = requestParameters;
    }

    @Override
    public Map<String, org.apache.struts2.dispatcher.Parameter> getParameters() {
      return this.requestParameters;
    }

    @Override
    public HttpServletRequest getRequest() {
      return requestOf(this.httpMethod);
    }
  }

  private final class DrivenReject extends RejectPartnerRequestAction {

    private static final long serialVersionUID = 1L;
    private final String httpMethod;
    private final Map<String, org.apache.struts2.dispatcher.Parameter> requestParameters;

    DrivenReject(String httpMethod, Map<String, org.apache.struts2.dispatcher.Parameter> requestParameters) {
      super(new EnvelopeConfig("pmu@unit.test", NOTIFY, "https://marlo.test"),
        PartnerRequestReviewGuardTest.this.manager.proxy(),
        EmailSendDoubles.answering(GlobalUnitManager.class, "getGlobalUnitById",
          args -> PartnerRequestReviewGuardTest.this.units.get(args[0])),
        PartnerRequestReviewGuardTest.this.mail);
      this.httpMethod = httpMethod;
      this.requestParameters = requestParameters;
    }

    @Override
    public String getBaseUrlCdn() {
      return CDN;
    }

    @Override
    public User getCurrentUser() {
      return user(9L, "Admin", APPROVER);
    }

    @Override
    public Map<String, org.apache.struts2.dispatcher.Parameter> getParameters() {
      return this.requestParameters;
    }

    @Override
    public HttpServletRequest getRequest() {
      return requestOf(this.httpMethod);
    }
  }

  private void answerReject(Status status) {
    PartnerRequest copy = request(20L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme Seeds Ltd");
    this.manager.answers.put("rejectPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], status, copy, null));
  }

  @Test
  public void testGetToEditAndRejectNeverReachesTheManager() throws Exception {
    DrivenEdit edit = new DrivenEdit("GET", editFields("20"));
    edit.prepare();
    assertEquals("success", edit.execute());

    DrivenReject reject = new DrivenReject("GET", rejectFields("20", "true"));
    reject.prepare();
    assertEquals("success", reject.execute());

    assertEquals("the manager must not be called on a GET", Collections.emptyList(), this.manager.calls);
    assertFalse(edit.isSuccess());
    assertEquals("notAllowed", edit.getOutcome());
    assertFalse(reject.isSuccess());
    assertEquals("notAllowed", reject.getOutcome());
    assertTrue("a GET must not send mail", this.mail.sent.isEmpty());
    this.loggedLine(Level.WARN, "editPartnerRequest refused for request 20: not an HTTP POST");
    this.loggedLine(Level.WARN, "rejectPartnerRequest refused for request 20: not an HTTP POST");
  }

  @Test
  public void testEditPassesThePostedFieldsToTheManagerAndReportsDone() throws Exception {
    PartnerRequest copy = request(20L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme Seeds Ltd");
    this.manager.answers.put("editPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], Status.DONE, copy, null));
    DrivenEdit edit = new DrivenEdit("POST", editFields("20"));
    edit.prepare();

    assertEquals("success", edit.execute());

    assertEquals(Arrays.asList("editPartnerRequest"), this.manager.calls);
    assertEquals(Arrays.<Object>asList(20L, "New Name", "NN", "https://new.test", 7L, "KE", "Fixed the name"),
      Arrays.asList(this.manager.arguments.get(0)));
    assertTrue(edit.isSuccess());
    assertEquals("done", edit.getOutcome());
  }

  @Test
  public void testEditOfARequestThatIsNoLongerPendingReportsAlreadyHandled() throws Exception {
    PartnerRequest copy = request(20L, this.unit, user(11L, "Ana", "ana@unit.test"), "Kenya", "Acme Seeds Ltd");
    this.manager.answers.put("editPartnerRequest",
      args -> new PartnerRequestOutcome((Long) args[0], Status.ALREADY_HANDLED, copy, null));
    DrivenEdit edit = new DrivenEdit("POST", editFields("20"));
    edit.prepare();

    assertEquals("success", edit.execute());

    assertEquals("the manager decides, once", Arrays.asList("editPartnerRequest"), this.manager.calls);
    assertEquals("alreadyHandled", edit.getOutcome());
    assertFalse(edit.isSuccess());
    this.loggedLine(Level.WARN, "editPartnerRequest: request 20 was not edited: ALREADY_HANDLED");
  }

  @Test
  public void testEditThatThrowsIsReportedAsFailedAndLogged() throws Exception {
    this.manager.answers.put("editPartnerRequest", args -> {
      throw new IllegalStateException("db down");
    });
    DrivenEdit edit = new DrivenEdit("POST", editFields("20"));
    edit.prepare();

    assertEquals("success", edit.execute());

    assertEquals(Arrays.asList("editPartnerRequest"), this.manager.calls);
    assertEquals("failed", edit.getOutcome());
    assertFalse(edit.isSuccess());
    this.loggedLine(Level.ERROR, "editPartnerRequest: could not edit partner request 20");
  }

  @Test
  public void testRejectWithSendNotificationUncheckedSendsNothing() throws Exception {
    this.answerReject(Status.DONE);
    DrivenReject reject = new DrivenReject("POST", rejectFields("20", "false"));
    reject.prepare();

    assertEquals("success", reject.execute());

    assertEquals(Arrays.asList("rejectPartnerRequest"), this.manager.calls);
    assertEquals(20L, this.manager.arguments.get(0)[0]);
    assertEquals("Not an institution", this.manager.arguments.get(0)[1]);
    assertEquals(APPROVER, ((User) this.manager.arguments.get(0)[2]).getEmail());
    assertTrue(reject.isSuccess());
    assertEquals("done", reject.getOutcome());
    assertTrue("send notification was unchecked", this.mail.sent.isEmpty());
  }

  @Test
  public void testRejectWithSendNotificationMailsTheRequesterOnceWithTheJustification() throws Exception {
    this.answerReject(Status.DONE);
    DrivenReject reject = new DrivenReject("POST", rejectFields("20", "true"));
    reject.prepare();

    assertEquals("success", reject.execute());

    assertEquals(Arrays.asList("rejectPartnerRequest"), this.manager.calls);
    assertEquals(1, this.mail.sent.size());
    Sent sent = this.mail.sent.get(0);
    assertEquals("ana@unit.test", sent.to);
    assertEquals(APPROVER, sent.cc);
    assertEquals(NOTIFY, sent.bcc);
    assertEquals("[MARLO] Partner verification - Rejected Acme Seeds Ltd", sent.subject);
    assertTrue(sent.body, sent.body.contains("Dear Ana,"));
    assertTrue(sent.body, sent.body.contains("Not an institution"));
    assertTrue(sent.html);
  }

  @Test
  public void testRejectOfARequestAlreadyHandledSendsNoEmailEvenWhenAskedTo() throws Exception {
    this.answerReject(Status.ALREADY_HANDLED);
    DrivenReject reject = new DrivenReject("POST", rejectFields("20", "true"));
    reject.prepare();

    assertEquals("success", reject.execute());

    assertEquals(Arrays.asList("rejectPartnerRequest"), this.manager.calls);
    assertEquals("alreadyHandled", reject.getOutcome());
    assertFalse(reject.isSuccess());
    assertTrue("nothing was rejected, so nobody is told so", this.mail.sent.isEmpty());
  }

  @Test
  public void testRejectThatThrowsIsFailedLoggedWithoutTheRecipientAndSendsNothing() throws Exception {
    this.manager.answers.put("rejectPartnerRequest", args -> {
      throw new IllegalStateException("db down");
    });
    DrivenReject reject = new DrivenReject("POST", rejectFields("20", "true"));
    reject.prepare();

    assertEquals("success", reject.execute());

    assertEquals("failed", reject.getOutcome());
    assertFalse(reject.isSuccess());
    assertTrue(this.mail.sent.isEmpty());
    this.loggedLine(Level.ERROR, "rejectPartnerRequest: could not reject partner request 20");
    for (ILoggingEvent event : this.logged.list) {
      assertFalse("no address in the logs", event.getFormattedMessage().contains("@"));
    }
  }
}
