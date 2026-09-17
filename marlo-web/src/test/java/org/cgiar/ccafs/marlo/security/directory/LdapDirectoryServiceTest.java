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

import org.cgiar.ccafs.marlo.security.directory.impl.LdapDirectoryService;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Runs {@link DirectoryServiceContractTest}'s full contract against {@link LdapDirectoryService},
 * plus LDAP-specific assertions the abstract contract cannot express.
 * <p>
 * Substitution mechanism (**DD-12**): every factory method below returns an
 * {@link LdapDirectoryService} whose {@link LdapDirectoryService#newLdapService()} is overridden to
 * return a test-local {@link LDAPService} stub. Everything else in {@link LdapDirectoryService} —
 * the null/blank fail-fast, {@code setInternalConnection(!isProduction())}, the raw field mapping,
 * the DD-11 catch discrimination, and {@code isWellFormed} — stays real production code. Only the
 * backend call, which cannot run against real {@code adauth} in this environment, is substituted.
 * {@code LDAPService}'s {@code super()} is provably non-throwing (design.md DD-12), so extending it
 * from a stub is safe.
 */
public class LdapDirectoryServiceTest extends DirectoryServiceContractTest {

  private int invocationCount;

  @Override
  protected DirectoryService createServiceWithNoMatch() {
    return new StubbedLdapDirectoryService(new NoMatchLdapService());
  }

  @Override
  protected DirectoryService createServiceWithFoundPerson(String email, String login, String firstName,
    String lastName) {
    return new StubbedLdapDirectoryService(new FoundLdapService(login, email, firstName, lastName));
  }

  @Override
  protected DirectorySource foundSource() {
    return DirectorySource.LDAP;
  }

  @Override
  protected DirectoryService createFailingService() {
    this.invocationCount = 0;
    return new StubbedLdapDirectoryService(new ThrowingLdapService());
  }

  @Override
  protected int failingServiceInvocationCount() {
    return this.invocationCount;
  }

  /**
   * FN-005 *Found*: {@code setInternalConnection(!config.isProduction())} is applied before the
   * search. {@link APConfig#isProduction()} answers {@code false} when unconfigured (its own
   * fail-safe default), so the seam must receive {@code true}.
   */
  @Test
  public void appliesInternalConnectionFromConfigBeforeSearching() {
    RecordingLdapService stub = new RecordingLdapService();
    LdapDirectoryService service = new StubbedLdapDirectoryService(stub);

    service.findByEmail(WELL_FORMED_EMAIL);

    assertEquals(Boolean.TRUE, stub.internalConnectionReceived);
  }

  /** A backend that never matches anyone. */
  private static final class NoMatchLdapService extends LDAPService {

    @Override
    public LDAPUser searchUserByEmail(String email) {
      return null;
    }

  }

  /** A backend that returns one canned, untransformed person for every lookup. */
  private static final class FoundLdapService extends LDAPService {

    private final LDAPUser canned;

    FoundLdapService(String login, String email, String firstName, String lastName) {
      this.canned = new LDAPUser(login, email, null, firstName, null, lastName, null);
    }

    @Override
    public LDAPUser searchUserByEmail(String email) {
      return this.canned;
    }

  }

  /** A backend that always throws, counting how many times it was actually invoked. */
  private final class ThrowingLdapService extends LDAPService {

    @Override
    public LDAPUser searchUserByEmail(String email) {
      LdapDirectoryServiceTest.this.invocationCount++;
      throw new IllegalStateException("simulated directory backend failure");
    }

  }

  /** A backend that records the flag it receives, so the config-mapping test can inspect it. */
  private static final class RecordingLdapService extends LDAPService {

    private Boolean internalConnectionReceived;

    @Override
    public void setInternalConnection(boolean internalConnection) {
      this.internalConnectionReceived = internalConnection;
      super.setInternalConnection(internalConnection);
    }

    @Override
    public LDAPUser searchUserByEmail(String email) {
      return null;
    }

  }

  /** Substitutes {@link LdapDirectoryService#newLdapService()} with the given stub (DD-12). */
  private static final class StubbedLdapDirectoryService extends LdapDirectoryService {

    private final LDAPService stub;

    StubbedLdapDirectoryService(LDAPService stub) {
      super(new APConfig());
      this.stub = stub;
    }

    @Override
    protected LDAPService newLdapService() {
      return this.stub;
    }

  }

}
