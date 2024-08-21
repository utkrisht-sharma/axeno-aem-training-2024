package com.assignment.core.services.Impl;

import com.assignment.core.services.CSVParsingService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the CSVParsingService interface for parsing and validating CSV data.
 */
@Component(service = CSVParsingService.class)
public class CSVParsingServiceImpl implements CSVParsingService {

    private static final Logger LOG = LoggerFactory.getLogger(CSVParsingServiceImpl.class);

    /**
     * Parses CSV data from the provided BufferedReader.
     */
    @Override
    public List<String[]> parseCSV(BufferedReader bufferedReader) {
        List<String[]> parsedData = new ArrayList<>();
        try {
            String line;
            boolean isHeader = true;
            while ((line = bufferedReader.readLine()) != null) {
                if (isHeader) {
                    LOG.info("Skipping header line");
                    isHeader = false;
                    continue;
                }
                LOG.info("Parsing line: {}", line);
                String[] csvData = line.split(",");

                if (isValidCsvData(csvData)) {
                    parsedData.add(csvData);
                } else {
                    LOG.warn("Invalid CSV data: {}", String.join(",", csvData));
                }
            }
        } catch (Exception e) {
            LOG.error("Error parsing CSV", e);
        }
        return parsedData;
    }
    /**
     * Validates a row of CSV data.
     */
    @Override
    public boolean isValidCsvData(String[] csvData) {
        if (csvData.length < 5) {
            LOG.error("CSV line does not contain enough columns: {}", String.join(",", csvData));
            return false;
        }
        for (String data : csvData) {
            if (data == null || data.trim().isEmpty()) {
                LOG.error("Invalid CSV data found: {}", String.join(",", csvData));
                return false;
            }
        }
        return true;
    }
}
