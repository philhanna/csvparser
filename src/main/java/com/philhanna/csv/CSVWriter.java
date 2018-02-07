package com.philhanna.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for writing <code>.csv</code> files
 */
public class CSVWriter {

   // ====================================================================
   // Class constants and variables
   // ====================================================================

   private static final int BUFFER_SIZE = 65536;

   // ====================================================================
   // Class methods
   // ====================================================================

   // ====================================================================
   // Instance variables
   // ====================================================================

   // Properties that can be set

   private DateFormat dateFormat;
   private List<String> columnNames;
   private boolean quoteAllStrings = false;

   // Internal variables

   private final BufferedWriter out;
   private int rowCount = 0;

   // ====================================================================
   // Constructors
   // ====================================================================

   /**
    * Creates a new CSV writer for the specified output writer
    * @param writer an output writer
    * @throws CSVException if an I/O error occurs
    */
   public CSVWriter(Writer writer) {
      this.out = new BufferedWriter(writer, BUFFER_SIZE);
   }

   /**
    * Creates a new CSV writer for the specified output stream. This is
    * a convenience method that simply opens an OutputStreamWriter and
    * calls the Writer constructor.
    * @param outputStream an output stream over a .csv file
    * @throws CSVException if a parsing error occurs
    */
   public CSVWriter(OutputStream outputStream) throws CSVException {
      this(new OutputStreamWriter(outputStream));
   }

   /**
    * Creates a new CSV writer for the specified file. This is a
    * convenience method that simply opens a FileWriter and calls the
    * Writer constructor.
    * @param outputFile an output .csv file
    * @throws IOException if an I/O error occurs
    */
   public CSVWriter(File outputFile) throws IOException {
      this(new FileWriter(outputFile));
   }

   // ====================================================================
   // Instance methods
   // ====================================================================

   /**
    * Writes the specified array to the output writer
    * @throws IOException if an I/O error occurs
    */
   public void write(Object... tokens) throws IOException {

      if (tokens == null || tokens.length == 0)
         return;

      // Check for list input

      if (tokens.length == 1 && tokens[0] instanceof List<?>) {
         final List<?> list = (List<?>) tokens[0];
         final int n = list.size();
         final Object[] listAsArray = list.toArray(new Object[n]);
         write(listAsArray);
         return;
      }

      // Write the column headers if this is the first time write() has
      // been called. Column headers are not quoted.

      if (rowCount == 0 && columnNames != null) {
         for (int i = 0, n = columnNames.size(); i < n; i++) {
            if (i > 0)
               out.write(",");
            out.write(columnNames.get(i));
         }
         out.write("\n");
      }

      // Increment the row count

      rowCount++;

      // Write the tokens

      for (int i = 0, n = tokens.length; i < n; i++) {
         final Object token = tokens[i];
         if (i > 0)
            out.write(",");
         out.write(toCSV(token));
      }
      out.write("\n");
   }

   /**
    * Converts the specified token to string form, suitable for writing
    * as a comma-separated value, according to its data type:
    * <ul>
    * <li>Numeric objects are unquoted.</li>
    * <li>Strings are quoted if they contain commas or embedded
    * quotes.</li>
    * <li>Dates are formatted according to the date format, if
    * specified.</li>
    * <li>All other types are formatted according with the
    * <code>String.valueOf()</code> method.</li>
    * </ul>
    * @param token the object to convert
    * @return the string form of the token
    */
   public String toCSV(Object token) {

      String s = "";

      if (token != null) {
         if (token instanceof String) {
            s = (String) token;
         }
         else if (token instanceof Date && dateFormat != null) {
            s = dateFormat.format((Date) token);
         }
         else {
            s = String.valueOf(token);
         }
         if (!(token instanceof Number)) {
            if (quoteAllStrings || s.contains(",") || s.contains("\"")) {
               s = s.replaceAll("\"", "\"\"");
               s = '"' + s + '"';
            }
         }
      }

      return s;
   }

   /**
    * Flushes the underlying output writer
    */
   public void flush() throws CSVException {
      try {
         out.flush();
      }
      catch (IOException e) {
         final String errmsg = "Could not flush the "
               + getClass().getSimpleName();
         throw new CSVException(errmsg, e);
      }
   }

   /**
    * Closes the CSV writer and the underlying output writer
    */
   public void close() throws CSVException {
      try {
         out.close();
      }
      catch (IOException e) {
         final String errmsg = "Could not close the "
               + getClass().getSimpleName();
         throw new CSVException(errmsg, e);
      }
   }

   /**
    * Returns the date format
    * @return the date format, or <code>null</code>, if there is no date
    *         format
    */
   public DateFormat getDateFormat() {
      return dateFormat;
   }

   /**
    * Sets the date format
    * @param dateFormat the date format
    */
   public void setDateFormat(DateFormat dateFormat) {
      this.dateFormat = dateFormat;
   }

   /**
    * Sets the column names, which are written as the first row. This
    * method is ignored if any write operations have already happened,
    * or if the column names have already been set
    * @param columnNames a list of strings
    */
   public void setColumnNames(List<String> columnNames) {
      if (this.columnNames == null) {
         this.columnNames = new ArrayList<String>();
         this.columnNames.addAll(columnNames);
      }
   }

   /**
    * Returns the list of column names
    */
   public List<String> getColumnNames() {
      return columnNames;
   }

   /**
    * Returns <code>true</code> if all strings should be quoted, not
    * just those with embedded commas and quote characters
    */
   public boolean isQuoteAllStrings() {
      return quoteAllStrings;
   }

   /**
    * Sets the quoting style.
    * @param quoteAllStrings If <code>true</code>, all strings will be
    *        quoted, not just those with embedded commas and quote
    *        characters. If <code>false</code>, only those strings that
    *        require it will be quoted.
    */
   public void setQuoteAllStrings(boolean quoteAllStrings) {
      this.quoteAllStrings = quoteAllStrings;
   }
}
