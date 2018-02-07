package com.philhanna.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for CSVReader instance method
 */
public class TestCSVReaderInstanceMethods extends BaseTest {

   // ==================================================================
   // Class constants and variables
   // ==================================================================

   private static final String TEST_FILE_NAME = "/row_metadata.csv";
   private static final DateFormat dateFormat;
   static {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
   }

   private static final String data = ""
         + "StoogeNumber,StoogeName,Saying\n"
         + "1,Moe,\"Why, I oughta\"\n"
         + "2,Larry,\"Owwww\"\n"
         + "3,Curly,\"Nyuk, nyuk, nyuk\"\n"
         + "4,Shemp,,\n"
         + "5,\"Curly Joe\",1993-07-03"
         + "";

   // ==================================================================
   // Class methods
   // ==================================================================

   /**
    * Parses a string as a date
    * @param dateString
    * @throws ParseException
    */
   private static Date parseDate(String dateString) throws ParseException {
      return dateFormat.parse(dateString);
   }

   // ==================================================================
   // Instance variables
   // ==================================================================

   private CSVReader rs;

   // ==================================================================
   // Fixtures
   // ==================================================================

   @Before
   public void setUp() throws Exception {
      super.setUp();
      rs = new CSVReader(new StringReader(data));
   }

   @After
   public void tearDown() throws Exception {
      rs.close();
      super.tearDown();
   }

   // ==================================================================
   // Unit tests
   // ==================================================================

   @Test
   public void getsColumnCount() throws Exception {
      final int expected = 3;
      final int actual = rs.getColumnCount();
      assertEquals(expected, actual);
   }

   @Test
   public void getsRowCount() throws Exception {
      final int expected = 5;
      int actual = 0;
      while (rs.next())
         actual++;
      assertEquals(expected, actual);
   }

   @Test
   public void getsColumnName() throws Exception {
      assertEquals("Saying", rs.getColumnName(3));
   }

   @Test
   public void getsColumnNames() throws Exception {
      List<String> actual = rs.getColumnNames();
      assertEquals(3, actual.size());
      int n = 0;
      assertEquals("StoogeNumber", actual.get(n++));
      assertEquals("StoogeName", actual.get(n++));
      assertEquals("Saying", actual.get(n++));
   }

   @Test
   public void doesntGetBadColumnName() throws Exception {
      try {
         rs.getColumnName(-17);
         fail("Should have thrown exception");
      }
      catch (CSVException isExpected) {
         // Expected exception was thrown
      }
   }

   @Test
   public void getsColumnIndex() throws Exception {
      assertEquals(2, rs.getColumnIndex("StoogeName"));
   }

   @Test
   public void doesntGetColumnIndexDifferentCase() throws Exception {
      try {
         rs.getColumnIndex("STOOGENAME");
         fail("Should have thrown exception");
      }
      catch (CSVException isExpected) {
         // Expected exception was thrown
      }
   }

   @Test
   public void doesntGetBadColumnIndex() throws Exception {
      try {
         rs.getColumnIndex("");
         fail("Should have thrown exception");
      }
      catch (CSVException isExpected) {
         // Expected exception was thrown
      }
   }

   @Test
   public void getsIntegerByName() throws Exception {
      // Skip to 3rd row
      rs.next();
      rs.next();
      rs.next();
      assertEquals(3, rs.getInteger("StoogeNumber"));
   }

   @Test
   public void getsIntegerByNumber() throws Exception {
      // Skip to 2nd row
      rs.next();
      rs.next();
      assertEquals(2, rs.getInteger(1));
   }

   @Test
   public void getsStringByName() throws Exception {
      rs.next();
      assertEquals("Moe", rs.getString("StoogeName"));
   }

   @Test
   public void getsStringByNumber() throws Exception {
      rs.next();
      rs.next();
      assertEquals("Owwww", rs.getString(3));
   }

   @Test
   public void getsDateByName() throws Exception {

      rs.setDateFormat(dateFormat);

      // Skip to 5th row
      rs.next();
      rs.next();
      rs.next();
      rs.next();
      rs.next();

      final Date expectedDate = parseDate("1993-07-03");
      assertEquals(expectedDate, rs.getDate("Saying"));
   }

   @Test
   public void getsDateByNumber() throws Exception {

      rs.setDateFormat(dateFormat);

      // Skip to 5th row
      rs.next();
      rs.next();
      rs.next();
      rs.next();
      rs.next();

      final Date expectedDate = parseDate("1993-07-03");
      assertEquals(expectedDate, rs.getDate(3));
   }

   @Test
   public void handlesWasNull() throws Exception {
      rs.next();
      rs.next();
      rs.next();
      rs.next();
      rs.getInteger("Saying");
      assertTrue(rs.wasNull());
   }

   @Test
   public void readsFile() throws Exception {

      // Get the test data input file

      final String fileName = TEST_FILE_NAME;
      final URL inputFile = getClass().getResource(fileName);
      assertNotNull(inputFile);

      // Get the expected row count

      final int expectedRowCount = 262;
      final int expectedColCount = 5;

      // Open a CSVReader over the input file

      final InputStream inputStream = inputFile.openStream();
      final CSVReader reader = new CSVReader(inputStream);

      assertEquals(expectedColCount, reader.getColumnCount());

      // Read the rest of the file

      int actualRowCount = 0;
      while (reader.next()) {
         actualRowCount++;
      }

      // Close the reader

      reader.close();

      assertEquals(expectedRowCount, actualRowCount);
   }
}
