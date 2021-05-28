package com.costs.costmanagement.dao;

import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import com.costs.costmanagement.mapper.ShowEpisodeCostRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 This is the DAO layer SHOW_EPISODE_COSTS
 */

@Repository
public class ShowEpisodeCostDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String SELECT_BY_SHOW_ID = "SELECT * FROM SHOW_EPISODE_COSTS WHERE ID = ?";

    private static final String INSERT_SQL = "INSERT INTO SHOW_EPISODE_COSTS (ID, EPISODE_CODE, AMOUNT) VALUES (?,?,?) ";

    public List<ShowEpisodeCost> findAllEpisodeCostsByShowId(final Long id) throws DataAccessException {
        List<ShowEpisodeCost> showEpisodeCostList = jdbcTemplate.query(SELECT_BY_SHOW_ID,
                new ShowEpisodeCostRowMapper(), id);
        return showEpisodeCostList;
    }
    public int insertEpisodeCost(ShowEpisodeCost showEpisodeCost) throws DataAccessException {
        int updated = jdbcTemplate.update(INSERT_SQL, showEpisodeCost.getId(), showEpisodeCost.getEpisodeCd(), showEpisodeCost.getAmount());
        return updated;
    }
}
