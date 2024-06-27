/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Event;
import entity.Person;
import entity.Registration;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mw
 */
@Local
public interface EventSessionBeanLocal {

    public void createEvent(Event e);

    public Event getEventbyId(Long eId);

    public List<Event> searchEventByTitle(String title);

    public List<Event> searchEventByLocation(String location);

    public List<Event> searchEventByDeadline(Date date);

    public List<Event> searchEventByEventDate(Date date);

    public List<Event> getAllEventsExcludingUser(Long userId, String title, Date deadline, String location);

    public void createRegistration(Event e, Registration r, Person participatant);

    public void deleteRegistration(Long registrationId);

    public Event refreshEvent(Long eventId);

    public List<Event> eventsCreatedByUser(Long userId);

    public void cancelEvent(Event e, Long userId);

    public List<Registration> getAllRegistrationbyEventId(Long id);

    public void updateRegistration(Registration r, Boolean Status);

    public List<Registration> getRegistrationsByPersonId(Long personId);

    public void updateEvent(Event event);

   
}
