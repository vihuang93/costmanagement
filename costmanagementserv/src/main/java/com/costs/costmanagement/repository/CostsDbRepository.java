package com.costs.costmanagement.repository;

import com.costs.costmanagement.dao.ShowEpisodeCostDAO;
import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class CostsDbRepository {

    private ShowEpisodeCostDAO showEpisodeCostDAO;

    @Inject
    public CostsDbRepository(ShowEpisodeCostDAO showEpisodeCostDAO){
        this.showEpisodeCostDAO = showEpisodeCostDAO;
    }
    public List<ShowEpisodeCost> getAggregatEpisodeCostsForShowWithoutAmortizedCost(Long id){
        List<ShowEpisodeCost> list = showEpisodeCostDAO.findAllEpisodeCostsByShowId(id);
        List<ShowEpisodeCost> aggregatedList = new ArrayList<>();
        // remove amortized cost, remove episode cost with episode code starting with "0"
        Predicate<ShowEpisodeCost> isAmortizedCost = cost -> String.valueOf(cost.getEpisodeCd()).startsWith("0");
        List<ShowEpisodeCost> amortizedCosts = list.stream().filter(isAmortizedCost).collect(Collectors.toList());
        list.removeAll(amortizedCosts);
        // therefore group by episode code
        list.stream().collect(Collectors.groupingBy(ShowEpisodeCost::getEpisodeCd, Collectors.summingLong(ShowEpisodeCost::getAmount)))
  .forEach((cd,sumTargetCost)->aggregatedList.add(new ShowEpisodeCost(id, cd,sumTargetCost)));

        return aggregatedList;
    }

    public List<ShowEpisodeCost> getProductionCostsIncludingAmortizedCost(Long id){
        List<ShowEpisodeCost> list = showEpisodeCostDAO.findAllEpisodeCostsByShowId(id);

        Predicate<ShowEpisodeCost> isAmortizedCost = cost -> String.valueOf(cost.getEpisodeCd()).startsWith("0");
        List<ShowEpisodeCost> amortizedCosts = list.stream().filter(isAmortizedCost).collect(Collectors.toList());
        list.removeAll(amortizedCosts);
        // season -> amortizedCost map, now the value is total amortizedCost per season
        Map<Integer, Long> seasonAmortizedCostMap = new HashMap<>();
        // eg: guaranteed 3 digit, 001, two digits after 0 indicate the season
        for(ShowEpisodeCost showEpisodeCost:amortizedCosts){
            String seasonStr = showEpisodeCost.getEpisodeCd().substring(1);
            // remove possible leading 0 from seasonStr,
            int season = seasonStr.startsWith("0") ? Integer.parseInt(seasonStr.substring(1))
                    : Integer.parseInt(seasonStr);
            seasonAmortizedCostMap.put(season, seasonAmortizedCostMap.getOrDefault(season, 0L) + showEpisodeCost.getAmount());
        }

        List<ShowEpisodeCost> aggregatedList = new ArrayList<>();
        // therefore group by episode code
        list.stream().collect(Collectors.groupingBy(ShowEpisodeCost::getEpisodeCd, Collectors.summingLong(ShowEpisodeCost::getAmount)))
                .forEach((cd,sumTargetCost)->aggregatedList.add(new ShowEpisodeCost(id, cd,sumTargetCost)));
        // season -> count of episode per season
        Map<Integer, Integer> seasonEpisodeCountMap = new HashMap<>();
        // map every unique episode to its season
        for(ShowEpisodeCost episodeCost:aggregatedList){
            int season = episodeCost.getEpisodeCd().charAt(0) - '0';
            seasonEpisodeCountMap.put(season, seasonEpisodeCountMap.getOrDefault(season, 0) + 1);
        }
        // update amortizedMap to season -> amortized cost per episode
        for(Map.Entry<Integer, Long> entry:seasonAmortizedCostMap.entrySet()){
            int season = entry.getKey();
            long totalAmortizedCost = entry.getValue();
            long amortizedCostPerEpisode = totalAmortizedCost/seasonEpisodeCountMap.get(season);
            seasonAmortizedCostMap.put(season, amortizedCostPerEpisode);
        }
        // add amortized per episode to each episode
        for(ShowEpisodeCost episodeCost:aggregatedList){
            int season = episodeCost.getEpisodeCd().charAt(0) - '0';
            if(seasonAmortizedCostMap.containsKey(season)){
                long productionCost = episodeCost.getAmount() + seasonAmortizedCostMap.get(season);
                episodeCost.setAMOUNT(productionCost);
            }
        }
        return aggregatedList;
    }

    public Optional<ShowEpisodeCost> createCost(Long id, String episodeCd, Long amount){
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
