package org.sbezgin.p2016.services.file.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.junit.Assert.assertEquals;

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

        TextFileDTO textFileDTO = new TextFileDTO();

        fileService.saveFile();
    }
}
