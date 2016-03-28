package org.sbezgin.p2016.service.file.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbezgin.p2016.common.FileType;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileContentDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.service.UserService;
import org.sbezgin.p2016.service.file.FileAccessDeniedException;
import org.sbezgin.p2016.service.file.FileChangedException;
import org.sbezgin.p2016.service.file.FileNotFoundException;
import org.sbezgin.p2016.service.file.FileService;
import org.sbezgin.p2016.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.util.TestUtil.commitAndStartTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(value = "file:src/main/webapp/WEB-INF/spring/applicationContext-db.xml"),
        @ContextConfiguration(value = "file:src/main/webapp/WEB-INF/spring/applicationContext.xml")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class FileServiceImplPermissionTest {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;

    private Map<String, Long> userIDs = new HashMap<>();

    @Test
    @Transactional
    public void testPermission() {

        FileServiceImpl service = (FileServiceImpl) fileService;
        UserServiceImpl userService = spy(service.getUserService());
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });
        service.setUserService(userService);

        createFoldersAndFiles();
        createUsers();

        testSetPermission();
        testUpdatePermission();
        testSetPermissionRecursively();
        testGetSharedFile(userService);
        testUpdateSharedFile(userService);
        testDeleteSharedFile(userService);

        //negative scenario
        testReadNotSharedChilred(userService);
        testDeleteFileFromNotAccessibleFile(userService);
        testReadFromNNotAccessibleFolder(userService);
        testDeleteRecursivelyIfUserDontHaveAccessOnChildren(userService);
        testUpdatingSharedFile(userService);
    }

    private void testReadNotSharedChilred(UserServiceImpl userService) {
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });


        FolderDTO secondFolder = (FolderDTO) fileService.getFilesByName("/ROOT/FirstFolder", "SecondFolder").get(0);
        TextFileDTO deepFile = new TextFileDTO();
        deepFile.setName("THE_FILE");
        deepFile.setType(FileType.JSON);
        deepFile.setParentId(secondFolder.getId());
        fileService.saveFile(deepFile);

        commitAndStartTransaction();

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            Long user2ID = userIDs.get("blabla2@ukr.net");
            UserDTO user = new UserDTO();
            user.setId(user2ID);
            return user;
        });

        List<AbstractFileDTO> children = fileService.getChildren(secondFolder.getId(), 0, 50);
        assertEquals(0, children.size());
    }

    private void testDeleteSharedFile(UserServiceImpl userService) {
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });
        Long user2ID = userIDs.get("blabla2@ukr.net");
        PermissionDTO perm = new PermissionDTO();
        perm.setRead(true);
        perm.setWrite(true);
        perm.setDelete(true);
        perm.setUserID(user2ID);

        AbstractFileDTO abstractFileDTO = fileService.getFilesByName("/ROOT/FirstFolder/SecondFolder", "THE_FILE").get(0);
        fileService.savePermission(abstractFileDTO, perm);

        commitAndStartTransaction();

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(user2ID);
            return user;
        });

        fileService.deleteFile(abstractFileDTO.getId(), false);

        commitAndStartTransaction();

        List<AbstractFileDTO> files = fileService.getFilesByName("/ROOT/FirstFolder/SecondFolder", "THE_FILE");
        assertEquals(0, files.size());
    }

    private void testUpdateSharedFile(UserServiceImpl userService) {
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });
        Long user2ID = userIDs.get("blabla2@ukr.net");
        PermissionDTO perm = new PermissionDTO();
        perm.setRead(true);
        perm.setWrite(true);
        perm.setUserID(user2ID);

        AbstractFileDTO abstractFileDTO = fileService.getFilesByName("/ROOT/FirstFolder/SecondFolder", "THE_FILE").get(0);
        fileService.savePermission(abstractFileDTO, perm);

        commitAndStartTransaction();

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(user2ID);
            return user;
        });

        TextFileDTO fullTextFile = fileService.getFullTextFile(abstractFileDTO.getId());
        TextFileContentDTO fileContent = fullTextFile.getFileContent();
        if (fileContent == null) {
            fileContent = new TextFileContentDTO();
            fullTextFile.setFileContent(fileContent);
        }
        fileContent.setData("1111");

        fileService.saveFile(fullTextFile);

        commitAndStartTransaction();

        TextFileDTO fullTextFile2 = fileService.getFullTextFile(abstractFileDTO.getId());
        assertEquals(fileContent.getData(), fullTextFile2.getFileContent().getData());
    }

    private void testGetSharedFile(UserServiceImpl userService) {
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            Long aLong = userIDs.get("blabla2@ukr.net");
            UserDTO user = new UserDTO();
            user.setId(aLong);
            return user;
        });

        AbstractFileDTO abstractFileDTO = fileService.getFilesByName("/ROOT/FirstFolder", "SecondFolder").get(0);
        assertNotNull(abstractFileDTO);
        assertEquals("SecondFolder", abstractFileDTO.getName());
    }

    private void testUpdatingSharedFile(UserServiceImpl userService) {
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });

        AbstractFileDTO firstUserFileDTO = fileService.getFilesByName("/ROOT", "Test File").get(0);

        PermissionDTO perm = new PermissionDTO();
        perm.setWrite(true);
        perm.setRead(true);
        perm.setUserID(77L);

        fileService.savePermission(firstUserFileDTO, perm);

        commitAndStartTransaction();

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(77L);
            return user;
        });

        TextFileDTO secondUserFullTextFile = fileService.getFullTextFile(firstUserFileDTO.getId());
        TextFileContentDTO fileContent = secondUserFullTextFile.getFileContent();
        if (fileContent == null) {
            fileContent = new TextFileContentDTO();
        }
        fileContent.setData("111112");
        fileService.saveFile(secondUserFullTextFile);

        commitAndStartTransaction();

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });

        try {
            fileService.saveFile(firstUserFileDTO);
            fail();
        } catch (FileChangedException e) {
            assertTrue(true);
        }
    }

    private void testDeleteRecursivelyIfUserDontHaveAccessOnChildren(UserServiceImpl userService) {
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });

        FolderDTO firstFolder = (FolderDTO) fileService.getFilesByName("/ROOT", "Folder_1").get(0);

        FolderDTO secondFolder = new FolderDTO();
        secondFolder.setName("Folder_2");
        secondFolder.setParentId(firstFolder.getId());
        fileService.saveFile(secondFolder);

        commitAndStartTransaction();

        TextFileDTO textFileDTO2 = new TextFileDTO();
        textFileDTO2.setName("TestFile2");
        textFileDTO2.setType(FileType.JSON);
        textFileDTO2.setParentId(firstFolder.getId());

        fileService.saveFile(secondFolder);

        AbstractFileDTO testFile = fileService.getFilesByName("/ROOT/Folder_1", "Test File").get(0);
        fileService.deleteFile(testFile.getId(), false);

        commitAndStartTransaction();

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setUserID(77L);
        permissionDTO.setRead(true);
        permissionDTO.setWrite(true);
        permissionDTO.setDelete(true);

        secondFolder = (FolderDTO) fileService.getFilesByName("/ROOT/Folder_1", "Folder_2").get(0);
        fileService.savePermission(firstFolder, permissionDTO);
        fileService.savePermission(secondFolder, permissionDTO);



        commitAndStartTransaction();

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(77L);
            return user;
        });

        try {
            fileService.deleteFile(firstFolder.getId(), true);
            fail();
        } catch (FileAccessDeniedException e) {
            assertTrue(true);
        }
    }

    private void testReadFromNNotAccessibleFolder(UserServiceImpl userService) {

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });

        FolderDTO rootFolder = fileService.getRootFolder();
        FolderDTO firstFolder = new FolderDTO();
        firstFolder.setName("Folder_1");
        firstFolder.setParentId(rootFolder.getId());
        fileService.saveFile(firstFolder);

        commitAndStartTransaction();

        firstFolder = (FolderDTO) fileService.getFilesByName("/ROOT", "Folder_1").get(0);
        TextFileDTO textFileDTO = new TextFileDTO();
        textFileDTO.setName("Test File");
        textFileDTO.setType(FileType.JSON);
        textFileDTO.setParentId(firstFolder.getId());

        fileService.saveFile(textFileDTO);

        commitAndStartTransaction();

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setRead(true);
        permissionDTO.setUserID(77L);

        fileService.setFolderPermissionRecursively(firstFolder, permissionDTO);

        commitAndStartTransaction();

        AbstractFileDTO testFile = fileService.getFilesByName("/ROOT/Folder_1", "Test File").get(0);

        //switch to another user
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(77L);
            return user;
        });

        List<AbstractFileDTO> children = fileService.getChildren(firstFolder.getId(), 0, 50);
        assertEquals(1, children.size());
        assertEquals(testFile, children.get(0));

        //switch to another user
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });

        UserDTO user_ = new UserDTO();
        user_.setId(77L);
        fileService.removeFolderPermissionRecursively(firstFolder, user_);

        commitAndStartTransaction();

        //switch to another user
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(77L);
            return user;
        });

        try {
            fileService.getChildren(firstFolder.getId(), 0, 50);
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }
    }

    private void testDeleteFileFromNotAccessibleFile(UserServiceImpl userService) {
        AbstractFileDTO testFile = fileService.getFilesByName("/ROOT", "Test File").get(0);
        Long fileID = testFile.getId();

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(77L);
            return user;
        });

        try {
            fileService.deleteFile(fileID, false);
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }


        //try delete if user has access only for read
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setDelete(false);
        permissionDTO.setRead(true);
        permissionDTO.setWrite(false);
        permissionDTO.setUserID(77L);

        fileService.savePermission(testFile, permissionDTO);

        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(77L);
            return user;
        });

        try {
            fileService.deleteFile(fileID, false);
            fail();
        } catch (FileAccessDeniedException e) {
            assertTrue(true);
        }

        TextFileDTO fullTextFile = fileService.getFullTextFile(fileID);
        TextFileContentDTO fileContent = new TextFileContentDTO();
        fileContent.setData("Bla Bla Bla");
        fullTextFile.setFileContent(fileContent);
        try {
            fileService.saveFile(fullTextFile);
            fail();
        } catch (FileAccessDeniedException e) {
            assertTrue(true);
        }
    }

    private void testUpdatePermission() {
        List<AbstractFileDTO> files = fileService.getFilesByName("/ROOT", "Test File");
        AbstractFileDTO fileDTO = files.get(0);
        PermissionDTO permissionDTO = fileDTO.getPermissionDTOs().get(0);
        permissionDTO.setWrite(true);
        permissionDTO.setDelete(false);

        fileService.saveFile(fileDTO);

        commitAndStartTransaction();

        files = fileService.getFilesByName("/ROOT", "Test File");
        fileDTO = files.get(0);
        permissionDTO = fileDTO.getPermissionDTOs().get(0);
        assertTrue(permissionDTO.getRead());
        assertTrue(permissionDTO.getWrite());
        assertFalse(permissionDTO.getDelete());

        Long user2ID = userIDs.get("blabla2@ukr.net");
        assertEquals(user2ID, permissionDTO.getUserID());
    }

    private void testSetPermissionRecursively() {
        FolderDTO firstFolder = (FolderDTO) fileService.getFilesByName("/ROOT", "FirstFolder").get(0);

        Long user2ID = userIDs.get("blabla2@ukr.net");

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setUserID(user2ID);
        permissionDTO.setRead(true);

        fileService.setFolderPermissionRecursively(firstFolder, permissionDTO);

        commitAndStartTransaction();

        firstFolder = (FolderDTO) fileService.getFilesByName("/ROOT", "FirstFolder").get(0);
        List<AbstractFileDTO> children = fileService.getChildren(firstFolder.getId(), 0, 50);

        FolderDTO secondFolder = (FolderDTO) children.get(0);
        children = fileService.getChildren(secondFolder.getId(), 0, 50);

        TextFileDTO textFileDTO = (TextFileDTO) children.get(0);

        //-------------------------------------
        List<PermissionDTO> permissionDTOs = firstFolder.getPermissionDTOs();
        assertEquals(1, permissionDTOs.size());
        assertEquals(permissionDTO, permissionDTOs.get(0));


        permissionDTOs = secondFolder.getPermissionDTOs();
        assertEquals(1, permissionDTOs.size());
        PermissionDTO savedPerm = permissionDTOs.get(0);
        assertTrue(savedPerm.getRead());
        assertNull(savedPerm.getWrite());
        assertNull(savedPerm.getDelete());
        assertEquals(user2ID, savedPerm.getUserID());

        permissionDTOs = textFileDTO.getPermissionDTOs();
        assertEquals(1, permissionDTOs.size());
        savedPerm = permissionDTOs.get(0);
        assertTrue(savedPerm.getRead());
        assertNull(savedPerm.getWrite());
        assertNull(savedPerm.getDelete());
        assertEquals(user2ID, savedPerm.getUserID());
    }

    private void createUsers() {

        UserDTO userDTO3 = new UserDTO();
        userDTO3.setEmail("blabla3@ukr.net");
        userDTO3.setPassword("123456789");
        userService.save(userDTO3);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setEmail("blabla2@ukr.net");
        userDTO2.setPassword("123456789");
        userService.save(userDTO2);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("blabla@ukr.net");
        userDTO.setPassword("123456789");
        userService.save(userDTO);

        commitAndStartTransaction();

        UserDTO savedDto = userService.getUserByEmail("blabla@ukr.net");
        assertNotNull(savedDto);
        assertEquals("blabla@ukr.net", savedDto.getEmail());

        UserDTO savedDto2 = userService.getUserByEmail("blabla2@ukr.net");
        assertNotNull(savedDto2);
        assertEquals("blabla2@ukr.net", savedDto2.getEmail());

        UserDTO savedDto3 = userService.getUserByEmail("blabla3@ukr.net");
        assertNotNull(savedDto3);
        assertEquals("blabla3@ukr.net", savedDto3.getEmail());

        userIDs.put("blabla@ukr.net", savedDto.getId());
        userIDs.put("blabla2@ukr.net", savedDto2.getId());
        userIDs.put("blabla3@ukr.net", savedDto3.getId());
    }

    private void testSetPermission() {
        List<AbstractFileDTO> files = fileService.getFilesByName("/ROOT", "Test File");
        assertEquals(1, files.size());

        AbstractFileDTO file = files.get(0);
        Long user2ID = userIDs.get("blabla2@ukr.net");

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setUserID(user2ID);
        permissionDTO.setRead(true);
        permissionDTO.setDelete(true);

        fileService.savePermission(file, permissionDTO);
        commitAndStartTransaction();

        AbstractFileDTO fileDTO = fileService.getFileByID(file.getId());

        assertNotNull(fileDTO);
        List<PermissionDTO> permissionDTOs = fileDTO.getPermissionDTOs();
        assertEquals(1, permissionDTOs.size());

        PermissionDTO savedPermissionDTO = permissionDTOs.get(0);
        assertEquals(permissionDTO, savedPermissionDTO);
    }

    private void createFoldersAndFiles() {
        FolderDTO rootFolder = new FolderDTO();
        rootFolder.setName("ROOT");
        rootFolder.setParentId(null);
        rootFolder.setIdPath("/");
        rootFolder.setPath("/");

        fileService.saveFile(rootFolder);

        commitAndStartTransaction();

        FolderDTO savedRootFolder = fileService.getRootFolder();
        FolderDTO firstFolder = new FolderDTO();
        firstFolder.setName("FirstFolder");
        firstFolder.setParentId(savedRootFolder.getId());
        fileService.saveFile(firstFolder);

        commitAndStartTransaction();

        firstFolder = (FolderDTO) fileService.getFilesByName("/ROOT", "FirstFolder").get(0);
        FolderDTO secondFolder = new FolderDTO();
        secondFolder.setName("SecondFolder");
        secondFolder.setParentId(firstFolder.getId());
        fileService.saveFile(secondFolder);

        commitAndStartTransaction();

        secondFolder = (FolderDTO) fileService.getFilesByName("/ROOT/FirstFolder", "SecondFolder").get(0);
        TextFileDTO deepFile = new TextFileDTO();
        deepFile.setName("THE_FILE");
        deepFile.setType(FileType.JSON);
        deepFile.setParentId(secondFolder.getId());
        fileService.saveFile(deepFile);

        commitAndStartTransaction();

        assertEquals(rootFolder, savedRootFolder);

        TextFileDTO textFileDTO = new TextFileDTO();
        textFileDTO.setName("Test File");
        textFileDTO.setType(FileType.JSON);
        textFileDTO.setParentId(savedRootFolder.getId());

        fileService.saveFile(textFileDTO);

        commitAndStartTransaction();
    }
}
