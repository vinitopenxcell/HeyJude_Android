package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;

public class Category {

    @Expose
    private String name;
    @Expose
    private Integer id;
    @Expose
    private Integer rank;

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The rank
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * @param rank The rank
     */
    public void setRank(Integer rank) {
        this.rank = rank;
    }

}