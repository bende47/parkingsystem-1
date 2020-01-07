package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {

	private Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;
		
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {logger.error("Error closing next available slot", e);};
		    try { if (ps != null) ps.close(); } catch (Exception e) {logger.error("Error closing next available slot", e);};
		    try { if (con != null) con.close(); } catch (Exception e) {logger.error("Error closing next available slot", e);};
		}
		return result;
	}

	public boolean updateParking(ParkingSpot parkingSpot) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isItAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			dataBaseConfig.closePreparedStatement(ps);
			return (updateRowCount == 1);
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
			return false;
		} finally {
		    try { if (ps != null) ps.close(); } catch (Exception e) {logger.error("Error closing updating parking info", e);};
		    try { if (con != null) con.close(); } catch (Exception e) {logger.error("Error closing updating parking info", e);};
		}
	}
	
	public void setLogger(Logger testlogger) {
		this.logger = testlogger;
	}


}
