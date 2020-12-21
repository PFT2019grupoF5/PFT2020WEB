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

import org.primefaces.event.RowEditEvent;

import com.entities.Almacenamiento;
import com.entities.Ciudad;
import com.entities.Producto;
import com.enumerated.Segmentacion;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.CiudadBeanRemote;
import com.services.EntidadLocBeanRemote;

@ManagedBean(name = "ciudad")
@ViewScoped
public class CiudadesBean {

	
	private Long id;
	private String nombre;

	private static tipoPerfil perfilLogeado;

	private Ciudad selectedCiudad;
	private List<Ciudad> ciudadesList;

	@EJB
	private CiudadBeanRemote ciudadesEJBBean;
	
	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;

	public String add() {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
				"Ciudad ingresada exitosamente!");
		String retPage = "altaCiudadPage";
		try {
			if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			
			} else {
				if (get2() == null) {
					Ciudad c = new Ciudad();
					c.setNombre(nombre);
					ciudadesEJBBean.add(c);
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
		FacesMessage message;
		String resultado = "modificarCiudadPage";
		
		try {
			if (nombre.isEmpty() || nombre.length() > 50 || nombre.trim() == "") {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Nombre no puede ser vacío, ser mayor a 50 caracteres o contener solo espacios");
				resultado = "retPage";
			} else if (ciudadesEJBBean.getNombre(nombre) != null) { //ya hay una ciudad con ese nombre
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Ya existe una ciudad con el nombre " + nombre + ". Por favor utilice otro.");
				resultado = "retPage";
			} else {	
				if (ciudadesEJBBean.getId(id) != null) { //existe la ciudad
					Ciudad c = new Ciudad();
					c.setId(id);
					c.setNombre(nombre);
					ciudadesEJBBean.update(c);
				
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
							"Ciudad modificada exitosamente!");
					resultado="modificacionCiudadPage";
					
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Modificar: ", "Ciudad no existe");
					resultado = "retPage";	
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			ciudadesList = obtenerTodosCiudades();
			
			return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public String delete(Ciudad ciudad) {
		FacesMessage message ;
		String retPage = "";
		
		try {
			if (ciudad == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Borrar: ",
						"Seleccione una Ciudad a borrar!");
			} else {
				if (entidadLocEJBBean.getLocalesxCiu(ciudad.getId()) > 0) {
					// No se puede eliminar la Ciudad porque hay Locales que la tienen asociada
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Borrar: ",
							"No se puede eliminar la Ciudad porque tiene Locales asociados. Elimine primero los Locales que tienen la Ciudad " + ciudad.getNombre());
				} else {
					ciudadesEJBBean.delete(ciudad.getId());
					ciudadesList.remove(ciudad); //elimino la CIudad de la lista para que se refleje en la página
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
							"Ciudad borrada exitosamente!");
					
					retPage = "bajaCiudadPage";
				}	
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void onRowEdit(RowEditEvent event) {
	    Ciudad c = (Ciudad) event.getObject();
	    
	   try {
			if (c != null) {
				this.update(c.getId(), c.getNombre());
			}
		} catch (Exception e) {
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
