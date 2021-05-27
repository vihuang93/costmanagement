package com.costs.costmanagement.repository;

import com.costs.costmanagement.dao.ShowEpisodeCostDAO;
import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CostsDbRepository {

    private ShowEpisodeCostDAO showEpisodeCostDAO;

    @Inject
    public CostsDbRepository(ShowEpisodeCostDAO showEpisodeCostDAO){
        this.showEpisodeCostDAO = showEpisodeCostDAO;
    }
    public List<ShowEpisodeCost> getAggregatEpisodeCostsForShow(Long id){
        List<ShowEpisodeCost> list = showEpisodeCostDAO.findAllEpisodeCostsByShowId(id);
        List<ShowEpisodeCost> aggregatedList = new ArrayList<>();
        // therefore group by episode code
        list.stream().collect(Collectors.groupingBy(ShowEpisodeCost::getEpisodeCd, Collectors.summingLong(ShowEpisodeCost::getAmount)))
  .forEach((cd,sumTargetCost)->aggregatedList.add(new ShowEpisodeCost(id, cd,sumTargetCost)));

        return aggregatedList;
    }

    public Optional<ShowEpisodeCost> createCost(Long id, Integer episodeCd, Long amount){
        ShowEpisodeCost newShowEpisodeCost = new ShowEpisodeCost(id, episodeCd, amount);
        int updatedRow = showEpisodeCostDAO.insertEpisodeCost(newShowEpisodeCost);
        if(updatedRow == 1){
            // success
            return Optional.of(newShowEpisodeCost);
        } else {
            return Optional.empty();
        }
    }
}
