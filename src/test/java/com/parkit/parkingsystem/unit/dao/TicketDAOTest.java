package com.parkit.parkingsystem.unit.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

	private static TicketDAO ticketDAO;

    private static Ticket ticket;
    @Mock
    private static DataBaseConfig dataBaseConfig;
    

	@Test
	public void ticketSavedShouldReturnSameValueByTheGetTicketMethod() {
		
		//ARRANGE
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(new ParkingSpot(3, ParkingType.CAR,false));
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(12);
        ticketDAO = new TicketDAO();
        //ticket.setOutTime(new Date(System.currentTimeMillis()));
        
        //ACT
        Boolean test = ticketDAO.saveTicket(ticket);
        Ticket ticket2 = ticketDAO.getTicket("ABCDEF");
        
        //ASSERT
        assertTrue(!test);
        assertEquals(ticket.getParkingSpot() , ticket2.getParkingSpot());
        
	}
	
	@Test
	public void updateTicketShouldReturnDifferentValueThanPreviousTicket() {
		
		//ARRANGE
        ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(new ParkingSpot(3, ParkingType.CAR,false));
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(12);
        ticketDAO = new TicketDAO();

        //ACT
        Boolean test = ticketDAO.saveTicket(ticket);
        ticket.setOutTime(new Date());
        Boolean test2 = ticketDAO.updateTicket(ticket);
        //ASSERT
        assertTrue(!test);
        assertTrue(test2);
        
	}

}
