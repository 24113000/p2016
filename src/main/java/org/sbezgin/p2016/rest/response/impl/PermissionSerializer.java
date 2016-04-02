package org.sbezgin.p2016.rest.response.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.collections.CollectionUtils;
import org.sbezgin.p2016.db.dto.PermissionDTO;

import java.io.IOException;
import java.util.List;

public class PermissionSerializer extends JsonSerializer<List> {
    private Long userID;

    public PermissionSerializer(Long userID) {
        this.userID = userID;
    }

    @Override
    public void serialize(List values, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (CollectionUtils.isNotEmpty(values)
                && values.get(0).getClass().equals(PermissionDTO.class)) {
            PermissionDTO permission = getPermission(values, userID);
            gen.writeStartObject();
            gen.writeBooleanField("read", permission.getRead() == null ? false : permission.getRead());
            gen.writeBooleanField("write", permission.getWrite() == null? false : permission.getWrite());
            gen.writeBooleanField("delete", permission.getDelete() == null? false : permission.getDelete());
            gen.writeEndObject();
        } else {
            serializers.defaultSerializeValue(values, gen);
        }
    }

    private PermissionDTO getPermission(List permissionDTOs, Long userID) {
        for (Object permissionDTO : permissionDTOs) {
            PermissionDTO perm = (PermissionDTO) permissionDTO;
            if (perm.getUserID().equals(userID)) {
                return perm;
            }
        }
        return null;
    }
}
