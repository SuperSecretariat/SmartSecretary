package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/llmFiles")
public class LLMFileController {

    private final Path rootPath;

    public LLMFileController(@Value("${file.storage.root:files}") String rootDir) throws IOException {
        this.rootPath = Paths.get(rootDir).toAbsolutePath().normalize();
        Files.createDirectories(rootPath);
    }

    // List the contents of a directory
    @GetMapping
    public List<Map<String, Object>> listFiles(
            @RequestParam(value = "dir", defaultValue = "") String dir) throws IOException {

        Path targetDir = getSafePath(dir);

        if (!Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Not a directory");
        }

        return Files.list(targetDir)
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

    // Upload a file to a directory
    @PostMapping
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "") String dir) throws IOException {

        Path targetDir = getSafePath(dir);

        if (!Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Target is not a directory");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path destination = targetDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reload")
    public ResponseEntity<?> reloadDocumentation() throws IOException, InterruptedException {
        ProcessBuilder venvBuilder = new ProcessBuilder("python3", "-m", "venv", "venv");
        venvBuilder.directory(new File("rag"));
        Process venvProcess = venvBuilder.start();
        venvProcess.waitFor();

        ProcessBuilder scriptBuilder = new ProcessBuilder("venv/bin/python", "create_embeddings.py");
        scriptBuilder.directory(new File("rag"));
        scriptBuilder.inheritIO(); // Optional: to see output in console
        Process scriptProcess = scriptBuilder.start();
        scriptProcess.waitFor();

        return ResponseEntity.ok().build();
    }

    // Download a file
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("dir") String dir,
            @RequestParam("file") String filename) throws MalformedURLException {
        Path filePath = getSafePath(dir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // Delete a file or folder (recursive for folders)
    @DeleteMapping
    public ResponseEntity<?> deleteFileOrDir(
            @RequestParam("dir") String dir,
            @RequestParam("name") String name) throws IOException {
        Path target = getSafePath(dir).resolve(name).normalize();

        if (!target.startsWith(rootPath)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (Files.isDirectory(target)) {
            deleteDirectoryRecursively(target);
        } else {
            Files.deleteIfExists(target);
        }

        return ResponseEntity.ok().build();
    }

    // Create a new subdirectory
    @PostMapping("/mkdir")
    public ResponseEntity<?> createDirectory(
            @RequestParam("dir") String dir,
            @RequestParam("name") String name) throws IOException {
        Path newDir = getSafePath(dir).resolve(name);
        Files.createDirectories(newDir);
        return ResponseEntity.ok().build();
    }

    // Helper to resolve and protect path traversal
    private Path getSafePath(String dir) {
        Path resolved = rootPath.resolve(dir).normalize();
        if (!resolved.startsWith(rootPath)) {
            throw new IllegalArgumentException("Invalid directory");
        }
        return resolved;
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
}