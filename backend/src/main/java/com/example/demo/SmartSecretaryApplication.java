package com.example.demo;

import com.example.demo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SmartSecretaryApplication {

	@Autowired
	private DocumentRepository documentRepository;

//	private DocumentService documentService = new DocumentService(documentRepository);

	public static void main(String[] args) {
		SpringApplication.run(SmartSecretaryApplication.class, args);
	}


//	public void run(String... args) throws Exception {
//		List<Document> documents = documentRepository.findAll();
//		for (Document document : documents) {
//			System.out.println(document);
//		}
//	}

}
