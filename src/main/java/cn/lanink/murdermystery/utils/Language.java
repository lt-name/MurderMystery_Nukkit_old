package cn.lanink.murdermystery.utils;

public class Language {

    public String cmdHelp = "§a查看帮助：/%cmdName% help";
    public String userHelp = "§eMurderMystery--命令帮助 \n " +
            "§a/%cmdName% §e打开ui \n " +
            "§a/%cmdName% join 房间名称 §e加入游戏 \n " +
            "§a/%cmdName% quit §e退出游戏 \n " +
            "§a/%cmdName% list §e查看房间列表";
    public String noPermission = "§c你没有权限！";
    public String joinRoom = "§a你已加入房间: %name%";
    public String joinRoomOnRoom = "§c你已经在一个房间中了!";
    public String joinRoomOnRiding = "§a请勿在骑乘状态下进入房间！";
    public String joinRandomRoom = "§a已为你随机分配房间！";
    public String joinRoomIsPlaying = "§a该房间正在游戏中，请稍后";
    public String joinRoomIsFull = "§a该房间已满人，请稍后";
    public String joinRoomIsNotFound = "§a该房间不存在！";
    public String joinRoomNotAvailable = "§a暂无房间可用！";
    public String quitRoom = "§a你已退出房间";
    public String quitRoomNotInRoom = "§a你本来就不在游戏房间！";
    public String listRoom = "§e房间列表： §a %list%";
    public String useCmdInCon = "请在游戏内输入！";
    public String adminHelp = "§eMurderMystery--命令帮助 \n " +
                              "§a/%cmdName% §e打开ui \n " +
                              "§a/%cmdName% 设置出生点 §e设置当前位置为游戏出生点 \n " +
                               "§a/%cmdName% 添加金锭生成点 §e将当前位置设置为金锭生成点 \n " +
                                "§a/%cmdName% 设置金锭产出间隔 数字 §e设置金锭生成间隔 \n " +
                                "§a/%cmdName% 设置等待时间 数字 §e设置游戏人数足够后的等待时间 \n " +
                                "§a/%cmdName% 设置游戏时间 数字 §e设置每轮游戏最长时间 \n " +
                                "§a/%cmdName% reload §e重载所有房间 \n " +
                               "§a/%cmdName% unload §e关闭所有房间,并卸载配置";
    public String adminSetSpawn = "§a默认出生点设置成功！";
    public String adminAddRandomSpawn = "§a随机出生点添加成功！";
    public String adminAddGoldSpawn = "§a金锭生成点添加成功！";
    public String adminNotNumber = "§a时间只能设置为正整数！";
    public String adminSetGoldSpawnTime = "§a金锭产出间隔已设置为： %time%";
    public String adminSetWaitTime = "§a等待时间已设置为：%time%";
    public String adminSetGameTime = "§a游戏时间已设置为：%time%";
    public String adminSetGameTimeShort = "§a游戏时间最小不能低于1分钟！";
    public String adminReload = "§a配置重载完成！请在后台查看信息！";
    public String adminUnload = "§a已卸载所有房间！请在后台查看信息！";
    public String roomSafeKick = "\n§c房间非正常关闭!\n为了您的背包安全，请稍后重进服务器！";
    public String playerDeathChat = "§c[死亡] %player% + §b >>> %message%";

    public String itemDetectiveBow = "§e侦探之弓";
    public String itemKillerSword = "§c杀手之剑";
    public String itemScan = "§c扫描器";
    public String itemQuitRoom = "§c退出房间";
    public String itemPotion = "§e神秘药水";
    public String itemShieldWall = "§a护盾生成器";
    public String itemSnowball = "§a减速雪球";

    public String useItemScan = "§a已显示所有玩家位置！";
    public String damageSnowball = "§a你被减速雪球打中了！";
    public String useItemSwordCD = "§a飞剑冷却中";
    public String useItemScanCD = "§a定位冷却中";
    public String exchangeItemsOnlyOne = "你只能携带一个 %name%";
    public String exchangeItem = "§a成功兑换到一个 %name%";
    public String exchangeUseGold = "§a需要使用金锭兑换 %name%";

    public String commonPeople = "平民";
    public String killer = "杀手";
    public String detective = "侦探";
    public String death = "死亡";

    public String killPlayer = "§a你成功击杀了一位玩家！";
    public String killKiller = "§a你成功击杀了杀手！";
    public String deathTitle = "§c死亡";
    public String deathByKillerSubtitle = "§c你被杀手杀死了";
    public String deathByDamageTeammateSubtitle = "§c你击中了队友";
    public String deathByTeammateSubtitle = "§c你被队友打死了";
    public String killerDeathSubtitle = "§c你被击杀了";
    public String titleCommonPeopleTitle = "§a平民";
    public String titleCommonPeopleSubtitle = "活下去，就是胜利";
    public String titleDetectiveTitle = "§e侦探";
    public String titleDetectiveSubtitle = "找出杀手，并用弓箭击杀他";
    public String titleKillerTitle = "§c杀手";
    public String titleKillerSubtitle = "杀掉所有人";
    public String killerGetSwordTime = "§e杀手将在 %time% 秒后拿到剑！";
    public String killerGetSword = "§e杀手已拿到剑！";
    public String titleVictoryKillerTitle = "§a杀手获得胜利！";
    public String titleVictoryCommonPeopleSubtitle = "§a平民和侦探获得胜利！";
    public String victoryMoney = "§a你获得了胜利奖励: %money% 元";
    public String victoryKillKillerMoney = "§a你获得了击杀杀手额外奖励: %money% 元！";

    public String waitTimeScoreBoard = " 玩家: §a %playerNumber%/16 \n §a开始倒计时： §l§e %time%";
    public String waitScoreBoard = " 玩家: §a %playerNumber%/16 \n 最低游戏人数为 5 人 \n 等待玩家加入中";
    public String waitTimeBottom = "§a当前已有: %playerNumber% 位玩家 \n §a游戏还有: %time% 秒开始！";
    public String waitBottom = "§c等待玩家加入中,当前已有: %playerNumber% 位玩家";

}
