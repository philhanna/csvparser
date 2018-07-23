package com.philhanna.csv;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.philhanna.csv.CSVWriter;

/**
 * Unit tests for CSV writer tests
 */
public class TestCSVWriter extends BaseTest {

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
   public void writesUnquotedStrings() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      ro.write("Larry", "Curly", "Moe");
      ro.flush();
      ro.close();
      final String expected = "Larry,Curly,Moe";
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }

   @Test
   public void writesUnquotedStringList() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      final List<Object> tokens = new ArrayList<Object>();
      tokens.add("Larry");
      tokens.add("Curly");
      tokens.add("Moe");
      ro.write(tokens);
      ro.flush();
      ro.close();
      final String expected = "Larry,Curly,Moe";
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }

   @Test
   public void writesMixedTypes() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      ro.write("Larry", 23, "Moe");
      ro.flush();
      ro.close();
      final String expected = "Larry,23,Moe";
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }

   @Test
   public void handlesEmbeddedQuotes() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      ro.write("Larry", "George Herman \"Babe\" Ruth", "Moe");
      ro.flush();
      ro.close();
      final String expected = "Larry,\"George Herman \"\"Babe\"\" Ruth\",Moe";
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }

   @Test
   public void handlesEmbeddedCommas() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      final Object[] tokens = {
            "Dangers",
            3,
            "Lions, Tigers, Bears", };
      ro.write(tokens);
      ro.flush();
      ro.close();
      final String expected = "Dangers,3,\"Lions, Tigers, Bears\"";
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }

   @Test
   public void handlesEmptyStrings() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      ro.write("Tom", "", "Thumb", "");
      ro.flush();
      ro.close();
      final String expected = "Tom,,Thumb,";
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }

   @Test
   public void handlesNulls() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      ro.write("Tom", null, "Thumb", "");
      ro.flush();
      ro.close();
      final String expected = "Tom,,Thumb,";
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }

   @Test
   public void writesMixedTypesIncludingDates() throws Exception {
      final StringWriter sw = new StringWriter();
      final CSVWriter ro = new CSVWriter(sw);
      final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      ro.setDateFormat(dateFormat);

      final Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(Calendar.YEAR, 1962);
      cal.set(Calendar.MONTH, Calendar.NOVEMBER);
      cal.set(Calendar.DATE, 7);
      final Date date = cal.getTime(); // Eleanor Roosevelt

      ro.write("Larry", "Curly", date, "Moe");
      ro.flush();
      ro.close();
      final String dateString = "1962-11-07";
      final String expected = String.format(
            "%s,%s,%s,%s",
            "Larry",
            "Curly",
            dateString,
            "Moe");
      final String actual = sw.toString().trim();
      assertEquals(expected, actual);
   }
}
