package cs.virginia.edu.contactbook;

import java.io.Serializable;

/**
 * Created by Caleb on 1/16/2016.
 */
public class Phone implements Serializable {
    String work, home, mobile;

    public Phone(String work, String home, String mobile) {
        this.work = work;
        this.home = home;
        this.mobile = mobile;
    }

    public Phone() {
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return getHome().equals("") ? (getMobile().equals("") ? getWork() : getMobile() + "\n" + getWork())
                : (getMobile().equals("") ? getHome() + "\n" + getWork() : getHome() + "\n" + getMobile() +
                "\n" + getWork() + "\n");
    }
}
