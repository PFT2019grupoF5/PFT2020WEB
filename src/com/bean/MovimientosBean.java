package com.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import com.entities.Almacenamiento;
import com.entities.Movimiento;
import com.entities.Producto;
import com.enumerated.tipoMovimiento;
import com.enumerated.tipoPerfil;
import com.services.ProductoBeanRemote;
import com.services.AlmacenamientoBeanRemote;
import com.services.MovimientoBeanRemote;

@ManagedBean(name = "movimiento")
@ViewScoped

public class MovimientosBean {

	private Long id;
	private Date fecha;
	private int cantidad;
	private String descripcion;
	private double costo;
	private tipoMovimiento tipoMov;
	private Producto producto;
	private Almacenamiento almacenamiento;

	private static tipoPerfil perfilLogeado;

	private Movimiento selectedMovimiento;
	private List<SelectItem> tiposDeMov;

	private Long idProducto;
	private Long idAlmacenamiento;

	private List<Producto> listaProducto;
	private List<Almacenamiento> listaAlmacenamiento;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	@EJB
	private MovimientoBeanRemote movimientosEJBBean;

	@EJB
	private ProductoBeanRemote productoEJBBean;
	@EJB
	private AlmacenamientoBeanRemote almacenamientoEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Movimiento:",
				"El Movimiento se creo correctamente");
		String retPage = "altaMovimientoPage";
		try {
			if (fecha == null || cantidad <= 0 || costo <= 0 || tipoMov == null || producto == null
					|| almacenamiento == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else if (descripcion.length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Descripcion no puede ser mayor a 250 caracteres");
			} else {
				if (get() == null) {
					Movimiento m = new Movimiento();
					m.setFecha(fecha);
					m.setCantidad(cantidad);
					m.setDescripcion(descripcion);
					m.setCosto(costo);
					m.setTipoMov(tipoMov);
					m.setProducto(productoEJBBean.getProducto(idProducto));
					m.setAlmacenamiento(almacenamientoEJBBean.getAlmacenamiento(idAlmacenamiento));
					movimientosEJBBean.add(m);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El Movimiento ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String update(Long id, Date fecha, int cantidad, String descripcion, double costo, tipoMovimiento tipoMov,
			Producto producto, Almacenamiento almacenamiento) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Movimiento Modificado exitosamente!");
		String retPage = "modificarMovimientoPage";
		try {

			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)
					|| !tipoPerfil.OPERARIO.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR O OPERARIO para poder acceder");
			} else if (fecha == null || cantidad <= 0 || costo <= 0 || tipoMov == null || producto == null
					|| almacenamiento == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else if (descripcion.length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Campo Descripcion no puede ser mayor a 250 caracteres");
			} else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				Movimiento m = new Movimiento();
				m.setId(id);
				m.setFecha(fecha);
				m.setCantidad(cantidad);
				m.setDescripcion(descripcion);
				m.setCosto(costo);
				m.setTipoMov(tipoMov);
				m.setProducto(productoEJBBean.getProducto(idProducto));
				m.setAlmacenamiento(almacenamientoEJBBean.getAlmacenamiento(idAlmacenamiento));
				movimientosEJBBean.update(m);
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String delete() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Movimiento borrado exitosamente!");
		String retPage = "bajaMovimientoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)
					|| !tipoPerfil.OPERARIO.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR O OPERARIO para poder acceder");
			} else if (selectedMovimiento == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione un Movimiento a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				movimientosEJBBean.delete(selectedMovimiento.getId());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public Movimiento get() {
		try {
			return movimientosEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public List<Movimiento> getAll() {
		try {
			return movimientosEJBBean.getAllMovimientos();
		} catch (Exception e) {
			return null;
		}
	}

	@PostConstruct
	public void tiM() {
		try {
			ArrayList<SelectItem> tiM = new ArrayList<>();
			tiM.add(new SelectItem(tipoMovimiento.M, tipoMovimiento.M.toString()));
			tiM.add(new SelectItem(tipoMovimiento.C, tipoMovimiento.C.toString()));
			tiM.add(new SelectItem(tipoMovimiento.P, tipoMovimiento.P.toString()));
			tiposDeMov = tiM;
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

	public List<SelectItem> getTiposDeMov() {
		return tiposDeMov;
	}

	public void setTiposDeMov(List<SelectItem> tiposDeMov) {
		this.tiposDeMov = tiposDeMov;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public tipoMovimiento getTipoMov() {
		return tipoMov;
	}

	public void setTipoMov(tipoMovimiento tipoMov) {
		this.tipoMov = tipoMov;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Almacenamiento getAlmacenamiento() {
		return almacenamiento;
	}

	public void setAlmacenamiento(Almacenamiento almacenamiento) {
		this.almacenamiento = almacenamiento;
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

	public List<Producto> getListaProducto() {
		return listaProducto;
	}

	public void setListaProducto(List<Producto> listaProducto) {
		this.listaProducto = listaProducto;
	}

	public List<Almacenamiento> getListaAlmacenamiento() {
		return listaAlmacenamiento;
	}

	public void setListaAlmacenamiento(List<Almacenamiento> listaAlmacenamiento) {
		this.listaAlmacenamiento = listaAlmacenamiento;
	}

}
