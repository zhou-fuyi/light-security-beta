package com.light.security.core.util.crypto.password;

/**
 * @InterfaceName PasswordEncoder
 * @Description 仿照SpringSecurity完成
 * Service interface for encoding passwords.
 *
 * The preferred implementation is {@code BCryptPasswordEncoder}.
 * @Author ZhouJian
 * @Date 2019-12-02
 */
public interface PasswordEncoder {

    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}
