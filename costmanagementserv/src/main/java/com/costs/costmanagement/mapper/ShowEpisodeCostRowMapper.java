package com.costs.costmanagement.mapper;

import com.costs.costmanagement.datamodels.ShowEpisodeCost;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowEpisodeCostRowMapper implements RowMapper<ShowEpisodeCost> {
    @Override
    public ShowEpisodeCost mapRow(ResultSet resultSet, int i) throws SQLException {
        ShowEpisodeCost showEpisodeCost = new ShowEpisodeCost();
        showEpisodeCost.setID(resultSet.getLong("ID"));
        showEpisodeCost.setEPISODE_CODE(resultSet.getInt("EPISODE_CODE"));
        showEpisodeCost.setAMOUNT(resultSet.getLong("AMOUNT"));
        return showEpisodeCost;
    }
}
