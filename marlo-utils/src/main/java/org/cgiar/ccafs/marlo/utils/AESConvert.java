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

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AESConvert {

  private static Logger LOG = LoggerFactory.getLogger(AESConvert.class);

  /**
   * @param value String with text to encrypt
   * @param encryptionKey String with AES encryption key
   * @return String with the encrypt text
   */
  public static String stringToAES(String value, String encryptionKey) {
    MessageDigest md;
    try {
      byte[] secretKeyBytes;
      if (encryptionKey != null && !encryptionKey.isEmpty()) {
        secretKeyBytes = encryptionKey.getBytes();
      } else {
        // Generate a secret key
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256); // Use a 256-bit key (AES-256)
        SecretKey secretKey = keygen.generateKey();

        // Convert the secret key to byte array
        secretKeyBytes = secretKey.getEncoded();
      }

      // Convert the text to compress into bytes
      String originalText = "Sample text to compress and encrypt using AES";
      byte[] originalBytes = originalText.getBytes();

      // Compress the text
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
      gzipStream.write(originalBytes);
      gzipStream.close();
      byte[] compressedBytes = byteStream.toByteArray();

      // Encrypt the compressed text using AES
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKeyBytes, "AES"));
      byte[] encryptedBytes = cipher.doFinal(compressedBytes);

      // Encode the encrypted bytes to Base64 for easier storage or transmission
      String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

      // Decode the encrypted text into bytes
      byte[] decodedEncryptedBytes = Base64.getDecoder().decode(encryptedText);

      // Decrypt the encrypted text using AES
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKeyBytes, "AES"));
      byte[] decryptedBytes = cipher.doFinal(decodedEncryptedBytes);

      // Decompress the decrypted text
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      outputStream.write(decryptedBytes);
      outputStream.close();

      // Convert the decompressed text to a string
      String decompressedText = new String(outputStream.toByteArray());

      return encryptedText;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
