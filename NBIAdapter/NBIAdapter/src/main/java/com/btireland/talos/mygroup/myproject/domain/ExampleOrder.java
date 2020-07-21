package com.btireland.talos.mygroup.myproject.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Version
    private Integer version;

    @Size(min = 5, max = 50, message = "Field must be between 5 - 50 characters")
    private String externalReference;

    @Size(max = 255)
    private String internalComment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Type type;

    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "exampleorder_id")
    private List<ExampleOrderItem> items;

    public enum Type {
        PFIB, CFIB
    }

    @PrePersist
    public void initCreationDate(){
        createdAt = LocalDateTime.now();
    }
}
