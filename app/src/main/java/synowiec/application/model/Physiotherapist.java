package synowiec.application.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Physiotherapist implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("photo")
    private String photo;
    @SerializedName("surname")
    private String surname;
    @SerializedName("profession_number")
    private String profession_number;
    @SerializedName("cabinet")
    private String cabinet;
    @SerializedName("description")
    private String description;
    @SerializedName("cabinet_address")
    private String cabinet_address;
    @SerializedName("days")
    private String days;
    @SerializedName("hours")
    private String hours;

    public Physiotherapist(String id, String name, String email, String password, String photo, String surname, String profession_number, String cabinet, String description, String cabinet_address, String days, String hours) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.surname = surname;
        this.profession_number = profession_number;
        this.cabinet = cabinet;
        this.description = description;
        this.cabinet_address = cabinet_address;
        this.days = days;
        this.hours = hours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {return photo;}

    public void setPhoto(String photo) { this.photo = photo;}

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfession_number() {
        return profession_number;
    }

    public void setProfession_number(String profession_number) {        this.profession_number = profession_number;    }

    public String getCabinet() {
        return cabinet;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCabinet_address() {return cabinet_address;    }

    public void setCabinet_address(String cabinet_address) {        this.cabinet_address = cabinet_address;    }

    public String getDays() {        return days;    }

    public void setDays(String days) {        this.days = days;    }

    public String getHours() {        return hours;    }

    public void setHours(String hours) {        this.hours = hours;    }

    @Override
    public String toString() {
        return "Physiotherapist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", surname='" + surname + '\'' +
                ", profession_number='" + profession_number + '\'' +
                ", cabinet='" + cabinet + '\'' +
                ", description='" + description + '\'' +
                ", cabinet_address='" + cabinet_address + '\'' +
                ", days='" + days + '\'' +
                ", hours='" + hours + '\'' +
                '}';
    }
}
