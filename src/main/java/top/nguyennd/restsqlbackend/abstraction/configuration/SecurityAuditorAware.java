package top.nguyennd.restsqlbackend.abstraction.configuration;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.firstNonBlank;

public class SecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isNull(authentication) || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }
        return Optional.ofNullable(extractUserName(authentication)).filter(username -> !username.isBlank());
    }

    private String extractUserName(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            var jwt = jwtAuth.getToken();
            return firstNonBlank(
                    jwt.getClaimAsString("username"),
                    jwt.getClaimAsString("preferred_username"),
                    jwt.getSubject());
        }
        return authentication.getName();
    }
}
