package com.bean;


import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.event.RowEditEvent;
import com.entities.Usuario;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
import com.services.PedidoBeanRemote;
import com.services.ProductoBeanRemote;
import com.services.UsuarioBeanRemote;


@ManagedBean(name = "usuario")
@ViewScoped
public class UsuariosBean {
	
	private Long id;
	private String nombre;
	private String apellido;
	private String nomAcceso;
	private String contrasena;
	private String correo;
	private tipoPerfil tipoPerfil;

	private List<SelectItem> availablePerfiles;

	private static tipoPerfil perfilLogeado;

	private String radioFindBy;

	private String filtroBusqueda;

	private List<Usuario> usuariosList;
	private List<SelectItem> perfiles;

	private Usuario selectedUsuario;
	
	private boolean confirmarBorrado = false;

	@SuppressWarnings("unused")
	private Usuario usu;

	
	@EJB
	private UsuarioBeanRemote usuariosEJBBean;
	
	@EJB
	private ProductoBeanRemote productosEJBBean;

	@EJB
	private PedidoBeanRemote pedidosEJBBean;

	public String add() {
		FacesMessage message;
		String retPage = "altaUsuarioPage";
		try {
			if (nombre.trim().isEmpty() || apellido.trim().isEmpty() || tipoPerfil == null || contrasena.trim().isEmpty() || nomAcceso.trim().isEmpty() || correo.trim().isEmpty()) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos", null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (nombre.trim().length() > 50 || apellido.trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los datos ingresados, superan el largo permitido (50).  Por favor revise sus datos" , null);
				System.out.println("Los datos ingresados, superan el largo permitido.  Por favor revise sus datos");
			} else if (contrasena.trim().length() < 8 || contrasena.trim().length() > 16) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "La contraseña debe tener por lo menos 8 dígitos y no superar los 16.  Por favor revise el dato ingresado" , null);
				System.out.println("La contraseña debe tener por lo menos 8 dígitos y no superar los 16.  Por favor revise el dato ingresado");
			} else if (nomAcceso.trim().length() > 30) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo nomAcceso no puede ser mayor a 30 caracteres" , null);
				System.out.println("Campo nomAcceso no puede ser mayor a 30 caracteres");
			} else if (correo.trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo no puede ser mayor a 50 caracteres" , null);
				System.out.println("Campo Correo no puede ser mayor a 50 caracteres");
			} else if (!correo.contains("@")) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo debe ser del formato : nombre @ dominio" , null);
				System.out.println("Campo Correo debe ser del formato : nombre @ dominio");
			} else if (getNomAcceso(nomAcceso.trim()) != null) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuario ya existente, por favor revise sus datos." , null);
					System.out.println("Usuario ya existente, por favor revise sus datos.");
			} else {
					Usuario u = new Usuario();
					u.setNombre(nombre.trim());
					u.setApellido(apellido.trim());
					u.setNomAcceso(nomAcceso.trim());
					u.setContrasena(DigestUtils.md5Hex(contrasena.trim()));
					u.setCorreo(correo.trim());
					u.setTipoPerfil(tipoPerfil);
					usuariosEJBBean.add(u);
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario ingresado exitosamente!" , null);
					System.out.println("Usuario ingresado exitosamente!"  + "\n" + nombre + "\n" + apellido + "\n" + nomAcceso + "\n" + correo + "\n" + tipoPerfil);
					FacesContext.getCurrentInstance().addMessage(null, message);
					return retPage;
				}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar usuario", null);
			System.out.println("No se ejecuto correctamente usuariosEJBBean.add");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}

	
	public String update(Long id, String nombre, String apellido, String nomAcceso, String contrasena, String correo, tipoPerfil tipoPerfil) {
		FacesMessage message;
		String retPage = "modificarUsuarioPage";
		try {
		
			if (nombre.trim().isEmpty() || apellido.trim().isEmpty() || tipoPerfil == null || contrasena.trim().isEmpty() || nomAcceso.trim().isEmpty() || correo.trim().isEmpty()) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos" , null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (nombre.trim().length() > 50 || apellido.trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los datos ingresados, superan el largo permitido.  Por favor revise sus datos" , null);
				System.out.println("Los datos ingresados, superan el largo permitido.  Por favor revise sus datos");
			} else if (nomAcceso.trim().length() > 30) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo nomAcceso no puede ser mayor a 30 caracteres" , null);
				System.out.println("Campo nomAcceso no puede ser mayor a 30 caracteres");
			} else if (correo.trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo no puede ser mayor a 50 caracteres" , null);
				System.out.println("Campo Correo no puede ser mayor a 50 caracteres");
			} else if (!correo.contains("@")) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo debe ser del formato : nombre @ dominio" , null);
				System.out.println("Campo Correo debe ser del formato : nombre @ dominio");
			} else if (getNomAcceso(nomAcceso.trim()) == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario no existe" , null);
				System.out.println("Usuario no existe" + nomAcceso);
			} else {
				Usuario u = new Usuario();
				u.setNombre(nombre.trim());
				u.setApellido(apellido.trim());
				u.setNomAcceso(nomAcceso.trim());
				u.setCorreo(correo.trim());
				u.setTipoPerfil(tipoPerfil);
				usuariosEJBBean.update(u);
				
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario modificado exitosamente!", null);
				System.out.println("Usuario modificado exitosamente!");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al modificar usuario." , null);
			System.out.println("No se ejecuto correctamente usuariosEJBBean.update");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		return null;
	}
	
	
	public String delete(Usuario usuario) throws ServiciosException {
		FacesMessage message;
		String retPage = "bajaUsuarioPage";
		try {
			if (usuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione un Usuario a borrar!" , null);
				System.out.println("Seleccione un Usuario a borrar!");
			} else if (productosEJBBean.getProductosxUsu(usuario.getId()) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede eliminar el usuario porque tiene productos asociados. Elimine primero los productos que tiene el usuario", null);
				System.out.println("No se puede eliminar el usuario porque tiene productos asociados. Elimine primero los productos que tiene asociado el usuario " + usuario.getNomAcceso());
			} else if (pedidosEJBBean.getPedidosxUsu(usuario.getId()) > 0) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede eliminar el usuario porque tiene Pedidos asociados. Elimine primero los Pedidos que tiene el usuario", null);
				System.out.println("No se puede eliminar el usuario porque tiene Pedidos asociados. Elimine primero los Pedidos que tiene asociado el usuario " + usuario.getNomAcceso());
			} else {
				usuariosEJBBean.delete(usuario.getId());
				usuariosList.remove(usuario);
				usuariosList = usuariosEJBBean.getAllUsuarios();
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario borrado exitosamente!", null);
				System.out.println("Usuario borrado exitosamente!");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al borrar el usuario" , null);
			System.out.println("No se ejecuto correctamente usuariosEJBBean.delete");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		usuariosList = usuariosEJBBean.getAllUsuarios();
		return retPage;
	}

	public Usuario get() {
		try {
			return usuariosEJBBean.getNA(nomAcceso);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Usuario getId(Long id) {
		try {
			return usuariosEJBBean.getId(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Usuario getNomAcceso(String nomAcceso) {
		try {
			return usuariosEJBBean.getNA(nomAcceso);
		} catch (Exception e) {
			return null;
		}
	}


	public List<Usuario> getAll() {
		try {
			return usuariosEJBBean.getAllUsuarios();
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Usuario> getAllUsuarios() throws ServiciosException{
		try {
			return usuariosList = usuariosEJBBean.getAllUsuarios();
		}catch (Exception e) {
			return null;
		}
		
	}

	private boolean ValidarContrasena(String nomAcceso, String contrasena) {
		try {
			return usuariosEJBBean.ValidarContrasena(nomAcceso, contrasena);
		} catch (Exception e) {
			return false;
		}
	}
	
	@PostConstruct
	public void perf() {
		try {
			ArrayList<SelectItem> perf = new ArrayList<>();
			perf.add(new SelectItem(com.enumerated.tipoPerfil.ADMINISTRADOR, com.enumerated.tipoPerfil.ADMINISTRADOR.toString()));
			perf.add(new SelectItem(com.enumerated.tipoPerfil.SUPERVISOR, com.enumerated.tipoPerfil.SUPERVISOR.toString()));
			perf.add(new SelectItem(com.enumerated.tipoPerfil.OPERARIO, com.enumerated.tipoPerfil.OPERARIO.toString()));
			perfiles =  perf;
			
			if(usuariosList == null) {
				usu = new Usuario();
			usuariosList = getAllUsuarios();
			System.out.println("Se creo la lista de tipos de perfil");
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se creo la lista de tipos de perfil" , null));
			System.out.println("No se creo la lista de tipos de perfil");
		}
	}

	
	
	public String onRowEdit(RowEditEvent event) throws ServiciosException {
	   Usuario u = (Usuario) event.getObject();
	   FacesMessage message;
	   String retPage = "modificarUsuarioPage";
		try {
			if (u.getNombre().trim().isEmpty() || u.getApellido().trim().isEmpty() || u.getTipoPerfil() == null || u.getContrasena().trim().isEmpty() || u.getNomAcceso().trim().isEmpty() || u.getCorreo().trim().isEmpty()) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos" , null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (u.getNombre().trim().length() > 50 || u.getApellido().trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los datos ingresados, superan el largo permitido.  Por favor revise sus datos" , null);
				System.out.println("Los datos ingresados, superan el largo permitido.  Por favor revise sus datos");
			} else if (u.getNomAcceso().trim().length() > 30) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo nomAcceso no puede ser mayor a 30 caracteres" , null);
				System.out.println("Campo nomAcceso no puede ser mayor a 30 caracteres");
			} else if (u.getCorreo().trim().length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo no puede ser mayor a 50 caracteres" , null);
				System.out.println("Campo Correo no puede ser mayor a 50 caracteres");
			} else if (!u.getCorreo().contains("@")) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo debe ser del formato : nombre @ dominio" , null);
				System.out.println("Campo Correo debe ser del formato : nombre @ dominio");
			} else if (getId(u.getId()) == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuario no existe" , null);
				System.out.println("Usuario no existe");
			} else if (getNomAcceso(u.getNomAcceso().trim()) != null && !(u.getNombre().equals(usuariosEJBBean.getId(u.getId()).getNomAcceso()))) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya existe un usuario con ese nombre" , null);
				System.out.println("Ya existe un usuario con ese nombre");
		   }else {
				usuariosEJBBean.update(u);
				usuariosList = usuariosEJBBean.getAllUsuarios();
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario modificado exitosamente!", null);
			    System.out.println("Modificacion de usuario pasa por row edit");
			    FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se pudo modificar el Usuario", null);
			System.out.println("No se pudo modificar el Usuario en row edit de UsuariosBean");
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
		usuariosList = usuariosEJBBean.getAllUsuarios();
		return retPage;
	}

	public void onRowDelete(RowEditEvent event) {
		Usuario u = (Usuario) event.getObject();

		FacesMessage message;

		try {
			this.delete(u);
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Contacte al administrador. Error al eliminar usuario.", null);
			System.out.println("No se ejecuto correctamente onRowDelete");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	/***********************************************************************************************************************************/

	public String login() {
		try {
			Usuario loginUser = usuariosEJBBean.getNA(nomAcceso);

			System.out.println(loginUser);
			System.out.println(DigestUtils.md5Hex(contrasena));
			System.out.println(ValidarContrasena(nomAcceso, contrasena));
			System.out.println("nomAcceso:  " + nomAcceso);
			
			if (nomAcceso != null && loginUser != null && contrasena != null && ValidarContrasena(nomAcceso, DigestUtils.md5Hex(contrasena))) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido: " + nomAcceso + "\n" + loginUser.getTipoPerfil().toString() , null));
				
				perfilLogeado = loginUser.getTipoPerfil();
				System.out.println("El usuario ingreso " + loginUser);
				return "Home";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales Inválidas " + "No existe un Usuario que coincida con los datos ingresados" , null));
				System.out.println("Error en Inicio de Sesión" + loginUser + "Credenciales Inválidas" + "No existe un Usuario que coincida con los datos ingresados");
				return "Login";
			}
		} catch (ServiciosException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error en Inicio de Sesión", null));
			System.out.println("Error en Inicio de Sesión. No funciono el login");
			
			return "Login";
		}
	}
	public String cancelLogin() {
		return "Login?faces-redirect=true";
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
	

	@SuppressWarnings("static-access")
	public boolean chequearOperario() {

			if (perfilLogeado == tipoPerfil.OPERARIO) {
				return true;
			} else {
				return false;
			}
	}
	
	@SuppressWarnings("static-access")
	public boolean chequearSupervisor() {

			if (perfilLogeado == tipoPerfil.SUPERVISOR) {
				return true;
			} else {
				return false;
			}
	}
	
	@SuppressWarnings("static-access")
	public boolean chequearAdministrador() {

			if (perfilLogeado == tipoPerfil.ADMINISTRADOR) {
				return true;
			} else {
				return false;
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

	public List<SelectItem> getPerfiles() {
		return perfiles;
	}


	public void setPerfiles(List<SelectItem> perfiles) {
		this.perfiles = perfiles;
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNomAcceso() {
		return nomAcceso;
	}

	public void setNomAcceso(String nomAcceso) {
		this.nomAcceso = nomAcceso;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public tipoPerfil getTipoPerfil() {
		return tipoPerfil;
	}

	public void setTipoPerfil(tipoPerfil tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}

	public List<SelectItem> getAvailablePerfiles() {
		return availablePerfiles;
	}

	public void setAvailablePerfiles(List<SelectItem> availablePerfiles) {
		this.availablePerfiles = availablePerfiles;
	}

	public static tipoPerfil getPerfilLogeado() {
		return perfilLogeado;
	}

	public static void setPerfilLogeado(tipoPerfil perfilLogeado) {
		UsuariosBean.perfilLogeado = perfilLogeado;
	}

	public String getRadioFindBy() {
		return radioFindBy;
	}

	public void setRadioFindBy(String radioFindBy) {
		this.radioFindBy = radioFindBy;
	}

	public String getFiltroBusqueda() {
		return filtroBusqueda;
	}

	public void setFiltroBusqueda(String filtroBusqueda) {
		this.filtroBusqueda = filtroBusqueda;
	}

	public List<Usuario> getUsuariosList() {
		return usuariosList;
	}

	public void setUsuariosList(List<Usuario> usuariosList) {
		this.usuariosList = usuariosList;
	}

	public Usuario getSelectedUsuario() {
		return selectedUsuario;
	}

	public void setSelectedUsuario(Usuario selectedUsuario) {
		this.selectedUsuario = selectedUsuario;
	}

	public boolean isConfirmarBorrado() {
		return confirmarBorrado;
	}

	public void setConfirmarBorrado(boolean confirmarBorrado) {
		this.confirmarBorrado = confirmarBorrado;
	}

	public UsuarioBeanRemote getUsuariosEJBBean() {
		return usuariosEJBBean;
	}

	public void setUsuariosEJBBean(UsuarioBeanRemote usuariosEJBBean) {
		this.usuariosEJBBean = usuariosEJBBean;
	}


	
}
