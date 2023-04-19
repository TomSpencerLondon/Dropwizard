package org.example.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import io.dropwizard.db.DataSourceFactory;

public class DropwizardBlogConfiguration extends Configuration {
    private static final String DATABASE = "database";

    @Valid
    @NotNull
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @JsonProperty(DATABASE)
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @JsonProperty(DATABASE)
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }
}
