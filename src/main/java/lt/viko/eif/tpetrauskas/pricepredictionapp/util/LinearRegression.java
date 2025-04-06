package lt.viko.eif.tpetrauskas.pricepredictionapp.util;

import lt.viko.eif.tpetrauskas.pricepredictionapp.model.Apartment;
import lt.viko.eif.tpetrauskas.pricepredictionapp.model.LinearRegressionModel;
import lt.viko.eif.tpetrauskas.pricepredictionapp.repository.LinearRegressionModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LinearRegression {
    private static final int MAX_ITERATIONS = 100000;
    private static final double LEARNING_RATE = 0.001;

    private double[] weights;
    private Map<String, Integer> cityIndexMap;
    private double[] featureMeans;
    private double[] featureStdDevs;

    @Autowired
    private LinearRegressionModelRepository modelRepository;

    public void train(List<Apartment> apartments) {
        int n = apartments.size();
        int m = 5; // number of features (city, square feet, bedrooms, bathrooms, bias)

        // Create a map for encoding cities
        cityIndexMap = new HashMap<>();
        int cityIndex = 0;
        for (Apartment apartment : apartments) {
            if (!cityIndexMap.containsKey(apartment.getCity())) {
                cityIndexMap.put(apartment.getCity(), cityIndex++);
            }
        }

        double[][] features = new double[n][m];
        double[] prices = new double[n];

        for (int i = 0; i < n; i++) {
            Apartment apartment = apartments.get(i);
            features[i][0] = cityIndexMap.get(apartment.getCity());
            features[i][1] = apartment.getSquareFeet();
            features[i][2] = apartment.getBedrooms();
            features[i][3] = apartment.getBathrooms();
            features[i][4] = 1; // bias term
            prices[i] = apartment.getPrice();
        }

        // Normalize features
        featureMeans = new double[m];
        featureStdDevs = new double[m];
        for (int j = 0; j < m - 1; j++) { // Exclude bias term from normalization
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += features[i][j];
            }
            featureMeans[j] = sum / n;

            double sumSq = 0;
            for (int i = 0; i < n; i++) {
                sumSq += Math.pow(features[i][j] - featureMeans[j], 2);
            }
            featureStdDevs[j] = Math.sqrt(sumSq / n);

            for (int i = 0; i < n; i++) {
                features[i][j] = (features[i][j] - featureMeans[j]) / featureStdDevs[j];
            }
        }

        weights = new double[m];

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            double[] gradients = new double[m];
            for (int i = 0; i < n; i++) {
                double prediction = predict(features[i]);
                double error = prediction - prices[i];

                for (int j = 0; j < m; j++) {
                    gradients[j] += error * features[i][j];
                }
            }

            for (int j = 0; j < m; j++) {
                weights[j] -= LEARNING_RATE * gradients[j] / n;
            }

            // Debugging: Print the gradients to check for NaN values
            System.out.println("Gradients at iteration " + iteration + ": " + java.util.Arrays.toString(gradients));
        }

        // Log weights for debugging
        System.out.println("Weights initialized: " + java.util.Arrays.toString(weights));

        // Save the trained model to the database
        saveModelToDatabase();
    }

    public double predict(String city, double squareFeet, double bedrooms, double bathrooms) {
        city = formatCityName(city);
        if (weights == null) {
            throw new IllegalStateException("The model has not been trained yet. Please call the train method before making predictions.");
        }

        if (!cityIndexMap.containsKey(city)) {
            throw new IllegalArgumentException("City not found in training data: " + city);
        }

        double[] features = new double[weights.length];
        features[0] = (cityIndexMap.get(city) - featureMeans[0]) / featureStdDevs[0];
        features[1] = (squareFeet - featureMeans[1]) / featureStdDevs[1];
        features[2] = (bedrooms - featureMeans[2]) / featureStdDevs[2];
        features[3] = (bathrooms - featureMeans[3]) / featureStdDevs[3];
        features[4] = 1; // bias term

        return predict(features);
    }

    private double predict(double[] features) {
        double prediction = 0;
        for (int i = 0; i < features.length; i++) {
            prediction += features[i] * weights[i];
        }
        return prediction;
    }

    private void saveModelToDatabase() {
        LinearRegressionModel model = new LinearRegressionModel();
        model.setWeights(weights);
        model.setCityIndexMap(cityIndexMap);
        model.setFeatureMeans(featureMeans);
        model.setFeatureStdDevs(featureStdDevs);
        modelRepository.save(model);
    }

    public void loadModelFromDatabase() {
        LinearRegressionModel model = modelRepository.findTopByOrderByIdDesc();
        if (model != null) {
            weights = model.getWeights();
            cityIndexMap = model.getCityIndexMap();
            featureMeans = model.getFeatureMeans();
            featureStdDevs = model.getFeatureStdDevs();
        } else {
            throw new IllegalStateException("No trained model found in the database.");
        }
    }

    public String formatCityName(String city) {
        if (city == null || city.isEmpty()) {
            return city;
        }
        String[] words = city.split("\\s+");
        StringBuilder formattedCity = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formattedCity.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return formattedCity.toString().trim();
    }
}
