package com.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
	
	//Comentario para Luis
		
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
		private List<Familia> listaFamilia;
		private List<Usuario> listaUsuario;
		private Usuario idUsu;
		
		//edit
		private Producto produc;
		private List<Producto> productos;

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
				if (nombre.length() > 50) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
							"Los datos ingresados superan el largo permitido. Por favor revise sus datos");
				} else if (felab.compareTo(fven)>0) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
							"La fecha de fabricación no puede ser posterior a la de vencimiento");
				} else {
					if (get() == null) {
						productosEJBBean.add(nombre, lote, precio, felab, fven, peso, volumen, estiba, stkMin, stkTotal, segmentac,usuariosEJBBean.getUsuario(idUsuario) , familiasEJBBean.getFamilia(idFamilia));
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
		
		public String update(Long id, String nombre, String lote, double precio, Date felab, Date fven, double peso, double volumen, int estiba, double stkMin, double stkTotal, Segmentacion segmentac, Usuario usuario, Familia familia) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Modificar: ",
					"Producto modificado exitosamente!");
			String retPage = "modificarProductoPage";
			try {
				if (nombre.isEmpty() || lote.isEmpty() || precio == 0 || felab == null || fven == null || peso == 0 || volumen == 0 || estiba == 0 || stkMin == 0 || stkTotal == 0 || segmentac == null || usuario == null || familia == null) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
							"Es necesario ingresar todos los datos requeridos");
				} else if (nombre.length() > 50) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
							"Los datos ingresados superan el largo permitido. Por favor revise sus datos");
				} else if (felab.compareTo(fven)>0) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ",
							"La fecha de fabricación no puede ser posterior a la de vencimiento");
				} else if (!confirmarModificar) {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Modificar: ",
							"Seleccione la casilla de confirmación!");
				} else {
					if (get() != null) {
						productosEJBBean.update(id, nombre, lote, precio, felab, fven, peso, volumen, estiba, stkMin, stkTotal, segmentac, usuario, familia);
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
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ",
					"Producto borrado exitosamente!");
			String retPage = "bajaProductoPage";
			try {
				if (!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado)||!tipoPerfil.SUPERVISOR.equals(perfilLogeado)) {
					message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ",
							"No tiene permisos suficientes para realizar esta acción");
				} else if (selectedProducto == null) {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
							"Seleccione Un Producto a borrar!");
				} else if (productosEJBBean.validoBajaProductos(producto)) {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
							"El Producto no se puede eliminar porque existe en Movimientos. Elimínelo previamente de Movimientos para proceder");		
				} else if (!confirmarBorrado) {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ",
							"Seleccione la casilla de confirmación!");
				} else {
					productosEJBBean.delete(selectedProducto.getId());
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

		public LinkedList<Producto> getAll() {
			try {
				return productosEJBBean.getAll();
			} catch (Exception e) {
				return null;
			}
		}
		
		public List<Producto> obtenerTodosProductos() throws ServiciosException {
			return productos = productosEJBBean.obtenerProductos();

		}
		
		public void modificarProducto() throws Exception {

			try {
				produc.setNombre(nombre);
				produc.setLote(lote);
				produc.setFelab(felab);
				produc.setFven(fven);
				produc.setPrecio(precio);
				produc.setPeso(peso);
				produc.setVolumen(volumen);
				produc.setEstiba(estiba);
				produc.setStkMin(stkMin);
				produc.setStkTotal(stkTotal);
				produc.setSegmentac(segmentac);
				productosEJBBean.modificarProducto(produc);

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("éxito"));

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		
		// rowEdit
		public void onRowEdit(RowEditEvent event) throws ServiciosException {
			Producto p = (Producto) event.getObject();

			System.out.println("--");
			System.out.println("--");
			System.out.println("--");

			System.out.println(p.getUsuario().getNombre());

			System.out.println("--");
			System.out.println("--");
			System.out.println("--");

			produc.setId(p.getId());
			produc.setNombre(p.getNombre());
			produc.setLote(p.getLote());
			produc.setFelab(p.getFelab());
			produc.setFven(p.getFven());
			produc.setPrecio(p.getPrecio());
			produc.setPeso(p.getPeso());
			produc.setVolumen(p.getVolumen());
			produc.setEstiba(p.getEstiba());
			produc.setStkMin(p.getStkMin());
			produc.setStkTotal(p.getStkTotal());
			produc.setSegmentac(p.getSegmentac());
			produc.setUsuario(p.getUsuario());
			produc.setFamilia(p.getFamilia());

			try {
				productosEJBBean.modificarProducto(produc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			FacesMessage msg = new FacesMessage("Producto editado", String.valueOf(p.getId()));
			FacesContext.getCurrentInstance().addMessage(null, msg);

		}

		@PostConstruct
		public void segm() {
			try {
				ArrayList<SelectItem> segm = new ArrayList<>();
				segm.add(new SelectItem(Segmentacion.S, Segmentacion.S.toString()));
				segm.add(new SelectItem(Segmentacion.N, Segmentacion.N.toString()));
				listaUsuario = usuariosEJBBean.getAllUsuarios();
				listaFamilia = familiasEJBBean.getAllFamilias();
				//rowEdit
				produc = new Producto();
				productos = obtenerTodosProductos();
				idUsu = usuariosEJBBean.getId(id);
				segmentaciones =  segm;
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

		public List<Familia> getListaFamilia() {
			return listaFamilia;
		}

		public void setListaFamilia(List<Familia> listaFamilia) {
			this.listaFamilia = listaFamilia;
		}

		public List<Usuario> getListaUsuario() {
			return listaUsuario;
		}

		public void setListaUsuario(List<Usuario> listaUsuario) {
			this.listaUsuario = listaUsuario;
		}

		public Producto getProduc() {
			return produc;
		}

		public void setProduc(Producto produc) {
			this.produc = produc;
		}

		public List<Producto> getProductos() {
			return productos;
		}

		public void setProductos(List<Producto> productos) {
			this.productos = productos;
		}
		
		
		
}
