package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        
        long intime = ticket.getInTime().getTime();
        long outtime = ticket.getOutTime().getTime();

        //I apply an addition of absolute time in millisecands then divide with a float casting to obtain the number of hours with two decimals
        float duration = (float) ((outtime - intime)/3600000.00);
        if(duration > 0.5)
	        switch (ticket.getParkingSpot().getParkingType()){
	            case CAR: {
	                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
	                break;
	            }
	            case BIKE: {
	                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
	                break;
	            }
	
	            default: throw new IllegalArgumentException("Unkown Parking Type");
	        }
        else {
        	ticket.setPrice(0.0);
        }
    }
}