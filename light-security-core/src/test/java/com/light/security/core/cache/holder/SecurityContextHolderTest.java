package com.light.security.core.cache.holder;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.authentication.token.UsernamePasswordAuthentication;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SecurityContextHolderTest {

    @Test
    public void internalSupport() {
        Assert.assertTrue(Authentication.class.isAssignableFrom(UsernamePasswordAuthentication.class));
    }
}