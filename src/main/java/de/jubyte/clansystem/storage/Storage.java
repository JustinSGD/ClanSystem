package de.jubyte.clansystem.storage;

import com.zaxxer.hikari.HikariDataSource;
import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfigBuilder;
import org.bukkit.Bukkit;

import java.net.InetSocketAddress;

public class Storage {

    private DatabaseDriver databaseDriver;
    private Database database;

    private DatabaseCollection clanCollection;
    private DatabaseCollection clanMembersCollection;

    public void createConnection() {
        Bukkit.getLogger();
        this.databaseDriver = DatabaseDriverFactory.create("ClanSystem", new SQLDatabaseDriverConfigBuilder()
                .setDialect(Dialect.MYSQL)
                .setAddress(new InetSocketAddress("localhost", 3306))
                .setDataSourceClassName(HikariDataSource.class.getName())
                .setUsername("")
                .setPassword("")
                .build());
        this.databaseDriver.connect();
        this.database = databaseDriver.getDatabase("ClanSystem");

        createCollections();
    }

    public void createCollections() {
        this.clanCollection = database.createCollection("Clan")
                .field("ClanID", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("ClanName", DataType.STRING)
                .field("ClanTag", DataType.STRING)
                .create();

        this.clanMembersCollection = database.createCollection("ClanMembers")
                .field("UUID", DataType.UUID, FieldOption.PRIMARY_KEY)
                .field("Name", DataType.STRING)
                .field("ClanID", DataType.INTEGER)
                .field("Role", DataType.INTEGER)
                .create();

    }

    public boolean isConnected() {
        return this.databaseDriver.isConnected();
    }

    public void deleteConnection() {
        this.databaseDriver.disconnect();
    }

    public DatabaseCollection getClanCollection() {
        return clanCollection;
    }

    public DatabaseCollection getClanMembersCollection() {
        return clanMembersCollection;
    }
}
