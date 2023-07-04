package ru.ds.education.currency.entity;

import lombok.*;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "curs_request")
public class CursRequestEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "curs_request_seq_gen")
    @SequenceGenerator(name = "curs_request_seq_gen", sequenceName = "curs_request_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Column(name = "currency_name")
    private String currencyName;

    @Nullable
    @Column(name = "curs_date")
    private LocalDate currencyDate;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "correlation_id")
    private String correlationId;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private StatusEntity statusEntity;
}
