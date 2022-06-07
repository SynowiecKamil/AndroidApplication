package synowiec.application.controller;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import synowiec.application.model.Physiotherapist;
import synowiec.application.model.Patient;

public class ResponseModel {

    /**
     * Our ResponseModel attributes
     */
    @SerializedName("result")
    private List<Physiotherapist> physiotherapists = new ArrayList();
    @SerializedName("resultPatient")
    private List<Patient> patients = new ArrayList();
    @SerializedName("code")
    private String code = "-1";
    @SerializedName("message")
    private String message = "UNKNOWN MESSAGE";

    /**
     * Generate Getter and Setters
     */
    public List<Physiotherapist> getResult() {
        return physiotherapists;
    }

    public void setResult(List<Physiotherapist> physiotherapists) { this.physiotherapists = physiotherapists; }

    public List<Patient> getResultPatient() {
        return patients;
    }

    public void setResultPatient(List<Patient> patients) { this.patients = patients; }

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
