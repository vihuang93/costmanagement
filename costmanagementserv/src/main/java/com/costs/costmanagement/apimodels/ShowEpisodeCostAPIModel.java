package com.costs.costmanagement.apimodels;

/*
 This is the API model for SHOW_EPISODE_COSTS
 */

public class ShowEpisodeCostAPIModel {
    String id;
    String episode_code;
    String amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEpisode_code() {
        return episode_code;
    }

    public void setEpisode_code(String episode_code) {
        this.episode_code = episode_code;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}