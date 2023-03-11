package synowiec.application.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Appointment implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("patient_id")
    private String patient_id;

    @SerializedName("physio_id")
    private String physio_id;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    @SerializedName("place")
    private String place;

    @SerializedName("treatment")
    private String treatment;

    @SerializedName("cabinet_address")
    private String cabinet_address;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getPatient_id() {return patient_id;    }

    public void setPatient_id(String patient_id) {this.patient_id = patient_id;    }

    public String getPhysio_id() {return physio_id;    }

    public void setPhysio_id(String physio_id) {this.physio_id = physio_id;    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() {return time;    }

    public void setTime(String time) {this.time = time;    }

    public String getPlace() { return place; }

    public void setPlace(String place) { this.place = place; }

    public String getTreatment() { return treatment; }

    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getCabinet_address() {        return cabinet_address;    }

    public void setCabinet_address(String cabinet_address) {        this.cabinet_address = cabinet_address;    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", patient_id='" + patient_id + '\'' +
                ", physio_id='" + physio_id + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", place='" + place + '\'' +
                ", treatment='" + treatment + '\'' +
                ", cabinet_address='" + cabinet_address + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
