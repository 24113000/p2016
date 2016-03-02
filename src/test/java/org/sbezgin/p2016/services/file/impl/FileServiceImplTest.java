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
    }

    private void testUpdateFiles() {
        AbstractFileDTO abstractFile = fileService.getFileByName("/ROOT", "Test File 4 With Content");
        TextFileDTO file = fileService.getFullTextFile(abstractFile.getId());

        assertNotNull(file);

        TextFileContentDTO fileContent = file.getFileContent();
        fileContent.setData("Test String 123 -- 2");

        fileService.saveFile(file);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        TextFileDTO savedFile = fileService.getFullTextFile(abstractFile.getId());
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
