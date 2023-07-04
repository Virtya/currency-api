package ru.ds.education.currency.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.dto.CursData;
import ru.ds.education.currency.dto.CursRequest;
import ru.ds.education.currency.dto.Status;
import ru.ds.education.currency.entity.CursDataEntity;
import ru.ds.education.currency.entity.CursRequestEntity;
import ru.ds.education.currency.entity.StatusEntity;
import ru.ds.education.currency.mapper.converter.LocalDateConverter;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MapperCurrency extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {

        factory.getConverterFactory().registerConverter(new LocalDateConverter());

        factory.classMap(CursDataEntity.class, CursData.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(CursData.class, CursDataEntity.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(CursRequestEntity.class, CursRequest.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(CursRequest.class, CursRequestEntity.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(Status.class, StatusEntity.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(StatusEntity.class, Status.class)
                .mapNulls(false)
                .byDefault()
                .register();
    }
}
