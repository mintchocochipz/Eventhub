/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Event;
import entity.Notification;
import entity.Person;
import entity.Registration;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import session.EventSessionBeanLocal;
import session.PersonSessionBeanLocal;

/**
 *
 * @author mw
 */
@Named(value = "eventManagedBean")
@ViewScoped

public class EventManagedBean implements Serializable {

    @EJB
    private PersonSessionBeanLocal personSessionLocal;
    @EJB
    private EventSessionBeanLocal eventSessionLocal;
    @Inject
    private PersonManagedBean personManaged;
    @Inject
    private AuthenticationManagedBean authenticationManagedBean;
    @Inject
    private RegistrationManagedBean registrationManagedBean;

    private String title;
    private String location;
    private String description;
    private Date deadline;
    private Date eventDate;
    private Person organizer;

    //use by managemyEvent
    private List<Event> eventsCreated;

    //use by eventdetail
    private Long selectedEventId;
    private Event selectedEvent;
    private String titleS;
    private String locationS;
    private String descriptionS;
    private List<Registration> selectedEventRegistration;

    //used by viewEvent.xhtml
    private List<Event> events;
    private String searchType = "TITLE";
    private String searchString;
    private Date searchDate;
    private Long eId;

    private List<Long> notificationIds = new ArrayList<>();

    /**
     * Creates a new instance of EventManagedBean
     */
    public EventManagedBean() {
    }

    public void createEvent() {

        try {

            // Check if the user is logged in
            // Assuming your Person entity has a method to retrieve a person by ID
            organizer = personSessionLocal.findPersonById(personManaged.getId());

            Event event = new Event();
            event.setTitle(title);
            event.setLocation(location);
            event.setDescription(description);
            event.setDeadline(deadline);
            event.setEventDate(eventDate);
            event.setOrganizer(organizer);
            organizer.getCreatedEvents().add(event);

            eventSessionLocal.createEvent(event);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void handleSearch() {
        init();
    }

    @PostConstruct
    public void init() {
        Long userId = authenticationManagedBean.getUserId(); // Fetch the current user ID
        eventsCreated = eventSessionLocal.eventsCreatedByUser(userId);

        switch (searchType) {
            case "TITLE":
                events = eventSessionLocal.getAllEventsExcludingUser(userId, searchString, null, null);
                break;
            case "LOCATION":
                events = eventSessionLocal.getAllEventsExcludingUser(userId, null, null, searchString);
                break;
            case "DEADLINE":
                events = eventSessionLocal.getAllEventsExcludingUser(userId, null, searchDate, null);
                break;
            case "EVENTDATE":
                // Assuming you want to fetch all events up to the search date
                events = eventSessionLocal.getAllEventsExcludingUser(userId, null, searchDate, null);
                break;
            default:
                // Fetch all events excluding the current user without any filter
                events = eventSessionLocal.getAllEventsExcludingUser(userId, null, null, null);
                break;
        }
    }

    @Transactional
    public void cancelEvent(Event event) {
        Long userId = authenticationManagedBean.getUserId();

        eventSessionLocal.cancelEvent(event, userId); // Implement this method in your session bean
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Event has been cancelled."));

        init(); // Refresh the list of created events
    }

    public void joinEvent(Event event) {
        Long currentUserId = authenticationManagedBean.getUserId(); // Retrieve the current user's ID
        if (hasUserJoinedEvent(currentUserId, event)) {
            // If the user has already joined the event, show a message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "You have already joined this event."));
        } else if (canJoinEvent(event)) {
            Registration r = new Registration();
            r.setEvent(event);
            r.setRegistrationDate(new Date());
            Person participatant = personSessionLocal.findPersonById(currentUserId);
            r.setPerson(participatant);
            r.setIsPresent(false);
            eventSessionLocal.createRegistration(event, r, participatant);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "You have successfully joined the event!"));
            init();
            registrationManagedBean.init();

        } else if (!canJoinEvent(event)) {
            // If the registration deadline has passed, show a message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "The registration deadline has passed."));
        }
    }

