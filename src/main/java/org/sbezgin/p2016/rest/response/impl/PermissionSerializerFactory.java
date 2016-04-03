package org.sbezgin.p2016.rest.response.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.sbezgin.p2016.db.dto.PermissionDTO;

public class PermissionSerializerFactory extends BeanSerializerFactory {

    private Long userID;

    public PermissionSerializerFactory(Long userID) {
        super(null);
        this.userID = userID;
    }

    public PermissionSerializerFactory(SerializerFactoryConfig config) {
        super(config);
    }

    @Override
    public JsonSerializer<Object> createSerializer(SerializerProvider prov, JavaType origType) throws JsonMappingException {
        if ((origType.getClass().equals(CollectionType.class))
            && origType.getContentType().getRawClass().equals(PermissionDTO.class)) {
            return new PermissionSerializer(userID);
        }

        return super.createSerializer(prov, origType);
    }
}
