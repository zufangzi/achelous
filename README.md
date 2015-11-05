#Achelous

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
+ plugin和worker对Spring的全面支持
+ kafka生产者消费者plugin完善
+ seda模块对msgid进行支持
+ msg定时plugin开发

##Changelog

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