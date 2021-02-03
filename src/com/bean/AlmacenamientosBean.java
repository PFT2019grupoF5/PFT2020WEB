package com.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import com.entities.Almacenamiento;
import com.entities.EntidadLoc;
import com.entities.Usuario;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.AlmacenamientoBeanRemote;
import com.services.EntidadLocBeanRemote;
import com.services.MovimientoBeanRemote;

@ManagedBean(name = "almacenamiento")
@ViewScoped
public class AlmacenamientosBean {

	
	private Long id;
	private int volumen;
	private String nombre;
	private double costoop;
	private double capestiba;
	private double cappeso;
	private EntidadLoc entidadLoc;

	private static tipoPerfil perfilLogeado;

	private Long idEntidadLoc;
	private List<Almacenamiento> almacenamientosList;
	private boolean operacionOK;
	
	private EntidadLoc idLoc;
	
	@EJB
	private AlmacenamientoBeanRemote almacenamientosEJBBean;

	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;

	@EJB
	private MovimientoBeanRemote movimientosEJBBean;

	public String add() {
		FacesMessage message;
		String retPage = "altaAlmacenamientoPage";
		
		try {
			
			if (volumen <= 0 || costoop <= 0 || capestiba <= 0 || cappeso <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos num�ricos deben ser mayores a 0. Por favor, revise sus datos.", null);
				System.out.println("Los campos num�ricos deben ser mayores a 0. Por favor, revise sus datos.");
			} else if (nombre.length() > 250 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vac�o o mayor a 250 caracteres o contener simbolos y espacios", null);
				System.out.println("Campo Nombre no puede ser vac�o o mayor a 250 caracteres o contener solo espacios");
			} else if (idEntidadLoc  <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Local no puede ser vac�o", null);
				System.out.println("Campo Local no puede ser vac�o");
			} else {
				if (almacenamientosEJBBean.getNombre(nombre) == null) { 
					Almacenamiento a = new Almacenamiento();
					a.setVolumen(volumen);
					a.setNombre(nombre);
					a.setCostoop(costoop);
					a.setCapestiba(capestiba);
					a.setCappeso(cappeso);
					a.setEntidadLoc(entidadLocEJBBean.getEntidadLoc(idEntidadLoc));
					almacenamientosEJBBean.add(a);

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Almacenamiento ingresado exitosamente!" + nombre, null);
					System.out.println("Almacenamiento ingresado exitosamente!" + "\n" + nombre + "\n" + volumen + "\n" + costoop + "\n" + capestiba + "\n" + cappeso + "\n" + idEntidadLoc);

				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un almacenamiento con el nombre: " + nombre, null);
					System.out.println("Ya existe un almacenamiento con el nombre: " + nombre);
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar almacenamiento", null);
			System.out.println("No se ejecuto correctamente almacenamientosEJBBean.add");
			
		}
		return retPage;
	}

	public String update(Long id, int volumen, String nombre, double costoop, double capestiba, double cappeso, long entidadLocIdNuevo) {
		FacesMessage message;
		String retPage="modificarAlmacenamientoPage";
		
		try {
			
			if (volumen <= 0 || costoop <= 0 || capestiba <= 0 || cappeso <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos num�ricos deben ser mayores a 0. Por favor, revise sus datos.", null);
				System.out.println("Los campos num�ricos deben ser mayores a 0. Por favor, revise sus datos.");
			} else if (nombre.length() > 250 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Nombre no puede ser vac�o o mayor a 250 caracteres o contener solo espacios", null);
				System.out.println("Campo Nombre no puede ser vac�o o mayor a 250 caracteres o contener solo espacios");
			} else if (entidadLoc.getId()  <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Local no puede ser vac�o", null);
				System.out.println("Campo Local no puede ser vac�o");
			} else if (almacenamientosEJBBean.getNombre(nombre.trim()) ==null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe un Local con ese nombre. Por favor ingrese otro.", null);
				System.out.println("Ya existe un Local con ese nombre. Por favor ingrese otro.");
			} else {
					
				if (almacenamientosEJBBean.getId(id) != null ) { 
					Almacenamiento a = new Almacenamiento();
					a.setId(id);
					a.setVolumen(volumen);
					a.setNombre(nombre);
					a.setCostoop(costoop);
					a.setCapestiba(capestiba);
					a.setCappeso(cappeso);
					a.setEntidadLoc(entidadLocEJBBean.getEntidadLoc(entidadLocIdNuevo));
					almacenamientosEJBBean.update(a);
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "�xito al Modificar el almacenamiento." , null);
					System.out.println("�xito al Modificar el almacenamiento." );
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Almacenamiento no existe",	null);
					System.out.println("Almacenamiento no existe" );
				}
	
			}
					
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
			} catch (ServiciosException e) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo modificar el almacenamiento.", null);
				System.out.println("No se ejecuto correctamente familiasEJBBean.update");
				
			}
		return retPage;
	}
	
	
	public String delete(Almacenamiento almacenamiento) {
		FacesMessage message ;
		String retPage = "bajaAlmacenamientoPage";
		
		try {
			if (almacenamiento == null) {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Seleccione Un Movimiento a borrar!", null);
					System.out.println("Seleccione Un Movimiento a borrar!");
				} else {
				
				if (movimientosEJBBean.getMovimientoxAlmac(almacenamiento.getId()) > 0) {
					// No se puede eliminar el Almacenamiento porque hay Movimientos que lo tienen asociado
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede eliminar el Almacenamiento porque tiene Movimientos asociados. Elimine primero los Movimientos que tienen el Almacenamiento",  null);
					System.out.println("No se puede eliminar el Almacenamiento porque tiene Movimientos asociados. Elimine primero los Movimientos que tienen el Almacenamiento");
				} else {
					almacenamientosEJBBean.delete(almacenamiento.getId());
					almacenamientosList.remove(almacenamiento); 
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Almacenamiento borrado exitosamente!", null);
					System.out.println("Almacenamiento borrado exitosamente!");
					
					return retPage;
					
				}
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

	public Almacenamiento get() {
		try {
			return almacenamientosEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public List<Almacenamiento> getAll() {
		try {
			return almacenamientosEJBBean.getAllAlmacenamientos();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public void onRowEdit(RowEditEvent event) {
	    Almacenamiento a = (Almacenamiento) event.getObject();
	    
	    FacesMessage message;
	    
	   try {
		 //Traigo clase local completa por el ID que se seleccion� en el desplegable
			Long locId = a.getEntidadLoc().getId();
			
			a.setEntidadLoc(entidadLocEJBBean.getId(locId));

			
			almacenamientosEJBBean.update(a);
		    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Almacenamiento modificado exitosamente!", null);
		    System.out.println("Familia modificada exitosamente!");
		    
	   FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo modificar el almacenamiento ", null);
			System.out.println("No se pudo modificar el almacenamiento ");
			
		}
	}

	
	
	@PostConstruct
	public void cargoLista() {
		try {
			// Carga la lista de Almacenamientos
			almacenamientosList = this.getAll();
			
			idLoc = entidadLocEJBBean.getId(id);
			
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo cargar la lista de almacenamientos", null);
			System.out.println("No se pudo cargar la lista de almacenamientos");
			
			
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

	public int getVolumen() {
		return volumen;
	}

	public void setVolumen(int volumen) {
		this.volumen = volumen;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getCostoop() {
		return costoop;
	}

	public void setCostoop(double costoop) {
		this.costoop = costoop;
	}

	public double getCapestiba() {
		return capestiba;
	}

	public void setCapestiba(double capestiba) {
		this.capestiba = capestiba;
	}

	public double getCappeso() {
		return cappeso;
	}

	public void setCappeso(double cappeso) {
		this.cappeso = cappeso;
	}

	public EntidadLoc getEntidadLoc() {
		return entidadLoc;
	}

	public void setEntidadLoc(EntidadLoc entidadLoc) {
		this.entidadLoc = entidadLoc;
	}

	public List<Almacenamiento> getAlmacenamientosList() {
		return almacenamientosList;
	}

	public void setAlmacenamientosList(List<Almacenamiento> almacenamientosList) {
		this.almacenamientosList = almacenamientosList;
	}

	public void setIdEntidadLoc(Long idEntidadLoc) {
		this.idEntidadLoc = idEntidadLoc;
	}

	public Long getIdEntidadLoc() {
		return idEntidadLoc;
	}

	public boolean isOperacionOK() {
		return operacionOK;
	}

	public void setOperacionOK(boolean operacionOK) {
		this.operacionOK = operacionOK;
	}
	
	

}
