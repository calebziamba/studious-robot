package cs.virginia.edu.contactbook;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Caleb on 1/16/2016.
 */

@SuppressWarnings("serial")
public class Contact implements Serializable, Comparable<Contact> {
    String name, company, detailsURL, smallImageURL, largeImageURL, email, website;
    Address homeAddr;
    Phone phone;
    long birthdate;
    int id;
    // private Bitmap smallImage, largeImage;

    public Contact() {
        phone = new Phone();
    }

    public Contact(String name, Bitmap smImg) {
        this.name = name;
        //  smallImage = smImg;
        phone = new Phone("555-5555", "555-5555", "555-5555");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDetailsURL() {
        return detailsURL;
    }

    public void setDetailsURL(String detailsURL) {
        this.detailsURL = detailsURL;
    }

    public String getSmallImageURL() {
        return smallImageURL;
    }

    public void setSmallImageURL(String smallImageURL) {
        this.smallImageURL = smallImageURL;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public void setLargeImageURL(String largeImageURL) {
        this.largeImageURL = largeImageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Address getHomeAddr() {
        return homeAddr;
    }

    public void setHomeAddr(Address homeAddr) {
        this.homeAddr = homeAddr;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        Date date = new Date(birthdate * 1000L);
        SimpleDateFormat simpledate = new SimpleDateFormat("MM-dd-yyyy");
        return simpledate.format(date);
    }

    public void setBirthdate(long birthdate) {
        this.birthdate = birthdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Contact another) {
        return this.getName().compareTo(another.getName());
    }
}
