package synowiec.application.Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Patient implements Serializable {

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

    public Patient(String id, String name, String email, String photo, String surname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.surname = surname;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSurname() { return surname;    }

    public void setSurname(String surname) { this.surname = surname;    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}

