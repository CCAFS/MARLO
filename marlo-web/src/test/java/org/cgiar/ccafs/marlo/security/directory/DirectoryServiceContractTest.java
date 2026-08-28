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

package org.cgiar.ccafs.marlo.security.directory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The reusable behavioral contract for every {@link DirectoryService} implementation — design.md
 * §5.1 and §6.3, decision **DD-9**.
 * <p>
 * This class is **abstract and deliberately provider-agnostic**: it imports nothing from
 * {@code org.cgiar.ciat}, never references {@link DirectorySource#LDAP} directly, and knows nothing
 * about how a subclass simulates "the backend". A subclass supplies the seams below, and this class
 * supplies every assertion. Per DD-9, a future non-LDAP provider's contract test extends this class
 * **verbatim** and only implements the seams for its own backend.
 * <p>
 * Rows encoded (design §5.1's table): {@code null}/blank email, malformed email, well-formed email
 * with a match, well-formed email with no match, and a backend failure — the last split into its
 * well-formed and malformed variants because **DD-11** requires them to resolve differently
 * (malformed wins: {@code NOT_FOUND}, never {@code ERROR}).
 */
public abstract class DirectoryServiceContractTest {

  /** A well-formed email: single {@code @}, non-empty local part, domain containing a {@code .}. */
  protected static final String WELL_FORMED_EMAIL = "jane.smith@cgiar.org";

  /**
   * A malformed email per the minimal check DD-11 defines: an {@code @} is present, but the domain
   * part contains no {@code .} — the "admin typo" shape DD-11's own narrative describes.
   */
  protected static final String MALFORMED_EMAIL = "admin@typo";

  /** Mixed-case on purpose (DIRABS-FN-004): a lowercase fixture would pass whether or not the raw
   *  contract holds. */
  protected static final String RAW_LOGIN = "JSmith";

  protected static final String RAW_FIRST_NAME = "Jane";

  protected static final String RAW_LAST_NAME = "Smith";

  /**
   * @return a {@link DirectoryService} wired so that a well-formed lookup reaches the backend and the
   *         backend answers "no match" — no exception. Used for the absent-person row and for the
   *         malformed-email row, since neither depends on the backend throwing.
   */
  protected abstract DirectoryService createServiceWithNoMatch();

  /**
   * @param email the email the returned service will resolve
   * @param login the raw login the backend reports; must be echoed back untransformed
   * @param firstName the raw first name the backend reports
   * @param lastName the raw last name the backend reports
   * @return a {@link DirectoryService} wired so that looking up {@code email} finds a person whose
   *         fields equal {@code login}, {@code firstName}, {@code lastName} exactly, per
   *         DIRABS-FN-004's no-transformation rule
   */
  protected abstract DirectoryService createServiceWithFoundPerson(String email, String login, String firstName,
    String lastName);

  /**
   * @return the {@link DirectorySource} this provider reports for a person it found — {@code LDAP}
   *         for {@code LdapDirectoryService}; a future provider reports its own value here instead.
   *         This is the hook DD-9 requires so this class never hardcodes a provider-specific source.
   */
  protected abstract DirectorySource foundSource();

  /**
   * @return a {@link DirectoryService} wired so that **any** backend call throws, regardless of the
   *         requested email's shape. Used to prove: a {@code null}/blank email never reaches it; a
   *         well-formed email's failure surfaces as {@code ERROR}; and a malformed email's failure
   *         surfaces as {@code NOT_FOUND}, never {@code ERROR} (DD-11).
   */
  protected abstract DirectoryService createFailingService();

  /**
   * @return how many times the backend behind the {@link DirectoryService} most recently produced by
   *         {@link #createFailingService()} has actually been invoked. Must reflect only calls made
   *         since that factory call, so a fresh test method sees a fresh count.
   */
  protected abstract int failingServiceInvocationCount();

  @Test
  public void nullEmailIsNotFoundWithoutABackendCall() {
    DirectoryService service = this.createFailingService();

    DirectoryPerson person = service.findByEmail(null);

    assertNotNull("findByEmail must never return null", person);
    assertFalse(person.isFound());
    assertNotNull(person.getSource());
    assertEquals(DirectorySource.NOT_FOUND, person.getSource());
    assertEquals("a null email must make no network call", 0, this.failingServiceInvocationCount());
  }

  @Test
  public void blankEmailIsNotFoundWithoutABackendCall() {
    DirectoryService service = this.createFailingService();

    DirectoryPerson person = service.findByEmail("   ");

    assertNotNull("findByEmail must never return null", person);
    assertFalse(person.isFound());
    assertNotNull(person.getSource());
    assertEquals(DirectorySource.NOT_FOUND, person.getSource());
    assertEquals("a blank email must make no network call", 0, this.failingServiceInvocationCount());
  }

  @Test
  public void malformedEmailIsNotFoundWhenTheBackendAnswers() {
    DirectoryService service = this.createServiceWithNoMatch();

    DirectoryPerson person = service.findByEmail(MALFORMED_EMAIL);

    assertNotNull("findByEmail must never return null", person);
    assertFalse(person.isFound());
    assertNotNull(person.getSource());
    assertEquals(DirectorySource.NOT_FOUND, person.getSource());
  }

  @Test
  public void wellFormedEmailWithNoMatchIsNotFoundWithNullFields() {
    DirectoryService service = this.createServiceWithNoMatch();

    DirectoryPerson person = service.findByEmail(WELL_FORMED_EMAIL);

    assertNotNull("findByEmail must never return null", person);
    assertFalse(person.isFound());
    assertNotNull(person.getSource());
    assertEquals(DirectorySource.NOT_FOUND, person.getSource());
    assertNull("a not-found login must be null, not empty", person.getLogin());
    assertNull("a not-found first name must be null, not empty", person.getFirstName());
    assertNull("a not-found last name must be null, not empty", person.getLastName());
  }

  @Test
  public void foundPersonKeepsRawFieldsAndReportsTheProviderSource() {
    DirectoryService service =
      this.createServiceWithFoundPerson(WELL_FORMED_EMAIL, RAW_LOGIN, RAW_FIRST_NAME, RAW_LAST_NAME);

    DirectoryPerson person = service.findByEmail(WELL_FORMED_EMAIL);

    assertNotNull("findByEmail must never return null", person);
    assertTrue(person.isFound());
    assertNotNull(person.getSource());
    assertEquals(this.foundSource(), person.getSource());
    assertEquals("login must be raw, not transformed", RAW_LOGIN, person.getLogin());
    assertEquals(RAW_FIRST_NAME, person.getFirstName());
    assertEquals(RAW_LAST_NAME, person.getLastName());
  }

  @Test
  public void backendFailureOnAWellFormedEmailIsReportedAsError() {
    DirectoryService service = this.createFailingService();

    DirectoryPerson person = service.findByEmail(WELL_FORMED_EMAIL);

    assertNotNull("findByEmail must never return null", person);
    assertFalse(person.isFound());
    assertNotNull(person.getSource());
    assertEquals(DirectorySource.ERROR, person.getSource());
  }

  @Test
  public void backendFailureOnAMalformedEmailIsReportedAsNotFoundNeverError() {
    DirectoryService service = this.createFailingService();

    DirectoryPerson person = service.findByEmail(MALFORMED_EMAIL);

    assertNotNull("findByEmail must never return null", person);
    assertFalse(person.isFound());
    assertNotNull(person.getSource());
    assertEquals("DD-11: malformed input wins over a backend failure", DirectorySource.NOT_FOUND,
      person.getSource());
  }

}
