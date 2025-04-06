package lt.viko.eif.tpetrauskas.pricepredictionapp.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LinearRegressionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private double[] weights;

    @ElementCollection
    @CollectionTable(name = "city_index_map")
    @MapKeyColumn(name = "city")
    @Column(name = "index")
    private Map<String, Integer> cityIndexMap;

    @Lob
    private double[] featureMeans;

    @Lob
    private double[] featureStdDevs;
}
