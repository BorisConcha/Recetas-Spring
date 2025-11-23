package com.recetas.recetas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class ArchivoService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    private static final String[] ALLOWED_VIDEO_TYPES = {"video/mp4", "video/webm", "video/quicktime"};
    

    public String guardarImagen(MultipartFile file) throws IOException {
        validarArchivo(file, ALLOWED_IMAGE_TYPES);
        
        String extension = obtenerExtension(file.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID().toString() + extension;
        Path rutaCompleta = Paths.get(uploadDir, "imagenes", nombreArchivo);
        
        Files.createDirectories(rutaCompleta.getParent());
        Files.copy(file.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
        
        return "/" + uploadDir + "/imagenes/" + nombreArchivo;
    }
    

    public String guardarVideo(MultipartFile file) throws IOException {
        validarArchivo(file, ALLOWED_VIDEO_TYPES);
        
        String extension = obtenerExtension(file.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID().toString() + extension;
        Path rutaCompleta = Paths.get(uploadDir, "videos", nombreArchivo);
        
        Files.createDirectories(rutaCompleta.getParent());
        Files.copy(file.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
        
        return "/" + uploadDir + "/videos/" + nombreArchivo;
    }
    

    private void validarArchivo(MultipartFile file, String[] tiposPermitidos) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("El archivo excede el tamaño máximo permitido (10MB)");
        }
        
        String tipoContenido = file.getContentType();
        boolean tipoPermitido = false;
        for (String tipo : tiposPermitidos) {
            if (tipo.equals(tipoContenido)) {
                tipoPermitido = true;
                break;
            }
        }
        
        if (!tipoPermitido) {
            throw new IOException("Tipo de archivo no permitido");
        }
    }
    

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }
}

