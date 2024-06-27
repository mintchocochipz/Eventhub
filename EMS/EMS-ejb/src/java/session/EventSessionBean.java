/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Event;
import entity.Notification;
import entity.Person;
import entity.Registration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author mw
 */
@Stateless
public class EventSessionBean implements EventSessionBeanLocal {

    @PersistenceContext(unitName = "EMS-ejbPU")
    private EntityManager em;

    public void createEvent(Event e) {
        em.persist(e);
    }

    public Event getEventbyId(Long eId) {
        Event e = em.find(Event.class, eId);
        return e;
    }

    @Override
    public List<Event> searchEventByTitle(String title) {
        Query q;
        if (title != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.title) LIKE :title");
            q.setParameter("title", "%" + title.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT e FROM Event e");
        }

        return q.getResultList();
    }

    @Override
    public List<Event> searchEventByLocation(String location) {
        Query q;

        if (location != null) {
            q = em.createQuery("SELECT e FROM Event e WHERE "
                    + "LOWER(e.location) LIKE :location");
            q.setParameter("location", "%" + location.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT e FROM Event e");
        }

        return q.getResultList();
    }

    @Override
    public List<Event> searchEventByEventDate(Date date) {
        Query q = em.createQuery("SELECT e FROM Event e WHERE e.eventDate <= :eventDate");
        q.setParameter("eventDate", date, TemporalType.TIMESTAMP);
        return q.getResultList();
    }

    @Override
    public List<Event> searchEventByDeadline(Date date) {
        Query q = em.createQuery("SELECT e FROM Event e WHERE e.deadline <= :deadline");
        q.setParameter("deadline", date, TemporalType.TIMESTAMP);
        return q.getResultList();
    }

    @Override
    public List<Event> getAllEventsExcludingUser(Long userId, String title, Date deadline, String location) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();
        if (userId != null) {
            predicates.add(cb.notEqual(event.get("organizer").get("id"), userId));
        }
        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(cb.lower(event.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (deadline != null) {
            predicates.add(cb.lessThanOrEqualTo(event.get("deadline"), deadline));
        }
        if (location != null && !location.isEmpty()) {
            predicates.add(cb.like(cb.lower(event.get("location")), "%" + location.toLowerCase() + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> query = em.createQuery(cq);
        return query.getResultList();
    }

    public void createRegistration(Event e, Registration r, Person participatant) {
        Person p = em.find(Person.class, participatant.getId());
        Event eve = em.find(Event.class, e.getId());
        eve.getRegistrations().add(r);
        p.getRegistrations().add(r);
        em.persist(r);
    }

    public void deleteRegistration(Long rId) {
        Registration registration = em.find(Registration.class, rId);

        Event event = registration.getEvent();
        Person participant = registration.getPerson();

        // Ensure the registration is removed from the event and person collections
        event.getRegistrations().remove(registration);
        participant.getRegistrations().remove(registration);

        // Synchronize changes with the database
        em.merge(event);
        em.merge(participant);

        // Remove the registration entity itself
        em.remove(registration);
    }

    // Inside your EventSessionBean or equivalent service
    public Event refreshEvent(Long eventId) {
        Event event = em.find(Event.class, eventId);
        if (event != null) {
            em.refresh(event);
        }
        return event;
    }

    public List<Event> eventsCreatedByUser(Long userId) {
        return em.createQuery("SELECT e FROM Event e WHERE e.organizer.id = :userId", Event.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Transactional // createnotification
    public void cancelEvent(Event e, Long userId) {
        Person owner = em.find(Person.class, userId);
        Event event = em.find(Event.class, e.getId());
        String notificationMessage = event.getTitle()
                + " has been canceled by the organiser:" + event.getOrganizerName();
        List<Registration> regList = getAllRegistrationbyEventId(e.getId());

        for (Registration r : regList) {
            Notification notification = new Notification();
            notification.setMessages(notificationMessage);
            Person p = em.find(Person.class, r.getPerson().getId());
            p.getRegistrations().remove(r);
            p.getNotification().add(notification);
            notification.setPerson(p);
            em.persist(notification);
        }

        owner.getCreatedEvents().remove(event);
        event.getRegistrations().clear();

        em.remove(event);
        em.flush();

    }

    public List<Registration> getAllRegistrationbyEventId(Long id) {
        return em.createQuery("SELECT r FROM Registration r WHERE r.event.id = :eventId", Registration.class)
                .setParameter("eventId", id)
                .getResultList();
    }

    public List<Registration> getRegistrationsByPersonId(Long personId) {
        return em.createQuery(
                "SELECT r FROM Registration r WHERE r.person.id = :personId", Registration.class)
                .setParameter("personId", personId)
                .getResultList();
    }

    public void updateRegistration(Registration r, Boolean status) {
        Registration rg = em.find(Registration.class, r.getId());
        rg.setIsPresent(status);
        em.merge(rg);

    }

    public void updateEvent(Event event) {
        em.merge(event);
    }

}
