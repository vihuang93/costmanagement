package com.costs.costmanagement;

import com.costs.costmanagement.apimodels.ShowEpisodeCostAPIModel;
import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import com.costs.costmanagement.repository.CostsDbRepository;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Named
@RunWith(MockitoJUnitRunner.class)
public class CostmanagementservApplicationTests {

	@Mock
	private CostsDbRepository costsDbRepository;

	@InjectMocks
	private CostmanagementservApplication service;

	@BeforeMethod
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetBasicCostReportForANonExistingShow() {

		//return empty cost list
		when(this.costsDbRepository.getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong())).thenReturn(new ArrayList<>());
		ResponseEntity<List<ShowEpisodeCostAPIModel>> res = this.service.getBasicCost(1L);
		verify(this.costsDbRepository, times(1)).getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong());
		assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}

	@Test
	public void testGetProductionCostReportForANonExistingShow() {

		//return empty cost list
		when(this.costsDbRepository.getProductionCostsIncludingAmortizedCost(anyLong())).thenReturn(new ArrayList<>());
		ResponseEntity<List<ShowEpisodeCostAPIModel>> res = this.service.getProductionCost(1L);
		verify(this.costsDbRepository, times(1)).getProductionCostsIncludingAmortizedCost(anyLong());
		assertThat(res.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}

	@Test
	public void testGetCostReportForANonEpisodicShow() {

		ShowEpisodeCost newShowEpisodeCost1 = createNewShowEpisodeCost(1L, "101", 200L);
		List<ShowEpisodeCost> list = Arrays.asList(newShowEpisodeCost1);

		when(this.costsDbRepository.getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong())).thenReturn(list);
		ResponseEntity<List<ShowEpisodeCostAPIModel>> res = this.service.getBasicCost(1L);
		verify(this.costsDbRepository, times(1)).getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong());
		assertThat(res.getStatusCode(), is(HttpStatus.OK));
		assertThat(res.getBody(), is(notNullValue()));
		assertThat(res.getBody().size(), is(1));
		assertThat(res.getBody().get(0), is(notNullValue()));
		assertThat(res.getBody().get(0).getId(), is("1"));
		assertThat(res.getBody().get(0).getEpisode_code(), is("101"));
		assertThat(res.getBody().get(0).getAmount(), is("200"));
	}

	private List<ShowEpisodeCost> createNewShowEpisodeCostListWithoutAmortizedCost() {
		// show id = 1 is a non-episodic show, all episode is 101. report should return {"101", 600}
		ShowEpisodeCost newShowEpisodeCost1 = createNewShowEpisodeCost(1L, "101", 200L);
		ShowEpisodeCost newShowEpisodeCost2 = createNewShowEpisodeCost(1L, "101", 100L);
		ShowEpisodeCost newShowEpisodeCost3 = createNewShowEpisodeCost(1L, "101", 300L);
		// show id = 1 is a non-episodic show, all episode is 101. report should return {"101", 600}
		ShowEpisodeCost newShowEpisodeCost4 = createNewShowEpisodeCost(2L, "101", 200L);
		ShowEpisodeCost newShowEpisodeCost5 = createNewShowEpisodeCost(2L, "102", 300L);
		ShowEpisodeCost newShowEpisodeCost6 = createNewShowEpisodeCost(2L, "103", 200L);
		ShowEpisodeCost newShowEpisodeCost7 = createNewShowEpisodeCost(2L, "101", 200L);
		List<ShowEpisodeCost> list = Arrays.asList(newShowEpisodeCost1, newShowEpisodeCost2, newShowEpisodeCost3,
				newShowEpisodeCost4, newShowEpisodeCost5, newShowEpisodeCost6, newShowEpisodeCost7);
		return list;

	}

	private ShowEpisodeCost createNewShowEpisodeCost(long id, String episodeCd, long amount) {
		ShowEpisodeCost newShowEpisodeCost = new ShowEpisodeCost();
		newShowEpisodeCost.setID(id);
		newShowEpisodeCost.setEPISODE_CODE(episodeCd);
		newShowEpisodeCost.setAMOUNT(amount);
		return newShowEpisodeCost;
	}

	@Test
	public void testGetCostReportForAEpisodicShow() {

		ShowEpisodeCost newShowEpisodeCost1 = createNewShowEpisodeCost(1L, "101", 200L);
		ShowEpisodeCost newShowEpisodeCost2 = createNewShowEpisodeCost(1L, "102", 300L);
		List<ShowEpisodeCost> list = Arrays.asList(newShowEpisodeCost1, newShowEpisodeCost2);

		when(this.costsDbRepository.getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong())).thenReturn(list);
		ResponseEntity<List<ShowEpisodeCostAPIModel>> res = this.service.getBasicCost(1L);
		verify(this.costsDbRepository, times(1)).getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong());
		assertThat(res.getStatusCode(), is(HttpStatus.OK));
		assertThat(res.getBody(), is(notNullValue()));
		assertThat(res.getBody().size(), is(2));
		assertThat(res.getBody().get(0), is(notNullValue()));
		assertThat(res.getBody().get(0).getId(), is("1"));
		assertThat(res.getBody().get(0).getEpisode_code(), is("101"));
		assertThat(res.getBody().get(0).getAmount(), is("200"));

		assertThat(res.getBody().get(1), is(notNullValue()));
		assertThat(res.getBody().get(1).getId(), is("1"));
		assertThat(res.getBody().get(1).getEpisode_code(), is("102"));
		assertThat(res.getBody().get(1).getAmount(), is("300"));
	}

	@Test
	public void testGetCostReportForAEpisodicShowIncludingAmortizedCost() {
		ShowEpisodeCost newShowEpisodeCost1 = createNewShowEpisodeCost(1L, "101", 200L);
		List<ShowEpisodeCost> list = Arrays.asList(newShowEpisodeCost1);

		when(this.costsDbRepository.getProductionCostsIncludingAmortizedCost(anyLong())).thenReturn(list);
		ResponseEntity<List<ShowEpisodeCostAPIModel>> res = this.service.getProductionCost(1L);
		verify(this.costsDbRepository, times(1)).getProductionCostsIncludingAmortizedCost(anyLong());
		assertThat(res.getStatusCode(), is(HttpStatus.OK));
		assertThat(res.getBody(), is(notNullValue()));
		assertThat(res.getBody().size(), is(1));
		assertThat(res.getBody().get(0), is(notNullValue()));
		assertThat(res.getBody().get(0).getId(), is("1"));
		assertThat(res.getBody().get(0).getEpisode_code(), is("101"));
		assertThat(res.getBody().get(0).getAmount(), is("200"));
	}

	private ShowEpisodeCostAPIModel createNewShowEpisodeCostApiModel(long id, String episodeCd, long amount) {
		ShowEpisodeCostAPIModel newShowEpisodeCost = new ShowEpisodeCostAPIModel();
		newShowEpisodeCost.setId(String.valueOf(id));
		newShowEpisodeCost.setEpisode_code(episodeCd);
		newShowEpisodeCost.setAmount(String.valueOf(amount));
		return newShowEpisodeCost;
	}
}
