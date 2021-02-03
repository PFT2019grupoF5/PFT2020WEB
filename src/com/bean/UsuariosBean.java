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

import com.entities.Familia;
import com.entities.Usuario;
import com.enumerated.tipoPerfil;
import com.exception.ServiciosException;
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
	private boolean confirmarModificar = false;
	
	private Usuario usu;

	
	@EJB
	private UsuarioBeanRemote usuariosEJBBean;

	public String add() {
		FacesMessage message;
		String retPage = "altaUsuarioPage";
		try {
			if (nombre.isEmpty() || apellido.isEmpty() || tipoPerfil == null || contrasena.length() == 0 || nomAcceso.isEmpty() || correo.isEmpty()) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos", null);
				System.out.println("Es necesario ingresar todos los datos requeridos");
			} else if (nombre.length() > 50 || apellido.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los datos ingresados, superan el largo permitido.  Por favor revise sus datos" , null);
				System.out.println("Los datos ingresados, superan el largo permitido.  Por favor revise sus datos");
			} else if (contrasena.length() < 8 || contrasena.length() > 16) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "La contraseña debe tener por lo menos 8 dígitos y no superar los 16.  Por favor revise el dato ingresado" , null);
				System.out.println("La contraseña debe tener por lo menos 8 dígitos y no superar los 16.  Por favor revise el dato ingresado");
			} else if (nomAcceso.length() > 30) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo nomAcceso no puede ser mayor a 30 caracteres" , null);
				System.out.println("Campo nomAcceso no puede ser mayor a 30 caracteres");
			} else if (correo.length() > 50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo no puede ser mayor a 50 caracteres" , null);
				System.out.println("Campo Correo no puede ser mayor a 50 caracteres");
			} else if (!correo.contains("@")) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo debe ser del formato : nombre @ dominio" , null);
				System.out.println("Campo Correo debe ser del formato : nombre @ dominio");
			} else {
				if (getNomAcceso(nomAcceso) == null) {
					Usuario u = new Usuario();
					u.setNombre(nombre);
					u.setApellido(apellido);
					u.setNomAcceso(nomAcceso);
					u.setContrasena(DigestUtils.md5Hex(contrasena));
					u.setCorreo(correo);
					u.setTipoPerfil(tipoPerfil);
					usuariosEJBBean.add(u);
					
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario ingresado exitosamente!" , null);
					System.out.println("Usuario ingresado exitosamente!"  + "\n" + nombre + "\n" + apellido + "\n" + nomAcceso + "\n" + correo + "\n" + tipoPerfil);
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario ya existente, por favor revise sus datos." , null);
					System.out.println("Usuario ya existente, por favor revise sus datos.");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. Error al ejecutar agregar usuario", null);
			System.out.println("No se ejecuto correctamente usuariosEJBBean.add");
			
			
		}
		return retPage;
	}

	
	public String update(Long id, String nombre, String apellido, String nomAcceso, String contrasena, String correo, tipoPerfil tipoPerfil) {
		FacesMessage message;
		String retPage = "modificarUsuarioPage";
		try {
				if (nombre.isEmpty() || apellido.isEmpty() || tipoPerfil == null || contrasena.length() == 0 || nomAcceso.isEmpty() || correo.isEmpty()) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Es necesario ingresar todos los datos requeridos" , null);
					System.out.println("Es necesario ingresar todos los datos requeridos");
				} else if (nombre.length() > 50 || apellido.length() > 50) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Los datos ingresados, superan el largo permitido.  Por favor revise sus datos" , null);
					System.out.println("Los datos ingresados, superan el largo permitido.  Por favor revise sus datos");
				} else if (nomAcceso.length() > 30) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo nomAcceso no puede ser mayor a 30 caracteres" , null);
					System.out.println("Campo nomAcceso no puede ser mayor a 30 caracteres");
				} else if (correo.length() > 50) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo no puede ser mayor a 50 caracteres" , null);
					System.out.println("Campo Correo no puede ser mayor a 50 caracteres");
				} else if (!correo.contains("@")) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo Correo debe ser del formato : nombre @ dominio" , null);
					System.out.println("Campo Correo debe ser del formato : nombre @ dominio");
			} else {
				if (getNomAcceso(nomAcceso) != null) {
					Usuario u = new Usuario();
					u.setNombre(nombre);
					u.setApellido(apellido);
					u.setNomAcceso(nomAcceso);
					u.setCorreo(correo);
					u.setTipoPerfil(tipoPerfil);
					usuariosEJBBean.update(u);
					
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario modificado exitosamente!", null);
					System.out.println("Usuario modificado exitosamente!");
				} else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario no existe" , null);
					System.out.println("Usuario no existe");
				}
			}
				FacesContext.getCurrentInstance().addMessage(null, message);
				return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador. Error al modificar usuario." , null);
			System.out.println("No se ejecuto correctamente usuariosEJBBean.update");
			
			
		}
		return retPage;
	}
	
	
	public String delete(Usuario usuario) {
		FacesMessage message;
		String retPage = "bajaUsuarioPage";
		try {
			if (usuario == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Seleccione un Usuario a borrar!" , null);
				System.out.println("Seleccione un Usuario a borrar!");
			} else if (!confirmarBorrado) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Seleccione la casilla de confirmación!" , null);
				System.out.println("Seleccione la casilla de confirmación!");
			} else {
				usuariosEJBBean.delete(usuario.getId());
				usuariosList.remove(usuario);
				
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario borrado exitosamente!", null);
				System.out.println("Usuario borrado exitosamente!");
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador. Error al borrar el usuario" , null);
			System.out.println("No se ejecuto correctamente usuariosEJBBean.delete");
			
			
		}
		return retPage;
	}

	public Usuario get() {
		try {
			return usuariosEJBBean.getNA(nomAcceso);
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
			return usuariosList = usuariosEJBBean.getAllUsuarios();
		
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
			System.out.println("La lista de tipos de perfil se cargo correctamente!");
			
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contacte al administrador. No se creo la lista de tipos de perfil" , null));
			System.out.println("No se creo la lista de tipos de perfil");
			
		}
	}

	
	
	public void onRowEdit(RowEditEvent event) {
	    Usuario u = (Usuario) event.getObject();
	   
	    FacesMessage message;
	    
	   try {
			if (u.getNombre().isEmpty() || u.getNombre().length() > 50 || u.getApellido().isEmpty() || u.getApellido().length() > 50 || u.getNomAcceso().isEmpty() || u.getNomAcceso().length() > 50 || u.getCorreo().isEmpty() || u.getCorreo().length() > 50 || u.getTipoPerfil() == null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe ingresar todos los datos correctamente" , null);
				System.out.println("Debe ingresar todos los datos correctamente");
			} else {
					
				usuariosEJBBean.update(u);
			    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario modificado exitosamente!" , null);
			    System.out.println("Usuario modificado exitosamente!");
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contacte al administrador. Error al modificar usuario." , null);
			System.out.println("No se ejecuto correctamente usuariosEJBBean.update");
			
		}
	}

	

	/***********************************************************************************************************************************/

	public String login() {
		FacesMessage message = null;
		try {
			Usuario loginUser = usuariosEJBBean.getNA(nomAcceso);

			System.out.println(loginUser);
			System.out.println(ValidarContrasena(nomAcceso, contrasena));
			System.out.println("nomAcceso:  " + nomAcceso);
			
			if (nomAcceso != null && loginUser != null && contrasena != null
					&& ValidarContrasena(nomAcceso, DigestUtils.md5Hex(contrasena))) {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", nomAcceso);
				FacesContext.getCurrentInstance().addMessage(null, message);
				perfilLogeado = loginUser.getTipoPerfil();
				System.out.println("El usuario ingreso" + loginUser);
				return "Home";
			} else {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error en Inicio de Sesión",
						loginUser != null ? "Credenciales Inválidas"
								: "No existe un Usuario que con coincida con los datos ingresados");
				System.out.println("Error en Inicio de Sesión" + loginUser + "Credenciales Inválidas" + "No existe un Usuario que con coincida con los datos ingresados");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return "Login";
			}
		} catch (ServiciosException e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Inicio de Sesión", e.getMessage());
			System.out.println("Error en Inicio de Sesión");
			FacesContext.getCurrentInstance().addMessage(null, message);
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

	public String logout() {
		perfilLogeado = null;
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
