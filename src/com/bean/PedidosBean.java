package com.bean;

import java.util.Date;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.entities.Pedido;
import com.entities.Usuario;
import com.enumerated.estadoPedido;
import com.enumerated.tipoPerfil;
import com.services.PedidoBeanRemote;


@ManagedBean(name = "pedido")
@ViewScoped


public class PedidosBean {
	
	private Long id;
	private Date pedfecestim;
	private Date fecha;
	private int pedreccodigo;
	private Date pedrecfecha;
	private String pedreccomentario;
	private estadoPedido pedestado;
	private Usuario usuario;
	
	private static tipoPerfil perfilLogeado;
	
	private Pedido selectedPedido;
	
	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;
	
	@EJB
	private PedidoBeanRemote pedidosEJBBean;
	
	
	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Pedido:","El Pedido se creo correctamente");
		String retPage = "altaPedidoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			}else if (pedfecestim == null || fecha == null || pedreccodigo<=0 || pedreccomentario.isEmpty() || pedestado == null || usuario == null){
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
					"Es necesario ingresar todos los datos requeridos");
		}else {
			if(get() == null) {
				pedidosEJBBean.add(pedfecestim, fecha, pedreccodigo, pedrecfecha, pedreccomentario, pedestado, usuario);
			}else {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
						"El Pedido ya existe");
			}
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return retPage;
	}catch (Exception e) {
		return null;
	}
	}
	
	public String update(Long id, Date pedfecestim, Date fecha, int pedreccodigo, Date pedrecfecha, String pedreccomentario, estadoPedido pedestado, Usuario usuario) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Pedido Modificado exitosamente!");
		String retPage = "modificarPedidoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			}else if (pedfecestim == null || fecha == null || pedreccodigo<=0 || pedreccomentario.isEmpty() || pedestado == null || usuario == null){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			}else if (estadoPedido.E.equals(pedestado)){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"El Pedido solo puede ser modificado mientras se encuentra en estado de Listo o Gestionado");
			}else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				pedidosEJBBean.update(id, pedfecestim, fecha, pedreccodigo, pedrecfecha, pedreccomentario, pedestado, usuario);
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		}catch (Exception e) {
			return null;
		}
	}
	
	public String delete(Long id) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Pedido borrado exitosamente!");
		String retPage = "bajaPedidoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado) ) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			} else if (selectedPedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione un Pedido a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				pedidosEJBBean.delete(selectedPedido.getId());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}
	


	public Pedido get() {
		try {
			return pedidosEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	

	
	public LinkedList<Pedido> getPedidosFechas(String fechaDesde, String fechaHasta) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos:","Entre Fechas");
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			}else {
			LinkedList<Pedido> listaPedidos = pedidosEJBBean.entreFechas(fechaHasta, fechaHasta);
			return listaPedidos;
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			return null;
		}
		return null;
		
	}
	
	public LinkedList<Pedido> getAll() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos:","Todos los Pedidos");
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			}else {
			LinkedList<Pedido> listaPedidos = pedidosEJBBean.getAll(); 
			return listaPedidos;
			}
			FacesContext.getCurrentInstance().addMessage(null, message);

		} catch (Exception e) {
			return null;
		}
		return null;
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

	public Date getPedfecestim() {
		return pedfecestim;
	}

	public void setPedfecestim(Date pedfecestim) {
		this.pedfecestim = pedfecestim;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getPedreccodigo() {
		return pedreccodigo;
	}

	public void setPedreccodigo(int pedreccodigo) {
		this.pedreccodigo = pedreccodigo;
	}

	public Date getPedrecfecha() {
		return pedrecfecha;
	}

	public void setPedrecfecha(Date pedrecfecha) {
		this.pedrecfecha = pedrecfecha;
	}

	public String getPedreccomentario() {
		return pedreccomentario;
	}

	public void setPedreccomentario(String pedreccomentario) {
		this.pedreccomentario = pedreccomentario;
	}

	public estadoPedido getPedestado() {
		return pedestado;
	}

	public void setPedestado(estadoPedido pedestado) {
		this.pedestado = pedestado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	
}
