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

import java.net.URI;
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
	 * Get aggregated production cost for all episodes, without Amortized costs. This endpoint is for TASK3
	 *  This should work for both TASK 1 & 2
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

	// This should work for both TASK 1 & 2
	@PostMapping("/costs")
	@ResponseBody
	public ResponseEntity<ShowEpisodeCostAPIModel> createCost(@RequestBody ShowEpisodeCostAPIModel showEpisodeCost) {

		Optional<ShowEpisodeCost> createdShowEpisodeCost = this.costsDbRepository.createCost(Long.parseLong(showEpisodeCost.getId()),
				showEpisodeCost.getEpisode_code(),Long.parseLong(showEpisodeCost.getAmount()));
		// 201 created
		if(createdShowEpisodeCost.isPresent()){
			URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
					.path("/costs")
					.buildAndExpand(showEpisodeCost.getId())
					.toUri();
			// map db object to api object
			ShowEpisodeCostAPIModel showEpisodeCostAPIModel = new ShowEpisodeCostAPIModel();
			showEpisodeCostAPIModel.setId(String.valueOf(createdShowEpisodeCost.get().getId()));
			showEpisodeCostAPIModel.setEpisode_code(String.valueOf(createdShowEpisodeCost.get().getEpisodeCd()));
			showEpisodeCostAPIModel.setAmount(String.valueOf(createdShowEpisodeCost.get().getAmount()));

			return ResponseEntity.created(uri).body(showEpisodeCostAPIModel);
		} else {
			// 200 ok
			return ResponseEntity.ok().build();
		}
	}

	/**
	 * Get aggregated production cost for all episodes, including Amortized costs. This endpoint is for TASK3
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
