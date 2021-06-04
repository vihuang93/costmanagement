package com.costs.costmanagement.datamodels;

import javax.persistence.*;

/*
 This is the db model for SHOW_EPISODE_COSTS
 */
@Entity
@Table(name = "SHOW_EPISODE_COSTS")
public class ShowEpisodeCost {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "SHOW_ID")
    private Long showId;

    @Column(name = "EPISODE_CODE")
    private String episodeCd;

    @Column(name = "AMOUNT")
    private Long amount;

    public ShowEpisodeCost(){}

    public ShowEpisodeCost(Long id, String episodeCd, Long amount){
        this.showId = id;
        this.episodeCd = episodeCd;
        this.amount = amount;
    }

    public Long getShowId() {
        return showId;
    }

    public void setID(Long id) {
        this.showId = id;
    }

    public String getEpisodeCd() {
        return episodeCd;
    }

    public void setEPISODE_CODE(String episodeCd) {
        this.episodeCd = episodeCd;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAMOUNT(Long amount) {
        this.amount = amount;
    }


}
