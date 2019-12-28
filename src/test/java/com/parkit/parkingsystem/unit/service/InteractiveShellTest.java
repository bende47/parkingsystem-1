package com.parkit.parkingsystem.unit.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;


@Tag("IShell_Tests")
@ExtendWith(MockitoExtension.class)
public class InteractiveShellTest {

	@Mock
    static InputReaderUtil inputReaderUtil;
    @Mock
    static ParkingSpotDAO parkingSpotDAO;
    @Mock
    static TicketDAO ticketDAO;
    @Mock
	static ParkingService parkingService;
    
    
	@BeforeEach
	private void setup() {
		inputReaderUtil = new InputReaderUtil();
		parkingSpotDAO = new ParkingSpotDAO();
		ticketDAO = new TicketDAO();
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	}
	
    @Test
    @DisplayName("verify if Interactiveshell is well browse")
    public static void shellShouldBeCallOnlyOneTime(){
    	//Arrange
    	doNothing().when(parkingService).processIncomingVehicle();
    	doNothing().when(parkingService).processExitingVehicle();
    	when(inputReaderUtil.readSelection()).thenReturn(1).thenReturn(2).thenReturn(3);
    	//Act
    	InteractiveShell.loadInterface();
    	//Assert
		verify(parkingService, times(1)).processIncomingVehicle();
		verify(parkingService, times(1)).processExitingVehicle();
    }
    
    
    
}
