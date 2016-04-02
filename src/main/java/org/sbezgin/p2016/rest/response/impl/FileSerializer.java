package org.sbezgin.p2016.rest.response.impl;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;

import java.io.IOException;
import java.util.List;

public class FileSerializer extends StdSerializer<AbstractFileDTO> {
    private Long userID;
    protected FileSerializer(Class<AbstractFileDTO> t, Long userID) {
        super(t);
        this.userID = userID;
    }

    @Override
    public void serialize(AbstractFileDTO file, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        PermissionDTO permission = getPermission(file.getPermissionDTOs(), userID);
        jgen.writeBooleanField("read", permission.getRead());
        jgen.writeBooleanField("write", permission.getWrite());
        jgen.writeBooleanField("delete", permission.getDelete());
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
