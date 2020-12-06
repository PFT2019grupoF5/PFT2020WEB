package com.bean;

import java.util.Date;
import java.util.LinkedList;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.entities.Almacenamiento;
import com.entities.Movimiento;
import com.entities.Producto;
import com.enumerated.tipoMovimiento;
import com.enumerated.tipoPerfil;
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
	
	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;
	
	@EJB
	private MovimientoBeanRemote movimientosEJBBean;
	
	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Movimiento:","El Movimiento se creo correctamente");
		String retPage = "altaMovimientoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado) || !tipoPerfil.OPERARIO.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR O OPERARIO para poder acceder");
		}else if (fecha == null || cantidad<=0 || costo<=0 || tipoMov == null || producto == null || almacenamiento == null){
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Es necesario ingresar todos los datos requeridos");
		}else if(descripcion.length() > 250) {
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Campo Descripcion no puede ser mayor a 250 caracteres");
		}else {
			if(get() == null) {
				movimientosEJBBean.add(fecha, cantidad, descripcion, costo, tipoMov, producto, almacenamiento);
			}else {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
						"El Movimiento ya existe");
			}
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return retPage;
	}catch (Exception e) {
		return null;
	}
	}
	
	public String update(Long id, Date fecha, int cantidad, String descripcion, double costo, tipoMovimiento tipoMov, Producto producto, Almacenamiento almacenamiento) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Movimiento Modificado exitosamente!");
		String retPage = "modificarMovimientoPage";
		try {
		
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado) || !tipoPerfil.OPERARIO.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR O OPERARIO para poder acceder");
		}else if (fecha == null || cantidad<=0 || costo<=0 || tipoMov == null || producto == null || almacenamiento == null){
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Es necesario ingresar todos los datos requeridos");
		}else if(descripcion.length() > 250) {
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Campo Descripcion no puede ser mayor a 250 caracteres");
			}else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				movimientosEJBBean.update(id, fecha, cantidad, descripcion, costo, tipoMov, producto, almacenamiento);
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		}catch (Exception e) {
			return null;
		}
	}
	
	public String delete(Long id) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Movimiento borrado exitosamente!");
		String retPage = "bajaMovimientoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado) || !tipoPerfil.OPERARIO.equals(perfilLogeado)) {
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

	
	

	
	public LinkedList<Movimiento> getAll() {
		try {
			return movimientosEJBBean.getAll();
		} catch (Exception e) {
			return null;
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

	
	
	
	
}
