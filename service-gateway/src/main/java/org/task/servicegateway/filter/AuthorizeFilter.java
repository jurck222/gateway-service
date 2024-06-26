package org.task.servicegateway.filter;

import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.task.servicegateway.exceptions.AuthorizationException;

import java.util.Objects;

@Component
public class AuthorizeFilter extends AbstractGatewayFilterFactory<AuthorizeFilter.Config> {

    public AuthorizeFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String role = exchange.getAttribute("role");
            String expectedRole = config.getConfigValue();
            System.out.println(expectedRole);
            if(!Objects.equals(role, expectedRole)){
                throw new AuthorizationException("User does not have permission to access this resource");
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
        private String expectedRole;

        public String getConfigValue() {
            return expectedRole;
        }

        public void setConfigValue(String configValue) {
            this.expectedRole = configValue;
        }
    }
}
