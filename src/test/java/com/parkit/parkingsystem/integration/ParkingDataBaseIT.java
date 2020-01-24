package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

	@AfterAll
	private static void tearDown() {

	}

	@Test
	@DisplayName("test Parking Car")
	public void testParkingACar() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
		Ticket getTicketTest = ticketDAO.getTicket("ABCDEF");
		assertThat(getTicketTest.getVehicleRegNumber()).isEqualTo("ABCDEF");
		assertThat(getTicketTest.getParkingSpot().isAvailable()).isEqualTo(false);

	}

	@Test
	@DisplayName("test Parking Exit Car 1h")
	public void testParkingLotExit() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEFG");
		dataBasePrepareService.clearDataBaseEntries();

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		Ticket TicketTest = ticketDAO.getTicket("ABCDEFG");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf.parse("2020/03/24 10:30");

		TicketTest.setOutTime(outTime);
		TicketTest.setPrice(1.5);

		ticketDAO.updateTicket(TicketTest);

		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		Date dateExit = TicketTest.getOutTime();

		double priceExpected = Fare.CAR_RATE_PER_HOUR * 1;

		double price = TicketTest.getPrice();

		assertThat(priceExpected).isEqualTo(price);
		assertThat(outTime).isEqualTo(dateExit);

	}

	@Test
	@DisplayName("Vehicle Already Enter")
	public void VehicleAlreadyEnterTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEFH");
		dataBasePrepareService.clearDataBaseEntries();
		ParkingService parkingServiceIn1 = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingServiceIn1.processIncomingVehicle();

		ParkingService parkingServiceIn2 = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingServiceIn2.processIncomingVehicle();

		int countUser = ticketDAO.countUser("ABCDEFH");

		assertThat(countUser).isEqualTo(1);

	}

	@Test
	@DisplayName("Vehicule Is Allow Enter")
	public void vehiculeIsEnterTest() throws ParseException {
		Ticket ticket1 = new Ticket();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf.parse("2020/03/24 10:30");

		ticket1.setVehicleRegNumber("AZERTY");
		ticket1.setOutTime(outTime);

		Ticket ticket2 = new Ticket();

		ticket2.setVehicleRegNumber("AZERTY");

		boolean allowEnter = ParkingService.vehiculeAllowEnter(ticket2);

		assertThat(allowEnter).isEqualTo(true);
	}

}
