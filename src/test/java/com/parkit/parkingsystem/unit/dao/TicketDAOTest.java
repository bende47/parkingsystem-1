package com.parkit.parkingsystem.unit.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

	private static TicketDAO ticketDAO;

    private static Ticket ticket;
    
	@Mock
    private static Logger testlogger = LogManager.getLogger("TicketDAO");
	@Mock
    private static Logger testlogger3 = LogManager.getLogger("TicketDAO");
	@Mock
	private static DataBaseTestConfig dataBaseConfigMock = new DataBaseTestConfig();
    private static DataBaseTestConfig dataBaseConfig = new DataBaseTestConfig();
    private static DataBaseTestConfig dataBaseConfig2 = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
    private static TicketDAO ticketDAO2;
    private static String regNumberString;
    
    @BeforeEach
    private void setUp(){
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseConfig;
        ticketDAO.setLogger(testlogger);
        dataBasePrepareService.clearDataBaseEntries();
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(new ParkingSpot(3, ParkingType.CAR,false));
        ticket.setVehicleRegNumber(regNumberString = "ABCDEF");
        ticket.setPrice(12);
        ticketDAO2 = new TicketDAO();
        ticketDAO2.dataBaseConfig = dataBaseConfig2;
    }
    
    @AfterEach
    private void tearDown(){
    	dataBasePrepareService.clearDataBaseEntries();
    }

	@Test
	public void ticketSavedShouldReturnSameValueByTheGetTicketMethod() {
		
		//ARRANGE
        
		//when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        //when(dataBaseConfig.getConnection()).thenReturn());
        //ticket.setOutTime(new Date(System.currentTimeMillis()));
        
        //ACT
        Boolean test = ticketDAO2.saveTicket(ticket);
        Ticket ticket2 = new Ticket();
        ticket2 = ticketDAO2.getTicket(regNumberString);
        
        
        //ASSERT
        assertTrue(!test);
        assertEquals(ticket.getParkingSpot() , ticket2.getParkingSpot());
        System.out.println(" / " +ticket.getPrice() + " / " + ticket2.getPrice() + " / ");
        assertEquals(ticket.getInTime().getTime()/10000 , ticket2.getInTime().getTime()/10000);
        assertEquals(ticket.getVehicleRegNumber() , ticket2.getVehicleRegNumber());
        assertEquals(12.0 , ticket.getPrice(), 0.00);
	}
	
	@Test
	public void updateTicketShouldReturnDifferentValueThanPreviousTicket() {
		
		//ARRANGE


        //ACT
        Boolean test = ticketDAO2.saveTicket(ticket);
        ticket.setOutTime(new Date());
        Boolean test2 = ticketDAO2.updateTicket(ticket);
        //ASSERT
        assertTrue(!test);
        assertTrue(test2);
	}

	@Test
	public void shouldReturnException_WhenDBConnectionFail() throws ClassNotFoundException, SQLException {
		// GIVEN
	    
        TicketDAO ticketDAO3 = new TicketDAO();
        ticketDAO3.dataBaseConfig = dataBaseConfigMock;
	    Ticket ticket3 = new Ticket();
		when(dataBaseConfigMock.getConnection()).thenThrow(new SQLException("Error occurred"));
		ticketDAO3.setLogger(testlogger3);
		
		// WHEN
		
		ticketDAO3.saveTicket(ticket3);
		// THEN
		verify(testlogger3, times(1)).error(anyString(),any(Throwable.class));
		verify(dataBaseConfigMock, times(1)).getConnection();
	}
}
