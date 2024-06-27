/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package error;

/**
 *
 * @author mw
 */
public class UserExistException extends Exception {

    /**
     * Creates a new instance of <code>NoResultException</code> without detail
     * message.
     */
    public UserExistException() {
    }

    /**
     * Constructs an instance of <code>NoResultException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UserExistException(String msg) {
        super(msg);
    }
}
