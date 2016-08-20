package org.sbezgin.p2016.spring.security;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthenticationEntryPointsHolder {
    private final Map<String, AuthenticationEntryPoint> map;
    public AuthenticationEntryPointsHolder(Map<String, AuthenticationEntryPoint> entryPoints) {
        map = entryPoints;
    }

    public LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> getConvertedEntryPoints() {
        if (map != null) {
            LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> convertedEntryPoints = new LinkedHashMap<>();
            for (Map.Entry<String, AuthenticationEntryPoint> entry : map.entrySet()) {//TODO java8
                RegexRequestMatcher requestMatcher = new RegexRequestMatcher(entry.getKey(), null);
                convertedEntryPoints.put(requestMatcher, entry.getValue());
            }
            return convertedEntryPoints;
        } else {
            return new LinkedHashMap<>();
        }
    }
}
