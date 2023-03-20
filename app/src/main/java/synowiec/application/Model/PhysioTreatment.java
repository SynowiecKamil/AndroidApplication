package synowiec.application.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PhysioTreatment implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("physioID")
    private String physioID;
    @SerializedName("treatmentID")
    private String treatmentID;
    @SerializedName("price")
    private double price;


    public PhysioTreatment(String id, String physioID, String treatmentID) {
        this.id = id;
        this.physioID = physioID;
        this.treatmentID = treatmentID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhysioID() {
        return physioID;
    }

    public void setPhysioID(String physioID) {
        this.physioID = physioID;
    }

    public String getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) { this.price = price; }
}