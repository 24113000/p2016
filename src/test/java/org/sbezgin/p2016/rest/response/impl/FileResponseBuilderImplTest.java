package org.sbezgin.p2016.rest.response.impl;

import org.junit.Test;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.rest.response.ResponseBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileResponseBuilderImplTest {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Test
    public void testConvertFileToJSON() throws ParseException {

        long fileID = 2L;

        AbstractFileDTO fileDTO = createTestObject(fileID);

        ResponseBuilder responseBuilder = new FileResponseBuilderImpl(1L);
        String resp = (String) responseBuilder.setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(fileDTO)
                .buildResponse();

        assertEquals(
                "{\"status\":\"success\",\"data\":{\"id\":2,\"name\":\"TestName\",\"path\":\"/ROOT\",\"idPath\":\"/1\",\"parentId\":1,\"createDate\":1456783200000,\"updateDate\":1454364000000,\"permissions\":{\"read\":true,\"write\":false,\"delete\":true},\"isFolder\":true}}",
                resp
        );
    }

    @Test
    public void testConvertFilesToJSON() throws ParseException {
        long fileID = 2L;

        List<AbstractFileDTO> files = Arrays.asList(createTestObject(fileID), createTestObject(fileID + 1));

        ResponseBuilder responseBuilder = new FileResponseBuilderImpl(1L);
        String resp = (String) responseBuilder.setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(files)
                .buildResponse();

        assertEquals(
                "{\"status\":\"success\",\"data\":[{\"id\":2,\"name\":\"TestName\",\"path\":\"/ROOT\",\"idPath\":\"/1\",\"parentId\":1,\"createDate\":1456783200000,\"updateDate\":1454364000000,\"permissions\":{\"read\":true,\"write\":false,\"delete\":true},\"isFolder\":true},{\"id\":3,\"name\":\"TestName\",\"path\":\"/ROOT\",\"idPath\":\"/1\",\"parentId\":1,\"createDate\":1456783200000,\"updateDate\":1454364000000,\"permissions\":{\"read\":true,\"write\":false,\"delete\":true},\"isFolder\":true}]}",
                resp
        );

    }


    private AbstractFileDTO createTestObject(long fileID) throws ParseException {
        AbstractFileDTO fileDTO = new FolderDTO();
        fileDTO.setId(fileID);
        fileDTO.setName("TestName");
        fileDTO.setParentId(1L);
        fileDTO.setIdPath("/1");
        fileDTO.setPath("/ROOT");
        fileDTO.setCreateDate(simpleDateFormat.parse("2016/03/01"));
        fileDTO.setUpdateDate(simpleDateFormat.parse("2016/02/02"));

        List<PermissionDTO> perms = new ArrayList<>();
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setId(99L);
        permissionDTO.setUserID(1L);
        permissionDTO.setFileDTO(fileDTO);
        permissionDTO.setRead(true);
        permissionDTO.setDelete(true);
        perms.add(permissionDTO);

        fileDTO.setPermissions(perms);
        return fileDTO;
    }
}
