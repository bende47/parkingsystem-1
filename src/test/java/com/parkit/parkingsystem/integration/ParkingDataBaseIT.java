package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	@DisplayName("test Parking Car")
	public void testParkingACar() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
		Ticket getTicketTest = ticketDAO.getTicket("ABCDEF");
		assertThat(getTicketTest.getVehicleRegNumber()).isEqualTo("ABCDEF");
		assertThat(getTicketTest.getParkingSpot().isAvailable()).isEqualTo(false);

	}

	@Test
	@DisplayName("test Parking Exit Car")
	public void testParkingLotExit() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/26 09:30");

		Ticket ticket = new Ticket();

		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticketDAO.saveTicket(ticket);

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		ParkingService parkingServiceOut = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingServiceOut.processExitingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		Ticket getTicketTest = ticketDAO.getTicket("ABCDEF");
		Date dateEntry = getTicketTest.getInTime();
		Date dateExit = getTicketTest.getOutTime();

		long duration = (dateExit.getTime() - dateEntry.getTime()) / (1000 * 3600);
		System.out.println(duration);

		long priceExpected = (long) Fare.CAR_RATE_PER_HOUR * duration;

		long price = (long) getTicketTest.getPrice();

		assertThat(priceExpected).isEqualTo(price);

	}

	@Test
	@DisplayName("Vehicle Already Enter")
	public void VehicleAlreadyEnterTest() throws Exception {
		ParkingService parkingServiceIn1 = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingServiceIn1.processIncomingVehicle();

		ParkingService parkingServiceIn2 = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingServiceIn2.processIncomingVehicle();

		int countUser = ticketDAO.countUser("ABCDEF");

		assertThat(countUser).isEqualTo(1);

	}

}
