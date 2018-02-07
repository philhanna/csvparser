package com.philhanna.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for CSVReader static methods
 */
public class TestCSVReaderStaticMethods extends BaseTest {

   // ==================================================================
   // Fixtures
   // ==================================================================

   @Before
   public void setUp() throws Exception {
      super.setUp();
   }

   @After
   public void tearDown() throws Exception {
      super.tearDown();
   }

   // ==================================================================
   // Unit tests
   // ==================================================================

   @Test
   public void canReadSimpleUnquotedLine() throws CSVException {
      final String input = "Larry,Curly,Moe";
      final List<String> tokens = CSVReader.parse(input);
      assertNotNull(tokens);
      assertEquals(3, tokens.size());
      int n = 0;
      assertEquals("Larry", tokens.get(n++));
      assertEquals("Curly", tokens.get(n++));
      assertEquals("Moe", tokens.get(n++));
   }

   @Test
   public void allowsQuotesInUnquotedLine() throws CSVException {
      final String input = "Larry,George Herman \"Babe\" Ruth,Moe";
      final List<String> tokens = CSVReader.parse(input);
      assertNotNull(tokens);
      assertEquals(3, tokens.size());
      int n = 0;
      assertEquals("Larry", tokens.get(n++));
      assertEquals("George Herman \"Babe\" Ruth", tokens.get(n++));
      assertEquals("Moe", tokens.get(n++));
   }

   @Test
   public void handlesEmptyTokens() throws CSVException {
      final String input = "Larry,,Moe";
      final List<String> tokens = CSVReader.parse(input);
      assertNotNull(tokens);
      assertEquals(3, tokens.size());
      int n = 0;
      assertEquals("Larry", tokens.get(n++));
      assertEquals("", tokens.get(n++));
      assertEquals("Moe", tokens.get(n++));
   }

   @Test
   public void understandsLeadingAndTrailingCommas() throws CSVException {
      final String input = ",,Larry,Curly,Moe,";
      final List<String> tokens = CSVReader.parse(input);
      assertNotNull(tokens);
      assertEquals(6, tokens.size());
      int n = 0;
      assertEquals("", tokens.get(n++));
      assertEquals("", tokens.get(n++));
      assertEquals("Larry", tokens.get(n++));
      assertEquals("Curly", tokens.get(n++));
      assertEquals("Moe", tokens.get(n++));
      assertEquals("", tokens.get(n++));
   }

   @Test
   public void canReadAMixOfQuotedAndUnquoted() throws CSVException {
      final String input = "Larry,\"Curly, Moe's Brother\",Moe";
      final List<String> tokens = CSVReader.parse(input);
      assertNotNull(tokens);
      assertEquals(3, tokens.size());
      int n = 0;
      assertEquals("Larry", tokens.get(n++));
      assertEquals("Curly, Moe's Brother", tokens.get(n++));
      assertEquals("Moe", tokens.get(n++));
   }

   @Test
   public void canHandleEscapedQuotes() throws CSVException {
      final String input = "NYY,\"George Herman \"\"Babe\"\" Ruth\",BOS";
      final List<String> tokens = CSVReader.parse(input);
      assertNotNull(tokens);
      assertEquals(3, tokens.size());
      int n = 0;
      assertEquals("NYY", tokens.get(n++));
      assertEquals("George Herman \"Babe\" Ruth", tokens.get(n++));
      assertEquals("BOS", tokens.get(n++));
   }

   @Test
   public void handlesGarbledInput() {
      final String input = "30hvb,\"xx\"\",wn\"\\\"";
      try {
         CSVReader.parse(input);
         fail("Should have thrown exception becaused of garbled input");
      }
      catch (CSVException isExpected) {
         // Expected exception was thrown
      }
   }
}
