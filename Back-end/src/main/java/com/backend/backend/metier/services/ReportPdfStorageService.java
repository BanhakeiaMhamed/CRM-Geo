package com.backend.backend.metier.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ReportPdfStorageService {

    private final Path storageDir;

    public ReportPdfStorageService(@Value("${app.storage.reports-dir:uploads/reports}") String dir) throws IOException {
        this.storageDir = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(this.storageDir);
    }

    public String store(String reference, MultipartFile file) throws IOException {
        validatePdf(file);
        String safeRef = reference.replaceAll("[^a-zA-Z0-9_-]", "_");
        String storedName = safeRef + "_" + UUID.randomUUID() + ".pdf";
        Path target = storageDir.resolve(storedName).normalize();
        if (!target.startsWith(storageDir)) {
            throw new IllegalArgumentException("Chemin de stockage invalide.");
        }
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return storedName;
    }

    public Resource load(String storedName) throws IOException {
        if (storedName == null || storedName.isBlank()) {
            throw new IllegalArgumentException("Aucun PDF joint.");
        }
        Path path = storageDir.resolve(storedName).normalize();
        if (!path.startsWith(storageDir)) {
            throw new IllegalArgumentException("Chemin de stockage invalide.");
        }
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("Fichier PDF introuvable.");
        }
        return resource;
    }

    public void deleteIfExists(String storedName) throws IOException {
        if (storedName == null || storedName.isBlank()) {
            return;
        }
        Path path = storageDir.resolve(storedName).normalize();
        if (path.startsWith(storageDir)) {
            Files.deleteIfExists(path);
        }
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier PDF est obligatoire.");
        }
        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename();
        boolean pdfType = contentType != null && contentType.equalsIgnoreCase("application/pdf");
        boolean pdfExt = originalName != null && originalName.toLowerCase().endsWith(".pdf");
        if (!pdfType && !pdfExt) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont acceptes.");
        }
    }
}
