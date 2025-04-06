package lt.viko.eif.tpetrauskas.pricepredictionapp.repository;

import lt.viko.eif.tpetrauskas.pricepredictionapp.model.LinearRegressionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinearRegressionModelRepository extends JpaRepository<LinearRegressionModel, Long> {
    LinearRegressionModel findTopByOrderByIdDesc();
}
