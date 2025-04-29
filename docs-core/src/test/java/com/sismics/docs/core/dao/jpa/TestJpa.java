package com.sismics.docs.core.dao.jpa;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.util.TransactionUtil;
import com.sismics.docs.core.util.authentication.InternalAuthenticationHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the persistance layer.
 * 
 * @author jtremeaux
 */
public class TestJpa extends BaseTransactionalTest {
    @Test
    public void testJpa() throws Exception {
        // Create a user
        UserDao userDao = new UserDao();
        User user = createUser("testJpa");

        TransactionUtil.commit();

        // Search a user by his ID
        user = userDao.getById(user.getId());
        Assert.assertNotNull(user);
        Assert.assertEquals("toto@docs.com", user.getEmail());

        // getById - not found
        Assert.assertNull(userDao.getById("nonexistentId"));

        // authentication
        Assert.assertNotNull(new InternalAuthenticationHandler().authenticate("testJpa", "12345678"));
        Assert.assertNull(new InternalAuthenticationHandler().authenticate("testJpa", "wrongpass"));
        Assert.assertNull(new InternalAuthenticationHandler().authenticate("noSuchUser", "12345678"));

        // Delete the created user
        userDao.delete("testJpa", user.getId());
        TransactionUtil.commit();
        Assert.assertNull(userDao.getById(user.getId()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullId() {
        new UserDao().delete("testJpa", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdWithNull() {
        new UserDao().getById(null);
    }

    @Test
    public void testUpdatePasswordAndHashedPassword() throws Exception {
        UserDao userDao = new UserDao();
        User user = createUser("updatePassUser");

        userDao.updatePassword(user, "newPassword123");
        userDao.updateHashedPassword(user);
        TransactionUtil.commit();

        User updated = userDao.getById(user.getId());
        Assert.assertNotNull(updated);
    }

    @Test
    public void testUpdateOnboarding() throws Exception{
        UserDao userDao = new UserDao();
        User user = createUser("onboardingUser");

        userDao.updateOnboarding(user);
        TransactionUtil.commit();

        User updated = userDao.getById(user.getId());
        Assert.assertNotNull(updated);
    }

    @Test
    public void testGetActiveUserCount() throws Exception {
        UserDao userDao = new UserDao();
        long before = userDao.getActiveUserCount();

        createUser("countUser");
        TransactionUtil.commit();

        long after = userDao.getActiveUserCount();
        Assert.assertTrue(after >= before);
    }

    @Test
    public void testGetGlobalStorageCurrent() {
        long size = new UserDao().getGlobalStorageCurrent();
        Assert.assertTrue(size >= 0);
    }

    @Test
    public void testGetActiveByUsername() throws Exception {
        UserDao userDao = new UserDao();
        User user = createUser("activeUser");

        User found = userDao.getActiveByUsername("activeUser");
        Assert.assertNotNull(found);
    }

    @Test
    public void testUpdateQuota() throws Exception {
        UserDao userDao = new UserDao();
        User user = createUser("quotaUser");

        user.setStorageQuota(1024L);
        userDao.updateQuota(user);
        TransactionUtil.commit();
    }

}
