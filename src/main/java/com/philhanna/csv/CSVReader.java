package com.philhanna.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading <code>.csv</code> files, using an approach
 * and method names similar to the <code>java.sql.ResultSet</code> API.
 * <ul>
 * <li>Iterating through the results uses a <code>next()</code>
 * method</li>
 * <li>Values from the current row are obtained with <code>get</code>
 * <i>&lt;type&gt;</i><code>()</code> methods for the expected type</li>
 * , using either a column number or column name
 * <li>Column numbers start at 1</li>
 * <li>The <code>wasNull()</code> method indicates whether the preceding
 * <code>get</code> operation was performed on a null value .
 * </ul>
 * <p>
 * To read a <code>.csv</code> file:
 * 
 * <pre>
 * <code>
 *       // Create a CSV reader object either from a file:
 * 
 *       File inputFile = new File("file_name.csv");
 *       CSVReader reader = new CSVReader(inputFile);
 * 
 *       // or an input stream
 * 
 *       URL url = <some URL>;
 *       InputStream inputStream = url.openStream();
 *       CSVReader reader = new CSVReader(inputStream);
 * 
 *       // Read and process each row:
 * 
 *       while (reader.next()) {
 *             
 *          // Extract the fields
 *          
 *          String name = reader.getString(1);
 *          int empno = reader.getInteger(2);
 *          if (reader.wasNull()) {
 *             // Do something with the unexpected value in column 2
 *          }
 *          String phoneNumber = reader.getString(3);
 *          
 *          // etc.
 *       }
 *       
 *       // Close the reader and underlying stream
 *       
 *       reader.close();
 * </code>
 * </pre>
 * <p>
 * The first row of the .csv file is interpreted as column names, which
 * can be used instead of column numbers.
 * <p>
 * This class also be used on standalone strings by means of its static
 * {{@link #parse(String)} method.
 */
public class CSVReader {

   // ====================================================================
   // Class constants and variables
   // ====================================================================

   private static enum State {
      INIT,
      READING_STRING,
      READING_QUOTED_STRING,
      FOUND_QUOTE_IN_QUOTED_STRING,
   };
   private static final int BUFFER_SIZE = 65536;

   // ====================================================================
   // Class methods
   // ====================================================================

   /**
    * Parses the specified string as a line of comma-separated values
    * @param input a string to be parsed
    * @return a list of strings containing each token. Consecutive
    *         commas are parsed as an empty string
    */
   public static List<String> parse(String input) throws CSVException {

      // List to receive tokens

      final List<String> list = new ArrayList<String>();

      // String buffer for current token

      final StringBuilder sb = new StringBuilder();

      // Read and process each input character

      State state = State.INIT;

      for (int i = 0, n = input.length(); i < n; i++) {
         char c = input.charAt(i);
         switch (state) {

            case INIT:
               if (c == ',') {
                  list.add(sb.toString());
                  sb.setLength(0);
                  state = State.INIT;
               }
               else if (c == '"') {
                  sb.setLength(0);
                  state = State.READING_QUOTED_STRING;
               }
               else {
                  sb.append(c);
                  state = State.READING_STRING;
               }
               break;

            case READING_STRING:
               if (c == ',') {
                  list.add(sb.toString());
                  sb.setLength(0);
                  state = State.INIT;
               }
               else {
                  sb.append(c);
                  state = State.READING_STRING;
               }
               break;

            case READING_QUOTED_STRING:
               if (c == '"') {
                  state = State.FOUND_QUOTE_IN_QUOTED_STRING;
               }
               else {
                  sb.append(c);
                  state = State.READING_QUOTED_STRING;
               }
               break;

            case FOUND_QUOTE_IN_QUOTED_STRING:
               if (c == '"') {
                  sb.append('"');
                  state = State.READING_QUOTED_STRING;
               }
               else if (c == ',') {
                  list.add(sb.toString());
                  sb.setLength(0);
                  state = State.INIT;
               }
               else {
                  final String errmsg = String.format(
                        "Found %c in input after a quote in a quoted string",
                        c);
                  throw new CSVException(errmsg);
               }
               break;
         }
      }

      // Add the token in progress when the input ends

      list.add(sb.toString());

      // Done

      return list;
   }

   // ====================================================================
   // Instance variables
   // ====================================================================

   private final BufferedReader in;
   private final Map<String, Integer> columnNames = new HashMap<String, Integer>();
   private final int columnCount;

   private List<String> values;
   private boolean eof = false;
   private boolean wasNull = false;
   private DateFormat dateFormat = DateFormat.getDateInstance();

   // ====================================================================
   // Constructors
   // ====================================================================

   /**
    * Creates a new CSV reader for the specified input reader
    * @param reader an input reader
    * @throws CSVException if a parsing error occurs
    * @throws IOException if an I/O error occurs
    */
   public CSVReader(Reader reader) throws CSVException, IOException {

      this.in = new BufferedReader(reader, BUFFER_SIZE);

      // Read and store the column headings from the first row

      final String line = in.readLine();
      if (line == null) {
         final String errmsg = "No records were found in .csv file";
         throw new CSVException(errmsg);
      }
      int columnIndex = 0;
      for (final String columnName : parse(line)) {
         columnIndex++;
         columnNames.put(columnName, columnIndex);
      }
      this.columnCount = columnNames.size();
   }

   /**
    * Creates a new CSV reader for the specified input stream. This is a
    * convenience method that simply opens an InputStreamReader and
    * calls the Reader constructor.
    * @param inputStream an input stream over a .csv file
    * @throws FileNotFoundException if the file is not found
    * @throws CSVException if a parsing error occurs
    * @throws IOException if an I/O error occurs
    */
   public CSVReader(InputStream inputStream) throws CSVException, IOException {
      this(new InputStreamReader(inputStream));
   }

   /**
    * Creates a new CSV reader for the specified file. This is a
    * convenience method that simply opens a FileReader and calls the
    * Reader constructor.
    * @param inputFile an input .csv file
    * @throws IOException if the file is not found or an I/O error occurs
    * @throws CSVException if a parsing error occurs
    */
   public CSVReader(File inputFile) throws IOException, CSVException {
      this(new FileReader(inputFile));
   }

   // ====================================================================
   // Instance methods
   // ====================================================================

   /**
    * Advances the cursor one row from its current position. Note that
    * when a CSVReader is opened, its first row is automatically read
    * for column headers. So the first time <code>next()</code> is
    * called, it returns the first data row.
    * @return <code>true</code>, if there is a current row, or
    *         <code>false</code> if there are no more rows
    * @throws CSVException if a parsing error occurs
    * @throws IOException if an I/O error occurs
    */
   public boolean next() throws CSVException, IOException {

      // Do not try to read from a closed reader

      if (eof)
         return false;

      // Read the next line. If there are no more lines, return false

      final String line = in.readLine();
      if (line == null) {
         eof = true;
         return false;
      }

      // Parse the line and verify that it has enough tokens

      final List<String> tokens = parse(line);
      if (tokens.size() < columnCount) {
         final String errmsg = String.format(
               "Expected %d columns, found only %d",
               columnCount,
               tokens.size());
         throw new CSVException(errmsg);
      }

      // Store its column values

      this.values = tokens;

      // Report success

      return true;
   }

   /**
    * Returns the number of columns in the first row (the column names
    * row)
    */
   public int getColumnCount() {
      return this.columnCount;
   }

   /**
    * Given a column name, returns the column index
    * @param columnName a column name as found on the first row
    * @throws CSVException if the column name is invalid
    */
   public int getColumnIndex(String columnName) throws CSVException {
      if (columnName == null) {
         final String errmsg = "Column name cannot be null";
         throw new CSVException(errmsg);
      }
      final Integer columnIndex = columnNames.get(columnName);
      if (columnIndex == null) {
         final String errmsg = String
               .format("[%s] is not a valid column name", columnName);
         throw new CSVException(errmsg);
      }
      return columnIndex.intValue();
   }

   /**
    * Given a column index, returns the column name
    * @param columnIndex the column number (1, 2, ...)
    * @throws CSVException if the column index is invalid
    */
   public String getColumnName(int columnIndex) throws CSVException {
      validateColumnIndex(columnIndex);
      for (final String columnName : columnNames.keySet()) {
         if (columnNames.get(columnName).intValue() == columnIndex) {
            return columnName;
         }
      }
      final String errmsg = String.format(
            ""
                  + "BUG: %d is a valid column index,"
                  + " but there was no column name found there."
                  + "",
            columnIndex);
      throw new CSVException(errmsg);
   }

   /**
    * Returns the list of all column names
    */
   public List<String> getColumnNames() throws CSVException {
      final int n = getColumnCount();
      final List<String> list = new ArrayList<String>();
      for (int i = 0; i < n; i++) {
         final int columnIndex = i + 1;
         final String columnName = getColumnName(columnIndex);
         list.add(columnName);
      }
      return list;
   }

   /**
    * Returns the column with the specified column index as a string. No
    * trimming of leading or trailing whitespace is done. Sets
    * <code>wasNull</code> to true if the string was empty
    * @param columnIndex the column number (1, 2, ...)
    * @throws CSVException if the columnIndex is not valid or if an I/O
    *         error occurs
    */
   public String getString(int columnIndex) throws CSVException {
      validateColumnIndex(columnIndex);
      final String value = values.get(columnIndex - 1);
      this.wasNull = value.equals("");
      return value;
   }

   /**
    * Returns the column with the specified name as a string No trimming
    * of leading or trailing whitespace is done. Sets
    * <code>wasNull</code> to true if the string was empty
    * @param columnName the column name as found on the first row (case
    *        insensitive)
    * @throws CSVException if the columnName is not valid or if an I/O
    *         error occurs
    * @see #getString(int)
    */
   public String getString(String columnName) throws CSVException {
      return getString(getColumnIndex(columnName));
   }

   /**
    * Returns the column with the specified column index as an integer.
    * Sets <code>wasNull</code> to true if the trimmed value was an
    * empty string
    * @param columnIndex the column number (1, 2, ...)
    * @throws CSVException if the columnIndex is not valid, if an I/O
    *         error occurs, or if the column value was not an integer
    */
   public int getInteger(int columnIndex) throws CSVException {
      validateColumnIndex(columnIndex);
      final String value = values.get(columnIndex - 1);
      final String trimmedValue = value.trim();
      this.wasNull = trimmedValue.equals("");
      if (wasNull)
         return 0;
      try {
         final int intValue = Integer.parseInt(trimmedValue);
         return intValue;
      }
      catch (NumberFormatException e) {
         final String errmsg = String.format(
               "Value of column %d was [%s], not an integer",
               columnIndex,
               value);
         throw new CSVException(errmsg);
      }
   }

   /**
    * Returns the column with the specified name as an integer. Sets
    * <code>wasNull</code> to true if the trimmed value was an empty
    * string
    * @param columnName the column name as found on the first row (case
    *        insensitive)
    * @throws CSVException if the columnName is not valid or if an I/O
    *         error occurs
    * @see #getInteger(int)
    */
   public int getInteger(String columnName) throws CSVException {
      return getInteger(getColumnIndex(columnName));
   }

   /**
    * Returns the column with the specified column index as a Date. Sets
    * <code>wasNull</code> to true if the trimmed value was an empty
    * string
    * @param columnIndex the column number (1, 2, ...)
    * @throws CSVException if the columnIndex is not valid, if an I/O
    *         error occurs, or if the column value was not a valid Date
    */
   public Date getDate(int columnIndex) throws CSVException {
      validateColumnIndex(columnIndex);
      final String value = values.get(columnIndex - 1);
      final String trimmedValue = value.trim();
      this.wasNull = trimmedValue.equals("");
      if (wasNull)
         return null;
      try {
         final Date dateValue = dateFormat.parse(trimmedValue);
         return dateValue;
      }
      catch (ParseException e) {
         final String errmsg = String.format(
               "Value of column %d was [%s], not a valid date",
               columnIndex,
               value);
         throw new CSVException(errmsg);
      }
   }

   /**
    * Returns the column with the specified column name as a Date. Sets
    * <code>wasNull</code> to true if the trimmed value was an empty
    * string
    * @param columnName the column name
    * @throws CSVException if the column name is not valid, if an I/O
    *         error occurs, or if the column value was not a valid Date
    * @see #getDate(int)
    */
   public Date getDate(String columnName) throws CSVException {
      return getDate(getColumnIndex(columnName));
   }

   /**
    * Returns <code>true</code> if the last get operation on this row
    * found a null value.
    */
   public boolean wasNull() {
      return wasNull;
   }

   /**
    * Returns the date format
    * @return the date format, or <code>null</code>, if no date format
    *         exists
    */
   public DateFormat getDateFormat() {
      return dateFormat;
   }

   /**
    * Sets the date format
    * @param dateFormat a <code>DateFormat</code> object
    */
   public void setDateFormat(DateFormat dateFormat) {
      this.dateFormat = dateFormat;
   }

   /**
    * Closes the CSV reader and the underlying input stream
    * @throws IOException if unable to close stream
    */
   public void close() throws IOException {
      this.eof = true;
      in.close();
   }

   // ====================================================================
   // Private instance methods
   // ====================================================================

   /**
    * Checks whether the column index is within the range of 1 to
    * columnCount.
    * @param columnIndex a 1-based index
    * @throws CSVException if the column index is invalid
    */
   private void validateColumnIndex(int columnIndex) throws CSVException {
      if (columnIndex < 1 || columnIndex > columnCount) {
         final String errmsg = String.format(
               "Invalid column index %d. Must be between 1 and %d",
               columnIndex,
               columnCount);
         throw new CSVException(errmsg);
      }
   }
}
