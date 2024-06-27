/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package session;

import entity.Notification;
import entity.Person;
import error.NoResultException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mw
 */
@Stateless
public class PersonSessionBean implements PersonSessionBeanLocal {

    @PersistenceContext(unitName = "EMS-ejbPU")
    private EntityManager em;

    public void createUser(Person user) {
        em.persist(user);
    }

    public Person login(String email, String password) throws NoResultException {
        try {
            Person user = em.createQuery("SELECT p FROM Person p WHERE p.email = :email AND p.password = :password", Person.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
            return user;
        } catch (Exception e) {
            throw new NoResultException("Invalid email or password.");
        }

    }

    public Person getUser(Long uId) {
        Person user = em.find(Person.class, uId);

        return user;
    }

    public List<Person> getAllPersons() { // Changed return type to List<Person>
        return em.createQuery("SELECT p FROM Person p", Person.class).getResultList(); // Use getResultList() to execute query and retrieve results
    }

    public Person findPersonById(Long id) {
        return em.find(Person.class, id);
    }

    public void updatePerson(Person person) {
        em.merge(person);
    }

    public void deletePerson(Person person) {
        em.remove(em.merge(person));
    }

    public ArrayList<Notification> getNotificfation(Long userId) {

        Person p = em.find(Person.class, userId);
        return p.getNotification();

    }

    public void deleteNotification(Long notificationId, Long userId) {
        Notification noti = em.find(Notification.class, notificationId);
        Person p = em.find(Person.class, userId);
        p.getNotification().remove(noti);
        em.remove(noti);
        em.flush();

    }

    public void deleteNotification(Long id) {
        Notification noti = em.find(Notification.class, id);
        em.remove(noti);
        em.flush();
    }

    public void setProfile(Long id, String pic) {
        Person p = em.find(Person.class, id);
        p.setProfilePhoto(pic);
    }

}
