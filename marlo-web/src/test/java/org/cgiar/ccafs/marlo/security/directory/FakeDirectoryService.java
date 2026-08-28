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

/**
 * Hand-rolled {@link DirectoryService} double for consumer tests (DIRABS-T06 .. T09). It is a real
 * implementation of the interface — not a stub of {@code LDAPService} — so it stays usable regardless
 * of which provider a given consumer test targets.
 * <p>
 * Two independent knobs configure a call: {@link #setMode(Mode)} chooses which of the three
 * producible outcomes {@link #findByEmail(String)} simulates, and {@link #setResponse(DirectoryPerson)}
 * supplies the exact {@link DirectoryPerson} returned while in {@link Mode#FOUND}.
 * <p>
 * Every call is recorded — the last email received and the total invocation count — so a consumer test
 * can assert that a guard short-circuited **before** ever reaching this fake, which is exactly what
 * DIRABS-T06 and DIRABS-T09 need to prove: **zero invocations**.
 */
public class FakeDirectoryService implements DirectoryService {

  /**
   * Which of {@code DirectoryService}'s three producible outcomes {@link #findByEmail(String)}
   * should simulate.
   */
  public enum Mode {

    /** Return the canned {@link DirectoryPerson} configured via {@link #setResponse}. */
    FOUND,

    /** Return {@code notFound} with {@code source == NOT_FOUND}. */
    NOT_FOUND,

    /** Return {@code notFound} with {@code source == ERROR}. */
    ERROR

  }

  private Mode mode = Mode.NOT_FOUND;

  private DirectoryPerson response;

  private String lastEmailReceived;

  private int invocationCount;

  @Override
  public DirectoryPerson findByEmail(String email) {
    this.lastEmailReceived = email;
    this.invocationCount++;

    if (this.mode == Mode.FOUND) {
      if (this.response == null) {
        throw new IllegalStateException("FakeDirectoryService.Mode.FOUND requires setResponse(...) first");
      }
      return this.response;
    }
    if (this.mode == Mode.ERROR) {
      return DirectoryPerson.notFound(email, DirectorySource.ERROR);
    }
    return DirectoryPerson.notFound(email, DirectorySource.NOT_FOUND);
  }

  /**
   * @return how many times {@link #findByEmail(String)} has been invoked on this instance
   */
  public int getInvocationCount() {
    return this.invocationCount;
  }

  /**
   * @return the email most recently passed to {@link #findByEmail(String)}, or {@code null} if it
   *         has never been called
   */
  public String getLastEmailReceived() {
    return this.lastEmailReceived;
  }

  /**
   * @param mode which outcome {@link #findByEmail(String)} should simulate; defaults to
   *        {@link Mode#NOT_FOUND}
   */
  public void setMode(Mode mode) {
    this.mode = mode;
  }

  /**
   * @param response the {@link DirectoryPerson} to return while in {@link Mode#FOUND}
   */
  public void setResponse(DirectoryPerson response) {
    this.response = response;
  }

}
