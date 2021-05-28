package com.costs.costmanagement.repository;

import com.costs.costmanagement.dao.ShowEpisodeCostDAO;
import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Named
public class CostsDbRepositoryTest {
    @Mock
    private ShowEpisodeCostDAO showEpisodeCostDAO;

    @InjectMocks
    private CostsDbRepository costsDbRepository;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAggregatedCostReportWithoutAmortizedCostNonEpisodic() {
        // all costs with episode cd 101
        List<ShowEpisodeCost> list = createNewShowEpisodeCostListWithoutAmortizedCostNonEpisodic();

        // return a non-episodic show's all costs
        when(this.showEpisodeCostDAO.findAllEpisodeCostsByShowId(anyLong())).thenReturn(list);
        List<ShowEpisodeCost> res = this.costsDbRepository.getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong());
        verify(this.showEpisodeCostDAO, times(1)).findAllEpisodeCostsByShowId(anyLong());
        assertThat(res, is(notNullValue()));
        assertThat(res.size(), is(1));
        assertThat(res.get(0).getAmount(), is(600L));
    }

    @Test
    public void testGetAggregatedCostReportWithoutAmortizedCostEpisodic() {
        // create a episodic show cost list
        List<ShowEpisodeCost> list = createNewShowEpisodeCostListWithoutAmortizedCostEpisodic();

        // return a episodic show's all costs
        when(this.showEpisodeCostDAO.findAllEpisodeCostsByShowId(anyLong())).thenReturn(list);
        List<ShowEpisodeCost> res = this.costsDbRepository.getAggregatEpisodeCostsForShowWithoutAmortizedCost(anyLong());
        verify(this.showEpisodeCostDAO, times(1)).findAllEpisodeCostsByShowId(anyLong());
        assertThat(res, is(notNullValue()));
        assertThat(res.size(), is(3));
        for(ShowEpisodeCost aggregatedCost:res){
            switch (aggregatedCost.getEpisodeCd()) {
                case "101":
                    assertThat(aggregatedCost.getAmount(), is(400L));
                    break;
                case "102":
                    assertThat(aggregatedCost.getAmount(), is(300L));
                    break;
                case "103":
                    assertThat(aggregatedCost.getAmount(), is(200L));
                    break;
            }
        }
    }

    @Test
    public void testGetAggregatedCostReportIncludingAmortizedCost() {
        // create an episodic show with 2 amortized cost

        List<ShowEpisodeCost> list = createNewShowEpisodeCostListWithAmortizedCostEpisodic();

        // return a episodic show's all costs
        when(this.showEpisodeCostDAO.findAllEpisodeCostsByShowId(anyLong())).thenReturn(list);
        List<ShowEpisodeCost> res = this.costsDbRepository.getProductionCostsIncludingAmortizedCost(anyLong());
        verify(this.showEpisodeCostDAO, times(1)).findAllEpisodeCostsByShowId(anyLong());
        assertThat(res, is(notNullValue()));
        assertThat(res.size(), is(3));
//        season 1 total amortized cost = 300
//        proportioned amortized cost = 300/3 = 100
//        Episode 101: 400 + 100 = 500
//        Episode 102: 300 + 100 = 400
//        Episode 103: 200 + 100 = 300
        for(ShowEpisodeCost aggregatedCost:res){
            switch (aggregatedCost.getEpisodeCd()) {
                case "101":
                    assertThat(aggregatedCost.getAmount(), is(500L));
                    break;
                case "102":
                    assertThat(aggregatedCost.getAmount(), is(400L));
                    break;
                case "103":
                    assertThat(aggregatedCost.getAmount(), is(300L));
                    break;
            }
        }
    }

    @Test
    public void testCreatedCostEpisodeCost() {

        // 1 impacted row, meaning insertion did happen
        when(this.showEpisodeCostDAO.insertEpisodeCost(any())).thenReturn(1);
        Optional<ShowEpisodeCost> res = this.costsDbRepository.createCost(1L, "101", 100L);
        verify(this.showEpisodeCostDAO, times(1)).insertEpisodeCost(any());
        assertThat(res, is(notNullValue()));
        assertThat(res.isPresent(), is(true));
    }

    @Test
    public void testCreatedCostEpisodeCostNOOP() {

        // 0 impacted row, meaning no row inserted
        when(this.showEpisodeCostDAO.insertEpisodeCost(any())).thenReturn(0);
        Optional<ShowEpisodeCost> res = this.costsDbRepository.createCost(1L, "101", 100L);
        verify(this.showEpisodeCostDAO, times(1)).insertEpisodeCost(any());
        assertThat(res, is(notNullValue()));
        assertThat(res.isPresent(), is(false));
    }

    private List<ShowEpisodeCost> createNewShowEpisodeCostListWithoutAmortizedCostNonEpisodic() {
		// show id = 1 is a non-episodic show, all episode is 101. report should return {"101", 600}
		ShowEpisodeCost newShowEpisodeCost1 = createNewShowEpisodeCost(1L, "101", 200L);
		ShowEpisodeCost newShowEpisodeCost2 = createNewShowEpisodeCost(1L, "101", 100L);
		ShowEpisodeCost newShowEpisodeCost3 = createNewShowEpisodeCost(1L, "101", 300L);

		List<ShowEpisodeCost> list = Arrays.asList(newShowEpisodeCost1, newShowEpisodeCost2, newShowEpisodeCost3);
		return list;

	}

    private List<ShowEpisodeCost> createNewShowEpisodeCostListWithoutAmortizedCostEpisodic() {
        // show id = 2 is a non-episodic show, all episode is 101. report should return {"101", 400}
                                                                                    // {"102", 300}
                                                                                    // {"103", 200}

        ShowEpisodeCost newShowEpisodeCost4 = createNewShowEpisodeCost(2L, "101", 200L);
        ShowEpisodeCost newShowEpisodeCost5 = createNewShowEpisodeCost(2L, "102", 300L);
        ShowEpisodeCost newShowEpisodeCost6 = createNewShowEpisodeCost(2L, "103", 200L);
        ShowEpisodeCost newShowEpisodeCost7 = createNewShowEpisodeCost(2L, "101", 200L);
        return Arrays.asList(
                newShowEpisodeCost4, newShowEpisodeCost5, newShowEpisodeCost6, newShowEpisodeCost7);

    }

    private List<ShowEpisodeCost> createNewShowEpisodeCostListWithAmortizedCostEpisodic() {
        // show id = 2 is a non-episodic show, all episode is 101. report should return {"101", 400 + 100}
        // {"102", 300 + 100}
        // {"103", 200 + 100}

        ShowEpisodeCost newShowEpisodeCost4 = createNewShowEpisodeCost(2L, "101", 200L);
        ShowEpisodeCost newShowEpisodeCost5 = createNewShowEpisodeCost(2L, "102", 300L);
        ShowEpisodeCost newShowEpisodeCost6 = createNewShowEpisodeCost(2L, "103", 200L);
        ShowEpisodeCost newShowEpisodeCost7 = createNewShowEpisodeCost(2L, "101", 200L);

        ShowEpisodeCost amortizedCost1 = createNewShowEpisodeCost(2L,"001",200L);
        ShowEpisodeCost amortizedCost2 = createNewShowEpisodeCost(2L,"001",100L);

        List<ShowEpisodeCost> list = new ArrayList<>();
        list.add(newShowEpisodeCost4);
        list.add(newShowEpisodeCost5);
        list.add(newShowEpisodeCost6);
        list.add(newShowEpisodeCost7);
        list.add(amortizedCost1);
        list.add(amortizedCost2);
        return list;
    }
    private ShowEpisodeCost createNewShowEpisodeCost(long id, String episodeCd, long amount) {
        ShowEpisodeCost newShowEpisodeCost = new ShowEpisodeCost();
        newShowEpisodeCost.setID(id);
        newShowEpisodeCost.setEPISODE_CODE(episodeCd);
        newShowEpisodeCost.setAMOUNT(amount);
        return newShowEpisodeCost;
    }

}
