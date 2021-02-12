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

import com.entities.Almacenamiento;
import com.entities.Movimiento;
import com.entities.Producto;
import com.enumerated.tipoMovimiento;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.ProductoBeanRemote;
import com.services.AlmacenamientoBeanRemote;
import com.services.MovimientoBeanRemote;

@ManagedBean(name = "movimiento")
@ViewScoped

public class MovimientosBean {

	
	private Long id;
	private Date fecha;
	private int cantidad;
	private String descripcion;
	private double costo;
	private tipoMovimiento tipoMov;
	private Producto producto;
	private Almacenamiento almacenamiento;

	private static tipoPerfil perfilLogeado;

	private Movimiento selectedMovimiento;
	private List<SelectItem> tiposDeMov;

	private Long idProducto;
	private Long idAlmacenamiento;

	private boolean confirmarBorrado = false;
	private boolean confirmarModificar = false;
	
	private Movimiento mov;
	private List<Movimiento> movimientosList;

	@EJB
	private MovimientoBeanRemote movimientosEJBBean;

	@EJB
	private ProductoBeanRemote productosEJBBean;

	@EJB
	private AlmacenamientoBeanRemote almacenamientosEJBBean;

	public String add() {
		String retPage = "altaMovimientoPage";
		FacesMessage message = null;
		
		try {
			if (fecha == null || cantidad <= 0 || costo <= 0 || tipoMov == null || idProducto == 0 || idAlmacenamiento == 0 || descripcion.trim().isEmpty()) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos", null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (descripcion.trim().length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Descripcion no puede ser mayor a 250 caracteres", null);
				System.out.println("Campo Descripcion no puede ser mayor a 250 caracteres");
			} else {
					
					Movimiento m = new Movimiento();
					Producto productoEnBD = productosEJBBean.getProducto(idProducto);
					Almacenamiento almacenamientoEnBD = almacenamientosEJBBean.getAlmacenamiento(idAlmacenamiento);
					
					m.setFecha(fecha);
					m.setCantidad(cantidad);
					m.setDescripcion(descripcion.trim());
					m.setCosto(costo);
					m.setTipoMov(tipoMov);
					m.setProducto(productoEnBD);
					m.setAlmacenamiento(almacenamientoEnBD);
					System.out.println("Prepara datos para la comprobacion");

					if (tipoMov.toString().equals("P")) {

						double stockTotalDelProducto = productoEnBD.getStkTotal();
						System.out.println("Se fija un valor para comprobar cantidades");
						
						if (stockTotalDelProducto < cantidad) {
							message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock insuficiente de Producto para registrar la Pérdida, por favor revise sus datos.", null);
							System.out.println("Stock insuficiente de Producto para registrar la Pérdida, por favor revise sus datos.");
						} else {
							productoEnBD.setStkTotal(stockTotalDelProducto - cantidad);
							System.out.println("Descuenta stock del producto");
							if (productoEnBD.getStkTotal() <= productoEnBD.getStkMin()) {
								message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock por debajo del mínimo requerido para este producto, por favor gestione un Pedido de reposición.", null);
								System.out.println("Stock por debajo del mínimo requerido para este producto, por favor gestione un Pedido de reposición");
							}else {
				                // disponibiliza espacio en el almacenamiento correspondiente a la perdida ocasionada	
								almacenamientoEnBD.setVolumen( (int) (almacenamientoEnBD.getVolumen() + cantidad*productoEnBD.getVolumen()) );
								productosEJBBean.update(productoEnBD);
								almacenamientosEJBBean.update(almacenamientoEnBD);
								
								movimientosEJBBean.add(m);
								message = new FacesMessage(FacesMessage.SEVERITY_INFO, "El Movimiento se creo correctamente" , null);
								System.out.println("El Movimiento se creo correctamente" + "\n" + fecha + "\n" + cantidad + "\n" + descripcion + "\n" + costo + "\n" + tipoMov + "\n" + idProducto + "\n" + idAlmacenamiento);
								FacesContext.getCurrentInstance().addMessage(null, message);
								return retPage;
							}
						}
						}else if(!tipoMov.toString().equals("P")){
							movimientosEJBBean.add(m);
							message = new FacesMessage(FacesMessage.SEVERITY_INFO, "El tipo de movimiento se registro", null);
							System.out.println("El Movimiento se creo correctamente" + "\n" + fecha + "\n" + cantidad + "\n" + descripcion + "\n" + costo + "\n" + tipoMov + "\n" + idProducto + "\n" + idAlmacenamiento);
							FacesContext.getCurrentInstance().addMessage(null, message);
							return retPage;
						
					}
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar movimiento", null);
			System.out.println("No se ejecuto correctamente movimientosEJBBean.add");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String update(Long id, Date fecha, int cantidad, String descripcion, double costo, tipoMovimiento tipoMov,
			Producto producto, Almacenamiento almacenamiento) {
		FacesMessage message = null;
		String retPage = "modificarMovimientoPage";
		try {

			if (fecha == null || cantidad <= 0 || costo <= 0 || tipoMov == null || producto == null
					|| almacenamiento == null || descripcion.trim().isEmpty()) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos", null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (descripcion.trim().length() > 250) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Descripcion no puede ser mayor a 250 caracteres", null);
				System.out.println("Campo Descripcion no puede ser mayor a 250 caracteres");
			} else if (tipoMov.toString().equals("P")) {
				// Si es PERDIDA no se permite MODIFICACION
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "No es posible modificar un movimiento tipo Perdida", null);
				System.out.println("No es posible modificar un movimiento tipo Perdida");
			} else {
				Movimiento m = new Movimiento();
				m.setId(id);
				m.setFecha(fecha);
				m.setCantidad(cantidad);
				m.setDescripcion(descripcion.trim());
				m.setCosto(costo);
				m.setTipoMov(tipoMov);
				m.setProducto(productosEJBBean.getProducto(idProducto));
				m.setAlmacenamiento(almacenamientosEJBBean.getAlmacenamiento(idAlmacenamiento));
				movimientosEJBBean.update(m);
				
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Movimiento Modificado exitosamente!", null);
				System.out.println("Movimiento Modificado exitosamente!");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Asegurese que no es de tipo Perdida", null);
			System.out.println("No se ejecuto correctamente movimientosEJBBean.update");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	public String delete(Movimiento movimiento) {
		FacesMessage message = null ;
		String retPage = "bajaMovimientoPage";
		try {
			if (movimiento == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Seleccione Un Movimiento a borrar!", null);
				System.out.println("Seleccione Un Movimiento a borrar!");
				
			} else {
					// if (tipoMov.toString().equals("P")) {
						Producto productoEnBD = productosEJBBean.getProducto(movimiento.getProducto().getId());
						Almacenamiento almacenamientoEnBD = almacenamientosEJBBean.getAlmacenamiento(movimiento.getAlmacenamiento().getId());
			
							// Significa que ELIMINO un registro de una PERDIDA previamente ingresada de un producto en un almacenamiento
							double stockTotalDelProducto = productoEnBD.getStkTotal();
							
	                        //repone stock del producto
							productoEnBD.setStkTotal(stockTotalDelProducto + cantidad);
	                        // vuelve a ocupar espacio en el almacenamiento
							almacenamientoEnBD.setVolumen( (int) (almacenamientoEnBD.getVolumen() - cantidad*productoEnBD.getVolumen()) );
							// actualiza BD
							productosEJBBean.update(productoEnBD);
							almacenamientosEJBBean.update(almacenamientoEnBD);
							
								movimientosEJBBean.delete(movimiento.getId());
								movimientosList.remove(movimiento); 
								message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Movimiento borrado exitosamente!", null);
								System.out.println("Movimiento borrado exitosamente!");
								FacesContext.getCurrentInstance().addMessage(null, message);
								return retPage;
//					
//					} else {
//						movimientosEJBBean.delete(movimiento.getId());
//						movimientosList.remove(movimiento); 
//						message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Movimiento borrado exitosamente!", null);
//						System.out.println("Movimiento borrado exitosamente!");
//						FacesContext.getCurrentInstance().addMessage(null, message);
//						return retPage;
//					}
			}
			
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo borrar", null);
			System.out.println("No se elimino el movimiento");
			
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	
	public Movimiento get() {
		try {
			return movimientosEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}

	public int getMovimientoxAlmac(long idAlma) {
		try {
			return movimientosEJBBean.getMovimientoxAlmac(idAlma);
		} catch (Exception e) {
			return 0;
		}
	}

	
	public List<Movimiento> getAll() {
		try {
			return movimientosEJBBean.getAllMovimientos();
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Movimiento> obtenerTodosMovimientos() throws ServiciosException {
		try {
			return movimientosList = movimientosEJBBean.getAllMovimientos();
		}catch (Exception e) {
			return null;
		}
	}
	
	public void onRowEdit(RowEditEvent event) {
	    Movimiento m = (Movimiento) event.getObject();
	    
	    FacesMessage message;
	    
	   try {
			if (m.getFecha() == null || m.getCantidad() == 0 || m.getDescripcion().trim().isEmpty() || m.getCosto() == 0 || m.getTipoMov() == null || m.getProducto() == null || m.getAlmacenamiento() == null || m.getDescripcion().trim().isEmpty()) {
				 message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos", null);
				 System.out.println("Es necesario ingresar todos los datos requeridos row edit");
				 //FacesContext.getCurrentInstance().addMessage(null,  message);
			} else {
					
				//Traigo clases usuario y familia completas por el ID que se seleccionó en el desplegable
				Long prodId = m.getProducto().getId();
				Long almaId = m.getAlmacenamiento().getId();
				
				m.setProducto(productosEJBBean.getId(prodId));
				m.setAlmacenamiento(almacenamientosEJBBean.getId(almaId));
				movimientosEJBBean.update(m);
				System.out.println("Modificacion de movimiento pasa por rowedit");
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Movimiento Modificado exitosamente!", null);
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador.No se pudo modificar el movimiento ", null);
			System.out.println("No se pudo modificar el movimiento en row edit");
			
		}
	   FacesContext.getCurrentInstance().addMessage(null, message);
	}


	@PostConstruct
	public void tiM() {
		try {
			ArrayList<SelectItem> tiM = new ArrayList<>();
			tiM.add(new SelectItem(tipoMovimiento.M, tipoMovimiento.M.toString()));
			tiM.add(new SelectItem(tipoMovimiento.C, tipoMovimiento.C.toString()));
			tiM.add(new SelectItem(tipoMovimiento.P, tipoMovimiento.P.toString()));
			tiposDeMov = tiM;
			
			if (movimientosList==null) {
				mov = new Movimiento();
				movimientosList = obtenerTodosMovimientos();
				System.out.println("Se crea la lista de tipos de movimiento");
			}
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No se pudo crear la lista de tipos de movimiento", null);
			System.out.println("No se pudo crear la lista de tipos de movimiento");
			FacesContext.getCurrentInstance().addMessage(null,  message);
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

	public List<SelectItem> getTiposDeMov() {
		return tiposDeMov;
	}

	public void setTiposDeMov(List<SelectItem> tiposDeMov) {
		this.tiposDeMov = tiposDeMov;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public tipoMovimiento getTipoMov() {
		return tipoMov;
	}

	public void setTipoMov(tipoMovimiento tipoMov) {
		this.tipoMov = tipoMov;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Almacenamiento getAlmacenamiento() {
		return almacenamiento;
	}

	public void setAlmacenamiento(Almacenamiento almacenamiento) {
		this.almacenamiento = almacenamiento;
	}

	public Long getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}

	public Long getIdAlmacenamiento() {
		return idAlmacenamiento;
	}

	public void setIdAlmacenamiento(Long idAlmacenamiento) {
		this.idAlmacenamiento = idAlmacenamiento;
	}

	public List<Movimiento> getMovimientosList() {
		return movimientosList;
	}

	public void setMovimientosList(List<Movimiento> movimientosList) {
		this.movimientosList = movimientosList;
	}
	
	

}
