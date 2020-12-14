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

import com.entities.Pedido;
import com.entities.Usuario;
import com.enumerated.estadoPedido;
import com.enumerated.tipoPerfil;
import com.services.PedidoBeanRemote;
import com.services.UsuarioBeanRemote;

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
	private List<SelectItem> estadoDelPedido;

	private Long idUsuario;
	private List<Usuario> listaUsuario;
	private List<Pedido> listaPedido;
	
	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

    private Date fechaIni;
    private Date fechaFin;

    @EJB
	private PedidoBeanRemote pedidosEJBBean;

	@EJB
	private UsuarioBeanRemote usuariosEJBBean;

	public String add() {
		FacesMessage message;
		String retPage = "altaPedidoPage";
		try {
			//if (pedfecestim == null || fecha == null || pedreccodigo <= 0 || pedreccomentario.isEmpty()
			//		|| pedestado == null || idUsuario <= 0) {
			//	message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
			//			"Es necesario ingresar todos los datos requeridos");
			//} else {
				if (get() == null) {
					Pedido p = new Pedido();
					p.setPedfecestim(pedfecestim);
					p.setFecha(fecha);
					p.setPedreccodigo(pedreccodigo);
					p.setPedrecfecha(pedrecfecha);
					p.setPedreccomentario(pedreccomentario);
					p.setPedestado(pedestado);
					p.setUsuario(usuariosEJBBean.getId(idUsuario));
					pedidosEJBBean.add(p);

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Pedido:",
							"El Pedido se creo correctamente");

				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El Pedido ya existe");
				}
			//}
			//FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String update(Long id, Date pedfecestim, Date fecha, int pedreccodigo, Date pedrecfecha,
			String pedreccomentario, estadoPedido pedestado, Usuario usuario) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Pedido Modificado exitosamente!");
		String retPage = "modificarPedidoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			} else if (pedfecestim == null || fecha == null || pedreccodigo <= 0 || pedreccomentario.isEmpty()
					|| pedestado == null || usuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else if (estadoPedido.E.equals(pedestado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"El Pedido solo puede ser modificado mientras se encuentra en estado de Listo o Gestionado");
			} else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				if (get() != null) {
					Pedido p = new Pedido();
					p.setId(id);	
					p.setPedfecestim(pedfecestim);
					p.setFecha(fecha);
					p.setPedreccodigo(pedreccodigo);
					p.setPedrecfecha(pedrecfecha);
					p.setPedreccomentario(pedreccomentario);
					p.setPedestado(pedestado);
					p.setUsuario(usuario);
					pedidosEJBBean.update(p);
				}else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"Pedido no existe");
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
				"Pedido borrado exitosamente!");
		String retPage = "bajaPedidoPage";
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
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

	public void getPedidosFechas(String fechaDesde, String fechaHasta) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos:", "Entre Fechas");
		try {
			//if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
			//	message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
			//			"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			//} else {
				this.listaPedido = pedidosEJBBean.getPedidosEntreFechas(fechaHasta, fechaHasta);
				//return listaPedidos;
			//}
			//FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			//return null;
		}
		//return null;

	}

	public List<Pedido> getAll() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos:", "Todos los Pedidos");
		try {
			if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado) || !tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"Debe ser un Usuario ADMINISTRADOR o SUPERVISOR para poder acceder");
			} else {
				List<Pedido> listaPedidos = pedidosEJBBean.getAllPedidos();
				return listaPedidos;
			}
			FacesContext.getCurrentInstance().addMessage(null, message);

		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public void validarFechas() throws Exception {
        if (this.fechaIni != null && this.fechaFin != null) {
            if (this.fechaIni.compareTo(this.fechaFin)>0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "La Fecha Inicial no puede ser anterior a la Fecha Final"));
            }
        }
    }
	
	
	
	@PostConstruct
	public void esP() {
		try {
			ArrayList<SelectItem> esP = new ArrayList<>();
			esP.add(new SelectItem(estadoPedido.G, estadoPedido.G.toString()));
			esP.add(new SelectItem(estadoPedido.C, estadoPedido.C.toString()));
			esP.add(new SelectItem(estadoPedido.P, estadoPedido.P.toString()));
			esP.add(new SelectItem(estadoPedido.E, estadoPedido.E.toString()));
			esP.add(new SelectItem(estadoPedido.L, estadoPedido.L.toString()));
			estadoDelPedido = esP;
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

	public List<SelectItem> getEstadoDelPedido() {
		return estadoDelPedido;
	}

	public void setEstadoDelPedido(List<SelectItem> estadoDelPedido) {
		this.estadoDelPedido = estadoDelPedido;
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

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public List<Usuario> getListaUsuario() {
		return listaUsuario;
	}

	public void setListaUsuario(List<Usuario> listaUsuario) {
		this.listaUsuario = listaUsuario;
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}


	public List<Pedido> getListaPedido() {
		return listaPedido;
	}

	public void setListaPedido(List<Pedido> listaPedido) {
		this.listaPedido = listaPedido;
	}

}
