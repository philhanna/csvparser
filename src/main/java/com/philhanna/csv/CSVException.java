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

   /**
    * Creates a new exception with a root cause exception
    * @param t the root cause exception
    */
   public CSVException(Throwable t) {
      super(t);
   }

   /**
    * Creates a new exception with the specified detail message and root cause
    * @param message the detail message
    * @param t the root cause exception
    */
   public CSVException(String message, Throwable t) {
      super(message, t);
   }

}
