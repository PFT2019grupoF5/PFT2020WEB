package com.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import com.entities.Pedido;
import com.entities.Producto;
import com.entities.RenglonPedido;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
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


	private Long idProducto;
	private Long idPedido;


	private List<RenglonPedido> listaRenglonPedido;

	@EJB
	private RenglonPedidoBeanRemote renglonesPedidoEJBBean;

	@EJB
	private ProductoBeanRemote productoEJBBean;

	@EJB
	private PedidoBeanRemote pedidosEJBBean;

	public String add() {
		FacesMessage message ;
		String retPage = "altaRenglonPedidoPage";
		try {
			if (rennro <= 0 || rencant <= 0 || idProducto == null || idPedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos", null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (getRen(rennro) != null){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe ese numero de renglon", null);
				System.out.println("Ya existe ese numero de renglon");
			} else {
					RenglonPedido r = new RenglonPedido();
					r.setRennro(rennro);
					r.setRencant(rencant);
					r.setProducto(productoEJBBean.getProducto(idProducto));
					r.setPedido(pedidosEJBBean.getPedido(idPedido));
					renglonesPedidoEJBBean.add(r);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "El Renglón se creo correctamente", null);
					System.out.println("El Renglón se creo correctamente" + "\n" + rennro + "\n" + rencant + "\n" + idProducto + "\n" + idPedido);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
			}
			
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar renglon de pedidos", null);
			System.out.println("No se ejecuto correctamente renglonesPedidoEJBBean.add");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String update(Long id, int rennro, int rencant, long productoIdNuevo, long pedidoIdNuevo) {
		FacesMessage message;
		String retPage = "modificarRenglonPedidoPage";
		try {
			
			if (rennro < 0 || rencant < 0 || producto.getId() <= 0 || pedido.getId() <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN,"Es necesario ingresar todos los datos requeridos" , null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (getId(id) == null) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "RengloPedido no existe", null);
					System.out.println("RengloPedido no existe");
			} else {
					RenglonPedido r = new RenglonPedido();
					r.setRennro(rennro);
					r.setRencant(rencant);
					r.setProducto(productoEJBBean.getProducto(productoIdNuevo));
					r.setPedido(pedidosEJBBean.getPedido(pedidoIdNuevo));
					renglonesPedidoEJBBean.update(r);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Renglon Modificado exitosamente!", null);
					System.out.println("Renglon Modificado exitosamente!");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. El renglon de pedidos no se pudo modificar" , null);
			System.out.println("No se ejecuto correctamente renglonesPedidoEJBBean.update");	
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String delete(RenglonPedido renglonPedido) throws ServiciosException {
		FacesMessage message;
		String retPage = "bajaRenglonPedidoPage";
		try {
			if (renglonPedido == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un Renglón a borrar!" , null);
				System.out.println("Seleccione un Renglón a borrar!");
			} else {
				renglonesPedidoEJBBean.delete(renglonPedido.getId());
				listaRenglonPedido.remove(renglonPedido);
				listaRenglonPedido = renglonesPedidoEJBBean.getAllRenglonesPedido();
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Renglon borrado exitosamente!", null);
				System.out.println("Renglon borrado exitosamente!");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al borrar el renglon de pedidos" , null);
			System.out.println("No se ejecuto correctamente renglonesPedidoEJBBean.delete");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		listaRenglonPedido = renglonesPedidoEJBBean.getAllRenglonesPedido();
		return retPage;
	}

	public RenglonPedido get() {
		try {
			return renglonesPedidoEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	public RenglonPedido getId(Long id) {
		try {
			return renglonesPedidoEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}
	public RenglonPedido getRen(int rennro) {
		try {
			return renglonesPedidoEJBBean.getRen(rennro);
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
	
	public int getRenglonxPedido (long idPedido) {
		try {
			return renglonesPedidoEJBBean.getRenglonxPedido(idPedido);
		}catch (Exception e) {
			return 0;
		}
	}
	
	public List<RenglonPedido> obtenerTodosRenglonesPedidos() throws ServiciosException {
		try {
			return listaRenglonPedido = renglonesPedidoEJBBean.getAllRenglonesPedido();
		}catch (Exception e) {
			return null;
		}
	}
	
	
	public String onRowEdit(RowEditEvent event) throws ServiciosException {
	    RenglonPedido rp = (RenglonPedido) event.getObject();
	    FacesMessage message;
	    String retPage = "modificarRenglonPedidoPage";
	    try {
		   if (rp.getRennro() <= 0 || rp.getRencant() <= 0 || rp.getProducto() == null || rp.getPedido() == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN,"Es necesario ingresar todos los datos requeridos" , null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
		   } else if (getRen(rp.getRennro()) != null){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe ese numero de renglon", null);
				System.out.println("Ya existe ese numero de renglon");
			} else if (getId(rp.getId()) == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "RengloPedido no existe", null);
				System.out.println("RengloPedido no existe");
			} else {	
				Long proId = rp.getProducto().getId();
				Long pedId = rp.getPedido().getId();
				rp.setProducto(productoEJBBean.getId(proId));
				rp.setPedido(pedidosEJBBean.getId(pedId));
				renglonesPedidoEJBBean.update(rp);
				listaRenglonPedido = renglonesPedidoEJBBean.getAllRenglonesPedido();
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Renglon Modificado exitosamente!", null);
			    System.out.println("Renglon de Pedido modificado exitosamente!");  
			    FacesContext.getCurrentInstance().addMessage(null, message);
			    return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo modificar el Renglón del Pedido", null);
			System.out.println("No se pudo modificar el Renglón en row edit de RenglonesPedidoBean");
		}
	    FacesContext.getCurrentInstance().addMessage(null, message);
	    listaRenglonPedido = renglonesPedidoEJBBean.getAllRenglonesPedido();
	    return retPage;
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

	
	@PostConstruct
	public void cargoLista() {
		try {
			if (listaRenglonPedido == null) {
				listaRenglonPedido = obtenerTodosRenglonesPedidos();
				System.out.println("Se creo la lista de renglones de pedidos");
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se creo la lista de renglones de pedidos" , null));
			System.out.println("No se creo la lista de renglones de pedidos");
		}
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


	public List<RenglonPedido> getListaRenglonPedido() {
		return listaRenglonPedido;
	}

	public void setListaRenglonPedido(List<RenglonPedido> listaRenglonPedido) {
		this.listaRenglonPedido = listaRenglonPedido;
	}

	
	
}
