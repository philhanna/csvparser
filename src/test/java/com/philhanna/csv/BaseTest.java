package com.philhanna.csv;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Abstract base class for all unit tests. Provides the minimal
 * functionality of reading unit test properties.
 */
public abstract class BaseTest {

   // ==================================================================
   // Class constants and variables
   // ==================================================================

   private static final String PROPERTIES_FILE_NAME = "/unitTest.properties";
   private static final Properties testProperties = new Properties();

   // ==================================================================
   // Class methods
   // ==================================================================

   /**
    * Loads the unit test properties (both the defaults and any
    * overrides for this machine). Called only once, when the class is
    * loaded.
    * @throws IOException if an I/O error occurs
    */
   @BeforeClass
   public static void setUpBeforeClass() throws IOException {

      // Create the base unit test properties file

      final URL defaultURL = BaseTest.class.getResource(PROPERTIES_FILE_NAME);
      if (defaultURL != null) {
         final InputStream in = defaultURL.openStream();
         testProperties.load(in);
         in.close();
      }
   }

   // ==================================================================
   // Fixtures
   // ==================================================================

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   // ==================================================================
   // Helper methods for getting test properties
   // ==================================================================

   /**
    * Returns the string property with the given name
    * @param key the exact property name (no RBKEY)
    * @return the property value, or <code>null</code>, if it does not
    *         exist
    */
   protected String getProperty(String key) {
      return testProperties.getProperty(key);
   }

   /**
    * Returns the string property with the given name.
    * @param key the property key suffix. The prefix is the simple name
    *        of the test class, followed with a "."
    * @return the property value.
    * @throws RuntimeException if the property is not found
    */
   protected String getStringProperty(String key) throws RuntimeException {
      final String RBKEY = getClass().getSimpleName();
      final String fullKey = RBKEY + "." + key;
      final String value = testProperties.getProperty(fullKey);
      if (value == null) {
         final String errmsg = String.format(
               "Property %s not found in %s",
               fullKey,
               PROPERTIES_FILE_NAME);
         throw new RuntimeException(errmsg);
      }
      return value;
   }

   /**
    * Returns the integer property with the given name.
    * @param key the property key suffix. The prefix is the simple name
    *        of the test class, followed with a "."
    * @return the property value.
    * @throws RuntimeException if the property is not found
    * @throws NumberFormatException if the property is not an integer
    */
   protected int getIntegerProperty(String key) {
      final String value = getStringProperty(key);
      try {
         final int numericValue = Integer.parseInt(value);
         return numericValue;
      }
      catch (NumberFormatException e) {
         final String errmsg = String
               .format("Property %s value %s is not numeric", key, value);
         throw new RuntimeException(errmsg);
      }
   }

}
