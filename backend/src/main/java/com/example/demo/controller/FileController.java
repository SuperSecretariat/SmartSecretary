package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {
    //to do: more error handling in these functions
    @GetMapping("/list/**")
    public List<Map<String, Object>> listFiles(
            HttpServletRequest request,
            @RequestParam(value = "subPath", defaultValue = "") String subPath) throws IOException {
        String path1 = request.getRequestURI().substring("/api/files/list/".length());//nu merge regex-ul "/list/{path:.+}" la uri asa ca am facut cum am putut
        Path allowedRoot = Paths.get(path1).toAbsolutePath().normalize();
        Path requestedPath = getSafePath(allowedRoot, subPath);

        return Files.list(requestedPath)
                .map(path -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("name", path.getFileName().toString());
                    entry.put("isDirectory", Files.isDirectory(path));
                    if (!Files.isDirectory(path)) {
                        try {
                            entry.put("size", Files.size(path));
                        } catch (IOException e) {
                            entry.put("size", 0);
                        }
                    }
                    return entry;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/upload/**")
    public ResponseEntity<?> uploadFile(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subPath", defaultValue = "") String subPath) throws IOException {
        String path = request.getRequestURI().substring("/api/files/upload/".length());
        Path allowedRoot = Paths.get(path).toAbsolutePath().normalize();
        Path targetDir = getSafePath(allowedRoot, subPath);

        if (!Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Target is not a directory");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path destination = targetDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/uploadDocument/**")
    public ResponseEntity<?> uploadDocument(HttpServletRequest request,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "subPath", defaultValue = "") String subPath) throws IOException {
        String path = request.getRequestURI().substring("/api/files/uploadDocument/".length());
        Path allowedRoot = Paths.get(path).toAbsolutePath().normalize();
        Path targetDir = getSafePath(allowedRoot, subPath);

        if (!Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Target is not a directory");
        }

        long maxFileSize = 10 * 1024 * 1024; //10mb
        if (file.getSize() > maxFileSize) {
            return ResponseEntity
                    .badRequest()
                    .body("File size exceeds the allowed limit of 10MB.");
        }


        String filename = Objects.requireNonNull(file.getOriginalFilename());
        if (!filename.toLowerCase().endsWith(".docx")) {
            return ResponseEntity.badRequest().body("Only .docx files are allowed.");
        }

        String dirName = filename.substring(0, filename.length() - 5);
        Path fileDir = targetDir.resolve(dirName);
        Files.createDirectories(fileDir);

        Path destination = fileDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/**")
    public ResponseEntity<Resource> downloadFile(
            HttpServletRequest request,
            @RequestParam(value = "subPath", defaultValue = "") String subPath,
            @RequestParam("file") String filename) throws IOException {
        String path = request.getRequestURI().substring("/api/files/download/".length());
        Path allowedRoot = Paths.get(path).toAbsolutePath().normalize();
        Path filePath = getSafePath(allowedRoot, subPath).resolve(filename).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/delete/**")
    public ResponseEntity<?> deleteFileOrDir(
            HttpServletRequest request,
            @RequestParam(value = "subPath", defaultValue = "") String subPath,
            @RequestParam("name") String name) throws IOException {
        String path = request.getRequestURI().substring("/api/files/delete/".length());
        Path allowedRoot = Paths.get(path).toAbsolutePath().normalize();
        Path target = getSafePath(allowedRoot, subPath).resolve(name).normalize();

        if (!target.startsWith(allowedRoot)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (Files.isDirectory(target)) {
            deleteDirectoryRecursively(target);
        } else {
            Files.deleteIfExists(target);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reload/")
    public ResponseEntity<?> reloadDocumentation() throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        ProcessBuilder venvBuilder;
        if (isWindows) {
            venvBuilder = new ProcessBuilder("python", "-m", "venv", "venv");
        } else {
            venvBuilder = new ProcessBuilder("python3", "-m", "venv", "venv");
        }
        venvBuilder.directory(new File("rag"));
        Process venvProcess = venvBuilder.start();
        venvProcess.waitFor();

        ProcessBuilder scriptBuilder;
        if (isWindows) {
            scriptBuilder = new ProcessBuilder("venv\\Scripts\\python.exe", "create_embeddings.py");
        } else {
            scriptBuilder = new ProcessBuilder("venv/bin/python", "create_embeddings.py");
        }
        scriptBuilder.directory(new File("rag"));
        scriptBuilder.inheritIO(); // Optional: to see output in console
        Process scriptProcess = scriptBuilder.start();
        scriptProcess.waitFor();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/mkdir/**")
    public ResponseEntity<?> createDirectory(
            HttpServletRequest request,
            @RequestParam(value = "subPath", defaultValue = "") String subPath,
            @RequestParam("name") String name) throws IOException {
        String path = request.getRequestURI().substring("/api/files/mkdir/".length());
        Path allowedRoot = Paths.get(path).toAbsolutePath().normalize();
        Path newDir = getSafePath(allowedRoot, subPath).resolve(name);
        Files.createDirectories(newDir);
        return ResponseEntity.ok().build();
    }

    private Path getSafePath(Path allowedRoot, String subPath) throws IOException {
        Path requested = allowedRoot.resolve(subPath).normalize();
        if (!requested.startsWith(allowedRoot)) {
            throw new SecurityException("Attempt to access files outside the allowed directory");
        }
        Files.createDirectories(requested); // if needed
        return requested;
    }

    private void deleteDirectoryRecursively(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        }
    }
    // The reloadDocumentation endpoint is unchanged, since it doesn't need to be per-baseDir.
}