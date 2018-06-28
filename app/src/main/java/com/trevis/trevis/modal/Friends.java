package models;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class Friends {
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
