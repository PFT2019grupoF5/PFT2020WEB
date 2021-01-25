package com.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import com.entities.Familia;
import com.entities.Producto;
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

	private List<Familia> familiasList;
	private Familia selectedFamilia;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	@EJB
	private FamiliaBeanRemote familiasEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Familia:",
				"La Familia se creo correctamente");
		String retPage = "altaFamiliaPage";
		try {
			if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (descrip.isEmpty() || descrip.length() > 100) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Descripcion no puede ser vacío o mayor a 100 caracteres");
			} else if (incompat.isEmpty() || incompat.length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Incompatible no puede ser vacío o mayor a 60 caracteres");
			} else {
				if (getNombre(nombre) == null) {
					Familia f = new Familia();
					f.setNombre(nombre);
					f.setDescrip(descrip);
					f.setIncompat(incompat);	
					familiasEJBBean.add(f);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Familia:",
							"La Familia se creo correctamente");
					
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"La Familia ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String update(Long id, String nombre, String descrip, String incompat) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Familia Modificada exitosamente!");
		String retPage = "modificarFamiliaPage";
		try {

			if (nombre.isEmpty() || nombre.length() > 50 || descrip.isEmpty() || descrip.length() > 100 || incompat.isEmpty() || incompat.length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Debe llenar todos los datos!");
			} else {
				if (getNombre(nombre) != null) {
					Familia f = new Familia();
					f.setNombre(nombre);
					f.setDescrip(descrip);
					f.setIncompat(incompat);	
					familiasEJBBean.update(f);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"Familia no existe");
				}	
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String delete(Familia familia) {
		FacesMessage message;
		String retPage = "bajaFamiliaPage";
		try {
			if (familia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione una Familia a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				familiasEJBBean.delete(familia.getId());
				familiasList.remove(familia);
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
						"Familia borrada exitosamente!");
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

	public Familia getNombre(String nombre) {
		try {
			return familiasEJBBean.getNombre(nombre);
		} catch (Exception e) {
			return null;
		}
	}

	public List<Familia> getAll() {
		try {
			return familiasEJBBean.getAllFamilias();
		} catch (Exception e) {
			return null;
		}
	}
	
	public void onRowEdit(RowEditEvent event) {
	    Familia f = (Familia) event.getObject();
	   
	    FacesMessage message;
	    
	   try {
			if (f.getNombre().isEmpty() || f.getNombre().length() > 50 || f.getDescrip().isEmpty() || f.getDescrip().length() > 100 || f.getIncompat().isEmpty() || f.getIncompat().length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Debe ingresar todos los datos correctamente");
			} else {
					
				familiasEJBBean.update(f);
			    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
						"Familia modificada exitosamente!");
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
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

	@PostConstruct
	public void cargoLista() {
		try {
			// Carga la lista de Familias
			familiasList = this.getAll();
		} catch (Exception e) {
		}
	}

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

	public List<Familia> getFamiliasList() {
		return familiasList;
	}

	public void setFamiliasList(List<Familia> familiasList) {
		this.familiasList = familiasList;
	}

}
