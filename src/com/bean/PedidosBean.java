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
			if (pedfecestim == null || fecha == null || pedrecfecha == null || pedreccomentario.trim().isEmpty() || pedreccodigo <= 0 || pedestado == null || idUsuario == null) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos no pueden estar vacios!", null);
					System.out.println("Los campos no pueden estar vacios!");
			}else if(pedreccomentario.trim().length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El comentario no puede ser mayor a 250 caracteres", null);
				System.out.println("El comentario no puede ser mayor a 250 caracteres");
			} else {
					Pedido pe = new Pedido();
					pe.setPedfecestim(pedfecestim);
					pe.setFecha(fecha);
					pe.setPedreccodigo(pedreccodigo);
					pe.setPedrecfecha(pedrecfecha);
					pe.setPedreccomentario(pedreccomentario.trim());
					pe.setPedestado(pedestado);
					pe.setUsuario(usuariosEJBBean.getUsuario(idUsuario));
					pedidosEJBBean.add(pe);

					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "El Pedido se creo correctamente" , null);
					System.out.println("El Pedido se creo correctamente" + "\n" + pedfecestim + "\n" + fecha + "\n" + pedreccodigo + "\n" + pedrecfecha + "\n" + pedreccomentario + "\n" + pedestado + "\n" + idUsuario);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar pedido", null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.add");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String update(Long id, Date pedfecestim, Date fecha, int pedreccodigo, Date pedrecfecha,
			String pedreccomentario, estadoPedido pedestado, long usuarioIdNuevo) {
		FacesMessage message;
		
		String retPage = "modificarPedidoPage";
		try {
			if (pedfecestim == null || fecha == null || pedreccodigo <= 0 || pedreccomentario.trim().isEmpty() || pedestado == null || idUsuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos" , null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			}else if(pedreccomentario.trim().length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El comentario no puede ser mayor a 250 caracteres", null);
				System.out.println("El comentario no puede ser mayor a 250 caracteres");
			} else if (estadoPedido.E.equals(pedestado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El Pedido solo puede ser modificado mientras se encuentra en estado de Listo o Gestionado" , null);
				System.out.println("El Pedido solo puede ser modificado mientras se encuentra en estado de Listo o Gestionado");
				//faltaria una division, para que permita modificar a tipo E y no desde tipo E (solo modificar el estado en el segundo caso, desde E a otro tipo)??
			} else if (getId(id) == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido no existe" , null);
				System.out.println("Pedido no existe");
			} else {
					Pedido pe = new Pedido();
					pe.setPedfecestim(pedfecestim);
					pe.setFecha(fecha);
					pe.setPedreccodigo(pedreccodigo);
					pe.setPedrecfecha(pedrecfecha);
					pe.setPedreccomentario(pedreccomentario.trim());
					pe.setPedestado(pedestado);
					pe.setUsuario(usuariosEJBBean.getUsuario(usuarioIdNuevo));
					pedidosEJBBean.update(pe);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido Modificado exitosamente!" , null);
					System.out.println("Pedido Modificado exitosamente!");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. El pedido no se pudo modificar" , null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.update");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String delete(Pedido pedido) {
		FacesMessage message;
		
		String retPage = "bajaPedidoPage";
		try {
			if (pedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Seleccione un Pedido a borrar!" , null);
				System.out.println("Seleccione un Pedido a borrar!");
			} else if (renglonesPedidoEJBBean.getRenglonxPedido(pedido.getId()) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "El pedido tiene renglones asociados, no se puede borrar", null);
				System.out.println("Seleccione la casilla de confirmación!");
			} else {
					pedidosEJBBean.delete(pedido.getId());
					pedidosList.remove(pedido);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Pedido borrado exitosamente!" , null);
					System.out.println("Pedido borrado exitosamente!");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al borrar el pedido" , null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.delete");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
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
		try {
			return pedidosList = pedidosEJBBean.getAllPedidos();
		}catch (Exception e) {
			return null;
		}
	}

	public String getPedidosFechas() {
		FacesMessage message;
		String retPage = "reportePedidosFecha";
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String SfechaIni = sdf.format(fechaIni);
			String SfechaFin = sdf.format(fechaFin);

			System.out.println("sfechaIni : " + SfechaIni);
			System.out.println("sfechaIni : " + SfechaFin);

			
				listaPedidoReporteFechas = pedidosEJBBean.getPedidosEntreFechas(SfechaIni, SfechaFin);
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Mostrando Pedidos: Entre Fechas: " + fechaIni + " y " + fechaFin, null);
				FacesContext.getCurrentInstance().addMessage(null, message);
				System.out.println("Se envia listado entre fechas");
				return retPage;
			
		} catch (ServiciosException e) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al mostrar los pedidos" , null);
				System.out.println("No se ejecuto correctamente el listado de reportes de pedidos");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}
	
	public List<Pedido> getAll() {

		try {
			return pedidosEJBBean.getAllPedidos();
		} catch (Exception e) {
			return null;
		}
	}

	public void validarFechas() throws Exception {
		FacesMessage message;
		try {
		if (this.fechaIni != null && this.fechaFin != null) {
			if (this.fechaIni.compareTo(this.fechaFin) > 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"La Fecha Inicial no puede ser anterior a la Fecha Final", null));
				System.out.println("La Fecha Inicial no puede ser anterior a la Fecha Final");
			}
		}
		}catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo validar las fechas" , null);
			System.out.println("No se pudo validar las fechas");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		
	}

	@PostConstruct
	public void esP() {
		FacesMessage message;
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
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Contacte al administrador. No se construyo el listado de estados de pedido", null);
			System.out.println("No se construyo el listado de estados de pedido");
			FacesContext.getCurrentInstance().addMessage(null, message);
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
			System.out.println("Se envia modificacion de pedido de row edit");
			
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al modificar un pedido", null);
			System.out.println("No se ejecuto correctamente pedidosEJBBean.update en rowedit");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		
	}

	/***********************************************************************************************************************************/

	public String chequearPerfil() {
		
		try {
			if (perfilLogeado == null) {
				System.out.println("Usuario no esta logueado correctamente");
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario no esta logueado correctamente", null);
				FacesContext.getCurrentInstance().addMessage(null,  message);
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
		System.out.println("Usuario se deslogueo");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deslogueado!", null);
		FacesContext.getCurrentInstance().addMessage(null,  message);
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
