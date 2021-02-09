package com.bean;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import com.entities.Ciudad;
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

		FacesMessage message;
		String retPage = "altaCiudadPage";
		try {
			if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vacío o mayor a 50 caracteres", null);
				System.out.println("Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			}else if (get2(nombre) != null){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El nombre de ciudad provisto ya existe", null);
				System.out.println("El nombre de ciudad provisto ya existe");
			}else{
					Ciudad c = new Ciudad();
					c.setNombre(nombre);
					ciudadesEJBBean.add(c);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se creo la ciudad: " + nombre, null);
					System.out.println("Se creo la ciudad: " + nombre);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar ciudad", null);
			System.out.println("No se ejecuto correctamente ciudadesEJBBean.add");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	

	public String update(Long id, String nombre) {
		FacesMessage message;
		String retPage = "modificarCiudadPage";
		
		try {
			if (nombre.isEmpty() || nombre.length() > 50 || nombre.trim() == "") {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vacío, ser mayor a 50 caracteres o contener solo espacios", null);
				System.out.println("Campo Nombre no puede ser vacío, ser mayor a 50 caracteres o contener solo espacios");
			}else if (get2(nombre) != null) { 
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe una ciudad con el nombre " + nombre, null);
				System.out.println("Ya existe una ciudad con el nombre " + nombre);
			}else if (ciudadesEJBBean.getId(id) == null){
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Modificar: Ciudad no existe", null);
				System.out.println("Se intento modificar una ciudad que no existe:");
			}else{ 
					Ciudad c = new Ciudad();
					c.setId(id);
					c.setNombre(nombre);
					ciudadesEJBBean.update(c);
				
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Ciudad modificada exitosamente!", null);
					System.out.println("Ciudad modificada exitosamente!");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo modificar la ciudad ", null);
			System.out.println("No se pudo modificar la ciudad: ");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return retPage;
	}

	
	public String delete(Ciudad ciudad) {
		FacesMessage message;
		String retPage = "bajaCiudadPage";
		
		try {
			if (ciudad == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione una Ciudad a borrar!", null);
				System.out.println("Seleccione una Ciudad a borrar!");
			} else if (entidadLocEJBBean.getLocalesxCiu(ciudad.getId()) > 0) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "No se puede eliminar la Ciudad porque tiene Locales asociados. Elimine primero los Locales que tienen la Ciudad", null);
					System.out.println("No se puede eliminar la Ciudad porque tiene Locales asociados. Elimine primero los Locales que tienen la Ciudad");
			} else {
					ciudadesEJBBean.delete(ciudad.getId());
					ciudadesList.remove(ciudad); 
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Ciudad borrada exitosamente!",  null);
					System.out.println("Ciudad borrada exitosamente!");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}	
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Asegurese que la ciudad no tenga Locales asociados",  null);
			System.out.println("No se puede eliminar la Ciudad. Asegurese que no tenga Locales asociados");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}
	
	public void onRowEdit(RowEditEvent event) {
	    Ciudad c = (Ciudad) event.getObject();
	    FacesMessage message = null;
	   try {
		   if(c == null) {
			   message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Esta pasando datos vacios", null);
			   System.out.println("Ciudad no puede estar vacio!");
		   }else {
			   this.update(c.getId(), c.getNombre());
			   System.out.println("Pasa datos al update desde rowEdit");
		   }
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo modificar la ciudad", null);
			System.out.println("No se pudo modificar la ciudad en row edit");
			
		}
	   FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	
	public Ciudad get() {
		try {
			return ciudadesEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	
	// se agrego String nombre
	public Ciudad get2(String nombre) {
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

	
	
	@PostConstruct
	public void segm() {
		try {
			// rowEdit
			if (ciudadesList==null) {
				ciudadesList = obtenerTodasCiudades();
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
