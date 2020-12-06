package com.ws;

import java.util.Date;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.services.PedidoBeanRemote;
import com.services.UsuarioBeanRemote;
import com.entities.Pedido;
import com.enumerated.estadoPedido;
import com.exception.ServiciosException;

@Stateless
public class PedidosRestBean implements PedidosRest {

	@EJB
	private PedidoBeanRemote pedidoEJBBean;
	@EJB
	private UsuarioBeanRemote usuarioEJBBean;
	
	@Override
    public void add(Date pedfecestim, Date fecha, int pedreccodigo, Date pedrecfecha, String pedreccomentario, estadoPedido pedestado, Long idUsuario) throws ServiciosException {
        try{
            pedidoEJBBean.add(pedfecestim, fecha, pedreccodigo, pedrecfecha, pedreccomentario, pedestado, usuarioEJBBean.getId(idUsuario));
        }catch(Exception e){
            throw new ServiciosException("No se pudo agregar pedido" + e.getMessage());
        }
    }

	
	@Override
    public void update(Long id, Date pedfecestim, Date fecha, int pedreccodigo, Date pedrecfecha, String pedreccomentario, estadoPedido pedestado, Long idUsuario) throws ServiciosException {
        try{
            pedidoEJBBean.update(id, pedfecestim, fecha, pedreccodigo, pedrecfecha, pedreccomentario, pedestado, usuarioEJBBean.getId(idUsuario));
        }catch(Exception e){
            throw new ServiciosException("No se pudo modificar pedido" + e.getMessage());
        }
    }

	
	@Override
    public void delete(Long id) throws ServiciosException {
		try{
			pedidoEJBBean.delete(id);;
		}catch(Exception e){
			throw new ServiciosException("No se pudo borrar con id " + id.toString() + e.getMessage());
		}
    }
	

	@Override
    public LinkedList<Pedido> getAll() throws ServiciosException {
		try{
			LinkedList<Pedido> listaPedidos = pedidoEJBBean.getAll(); 
			return listaPedidos;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener lista de pedidos");
		}
    }

	
	@Override
    public Pedido get(Long id) throws ServiciosException {
		try{
			Pedido pedido = pedidoEJBBean.getId(id);
			return pedido;
		}catch(Exception e){
			throw new ServiciosException("No se pudo obtener pedido con id " + id.toString() + e.getMessage());
		}
    }

}

