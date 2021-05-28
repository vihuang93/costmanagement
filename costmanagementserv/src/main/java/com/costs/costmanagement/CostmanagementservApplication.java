package com.costs.costmanagement;

import com.costs.costmanagement.apimodels.ShowEpisodeCostAPIModel;
import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import com.costs.costmanagement.repository.CostsDbRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
			apiModel.setAmount(doObject.getAmount().toString());
			apiModel.setEpisode_code(doObject.getEpisodeCd().toString());
			apiModel.setId(doObject.getId().toString());
					return apiModel; }).collect(Collectors.toList());

		if (costReport.size() == 0) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(costReport);
		}
	}
	/**
	 *
	 * @param showEpisodeCostListStr json object include list of show id, episode code, and cost amount
	 * @return created episode cost object
	 *
	 *  This should work for both TASK 1 & 2
	 */
	@PostMapping("/costs")
	@ResponseBody
	public ResponseEntity<List<ShowEpisodeCostAPIModel>> createCost(@RequestBody @Valid String showEpisodeCostListStr) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<ShowEpisodeCostAPIModel> showEpisodeCostList;
		try {
			showEpisodeCostList = objectMapper.readValue(showEpisodeCostListStr, new TypeReference<List<ShowEpisodeCostAPIModel>>() {
			});
		} catch (JsonProcessingException e){
			return ResponseEntity.badRequest().build();
		}
		List<ShowEpisodeCostAPIModel> createdList = new ArrayList<>();
		for(ShowEpisodeCostAPIModel showEpisodeCostAPIModel:showEpisodeCostList) {
			if(showEpisodeCostAPIModel.getEpisode_code() == null || showEpisodeCostAPIModel.getAmount() == null
					|| showEpisodeCostAPIModel.getId() == null || showEpisodeCostAPIModel.getEpisode_code().length() != 3){
				return ResponseEntity.badRequest().build();
			}
			Optional<ShowEpisodeCost> createdEpisodeCost = this.costsDbRepository.createCost(Long.parseLong(showEpisodeCostAPIModel.getId()), showEpisodeCostAPIModel.getEpisode_code(),
					Long.parseLong(showEpisodeCostAPIModel.getAmount()));
			if(createdEpisodeCost.isPresent()){
				ShowEpisodeCostAPIModel createdEpisodeCostApi = new ShowEpisodeCostAPIModel();
				createdEpisodeCostApi.setId(String.valueOf(createdEpisodeCost.get().getId()));
				createdEpisodeCostApi.setEpisode_code(String.valueOf(createdEpisodeCost.get().getEpisodeCd()));
				createdEpisodeCostApi.setAmount(String.valueOf(createdEpisodeCost.get().getAmount()));

				createdList.add(createdEpisodeCostApi);
			}
		}
		if(createdList.size() > 0){
			URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
					.path("/costs")
					.buildAndExpand(createdList)
					.toUri();
			return ResponseEntity.created(uri).body(createdList);
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
			apiModel.setAmount(doObject.getAmount().toString());
			apiModel.setEpisode_code(doObject.getEpisodeCd());
			apiModel.setId(doObject.getId().toString());
			return apiModel; }).collect(Collectors.toList());

		if (costReport.size() == 0) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(costReport);
		}
	}


}
