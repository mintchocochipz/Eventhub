package managedbean;

import entity.Person;
import error.NoResultException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import session.PersonSessionBeanLocal;

@Named(value = "authenticationManagedBean")
@SessionScoped
public class AuthenticationManagedBean implements Serializable {
    
    @EJB
    private PersonSessionBeanLocal personSessionLocal;

    private String username = null;
    private String password = null;
    private Long userId = null;

    public AuthenticationManagedBean() {
    }

    public String login() {

        try {
            Person person = personSessionLocal.login(username, password);
            userId = person.getId();

            return "secret/viewEvent.xhtml?faces-redirect=true";
        } catch (NoResultException e) {
            // Authentication failed
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email or password", null));

            // Reset input fields
            username = null;
            password = null;

            // Reset user id
            userId = null;

            return "login.xhtml";
        }

    } //end login

    public String logout() {
        //FacesContext x = FacesContext.getCurrentInstance();
        username = null;
        password = null;
        userId = null;

        return "/login.xhtml?faces-redirect=true";
    } //end logout

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}
