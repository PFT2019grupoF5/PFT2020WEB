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
import com.entities.Movimiento;
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
	
	@EJB
	private AlmacenamientoBeanRemote almacenamientosEJBBean;

	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;

	@EJB
	private MovimientoBeanRemote movimientosEJBBean;

	public String add() {
		FacesMessage message ;
		String retPage = "altaAlmacenamientoPage";
		
		try {
			
			if (volumen <= 0 || costoop <= 0 || capestiba <= 0 || cappeso <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos numéricos deben ser mayores a 0. Por favor, revise sus datos.");
			} else if (nombre.length() > 250 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 250 caracteres o contener solo espacios");
			} else if (idEntidadLoc  <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Local no puede ser vacío");
			} else {
				if (almacenamientosEJBBean.getNombre(nombre) == null) { // no existe el almacenamiento con ese nombre
					Almacenamiento a = new Almacenamiento();
					a.setVolumen(volumen);
					a.setNombre(nombre);
					a.setCostoop(costoop);
					a.setCapestiba(capestiba);
					a.setCappeso(cappeso);
					a.setEntidadLoc(entidadLocEJBBean.getEntidadLoc(idEntidadLoc));
					almacenamientosEJBBean.add(a);

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
							"Almacenamiento ingresado exitosamente!");

				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Registrar: ",
							"El almacenamiento " + nombre + " ya existe");
					return "";
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String update(Long id, int volumen, String nombre, double costoop, double capestiba, double cappeso, EntidadLoc entidadLoc) {
		FacesMessage message ;
		String resultado="";
		
		try {
			
			if (volumen <= 0 || costoop <= 0 || capestiba <= 0 || cappeso <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos numéricos deben ser mayores a 0. Por favor, revise sus datos.");
			} else if (nombre.length() > 250 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 250 caracteres o contener solo espacios");
			} else if (idEntidadLoc  <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Local no puede ser vacío");
			} else {
				
					if (almacenamientosEJBBean.getNombre(nombre) != null) { // si existe el almacenamiento con ese nombre, puedo modificar
							Almacenamiento a = new Almacenamiento();
							a.setId(id);
							a.setVolumen(volumen);
							a.setNombre(nombre);
							a.setCostoop(costoop);
							a.setCapestiba(capestiba);
							a.setCappeso(cappeso);
							a.setEntidadLoc(entidadLocEJBBean.getEntidadLoc(idEntidadLoc));
							almacenamientosEJBBean.update(a);
							message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
									"Almacenamiento " + nombre + " fue modificado!");
							resultado = "retPage";	
					
					} else {
							message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Modificar: ",
									"Almacenamiento no existe");
							resultado = "modificarAlmacenamientoPage";	
					}
				}
					
			FacesContext.getCurrentInstance().addMessage(null, message);
			return resultado;
			} catch (ServiciosException e) {
				e.printStackTrace();
				return null;
			}
	}
			

	public String delete(Almacenamiento almacenamiento) {
		FacesMessage message ;
		String retPage = "";
		
		try {
			if (almacenamiento == null) {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Borrar: ",
							"Seleccione Un Movimiento a borrar!");
				} else {
				
				if (movimientosEJBBean.getMovimientoxAlmac(almacenamiento.getId()) != 0) {
					// No se puede eliminar el Almacenamiento porque hay Movimientos que lo tienen asociado
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Borrar: ",
							"No se puede eliminar el Almacenamiento porque tiene Movimientos asociados. Elimine primero los Movimientos que tienen el Almacenamiento" + almacenamiento.getNombre());
				} else {
					almacenamientosEJBBean.delete(almacenamiento.getId());
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
							"Almacenamiento borrado exitosamente!");
					
					retPage = "bajaAlmacenamientoPage";
				}
			}
			
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
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
	    operacionOK = false;
	    
	   try {
			if (a.getCapestiba() == 0 || a.getCappeso() == 0 || a.getCostoop() == 0 || a.getNombre() == "" || a.getVolumen() == 0 || a.getEntidadLoc() == null) {
				 message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else {
					
				//Traigo la clase Local completa por el ID que se seleccionó en el desplegable
				Long locId = a.getEntidadLoc().getId();
				
				a.setEntidadLoc(entidadLocEJBBean.getId(locId));
				
				almacenamientosEJBBean.update(a);
				 message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
						"Almacenamiento modificado exitosamente!");
				 operacionOK = true;
			}
			FacesContext.getCurrentInstance().addMessage(null,  message);
		} catch (Exception e) {

		}
	}
	
	
	@PostConstruct
	public void cargoLista() {
		try {
			// Carga la lista de Almacenamientos
			almacenamientosList = this.getAll();
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
