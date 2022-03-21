package synowiec.application.controller;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import synowiec.application.model.Physiotherapist;
import synowiec.application.model.Patient;

public class ResponseModel {

    @SerializedName("resultPhysio")
    List<Physiotherapist> physiotherapists;

    @SerializedName("resultPatient")
    List<Patient> patients;

    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    public List<Physiotherapist> getPhysiotherapists() {
        return physiotherapists;
    }

    public void setPhysiotherapists(List<Physiotherapist> physiotherapists) {
        this.physiotherapists = physiotherapists;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

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
