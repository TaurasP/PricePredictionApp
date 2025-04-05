package lt.viko.eif.tpetrauskas.pricepredictionapp.controller;

import lt.viko.eif.tpetrauskas.pricepredictionapp.repository.ApartmentRepository;
import lt.viko.eif.tpetrauskas.pricepredictionapp.util.CSVReader;
import lt.viko.eif.tpetrauskas.pricepredictionapp.util.LinearRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {

    public static final String DATASET_PATH = "/Users/tauras/Documents/Projects/Java/PricePredictionApp/src/main/resources/dataset/apartments_for_rent_classified_100K.csv";

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private CSVReader csvReader;

    @Autowired
    private LinearRegression linearRegression;

    @PostMapping("/import")
    public String importCSV(@RequestParam int startLine, @RequestParam int endLine) {
        apartmentRepository.saveAll(csvReader.readCSV(DATASET_PATH, startLine, endLine));
        return "Data is imported.";
    }

    @GetMapping("/predict")
    public double predictPrice(
            @RequestParam String city,
            @RequestParam double squareFeet,
            @RequestParam double bedrooms,
            @RequestParam double bathrooms) {
        return linearRegression.predict(city, squareFeet, bedrooms, bathrooms);
    }

    @PostMapping("/train")
    public String importCSV(@RequestParam boolean train) {
        String response = "Model is not trained.";
        if (train) {
            linearRegression.train(apartmentRepository.findAll());
            response = "Model is trained.";
        }
        return response;
    }
}
