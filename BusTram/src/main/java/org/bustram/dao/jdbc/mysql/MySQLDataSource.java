package org.bustram.dao.jdbc.mysql;

import org.bustram.dao.jdbc.DataSource;

/**
 * La classe qui permet d'indiquer une base de données JDBC MySQL
 */
public class MySQLDataSource extends DataSource {

    /**
     * Le pilote de connexion
     */
    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Le pont du connexion (Protocole)
     */
    public static final String MYSQL_BRIDGE = "jdbc:mysql:";

    /**
     * Construceur de la classe
     */
    public MySQLDataSource(String host, String source, String userName, String password) {
        super(MYSQL_BRIDGE + "//" + host + "/" + source, MYSQL_DRIVER, userName, password);
    }
}
