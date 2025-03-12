package com.tacticboard.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtils {

    private SecurityUtils() {
        // Utility class, no instantiation
    }

    /**
     * Get the username of the current user.
     *
     * @return the username of the current user
     */
    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return Optional.ofNullable(springSecurityUser.getUsername());
        }

        if (authentication.getPrincipal() instanceof String) {
            return Optional.ofNullable((String) authentication.getPrincipal());
        }

        return Optional.empty();
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Check if the current user has a specific authority.
     *
     * @param authority the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
        }
        return false;
    }

    /**
     * Check if the current user has any of the specified authorities.
     *
     * @param authorities the authorities to check
     * @return true if the current user has any of the authorities, false otherwise
     */
    public static boolean hasAnyAuthority(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            for (String authority : authorities) {
                if (authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the current user has all of the specified authorities.
     *
     * @param authorities the authorities to check
     * @return true if the current user has all of the authorities, false otherwise
     */
    public static boolean hasAllAuthorities(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            for (String authority : authorities) {
                if (authentication.getAuthorities().stream()
                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}