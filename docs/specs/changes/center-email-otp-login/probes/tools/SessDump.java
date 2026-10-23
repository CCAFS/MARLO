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

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * CHG-OTP-LOGIN-001-T15 probe tool. Reads every Shiro SimpleSession of a JVM started with a JDWP agent, without
 * invoking any method in it.
 */
public class SessDump {

  static String str(Value v) {
    if (v == null) {
      return "null";
    }
    if (v instanceof StringReference) {
      return "\"" + ((StringReference) v).value() + "\"";
    }
    if (v instanceof PrimitiveValue) {
      return v.toString();
    }
    return null;
  }

  static Value f(ObjectReference o, String name) {
    Field fld = o.referenceType().fieldByName(name);
    return fld == null ? null : o.getValue(fld);
  }

  static boolean isPlainObject(Value x) {
    return x instanceof ObjectReference && !(x instanceof StringReference)
      && !((ObjectReference) x).referenceType().name().startsWith("java.lang");
  }

  static String describe(Value v, int depth) {
    String s = str(v);
    if (s != null) {
      return s;
    }
    ObjectReference o = (ObjectReference) v;
    String cn = o.referenceType().name();
    if (cn.equals("java.lang.Boolean") || cn.equals("java.lang.Long") || cn.equals("java.lang.Integer")) {
      return str(f(o, "value"));
    }
    if (cn.endsWith("HashMap") || cn.endsWith("LinkedHashMap")) {
      if (depth <= 0) {
        return cn + "{...}";
      }
      TreeMap<String, String> m = new TreeMap<>();
      collect(o, m, depth - 1);
      return m.toString();
    }
    if (cn.equals("java.util.ArrayList")) {
      if (depth <= 0) {
        return cn + "[...]";
      }
      Value size = f(o, "size");
      ObjectReference data = (ObjectReference) f(o, "elementData");
      List<String> out = new ArrayList<>();
      int n = Integer.parseInt(size.toString());
      for (int i = 0; i < n; i++) {
        out.add(describe(((ArrayReference) data).getValue(i), depth - 1));
      }
      return out.toString();
    }
    StringBuilder sb = new StringBuilder(cn);
    if (depth > 0) {
      for (String id : new String[] {"id", "email", "acronym", "name", "nonce"}) {
        Value x = f(o, id);
        if (x != null && !isPlainObject(x)) {
          sb.append(" ").append(id).append("=").append(describe(x, 0));
        }
      }
    }
    return sb.toString();
  }

  static void collect(ObjectReference map, TreeMap<String, String> out, int depth) {
    ArrayReference table = (ArrayReference) f(map, "table");
    if (table == null) {
      return;
    }
    for (Value b : table.getValues()) {
      ObjectReference n = (ObjectReference) b;
      while (n != null) {
        Value key = f(n, "key");
        Value val = f(n, "val") != null ? f(n, "val") : f(n, "value");
        if (n.referenceType().fieldByName("val") == null && n.referenceType().fieldByName("value") == null) {
          break;
        }
        String k = str(key) != null ? str(key) : describe(key, 0);
        out.put(k, describe(val, depth));
        n = (ObjectReference) f(n, "next");
      }
    }
  }

  public static void main(String[] a) throws Exception {
    AttachingConnector c = null;
    for (AttachingConnector x : Bootstrap.virtualMachineManager().attachingConnectors()) {
      if (x.name().equals("com.sun.jdi.SocketAttach")) {
        c = x;
      }
    }
    Map<String, Connector.Argument> args = c.defaultArguments();
    args.get("port").setValue(a[0]);
    args.get("hostname").setValue("127.0.0.1");
    VirtualMachine vm = c.attach(args);
    String want = a.length > 1 ? a[1] : null;
    for (ReferenceType rt : vm.classesByName("org.apache.shiro.session.mgt.SimpleSession")) {
      for (ObjectReference s : rt.instances(0)) {
        String id = describe(f(s, "id"), 0);
        if (want != null && !id.contains(want)) {
          continue;
        }
        System.out.println(
          "SESSION " + id + " stop=" + describe(f(s, "stopTimestamp"), 0) + " expired=" + describe(f(s, "expired"), 0));
        TreeMap<String, String> m = new TreeMap<>();
        Value attrs = f(s, "attributes");
        if (attrs != null) {
          collect((ObjectReference) attrs, m, 2);
        }
        for (Map.Entry<String, String> e : m.entrySet()) {
          System.out.println("  " + e.getKey() + " = " + e.getValue());
        }
      }
    }
    vm.dispose();
  }
}
