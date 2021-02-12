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
import com.services.RenglonPedidoBeanRemote;

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

	@EJB
	private RenglonPedidoBeanRemote renglonesPedidosEJBBean;
	
	public String add() {
		FacesMessage message;
		String retPage = "altaProductoPage";
		try {
			if (nombre.trim().isEmpty() || nombre.trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Revise el campo nombre, no puede ser vacío, contener más de 50 caracteres o solo espacios" , null);
				System.out.println("Revise el campo nombre, no puede ser vacío, contener más de 50 caracteres o solo espacios");
			}	else if (precio <= 0 || peso <= 0 || volumen <= 0 || estiba <= 0 || stkMin <= 0 || stkTotal <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos numéricos no pueden ser inferior a 0 y son obligatorios" , null);
				System.out.println("Los campos numéricos no pueden ser inferior a 0 y son obligatorios");
			}	else if (lote.trim().isEmpty() || segmentac == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos Lote y Segmentación son obligatorios" , null);
				System.out.println("Los campos Lote y Segmentación son obligatorios");
			} else if (lote.trim().length()>10) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El campo Lote no puede sobrepasar los 10 caracteres" , null);
				System.out.println("El campo Lote no puede sobrepasar los 10 caracteres");
			}	else if (idUsuario == null || idFamilia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos Usuario y Familia son obligatorios" , null);
				System.out.println("Los campos Usuario y Familia son obligatorios");
			} else if (felab == null || fven == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de fabricación y vencimiento se deben ingresar en formato dd/mm/aa" , null);
				System.out.println("La fecha de fabricación y vencimiento se deben ingresar en formato dd/mm/aa");
			} else if (felab.compareTo(fven) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de fabricación no puede ser posterior a la de vencimiento" , null);
				System.out.println("La fecha de fabricación no puede ser posterior a la de vencimiento");
			} else if (stkMin > stkTotal) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El stock minimo no puede ser mayor al total" , null);
				System.out.println("El stock minimo no puede ser mayor al total");
			} else if (getNombre(nombre.trim()) != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El Producto ya existe. Por favor revise sus datos." , null);
				System.out.println("El producto ya existe");	
			} else {
					Producto p = new Producto();
		   			p.setNombre(nombre.trim());
		   			p.setLote(lote.trim());
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
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Producto ingresado exitosamente! " + nombre, null);
					System.out.println("El Producto se creo correctamente" + "\n" + nombre + "\n" + lote + "\n" + precio + "\n" + felab + "\n" + fven + "\n" + peso + "\n" + volumen + "\n" + estiba + "\n" + stkMin + "\n" + stkTotal + "\n" + segmentac + "\n"  + idUsuario + "\n"  + idFamilia);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar producto", null);
			System.out.println("No se ejecuto correctamente productosEJBBean.add");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String update(Long id, String nombre, String lote, double precio, Date felab, Date fven, double peso,
			double volumen, int estiba, double stkMin, double stkTotal, Segmentacion segmentac, long usuarioIdNuevo,
			long familiaIdNuevo) {
		FacesMessage message;
		String retPage = "modificarProductoPage";
		try {
			if (nombre.trim().isEmpty() || nombre.trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Revise el campo nombre, no puede ser vacío, contener más de 50 caracteres o solo espacios", null);
				System.out.println("Revise el campo nombre, no puede ser vacío, contener más de 50 caracteres o solo espacios");
			}else if (precio <= 0 || peso <= 0 || volumen <= 0 || estiba <= 0 || stkMin <= 0 || stkTotal <= 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos numéricos no pueden ser inferior a 0 y son obligatorios" , null);
				System.out.println("Los campos numéricos no pueden ser inferior a 0 y son obligatorios");
			}else if (lote.trim().isEmpty() || segmentac == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN,"Los campos Lote y Segmentación son obligatorios" , null);
				System.out.println("Los campos Lote y Segmentación son obligatorios");
			}else if (lote.trim().length()>10) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El campo Lote no puede sobrepasar los 10 caracteres" , null);
				System.out.println("El campo Lote no puede sobrepasar los 10 caracteres");
			}else if (idUsuario == null || idFamilia == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los campos Usuario y Familia son obligatorios" , null);
				System.out.println("Los campos Usuario y Familia son obligatorios");
			} else if (felab == null || fven == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de fabricación y vencimiento se deben ingresar en formato dd/mm/aa" , null);
				System.out.println("La fecha de fabricación y vencimiento se deben ingresar en formato dd/mm/aa");
			} else if (felab.compareTo(fven) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de fabricación no puede ser posterior a la de vencimiento" , null);
				System.out.println("La fecha de fabricación no puede ser posterior a la de vencimiento");
			} else if (stkMin > stkTotal) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "El stock minimo no puede ser mayor al total" , null);
				System.out.println("El stock minimo no puede ser mayor al total");
			} else if (getNombre(nombre.trim()) != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Producto no existe", null);
				System.out.println("Producto no existe");
			} else {
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
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto modificado exitosamente!", null);
					System.out.println("Producto modificado exitosamente!");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. El producto no se pudo modificar" , null);
			System.out.println("No se ejecuto correctamente productosEJBBean.update");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}


	
	public String delete(Producto producto) {
		FacesMessage message; 
		String retPage = "bajaProductoPage";
		try {
			if (producto == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione Un Producto a borrar!" , null);
				System.out.println("Seleccione Un Producto a borrar!");
			} else if (movimientosEJBBean.validoBajaProducto(producto.getId()) != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN,
						"El Producto no se puede eliminar porque existe un registro de Perdida de este Producto en Movimientos. Elimínelo previamente de Movimientos para proceder" , null);
						System.out.println("El Producto no se puede eliminar porque existe un registro de Perdida de este Producto en Movimientos. Elimínelo previamente de Movimientos para proceder");
			} else if (renglonesPedidosEJBBean.getRenglonxPedido(producto.getId()) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"El Producto no se puede eliminar porque existen registros de este Producto en Renglones Pedido. Elimíne previamente los Renglones para proceder" , null);
					System.out.println("El Producto no se puede eliminar porque existen registros de este Producto en Renglones Pedido. Elimíne previamente los Renglones para proceder");
			} else {
				productosEJBBean.delete(producto.getId());
				productosList.remove(producto);

				message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Producto borrado exitosamente!" , null);
				System.out.println("Producto borrado exitosamente!");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al borrar el producto" , null);
			System.out.println("No se ejecuto correctamente productosEJBBean.delete");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
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
		try {
			return productosList = productosEJBBean.getAllProductos();
		}catch (Exception e) {
			return null;
		}

	}

	public void onRowEdit(RowEditEvent event) {
	    Producto p = (Producto) event.getObject();
	   
	    FacesMessage message;
	    
	   try {
			if (p.getNombre().isEmpty() || p.getLote().isEmpty() || p.getPrecio() == 0 || p.getFelab() == null || p.getFven() == null || p.getPeso() == 0 || p.getVolumen() == 0 || p.getEstiba() == 0 || p.getStkMin() == 0 || p.getStkTotal() == 0 || p.getSegmentac() == null || p.getUsuario() == null || p.getFamilia() == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN,"Es necesario ingresar todos los datos requeridos" , null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
				//FacesContext.getCurrentInstance().addMessage(null, message);
			} else if (p.getNombre().trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los datos ingresados superan el largo permitido. Por favor revise sus datos" , null);
				System.out.println("Los datos ingresados superan el largo permitido. Por favor revise sus datos");
				//FacesContext.getCurrentInstance().addMessage(null, message);
			} else if (p.getFelab().compareTo(p.getFven())>0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de fabricación no puede ser posterior a la de vencimiento" , null);
				System.out.println("La fecha de fabricación no puede ser posterior a la de vencimiento");
				//FacesContext.getCurrentInstance().addMessage(null, message);
			} else {
					
				//Traigo clases usuario y familia completas por el ID que se seleccionó en el desplegable
				Long usuId = p.getUsuario().getId();
				Long famId = p.getFamilia().getId();
				p.setFamilia(familiasEJBBean.getId(famId));
				p.setUsuario(usuariosEJBBean.getId(usuId));
				productosEJBBean.update(p);
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto modificado exitosamente!", null);
			    System.out.println("Modificacion de producto paso por row edit");
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. El producto no se pudo modificar" , null);
			System.out.println("No se ejecuto correctamente productosEJBBean.update");
			
		}
	   FacesContext.getCurrentInstance().addMessage(null, message);
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
				System.out.println("Se creo la lista para la segmentacion");
			}	
			idUsu = usuariosEJBBean.getId(id);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se creo la lista de segmentacion" , null));
			System.out.println("No se creo la lista de segmentacion");
			
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
