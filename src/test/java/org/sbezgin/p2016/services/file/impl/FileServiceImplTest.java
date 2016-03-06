package org.sbezgin.p2016.services.file.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbezgin.p2016.common.FileType;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileContentDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.services.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(value = "file:src/main/webapp/WEB-INF/spring/applicationContext-db.xml"),
        @ContextConfiguration(value = "file:src/main/webapp/WEB-INF/spring/applicationContext.xml")
})
public class FileServiceImplTest {

    @Autowired
    private FileService fileService;

    @Test
    @Transactional
    public void testFileOperation() {
        testSaveFolder();
        testSaveChildren();
        testUpdateFiles();
        testGetFolderByID();
        testDeleteCascade();
    }

    private void testDeleteCascade() {
        //create folders
        List<AbstractFileDTO> files = fileService.getFilesByName("/ROOT", "Test Folder");
        assertEquals(1, files.size());

        AbstractFileDTO testFolder1 = files.get(0);
        FolderDTO testFolder2 = new FolderDTO();
        testFolder2.setName("Test Folder 2");
        testFolder2.setParentId(null);
        testFolder2.setParentId(testFolder1.getId());
        fileService.saveFile(testFolder2);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        files = fileService.getFilesByName("/ROOT/Test Folder", "Test Folder 2");
        assertEquals(1, files.size());
        testFolder2 = (FolderDTO) files.get(0);
        FolderDTO testFolder3 = new FolderDTO();
        testFolder3.setName("Test Folder 3");
        testFolder3.setParentId(null);
        testFolder3.setParentId(testFolder2.getId());

        fileService.saveFile(testFolder3);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        files = fileService.getFilesByName("/ROOT/Test Folder/Test Folder 2", "Test Folder 3");
        assertEquals(1, files.size());
        testFolder3 = (FolderDTO) files.get(0);
        FolderDTO testFolder4 = new FolderDTO();
        testFolder4.setName("Test Folder 4");
        testFolder4.setParentId(null);
        testFolder4.setParentId(testFolder3.getId());

        fileService.saveFile(testFolder4);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        files = fileService.getFilesByName("/ROOT/Test Folder/Test Folder 2/Test Folder 3", "Test Folder 4");
        assertEquals(1, files.size());
        testFolder4 = (FolderDTO) files.get(0);
        FolderDTO testFolder5 = new FolderDTO();
        testFolder5.setName("Test Folder 5");
        testFolder5.setParentId(null);
        testFolder5.setParentId(testFolder4.getId());

        fileService.saveFile(testFolder5);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        //deleting file
        files = fileService.getFilesByName("/ROOT/Test Folder/Test Folder 2/Test Folder 3/Test Folder 4", "Test Folder 5");
        assertEquals(1, files.size());

        testFolder5 = (FolderDTO) files.get(0);

        fileService.deleteFile(testFolder5.getId(), false);

        files = fileService.getFilesByName("/ROOT/Test Folder/Test Folder 2/Test Folder 3/Test Folder 4", "Test Folder 5");
        assertEquals(0, files.size());

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        //cascade deleting files
        files = fileService.getFilesByName("/ROOT/Test Folder", "Test Folder 2");
        assertEquals(1, files.size());
        AbstractFileDTO folder2 = files.get(0);

        fileService.deleteFile(folder2.getId(), true);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        files = fileService.getFilesByName("/ROOT/Test Folder/Test Folder 2/Test Folder 3", "Test Folder 4");
        assertEquals(0, files.size());

        files = fileService.getFilesByName("/ROOT/Test Folder/Test Folder 2", "Test Folder 3");
        assertEquals(0, files.size());
    }

    private void testGetFolderByID() {
        FolderDTO rootFolder = fileService.getRootFolder();

        FolderDTO someFolder = new FolderDTO();
        someFolder.setName("Test Folder");
        someFolder.setParentId(null);
        someFolder.setParentId(rootFolder.getId());

        fileService.saveFile(someFolder);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        List<AbstractFileDTO> files = fileService.getFilesByName("/ROOT", "Test Folder");
        assertEquals(1, files.size());

        FolderDTO folder = fileService.getFolder(files.get(0).getId());
        assertNotNull(folder);
    }

