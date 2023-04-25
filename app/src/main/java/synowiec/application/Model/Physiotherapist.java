package synowiec.application.Model;

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
    private String professionNumber;
    @SerializedName("city")
    private String city;
    @SerializedName("description")
    private String description;
    @SerializedName("address")
    private String address;
    @SerializedName("days")
    private String days;
    @SerializedName("hours")
    private String hours;

    public Physiotherapist(String id, String name, String email, String password, String photo, String surname, String professionNumber, String city, String description, String address, String days, String hours) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.surname = surname;
        this.professionNumber = professionNumber;
        this.city = city;
        this.description = description;
        this.address = address;
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

    public String getProfessionNumber() {
        return professionNumber;
    }

    public void setProfessionNumber(String professionNumber) {        this.professionNumber = professionNumber;    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {return address;    }

    public void setAddress(String address) {        this.address = address;    }

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
                ", professionNumber='" + professionNumber + '\'' +
                ", city='" + city + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", days='" + days + '\'' +
                ", hours='" + hours + '\'' +
                '}';
    }
}
