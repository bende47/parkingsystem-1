package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, boolean applyReduction) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		ParkingType vehiculeType = ticket.getParkingSpot().getParkingType();

		double reduction;
		double inHour = ticket.getInTime().getTime();
		double outHour = ticket.getOutTime().getTime();

		double duration = (outHour - inHour) / (1000 * 60 * 60);

		if (applyReduction == true) {
			reduction = 0.95;
		} else {
			reduction = 1;
		}

		double calculateReduction = reduction * duration;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			if (duration > 0.5) {
				ticket.setPrice(calculateReduction * Fare.CAR_RATE_PER_HOUR);
			} else {
				ticket.setPrice(0);
			}
			break;
		}
		case BIKE: {
			if (duration > 0.5) {
				ticket.setPrice(calculateReduction * Fare.BIKE_RATE_PER_HOUR);
			} else {
				ticket.setPrice(0);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}

	}
}