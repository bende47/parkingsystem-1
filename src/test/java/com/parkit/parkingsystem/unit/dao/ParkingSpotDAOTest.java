package com.parkit.parkingsystem.unit.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

	private static ParkingSpotDAO parkingSpotDAO;
	private static ParkingSpot parkingSpot;
	private static DataBaseTestConfig dataBaseConfig = new DataBaseTestConfig();
	private static DataBaseTestConfig dataBaseConfig2 = new DataBaseTestConfig();
	private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
	
	@Mock
	private static Logger testlogger = LogManager.getLogger("TicketDAO");
	@Mock
	private static DataBaseTestConfig dataBaseConfigMock = new DataBaseTestConfig();
	
	
	private static String regNumberString;
	private static int randomplace;

	@BeforeEach
	private void setUp() {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseConfig;
		parkingSpotDAO.setLogger(testlogger);
		byte[] RegNumber = new byte[7];
		new Random().nextBytes(RegNumber);
		regNumberString = new String(RegNumber, Charset.forName("UTF-8"));
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterEach
	private void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	public void shouldReturnParkingSpotNumberOne() {
		// ACT
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		
		// ASSERT
		assertEquals(1, result);
	}
	
	@Test
	public void shouldReturnParkingSpotNumber3After2VehiculeIncoming() {
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);
		// ACT
		parkingSpotDAO.updateParking(parkingSpot);
		parkingSpotDAO.updateParking(parkingSpot2);
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		
		// ASSERT
		assertEquals(3, result);
	}
	
	@Test
	public void shouldReturnParkingSpotNumber2After2VehiculeIncomingAndFirstExiting() {
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);
		// ACT
		parkingSpotDAO.updateParking(parkingSpot);
		parkingSpotDAO.updateParking(parkingSpot2);
		parkingSpot.setAvailable(true);
		parkingSpotDAO.updateParking(parkingSpot);
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		
		// ASSERT
		assertEquals(1, result);
	}
/*	
	@Test
	public void shouldReturnFalseIfAVehicleNumberIsRecordedTwice() {
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);
		// ACT
		parkingSpotDAO.updateParking(parkingSpot);
		parkingSpotDAO.updateParking(parkingSpot2);
		parkingSpot.setAvailable(true);
		parkingSpotDAO.updateParking(parkingSpot);
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		
		// ASSERT
		assertEquals(1, result);
	}
	*/
}