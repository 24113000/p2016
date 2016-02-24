package org.sbezgin.p2016.services.file.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbezgin.p2016.common.FileType;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void testfileOperation() {
        testSaveFolder();

        testSaveChildren();
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

        fileService.saveFile(textFileDTO1);
        fileService.saveFile(textFileDTO2);
        fileService.saveFile(textFileDTO3);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        List<AbstractFileDTO> savedChildren = fileService.getChildren(rootFolder.getId(), 0, 50);
        assertEquals(3, savedChildren.size());

        Set<String> names = savedChildren.stream().map(AbstractFileDTO::getName).collect(Collectors.toSet());

        assertTrue("Failed checking name: " + textFileDTO1.getName(), names.contains(textFileDTO1.getName()));
        assertTrue("Failed checking name: " + textFileDTO2.getName(), names.contains(textFileDTO2.getName()));
        assertTrue("Failed checking name: " + textFileDTO3.getName(), names.contains(textFileDTO3.getName()));

    }
}
