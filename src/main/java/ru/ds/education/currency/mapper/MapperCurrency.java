package ru.ds.education.currency.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import ru.ds.education.currency.dto.CursDataDto;
import ru.ds.education.currency.model.CursDataModel;

@Component
@RequiredArgsConstructor
public class MapperCurrency extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {

        factory.classMap(CursDataModel.class, CursDataDto.class)
                .mapNulls(false)
                .byDefault()
                .register();

        factory.classMap(CursDataDto.class, CursDataModel.class)
                .mapNulls(false)
                .byDefault()
                .register();

    }
}
