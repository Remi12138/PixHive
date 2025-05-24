package com.jin.pixhive.shared.sharding;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jin.pixhive.domain.space.entity.Space;
import com.jin.pixhive.domain.space.valueobject.SpaceLevelEnum;
import com.jin.pixhive.domain.space.valueobject.SpaceTypeEnum;
import com.jin.pixhive.application.service.SpaceApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.jdbc.core.connection.ShardingSphereConnection;
import org.apache.shardingsphere.infra.metadata.database.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.rule.ShardingRule;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//@Component
@Slf4j
public class DynamicShardingManager {

    @Resource
    private DataSource dataSource;

    @Resource
    private SpaceApplicationService spaceApplicationService;

    private static final String LOGIC_TABLE_NAME = "picture";

    private static final String DATABASE_NAME = "logic_db"; // database name in the config file

    @PostConstruct
    public void initialize() {
        log.info("Initialize the dynamic table sharding configuration...");
        updateShardingTableNodes();
    }

    /**
     * fetch All Picture Table Names
     * include original "picture" and sharding "picture_{spaceId}"
     */
    private Set<String> fetchAllPictureTableNames() {
        // For the convenience of testing, sharding all team Spaces
        // in production, only sharding "premium" team space
        Set<Long> spaceIds = spaceApplicationService.lambdaQuery()
                .eq(Space::getSpaceType, SpaceTypeEnum.TEAM.getValue())
                .list()
                .stream()
                .map(Space::getId)
                .collect(Collectors.toSet());
        Set<String> tableNames = spaceIds.stream()
                .map(spaceId -> LOGIC_TABLE_NAME + "_" + spaceId)
                .collect(Collectors.toSet());
        tableNames.add(LOGIC_TABLE_NAME); // add original (logic) table
        return tableNames;
    }

    /**
     * update ShardingSphere 's actual-data-nodes dynamic table name config
     */
    private void updateShardingTableNodes() {
        Set<String> tableNames = fetchAllPictureTableNames();
        String newActualDataNodes = tableNames.stream()
                .map(tableName -> "PixHive." + tableName)
                .collect(Collectors.joining(","));
        log.info("Dynamic sharding actual-data-nodes config: {}", newActualDataNodes);

        ContextManager contextManager = getContextManager();
        ShardingSphereRuleMetaData ruleMetaData = contextManager.getMetaDataContexts()
                .getMetaData()
                .getDatabases()
                .get(DATABASE_NAME)
                .getRuleMetaData();

        Optional<ShardingRule> shardingRule = ruleMetaData.findSingleRule(ShardingRule.class);
        if (shardingRule.isPresent()) {
            ShardingRuleConfiguration ruleConfig = (ShardingRuleConfiguration) shardingRule.get().getConfiguration();
            List<ShardingTableRuleConfiguration> updatedRules = ruleConfig.getTables()
                    .stream()
                    .map(oldTableRule -> {
                        if (LOGIC_TABLE_NAME.equals(oldTableRule.getLogicTable())) {
                            ShardingTableRuleConfiguration newTableRuleConfig = new ShardingTableRuleConfiguration(LOGIC_TABLE_NAME, newActualDataNodes);
                            newTableRuleConfig.setDatabaseShardingStrategy(oldTableRule.getDatabaseShardingStrategy());
                            newTableRuleConfig.setTableShardingStrategy(oldTableRule.getTableShardingStrategy());
                            newTableRuleConfig.setKeyGenerateStrategy(oldTableRule.getKeyGenerateStrategy());
                            newTableRuleConfig.setAuditStrategy(oldTableRule.getAuditStrategy());
                            return newTableRuleConfig;
                        }
                        return oldTableRule;
                    })
                    .collect(Collectors.toList());
            ruleConfig.setTables(updatedRules);
            contextManager.alterRuleConfiguration(DATABASE_NAME, Collections.singleton(ruleConfig));
            contextManager.reloadDatabase(DATABASE_NAME);
            log.info("The dynamic table sharding rule has been updated successfully!");
        } else {
            log.error("Cannot find the sharding rule configuration of ShardingSphere. The dynamic table update failed.");
        }
    }

    /**
     * Get ShardingSphere ContextManager
     */
    private ContextManager getContextManager() {
        try (ShardingSphereConnection connection = dataSource.getConnection().unwrap(ShardingSphereConnection.class)) {
            return connection.getContextManager();
        } catch (SQLException e) {
            throw new RuntimeException("Get ShardingSphere ContextManager failed, ", e);
        }
    }

    /**
     * Dynamically create sharding tables
     */
    public void createSpacePictureTable(Space space) {
        // only for premium team space
        if (space.getSpaceType() == SpaceTypeEnum.TEAM.getValue() && space.getSpaceLevel() == SpaceLevelEnum.PREMIUM.getValue()) {
            Long spaceId = space.getId();
            String tableName = "picture_" + spaceId;
            // create new table
            String createTableSql = "CREATE TABLE " + tableName + " LIKE picture";
            try {
                SqlRunner.db().update(createTableSql);
                // update sharding table
                updateShardingTableNodes();
            } catch (Exception e) {
                log.error("Failed to create the picture space sharding table, space id = {}", space.getId());
            }
        }
    }

}

