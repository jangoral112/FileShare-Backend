package com.jango.file.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "file_share")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileShare {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id")
    @Column(name="owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name="id")
    @Column(name="recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name="id")
    @Column(name="file_id")
    private FileMetadata fileMetadata;

    @Column(name = "share_date")
    private Timestamp shareDate;
}
