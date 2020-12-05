package com.bean;


import java.util.LinkedList;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.entities.Ciudad;
import com.enumerated.tipoPerfil;
import com.services.CiudadBeanRemote;


@SuppressWarnings("deprecation")
@ManagedBean(name = "ciudad")
@ViewScoped
public class CiudadesBean {
	
	private Long id;
	private String nombre;
	
	private static tipoPerfil perfilLogeado;
	

	private Ciudad selectedCiudad;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	@EJB
	private CiudadBeanRemote ciudadesEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
				"Ciudad ingresada exitosamente!");
		String retPage = "altaCiudadPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) ||!tipoPerfil.SUPERVISOR.equals(perfilLogeado) ) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acción");
			} else if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else {
				if (get() == null) {
					ciudadesEJBBean.add(nombre);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El nombre de ciudad provisto ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String update(Long id, String nombre) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Ciudad modificada exitosamente!");
		String retPage = "updateCiudadPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) ||!tipoPerfil.SUPERVISOR.equals(perfilLogeado) ) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acción");
			} else if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				if (get() != null) {
					ciudadesEJBBean.update(id, nombre);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"Ciudad no existe");
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
				"Ciudad borrada exitosamente!");
		String retPage = "bajaCiudadPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado)||!tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acción");
			} else if (selectedCiudad == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione una Ciudad a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				ciudadesEJBBean.delete(selectedCiudad.getId());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public Ciudad get() {
		try {
			return ciudadesEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public LinkedList<Ciudad> getAll() {
		try {
			return ciudadesEJBBean.getAll();
		} catch (Exception e) {
			return null;
		}
	}



	/***********************************************************************************************************************************/



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

	
}
