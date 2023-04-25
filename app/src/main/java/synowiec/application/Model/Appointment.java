package synowiec.application.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Appointment implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("physio_id")
    private String physioId;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    @SerializedName("city")
    private String city;

    @SerializedName("treatment")
    private String treatment;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getPatientId() {return patientId;    }

    public void setPatientId(String patientId) {this.patientId = patientId;    }

    public String getPhysioId() {return physioId;    }

    public void setPhysioId(String physioId) {this.physioId = physioId;    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() {return time;    }

    public void setTime(String time) {this.time = time;    }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getTreatment() { return treatment; }

    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getAddress() {       return address;    }

    public void setAddress(String address) {        this.address = address;    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", physioId='" + physioId + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", city='" + city + '\'' +
                ", treatment='" + treatment + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
