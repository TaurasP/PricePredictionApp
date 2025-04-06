package lt.viko.eif.tpetrauskas.pricepredictionapp.controller;

import lt.viko.eif.tpetrauskas.pricepredictionapp.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @PostMapping("/import")
    public String importCSV(@RequestParam int startLine, @RequestParam int endLine) {
        return apartmentService.importCSV(startLine, endLine);
    }

    @GetMapping("/predict")
    public double predictPrice(
            @RequestParam String city,
            @RequestParam double squareFeet,
            @RequestParam double bedrooms,
            @RequestParam double bathrooms) {
        return apartmentService.predictPrice(city, squareFeet, bedrooms, bathrooms);
    }

    @PostMapping("/train")
    public String trainModel(@RequestParam boolean train) {
        return apartmentService.trainModel(train);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(apartmentService.getAllCities());
    }
}
