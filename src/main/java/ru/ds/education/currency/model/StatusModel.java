package ru.ds.education.currency.model;

import lombok.*;
import org.codehaus.commons.nullanalysis.NotNull;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "status")
public class StatusModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "status_name")
    private String statusName;
}
