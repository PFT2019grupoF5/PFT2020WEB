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
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos no pueden estar vacios!", null);
				System.out.println("Los campos no pueden estar vacios!");
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

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "El Pedido se creo correctamente" , null);
					System.out.println("El Pedido se creo correctamente" + "\n" + pedfecestim + "\n" + fecha + "\n" + pedreccodigo + "\n" + pedrecfecha + "\n" + pedreccomentario + "\n" + pedestado + "\n" + idUsuario);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "El Pedido ya existe" , null);
					System.out.println("El Pedido ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar pedido", null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.add");
			
		}
		return retPage;
	}

	public String update(Long id, Date pedfecestim, Date fecha, int pedreccodigo, Date pedrecfecha,
			String pedreccomentario, estadoPedido pedestado, long usuarioIdNuevo) {
		FacesMessage message;
		
		String retPage = "modificarPedidoPage";
		try {
			if (pedfecestim == null || fecha == null || pedreccodigo <= 0 || pedreccomentario.isEmpty()
					|| pedestado == null || idUsuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos" , null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (estadoPedido.E.equals(pedestado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El Pedido solo puede ser modificado mientras se encuentra en estado de Listo o Gestionado" , null);
				System.out.println("El Pedido solo puede ser modificado mientras se encuentra en estado de Listo o Gestionado");
			} else if (!confirmarModificar) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Seleccione la casilla de confirmación!" , null);
				System.out.println("No se ejecuto correctamente pedidosEJBBean.update");
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
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido Modificado exitosamente!" , null);
					System.out.println("Pedido Modificado exitosamente!");
					
					
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido no existe" , null);
					System.out.println("Pedido no existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador. El pedido no se pudo modificar" , null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.update");
		}
		return retPage;
	}

	public String delete(Pedido pedido) {
		FacesMessage message;
		
		String retPage = "bajaPedidoPage";
		try {
			if (pedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Seleccione un Pedido a borrar!" , null);
				System.out.println("Seleccione un Pedido a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Seleccione la casilla de confirmación!" , null);
				System.out.println("Seleccione la casilla de confirmación!");
			} else {
				if (renglonesPedidoEJBBean.getRenglonxPedido(pedido.getId()) > 0) {

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Seleccione la casilla de confirmación!", null);
					System.out.println("Seleccione la casilla de confirmación!");
				} else {
					pedidosEJBBean.delete(pedido.getId());
					pedidosList.remove(pedido);
					
					 message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido borrado exitosamente!" , null);
					 System.out.println("Pedido borrado exitosamente!");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador. Error al borrar el pedido" , null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.delete");
		}
		return retPage;
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
		FacesMessage message;
		

		if (fechaIni.compareTo(fechaFin) < 0) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String SfechaIni = sdf.format(fechaIni);
			String SfechaFin = sdf.format(fechaFin);

			System.out.println("sfechaIni : " + SfechaIni);
			System.out.println("sfechaIni : " + SfechaFin);

			try {
				listaPedidoReporteFechas = pedidosEJBBean.getPedidosEntreFechas(SfechaIni, SfechaFin);
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos: Entre Fechas", null);
				System.out.println("Mostrando Pedidos: Entre Fechas");
			} catch (ServiciosException e) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al mostrar los pedidos" , null);
				System.out.println("No se ejecuto correctamente el listado de reportes de pedidos");
			}
			FacesContext.getCurrentInstance().addMessage(null, message);

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
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"La Fecha Inicial no puede ser anterior a la Fecha Final", null));
				System.out.println("La Fecha Inicial no puede ser anterior a la Fecha Final");
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
				System.out.println("Se construyo el listado de estados de pedido");
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No se construyo el listado de estados de pedido", null));
			System.out.println("No se construyo el listado de estados de pedido");
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
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido modificado exitosamente!", null);
			System.out.println("Pedido modificado exitosamente!");

			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al modificar un pedido", null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.update en rowedit");
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
