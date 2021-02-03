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
import com.exception.ServiciosException;
import com.services.FamiliaBeanRemote;
import com.services.ProductoBeanRemote;
import com.sun.media.jfxmedia.logging.Logger;

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

	@EJB
	private ProductoBeanRemote productosEJBBean;

	public String add() {
		FacesMessage message;
		String retPage = "altaFamiliaPage";
		
		try {
			if (nombre.isEmpty() || nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vacío o mayor a 50 caracteres", null);
				System.out.println("Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (descrip.isEmpty() || descrip.length() > 100) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Descripcion no puede ser vacío o mayor a 100 caracteres", null);
				System.out.println("Campo Descripcion no puede ser vacío o mayor a 100 caracteres");
			} else if (incompat.isEmpty() || incompat.length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Incompatible no puede ser vacío o mayor a 60 caracteres", null);
				System.out.println("Campo Incompatible no puede ser vacío o mayor a 60 caracteres");
			} else {
				if (getNombre(nombre) == null) {
					Familia f = new Familia();
					f.setNombre(nombre);
					f.setDescrip(descrip);
					f.setIncompat(incompat);
					familiasEJBBean.add(f);
					
					
					System.out.println("Se crea una familia con los datos:" + "\n" + nombre + "\n" + descrip + "\n" + incompat);

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se creo correctamente la familia: " +nombre, null);

				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Ya existe una Familia con ese nombre: " + nombre, null);
					System.out.println("Se intento crear una familia ya existente con el nombre:" + nombre);
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar familias", null);
			System.out.println("No se ejecuto correctamente familiasEJBBean.add");
			
		}
		return retPage;
	}

	public String update(Long id, String nombre, String descrip, String incompat) {
		FacesMessage message;
		String retPage = "modificarFamiliaPage";
		try {

			if (nombre.isEmpty() || nombre.length() > 50 || descrip.isEmpty() || descrip.length() > 100
					|| incompat.isEmpty() || incompat.length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe llenar todos los datos!", null);
				System.out.println("Debe llenar todos los datos!");
			} else {
				if (getNombre(nombre) != null) {
					Familia f = new Familia();

					f.setNombre(nombre);
					f.setDescrip(descrip);
					f.setIncompat(incompat);
					familiasEJBBean.update(f);
					
					System.out.println("Se modifico correctamente la familia");
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se modifico correctamente la familia", null);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar. Familia no existe", null);
					System.out.println("Se intento modificar una familia que no existe:");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador. No se pudo modificar la familia: ", null);
			System.out.println("No se pudo modificar la familia: ");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return retPage;
	}

	public String delete(Familia familia) throws ServiciosException {
		FacesMessage message;
		String retPage = "bajaFamiliaPage";
		try {
			if (familia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Seleccione una familia a borrar!", null);
				System.out.println("Familia borrada exitosamente!");
			} else {
				familiasEJBBean.delete(familia.getId());
				familiasList.remove(familia);

				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Familia borrada exitosamente!", null);
				System.out.println("Familia borrada exitosamente!");
				return retPage;
			}

			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador. Asegurese que la familia no tenga Productos asociados" , null);
			System.out.println("No se puede eliminar la Familia. Asegurese que no tenga Productos asociados");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return retPage;
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

	public List<Familia> obtenerTodasFamilias() throws ServiciosException {
		return familiasList = familiasEJBBean.getAllFamilias();
	}

	public void onRowEdit(RowEditEvent event) {
		Familia f = (Familia) event.getObject();

		FacesMessage message;

		try {
			if (f.getNombre().isEmpty() || f.getNombre().length() > 50 || f.getDescrip().isEmpty()
					|| f.getDescrip().length() > 100 || f.getIncompat().isEmpty() || f.getIncompat().length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe ingresar todos los datos correctamente", null);
				System.out.println("Debe ingresar todos los datos correctamente");
			} else {

				familiasEJBBean.update(f);
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Familia modificada exitosamente!", null);
				System.out.println("Familia modificada exitosamente!");
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo modificar la familia.", null);
			System.out.println("No se pudo modificar la familia.");

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
			if (familiasList == null) {
				familiasList = obtenerTodasFamilias();
			}
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo obtener todas las familias", null);
			System.out.println("No se pudo obtener todas las familias");
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
