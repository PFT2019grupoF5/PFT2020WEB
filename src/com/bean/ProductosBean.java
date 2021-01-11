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
import com.services.MovimientoBeanRemote;
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

	@EJB
	private MovimientoBeanRemote movimientosEJBBean;

	public String add() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ",
				"Producto ingresado exitosamente!");
		String retPage = "altaProductoPage";
		try {
			if (nombre.isEmpty() || nombre.length() > 50 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Revise el campo nombre, no puede ser vacío, contener más de 50 caracteres o solo espacios");
			}	else if (precio <= 0 || peso <= 0 || volumen <= 0 || estiba <= 0 
					|| stkMin <= 0 || stkTotal <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos numéricos no pueden ser inferior a 0 y son obligatorios");
			}	else if (lote.isEmpty() || segmentac == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos Lote y Segmentación son obligatorios");
			}	else if (idUsuario == null || idFamilia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos Usuario y Familia son obligatorios");
			} else if (felab == null || fven == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"La fecha de fabricación y vencimiento se deben ingresar en formato dd/mm/aa");
			} else if (felab.compareTo(fven) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"La fecha de fabricación no puede ser posterior a la de vencimiento");
			} else if (stkMin > stkTotal) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"El stock minimo no puede ser mayor al total");
			} else {
				
				if (getNombre(nombre) == null) {
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
					
					System.out.print(nombre.trim());
					
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ",
							"El Producto " + nombre.trim() + " ya existe. Por favor revise sus datos.");
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
			if (nombre.isEmpty() || nombre.length() > 50 || nombre.length()==0 || nombre.trim().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Revise el campo nombre, no puede ser vacío, contener más de 50 caracteres o solo espacios");
			}	else if (precio <= 0 || peso <= 0 || volumen <= 0 || estiba <= 0 
					|| stkMin <= 0 || stkTotal <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos numéricos no pueden ser inferior a 0 y son obligatorios");
			}	else if (lote.isEmpty() || segmentac == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos Lote y Segmentación son obligatorios");
			}	else if (lote.length()>10) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"El campo Lote no puede sobrepasar los 10 caracteres");
			}	else if (idUsuario == null || idFamilia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"Los campos Usuario y Familia son obligatorios");
			} else if (felab == null || fven == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"La fecha de fabricación y vencimiento se deben ingresar en formato dd/mm/aa");
			} else if (felab.compareTo(fven) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"La fecha de fabricación no puede ser posterior a la de vencimiento");
			} else if (stkMin > stkTotal) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
						"El stock minimo no puede ser mayor al total");
			} else {
				
				if (getNombre(nombre) != null) {
					Producto p = new Producto();
		   			p = productosEJBBean.getId(id);
		   			//Por requerimiento RF002 en Modificacion No se permitira cambiar el nombre
		   			//p.setNombre(nombre);
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
			} else if (movimientosEJBBean.validoBajaProducto(producto.getId()) != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
						"El Producto no se puede eliminar porque existe un registro de Perdida de este Producto en Movimientos. Elimínelo previamente de Movimientos para proceder");
			} else {
				productosEJBBean.delete(producto.getId());
				productosList.remove(producto); //se elimina el producto de la lista para que se muestre actualizado en la página

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
