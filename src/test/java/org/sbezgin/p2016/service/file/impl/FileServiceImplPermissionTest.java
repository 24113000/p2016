package org.sbezgin.p2016.service.file.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbezgin.p2016.common.FileType;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.service.UserService;
import org.sbezgin.p2016.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        createFoldersAndFiles();
        createUsers();

        testSetPermission();
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

        fileService.setPermission(file, permissionDTO);
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

        assertEquals(rootFolder, savedRootFolder);

        TextFileDTO textFileDTO = new TextFileDTO();
        textFileDTO.setName("Test File");
        textFileDTO.setType(FileType.JSON);
        textFileDTO.setParentId(savedRootFolder.getId());

        fileService.saveFile(textFileDTO);

        commitAndStartTransaction();
    }
}
