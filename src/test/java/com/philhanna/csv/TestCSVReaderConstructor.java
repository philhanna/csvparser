package com.philhanna.csv;

import static org.junit.Assert.*;

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

}
