package com.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.RowEditEvent;

import com.entities.Familia;
import com.entities.Pedido;
import com.entities.Producto;
import com.entities.RenglonReporte;
import com.entities.Usuario;
import com.enumerated.estadoPedido;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.PedidoBeanRemote;
import com.services.RenglonPedidoBeanRemote;
import com.services.UsuarioBeanRemote;

@ManagedBean(name = "pedido")
@SessionScoped
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
	private Usuario idUsu;

	// edit
	private Pedido ped;
	private List<Pedido> pedidosList;

	private List<Pedido> listaPedidoReporteFechas;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	private Date fechaIni;
	private Date fechaFin;

	@EJB
	private PedidoBeanRemote pedidosEJBBean;

	@EJB
	private UsuarioBeanRemote usuariosEJBBean;

	@EJB
	private RenglonPedidoBeanRemote renglonesPedidoEJBBean;

	public String add() {
		FacesMessage message;
		String retPage = "altaPedidoPage";
		try {
			if (pedfecestim == null || fecha == null || pedrecfecha == null || pedreccomentario.isEmpty()
					|| pedreccodigo <= 0 || pedestado == null || idUsuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos no pueden estar vacios!");

			} else {
				if (get() == null) {

					Pedido pe = new Pedido();
					pe.setPedfecestim(pedfecestim);
					pe.setFecha(fecha);
					pe.setPedreccodigo(pedreccodigo);
					pe.setPedrecfecha(pedrecfecha);
					pe.setPedreccomentario(pedreccomentario);
					pe.setPedestado(pedestado);
					pe.setUsuario(usuariosEJBBean.getUsuario(idUsuario));
					pedidosEJBBean.add(pe);

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al crear Pedido:",
							"El Pedido se creo correctamente");
					retPage = "altaPedidoPage";
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El Pedido ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String update(Long id, Date pedfecestim, Date fecha, int pedreccodigo, Date pedrecfecha,
			String pedreccomentario, estadoPedido pedestado, long usuarioIdNuevo) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Pedido Modificado exitosamente!");
		String retPage = "modificarPedidoPage";
		try {
			if (pedfecestim == null || fecha == null || pedreccodigo <= 0 || pedreccomentario.isEmpty()
					|| pedestado == null || idUsuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else if (estadoPedido.E.equals(pedestado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"El Pedido solo puede ser modificado mientras se encuentra en estado de Listo o Gestionado");
			} else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				if (getId(id) != null) {
					Pedido pe = new Pedido();
					pe.setPedfecestim(pedfecestim);
					pe.setFecha(fecha);
					pe.setPedreccodigo(pedreccodigo);
					pe.setPedrecfecha(pedrecfecha);
					pe.setPedreccomentario(pedreccomentario);
					pe.setPedestado(pedestado);
					pe.setUsuario(usuariosEJBBean.getUsuario(usuarioIdNuevo));
					pedidosEJBBean.update(pe);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ", "Pedido no existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String delete(Pedido pedido) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
				"Pedido borrado exitosamente!");
		String retPage = "bajaPedidoPage";
		try {
			if (pedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione un Pedido a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione la casilla de confirmación!");
			} else {
				if (renglonesPedidoEJBBean.getRenglonxPedido(pedido.getId()) > 0) {

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
							"Seleccione la casilla de confirmación!");
				} else {
					pedidosEJBBean.delete(pedido.getId());
					pedidosList.remove(pedido);
				}
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

	public Pedido getId(Long id) {
		try {
			return pedidosEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public List<Pedido> obtenerTodosPedidos() throws ServiciosException {
		return pedidosList = pedidosEJBBean.getAllPedidos();
	}

	public String getPedidosFechas() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos:", "Entre Fechas");
		FacesContext.getCurrentInstance().addMessage(null, message);

		if (fechaIni.compareTo(fechaFin) < 0) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String SfechaIni = sdf.format(fechaIni);
			String SfechaFin = sdf.format(fechaFin);

			System.out.println("sfechaIni : " + SfechaIni);
			System.out.println("sfechaIni : " + SfechaFin);

			try {
				listaPedidoReporteFechas = pedidosEJBBean.getPedidosEntreFechas(SfechaIni, SfechaFin);
			} catch (ServiciosException e) {
				e.printStackTrace();
			}

		}

		return "resultadoReportePedidosFecha";
	}
	
//	public String getReporteFechas() {
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos:", "Entre Fechas");
//		FacesContext.getCurrentInstance().addMessage(null, message);
//
//		if (fechaIni.compareTo(fechaFin) < 0) {
//
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String SfechaIni = sdf.format(fechaIni);
//			String SfechaFin = sdf.format(fechaFin);
//
//			System.out.println("sfechaIni : " + SfechaIni);
//			System.out.println("sfechaIni : " + SfechaFin);
//
//			try {
//				listaPedidoReporteFechas = pedidosEJBBean.getReporteEntreFechas(SfechaIni, SfechaFin);
//			} catch (ServiciosException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		return "reportePedidosFecha";
//	}

	public List<Pedido> getAll() {

		try {
			return pedidosEJBBean.getAllPedidos();
		} catch (Exception e) {
			return null;
		}
	}

	public void validarFechas() throws Exception {
		if (this.fechaIni != null && this.fechaFin != null) {
			if (this.fechaIni.compareTo(this.fechaFin) > 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Error!", "La Fecha Inicial no puede ser anterior a la Fecha Final"));
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

			// rowEdit
			if (pedidosList == null) {
				ped = new Pedido();
				pedidosList = obtenerTodosPedidos();
			}
		} catch (Exception e) {
		}
	}

	public void onRowEdit(RowEditEvent event) {
		Pedido pe = (Pedido) event.getObject();

		FacesMessage message;

		try {
			// Traigo clase usuario completa por el ID que se seleccionó en el desplegable
			Long usuId = pe.getUsuario().getId();

			pe.setUsuario(usuariosEJBBean.getId(usuId));

			pedidosEJBBean.update(pe);
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
					"Pedido modificado exitosamente!");

			FacesContext.getCurrentInstance().addMessage(null, message);
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
		return pedidosList;
	}

	public void setListaPedido(List<Pedido> listaPedido) {
		this.pedidosList = listaPedido;
	}

	public List<Pedido> getListaPedidoReporteFechas() {
		return listaPedidoReporteFechas;
	}

	public void setListaPedidoReporteFechas(List<Pedido> listaPedidoReporteFechas) {
		this.listaPedidoReporteFechas = listaPedidoReporteFechas;
	}

//	public List<RenglonReporte> getListaPedidoReporteFechas() {
//		return listaPedidoReporteFechas;
//	}
//
//	public void setListaPedidoReporteFechas(List<RenglonReporte> listaPedidoReporteFechas) {
//		this.listaPedidoReporteFechas = listaPedidoReporteFechas;
//	}
	
	

}
