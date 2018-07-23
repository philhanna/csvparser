package com.philhanna.csv;

/**
 * Thrown when an invalid line in a <code>.csv</code> file is parsed
 */
public class CSVException extends Exception {

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new exception with the specified detail message
    * @param message the detail message
    */
   public CSVException(String message) {
      super(message);
   }
}
