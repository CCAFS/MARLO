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

import java.util.Objects;

/**
 * An immutable, self-describing result of a directory lookup: either a corporate person was found,
 * or it was not — and either way, the result knows which backend produced it.
 * <p>
 * All fields are final and set once, at construction, through one of the two static factories below.
 * There are no setters, so the only way to obtain an instance is through {@link #found} or
 * {@link #notFound}, and both require a non-null {@link DirectorySource}: an instance with a
 * <code>null</code> {@code source} is not constructible.
 * <p>
 * Fields carry raw, untransformed values. Neither this type nor any implementation of
 * {@code DirectoryService} lowercases, uppercases, or trims {@code login} or {@code email} — every
 * existing consumer keeps applying its own transformation at its own call site.
 */
public final class DirectoryPerson {

  /**
   * Builds a result for a person the directory found.
   *
   * @param email the email returned by the directory, raw and untransformed
   * @param login the login returned by the directory, raw and untransformed
   * @param firstName the first name returned by the directory
   * @param lastName the last name returned by the directory
   * @param source the backend that produced this result; must not be null
   * @return a {@link DirectoryPerson} with {@code found == true}
   */
  public static DirectoryPerson found(String email, String login, String firstName, String lastName,
    DirectorySource source) {
    return new DirectoryPerson(true, email, login, firstName, lastName, source);
  }

  /**
   * Builds a result for a lookup that did not resolve to a person — either because the directory
   * answered that the person is not there, or because the lookup itself failed. The caller states
   * which one happened by passing the matching {@link DirectorySource} (for example
   * {@link DirectorySource#NOT_FOUND} or {@link DirectorySource#ERROR}); this factory never assumes
   * one on the caller's behalf.
   *
   * @param email the email that was requested, raw and untransformed
   * @param source the backend outcome that produced this result; must not be null
   * @return a {@link DirectoryPerson} with {@code found == false} and {@code login}, {@code firstName},
   *         {@code lastName} all {@code null} — never empty strings
   */
  public static DirectoryPerson notFound(String email, DirectorySource source) {
    return new DirectoryPerson(false, email, null, null, null, source);
  }

  private final boolean found;

  private final String email;

  private final String login;

  private final String firstName;

  private final String lastName;

  private final DirectorySource source;

  private DirectoryPerson(boolean found, String email, String login, String firstName, String lastName,
    DirectorySource source) {
    this.found = found;
    this.email = email;
    this.login = login;
    this.firstName = firstName;
    this.lastName = lastName;
    this.source = Objects.requireNonNull(source, "source must not be null");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    DirectoryPerson other = (DirectoryPerson) obj;
    if (found != other.found) {
      return false;
    }
    if (source != other.source) {
      return false;
    }
    if (email == null) {
      if (other.email != null) {
        return false;
      }
    } else if (!email.equals(other.email)) {
      return false;
    }
    if (login == null) {
      if (other.login != null) {
        return false;
      }
    } else if (!login.equals(other.login)) {
      return false;
    }
    if (firstName == null) {
      if (other.firstName != null) {
        return false;
      }
    } else if (!firstName.equals(other.firstName)) {
      return false;
    }
    if (lastName == null) {
      if (other.lastName != null) {
        return false;
      }
    } else if (!lastName.equals(other.lastName)) {
      return false;
    }
    return true;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getLogin() {
    return login;
  }

  public DirectorySource getSource() {
    return source;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + (found ? 1231 : 1237);
    result = (prime * result) + ((source == null) ? 0 : source.hashCode());
    result = (prime * result) + ((email == null) ? 0 : email.hashCode());
    result = (prime * result) + ((login == null) ? 0 : login.hashCode());
    result = (prime * result) + ((firstName == null) ? 0 : firstName.hashCode());
    result = (prime * result) + ((lastName == null) ? 0 : lastName.hashCode());
    return result;
  }

  public boolean isFound() {
    return found;
  }

  /**
   * Masks a personal-data value so it never reaches a log line in full: keeps the first character
   * and replaces the rest with asterisks. Returns the literal string {@code "null"} for a null input,
   * matching the pattern already used across MARLO's other {@code toString} implementations.
   *
   * @param value the raw value to mask
   * @return the masked representation
   */
  private static String mask(String value) {
    if (value == null) {
      return "null";
    }
    if (value.isEmpty()) {
      return "";
    }
    return value.charAt(0) + "***";
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DirectoryPerson [found=");
    builder.append(found);
    builder.append(", email=");
    builder.append(mask(email));
    builder.append(", login=");
    builder.append(mask(login));
    builder.append(", firstName=");
    builder.append(firstName);
    builder.append(", lastName=");
    builder.append(lastName);
    builder.append(", source=");
    builder.append(source);
    builder.append("]");
    return builder.toString();
  }

}
