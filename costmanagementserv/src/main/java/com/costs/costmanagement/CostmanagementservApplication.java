package com.costs.costmanagement;

import com.costs.costmanagement.apimodels.ShowEpisodeCostAPIModel;
import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import com.costs.costmanagement.repository.CostsDbRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	// TODO: for show id, list aggregate costs for each episode
	@GetMapping("/costs/{id}")
	public ResponseEntity<List<ShowEpisodeCostAPIModel>> getBasicCost(Long id) {
		List<ShowEpisodeCost> doObjects = this.costsDbRepository.getAggregatEpisodeCostsForShow(id);
		List<ShowEpisodeCostAPIModel> costReport = doObjects.stream().map(doObject ->{
			ShowEpisodeCostAPIModel apiModel = new ShowEpisodeCostAPIModel();
			apiModel.setAmount(doObject.getAmount().toString());
			apiModel.setEpisode_code(doObject.getEpisodeCd().toString());
			apiModel.setId(doObject.getId().toString());
					return apiModel; }).collect(Collectors.toList());

		if (costReport == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(costReport);
		}
	}

	//TODO: store a episode cost for a show.
	@PostMapping("/costs")
	@ResponseBody
	public String createCost(@RequestBody ShowEpisodeCostAPIModel showEpisodeCost) {

		boolean writeSuccess = this.costsDbRepository.createCost(Long.parseLong(showEpisodeCost.getId()),
				Integer.parseInt(showEpisodeCost.getEpisode_code()),Long.parseLong(showEpisodeCost.getAmount()));
		//TODO: response object and error code
		if(writeSuccess){
			return "Success 200";
		} else {
			return "Fail 400";
		}
	}
}
