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
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.FamiliaBeanRemote;
import com.services.ProductoBeanRemote;


@ManagedBean(name = "familia")
@ViewScoped

public class FamiliasBean {

	private Long id;
	private String nombre;
	private String descrip;
	private String incompat;

	private static tipoPerfil perfilLogeado;

	private List<Familia> familiasList;


	@EJB
	private FamiliaBeanRemote familiasEJBBean;

	@EJB
	private ProductoBeanRemote productosEJBBean;

	public String add() {
		FacesMessage message;
		String retPage = "altaFamiliaPage";
		
		try {
			if (nombre.trim().isEmpty() || nombre.trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vacío o mayor a 50 caracteres", null);
				System.out.println("Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (descrip.trim().isEmpty() || descrip.trim().length() > 100) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Descripcion no puede ser vacío o mayor a 100 caracteres", null);
				System.out.println("Campo Descripcion no puede ser vacío o mayor a 100 caracteres");
			} else if (incompat.trim().isEmpty() || incompat.trim().length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Incompatible no puede ser vacío o mayor a 60 caracteres", null);
				System.out.println("Campo Incompatible no puede ser vacío o mayor a 60 caracteres");
			} else if (getNombre(nombre.trim()) != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe una Familia con ese nombre: " + nombre, null);
				System.out.println("Se intento crear una familia ya existente con el nombre: " + nombre);
			}else {
					Familia f = new Familia();
					f.setNombre(nombre.trim());
					f.setDescrip(descrip.trim());
					f.setIncompat(incompat.trim());
					familiasEJBBean.add(f);
					
					System.out.println("Se crea una familia con los datos:" + "\n" + nombre + "\n" + descrip + "\n" + incompat);
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se creo correctamente la familia: " +nombre, null);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar familias", null);
			System.out.println("No se ejecuto correctamente familiasEJBBean.add");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String update(Long id, String nombre, String descrip, String incompat) {
		FacesMessage message;
		String retPage = "modificarFamiliaPage";
		try {

			if (nombre.trim().isEmpty() || nombre.trim().length() > 50 || descrip.trim().isEmpty() || descrip.trim().length() > 100	|| incompat.trim().isEmpty() || incompat.trim().length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe llenar todos los datos!", null);
				System.out.println("Debe llenar todos los datos!");
			} else if (getNombre(nombre.trim()) != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe una Familia con ese nombre: " + nombre, null);
				System.out.println("Ya existe una Familia con ese nombre: " + nombre);
			}else if (getId(id) == null){
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Modificar. Familia no existe", null);
				System.out.println("Se intento modificar una familia que no existe:");
			} else {	
					Familia f = new Familia();
					f.setNombre(nombre.trim());
					f.setDescrip(descrip.trim());
					f.setIncompat(incompat.trim());
					familiasEJBBean.update(f);
					
					System.out.println("Se modifico correctamente la familia");
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se modifico correctamente la familia", null);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo modificar la familia: ", null);
			System.out.println("No se pudo modificar la familia: ");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String delete(Familia familia) throws ServiciosException {
		FacesMessage message;
		String retPage = "bajaFamiliaPage";
		try {
			if (familia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione una familia a borrar!", null);
				System.out.println("Seleccione una familia a borrar!");
			} else if (productosEJBBean.getProductosxFam(familia.getId()) > 0){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "No se puede eliminar la familia porque tiene productos asociados. Elimine primero los productos que tiene la familia", null);
				System.out.println("No se puede eliminar la familia porque tiene productos asociados. Elimine primero los productos que tiene la familia " + familia.getNombre());
			} else {
				familiasEJBBean.delete(familia.getId());
				familiasList.remove(familia);
				familiasList = familiasEJBBean.getAllFamilias();
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Familia borrada exitosamente!", null);
				System.out.println("Familia borrada exitosamente!");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Asegurese que la familia no tenga Productos asociados" , null);
			System.out.println("No se puede eliminar la Familia. Asegurese que no tenga Productos asociados");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		familiasList = familiasEJBBean.getAllFamilias();
		return retPage;
	}

	public Familia get() {
		try {
			return familiasEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Familia getId(Long id) {
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
		try {
			return familiasList = familiasEJBBean.getAllFamilias();
		}catch (Exception e) {
			return null;
		}
	}

	public String onRowEdit(RowEditEvent event) throws ServiciosException {
		Familia f = (Familia) event.getObject();
		FacesMessage message;
		String retPage = "modificarFamiliaPage";
		try {
			if (f.getNombre().trim().isEmpty() || f.getNombre().trim().length() > 50 || f.getDescrip().trim().isEmpty() || f.getDescrip().trim().length() > 100	|| f.getIncompat().trim().isEmpty() || f.getIncompat().trim().length() > 60) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe llenar todos los datos!", null);
				System.out.println("Debe llenar todos los datos!");
			}else if (getNombre(f.getNombre().trim()) != null && !(f.getNombre().equals(familiasEJBBean.getId(f.getId()).getNombre()))) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe una Familia con ese nombre", null);
				System.out.println("Ya existe una Familia con ese nombre: " + nombre);
			}else if (getId(f.getId()) == null){
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Modificar. Familia no existe", null);
				System.out.println("Se intento modificar una familia que no existe:");
			}else {
				f.setNombre(f.getNombre().trim());
				f.setDescrip(f.getDescrip().trim());
				f.setIncompat(f.getIncompat().trim());
			   	familiasEJBBean.update(f);
			   	familiasList = familiasEJBBean.getAllFamilias();
			   	message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Se modifico correctamente la familia", null);
			   	System.out.println("Pasa datos al update desde rowEdit de Familias Bean");
			   	FacesContext.getCurrentInstance().addMessage(null, message);
			   	return retPage;
				}
			} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo modificar la Familia.", null);
			System.out.println("No se pudo modificar la Familia en row edit");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		familiasList = familiasEJBBean.getAllFamilias();
		return retPage;
	}

	/***********************************************************************************************************************************/

	public String chequearPerfil() {
		
		try {
			if (perfilLogeado == null) {
				System.out.println("Usuario no esta logueado correctamente");
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario no esta logueado correctamente", null);
				FacesContext.getCurrentInstance().addMessage(null, message);
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
		System.out.println("Usuario se deslogueo");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deslogueado!", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		return "Login?faces-redirect=true";
	}

	/***********************************************************************************************************************************/

	@PostConstruct
	public void cargoLista() {
		try {
			if (familiasList == null) {
				familiasList = obtenerTodasFamilias();
				System.out.println("Se carga lista de familias");
			}
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo obtener las familias", null);
			System.out.println("No se pudo obtener las familias");
			FacesContext.getCurrentInstance().addMessage(null, message);
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
