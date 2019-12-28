package com.parkit.parkingsystem.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

@Tag("Fare_Tests")
public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;

	@Mock
	private Ticket ticket;
	private Boolean reduction = false;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();

	}

	@Test
	@DisplayName("Calculate the fare price according to the parkingtime spent for a car")
	public void calculateFareCar() throws ClassNotFoundException, SQLException {
		// ARRANGE
		Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// ACT
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket, reduction);
		// ASSERT
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Calculate the fare price according to the parkingtime spent for a bike")
	public void calculateFareBike() throws ClassNotFoundException, SQLException {
		// ARRANGE
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		// ACT
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket, reduction);
		// ASSERT
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("Send an exception for trying to calculate fareprice on unknow parking spot type")
	public void calculateFareUnkownType() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, reduction));
	}

	@Test
	@DisplayName("Send an exception for trying to calculate fareprice based on an earlier outime than intime")
	public void calculateFareBikeWithFutureInTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, reduction));
	}

	@Test
	@DisplayName("Calculate the fare price according to the parkingtime spent for a bike for less than 1 hour")
	public void calculateFareBikeWithLessThanOneHourParkingTime() throws ClassNotFoundException, SQLException {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket, reduction);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice(), 0.01);
	}

	@Test
	@DisplayName("Calculate the fare price according to the parkingtime spent for a car for less than 1 hour")
	public void calculateFareCarWithLessThanOneHourParkingTime() throws ClassNotFoundException, SQLException {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket, reduction);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice(), 0.01);
	}

	@Test
	@DisplayName("Calculate the fare price according to the parkingtime spent for a car for more than a day")
	public void calculateFareCarWithMoreThanADayParkingTime() throws ClassNotFoundException, SQLException {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (25 * 60 * 60 * 1000));// 24 hours parking time should give 24 *
																			// parking fare per hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket, reduction);
		assertEquals((25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice(), 0.01);
	}

	@Test
	@DisplayName("Verify that less than 30 minutes parkingtime is free")
	public void verifyLessThan30MinutesParkingTimeFree() throws ClassNotFoundException, SQLException {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket, reduction);

		assertEquals(0, ticket.getPrice());
	}

	@Test
	@DisplayName("verify 5% redution is applicated")
	public void numberOccurencesDBQuery() throws ClassNotFoundException, SQLException {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		reduction=true;
		fareCalculatorService.calculateFare(ticket, reduction);
		assertEquals(1.42, ticket.getPrice());
	}

}
