#Achelous

##QuickStart
以下将通过几个常见的使用case来简单说明如何使用Achelous框架
###case: 基于Kafka的SEDA项目
seda项目常用于消息中心,异步推送通知的场景中.

**基于Spring的使用方**
+ Spring配置文件引用&lt; import resource="achelous-core.xml"/&gt;

+ 如果是消费者方,则增加kafka.properties配置文件,声明如下

```
kafka_consumer.zkconfig=localhost:2181 #kafka所用的zk地址
async.from=my-replicated-topic #订阅topic
async.streams=4 #同时处理线程数
kafka_proc.worker=kafkaConsumerTestWorker #实际消费处理者的bean名
```

+ 如果是生产者方,则增加kafka.properties配置文件,声明如下

```
fail_retry.times=1 #失败重试1次
fail_retry.sleep=1000 #失败重试间隔1000ms
kafka_producer.to=my-replicated-topic #推送topic
kafka_producer.brokers=localhost:9092 #kafkfail_retry.times=1
fail_retry.sleep=1000
kafka_producer.to=my-replicated-topic
kafka_producer.brokers=localhost:9092a broker地址,多个按照逗号分隔
```

+ 如果是消费者方, 则只需调用一次KafkaBootStraper.startSpringConsumer()即可.
+ 如果是生产者方, 则推送的时候调用 KafkaBootStraper.get().pub(new TestObj())即可.
+ 如果同一个使用方中,既有生产者也有消费者,则配置如下,调用时,要推送则KafkaBootStraper.get().pubDefaultKey("producer", new TestObj());要消费则KafkaBootStraper.get().sub("consumer");

```
producer.fail_retry.times=1
producer.fail_retry.sleep=1000
producer.kafka_producer.to=my-replicated-topic2
producer.kafka_producer.brokers=localhost:9092

consumer.kafka_consumer.zkconfig=localhost:2181
consumer.async.cooker_msgfrom=my-replicated-topic
consumer.async.cooker=com.dingding.open.achelous.kafka.support.KafkaAsyancCooker #可不写
consumer.async.streams=4
consumer.kafka_proc.worker=kafkaConsumerTestWorker
consumer.kafka_producer.to=my-replicated-topic
consumer.kafka_producer.brokers=localhost:9092_
```

+ 以上仅为简单使用.更深入的用法此处未给出.待补充.更多使用方式请见achelous-kafka工程下的单测.

**非Spring项目**
+ 待补充.使用方式基本一致.使用方式请见achelous-kafka工程下的单测.


##Paraphrase
achelous，阿刻罗俄斯，是希腊achelous river的守护神。在现实的开发场景中，服务端上无数的数据流向各个服务，触发各种分支，面临着非常复杂的场景，服务的搭建、优化和治理一直是一个难题。顾名思义，该系统也即旨在为服务开发与治理提供一些辅助手段，以期规避一些常规问题，并优化产能。

##Introdution
+ achelous是一套基于核心插件框架的服务治理系统，他的核心思想是通过微核+插件化的体系结构，结合响应式函数编程或者配置化编程，以期望java客户端/服务端开发中的一些问题进行抽象和简化。
+ 你可以通过XML/Properties/API方式来声明每个阶段的处理逻辑。每个阶段即一个插件。遵循“插件即服务”（plugin-as-service）的原则。
+ achelous旨在对开发中常常面临的多线程编程、异步调度、热插拔、开关、abtest、降级、容错、熔断等提供帮助，并对所有的插件运行现状进行统一监控管理。
+ 除此之外，后期achelous也会在rpc框架、系统监控报警与反馈等方面提供一些功能和优化。

## Modules 
+ achelous-core提供系统核心的插件定义和组装功能。以及一些通用的插件实现。
+ achelous-seda提供SEDA（阶段性事件处理架构）的一个抽象实现。主要用于异步调度场景。
+ achelous-fluent提供通用的响应式函数编程API。
+ achelous-kafka提供基于kafka的seda实现。

## Todo Recently
+ seda模块对msgid进行支持
+ msg定时plugin开发
+ 带补充

## Contact Us
inf@zufangit.cn

## Changelog

**v0.8** —— **2015-12-25**
+ 对锁逻辑进行优化升级.性能提升.
+ Async plugin优化.增加cooker.与其他plugin解耦.
+ 增加@FilePath和@DefaultProps注解.来指定配置路径以及默认配置项.并将其与plugin解耦.
+ 对于Spring进行深度支持.
+ 对kafka进行深入封装.
+ 修复多单线程多pipeline的bug
+ 补充使用说明

**v0.7** —— **2015-11-09**
+ plugin以及worker对spring提供支持
+ 提供通用异步plugin。病对kafkaConsumer的三个阶段进行改造。

**v0.6** —— **2015-11-04**
+ 新增achelous-fluent工程。提供响应式API。
+ achelous核心框架进行大规模修改，适应pipeline里多个相同plugin的case等场景。
+ kafka producer进行容错。提供两种通用容错插件fastfail以及retry。
+ properties改造为key可重复、可按序读取。
+ plugin进行改造，目前基本支持properties以及fluent api两种声明方式。

**v0.5** —— **2015-11-03**
+ 转为多maven工程。

**v0.4** —— **2015-11-03**
+ 提供kafka的生产者/消费者插件。
+ 提供kafka入口和启动器的封装。
+ achelous-core框架的扩展性升级。
+ 修复一些严重bug。

**v0.3** —— **2015-10-29** 
+ 基本完成kafka的生产者插件。
+ 提供kafka入口中的**pub**功能。
+ 提供动态插件路径路由。可以采用具体的插件入口声明的路径，或者默认路径。
+ 提供三级cache。

**v0.2** —— **2015-10-27**
+ 提供properties 解析器。
+ pipeline管理器核心初始化代码基本完成。

**v0.1** —— **2015-10-26**
+ achelous核心框架初始化。