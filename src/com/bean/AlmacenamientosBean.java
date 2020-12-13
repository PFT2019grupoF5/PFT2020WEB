package com.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.entities.Almacenamiento;
import com.entities.EntidadLoc;
import com.enumerated.tipoPerfil;
import com.services.AlmacenamientoBeanRemote;
import com.services.EntidadLocBeanRemote;

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

	private Almacenamiento selectedAlmacenamiento;
	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	private Long idEntidadLoc;
	private List<EntidadLoc> listaEntidadLoc;
	private List<Almacenamiento> almacenamientosList;
	private Long idProducto;
	private Long idAlmacenamiento;

	@EJB
	private AlmacenamientoBeanRemote almacenamientosEJBBean;

	@EJB
	private EntidadLocBeanRemote entidadLocEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
				"Almacenamiento ingresado exitosamente!");
		String retPage = "altaAlmacenamientoPage";
		try {
			if (volumen <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Volumen no puede ser vacío o mayor a 50 caracteres");
			} else if (nombre.length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (costoop <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Costo Operacion no puede ser vacío o mayor a 50 caracteres");
			} else if (capestiba <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Capacidad de Estiba no puede ser vacío o mayor a 50 caracteres");
			} else if (cappeso <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Capacidad de Peso no puede ser vacío o mayor a 50 caracteres");
			} else if (entidadLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo entidadLoc no puede ser vacío");
			} else {
				if (get() == null) {
					Almacenamiento a = new Almacenamiento();
					a.setVolumen(volumen);
					a.setNombre(nombre);
					a.setCostoop(costoop);
					a.setCapestiba(capestiba);
					a.setCappeso(cappeso);
					a.setEntidadLoc(entidadLocEJBBean.getEntidadLoc(idEntidadLoc));
					almacenamientosEJBBean.add(a);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El almacenamiento ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String update(Long id, int volumen, String nombre, double costoop, double capestiba, double cappeso,
			EntidadLoc entidadLoc) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Almacenamiento modificada exitosamente!");
		String retPage = "modificarAlmacenamientoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acción");
			} else if (volumen <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Volumen no puede ser vacío o mayor a 50 caracteres");
			} else if (nombre.length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			} else if (costoop <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Costo Operacion no puede ser vacío o mayor a 50 caracteres");
			} else if (capestiba <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Capacidad de Estiba no puede ser vacío o mayor a 50 caracteres");
			} else if (cappeso <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Capacidad de Peso no puede ser vacío o mayor a 50 caracteres");
			} else if (entidadLoc == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo entidadLoc no puede ser vacío");
			} else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				if (get() != null) {
					Almacenamiento a = new Almacenamiento();
					a.setId(id);
					a.setVolumen(volumen);
					a.setNombre(nombre);
					a.setCostoop(costoop);
					a.setCapestiba(capestiba);
					a.setCappeso(cappeso);
					a.setEntidadLoc(entidadLocEJBBean.getEntidadLoc(idEntidadLoc));
					almacenamientosEJBBean.update(a);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"Almacenamiento no existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String delete() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Almacenamiento borrado exitosamente!");
		String retPage = "bajaAlmacenamientoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acción");
			} else if (selectedAlmacenamiento == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione una Ciudad a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				almacenamientosEJBBean.delete(selectedAlmacenamiento.getId());
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

	public long getIdEntidadLoc() {
		return idEntidadLoc;
	}

	public void setIdEntidadLoc(long idEntidadLoc) {
		this.idEntidadLoc = idEntidadLoc;
	}

	public List<EntidadLoc> getListaEntidadLoc() {
		return listaEntidadLoc;
	}

	public void setListaEntidadLoc(List<EntidadLoc> listaEntidadLoc) {
		this.listaEntidadLoc = listaEntidadLoc;
	}

	public List<Almacenamiento> getAlmacenamientosList() {
		return almacenamientosList;
	}

	public void setAlmacenamientosList(List<Almacenamiento> almacenamientosList) {
		this.almacenamientosList = almacenamientosList;
	}

	public Long getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}

	public Long getIdAlmacenamiento() {
		return idAlmacenamiento;
	}

	public void setIdAlmacenamiento(Long idAlmacenamiento) {
		this.idAlmacenamiento = idAlmacenamiento;
	}
	
	

}
