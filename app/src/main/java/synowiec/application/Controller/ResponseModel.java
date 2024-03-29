package synowiec.application.Controller;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import synowiec.application.Model.Appointment;
import synowiec.application.Model.Physiotherapist;
import synowiec.application.Model.Patient;
import synowiec.application.Model.Treatment;

public class ResponseModel {

    /**
     * Our ResponseModel attributes
     */
    @SerializedName("result")
    private List<Physiotherapist> physiotherapists = new ArrayList();
    @SerializedName("resultPatient")
    private List<Patient> patients = new ArrayList();
    @SerializedName("resultTreatment")
    private List<Treatment> treatments = new ArrayList();
    @SerializedName("resultAppointment")
    private List<Appointment> appointments = new ArrayList();
    @SerializedName("code")
    private String code = "-1";
    @SerializedName("message")
    private String message = "UNKNOWN MESSAGE";

    /**
     * Generate Getters and Setters
     */
    public List<Physiotherapist> getPhysiotherapists() {
        return physiotherapists;
    }

    public void setPhysiotherapists(List<Physiotherapist> physiotherapists) { this.physiotherapists = physiotherapists; }

    public List<Patient> getPatients() { return patients; }

    public void setPatients(List<Patient> patients) { this.patients = patients; }

    public List<Treatment> getTreatments() { return treatments; }

    public void setTreatments(List<Treatment> treatments) { this.treatments = treatments; }

    public List<Appointment> getAppointments() { return appointments; }

    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
