package org.sbezgin.p2016.rest;

import org.junit.Test;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.service.file.FileService;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileOperationRestTest {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private FileOperationRest rest;

    public FileOperationRestTest() {
        rest = new FileOperationRest();
        rest.setFileService(mock(FileService.class));
    }

    @Test
    public void testGetFile() throws ParseException {
        long fileID = 2L;
        FolderDTO folderDTO = new FolderDTO();
        folderDTO.setName("TestName");
        folderDTO.setId(10L);
        folderDTO.setParentId(1L);
        folderDTO.setIdPath("/1");
        folderDTO.setCreateDate(dateFormat.parse("2016/01/01"));
        folderDTO.setUpdateDate(dateFormat.parse("2016/01/05"));

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setDelete(false);
        permissionDTO.setRead(true);
        permissionDTO.setWrite(true);
        permissionDTO.setUserID(1L);

        folderDTO.setPermissions(Arrays.asList(permissionDTO));

        FileService fileService = rest.getFileService();
        when(fileService.getFileByID(fileID)).then(invocationOnMock -> folderDTO);

        Response resp = rest.getFile(fileID);
        Object entity = resp.getEntity();

        String expResult = "{\"status\":\"success\",\"data\":{\"id\":10,\"name\":\"TestName\",\"path\":null,\"idPath\":\"/1\",\"parentId\":1,\"createDate\":1451599200000,\"updateDate\":1451944800000,\"permissions\":{\"read\":true,\"write\":true,\"delete\":false},\"isFolder\":true}}";

        assertEquals(expResult, entity);
    }

    @Test
    public void testIncorrectIDGetFile() throws ParseException {
        long fileID = 2L;

        FileService fileService = rest.getFileService();
        when(fileService.getFileByID(fileID)).then(invocationOnMock -> null);

        Response resp = rest.getFile(fileID);
        Object entity = resp.getEntity();

        String expResult = "{\"status\":\"success\",\"data\":null}";

        assertEquals(expResult, entity);
    }


    public void testGetFolderChildren(){

    }


    public void testGetFileContent() {

    }


    public void testSaveFileContent() {

    }


    public void testCreateFolder() {


    }


    public void testCreateFile() {

    }


    public void testDeleteFile() {

    }


    public void testRenameFile() {

    }


    public void testSetPermission() {

    }
}
