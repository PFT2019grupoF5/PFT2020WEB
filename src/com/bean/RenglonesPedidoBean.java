package com.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.entities.Movimiento;
import com.entities.Pedido;
import com.entities.Producto;
import com.entities.RenglonPedido;
import com.enumerated.tipoPerfil;
import com.services.PedidoBeanRemote;
import com.services.ProductoBeanRemote;
import com.services.RenglonPedidoBeanRemote;

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

	private Long idProducto;
	private Long idPedido;

	private List<Producto> listaProducto;
	private List<Pedido> listaPedido;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	@EJB
	private RenglonPedidoBeanRemote renglonesPedidoEJBBean;

	@EJB
	private ProductoBeanRemote productoEJBBean;

	@EJB
	private PedidoBeanRemote pedidoEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear el Renglón:",
				"El Renglón se creo correctamente");
		String retPage = "altaRenglonesPedidoPage";
		try {
			if (rennro <= 0 || rencant <= 0 || producto == null || pedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else {
				if (get() == null) {
					RenglonPedido r = new RenglonPedido();
					r.setRennro(rennro);
					r.setRencant(rencant);
					r.setProducto(productoEJBBean.getProducto(idProducto));
					r.setPedido(pedidoEJBBean.getPedido(idPedido));
					renglonesPedidoEJBBean.add(r);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El Renglon ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
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
			} else if (rennro <= 0 || rencant <= 0 || producto == null || pedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				if (get() != null) {
				RenglonPedido r = new RenglonPedido();
				r.setId(id);
				r.setRennro(rennro);
				r.setRencant(rencant);
				r.setProducto(productoEJBBean.getProducto(idProducto));
				r.setPedido(pedidoEJBBean.getPedido(idPedido));
				renglonesPedidoEJBBean.update(r);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"RengloPedido no existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
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

	public List<RenglonPedido> getAll() {
		try {
			return renglonesPedidoEJBBean.getAllRenglonesPedido();
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

	public int getRencant() {
		return rencant;
	}

	public void setRencant(int rencant) {
		this.rencant = rencant;
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

	public Long getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	public List<Producto> getListaProducto() {
		return listaProducto;
	}

	public void setListaProducto(List<Producto> listaProducto) {
		this.listaProducto = listaProducto;
	}

	public List<Pedido> getListaPedido() {
		return listaPedido;
	}

	public void setListaPedido(List<Pedido> listaPedido) {
		this.listaPedido = listaPedido;
	}

}
