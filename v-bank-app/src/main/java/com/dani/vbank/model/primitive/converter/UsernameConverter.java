package com.dani.vbank.model.primitive.converter;

import com.dani.vbank.model.primitive.Username;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UsernameConverter implements AttributeConverter<Username, String> {

    @Override
    public String convertToDatabaseColumn(Username attribute) {
        return attribute.toString();
    }

    @Override
    public Username convertToEntityAttribute(String dbData) {
        return new Username(dbData);
    }
}
