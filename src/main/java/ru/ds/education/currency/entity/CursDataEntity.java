package ru.ds.education.currency.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "curs_data")
public class CursDataEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "currency_name")
    private String currencyName;

    @Nullable
    @Column(name = "currency_code")
    private Integer currencyCode;

    @NotNull
    @Column(name = "curs")
    private Double curs;

    @Nullable
    @Column(name = "curs_date")
    private LocalDate cursDate;
}
