package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "data", columnDefinition = "BYTEA", nullable = false)
    private byte[] data;

    public Document() { }

    public Document(Integer id, String fileName, String fileType, byte[] data) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public Integer getId()      { return id; }
    public String  getFileName(){ return fileName; }
    public String  getFileType(){ return fileType; }
    public byte[]  getData()    { return data; }

    public void setId(Integer id)           { this.id = id; }
    public void setFileName(String fileName){ this.fileName = fileName; }
    public void setFileType(String fileType){ this.fileType = fileType; }
    public void setData(byte[] data)        { this.data = data; }

}
