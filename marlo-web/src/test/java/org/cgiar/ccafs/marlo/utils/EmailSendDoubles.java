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

import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;

import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import com.opensymphony.xwork2.TextProvider;

/**
 * Hand-written doubles for driving the send path of an action that mails through {@link SendMailS}, without a
 * container, a database or an SMTP server (CHG-EMAIL-LAYOUT-001 FN-010, FN-011). Every double records the arguments
 * it receives, so a test asserts what the action passed instead of only that it called.
 */
public final class EmailSendDoubles {

  /** One call of {@code SendMailS.send}, with every argument as the action passed it. */
  public static final class Sent {

    public final String to;
    public final String cc;
    public final String bcc;
    public final String subject;
    public final String body;
    public final byte[] attachment;
    public final String mimeType;
    public final String fileName;
    public final boolean html;

    Sent(String to, String cc, String bcc, String subject, String body, byte[] attachment, String mimeType,
      String fileName, boolean html) {
      this.to = to;
      this.cc = cc;
      this.bcc = bcc;
      this.subject = subject;
      this.body = body;
      this.attachment = attachment;
      this.mimeType = mimeType;
      this.fileName = fileName;
      this.html = html;
    }
  }

  /** A SendMailS that sends nothing and keeps every call. */
  public static final class RecordingSendMail extends SendMailS {

    private static final long serialVersionUID = 1L;

    public final transient List<Sent> sent = new ArrayList<>();

    public RecordingSendMail() {
      super(null, null, null, null);
    }

    @Override
    public void send(String toEmail, String ccEmail, String bbcEmail, String subject, String messageContent,
      byte[] attachment, String attachmentMimeType, String fileName, boolean isHtml) {
      this.sent.add(new Sent(toEmail, ccEmail, bbcEmail, subject, messageContent, attachment, attachmentMimeType,
        fileName, isHtml));
    }
  }

  /** The three configuration values the send paths read, each one distinct so a swapped slot shows. */
  public static final class EnvelopeConfig extends APConfig {

    private final String pmu;
    private final String notification;
    private final String baseUrl;

    public EnvelopeConfig(String pmu, String notification, String baseUrl) {
      this.pmu = pmu;
      this.notification = notification;
      this.baseUrl = baseUrl;
    }

    @Override
    public String getBaseUrl() {
      return this.baseUrl;
    }

    @Override
    public String getEmail_pmu() {
      return this.pmu;
    }

    @Override
    public String getEmailNotification() {
      return this.notification;
    }
  }

  /**
   * A double of an interface that answers one method and refuses every other, so a path the test did not expect
   * fails loudly instead of returning null.
   */
  @SuppressWarnings("unchecked")
  public static <T> T answering(Class<T> type, String method, Function<Object[], Object> answer) {
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, invoked, args) -> {
      if (invoked.getName().equals(method)) {
        return answer.apply(args);
      }
      if (invoked.getName().equals("toString")) {
        return type.getSimpleName() + " double";
      }
      throw new UnsupportedOperationException(type.getSimpleName() + "." + invoked.getName());
    });
  }

  /**
   * A CustomParameterManager that answers the custom file of each unit id from the map (none when absent) and records
   * every lookup as "key@unitId".
   */
  public static CustomParameterManager customFiles(Map<Long, String> files, List<String> lookups) {
    return answering(CustomParameterManager.class, "getCustomParameterByParameterKeyAndGlobalUnitId", args -> {
      Long unit = (Long) args[1];
      lookups.add(args[0] + "@" + unit);
      String file = files.get(unit);
      if (file == null) {
        return null;
      }
      CustomParameter parameter = new CustomParameter();
      parameter.setValue(file);
      return parameter;
    });
  }

  /**
   * The texts of a unit whose custom file carries the DD-9 opt-out block exactly as global.properties documents it:
   * email.layout={0} plus the commented email.role.* and login.otp.email.html lines below it, uncommented. Answers
   * getText with and without arguments through MessageFormat, as Struts does.
   */
  public static TextProvider documentedOptOut() throws Exception {
    Properties global = new Properties();
    try (InputStream stream = EmailSendDoubles.class.getResourceAsStream("/global.properties")) {
      global.load(stream);
    }
    StringBuilder block = new StringBuilder("email.layout={0}\n");
    String documented = new String(Files.readAllBytes(
      Paths.get(EmailSendDoubles.class.getResource("/global.properties").toURI())), StandardCharsets.ISO_8859_1);
    for (String line : documented.split("\\r?\\n")) {
      if (line.startsWith("# email.role.") || line.startsWith("# login.otp.email.html=")) {
        block.append(line.substring(2)).append('\n');
      }
    }
    Properties unit = new Properties(global);
    unit.load(new StringReader(block.toString()));
    return (TextProvider) Proxy.newProxyInstance(TextProvider.class.getClassLoader(),
      new Class<?>[] {TextProvider.class}, (proxy, invoked, args) -> {
        String key = (String) args[0];
        String pattern = unit.getProperty(key);
        Object[] values = args.length > 1 && args[1] instanceof String[] ? (Object[]) args[1] : new Object[0];
        return pattern == null ? key : MessageFormat.format(pattern, values);
      });
  }

  private EmailSendDoubles() {
  }
}
