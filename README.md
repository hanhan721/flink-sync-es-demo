# 通过Flink实现MySQL与ES的数据同步
## 1.填写运行参数配置自己的MySQL数据库和ES地址
### 参数如下
```
--job-conf
parallelism=1
--job-conf
checkpoint-interval=1000
--mysql-conf
hostname=localhost
--mysql-conf
port=3306
--mysql-conf
username=root
--mysql-conf
password=root
--mysql-conf
database-name=test
--mysql-conf
start-up-mode=latest_offset
--mysql-conf
jdbc-url="jdbc:mysql://127.0.0.1:3306/test"
--sink-conf
es-host=127.0.0.1
--sink-conf
es-port=9200
--sink-conf
es-schema=http
```
## 2.修改关于索引相关的所有字段
### 包括以下文件
resource/esIndex/addrIndex.json  
src/main/java/MySqlCdcUserIndexEsJob.java  
src/main/java/strategy/SyncStrategyFactory.java  
src/main/java/strategy/impl/UserAddrSyncStrategy.java  
src/main/java/es/entity/EsAddressEntity.java  
