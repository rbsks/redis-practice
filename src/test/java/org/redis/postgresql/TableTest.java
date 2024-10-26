//package org.redis.postgresql;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.UUID;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Slf4j
//@SpringBootTest
//public class TableTest {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Test
//    public void createTable() throws Exception {
//        int numberOfTablesToCreate = 100_000;
//        int availableProcessors = 10;
//        int tablesPerThread = numberOfTablesToCreate / availableProcessors;
//
//        CountDownLatch doneSignal = new CountDownLatch(numberOfTablesToCreate);
//        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);
//
//        String createSql = """
//                CREATE TABLE %s (
//                    id SERIAL PRIMARY KEY,
//                    email VARCHAR(100)
//                );
//                """;
//        String insertSql = """
//                INSERT INTO %s (email) values('%s')
//                """;
//
//        for (int i = 0; i < availableProcessors; i++) {
//            executorService.execute(() -> {
//                int count = tablesPerThread;
//                while (count > 0) {
//                    String tableName = "testdb_" + UUID.randomUUID().toString().replace("-", "_");
//                    try {
//                        jdbcTemplate.execute(String.format(createSql, tableName));
//
//                        String[] insertQueries = new String[20];
//                        for (int k = 0; k < 20; k++) {
//                            insertQueries[k] = String.format(insertSql, tableName, UUID.randomUUID());
//                        }
//
//                        jdbcTemplate.batchUpdate(insertQueries);
//                        doneSignal.countDown();
//                        count -= 1;
//                    } catch (Exception e) {
//                        log.info("table 생성 실패.", e);
//                    }
//                }
//            });
//        }
//
//        doneSignal.await();
//    }
//}
