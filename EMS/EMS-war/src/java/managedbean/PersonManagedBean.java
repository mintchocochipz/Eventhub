/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import entity.Person;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import org.primefaces.event.FileUploadEvent;
import session.PersonSessionBeanLocal;

/**
 *
 * @author mw
 */
@Named(value = "personManagedBean")
@ViewScoped
public class PersonManagedBean implements Serializable {

    @EJB
    private PersonSessionBeanLocal personSessionLocal;
    @Inject
    private AuthenticationManagedBean authenticationManagedBean;

    private Long id;
    private String name;
    private String phone;
    private String email;
    private byte gender;
    //private byte[] profilePhoto;
    private String password;
    //private Part uploadedfile;
    private String filename;

    public PersonManagedBean() {
    }

    @PostConstruct
    public void init() {
        Long userId = authenticationManagedBean.getUserId();

        if (userId != null) {
            Person loggedInPerson = personSessionLocal.findPersonById(userId);
            name = loggedInPerson.getName();
            phone = loggedInPerson.getPhone();
            email = loggedInPerson.getEmail();
            gender = loggedInPerson.getGender();
            filename = loggedInPerson.getProfilePhoto();
            password = loggedInPerson.getPassword();
            id = loggedInPerson.getId();
        }

    }

    public void createPerson(javax.faces.event.ActionEvent evt) {

        Person person = new Person();
        person.setName(this.name);
        person.setPhone(this.phone);
        person.setEmail(this.email);
        person.setGender(this.gender);
        person.setPassword(password);
        person.setProfilePhoto(null);

        personSessionLocal.createUser(person); // Save the person using your service

    }

    public void updatePerson() {
        try {

            Person personToUpdate = personSessionLocal.findPersonById(this.id); // Retrieve the person to update
            if (personToUpdate != null) {
                personToUpdate.setName(this.name);
                personToUpdate.setPhone(this.phone);
                personToUpdate.setEmail(this.email);
                personToUpdate.setGender(this.gender);

                if (this.password != null && !this.password.isEmpty()) {
                    personToUpdate.setPassword(this.password); // Only update the password if it's been changed
                }

                personSessionLocal.updatePerson(personToUpdate); // Update the person using your service
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profile updated successfully."));
                FacesContext.getCurrentInstance().getExternalContext().redirect("editProfile.xhtml"); // Replace with the actual page name
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User not found."));
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred during redirection."));
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonSessionBeanLocal getPersonSessionLocal() {
        return personSessionLocal;
    }

    public void setPersonSessionLocal(PersonSessionBeanLocal personSessionLocal) {
        this.personSessionLocal = personSessionLocal;
    }

    public AuthenticationManagedBean getAuthenticationManagedBean() {
        return authenticationManagedBean;
    }

    public void setAuthenticationManagedBean(AuthenticationManagedBean authenticationManagedBean) {
        this.authenticationManagedBean = authenticationManagedBean;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

}
