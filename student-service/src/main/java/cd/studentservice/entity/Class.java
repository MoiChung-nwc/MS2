package cd.studentservice.entity;

import cd.studentservice.enumerate.ClassStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "classes")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true,nullable = false)
    String code;
    String name;
    Integer grade;
    @Enumerated(EnumType.STRING)
    ClassStatus status;
    @CreationTimestamp
    Instant createdAt;
    @ManyToOne
    @JoinColumn(name = "school_id")
    School school;
}
