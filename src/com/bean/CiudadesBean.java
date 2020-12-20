package com.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.entities.Ciudad;
import com.entities.Producto;
import com.enumerated.Segmentacion;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.CiudadBeanRemote;

@ManagedBean(name = "ciudad")
@ViewScoped
public class CiudadesBean {

	
	private Long id;
	private String nombre;

	private static tipoPerfil perfilLogeado;

	private Ciudad selectedCiudad;
	private List<Ciudad> ciudadesList;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	@EJB
	private CiudadBeanRemote ciudadesEJBBean;

	public String add() {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
				"Ciudad ingresada exitosamente!");
		String retPage = "altaCiudadPage";
		try {
			if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
				System.out.println("esta vacio o muy largo no funca");
			} else {
				if (get2() == null) {
					Ciudad c = new Ciudad();
					c.setNombre(nombre);
					ciudadesEJBBean.add(c);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El nombre de ciudad provisto ya existe");
					System.out.println("falló todito");
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
		String retPage = "modificarCiudadPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
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
					Ciudad c = new Ciudad();
					c.setId(id);
					c.setNombre(nombre);
					ciudadesEJBBean.update(c);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ", "Ciudad no existe");
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
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
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

	public Ciudad get2() {
		try {
			return ciudadesEJBBean.getNombre(nombre);
		} catch (Exception e) {
			return null;
		}
	}

	public List<Ciudad> getAll() {
		try {
			return ciudadesEJBBean.getAllCiudades();
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Ciudad> obtenerTodasCiudades() throws ServiciosException {
		return ciudadesList =ciudadesEJBBean.getAllCiudades();
	}

	/***********************************************************************************************************************************/

	public String chequearPerfil() {
		try {
			if (perfilLogeado == null) {
				return "Login?faces-redirect=true";
			} else {
				return null;
			}
		} catch (Exception e) {
			return "Login?faces-redirect=true";
		}
	}

	public String logout() {
		perfilLogeado = null;
		return "Login?faces-redirect=true";
	}

	public List<Ciudad> obtenerTodosCiudades() throws ServiciosException {
		return ciudadesList = ciudadesEJBBean.getAllCiudades();

	}
	
	@PostConstruct
	public void segm() {
		try {
			// rowEdit
			if (ciudadesList==null) {
				ciudadesList = obtenerTodosCiudades();
			}	
		} catch (Exception e) {
		}
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

	public Ciudad getSelectedCiudad() {
		return selectedCiudad;
	}

	public void setSelectedCiudad(Ciudad selectedCiudad) {
		this.selectedCiudad = selectedCiudad;
	}

	public List<Ciudad> getCiudades() {
		return ciudadesList;
	}

	public void setCiudades(List<Ciudad> ciudades) {
		this.ciudadesList = ciudades;
	}

	public List<Ciudad> getCiudadesList() {
		return ciudadesList;
	}

	public void setCiudadesList(List<Ciudad> ciudadesList) {
		this.ciudadesList = ciudadesList;
	}
	
	

}
