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

import com.entities.Ciudad;
import com.entities.EntidadLoc;
import com.enumerated.tipoLoc;
import com.enumerated.tipoPerfil;
import com.services.CiudadBeanRemote;
import com.services.EntidadLocBeanRemote;


@ManagedBean(name = "entidadLoc")
@ViewScoped
public class EntidadesLocBean {
	
	private Long id;
	private int codigo;
	private String nombre;
	private String direccion;
	private tipoLoc tipoLoc;
	private Ciudad ciudad;
	
	private EntidadLoc selectedEntidadLoc;
	private static tipoPerfil perfilLogeado;
	private List<SelectItem> tiposDeLocal;
	
	private Long idCiudad;
	private List <Ciudad> listaCiudad;
	
	
	private boolean confirmarBorrado = false;
	
	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;
	
	@EJB
	private CiudadBeanRemote ciudadEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "�xito al Registrar: ",
				"Local ingresada exitosamente!");
		String retPage = "altaLocalPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) ||!tipoPerfil.SUPERVISOR.equals(perfilLogeado) ) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acci�n");
			} else if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vac�o o mayor a 50 caracteres");
			} else if (direccion.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Direcci�n no puede ser vac�o o mayor a 50 caracteres");
			} else if (tipoLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo tipoLoc no puede ser vac�o");
			} else if (ciudad == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo ciudad no puede ser vac�o");
			} else {
				if (get() == null) {
					entidadLocEJBBean.add(codigo, nombre, direccion, tipoLoc, ciudadEJBBean.getCiudad(idCiudad));
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El nombre del local provisto ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String update(int codigo, String nombre, String direccion, tipoLoc tipoLoc, Ciudad ciudad) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "�xito al Modificar: ",
				"Local modificado exitosamente!");
		String retPage = "modificarLocalPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) ||!tipoPerfil.SUPERVISOR.equals(perfilLogeado) ) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acci�n");
			} else if (nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Nombre no puede ser vac�o o mayor a 50 caracteres");
			} else if (direccion.isEmpty() || direccion.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Direccion no puede superar los 50 caracteres");
			} else {
				if (get() != null) {
					entidadLocEJBBean.update(id, codigo, nombre, direccion, tipoLoc, ciudad);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"Local no existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String delete() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "�xito al Borrar: ",
				"Local borrado exitosamente!");
		String retPage = "bajaLocalPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado)||!tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acci�n");
			} else if (selectedEntidadLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione un Local a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmaci�n!");
			} else {
				entidadLocEJBBean.delete(selectedEntidadLoc.getId());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public EntidadLoc get() {
		try {
			return entidadLocEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public LinkedList<EntidadLoc> getAll() {
		try {
			return entidadLocEJBBean.getAll();
		} catch (Exception e) {
			return null;
		}
	}

	@PostConstruct
	public void tipL() {
		try {
			ArrayList<SelectItem> tipL = new ArrayList<>();
			tipL.add(new SelectItem(com.enumerated.tipoLoc.REGIONAL, com.enumerated.tipoLoc.REGIONAL.toString()));
			tipL.add(new SelectItem(com.enumerated.tipoLoc.LOCAL, com.enumerated.tipoLoc.LOCAL.toString()));
			tipL.add(new SelectItem(com.enumerated.tipoLoc.PUNTODEVENTA, com.enumerated.tipoLoc.PUNTODEVENTA.toString()));
			tipL.add(new SelectItem(com.enumerated.tipoLoc.OTRO, com.enumerated.tipoLoc.OTRO.toString()));
			tiposDeLocal =  tipL;
		} catch (Exception e) {
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

	public List<SelectItem> getTiposDeLocal() {
		return tiposDeLocal;
	}

	public void setTiposDeLocal(List<SelectItem> tiposDeLocal) {
		this.tiposDeLocal = tiposDeLocal;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public tipoLoc getTipoLoc() {
		return tipoLoc;
	}

	public void setTipoLoc(tipoLoc tipoLoc) {
		this.tipoLoc = tipoLoc;
	}

	public Ciudad getCiudad() {
		return ciudad;
	}

	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public List<Ciudad> getListaCiudad() {
		return listaCiudad;
	}

	public void setListaCiudad(List<Ciudad> listaCiudad) {
		this.listaCiudad = listaCiudad;
	}

	public EntidadLocBeanRemote getEntidadLocEJBBean() {
		return entidadLocEJBBean;
	}

	public void setEntidadLocEJBBean(EntidadLocBeanRemote entidadLocEJBBean) {
		this.entidadLocEJBBean = entidadLocEJBBean;
	}

	
	
	
}
