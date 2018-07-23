package com.philhanna.csv;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for CSVReader constructor
 */
public class TestCSVReaderConstructor extends BaseTest {

   @Before
   public void setUp() throws Exception {
      super.setUp();
   }

   @After
   public void tearDown() throws Exception {
      super.tearDown();
   }

   @Test
   public void testEmptyFile() throws Exception {
      final File emptyFile = File
            .createTempFile(getClass().getSimpleName(), ".csv");
      emptyFile.deleteOnExit();
      try {
         new CSVReader(emptyFile);
         fail("Should have thrown exception");
      }
      catch (CSVException e) {
         // This is the expected behavior
      }
   }
   
   @Test
   public void testFileConstructor() throws Exception {
      final File cwd = new File(".");
      final File testFile = new File(cwd, "src/test/resources/row_metadata.csv");
      final CSVReader reader = new CSVReader(testFile);
      assertNotNull(reader);
   }

}
