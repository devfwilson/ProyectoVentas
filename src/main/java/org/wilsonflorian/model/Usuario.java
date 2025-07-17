package org.wilsonflorian.model;

/**
 *
 * @author Wilson Florian
 */
public class Usuario {

    private int idUsuario;
    private String correoUsuario;
    private String contraseñaUsuario;

    public Usuario() {
    }

    public Usuario(int idUsuario, String correoUsuario, String contraseñaUsuario) {
        this.idUsuario = idUsuario;
        this.correoUsuario = correoUsuario;
        this.contraseñaUsuario = contraseñaUsuario;
    }

    public Usuario(String correoUsuario, String contraseñaUsuario) {
        this.correoUsuario = correoUsuario;
        this.contraseñaUsuario = contraseñaUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getContraseñaUsuario() {
        return contraseñaUsuario;
    }

    public void setContraseñaUsuario(String contraseñaUsuario) {
        this.contraseñaUsuario = contraseñaUsuario;
    }
}
