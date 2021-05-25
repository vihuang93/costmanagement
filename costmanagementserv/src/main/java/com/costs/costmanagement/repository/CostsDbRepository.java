package com.costs.costmanagement.repository;

import com.costs.costmanagement.dao.ShowEpisodeCostDAO;
import com.costs.costmanagement.datamodel.ShowEpisodeCost;
import org.springframework.stereotype.Repository;
import javax.inject.Inject;

import java.util.List;

@Repository
public class CostsDbRepository {

    private ShowEpisodeCostDAO showEpisodeCostDAO;

    @Inject
    public CostsDbRepository(ShowEpisodeCostDAO showEpisodeCostDAO){
        this.showEpisodeCostDAO = showEpisodeCostDAO;
    }
    public List<ShowEpisodeCost> getAggregatEpisodeCostsForShow(Long id){
        List<ShowEpisodeCost> list = showEpisodeCostDAO.findAllEpisodeCostsByShowId(id);
        // TODO: add up all costs for one episode and return: episodeCd, aggregateAmount
        return list;
    }

    public boolean createCost(Long id, Integer episodeCd, Long amount){
        ShowEpisodeCost newShowEpisodeCost = new ShowEpisodeCost(id, episodeCd, amount);
        int updatedRow = showEpisodeCostDAO.insertEpisodeCost(newShowEpisodeCost);
        if(updatedRow == 1){
            // success
            return true;
        } else {
            return false;
        }
    }
}
