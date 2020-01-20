package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

@DisplayName("Fare calculate test")
public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	private boolean applyReduction = false;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	@DisplayName("calculate Fare car 1h")
	public void calculateFareCar() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:30");

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("calculate Fare bike 1h")
	public void calculateFareBike() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:30");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	@DisplayName("calculate Fare Unkown Type")
	public void calculateFareUnkownType() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:30");
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, applyReduction));
	}

	@Test
	@DisplayName("calculate Fare Bike With Future InTime")
	public void calculateFareBikeWithFutureInTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1983/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:30");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, applyReduction));
	}

	@Test
	@DisplayName("calculate Fare Bike <1h")
	public void calculateFareBikeWithLessThanOneHourParkingTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:15");

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	@DisplayName("calculate Fare Care <1h")
	public void calculateFareCarWithLessThanOneHourParkingTime() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:15");

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	@DisplayName("calculate Fare Bike <30min")
	public void calculateFareBikeWithLessThanHalfHourParkingTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 09:40");

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals(0, ticket.getPrice());
	}

	@Test
	@DisplayName("calculate Fare Care <30min")
	public void calculateFareCarWithLessThanHalfHourParkingTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 09:40");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals(0, ticket.getPrice());
	}

	@Test
	@DisplayName("calculate Fare Care >1day")
	public void calculateFareCarWithMoreThanADayParkingTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/26 09:30");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals((48 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	@DisplayName("calculate Fare Bike >1day")
	public void calculateFareBikeWithMoreThanADayParkingTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/26 09:30");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals((48 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	@DisplayName("calculate Fare Care Recuring User 1h")
	public void calculateFareCarRecuringUser() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:30");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		boolean applyReduction = true;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR * 0.95);
	}

	@Test
	@DisplayName("calculate Fare Bike Recuring User 1h")
	public void calculateFareBikeRecuringUser() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date inTime = sdf.parse("1982/03/24 09:30");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date outTime = sdf1.parse("1982/03/24 10:30");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		boolean applyReduction = true;

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, applyReduction);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR * 0.95);
	}

}
