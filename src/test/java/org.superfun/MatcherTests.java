package org.superfun;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mike Brock
 */
public class MatcherTests {
  @Test
  public void testMatchparser() {
    Matcher matcher = MatchPattern.of("hello {person}!").parse();
    MatchResult result = matcher.match("hello lincoln!");

    Assert.assertTrue("pattern must match", result.matches());
    Assert.assertEquals("should have one param", 1, result.parameterCount());
    Assert.assertEquals("parameter 'person' should be 'lincoln", "lincoln", result.getValue("person"));
  }
}
