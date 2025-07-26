package com.epam.gymcrm.bootstrap;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSqlRunner {

    private final DataSource dataSource;

    public DataSqlRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void runSql() throws Exception {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("data.sql"));
    }
}
