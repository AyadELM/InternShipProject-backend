package com.nov.dbEngine;


import ch.qos.logback.core.db.dialect.OracleDialect;
import com.mysql.jdbc.Driver;
import com.nov.domain.Connexion;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SQLConnectionBuilder {
    private  Connexion connexion;

    public SQLConnectionBuilder(Connexion connexion){
        this.connexion = connexion;

    }
    public Connexion getConnetionParams(){
        return connexion;
    }
    public void setConnetionParams(Connexion conn){
        this.connexion = conn;
    }

    public JdbcTemplate build()  {

        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(dataSource(this.connexion.getConnector().getType()));
        return template;
    }

    public DataSource dataSource(String SGBD) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        System.out.println("sgbd = "+SGBD);
        String baseUrl = null;
        String sslParam = "";
        switch (SGBD){
            case "mysql":
                baseUrl="jdbc:mysql://";
                sslParam = connexion.isSsl() ?"useSSL=true":"useSSL=false";
                break;
            case "oracle":
                baseUrl = "jdbc:oracle:thin:@";
                break;
            case "h2":

                baseUrl = "jjdbc:h2:mem:";
                break;
            case "postgree":
                baseUrl = "jdbc:postgresql://";
                sslParam = connexion.isSsl() ?"ssl=true&":"ssl=false&";
                sslParam+="sslfactory=org.postgresql.ssl.NonValidatingFactory";
                System.out.println("sgbd = "+SGBD+" inside case postgree");
                break;
            default:
                break;
        }
        ds.setDriverClassName(connexion.getConnector().getDriver());
        if(connexion.getConnector().getType().equals("oracle")){
            ds.setUrl(baseUrl + connexion.getHostname() + ":" + connexion.getPort() + ":"
                + connexion.getCurrentDatabase());
            ds.setUsername(connexion.getUser());
        }else {
            ds.setUrl(baseUrl + connexion.getHostname() + ":" + connexion.getPort() +"/"
                + connexion.getCurrentDatabase() + "?" + sslParam);
            ds.setUsername(connexion.getUser());
        }
        ds.setPassword(connexion.getPassword());

        return ds;
    }




}
