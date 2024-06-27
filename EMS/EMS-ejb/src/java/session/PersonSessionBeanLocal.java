/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package session;

import entity.Notification;
import entity.Person;
import entity.Registration;
import error.NoResultException;
import error.UserExistException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mw
 */
@Local
public interface PersonSessionBeanLocal {

    public void createUser(Person user);

    public Person login(String email, String password) throws NoResultException;

    public Person getUser(Long uId);

    public List<Person> getAllPersons();

    public Person findPersonById(Long id);

    public void updatePerson(Person person);

    public void deletePerson(Person person);

    public ArrayList<Notification> getNotificfation(Long userId);

    public void deleteNotification(Long notificationId, Long userId);

    public void deleteNotification(Long id);

    public void setProfile(Long id, String pic);

}
