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

import org.cgiar.ccafs.marlo.config.APConstants;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * A text provider for one Global Unit that is built on its own, before any user is signed in (CHG-OTP-LOGIN-001-T20,
 * FN-017, UI-005, DD-7).
 * <p>
 * Every other MARLO email gets the custom i18n file of its Global Unit from the logged session:
 * {@code InternationalitazionFileInterceptor} puts {@code custom/<crp_custom_file>} on top of {@code global} in the
 * <b>shared</b> Struts provider, once per request. The code email is sent before sign-in, when the session holds no
 * Global Unit, and that provider is shared across requests and users, so it must not be changed for one email. This
 * class reads the same two files itself and changes nothing that is shared: a key is looked up in the custom file
 * first and then in {@code global.properties}, and the value is formatted with {@link MessageFormat}, as Struts does
 * (so an apostrophe is doubled and a brace that is not an argument is parsed).
 * <p>
 * The bundles come from {@link ResourceBundle#getBundle(String, Locale, ClassLoader)}, which caches them for the whole
 * application: after the first read an instance costs two cache lookups and no I/O. An instance holds no state of its
 * own beyond the two immutable bundles, so it is safe to use from any thread.
 */
public final class UnitTextProvider implements TextProvider {

  /** The custom file name is a DB value: only a plain file name is accepted, so it can never reach another path. */
  private static final Pattern FILE_NAME = Pattern.compile("[A-Za-z0-9_-]{1,64}");

  private static final Locale LOCALE = new Locale(APConstants.CUSTOM_LAGUAGE);

  private final ResourceBundle custom;
  private final ResourceBundle global;

  private UnitTextProvider(ResourceBundle custom, ResourceBundle global) {
    this.custom = custom;
    this.global = global;
  }

  /**
   * Builds the provider of a Global Unit from the value of its {@code crp_custom_file} parameter.
   *
   * @param customFile the parameter value, without {@code .properties}; {@code null}, blank, not a plain file name or
   *        naming a file that does not exist all give a provider that resolves {@code global.properties} only, which
   *        is what a Global Unit with no custom file gets everywhere else
   * @return a provider that falls back to {@code global.properties} for every key the custom file does not define
   * @throws MissingResourceException when {@code global.properties} itself cannot be read
   */
  public static UnitTextProvider forCustomFile(String customFile) {
    ClassLoader loader = UnitTextProvider.class.getClassLoader();
    ResourceBundle global = ResourceBundle.getBundle(APConstants.CUSTOM_FILE, LOCALE, loader);
    ResourceBundle custom = null;
    String name = customFile == null ? "" : customFile.trim();
    if (FILE_NAME.matcher(name).matches()) {
      try {
        custom = ResourceBundle.getBundle(APConstants.PATH_CUSTOM_FILES + name, LOCALE, loader);
      } catch (MissingResourceException e) {
        // A parameter that names no file leaves the unit on global.properties, as it does for every other email.
        custom = null;
      }
    }
    return new UnitTextProvider(custom, global);
  }

  @Override
  public String getText(String key) {
    return this.render(key, key, null);
  }

  @Override
  public String getText(String key, String defaultValue) {
    return this.render(key, defaultValue, null);
  }

  @Override
  public String getText(String key, String defaultValue, String obj) {
    return this.render(key, defaultValue, new Object[] {obj});
  }

  @Override
  public String getText(String key, List<?> args) {
    return this.render(key, key, args == null ? null : args.toArray());
  }

  @Override
  public String getText(String key, String[] args) {
    return this.render(key, key, args);
  }

  @Override
  public String getText(String key, String defaultValue, List<?> args) {
    return this.render(key, defaultValue, args == null ? null : args.toArray());
  }

  @Override
  public String getText(String key, String defaultValue, String[] args) {
    return this.render(key, defaultValue, args);
  }

  @Override
  public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
    return this.render(key, defaultValue, args == null ? null : args.toArray());
  }

  @Override
  public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
    return this.render(key, defaultValue, args);
  }

  @Override
  public ResourceBundle getTexts() {
    return this.global;
  }

  @Override
  public ResourceBundle getTexts(String bundleName) {
    return ResourceBundle.getBundle(bundleName, LOCALE, UnitTextProvider.class.getClassLoader());
  }

  @Override
  public boolean hasKey(String key) {
    return key != null && ((this.custom != null && this.custom.containsKey(key)) || this.global.containsKey(key));
  }

  private String render(String key, String defaultValue, Object[] args) {
    if (key == null) {
      return defaultValue;
    }
    String pattern = null;
    if (this.custom != null && this.custom.containsKey(key)) {
      pattern = this.custom.getString(key);
    } else if (this.global.containsKey(key)) {
      pattern = this.global.getString(key);
    }
    if (pattern == null) {
      return defaultValue;
    }
    return new MessageFormat(pattern, LOCALE).format(args == null ? new Object[0] : args);
  }
}
