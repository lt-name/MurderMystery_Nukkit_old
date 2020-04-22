# MurderMystery  
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
![Java CI](https://github.com/lt-name/MurderMystery_Nukkit/workflows/Java%20CI/badge.svg)
#### 本插件仍在开发中！  
密室杀人游戏  
在论坛的介绍：  
[McBBS](https://www.mcbbs.net/thread-1014768-1-1.html)  
[MineBBS](https://www.minebbs.com/resources/murdermystery.1012/)  
#### 已实现功能：  
- [X] 多房间  
- [X] 游戏核心玩法  
  - [X] 侦探死后掉弓  
  - [X] 被杀生成尸体  
  - [X] 给玩家设置随机皮肤  
- [X] 进退游戏保存背包  
- [X] 聊天限制  
- [X] GUI操作  
- [X] 一些道具  
  - [X] 随机药水  
  - [X] 护盾生成器  
  - [X] 减速雪球  
    
#### 对于开发者：
API请参考：  
main/java/name/murdermystery/api/Api.java  
  
插件提供事件： 
 - MurderRoomStartEvent 房间开始事件
 - MurderPlayerDamageEvent 玩家被攻击事件  
   杀手用剑打人，平民或侦探用弓打人时调用
 - MurderPlayerDeathEvent 玩家死亡事件   
   杀手被平民或侦探击杀，平民或侦探被杀手击杀时调用   
 - MurderPlayerCorpseSpawnEvent 尸体生成事件  
   玩家死亡时调用
   
物品CompoundTag：  
isMurderItem - boolean类型 是否为插件物品  
MurderType - int类型 物品功能 
   - 1: 侦探之弓  
   - 2: 杀手之剑  
   - 20: 箭  
   - 21: 神秘药水  
   - 22: 护盾生成器  
   - 23: 减速雪球  