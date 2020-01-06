package com.parkit.parkingsystem.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
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
	@Mock
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
			ticket.setInTime(new Date(1300000000L * 1000));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber(regNumberString);
			ticket.setPrice(0);
			ticket.setOutTime(null);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(testlogger);
			parkingService.setfarecalculatorservice(fareCalculatorService);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

    @Test
    @DisplayName("all process is call during exiting vehicle")
	public void processExitingVehicleTest() throws Exception {
		// GIVEN
		ArgumentCaptor<ParkingSpot> parkingSpotArgCapt = ArgumentCaptor.forClass(ParkingSpot.class);
		when(ticketDAO.availableReduction5Percent(any(Ticket.class))).thenReturn(true);
		doNothing().when(fareCalculatorService).calculateFare(any(Ticket.class), Mockito.anyBoolean());
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		// WHEN
		parkingService.processExitingVehicle();
		// THEN
		verify(ticketDAO, times(1)).getTicket(anyString());
		verify(ticketDAO, times(1)).availableReduction5Percent(any(Ticket.class));
		verify(parkingSpotDAO, times(1)).updateParking(parkingSpotArgCapt.capture());
		verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
		Assertions.assertThat(parkingSpotArgCapt.getValue().isItAvailable())
				.isEqualTo(true);
		Assertions.assertThat(parkingSpotArgCapt.getValue().getParkingType())
				.isEqualTo(ParkingType.CAR);
		Assertions.assertThat(parkingSpotArgCapt.getValue().getId())
				.isEqualTo(randomplace);

	}

    @Test
    @DisplayName("all process is called during incomming vehicle, and park on the specified parking spot")
	public void processIncomingVehicleTest() {
		// GIVEN
		ArgumentCaptor<Ticket> ticketArgCapt = ArgumentCaptor.forClass(Ticket.class);
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(randomplace);
		when(ticketDAO.noDoubleRegNumber(regNumberString)).thenReturn(true);
		when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
		verify(ticketDAO, times(1)).noDoubleRegNumber(regNumberString);
		verify(ticketDAO, times(1)).saveTicket(ticketArgCapt.capture());
		Assertions.assertThat(ticketArgCapt.getValue().getPrice())
				.isEqualTo(ticket.getPrice());
		Assertions.assertThat(ticketArgCapt.getValue().getVehicleRegNumber())
				.isEqualTo(ticket.getVehicleRegNumber());
		Assertions.assertThat(ticketArgCapt.getValue().getParkingSpot())
				.isEqualTo(ticket.getParkingSpot());
		Assertions.assertThat(ticketArgCapt.getValue().getInTime())
				.isAfter(ticket.getInTime());
		Assertions.assertThat(ticketArgCapt.getValue().getOutTime())
				.isEqualTo(ticket.getOutTime());
	}


    @Test
    @DisplayName("the same parking spot should turn to true during exiting then false during incoming")
	public void incomingVehiculeCouldTakeTheSameParkingSpotThanTheSameExitingVehicule() throws ClassNotFoundException, SQLException{
		 
	//GIVEN
	 when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
	 when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
	 when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
	 when(ticketDAO.availableReduction5Percent(any(Ticket.class))).thenReturn(true);
	 doNothing().when(fareCalculatorService).calculateFare(any(Ticket.class), Mockito.anyBoolean());
		
	//WHEN
	 parkingService.processExitingVehicle(); 
	 
	//THEN
	 verify(parkingSpotDAO, times(1)).updateParking(parkingSpot); 
	 assertTrue(parkingSpot.isItAvailable()); 
	 
	//GIVEN
	 when(inputReaderUtil.readSelection()).thenReturn(1);
	 when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(randomplace);
	 when(ticketDAO.noDoubleRegNumber(regNumberString)).thenReturn(true);
	 
	//WHEN
	 parkingService.processIncomingVehicle(); 
	 
	//THEN 
	 verify(parkingSpotDAO, times(2)).updateParking(parkingSpot); 
	 assertEquals(parkingSpot.getId(),randomplace);
	 }


	@Test
	@DisplayName("Should return exception if out-time is earlier than in-time")
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
