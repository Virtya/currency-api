package ru.ds.education.currency.entity;

import lombok.*;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "curs_request")
public class CursRequestEntity {

    @Id
    @Column(name = "correlation_id")
    private String correlationId = UUID.randomUUID().toString();

    @NotNull
    @Column(name = "currency_name")
    private String currencyName;

    @Nullable
    @Column(name = "curs_date")
    private LocalDate currencyDate;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private StatusEntity statusEntity;
}