    // Helper method to determine if a user has already joined an event
    public boolean hasUserJoinedEvent(Long currentUserId, Event event) {
        Event freshEvent = eventSessionLocal.refreshEvent(event.getId());
        return event.getRegistrations().stream()
                .anyMatch(reg -> reg.getPerson().getId().equals(currentUserId));
    }

    // Method to check if the user can join the event (registration deadline has not passed)
    public boolean canJoinEvent(Event event) {
        Date now = new Date();
        return event.getDeadline() == null || now.before(event.getDeadline());
    }

    public String updateEvent() {

        selectedEvent.setTitle(this.titleS);
        selectedEvent.setDescription(descriptionS);
        selectedEvent.setLocation(this.locationS);
        eventSessionLocal.updateEvent(selectedEvent);
        return "editEvent.xhtml?faces-redirect=true&includeViewParams=true";

    }

    public String viewEventDetails(Event e) {
        return "viewEventDetail.xhtml?faces-redirect=true&eventId=" + e.getId();
    }

    public String updateEventDetails(Event e) {
        return "editEvent.xhtml?faces-redirect=true&eventId=" + e.getId();
    }

    public void loadSelectedEvent() {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedEvent = eventSessionLocal.getEventbyId(selectedEventId);
        if (selectedEvent != null) {
            this.titleS = selectedEvent.getTitle();
            this.locationS = selectedEvent.getLocation();
            this.descriptionS = selectedEvent.getDescription();
        }

        selectedEventRegistration = eventSessionLocal.getAllRegistrationbyEventId(selectedEventId);

    }

    public void markAttendance(Registration registration, Boolean status) {
        eventSessionLocal.updateRegistration(registration, status); // Implement this method to update registration in the database
    }

    public PersonSessionBeanLocal getPersonSessionLocal() {
        return personSessionLocal;
    }

    public void setPersonSessionLocal(PersonSessionBeanLocal personSessionLocal) {
        this.personSessionLocal = personSessionLocal;
    }

    public EventSessionBeanLocal getEventSessionLocal() {
        return eventSessionLocal;
    }

    public void setEventSessionLocal(EventSessionBeanLocal eventSessionLocal) {
        this.eventSessionLocal = eventSessionLocal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Person getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Person organizer) {
        this.organizer = organizer;
    }

    public PersonManagedBean getPersonManaged() {
        return personManaged;
    }

    public void setPersonManaged(PersonManagedBean personManaged) {
        this.personManaged = personManaged;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public Date getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }

    public Long getSelectedEventId() {
        return selectedEventId;
    }

    public void setSelectedEventId(Long selectedEventId) {
        this.selectedEventId = selectedEventId;
    }

    public AuthenticationManagedBean getAuthenticationManagedBean() {
        return authenticationManagedBean;
    }

    public void setAuthenticationManagedBean(AuthenticationManagedBean authenticationManagedBean) {
        this.authenticationManagedBean = authenticationManagedBean;
    }

    public RegistrationManagedBean getRegistrationManagedBean() {
        return registrationManagedBean;
    }

    public void setRegistrationManagedBean(RegistrationManagedBean registrationManagedBean) {
        this.registrationManagedBean = registrationManagedBean;
    }

    public List<Event> getEventsCreated() {
        return eventsCreated;
    }

    public void setEventsCreated(List<Event> eventsCreated) {
        this.eventsCreated = eventsCreated;
    }

    public Long geteId() {
        return eId;
    }

    public void seteId(Long eId) {
        this.eId = eId;
    }

    public List<Registration> getSelectedEventRegistration() {
        return selectedEventRegistration;
    }

    public void setSelectedEventRegistration(List<Registration> selectedEventRegistration) {
        this.selectedEventRegistration = selectedEventRegistration;
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<Long> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }

    public String getTitleS() {
        return titleS;
    }

    public void setTitleS(String titleS) {
        this.titleS = titleS;
    }

    public String getLocationS() {
        return locationS;
    }

    public void setLocationS(String locationS) {
        this.locationS = locationS;
    }

    public String getDescriptionS() {
        return descriptionS;
    }

    public void setDescriptionS(String descriptionS) {
        this.descriptionS = descriptionS;
    }

}
