package com.bean;

import java.util.LinkedList;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.entities.Familia;
import com.enumerated.tipoPerfil;
import com.services.FamiliaBeanRemote;


@ManagedBean(name = "familia")
@ViewScoped

public class FamiliasBean {
	
	private Long id;
	private String nombre;
	private String descrip;
	private String incompat;
	
	private static tipoPerfil perfilLogeado;
	
	private Familia selectedFamilia;
	
	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;




	@EJB
	private FamiliaBeanRemote familiasEJBBean;
	
	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Familia:","La Familia se creo correctamente");
		String retPage = "altaFamiliaPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
		}else if (nombre.isEmpty() || nombre.length() > 50){
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
		}else if(descrip.isEmpty() || descrip.length() > 100) {
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Campo Descripcion no puede ser vacío o mayor a 100 caracteres");
		}else if(incompat.isEmpty() || incompat.length() > 60) {
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Campo Incompatible no puede ser vacío o mayor a 60 caracteres");
		}else {
			if(get() == null) {
				familiasEJBBean.add(nombre, descrip, incompat);
			}else {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
						"La Familia ya existe");
			}
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return retPage;
	}catch (Exception e) {
		return null;
	}
	}
	
	public String update(Long id, String nombre, String descrip, String incompat) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Familia Modificada exitosamente!");
		String retPage = "modificarFamiliaPage";
		try {
		
			if(!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			}else if(selectedFamilia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione una Familia a Modificar!");
			}else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			}else if (nombre.isEmpty() || nombre.length() > 50){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			}else if(descrip.isEmpty() || descrip.length() > 100) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Descripcion no puede ser vacío o mayor a 100 caracteres");
			}else if(incompat.isEmpty() || incompat.length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Incompatible no puede ser vacío o mayor a 60 caracteres");
			} else {
				familiasEJBBean.update(id, nombre, descrip, incompat);
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		}catch (Exception e) {
			return null;
		}
	}
	
	public String delete(Long id) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Familia borrada exitosamente!");
		String retPage = "bajaFamiliaPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			} else if (selectedFamilia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione un Usuario a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				familiasEJBBean.delete(selectedFamilia.getId());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}
	


	public Familia get() {
		try {
			return familiasEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	
	
	public LinkedList<Familia> getAll() {
		try {
			return familiasEJBBean.getAll();
		} catch (Exception e) {
			return null;
		}
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
	
	/***********************************************************************************************************************************/

	
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

	public String getDescrip() {
		return descrip;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	public String getIncompat() {
		return incompat;
	}

	public void setIncompat(String incompat) {
		this.incompat = incompat;
	}
	
}
