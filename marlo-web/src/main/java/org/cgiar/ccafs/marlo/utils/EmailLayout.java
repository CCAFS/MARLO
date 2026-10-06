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
package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.action.BaseAction;

import com.opensymphony.xwork2.TextProvider;

/**
 * Puts the body of a notification inside the branded email shell of its Global Unit, held by the i18n key
 * email.layout. The base text of the key is "{0}", so a Global Unit that does not override it sends the body as it
 * is; the ones that do override it with the HTML shell, where {0} is the body and {1} the URL the images of the
 * shell are served from.
 * The shell is applied only by the notifications that opt in, which are the role notifications and the welcome
 * email: SendMailS also sends the technical alerts, which must stay without it.
 */
public final class EmailLayout {

  static final String LAYOUT_KEY = "email.layout";

  private EmailLayout() {
  }

  /**
   * Wraps the body of a notification sent from an action, with the images taken from the CDN of the instance, or
   * from its base URL when it has none.
   *
   * @param action the action sending the email, which resolves the key for its Global Unit.
   * @param body the body of the email, as assembled from its i18n keys.
   * @return the body inside the shell, or the body as it is when the Global Unit has no shell.
   */
  public static String wrap(BaseAction action, String body) {
    return wrap(action, action.getBaseUrlCdn(), body);
  }

  /**
   * Wraps the body of a notification in the shell held by the key email.layout.
   *
   * @param texts the provider that resolves the key for the Global Unit of the email.
   * @param assetsUrl the URL the images of the shell are served from, without the trailing slash.
   * @param body the body of the email, as assembled from its i18n keys.
   * @return the body inside the shell, or the body as it is when the key cannot be resolved.
   */
  public static String wrap(TextProvider texts, String assetsUrl, String body) {
    // The body goes in as an argument, so its apostrophes and braces are not read as MessageFormat syntax.
    String wrapped = texts.getText(LAYOUT_KEY, new String[] {body, assetsUrl});
    // getText answers with the key itself when no file defines it; the email must not lose its body for that.
    if (wrapped == null || LAYOUT_KEY.equals(wrapped)) {
      return body;
    }
    return wrapped;
  }
}
