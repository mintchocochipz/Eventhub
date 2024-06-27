/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package managedbean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import session.PersonSessionBeanLocal;

/**
 *
 * @author mw
 */
@Named(value = "profileManagedBean")
@ManagedBean
@ViewScoped
public class ProfileManagedBean implements Serializable {
    
    private Part uploadedfile;
    private String filename = "";
    @EJB
    private PersonSessionBeanLocal personSessionLocal;
    @Inject
    private AuthenticationManagedBean authenticationManagedBean;
    


    public ProfileManagedBean() {
    }
    
     public void upload() throws IOException {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

        //get the deployment path
        String UPLOAD_DIRECTORY = ctx.getRealPath("/") + "upload/";
        System.out.println("#UPLOAD_DIRECTORY : " + UPLOAD_DIRECTORY);

        //debug purposes
        setFilename(Paths.get(uploadedfile.getSubmittedFileName()).getFileName().toString());
        System.out.println("filename: " + getFilename());
        //---------------------
        
        //replace existing file
        Path path = Paths.get(UPLOAD_DIRECTORY + getFilename());
        InputStream bytes = uploadedfile.getInputStream();
        Files.copy(bytes, path, StandardCopyOption.REPLACE_EXISTING);
        
        Long userId = authenticationManagedBean.getUserId();
        personSessionLocal.setProfile(userId, "/upload/"+filename);
        
    }

    public Part getUploadedfile() {
        return uploadedfile;
    }

    public void setUploadedfile(Part uploadedfile) {
        this.uploadedfile = uploadedfile;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}

    

