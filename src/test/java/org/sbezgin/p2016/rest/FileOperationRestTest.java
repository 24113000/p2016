package org.sbezgin.p2016.rest;

import org.junit.Test;
import org.sbezgin.p2016.common.FileType;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileContentDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.service.file.FileService;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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

    @Test
    public void testGetFolderChildren() {
        long folderID = 2L;

        FileService fileService = rest.getFileService();
        List<AbstractFileDTO> fileList = Arrays.asList(new FolderDTO(), new TextFileDTO());
        when(fileService.getChildren(folderID, 0, 50)).then(invocationOnMock -> fileList);

        Response resp = rest.getFolderChildren(folderID, 0, 50);
        Object entity = resp.getEntity();

        String expResult = "{\"status\":\"success\",\"data\":[{\"id\":null,\"name\":null,\"path\":null,\"idPath\":null,\"parentId\":null,\"createDate\":null,\"updateDate\":null,\"permissions\":null,\"isFolder\":true},{\"id\":null,\"name\":null,\"path\":null,\"idPath\":null,\"parentId\":null,\"createDate\":null,\"updateDate\":null,\"permissions\":null,\"type\":null,\"fileContent\":null,\"isFolder\":false}]}";
        assertEquals(expResult, entity);
    }

    @Test
    public void testGetFileContent() {
        long fileID = 2L;

        FileService fileService = rest.getFileService();

        TextFileDTO textFileDTO = new TextFileDTO();
        TextFileContentDTO contentDTO = new TextFileContentDTO();
        contentDTO.setData("111 222");
        textFileDTO.setFileContent(contentDTO);

        when(fileService.getFullTextFile(fileID)).then(invocationOnMock -> textFileDTO);

        Response resp = rest.getFileContent(fileID);
        Object entity = resp.getEntity();

        String expResult = "{\"status\":\"success\",\"data\":{\"id\":null,\"name\":null,\"path\":null,\"idPath\":null,\"parentId\":null,\"createDate\":null,\"updateDate\":null,\"permissions\":null,\"type\":null,\"fileContent\":{\"id\":null,\"data\":\"111 222\"},\"isFolder\":false}}";
        assertEquals(expResult, entity);
    }

    @Test
    public void testSaveFileContent() {
        long fileID = 2L;
        String fileContent = "111 222";

        FileService fileService = rest.getFileService();

        TextFileDTO textFileDTO = new TextFileDTO();

        when(fileService.getFullTextFile(fileID)).then(invocationOnMock -> textFileDTO);

        rest.saveFileContent(fileID, fileContent);

        verify(fileService, times(1)).saveTextFileContent(fileID, fileContent);
    }

    @Test
    public void testCreateFolder() {
        FileService fileService = rest.getFileService();

        rest.createFolder("test", 11L);

        FolderDTO newFolder = new FolderDTO();
        newFolder.setName("test");
        newFolder.setParentId(11L);
        verify(fileService, times(1)).saveFile(newFolder);
    }

    @Test
    public void testCreateFile() {
        FileService fileService = rest.getFileService();

        rest.createFile("testName", FileType.JSON.toString(), 11L);

        TextFileDTO newFile = new TextFileDTO();
        newFile.setType(FileType.JSON);
        newFile.setName("testName");
        newFile.setParentId(11L);
        verify(fileService, times(1)).saveFile(newFile);
    }

    @Test
    public void testDeleteFile() {
        FileService fileService = rest.getFileService();

        rest.deleteFile(11L);

        verify(fileService, times(1)).deleteFile(11L, false);
    }

    @Test
    public void testRenameFile() {
        FileService fileService = rest.getFileService();

        rest.renameFile(11L, "new_name");

        verify(fileService, times(1)).renameFile(11L, "new_name");
    }


    public void testSetPermission() {

    }
}
