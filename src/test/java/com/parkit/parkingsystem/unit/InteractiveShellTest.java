package com.parkit.parkingsystem.unit;

import org.mockito.Mock;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class InteractiveShellTest {

	boolean continueApp = true;
    static InputReaderUtil inputReaderUtil = new InputReaderUtil();
    static ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
    static TicketDAO ticketDAO = new TicketDAO();
    @Mock
	static ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	
	
    public static void shellShouldBeCallOnlyOneTime(){
/*
    	//Given
    	doNothing().when(parkingService).processIncomingVehicle();
    	doNothing().when(parkingService).processExitingVehicle();
    	
    	//WHEN
    	InteractiveShell.loadInterface();
    	*/
 
    }
}
