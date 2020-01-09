package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, int count) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double reduction;
		double inHour = ticket.getInTime().getTime();
		double outHour = ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct

		double duration = (outHour - inHour) / (1000 * 60 * 60);

		if (count >= 1) {
			reduction = 0.95;
		} else {
			reduction = 1;
		}

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			if (duration > 0.5) {
				ticket.setPrice(reduction * duration * Fare.CAR_RATE_PER_HOUR);
			} else {
				ticket.setPrice(0);
			}
			break;
		}
		case BIKE: {
			if (duration > 0.5) {
				ticket.setPrice(reduction * duration * Fare.BIKE_RATE_PER_HOUR);
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