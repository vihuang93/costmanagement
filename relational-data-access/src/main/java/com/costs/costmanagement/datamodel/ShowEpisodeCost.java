package com.costs.costmanagement.datamodel;

public class ShowEpisodeCost {

    private Long id;
    private Integer episodeCd;
    private Long amount;

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
