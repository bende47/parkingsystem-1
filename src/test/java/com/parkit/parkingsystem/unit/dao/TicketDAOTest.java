package com.parkit.parkingsystem.unit.dao;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
    

    private static DataBaseTestConfig dataBaseConfig = new DataBaseTestConfig();
    private static DataBaseTestConfig dataBaseConfig2 = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
    private static TicketDAO ticketDAO2;
    private static String regNumberString;
    
	@Mock
    private static Logger testlogger = LogManager.getLogger("TicketDAO");
	@Mock
    private static Logger testlogger3 = LogManager.getLogger("TicketDAO");
	@Mock
	private static DataBaseTestConfig dataBaseConfigMock = new DataBaseTestConfig();
	
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
    }
    
    @AfterEach
    private void tearDown(){
    	dataBasePrepareService.clearDataBaseEntries();
    }

	@Test
	public void ticketSavedShouldReturnSameValueByTheGetTicketMethod() {

        //ACT
        Boolean test = ticketDAO.saveTicket(ticket);
        Ticket ticket2 = new Ticket();
        ticket2 = ticketDAO.getTicket(regNumberString);
        
        
        //ASSERT
        assertTrue(!test);
        assertEquals(ticket.getParkingSpot() , ticket2.getParkingSpot());
        System.out.println(" / " +ticket.getPrice() + " / " + ticket2.getPrice() + " / ");
        assertEquals(ticket.getInTime().getTime()/1000000 , ticket2.getInTime().getTime()/1000000);
        assertEquals(ticket.getVehicleRegNumber() , ticket2.getVehicleRegNumber());
        assertEquals(12.0 , ticket.getPrice());
	}
	
	@Test
	public void updateTicketShouldReturnDifferentValueThanPreviousTicket() {

        //ACT
        Boolean test = ticketDAO.saveTicket(ticket);
        ticket.setOutTime(new Date());
        Boolean test2 = ticketDAO.updateTicket(ticket);
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

    @Test
    public void shouldReturnFalseIfAVehicleNumberIsRecordedTwice() {
        //ARRANGE
        ticketDAO.saveTicket(ticket);
        // ACT
        boolean test = ticketDAO.noDoubleRegNumber(regNumberString);

        // ASSERT
        assertTrue(!test);
    }

    @Test
    public void shouldReturnTrueIfOccurrenceSupTo1For5PercentReduction() throws SQLException, ClassNotFoundException {
        //ARRANGE
        ticketDAO.saveTicket(ticket);
        Ticket ticket2 = ticketDAO.getTicket(regNumberString);
        ticket2.setOutTime(new Date(System.currentTimeMillis()));
        ticketDAO.saveTicket(ticket2);
        Ticket ticket3 = new Ticket();
        ticket3.setVehicleRegNumber(regNumberString);
        ticketDAO.saveTicket(ticket3);

        Ticket ticket4 = ticketDAO.getTicket(regNumberString);
        // ACT
        boolean test = ticketDAO.availableReduction5Percent(ticket4);

        // ASSERT
        assertTrue(test);
    }

}
