package com.costs.costmanagement.apimodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;

/*
 This is the API model for SHOW_EPISODE_COSTS
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowEpisodeCostAPIModel {

    @JsonProperty("show_id")
    @Size(min = 1, max = 16, message = "INVALID_STRING_LENGTH")
    Long show_id;

    @JsonProperty("episode_code")
    @Size(min = 3, max = 3, message = "INVALID_STRING_LENGTH")
    Integer episode_code;

    @JsonProperty("amount")
    Long amount;

    public Long getShow_id() {
        return show_id;
    }

    public void setShow_id(Long show_id) {
        this.show_id = show_id;
    }

    public Integer getEpisode_code() {
        return episode_code;
    }

    public void setEpisode_code(Integer episode_code) {
        this.episode_code = episode_code;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

}
