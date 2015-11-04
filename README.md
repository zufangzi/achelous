#Achelous

##介绍
1. achelous是一套基于核心插件框架的服务治理系统，他的核心思想是通过微核+插件化的体系结构，结合响应式函数编程或者配置化编程，以期望java客户端/服务端开发中的一些问题进行抽象和简化。
2. 你可以通过XML/Properties/API方式来声明每个阶段的处理逻辑。每个阶段即一个插件。遵循“插件即服务”（plugin-as-service）的原则。

3. will realize crontab and push function by default.
4. will use kafka as the default stage-communicated mq.

## 模块介绍
1. achelous-core提供系统核心的插件定义和组装功能。以及一些通用的插件实现。
2. achelous-seda提供SEDA（阶段性事件处理架构）的一个抽象实现。
3. achelous-fluent提供通用的响应式函数编程API。
4. achelous-kafka提供基于kafka的seda实现。

##Changelog

**v0.5** —— **2015-11-03**
+ turn into multi-maven projects

**v0.4** —— **2015-11-03**
+ provide kafka consumer & producer plugin. 
+ kafka entrance & starter packaging.
+ achelous-core framwork's expansibility upgrade.
+ fix a few bugs.

**v0.3** —— **2015-10-29** 
+ kafka producer plugin mainly completed.
+ provide kafka entrance with **pub** function
+ provide dynamic plugin path. can use specific plugin entrance path or default path.
+ provide 3-level cache

**v0.2** —— **2015-10-27**
+ provide properties parser.
+ pipeline manger core-initialization mainly fininshed.

**v0.1** —— **2015-10-26**
+ fluent core framework initialization. 