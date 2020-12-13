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

import org.primefaces.event.RowEditEvent;

import com.services.FamiliaBeanRemote;
import com.services.UsuarioBeanRemote;
import com.entities.Familia;
import com.entities.Producto;
import com.entities.Usuario;
import com.enumerated.Segmentacion;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.ProductoBeanRemote;

@ManagedBean(name = "producto")
@ViewScoped

public class ProductosBean {
	private Long id;
	private String nombre;
	private String lote;
	private double precio;
	private Date felab;
	private Date fven;
	private double peso;
	private double volumen;
	private int estiba;
	private double stkMin;
	private double stkTotal;
	private Segmentacion segmentac;
	private Usuario usuario;
	private Familia familia;

	private static tipoPerfil perfilLogeado;
	private List<SelectItem> segmentaciones;

	private Producto selectedProducto;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;

	// Para buscar Familia y Usuario en el add y en edit
	private Long idUsuario;
	private Long idFamilia;
	private Usuario idUsu;

	// edit
	private Producto produc;
	private List<Producto> productosList;

	@EJB
	private ProductoBeanRemote productosEJBBean;

	@EJB
	private FamiliaBeanRemote familiasEJBBean;

	@EJB
	private UsuarioBeanRemote usuariosEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
				"Producto ingresado exitosamente!");
		String retPage = "altaProductoPage";
		try {
			if (tipoPerfil.OPERARIO.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
						"No tiene permisos suficientes para realizar esta acción");
				System.out.println("***********ENTRA EN LA VALIDACION DEL PERFIL**********************");
			} else if (nombre.isEmpty() || lote.isEmpty() || precio <= 0 || felab == null || fven == null || peso <= 0
					|| volumen <= 0 || estiba <= 0 || stkMin <= 0 || stkTotal <= 0 || segmentac == null
					|| idUsuario == null || idFamilia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
				System.out.println("**************Entra ACA por la validacion de campos null o 0**************");
			}	else if (nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los datos ingresados superan el largo permitido. Por favor revise sus datos");
			} else if (felab.compareTo(fven) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"La fecha de fabricación no puede ser posterior a la de vencimiento");
			} else {
				if (get() == null) {
		   			Producto p = new Producto();
		   			p.setNombre(nombre);
		   			p.setLote(lote);
		   			p.setPrecio(precio);
		   			p.setFelab(felab);
		   			p.setFven(fven);
		   			p.setPeso(peso);
		   			p.setVolumen(volumen);
		   			p.setEstiba(estiba);
		   			p.setStkMin(stkMin);
		   			p.setStkTotal(stkTotal);
		   			p.setSegmentac(segmentac);
		   			p.setUsuario(usuariosEJBBean.getUsuario(idUsuario));
		   			p.setFamilia(familiasEJBBean.getFamilia(idFamilia));
					productosEJBBean.add(p);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El Producto ya existe. Por favor revise sus datos.");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String update(Long id, String nombre, String lote, double precio, Date felab, Date fven, double peso,
			double volumen, int estiba, double stkMin, double stkTotal, Segmentacion segmentac, long usuarioIdNuevo,
			long familiaIdNuevo) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
				"Producto modificado exitosamente!");
		String retPage = "modificarProductoPage";
		try {
			if (nombre.isEmpty() || lote.isEmpty() || precio == 0 || felab == null || fven == null || peso == 0
					|| volumen == 0 || estiba == 0 || stkMin == 0 || stkTotal == 0 || segmentac == null
					|| usuarioIdNuevo != 0 || familiaIdNuevo != 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else if (nombre.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los datos ingresados superan el largo permitido. Por favor revise sus datos");
			} else if (felab.compareTo(fven) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"La fecha de fabricación no puede ser posterior a la de vencimiento");
				// } else if (!confirmarModificar) {
				// message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar:
				// ",
				// "Seleccione la casilla de confirmación!");
			} else {
				if (getNombre(nombre) != null) {

		   			Producto p = new Producto();
		   			p.setNombre(nombre);
		   			p.setLote(lote);
		   			p.setPrecio(precio);
		   			p.setFelab(felab);
		   			p.setFven(fven);
		   			p.setPeso(peso);
		   			p.setVolumen(volumen);
		   			p.setEstiba(estiba);
		   			p.setStkMin(stkMin);
		   			p.setStkTotal(stkTotal);
		   			p.setSegmentac(segmentac);
		   			
		   			
		   			p.setUsuario(usuariosEJBBean.getUsuario(usuarioIdNuevo));
		   			p.setFamilia(familiasEJBBean.getFamilia(familiaIdNuevo));
					productosEJBBean.update(p);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"Producto no existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public String delete(Producto producto) {
		FacesMessage message; 
		String retPage = "bajaProductoPage";
		try {
			if (producto == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"Seleccione Un Producto a borrar!");
			} else if (productosEJBBean.validoBajaProductos(producto)) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"El Producto no se puede eliminar porque existe en Movimientos. Elimínelo previamente de Movimientos para proceder");
			//} else if (!confirmarBorrado) {
			//	message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
			//			"Seleccione la casilla de confirmación!");
			} else {
				productosEJBBean.delete(producto.getId());
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
						"Producto borrado exitosamente!");
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			return null;
		}
	}

	public Producto get() {
		try {
			return productosEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public Producto getNombre(String nombre) {
		try {
			return productosEJBBean.getNombre(nombre);
		} catch (Exception e) {
			return null;
		}
	}

	public List<Producto> getAll() {
		try {
			return productosEJBBean.getAllProductos();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Producto> obtenerTodosProductos() throws ServiciosException {
		return productosList = productosEJBBean.getAllProductos();

	}

	public void onRowEdit(RowEditEvent event) {
	    Producto p = (Producto) event.getObject();
	   
	    FacesMessage message;
	    
	   try {
			if (p.getNombre().isEmpty() || p.getLote().isEmpty() || p.getPrecio() == 0 || p.getFelab() == null || p.getFven() == null || p.getPeso() == 0 || p.getVolumen() == 0 || p.getEstiba() == 0 || p.getStkMin() == 0 || p.getStkTotal() == 0 || p.getSegmentac() == null || p.getUsuario() == null || p.getFamilia() == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Es necesario ingresar todos los datos requeridos");
			} else if (p.getNombre().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los datos ingresados superan el largo permitido. Por favor revise sus datos");
			} else if (p.getFelab().compareTo(p.getFven())>0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"La fecha de fabricación no puede ser posterior a la de vencimiento");
			} else {
					
				//Traigo clases usuario y familia completas por el ID que se seleccionó en el desplegable
				Long usuId = p.getUsuario().getId();
				Long famId = p.getFamilia().getId();
				
				p.setFamilia(familiasEJBBean.getId(famId));
				p.setUsuario(usuariosEJBBean.getId(usuId));
				
				productosEJBBean.update(p);
			    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
						"Producto modificado exitosamente!");
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {

		}
	}

	
	
	@PostConstruct
	public void segm() {
		try {
			ArrayList<SelectItem> segm = new ArrayList<>();
			segm.add(new SelectItem(Segmentacion.S, Segmentacion.S.toString()));
			segm.add(new SelectItem(Segmentacion.N, Segmentacion.N.toString()));
			segmentaciones = segm;
			
			// rowEdit
			if (productosList==null) {
				produc = new Producto();
				productosList = obtenerTodosProductos();
			}	
			idUsu = usuariosEJBBean.getId(id);
			
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

	public List<SelectItem> getSegmentacion() {
		return segmentaciones;
	}

	public void setSegmentacion(List<SelectItem> segmentaciones) {
		this.segmentaciones = segmentaciones;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public Date getFelab() {
		return felab;
	}

	public void setFelab(Date felab) {
		this.felab = felab;
	}

	public Date getFven() {
		return fven;
	}

	public void setFven(Date fven) {
		this.fven = fven;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public double getVolumen() {
		return volumen;
	}

	public void setVolumen(double volumen) {
		this.volumen = volumen;
	}

	public int getEstiba() {
		return estiba;
	}

	public void setEstiba(int estiba) {
		this.estiba = estiba;
	}

	public double getStkMin() {
		return stkMin;
	}

	public void setStkMin(double stkMin) {
		this.stkMin = stkMin;
	}

	public double getStkTotal() {
		return stkTotal;
	}

	public void setStkTotal(double stkTotal) {
		this.stkTotal = stkTotal;
	}

	public Segmentacion getSegmentac() {
		return segmentac;
	}

	public void setSegmentac(Segmentacion segmentac) {
		this.segmentac = segmentac;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Familia getFamilia() {
		return familia;
	}

	public void setFamilia(Familia familia) {
		this.familia = familia;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Long getIdFamilia() {
		return idFamilia;
	}

	public void setIdFamilia(Long idFamilia) {
		this.idFamilia = idFamilia;
	}

	public Producto getProduc() {
		return produc;
	}

	public void setProduc(Producto produc) {
		this.produc = produc;
	}

	public List<Producto> getProductosList() {
		return productosList;
	}

	public void setProductosList(List<Producto> productosList) {
		this.productosList = productosList;
	}

}
