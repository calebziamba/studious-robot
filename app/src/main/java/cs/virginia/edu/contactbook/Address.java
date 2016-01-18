package cs.virginia.edu.contactbook;

import java.io.Serializable;

/**
 * Created by Caleb on 1/16/2016.
 */
public class Address implements Serializable {
    String street, city, state, country, zip;
    int latitude, longitude;

    public Address(String street, String city, String state, String country, String zip, int latitude, int longitude) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return street + "\n" + city + ", " + state + "\n" + country
                + " " + zip;
    }
}
