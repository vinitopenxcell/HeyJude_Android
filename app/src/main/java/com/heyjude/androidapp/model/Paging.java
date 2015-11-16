package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;

public class Paging {

    @Expose
    private Integer pageSize;
    @Expose
    private Integer pageOffset;
    @Expose
    private Integer numItemsOnPage;
    @Expose
    private Integer numItemsInTotal;
    @Expose
    private Integer numPages;

    /**
     * @return The pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize The pageSize
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return The pageOffset
     */
    public Integer getPageOffset() {
        return pageOffset;
    }

    /**
     * @param pageOffset The pageOffset
     */
    public void setPageOffset(Integer pageOffset) {
        this.pageOffset = pageOffset;
    }

    /**
     * @return The numItemsOnPage
     */
    public Integer getNumItemsOnPage() {
        return numItemsOnPage;
    }

    /**
     * @param numItemsOnPage The numItemsOnPage
     */
    public void setNumItemsOnPage(Integer numItemsOnPage) {
        this.numItemsOnPage = numItemsOnPage;
    }

    /**
     * @return The numItemsInTotal
     */
    public Integer getNumItemsInTotal() {
        return numItemsInTotal;
    }

    /**
     * @param numItemsInTotal The numItemsInTotal
     */
    public void setNumItemsInTotal(Integer numItemsInTotal) {
        this.numItemsInTotal = numItemsInTotal;
    }

    /**
     * @return The numPages
     */
    public Integer getNumPages() {
        return numPages;
    }

    /**
     * @param numPages The numPages
     */
    public void setNumPages(Integer numPages) {
        this.numPages = numPages;
    }

}