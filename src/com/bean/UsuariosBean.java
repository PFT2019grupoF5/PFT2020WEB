package com.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.event.RowEditEvent;
import com.entities.Usuario;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.UsuarioBeanRemote;

@SuppressWarnings("deprecation")
@ManagedBean(name = "usuario")
@ViewScoped
public class UsuariosBean {

	private Long id;
	private String nombre;
	private String apellido;
	private String nomAcceso;
	private String contrasena;
	private String correo;
	private tipoPerfil tipoPerfil;

	private List<SelectItem> availablePerfiles;

	private static tipoPerfil perfilLogeado;

	private String radioFindBy;

	private String filtroBusqueda;

	private List<Usuario> usuariosList;

	private Usuario selectedUsuario;

	private boolean confirmarBorrado = false;

	@EJB
	private UsuarioBeanRemote usuariosEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
				"Usuario ingresado exitosamente!");
		String retPage = "altaUsuarioPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR para poder acceder");
			} else if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (apellido.isEmpty() || apellido.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Apellido no puede ser vacío o mayor a 50 caracteres");
			} else if (tipoPerfil == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Debe Seleccionar un tipo de Perfil");
			} else if (contrasena.length() == 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Contraseña no puede ser vacío");
			} else if (contrasena.length() < 8 || contrasena.length() > 16) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Contraseña debe tener un largo entre 8 y 16 caracteres");
			} else if (nomAcceso.isEmpty() || nomAcceso.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo nomAcceso no puede ser vacío o mayor a 50 caracteres");
			} else if (!correo.contains("@")) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Correo debe ser del formato : nombre @ dominio");
			} else {
				if (get() == null) {
					usuariosEJBBean.add(nombre, apellido, nomAcceso, DigestUtils.md5Hex(contrasena), correo,
							tipoPerfil);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El nombre de usuario provisto ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String delete() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Usuario borrado exitosamente!");
		String retPage = "bajaUsuarioPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR para poder acceder");
			} else if (selectedUsuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione un Usuario a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				usuariosEJBBean.delete(selectedUsuario.getId());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public Usuario get() {
		try {
			return usuariosEJBBean.getNA(nomAcceso);
		} catch (Exception e) {
			return null;
		}
	}

	public LinkedList<Usuario> getAll() {
		try {
			return usuariosEJBBean.getAll();
		} catch (Exception e) {
			return null;
		}
	}

	private boolean ValidarContrasena(String userTyped, String typedPwd) {
		try {
			return usuariosEJBBean.ValidarContrasena(userTyped, typedPwd);
		} catch (Exception e) {
			return false;
		}
	}

	/***********************************************************************************************************************************/

	public String login() {
		FacesMessage message = null;
		try {
			Usuario loginUser = usuariosEJBBean.getNA(nomAcceso);

			if (nomAcceso != null && loginUser != null && contrasena != null
					&& ValidarContrasena(nomAcceso, DigestUtils.md5Hex(contrasena))) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", nomAcceso);
				FacesContext.getCurrentInstance().addMessage(null, message);
				perfilLogeado = loginUser.getTipoPerfil();
				return "mainPage?faces-redirect=true";
			} else {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error en Inicio de Sesión",
						loginUser != null ? "Credenciales Inválidas"
								: "No existe un Usuario que con coincida con los datos ingresados");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return "index";
			}
		} catch (ServiciosException e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Inicio de Sesión", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
			return "index";
		}

	}

	public String cancelLogin() {
		return "index?faces-redirect=true";
	}


	public String checkRoles() {
		try {
			if (perfilLogeado == null) {
				return "index?faces-redirect=true";
			} else {
				return null;
			}
		} catch (Exception e) {
			return "index?faces-redirect=true";
		}
	}

	public String logout() {
		perfilLogeado = null;
		return "index?faces-redirect=true";
	}

	/***********************************************************************************************************************************/

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNomAcceso() {
		return nomAcceso;
	}

	public void setNomAcceso(String nomAcceso) {
		this.nomAcceso = nomAcceso;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public tipoPerfil getTipoPerfil() {
		return tipoPerfil;
	}

	public void setTipoPerfil(tipoPerfil tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}

	public List<SelectItem> getAvailablePerfiles() {
		return availablePerfiles;
	}

	public void setAvailablePerfiles(List<SelectItem> availablePerfiles) {
		this.availablePerfiles = availablePerfiles;
	}

	public static tipoPerfil getPerfilLogeado() {
		return perfilLogeado;
	}

	public static void setPerfilLogeado(tipoPerfil perfilLogeado) {
		UsuariosBean.perfilLogeado = perfilLogeado;
	}

	public String getRadioFindBy() {
		return radioFindBy;
	}

	public void setRadioFindBy(String radioFindBy) {
		this.radioFindBy = radioFindBy;
	}

	public String getFiltroBusqueda() {
		return filtroBusqueda;
	}

	public void setFiltroBusqueda(String filtroBusqueda) {
		this.filtroBusqueda = filtroBusqueda;
	}

	public List<Usuario> getUsuariosList() {
		return usuariosList;
	}

	public void setUsuariosList(List<Usuario> usuariosList) {
		this.usuariosList = usuariosList;
	}

	public Usuario getSelectedUsuario() {
		return selectedUsuario;
	}

	public void setSelectedUsuario(Usuario selectedUsuario) {
		this.selectedUsuario = selectedUsuario;
	}

	public boolean isConfirmarBorrado() {
		return confirmarBorrado;
	}

	public void setConfirmarBorrado(boolean confirmarBorrado) {
		this.confirmarBorrado = confirmarBorrado;
	}

	public UsuarioBeanRemote getUsuariosEJBBean() {
		return usuariosEJBBean;
	}

	public void setUsuariosEJBBean(UsuarioBeanRemote usuariosEJBBean) {
		this.usuariosEJBBean = usuariosEJBBean;
	}

}
