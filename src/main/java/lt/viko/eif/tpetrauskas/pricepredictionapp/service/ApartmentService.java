package lt.viko.eif.tpetrauskas.pricepredictionapp.service;

import lt.viko.eif.tpetrauskas.pricepredictionapp.model.Apartment;
import lt.viko.eif.tpetrauskas.pricepredictionapp.repository.ApartmentRepository;
import lt.viko.eif.tpetrauskas.pricepredictionapp.util.CSVReader;
import lt.viko.eif.tpetrauskas.pricepredictionapp.util.LinearRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentService {

    public static final String DATASET_PATH = "/Users/tauras/Documents/Projects/Java/PricePredictionApp/src/main/resources/dataset/apartments.csv";

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private CSVReader csvReader;

    @Autowired
    private LinearRegression linearRegression;

    public String importCSV(int startLine, int endLine) {
        apartmentRepository.saveAll(csvReader.readCSV(DATASET_PATH, startLine, endLine));
        return "Data is imported.";
    }

    public double predictPrice(String city, double squareFeet, double bedrooms, double bathrooms) {
        linearRegression.loadModelFromDatabase();
        return linearRegression.predict(city, squareFeet, bedrooms, bathrooms);
    }

    public String trainModel(boolean train) {
        String response = "Model is not trained.";
        if (train) {
            linearRegression.train(apartmentRepository.findAll());
            response = "Model is trained.";
        }
        return response;
    }

    public List<String> getAllCities() {
        return apartmentRepository.findAll()
                .stream()
                .map(Apartment::getCity)
                .distinct()
                .collect(Collectors.toList());
    }
}
