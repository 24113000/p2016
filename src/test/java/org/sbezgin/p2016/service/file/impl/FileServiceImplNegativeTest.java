package org.sbezgin.p2016.service.file.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.service.file.FileNotFoundException;
import org.sbezgin.p2016.service.file.FileService;
import org.sbezgin.p2016.service.file.FileServiceImpl;
import org.sbezgin.p2016.service.file.FolderIsNotEmpty;
import org.sbezgin.p2016.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
public class FileServiceImplNegativeTest {

    @Autowired
    private FileService fileService;

    @Test
    @Sql({"/add_root_directory.sql"})
    @Transactional
    public void testNegativeFileOperation() {

        FileServiceImpl service = (FileServiceImpl) fileService;
        UserServiceImpl userService = spy(service.getUserService());
        when(userService.getCurrentUser()).then(invocationOnMock -> {
            UserDTO user = new UserDTO();
            user.setId(1L);
            return user;
        });
        service.setUserService(userService);

        testFolderByIncorrectID();
        testGetChildrenFolderByIncorrectID();
        testDeleteFileByIncorrectID();
        testDeletingNotEmptyFolder();
        testRenameFileByIncorrectID();
        testIfRootNotUniqueRootFile();
        testGettingFileByIncorrectName();
        testSavingFolderWithSlash();
        tryUpdateFileNotExists();
    }

    private void testDeletingNotEmptyFolder() {
        //creating folders

        FolderDTO rootFolder = fileService.getRootFolder();

        FolderDTO someFolder = new FolderDTO();
        someFolder.setName("Test Folder");
        someFolder.setParentId(null);
        someFolder.setParentId(rootFolder.getId());
        fileService.saveFile(someFolder);

        commitAndStartTransaction();

        List<AbstractFileDTO> files = fileService.getFilesByName("/ROOT", "Test Folder");
        assertEquals(1, files.size());

        AbstractFileDTO testFolder1 = files.get(0);

        FolderDTO testFolder2 = new FolderDTO();
        testFolder2.setName("Test Folder 2");
        testFolder2.setParentId(null);
        testFolder2.setParentId(testFolder1.getId());
        fileService.saveFile(testFolder2);

        commitAndStartTransaction();

        try {
            fileService.deleteFile(testFolder1.getId(), false);
            fail();
        } catch (FolderIsNotEmpty e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    private void tryUpdateFileNotExists() {
        FolderDTO someFolder = new FolderDTO();
        someFolder.setId(777L);
        someFolder.setName("nextFolder");
        someFolder.setParentId(null);
        someFolder.setIdPath("/");
        someFolder.setPath("/");

        try {
            fileService.saveFile(someFolder);
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    private void testSavingFolderWithSlash() {
        FolderDTO folderWithSlash = new FolderDTO();
        folderWithSlash.setName("nextFolder/");
        folderWithSlash.setParentId(null);
        folderWithSlash.setIdPath("/");
        folderWithSlash.setPath("/");

        try {
            fileService.saveFile(folderWithSlash);
            fail();
        } catch (Exception e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    private void testGettingFileByIncorrectName() {
        List<AbstractFileDTO> filesByName = fileService.getFilesByName("/ROOT/Bla/Bla", "bla.txt");
        assertNotNull(filesByName);
        assertEquals(0, filesByName.size());
    }

    private void testIfRootNotUniqueRootFile() {
        FolderDTO secondRootFolder = new FolderDTO();
        secondRootFolder.setName("ROOT");
        secondRootFolder.setParentId(null);
        secondRootFolder.setIdPath("/");
        secondRootFolder.setPath("/");

        fileService.saveFile(secondRootFolder);

        commitAndStartTransaction();

        try {
            fileService.getRootFolder();
            fail();
        } catch (Exception e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    private void testGettingRootFolderIfDoesntExist() {
        try {
            fileService.getRootFolder();
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    private void testRenameFileByIncorrectID() {
        try {
            fileService.renameFile(777, "Bla Bla");
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }
    }

    private void testDeleteFileByIncorrectID() {
        try {
            fileService.deleteFile(777, false);
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }

        try {
            fileService.deleteFile(777, true);
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }
    }

    private void testGetChildrenFolderByIncorrectID() {
        try {
            fileService.getChildren(777, 0, 10);
            fail();
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }
    }

    private void testFolderByIncorrectID() {
        FolderDTO folder = fileService.getFolder(777);
        assertNull(folder);
    }
}
