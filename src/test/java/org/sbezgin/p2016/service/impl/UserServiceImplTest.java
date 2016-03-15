package org.sbezgin.p2016.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.util.TestUtil.commitAndStartTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(value = "file:src/main/webapp/WEB-INF/spring/applicationContext-db.xml"),
        @ContextConfiguration(value = "file:src/main/webapp/WEB-INF/spring/applicationContext.xml")
})

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserServiceImplTest {
    
    @Autowired
    private UserService userService;

    @Transactional
    @Test
    public void testUserOperation() {
        testCreateUserAndGet();
        testUpdateUser();
        testGetUserByID();
        testRemoveUser();
        testRemoveUserByIncorrectID();
    }

    private void testGetUserByID() {
        UserDTO userDTO = userService.getUserByEmail("blabla@ukr.net");
        commitAndStartTransaction();
        assertNotNull(userService.getUser(userDTO.getId()));
    }

    private void testRemoveUserByIncorrectID() {
        try {
            userService.delete(100L);
            fail();
        } catch (P2016Exception e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    private void testUpdateUser() {
        UserDTO userDTO = userService.getUserByEmail("blabla@ukr.net");
        userDTO.setResetToken("TokenBlaBla");
        userService.save(userDTO);
        commitAndStartTransaction();
        UserDTO savedDTO = userService.getUserByEmail("blabla@ukr.net");
        assertNotNull(savedDTO);
        assertEquals("TokenBlaBla", userDTO.getResetToken());
    }

    private void testCreateUserAndGet() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("blabla@ukr.net");
        userDTO.setPassword("123456789");
        userService.save(userDTO);

        commitAndStartTransaction();

        UserDTO savedDto = userService.getUserByEmail("blabla@ukr.net");
        assertNotNull(savedDto);
        assertEquals("blabla@ukr.net", savedDto.getEmail());
    }

    private void testRemoveUser() {
        UserDTO userDTO = userService.getUserByEmail("blabla@ukr.net");
        commitAndStartTransaction();
        userService.delete(userDTO.getId());
        commitAndStartTransaction();

        UserDTO savedDto = userService.getUserByEmail("blabla@ukr.net");
        assertNull(savedDto);
    }
}
