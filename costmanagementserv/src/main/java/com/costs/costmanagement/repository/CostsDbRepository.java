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


    /**
     *
     * @param id show id
     * @return list of aggregated episode cost, excluding amortized costs.
     *
     * Calculation logic: group episodes by episode code, add the cost amount together for each unique episode code.
     *
     */
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

    /**
     *
     * @param id show id
     * @return list of aggregated episode cost, including amortized costs.
     *
     * Calculation logic:
     * each production cost of an episode = episode cost + proportion of amortized cost
     * proportion of amortized cost = total amortized costs for a season/episodes of a season
     */
    public List<ShowEpisodeCost> getProductionCostsIncludingAmortizedCost(Long id){
        List<ShowEpisodeCost> list = showEpisodeCostDAO.findAllEpisodeCostsByShowId(id);

        Predicate<ShowEpisodeCost> isAmortizedCost = cost -> String.valueOf(cost.getEpisodeCd()).startsWith("0");
        List<ShowEpisodeCost> amortizedCosts = list.stream().filter(isAmortizedCost).collect(Collectors.toList());
        list.removeAll(amortizedCosts);
        // [key -> value]
        // season -> total amortizedCost per season
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
        // group by episode code
        list.stream().collect(Collectors.groupingBy(ShowEpisodeCost::getEpisodeCd, Collectors.summingLong(ShowEpisodeCost::getAmount)))
                .forEach((cd,sumTargetCost)->aggregatedList.add(new ShowEpisodeCost(id, cd,sumTargetCost)));
        // [key -> value]
        // season -> count of episode per season
        Map<Integer, Integer> seasonEpisodeCountMap = new HashMap<>();
        // map every unique episode to its season
        for(ShowEpisodeCost episodeCost:aggregatedList){
            int season = episodeCost.getEpisodeCd().charAt(0) - '0';
            seasonEpisodeCountMap.put(season, seasonEpisodeCountMap.getOrDefault(season, 0) + 1);
        }
        // update the value of the map
        // [key -> value]
        // season -> amortized cost PER episode (based on how many episodes we have for each season)
        for(Map.Entry<Integer, Long> entry:seasonAmortizedCostMap.entrySet()){
            int season = entry.getKey();
            long totalAmortizedCost = entry.getValue();
            long amortizedCostPerEpisode = totalAmortizedCost/seasonEpisodeCountMap.get(season);
            seasonAmortizedCostMap.put(season, amortizedCostPerEpisode);
        }
        // add proportioned amortized cost to each episode if there is one.
        for(ShowEpisodeCost episodeCost:aggregatedList){
            int season = episodeCost.getEpisodeCd().charAt(0) - '0';
            if(seasonAmortizedCostMap.containsKey(season)){
                long productionCost = episodeCost.getAmount() + seasonAmortizedCostMap.get(season);
                episodeCost.setAMOUNT(productionCost);
            }
        }
        return aggregatedList;
    }

    /**
     *
     * @param id show id
     * @param episodeCd episode code, includes season and episode info
     * @param amount single cost of this transaction
     * @return Optional<ShowEpisodeCost> returns an optional if 0 row updated.
     */
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
