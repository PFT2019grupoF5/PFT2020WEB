package com.bean;

import java.util.ArrayList;
import java.util.LinkedList;
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
import com.services.UsuarioBeanRemote;

@SuppressWarnings("deprecation")
@ManagedBean(name="usuario")	
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
	
	private Usuario selectedUsuario;
	
	private boolean confirmarBorrado=false;
	
	@EJB
	private UsuarioBeanRemote usuariosEJBBean;
	
	public String add(){
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Registrar: ", "Usuario ingresado exitosamente!");
		String retPage = "altaUsuarioPage";
		try {
			if(!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ", "Debe ser un Usuario ADMINISTRADOR para poder acceder");
			}else if(nombre.isEmpty() || nombre.length()>50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ", "Campo Nombre no puede ser vacío o mayor a 50 caracteres");
			}else if(apellido.isEmpty() || apellido.length()>50) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ", "Campo Apellido no puede ser vacío o mayor a 50 caracteres");
			}else if(tipoPerfil==null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ", "Debe Seleccionar un tipo de Perfil");
			}else if(contrasena.length()==0){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ", "Campo Contraseña no puede ser vacío");
			}else if(contrasena.length()<8 || contrasena.length()>16){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ", "Campo Contraseña debe tener un largo entre 8 y 16 caracteres");
			}else if(nomAcceso.isEmpty() || nomAcceso.length()>50){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ", "Campo nomAcceso no puede ser vacío o mayor a 50 caracteres");
			}else if(!correo.contains("@")){
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Registrar: ", "Campo Correo debe ser del formato : nombre @ dominio");
			}else {
				if(get()==null) {
					usuariosEJBBean.add(nombre, apellido, nomAcceso, DigestUtils.md5Hex(contrasena), correo, tipoPerfil );
				}else {
					message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Registrar: ", "El nombre de usuario provisto ya existe");
				}
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		}catch (Exception e) {
			return null;
		}
	}
	
	public String delete(){
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito al Borrar: ", "Usuario borrado exitosamente!");
		String retPage = "bajaUsuarioPage";
		try {
			if(!tipoPerfil.ADMINISTRADOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ", "Debe ser un Usuario ADMINISTRADOR para poder acceder");
			}else if(selectedUsuario==null){
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ", "Seleccione un Usuario a borrar!");
			}else if(!confirmarBorrado){
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al Borrar: ", "Seleccione la casilla de confirmación!");
			}else if(usuariosEJBBean.hasGuacheras(selectedUsuario.getUsuario())!=0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Borrar: ", "El usuario seleccionado, administra Guacheras. "
						+ "Desvincular guachera/s antes de borrar");
			}else {
				usuariosEJBBean.delete(selectedUsuario.getUsuario());
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
			return retPage;
		}catch (Exception e) {
			return null;
		}		
	}
	
	public Usuario get(){
		try {
			return usuariosEJBBean.get(usuario);
		}catch (Exception e) {
			return null;
		}		
	}
	
	public LinkedList<Usuario> getAll(){
		try {
			return usuariosEJBBean.getAll();
		}catch (Exception e) {
			return null;
		}	
	}
	
	private boolean checkPwd(String userTyped, String typedPwd){
		try {
			return usuariosEJBBean.checkPwd(userTyped, typedPwd);
		}catch (Exception e) {
			return false;
		}
	}
	
	public Object[][] getAllByApellidoOrUsuario(){
		try {
			return usuariosEJBBean.getAllByApellidoOrUsuario(apellido, usuario);
		}catch (Exception e) {
			return null;
		}
	}
	
	public int hasGuacheras() throws serviciosException{
		try {
			return usuariosEJBBean.hasGuacheras(usuario);
		} catch (Exception e) {
			throw new serviciosException(e.getMessage());
		}
	}
	
	/***********************************************************************************************************************************/
	
	public String login() {
        FacesMessage message = null;
        try {
        	Usuario loginUser = usuariosEJBBean.get(usuario);
            
            if(usuario!=null && loginUser!=null && password !=null && checkPwd(usuario, DigestUtils.md5Hex(password))) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", usuario);
                FacesContext.getCurrentInstance().addMessage(null, message);
                perfilLogeado = loginUser.getPerfil();
                return "mainPage?faces-redirect=true";
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error en Inicio de Sesión", 
                		loginUser!=null ? "Credenciales Inválidas" : "No existe un Usuario que con coincida con los datos ingresados");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return "index";
            }
		} catch (serviciosException e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Inicio de Sesión", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "index";
		}
 
    } 
	
	public String cancelLogin() {
		return "index?faces-redirect=true";
	}
	
	/**
	 * Metodo para carga cuadro en la tabla luego de realizar la busqueda
	 */
	public void loadTable() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Exito al Buscar: ",
				"Registros Recuperados!");

		try {
			if (filtroBusqueda.isEmpty()) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Buscar: ",
						"Debe ingresar un Nombre de Usuario o Apellido");
			} else if (radioFindBy==null) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Buscar: ",
						"Debe elegir un tipo de buqueda: Nombre de Usuario o Apellido");
			} else {
				if (radioFindBy.equals("username")) {
					usuariosList = usuariosEJBBean.getAllByApellidoOrUsuarioAsList("", filtroBusqueda);
				}else{
					usuariosList = usuariosEJBBean.getAllByApellidoOrUsuarioAsList(filtroBusqueda,"");
				}
				
				if (usuariosList.size()==0) {
					message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Actualizar: ","No existen registros para el usuario deseado");
				}
			}
		} catch (serviciosException e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Actualizar: ",e.getMessage());
		} finally {
			FacesContext.getCurrentInstance().addMessage(null, message);	
		}
	}
		

	
	/**
	 * Metodo para armar los combos
	 */
	@PostConstruct
	public void init() {
		FacesMessage message = null;
		try {
			ArrayList<SelectItem> perfiles = new ArrayList<>();
			perfiles.add(new SelectItem(PerfilUsuario.ENCARGADO, PerfilUsuario.ENCARGADO.toString()));
			perfiles.add(new SelectItem(PerfilUsuario.PERSONAL, PerfilUsuario.PERSONAL.toString()));
			availablePerfiles =  perfiles;
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en SetUp: ", e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	/**
	 * Metodo para editar una fila de una tabla
	 * @param event
	 */
	public void onRowEdit(RowEditEvent event) {
		FacesMessage message = null;

		try {
			if(!PerfilUsuario.ADMINISTRADOR.equals(perfilLogeado)) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falta de Permisos: ", "Debe ser un Usuario ADMINISTRADOR para poder acceder");
			}else if (((Usuario) event.getObject()).getPassword().length()==0) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Actualizar: ",
						"Campo Contraseña no puede ser vacío");
			} else if (((Usuario) event.getObject()).getPassword().length()<8 || ((Usuario) event.getObject()).getPassword().length()>16) {
				message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al Actualizar: ",
						"Campo debe tener un largo entre 8 y 16 caracteres");
			} else {
				usuariosEJBBean.update(((Usuario) event.getObject()).getUsuario(), DigestUtils.md5Hex(((Usuario) event.getObject()).getPassword()));
				message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito al Actualizar: ",
						"Usuario Actualizado");
			}
		} catch (Exception e) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al Actualizar: ", e.getMessage());
		} finally {
			FacesContext.getCurrentInstance().addMessage(null, message);	
		}
	}

	/**
	 * Metodo para cancelar la edicion de una fila de una tabla
	 * @param event
	 */
	public void onRowCancel(RowEditEvent event) {
		FacesMessage message = new FacesMessage("Edición Cancelada",
				Long.toString(((Usuario) event.getObject()).getIdUsuario()));
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public String checkRoles(){
		try {
			if(perfilLogeado==null) {
		       return "index?faces-redirect=true";
			}else {
				return null;
			}
	    } catch (Exception e) {
	        return "index?faces-redirect=true";
	    }
	}
	
	public String logout() {
		perfilLogeado = null;
		return "index?faces-redirect=true";
	}
	
	/***********************************************************************************************************************************/
	
	//Getters and Setters
	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PerfilUsuario getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilUsuario perfil) {
		this.perfil = perfil;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public List<SelectItem> getAvailablePerfiles() {
		return availablePerfiles;
	}

	public void setAvailablePerfiles(List<SelectItem> availablePerfiles) {
		this.availablePerfiles = availablePerfiles;
	}

	public static PerfilUsuario getPerfilLogeado() {
		return UsuariosBean.perfilLogeado;
	}

	public void setPerfilLogeado(PerfilUsuario perfilLogeado) {
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

	public void setUsuariosList(List<Usuario>usuariosList) {
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
	
	
}
