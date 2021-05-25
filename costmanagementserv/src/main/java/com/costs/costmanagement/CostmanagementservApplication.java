package com.costs.costmanagement;

import com.costs.costmanagement.datamodel.ShowEpisodeCost;
import com.costs.costmanagement.repository.CostsDbRepository;
import javax.inject.Inject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class CostmanagementservApplication {

	@Inject
	private CostsDbRepository costsDbRepository;

	public static void main(String[] args) {
		SpringApplication.run(CostmanagementservApplication.class, args);
	}
	// TODO: for show id, list aggregate costs for each episode
	@GetMapping("/costs/{id}")
	public List<ShowEpisodeCost> getBasicCost(Long id) {

		return this.costsDbRepository.getAggregatEpisodeCostsForShow(id);

	}

	//TODO: store a episode cost for a show.
	@PostMapping("/costs/{id}")
	public String createCost(Long id, int episodeCd, Long amount) {

		boolean writeSuccess = this.costsDbRepository.createCost(id, episodeCd, amount);
		//TODO: response object and error code
		if(writeSuccess){
			return "Success 200";
		} else {
			return "Fail 400";
		}
	}
}
