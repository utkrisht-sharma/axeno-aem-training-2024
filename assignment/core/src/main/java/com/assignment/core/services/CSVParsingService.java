package com.assignment.core.services;

import java.io.BufferedReader;
import java.util.List;

/**
 * Service interface for parsing CSV data.
 */
public interface CSVParsingService {
    /**
     * Parses the CSV data from a BufferedReader.
     */
    List<String[]> parseCSV(BufferedReader bufferedReader);
    /**
     * Validates a row of CSV data.
     */
    boolean isValidCsvData(String[] csvData);
}
