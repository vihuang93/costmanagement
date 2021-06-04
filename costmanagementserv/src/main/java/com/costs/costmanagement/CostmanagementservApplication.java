package com.costs.costmanagement;

import com.costs.costmanagement.apimodels.ShowEpisodeCostAPIModel;
import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import com.costs.costmanagement.repository.CostsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
public class CostmanagementservApplication {
	@Autowired
	private CostsDbRepository costsDbRepository;

	public static void main(String[] args) {
		SpringApplication.run(CostmanagementservApplication.class, args);
	}
	/**
	 *
	 * @param id show id
	 * @return list of aggregated episode cost, excluding amortized costs.
	 *
	 * This should work for both TASK 1 & 2
	 */

	@GetMapping("/costs/{id}")
	public ResponseEntity<List<ShowEpisodeCostAPIModel>> getBasicCost(@PathVariable Long id) {
		List<ShowEpisodeCost> doObjects = this.costsDbRepository.getAggregatEpisodeCostsForShowWithoutAmortizedCost(id);
		List<ShowEpisodeCostAPIModel> costReport = doObjects.stream().map(doObject ->{
			ShowEpisodeCostAPIModel apiModel = new ShowEpisodeCostAPIModel();
			apiModel.setAmount(doObject.getAmount());
			apiModel.setEpisode_code(Integer.parseInt(doObject.getEpisodeCd()));
			apiModel.setShow_id(doObject.getShowId());
					return apiModel; }).collect(Collectors.toList());

		if (costReport.size() == 0) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(costReport);
		}
	}
	/**
	 *
	 * @param showEpisodeCostList json object include list of show id, episode code, and cost amount
	 * @return created episode cost object
	 *
	 *  This should work for both TASK 1 & 2
	 */
	@PostMapping("/costs")
	@ResponseBody
	public ResponseEntity<Void> createCost(@RequestBody @Valid List<ShowEpisodeCostAPIModel> showEpisodeCostList) {
		List<ShowEpisodeCost> preparedInsertionList = new ArrayList<>();

		for(ShowEpisodeCostAPIModel showEpisodeCostAPIModel:showEpisodeCostList) {
			if(showEpisodeCostAPIModel.getEpisode_code() == null || showEpisodeCostAPIModel.getAmount() == null
					|| showEpisodeCostAPIModel.getShow_id() == null || String.valueOf(showEpisodeCostAPIModel.getEpisode_code()).length() != 3){
				return ResponseEntity.badRequest().build();
			}
			ShowEpisodeCost showEpisodeCostDO = new ShowEpisodeCost(showEpisodeCostAPIModel.getShow_id(), showEpisodeCostAPIModel.getEpisode_code().toString(),
					showEpisodeCostAPIModel.getAmount());
			preparedInsertionList.add(showEpisodeCostDO);
		}
		int rowUpdated = this.costsDbRepository.createListOfCosts(preparedInsertionList);
		if(rowUpdated > 0){
			URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
					.path("/costs")
					.buildAndExpand(Collections.emptyList())
					.toUri();
			return ResponseEntity.created(uri).build();
		} else {
			return ResponseEntity.ok().build();
		}

	}

	/**
	 *
	 * @param id show id
	 * @return list of aggregated episode cost, including amortized costs
	 *
	 * 	Get aggregated production cost for all episodes, including Amortized costs.
	 * 	This endpoint is for TASK 3
	 *
	 */
	@GetMapping("/prodcosts/{id}")
	public ResponseEntity<List<ShowEpisodeCostAPIModel>> getProductionCost(@PathVariable Long id) {
		List<ShowEpisodeCost> doObjects = this.costsDbRepository.getProductionCostsIncludingAmortizedCost(id);
		List<ShowEpisodeCostAPIModel> costReport = doObjects.stream().map(doObject ->{
			ShowEpisodeCostAPIModel apiModel = new ShowEpisodeCostAPIModel();
			apiModel.setAmount(doObject.getAmount());
			apiModel.setEpisode_code(Integer.parseInt(doObject.getEpisodeCd()));
			apiModel.setShow_id(doObject.getShowId());
			return apiModel; }).collect(Collectors.toList());

		if (costReport.size() == 0) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(costReport);
		}
	}


}
