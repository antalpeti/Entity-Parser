package io.github.antalpeti.entityparser.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
  public static boolean isEmpty(String value) {
    return value == null || value.isEmpty();
  }

  public static int indexOf(Pattern pattern, String s) {
    Matcher matcher = pattern.matcher(s);
    return matcher.find() ? matcher.start() : -1;
  }
}
