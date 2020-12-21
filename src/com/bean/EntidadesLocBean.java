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
import com.entities.EntidadLoc;
import com.enumerated.tipoLoc;
import com.enumerated.tipoPerfil;
import com.services.AlmacenamientoBeanRemote;
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
	private List <EntidadLoc> entidadLocList;
	
	
	private boolean confirmarBorrado = false;
	
	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;
	
	@EJB
	private CiudadBeanRemote ciudadEJBBean;

	@EJB
	private AlmacenamientoBeanRemote almacenamientoEJBBean;

	public String add() {
		FacesMessage message;
		String resultado = "altaLocalPage";
		
		try {
			if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
				resultado = "retPage";
			} else if (direccion.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Dirección no puede ser vacío o mayor a 50 caracteres");
				resultado = "retPage";
				
			} else if (codigo <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Código no puede ser menor a 0");
				resultado = "retPage";
			} else if (tipoLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo tipoLoc no puede ser vacío");
				resultado = "retPage";
			} else if (idCiudad <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo ciudad no puede ser vacío");
				resultado = "retPage";
			} else if (entidadLocEJBBean.getNombre(nombre.trim()) != null) {
					//ya existe otro Local con el mismo nombre
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Ya existe un Local con ese nombre. Por favor ingrese otro.");
				resultado = "retPage";
			} else {
				if (get() == null) {
					EntidadLoc e = new EntidadLoc();
					e.setCodigo(codigo);
					e.setNombre(nombre);
					e.setDireccion(direccion);
					e.setTipoloc(tipoLoc);
					e.setCiudad(ciudadEJBBean.getCiudad(idCiudad));
					entidadLocEJBBean.add(e);
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
							"Local ingresado exitosamente!");
					resultado ="altaEntidadLocPage";
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Registrar: ",
							"El nombre del local provisto ya existe");
					resultado = "retPage";
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return resultado;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String update(int codigo, String nombre, String direccion, tipoLoc tipoLoc, Ciudad ciudad) {
		FacesMessage message = null;
		String resultado="";
		
		try {
			if (codigo <=0 ) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"EL código no puede ser menor a 0");
				resultado = "retPage";
			} else if (nombre.length() > 50 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Nombre no puede ser vacío, mayor a 50 caracteres ni contener solo espacios");
				resultado = "retPage";
			} else if (direccion.isEmpty() || direccion.length() > 50 || direccion.length()==0 || direccion.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Modificar: ",
						"Campo Direccion no puede ser vacío, mayor a 50 caracteres ni contener solo espacios");
				resultado = "retPage";
			} else if (tipoLoc != com.enumerated.tipoLoc.LOCAL && tipoLoc != com.enumerated.tipoLoc.OTRO && tipoLoc != com.enumerated.tipoLoc.PUNTODEVENTA && tipoLoc != com.enumerated.tipoLoc.REGIONAL) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Local no puede ser vacío");
				resultado = "retPage";
			} else if (ciudad.getId()<=0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Ciudad no puede ser vacío");
				resultado = "retPage";
			} else if (entidadLocEJBBean.getNombre(nombre.trim()) != null) { //ya existe otro Local con el mismo nombre
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Ya existe un Local con ese nombre. Por favor ingrese otro.");

				FacesContext.getCurrentInstance().addMessage(null, message);
				resultado = "modificarEntidadLocPage";
				entidadLocList = entidadLocEJBBean.getAllEntidadesLoc();
				return resultado;
				
			} else {
				
				if (entidadLocEJBBean.getId(id) != null) {// si existe el Local, lo modifico
						EntidadLoc e = new EntidadLoc();
						e.setId(id);
						e.setCodigo(codigo);
						e.setNombre(nombre);
						e.setDireccion(direccion);
						e.setTipoloc(tipoLoc);
						e.setCiudad(ciudadEJBBean.getCiudad(idCiudad));
						entidadLocEJBBean.update(e);
	
						message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
								"Local modificado exitosamente!");
						resultado = "modificarEntidadLocPage";
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Modificar: ",
							"Local no existe");
					resultado = "retPage";
					
				}
			}
		
			entidadLocList = entidadLocEJBBean.getAllEntidadesLoc();
			FacesContext.getCurrentInstance().addMessage(null, message);
			return resultado;
			
		} catch (Exception e) {
			return null;
		}
	}

	public String delete(EntidadLoc entidadLoc) {
		FacesMessage message;
		String retPage="";
		
		try {
			if (entidadLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Borrar: ",
						"Seleccione un Local a borrar!");
			} else {
				
			if (almacenamientoEJBBean.getAlmacenamientoxLoc(entidadLoc.getId()) > 0) {
				//No se puede eliminar el Local porque hay Almacenamientos que lo tienen asociado

				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Borrar: ",
						"No se puede eliminar el Local porque tiene Almacenamientos asociados. ELimine primero los Almacenamientos que tienen el Local " + entidadLoc.getNombre());
			} else {
				
				entidadLocEJBBean.delete(entidadLoc.getId());
				entidadLocList.remove(entidadLoc); //elimino el local de la lista para que se refleje en la página
				
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
						"Local borrado exitosamente!");
				retPage ="bajaLocalPage";
			}
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

	public List<EntidadLoc> getAll() {
		try {
			return entidadLocEJBBean.getAllEntidadesLoc();
		} catch (Exception e) {
			return null;
		}
	}

	
	public void onRowEdit(RowEditEvent event) {
	    EntidadLoc el = (EntidadLoc) event.getObject();
	    
	   try {
			if (el != null) {
				this.update(el.getCodigo(), el.getNombre(), el.getDireccion(), el.getTipoloc(), el.getCiudad());
				
			}
		} catch (Exception e) {
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
			
			entidadLocList = entidadLocEJBBean.getAllEntidadesLoc();
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

	public List<EntidadLoc> getEntidadLocList() {
		return entidadLocList;
	}

	public void setEntidadLocList(List<EntidadLoc> entidadLocList) {
		this.entidadLocList = entidadLocList;
	}

	
	
	
}
