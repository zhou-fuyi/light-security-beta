package com.light.security.client.config;

import com.light.security.core.access.role.GrantedRole;
import com.light.security.core.authentication.SubjectDetailService;
import com.light.security.core.authentication.subject.SubjectDetail;
import com.light.security.core.exception.SubjectNameNotFoundException;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;


/**
 * @ClassName SubjectServiceImpl
 * @Description TODO
 * @Author ZhouJian
 * @Date 2019-12-05
 */
@Configuration
public class SubjectServiceImpl implements SubjectDetailService {
    @Override
    public SubjectDetail loadSubjectBySubjectName(String subjectName) throws SubjectNameNotFoundException {
        return new SubjectDetail() {
            @Override
            public Collection<? extends GrantedRole> getRoles() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getSubjectName() {
                return null;
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
    }
}
