package com.costs.costmanagement.datamodels;

import javax.persistence.*;

/*
 This is the db model for SHOW_EPISODE_COSTS
 */
@Entity
@Table(name = "SHOW_EPISODE_COSTS")
public class ShowEpisodeCost {

    @Id
    @Column
    private Long id;

    @Column
    private Integer episodeCd;

    @Column
    private Long amount;

    public ShowEpisodeCost(){}

    public ShowEpisodeCost(Long id, Integer episodeCd, Long amount){
        this.id = id;
        this.episodeCd = episodeCd;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    public Integer getEpisodeCd() {
        return episodeCd;
    }

    public void setEPISODE_CODE(Integer episodeCd) {
        this.episodeCd = episodeCd;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAMOUNT(Long amount) {
        this.amount = amount;
    }


}
