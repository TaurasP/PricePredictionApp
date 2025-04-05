package lt.viko.eif.tpetrauskas.pricepredictionapp.util;

import lt.viko.eif.tpetrauskas.pricepredictionapp.model.Apartment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVReader {
    public List<Apartment> readCSV(String filePath, int startLine, int endLine) {
        List<Apartment> apartments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 0;

            while ((line = br.readLine()) != null) {
                currentLine++;
                if (currentLine < startLine) {
                    continue;
                }
                if (currentLine > endLine) {
                    break;
                }
                String[] values = line.split(";");
                if (values.length >= 1) {
                    try {
                        if (isValidNumber(values[5]) && isValidNumber(values[6]) &&
                                isValidNumber(values[11]) && isValidNumber(values[14]) &&
                                values[16] != null && !values[16].isEmpty()) {

                            Apartment apartment = new Apartment();
                            apartment.setBathrooms(Double.parseDouble(values[5]));
                            apartment.setBedrooms(Double.parseDouble(values[6]));
                            apartment.setPrice(Double.parseDouble(values[11]));
                            apartment.setSquareFeet(Double.parseDouble(values[14]));
                            apartment.setCity(values[16]);
                            apartments.add(apartment);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Line does not contain enough values: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apartments;
    }

    private boolean isValidNumber(String value) {
        try {
            return value != null && !value.isEmpty() && Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
