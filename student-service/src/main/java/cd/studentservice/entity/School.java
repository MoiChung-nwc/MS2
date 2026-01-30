package cd.studentservice.entity;

import cd.studentservice.enumerate.SchoolStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "schools")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true,nullable = false)
    String code;
    @Column(nullable = false)
    String name;
    String address;
    @Enumerated(EnumType.STRING)
    SchoolStatus status;
    @CreationTimestamp
    Instant createdAt;
}
