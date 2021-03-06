package com.flytecnologia.core.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConnectionProviderImpl;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FlyHibernateConfig {

    private JpaProperties jpaProperties;

    public FlyHibernateConfig(JpaProperties jpaProperties){
        this.jpaProperties = jpaProperties;
    }

    @Bean
    public Module datatypeHibernateModule() {
        //resolve problem of lazy inicialization :)
        Hibernate5Module module = new Hibernate5Module();
        //get only id of lazy objects
        module.configure(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        return module;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                       MultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                       CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        Map<String, Object> properties = new HashMap<>();
        properties.putAll(jpaProperties.getHibernateProperties(dataSource));

        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.flytecnologia","br.com");
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public JpaProperties jpaProperties(){
        return new JpaProperties();
    }

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        return new FlyMultiTenantConnectionProviderImpl();
    }
/*
    @Bean(name = "currentTenantIdentifierResolver") @Profile("oauth-security")
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }

    @Bean(name = "currentTenantIdentifierResolver") @Profile("basic-security")
    public CurrentTenantIdentifierResolver currentTenantIdentifierBasicResolver() {
        return new TenantBasicIdentifierResolver();
    }*/
}
