package com.light.security.core.cache.holder;

import com.light.security.core.authentication.token.Authentication;
import com.light.security.core.authentication.token.SubjectNamePasswordAuthenticationToken;
import org.junit.Assert;
import org.junit.Test;

public class InternalSecurityContextHolderTest {

    @Test
    public void internalSupport() {
        Assert.assertTrue(Authentication.class.isAssignableFrom(SubjectNamePasswordAuthenticationToken.class));
    }
}