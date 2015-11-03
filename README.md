#Achelous

##Introdution
1. achelous use a fluent-style core frame that support seda system. 
2. you can use XML/Properties/API to declare stages that realized by customized plugin.
3. will realize crontab and push function by default.
4. will use kafka as the default stage-communicated mq.

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