    private void testUpdateFiles() {
        List<AbstractFileDTO> abstractFile = fileService.getFilesByName("/ROOT", "Test File 4 With Content");
        assertEquals(1, abstractFile.size());
        Long fileID = abstractFile.get(0).getId();
        TextFileDTO file = fileService.getFullTextFile(fileID);

        assertNotNull(file);

        TextFileContentDTO fileContent = file.getFileContent();
        fileContent.setData("Test String 123 -- 2");

        fileService.saveFile(file);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        TextFileDTO savedFile = fileService.getFullTextFile(fileID);
        TextFileContentDTO savedFileContent = savedFile.getFileContent();
        assertNotNull(savedFileContent);
        assertEquals("Test String 123 -- 2", savedFileContent.getData());
    }

    private void testSaveFolder() {
        FolderDTO rootFolder = new FolderDTO();
        rootFolder.setName("ROOT");
        rootFolder.setParentId(null);
        rootFolder.setIdPath("/");
        rootFolder.setPath("/");

        fileService.saveFile(rootFolder);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        FolderDTO savedRootFolder = fileService.getRootFolder();

        assertEquals(rootFolder, savedRootFolder);
    }

    private void testSaveChildren() {
        FolderDTO rootFolder = fileService.getRootFolder();
        List<AbstractFileDTO> children = fileService.getChildren(rootFolder.getId(), 0, 50);
        assertEquals(0, children.size());

        TextFileDTO textFileDTO1 = new TextFileDTO();
        textFileDTO1.setName("Test File1");
        textFileDTO1.setType(FileType.JSON);
        textFileDTO1.setParentId(rootFolder.getId());

        TextFileDTO textFileDTO2 = new TextFileDTO();
        textFileDTO2.setName("Test File2");
        textFileDTO2.setType(FileType.JSON);
        textFileDTO2.setParentId(rootFolder.getId());

        TextFileDTO textFileDTO3 = new TextFileDTO();
        textFileDTO3.setName("Test File3");
        textFileDTO3.setType(FileType.JSON);
        textFileDTO3.setParentId(rootFolder.getId());

        TextFileDTO textFileDTO4 = new TextFileDTO();
        textFileDTO4.setName("Test File 4 With Content");
        textFileDTO4.setType(FileType.JSON);
        textFileDTO4.setParentId(rootFolder.getId());

        TextFileContentDTO fileContent = new TextFileContentDTO();
        fileContent.setData("Test String 123");
        textFileDTO4.setFileContent(fileContent);

        fileService.saveFile(textFileDTO1);
        fileService.saveFile(textFileDTO2);
        fileService.saveFile(textFileDTO3);
        fileService.saveFile(textFileDTO4);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        List<AbstractFileDTO> savedChildren = fileService.getChildren(rootFolder.getId(), 0, 50);
        assertEquals(4, savedChildren.size());

        Set<String> names = savedChildren.stream().map(AbstractFileDTO::getName).collect(Collectors.toSet());

        assertTrue("Failed checking name: " + textFileDTO1.getName(), names.contains(textFileDTO1.getName()));
        assertTrue("Failed checking name: " + textFileDTO2.getName(), names.contains(textFileDTO2.getName()));
        assertTrue("Failed checking name: " + textFileDTO3.getName(), names.contains(textFileDTO3.getName()));
        assertTrue("Failed checking name: " + textFileDTO4.getName(), names.contains(textFileDTO4.getName()));

        TextFileDTO savedFile = null;
        for (AbstractFileDTO savedChild : savedChildren) {
            if (savedChild.getName().equals("Test File 4 With Content")) {
                savedFile = (TextFileDTO) savedChild;
            }
        }

        assertNotNull(savedFile);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        savedFile = fileService.getFullTextFile(savedFile.getId());

        TextFileContentDTO savedFileContent = savedFile.getFileContent();

        assertEquals("Test String 123", savedFileContent.getData());
    }
}
