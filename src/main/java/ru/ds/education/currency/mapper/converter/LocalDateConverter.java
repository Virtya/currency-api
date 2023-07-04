package ru.ds.education.currency.mapper.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends BidirectionalConverter<LocalDate, String> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String convertTo(LocalDate source, Type<String> destinationType, MappingContext mappingContext) {
        return source.format(formatter);
    }

    @Override
    public LocalDate convertFrom(String source, Type<LocalDate> destinationType, MappingContext mappingContext) {
        return LocalDate.parse(source, formatter);
    }
}
