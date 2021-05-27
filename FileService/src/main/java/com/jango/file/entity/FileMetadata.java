package com.jango.file.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "files_metadata")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadata {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;
    
    @Column(name = "size")
    private Long size;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "public_file_flag")
    private Boolean publicFileFlag;
    
    @Column(name = "upload_timestamp")
    private Timestamp uploadTimestamp;

    @Column(name = "key_id")
    @Generated(GenerationTime.INSERT)
    private Long keyId;
}
