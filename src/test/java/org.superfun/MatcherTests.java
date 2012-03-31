package org.superfun;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mike Brock
 */
public class MatcherTests {
  @Test
  public void testAttributes() {
    Matcher matcher = MatchPattern.of("hello {person}!").parse();
    MatchResult result = matcher.match("hello lincoln!");

    assertTrue("pattern must match", result.matches());
    assertEquals("should have one param", 1, result.parameterCount());
    assertEquals("parameter 'person' should be 'lincoln", "lincoln", result.getValue("person"));
  }

  @Test
  public void testMultiAttributes() {
    Matcher matcher = MatchPattern.of("/path/{foo}/{bar}/{cat}").parse();
    MatchResult result = matcher.match("/path/curly/larry/moe");

    assertTrue("pattern must match", result.matches());
    assertEquals("should have one param", 3, result.parameterCount());
    assertEquals("parm does not match", "curly", result.getValue("foo"));
    assertEquals("parm does not match", "larry", result.getValue("bar"));
    assertEquals("parm does not match", "moe", result.getValue("cat"));
  }


  @Test
  public void testSimpleMatch() {
    Matcher matcher = MatchPattern.of("*/foo").parse();

    assertTrue("pattern must match", matcher.match("/bar/foo").matches());
    assertTrue("pattern must match", matcher.match("/test/bar/foo").matches());
  }

  @Test
  public void testSimpleMustNotMatch() {
    Matcher matcher = MatchPattern.of("*/foo").parse();
    assertFalse("pattern must not match", matcher.match("/foo/bar").matches());
  }

  @Test
  public void testMultiLevelMatch() {
    Matcher matcher = MatchPattern.of("/fruit/*/apples/*").parse();

    assertTrue("pattern must match", matcher.match("/fruit/red/apples/granny_smith").matches());
  }

  @Test
  public void testMultiLevelMustNotMatch() {
    Matcher matcher = MatchPattern.of("/fruit/*/apples/*").parse();

    assertFalse("pattern must not match", matcher.match("/fruit/red/oranges/florida").matches());
  }


  final static int RUN_COUNT = 1000000;
  final static int LOOP = 4;
  final static NumberFormat nf = new DecimalFormat("###,###.###");


  private static void printPerfResultTitle(String description) {
    System.out.println("Perfomance Test: " + description);
    System.out.println("----------------------------------");
  }

  public static void printResult(String title, long timeInMillesconds) {
    System.out.println(title + ":");
    System.out.println("    Runtime    (average): " + nf.format(timeInMillesconds / (double) LOOP) + "ms");
    System.out.println("    Throughput (average): " + nf.format((RUN_COUNT / (timeInMillesconds / 1000d)) / (double) LOOP) + " per second.");
    System.out.println();
  }


  @Test
  public void perfTestMatchOnly() {
    Matcher matcher = MatchPattern.of("/foo/*/bar").parse();
    Pattern regexMatcher = Pattern.compile("/foo/.*/bar");

    long superMatcherTime = 0;
    long regexMatcherTime = 0;

    for (int warm = 0; warm < LOOP; warm++) {

      long start = System.nanoTime();
      for (int i = 0; i < RUN_COUNT; i++) {
        MatchResult result = matcher.match("/foo/cat/bar");
        assertTrue("lincoln", result.matches());
      }
      long time = (System.nanoTime() - start) / 1000 / 1000;
      superMatcherTime += time;


      start = System.nanoTime();
      for (int i = 0; i < RUN_COUNT; i++) {
        java.util.regex.Matcher result = regexMatcher.matcher("/foo/cat/bar");
        assertTrue(result.matches());
      }
      time = (System.nanoTime() - start) / 1000 / 1000;
      regexMatcherTime += time;
    }

    printPerfResultTitle("Simple Matching");
    printResult("SuperMatch!", superMatcherTime);
    printResult("Java Regex", regexMatcherTime);


  }

  @Test
  public void perfTestCapture() {
    Matcher matcher = MatchPattern.of("hello {person}!").parse();
    Pattern regexMatcher = Pattern.compile("hello (.*)!");

    long superMatcherTime = 0;
    long regexMatcherTime = 0;

    for (int warm = 0; warm < LOOP; warm++) {

      long start = System.nanoTime();
      for (int i = 0; i < RUN_COUNT; i++) {
        MatchResult result = matcher.match("hello lincoln!");
        assertEquals("lincoln", result.getValue("person"));
      }
      long time = (System.nanoTime() - start) / 1000 / 1000;
      superMatcherTime += time;


      start = System.nanoTime();
      for (int i = 0; i < RUN_COUNT; i++) {
        java.util.regex.Matcher result = regexMatcher.matcher("hello lincoln!");
        assertTrue(result.find());
        assertEquals("lincoln", result.group(1));
      }
      time = (System.nanoTime() - start) / 1000 / 1000;
      regexMatcherTime += time;

    }

    printPerfResultTitle("Group Capturing");
    printResult("SuperMatch!", superMatcherTime);
    printResult("Java Regex", regexMatcherTime);
  }


}
