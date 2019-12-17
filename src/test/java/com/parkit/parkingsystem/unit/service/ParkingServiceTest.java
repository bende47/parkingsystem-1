package com.parkit.parkingsystem.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@Tag("ParkingProcess_Tests")
@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;
	@Mock
    private static Logger testlogger = LogManager.getLogger("ParkingService");
	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	private static ParkingSpot parkingSpot;

	private static Ticket ticket;

	private static int randomplace;

	private static String regNumberString;
	@Spy
	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	@BeforeEach
	private void setup() {
		try {
			byte[] RegNumber = new byte[7];
			new Random().nextBytes(RegNumber);
			regNumberString = new String(RegNumber, Charset.forName("UTF-8"));
			randomplace = new Random().nextInt(100) + 1;
			parkingSpot = new ParkingSpot(randomplace, ParkingType.CAR, false);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumberString);
			ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber(regNumberString);
			ticket.setPrice(10.5);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(testlogger);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void processExitingVehicleTest() throws Exception {
		// GIVEN
		doNothing().when(fareCalculatorService).calculateFare(any(Ticket.class));
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		// WHEN
		parkingService.processExitingVehicle();
		// THEN
		verify(ticketDAO, times(1)).getTicket(anyString());
		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
		verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
	}

	@Test
	public void processIncomingVehicleTest() {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(randomplace);
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
		assertEquals(parkingSpot.getId(), randomplace);
	}


	 @Test public void incomingVehiculeCouldTakeTheSameParkingSpotThanTheSameExitingVehicule(){
		 
	//GIVEN
	 //doNothing().when(fareCalculatorService).calculateFare(any(Ticket.class));
	 when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
	 when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
	 when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
	 
	//WHEN
	 parkingService.processExitingVehicle(); 
	 
	//THEN
	 verify(parkingSpotDAO, times(1)).updateParking(parkingSpot); 
	 assertTrue(parkingSpot.isItAvailable()); 
	 
	//GIVEN
	 when(inputReaderUtil.readSelection()).thenReturn(1);
	 when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(randomplace);
	 
	//WHEN
	 parkingService.processIncomingVehicle(); 
	 
	//THEN 
	 verify(parkingSpotDAO, times(2)).updateParking(parkingSpot); 
	 assertEquals(parkingSpot.getId(),randomplace); 
	 //assertTrue(parkingService.processIncomingVehicle().parkingSpot.isItAvailable()); 
	 }


	@Test
	public void shouldReturnException_ForOutimeBeforeIntimeAtExit() {
		// GIVEN
		ticket.setInTime(new Date(System.currentTimeMillis() + (60 * 60 * 1000)));
		when(ticketDAO.getTicket(regNumberString)).thenThrow(new RuntimeException());
		// WHEN
		assertThrows(RuntimeException.class, () -> {
			parkingService.processExitingVehicle();
		});
		// THEN
		verify(testlogger, times(1)).error(anyString(),any(Throwable.class));
		verify(ticketDAO, times(1)).getTicket(regNumberString);
	}
}
