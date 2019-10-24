package com.company.roomie.models;

import java.io.Serializable;

public class Houses implements Serializable {

    private String house_id, house_title, house_description, house_type, user_id, house_banner,house_price;
    private String [] house_images;

    public Houses() {
    }

    public String getHouse_id() {
        return house_id;
    }

    public String getHouse_price() {
        return house_price;
    }

    public void setHouse_price(String house_price) {
        this.house_price = house_price;
    }

    public void setHouse_id(String house_id) {
        this.house_id = house_id;
    }

    public String getHouse_title() {
        return house_title;
    }

    public void setHouse_title(String house_title) {
        this.house_title = house_title;
    }

    public String getHouse_description() {
        return house_description;
    }

    public void setHouse_description(String house_description) {
        this.house_description = house_description;
    }

    public String getHouse_type() {
        return house_type;
    }

    public void setHouse_type(String house_type) {
        this.house_type = house_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getHouse_banner() {
        return house_banner;
    }

    public void setHouse_banner(String house_banner) {
        this.house_banner = house_banner;
    }

    public String[] getHouse_images() {
        return house_images;
    }

    public void setHouse_images(String[] house_images) {
        this.house_images = house_images;
    }
}
