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

package org.cgiar.ccafs.marlo.security.otp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * CHG-OTP-LOGIN-001-T05, SEC-004: the session handle holds the nonce, the normalised email, the Global Unit, the
 * return URL and the terms flag, and nothing else: no code, no MAC.
 */
public class OtpPendingTest {

  @Test
  public void isSerializable() {
    assertTrue(Serializable.class.isAssignableFrom(OtpPending.class));
  }

  @Test
  public void survivesASerializationRoundTrip() throws Exception {
    OtpPending original = new OtpPending("AAAAAAAAAAAAAAAAAAAAAA", "ana@partner.org", 45L,
      "https://marlo.example/projects.do", true);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(original);
    }
    OtpPending copy;
    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      copy = (OtpPending) in.readObject();
    }

    assertEquals("AAAAAAAAAAAAAAAAAAAAAA", copy.getNonce());
    assertEquals("ana@partner.org", copy.getEmail());
    assertEquals(45L, copy.getGlobalUnitId());
    assertEquals("https://marlo.example/projects.do", copy.getReturnUrl());
    assertTrue(copy.isAgree());
  }

  @Test
  public void keepsEachValueItWasGivenAndAllowsANullReturnUrl() {
    OtpPending pending = new OtpPending("N", "e@x.org", 7L, null, false);

    assertEquals("N", pending.getNonce());
    assertEquals("e@x.org", pending.getEmail());
    assertEquals(7L, pending.getGlobalUnitId());
    assertNull(pending.getReturnUrl());
    assertFalse(pending.isAgree());
  }

  @Test
  public void holdsExactlyTheFiveBindingFieldsAndNoCodeOrMac() {
    Set<String> instanceFields = new HashSet<String>();
    for (Field field : OtpPending.class.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers())) {
        instanceFields.add(field.getName());
      }
    }

    assertEquals(new HashSet<String>(Arrays.asList("nonce", "email", "globalUnitId", "returnUrl", "agree")),
      instanceFields);
  }
}
