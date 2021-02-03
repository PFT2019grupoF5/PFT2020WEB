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
import com.entities.Ciudad;
import com.entities.EntidadLoc;
import com.enumerated.tipoLoc;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
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
	private EntidadLoc entLoc;
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
		String retPage = "altaLocalPage";
		
		try {
			if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vacío o mayor a 50 caracteres", null);
				System.out.println("Campo Nombre no puede ser vacío o mayor a 50 caracteres");
//			La direccion puede ser vacio
			} else if (direccion.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN,  "Campo Dirección no puede ser vacío o mayor a 50 caracteres", null);
				System.out.println("Campo Dirección no puede ser vacío o mayor a 50 caracteres");
				
			} else if (codigo <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Código no puede ser menor a 0", null);
				System.out.println("Campo Código no puede ser menor a 0");
			} else if (tipoLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo tipoLoc no puede ser vacío", null);
				System.out.println("Campo tipoLoc no puede ser vacío");
			} else if (idCiudad <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo ciudad no puede ser vacío", null);
				System.out.println("Campo ciudad no puede ser vacío");
			} else if (entidadLocEJBBean.getNombre(nombre.trim()) != null) {
					
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe un Local con ese nombre. Por favor ingrese otro.", null);
				System.out.println("Ya existe un Local con ese nombre. Por favor ingrese otro.");
				
			} else {
				if (get() == null) {
					EntidadLoc e = new EntidadLoc();
					e.setCodigo(codigo);
					e.setNombre(nombre);
					e.setDireccion(direccion);
					e.setTipoloc(tipoLoc);
					e.setCiudad(ciudadEJBBean.getCiudad(idCiudad));
					entidadLocEJBBean.add(e);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Local ingresado exitosamente!" + nombre, null);
					System.out.println("Local ingresado exitosamente!" + "\n" + nombre + "\n" + codigo + "\n" + direccion + "\n" + tipoLoc + "\n" + idCiudad);
	
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un local con el nombre: " + nombre, null);
					System.out.println("Ya existe un local con el nombre: " + nombre);
					
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar Locales", null);
			System.out.println("No se ejecuto correctamente entidadLocEJBBean.add");
		}
		return retPage;
	}
	
	public String update(Long id, int codigo, String nombre, String direccion, tipoLoc tipoLoc, long ciudadIdNuevo) {
		FacesMessage message;
		String retPage="modificarEntidadLocPage";
		
		try {
			if (codigo <=0 ) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Código no puede ser menor a 0", null);
				System.out.println("Campo Código no puede ser menor a 0");
				
			} else if (nombre.length() > 50 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vacío o mayor a 50 caracteres", null);
				System.out.println("Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (direccion.isEmpty() || direccion.length() > 50 || direccion.length()==0 || direccion.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN,  "Campo Dirección no puede ser vacío o mayor a 50 caracteres", null);
				System.out.println("Campo Dirección no puede ser vacío o mayor a 50 caracteres");
			
			} else if (tipoLoc != com.enumerated.tipoLoc.LOCAL && tipoLoc != com.enumerated.tipoLoc.OTRO && tipoLoc != com.enumerated.tipoLoc.PUNTODEVENTA && tipoLoc != com.enumerated.tipoLoc.REGIONAL) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo tipoLoc no puede ser vacío", null);
				System.out.println("Campo tipoLoc no puede ser vacío");
				
			} else if (ciudad.getId()<=0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo ciudad no puede ser vacío", null);
				System.out.println("Campo ciudad no puede ser vacío");
			
			} else if (entidadLocEJBBean.getNombre(nombre.trim()) != null) { 
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe un Local con ese nombre. Por favor ingrese otro.", null);
				System.out.println("Ya existe un Local con ese nombre. Por favor ingrese otro.");

				
				entidadLocList = entidadLocEJBBean.getAllEntidadesLoc();
			
				
			} else {
				
				if (getId(id) != null) {
						EntidadLoc e = new EntidadLoc();
						e.setCodigo(codigo);
						e.setNombre(nombre);
						e.setDireccion(direccion);
						e.setTipoloc(tipoLoc);
						e.setCiudad(ciudadEJBBean.getCiudad(ciudadIdNuevo));
						entidadLocEJBBean.update(e);
	
						message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Local modificado exitosamente!" + nombre, null);
						System.out.println("Local ingresado exitosamente!" + "\n" + nombre + "\n" + codigo + "\n" + direccion + "\n" + tipoLoc + "\n" + ciudadIdNuevo);
		
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Local no existe", null);
					System.out.println("Local no existe");
					
				}
			}
		
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
			
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo modificar el local.", null);
			System.out.println("No se ejecuto correctamente entidadLocEJBBean.update");
			
		}
		return retPage;
	}

	public String delete(EntidadLoc entidadLoc) {
		FacesMessage message;
		String retPage="bajaEntidadLocPage";
		
		try {
			if (entidadLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Seleccione un Local a borrar!", null);
				System.out.println("Seleccione un Local a borrar!");
			} else {
				
			if (almacenamientoEJBBean.getAlmacenamientoxLoc(entidadLoc.getId()) > 0) {
				
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede eliminar el Local porque tiene Almacenamientos asociados. ELimine primero los Almacenamientos que tienen el Local", null);
				System.out.println("No se puede eliminar el Local porque tiene Almacenamientos asociados. ELimine primero los Almacenamientos que tienen el Local");
			} else {
				
				entidadLocEJBBean.delete(entidadLoc.getId());
				entidadLocList.remove(entidadLoc); 
				
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, 	"Local borrado exitosamente!", null);
				System.out.println("Local borrado exitosamente!");
			}
		}	
		
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, 	"Contacte al administrador. Asegurese que el local no tenga movimientos asociados", null);
			System.out.println("No se ejecuto correctamente entidadLocEJBBean.delete");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return retPage;
	}

	public EntidadLoc get() {
		try {
			return entidadLocEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public EntidadLoc getId(Long id) {
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
	
	public List<EntidadLoc> obtenerTodosEntidadLoc() throws ServiciosException{
		return entidadLocList = entidadLocEJBBean.getAllEntidadesLoc();
	}

	
	public void onRowEdit(RowEditEvent event) {
	    EntidadLoc el = (EntidadLoc) event.getObject();
	    FacesMessage message;
	   try {
		   if(el.getCodigo() == 0 || el.getDireccion().isEmpty() || el.getNombre().isEmpty() || el.getTipoloc() == null || el.getCiudad() == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos", null);
				 System.out.println("Es necesario ingresar todos los datos requeridos");
		   }else {
		   Long entLocId = el.getCiudad().getId();
		   el.setCiudad(ciudadEJBBean.getId(entLocId));
			
		   entidadLocEJBBean.update(el);
		   message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Local modificado exitosamente!", null);
		   System.out.println("Local modificada exitosamente!");
		   }
		   FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo modificar el local", null);
			System.out.println("No se pudo modificar el local");
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
			
			if (entidadLocList==null) {
				entLoc = new EntidadLoc();
				entidadLocList = obtenerTodosEntidadLoc();
				
				System.out.println("Se carga la lista de tipos de locales");
			}
			entidadLocList = entidadLocEJBBean.getAllEntidadesLoc();
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo cargar la lista tipo de locales", null);
			System.out.println("No se pudo cargar la lista de tipos de locales");
			
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
