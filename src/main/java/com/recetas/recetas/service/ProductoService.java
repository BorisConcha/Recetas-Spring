package com.recetas.recetas.service;

import com.recetas.recetas.model.Producto;
import com.recetas.recetas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }
    
    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByActivo(1);
    }
    
    public List<Producto> obtenerProductosRecientes() {
        return productoRepository.findProductosActivosRecientes();
    }
    
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }
    
    public Optional<Producto> obtenerProductoActivoPorId(Long id) {
        return productoRepository.findByIdAndActivo(id, 1);
    }
    
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivo(categoria, 1);
    }
    
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
    
    public void eliminarProducto(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            producto.get().setActivoBoolean(false);
            productoRepository.save(producto.get());
        }
    }
    
    public Producto actualizarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
}

