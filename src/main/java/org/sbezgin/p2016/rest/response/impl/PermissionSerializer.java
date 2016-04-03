package org.sbezgin.p2016.rest.response.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.collections.CollectionUtils;
import org.sbezgin.p2016.db.dto.PermissionDTO;

import java.io.IOException;
import java.util.List;

public class PermissionSerializer extends JsonSerializer {
    private Long userID;

    public PermissionSerializer(Long userID) {
        this.userID = userID;
    }

    @Override
    public void serialize(Object values, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        List<PermissionDTO> permissionDTOs = (List<PermissionDTO>) values;
        if (CollectionUtils.isNotEmpty(permissionDTOs)) {
            PermissionDTO permission = getPermission(permissionDTOs, userID);
            gen.writeStartObject();
            gen.writeBooleanField("read", permission.getRead() == null ? false : permission.getRead());
            gen.writeBooleanField("write", permission.getWrite() == null ? false : permission.getWrite());
            gen.writeBooleanField("delete", permission.getDelete() == null ? false : permission.getDelete());
            gen.writeEndObject();
        }
    }

    private PermissionDTO getPermission(List<PermissionDTO> permissionDTOs, Long userID) {
        for (PermissionDTO permissionDTO : permissionDTOs) {
            if (permissionDTO.getUserID().equals(userID)) {
                return permissionDTO;
            }
        }
        return null;
    }
}
