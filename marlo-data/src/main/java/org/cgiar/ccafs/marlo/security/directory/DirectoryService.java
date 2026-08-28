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
 * The single lookup seam for resolving a corporate person by email, regardless of which directory
 * provider is configured behind it.
 * <p>
 * This interface has exactly one method, and that method carries a never-throws contract: no
 * implementation may propagate an exception under any input or backend condition, and no
 * implementation may return {@code null}. Every returned {@link DirectoryPerson} carries a
 * non-null {@link DirectorySource}, so a caller — or a log line, or a support engineer reading one
 * — can always tell what actually happened.
 * <p>
 * <b>{@code findByEmail} resolves to exactly one of the following five outcomes:</b>
 * <ol>
 * <li>{@code email} is {@code null} or blank &rarr; {@link DirectoryPerson#notFound} with
 * {@code source == }{@link DirectorySource#NOT_FOUND}.</li>
 * <li>{@code email} is malformed &rarr; {@link DirectoryPerson#notFound} with
 * {@code source == }{@link DirectorySource#NOT_FOUND}.</li>
 * <li>{@code email} is well-formed and the directory has a matching person &rarr;
 * {@link DirectoryPerson#found} with {@code source == }{@link DirectorySource#LDAP}.</li>
 * <li>{@code email} is well-formed and the directory answers that no matching person exists
 * &rarr; {@link DirectoryPerson#notFound} with {@code source == }{@link DirectorySource#NOT_FOUND}.</li>
 * <li>The backend is unreachable, times out, or throws &rarr; {@link DirectoryPerson#notFound} with
 * {@code source == }{@link DirectorySource#ERROR}.</li>
 * </ol>
 * <p>
 * <b>Outcome 2 takes precedence over outcome 5 when both apply.</b> A malformed email that also causes
 * the backend to throw resolves to outcome 2 ({@code NOT_FOUND}), never outcome 5 ({@code ERROR}): the
 * failure is attributable to the invalid input, not to the backend, so it must not be reported as a
 * backend failure.
 * <p>
 * <b>Invariants, on every one of the outcomes above, with no exception:</b> {@code findByEmail}
 * never throws; it never returns {@code null}; and the returned person's
 * {@link DirectoryPerson#getSource()} is never {@code null}.
 * <p>
 * <b>Outcome 1 makes no network call.</b> A {@code null} or blank {@code email} must fail fast,
 * before any bind or connection to the backend.
 * <p>
 * <b>{@code NOT_FOUND} and {@code ERROR} both leave {@code found == false}, but they are not
 * interchangeable.</b> {@link DirectorySource#NOT_FOUND} <em>asserts knowledge</em>: the directory
 * was reached, it answered, and the person is not there. {@link DirectorySource#ERROR}
 * <em>asserts the absence of knowledge</em>: the lookup itself failed — the backend was
 * unreachable, timed out, or threw — and nothing is known about whether the person exists.
 * Collapsing this distinction would let a backend outage read as "this email does not exist",
 * which is a false statement, not merely an unfriendly one. Most callers read only {@code found}
 * and are correctly indifferent to which of the two produced it; a caller that must not silently
 * degrade a backend outage into "not found" reads {@code source} instead and reacts only to
 * {@code ERROR}.
 */
public interface DirectoryService {

  /**
   * Resolves a corporate person by email.
   * <p>
   * Never throws and never returns {@code null} for any input, including a {@code null} or blank
   * {@code email} — see the type-level Javadoc above for the full five-outcome contract and the
   * invariants this method upholds on every one of them.
   *
   * @param email the email to look up, raw and untransformed; {@code null} or blank is a valid,
   *        non-exceptional input that resolves without a network call
   * @return a non-null {@link DirectoryPerson} whose {@link DirectoryPerson#getSource()} is never
   *         {@code null}
   */
  DirectoryPerson findByEmail(String email);

}
