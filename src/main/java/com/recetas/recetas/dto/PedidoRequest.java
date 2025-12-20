package com.recetas.recetas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class PedidoRequest {
    
    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<DetallePedidoRequest> detalles;
    
    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String direccionEntrega;
    
    @NotBlank(message = "El teléfono de contacto es obligatorio")
    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String telefonoContacto;
    
    public PedidoRequest() {
    }
    
    public List<DetallePedidoRequest> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetallePedidoRequest> detalles) {
        this.detalles = detalles;
    }
    
    public String getDireccionEntrega() {
        return direccionEntrega;
    }
    
    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }
    
    public String getTelefonoContacto() {
        return telefonoContacto;
    }
    
    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }
    
    public static class DetallePedidoRequest {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;
        
        public DetallePedidoRequest() {
        }
        
        public Long getProductoId() {
            return productoId;
        }
        
        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }
        
        public Integer getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}

