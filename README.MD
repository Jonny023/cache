# Grails3 Hibernate二级缓存

> 测试环境
```
| Grails Version: 3.1.5
| Groovy Version: 2.4.6
| JVM Version: 1.8.0_141
```

> 个人理解
* 项目是否需要加缓存要根据实际情况而定
* ehcache.xml配置maxElementsInMemory值要根据数据量而定，并不是越大越好
* 简单的缓存采用默认配置，复杂的可以单独定制，不同数据量不同配置

> 主要配置
* application.yml
* ehcache.xml
* domain类中添加cache true

> 测试结果
* 数据量小缓存效果明显，数据量大反而慢

> 总结
* 配置文件同时启用查询缓存和二级缓存
* 若domain中添加了cache true,查询的时候可以不加
* 若domain中未指定cache true,查询的时候可以启用缓存Persion.findAll([cache:true])
