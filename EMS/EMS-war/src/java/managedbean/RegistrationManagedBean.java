/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Notification;
import entity.Person;
import entity.Registration;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import session.EventSessionBeanLocal;
import session.PersonSessionBeanLocal;

/**
 *
 * @author mw
 */
@Named(value = "registrationManagedBean")
@ViewScoped
public class RegistrationManagedBean implements Serializable {

    @EJB
    private PersonSessionBeanLocal personSession;
    @EJB
    private EventSessionBeanLocal eventSession;

    @Inject
    private AuthenticationManagedBean authenticationManagedBean;

    private List<Notification> unreadNotifications;
    private List<Registration> userRegistrations;

    /**
     * Creates a new instance of RegistrationManagedBean
     */
    public RegistrationManagedBean() {
    }

    public void handleSearch() {
        init();
    }

    @PostConstruct
    public void init() {

        if (authenticationManagedBean != null) {
            Long userId = authenticationManagedBean.getUserId();
            Person p = personSession.findPersonById(userId);
            userRegistrations = eventSession.getRegistrationsByPersonId(userId);
            unreadNotifications = p.getNotification();
        }
    }

    @Transactional
    public void unregisterEvent(Registration r) {
        Long currentUserId = authenticationManagedBean.getUserId();

        if (r != null) {
            eventSession.deleteRegistration(r.getId());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "You have been unregistered from the event."));
            init();
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "You are not registered for this event."));
        }
    }

    private void loadUnreadNotifications() {
        Long userId = authenticationManagedBean.getUserId();
        unreadNotifications = personSession.getNotificfation(userId);
        for (Notification notification : unreadNotifications) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Notification", notification.getMessages());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void deleteNotifications(Notification notification) {
        Long userId = authenticationManagedBean.getUserId();
        personSession.deleteNotification(notification.getId(), userId);
        unreadNotifications.remove(notification);
    }

    public PersonSessionBeanLocal getPersonSession() {
        return personSession;
    }

    public void setPersonSession(PersonSessionBeanLocal personSession) {
        this.personSession = personSession;
    }

    public List<Registration> getUserRegistrations() {
        return userRegistrations;
    }

    public void setUserRegistrations(List<Registration> userRegistrations) {
        this.userRegistrations = userRegistrations;
    }

    public AuthenticationManagedBean getAuthenticationManagedBean() {
        return authenticationManagedBean;
    }

    public void setAuthenticationManagedBean(AuthenticationManagedBean authenticationManagedBean) {
        this.authenticationManagedBean = authenticationManagedBean;
    }

    public EventSessionBeanLocal getEventSession() {
        return eventSession;
    }

    public void setEventSession(EventSessionBeanLocal eventSession) {
        this.eventSession = eventSession;
    }

    public List<Notification> getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(List<Notification> unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

}
