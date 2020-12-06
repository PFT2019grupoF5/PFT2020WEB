package com.bean;

import java.util.Date;
import java.util.LinkedList;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.entities.Almacenamiento;
import com.entities.Movimiento;
import com.entities.Pedido;
import com.entities.Producto;
import com.entities.RenglonPedido;
import com.enumerated.tipoMovimiento;
import com.enumerated.tipoPerfil;
import com.services.MovimientoBeanRemote;
import com.services.RenglonPedidoBeanRemote;

@SuppressWarnings("deprecation")
@ManagedBean(name = "renglonPedido")
@ViewScoped


public class RenglonesPedidoBean {
	
	private Long id;
	private int rennro;
	private int rencant;
	private Producto producto;
	private Pedido pedido;
	
	private static tipoPerfil perfilLogeado;
	
	private Movimiento selectedRenglonPedido;
	
	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;
	
	@EJB
	private RenglonPedidoBeanRemote renglonesPedidoEJBBean;
	
	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear el Renglón:","El Renglón se creo correctamente");
		String retPage = "altaRenglonesPedidoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
		}else if (rennro <= 0 || rencant <=0 || producto == null|| pedido == null){
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Es necesario ingresar todos los datos requeridos");
		}else {
			if(get() == null) {
				renglonesPedidoEJBBean.add(rennro, rencant, producto, pedido);
			}else {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
						"El Renglon ya existe");
			}
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return retPage;
	}catch (Exception e) {
		return null;
	}
	}
	
	public String update(Long id, int rennro, int rencant, Producto producto, Pedido pedido) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Renglon Modificado exitosamente!");
		String retPage = "modificarRenglonPage";
		try {
		
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
		}else if (rennro <= 0 || rencant <= 0 || producto == null || pedido == null){
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Es necesario ingresar todos los datos requeridos");
			}else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				renglonesPedidoEJBBean.update(id, rennro, rencant, producto, pedido);
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		}catch (Exception e) {
			return null;
		}
	}
	
	public String delete(Long id) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Renglon borrado exitosamente!");
		String retPage = "bajaRenglonPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			} else if (selectedRenglonPedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione un Renglón a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				renglonesPedidoEJBBean.delete(selectedRenglonPedido.getId());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}
	


	public RenglonPedido get() {
		try {
			return renglonesPedidoEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	
	
	public String checkRoles() {
		try {
			if (perfilLogeado == null) {
				return "index?faces-redirect=true";
			} else {
				return null;
			}
		} catch (Exception e) {
			return "index?faces-redirect=true";
		}
	}
	
	public LinkedList<RenglonPedido> getAll() {
		try {
			return renglonesPedidoEJBBean.getAll();
		} catch (Exception e) {
			return null;
		}
	}
	
	public String logout() {
		perfilLogeado = null;
		return "index?faces-redirect=true";
	}


	/***********************************************************************************************************************************/

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getRennro() {
		return rennro;
	}

	public void setRennro(int rennro) {
		this.rennro = rennro;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
}
