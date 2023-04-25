package synowiec.application.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PhysioTreatment implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("physioId")
    private String physioId;
    @SerializedName("treatmentId")
    private String treatmentId;
    @SerializedName("price")
    private double price;


    public PhysioTreatment(String id, String physioId, String treatmentId, double price) {
        this.id = id;
        this.physioId = physioId;
        this.treatmentId = treatmentId;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhysioId() {
        return physioId;
    }

    public void setPhysioId(String physioId) {
        this.physioId = physioId;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) { this.price = price; }
}