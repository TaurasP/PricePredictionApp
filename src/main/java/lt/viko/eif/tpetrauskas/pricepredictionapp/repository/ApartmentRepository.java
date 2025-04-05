package lt.viko.eif.tpetrauskas.pricepredictionapp.repository;

import lt.viko.eif.tpetrauskas.pricepredictionapp.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
}
