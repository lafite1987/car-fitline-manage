/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50618
Source Host           : localhost:3306
Source Database       : car-fitline-manage

Target Server Type    : MYSQL
Target Server Version : 50618
File Encoding         : 65001

Date: 2017-06-12 22:40:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS `dict`;
CREATE TABLE `dict` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL DEFAULT '' COMMENT '字典类型',
  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
  `parentId` int(11) NOT NULL DEFAULT '0' COMMENT '父节点ID',
  `parentIdPath` varchar(200) NOT NULL DEFAULT '' COMMENT '父节点路径',
  `remark` varchar(100) NOT NULL DEFAULT '' COMMENT '备注',
  `seq` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '顺序号',
  `updateTime` bigint(20) NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_pid` (`type`,`parentId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典表';

-- ----------------------------
-- Records of dict
-- ----------------------------

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `type` int(4) DEFAULT NULL COMMENT '菜单所属分类，1为内勤人员功能菜单，2为外勤人员功能菜单',
  `parentId` bigint(11) NOT NULL COMMENT '父级菜单ID',
  `parentIdPath` varchar(500) NOT NULL COMMENT '父级菜单ID串联，便于查询，格式：$1$2$',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单链接',
  `orderNo` int(11) DEFAULT '0' COMMENT '排序',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `state` int(2) DEFAULT '1' COMMENT '数据状态',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `onMenu` tinyint(4) DEFAULT '1' COMMENT '是否显示在菜单',
  `icon` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES ('1', '清能管理平台', '1', '-1', '$', '', '0', '清能管理平台', '1', '2013-01-21 14:19:22', '1', null);
INSERT INTO `menu` VALUES ('2', '系统管理', '1', '1', '$1$', '', '1', '用户和菜单的设置', '1', '2013-01-23 10:12:31', '1', 'icon-cogs');
INSERT INTO `menu` VALUES ('3', '用户管理', '1', '2', '$1$2$', '/manage/user/list', '1', '系统用户管理', '1', '2013-01-23 10:15:27', '1', null);
INSERT INTO `menu` VALUES ('4', '菜单管理', '1', '2', '$1$2$', '/manage/menu/list', '2', '功能菜单管理', '1', '2013-01-23 10:17:34', '1', null);
INSERT INTO `menu` VALUES ('5', '角色管理', '1', '2', '$1$2$', '/manage/role/list', '3', '用户角色管理', '1', '2013-01-24 14:28:03', '1', null);
INSERT INTO `menu` VALUES ('9', '分配权限查看', '1', '5', '$1$2$5$', '/manage/role/privileges', '1', '', '1', '2015-10-16 00:14:17', '0', null);
INSERT INTO `menu` VALUES ('10', '分配权限提交', '1', '5', '$1$2$5$', '/manage/role/privileges/save', '2', null, '1', '2015-10-16 00:21:56', '0', null);
INSERT INTO `menu` VALUES ('11', '菜单详情', '1', '4', '$1$2$4$', '/manage/menu/detail', '1', null, '1', '2015-10-16 00:28:52', '0', null);
INSERT INTO `menu` VALUES ('12', '新增菜单提交', '1', '4', '$1$2$4$', '/manage/menu/add', '4', '', '1', '2015-10-16 00:29:56', '0', null);
INSERT INTO `menu` VALUES ('13', '修改菜单', '1', '4', '$1$2$4$', '/manage/menu/update', '3', null, '1', '2015-10-16 00:31:31', '0', null);
INSERT INTO `menu` VALUES ('16', '删除菜单', '1', '4', '$1$2$4$', '/manage/menu/del', '6', null, '1', '2015-10-16 00:36:38', '0', null);
INSERT INTO `menu` VALUES ('19', '新增用户提交', '1', '3', '$1$2$3$', '/manage/user/add', '2', null, '1', '2015-10-16 01:13:24', '0', null);
INSERT INTO `menu` VALUES ('20', '修改用户', '1', '3', '$1$2$3$', '/manage/user/detail', '3', null, '1', '2015-10-16 01:14:20', '0', null);
INSERT INTO `menu` VALUES ('21', '修改用户提交', '1', '3', '$1$2$3$', '/manage/user/update', '4', null, '1', '2015-10-16 01:14:36', '0', null);
INSERT INTO `menu` VALUES ('22', '删除用户', '1', '3', '$1$2$3$', '/manage/user/del', '5', null, '1', '2015-10-16 01:17:26', '0', null);
INSERT INTO `menu` VALUES ('24', '角色树形', '1', '5', '$1$2$5$', '/manage/role/api/tree', '3', null, '1', '2015-10-16 01:19:59', '0', null);
INSERT INTO `menu` VALUES ('25', '新增角色提交', '1', '5', '$1$2$5$', '/manage/role/add', '4', null, '1', '2015-10-16 01:20:46', '0', null);
INSERT INTO `menu` VALUES ('26', '修改角色', '1', '5', '$1$2$5$', '/manage/role/detail', '5', null, '1', '2015-10-16 01:21:27', '0', null);
INSERT INTO `menu` VALUES ('27', '修改角色提交', '1', '5', '$1$2$5$', '/manage/role/update', '6', null, '1', '2015-10-16 01:22:02', '0', null);
INSERT INTO `menu` VALUES ('28', '删除角色', '1', '5', '$1$2$5$', '/manage/role/del', '7', null, '1', '2015-10-16 01:23:42', '0', null);
INSERT INTO `menu` VALUES ('65', '用户列表', '1', '3', '$1$2$3$', '/manage/user/api/list', '7', '', '1', '2015-10-16 01:11:31', '0', '');
INSERT INTO `menu` VALUES ('66', '菜单树形', '1', '4', '$1$2$', '/manage/menu/api/list', '1', '', '1', '2013-01-23 10:17:34', '0', '');
INSERT INTO `menu` VALUES ('67', '分配角色查看', '1', '3', '$1$2$3$', '/manage/user/role/tree', '6', '', '1', '2017-01-01 21:24:41', '0', null);
INSERT INTO `menu` VALUES ('68', '分配角色提交', '1', '3', '$1$2$3$', '/manage/user/role/save', '7', '', '1', '2017-01-01 21:28:53', '0', null);
INSERT INTO `menu` VALUES ('69', '后台主页', '1', '-1', '$', '/manage/home', '1', '', '1', '2017-01-01 21:44:07', '0', null);
INSERT INTO `menu` VALUES ('76', '个人设置', '1', '69', '$69$', '/manage/user/personal', '11', '', '1', '2017-01-06 22:34:33', '0', null);
INSERT INTO `menu` VALUES ('77', '业务管理', '1', '1', '$1$', '', '2', '', '1', '2017-01-08 21:33:40', '1', null);
INSERT INTO `menu` VALUES ('78', '业务管理1', '1', '77', '$1$77$', '/manage/role/list', '1', '', '1', '2017-01-08 21:34:17', '1', null);
INSERT INTO `menu` VALUES ('79', '定时任务', '1', '2', '$1$2$', '/manage/scheduleJob/list', '4', '', '1', '2017-04-29 22:44:23', '1', null);
INSERT INTO `menu` VALUES ('80', '任务列表', '1', '79', '$1$2$79$', '/manage/scheduleJob/api/list', '1', '', '1', '2017-04-29 22:49:22', '0', null);
INSERT INTO `menu` VALUES ('81', '新增任务', '1', '79', '$1$2$79$', '/manage/scheduleJob/add', '2', '', '1', '2017-04-29 22:52:02', '0', null);
INSERT INTO `menu` VALUES ('82', '部署', '1', '79', '$1$2$79$', '/manage/scheduleJob/schedule', '3', '', '1', '2017-04-29 22:55:45', '0', null);
INSERT INTO `menu` VALUES ('83', '修改', '1', '79', '$1$2$79$', '/manage/scheduleJob/update', '4', '', '1', '2017-04-29 23:12:30', '0', null);
INSERT INTO `menu` VALUES ('84', '运行一次', '1', '79', '$1$2$79$', '/manage/scheduleJob/runOne', '5', '', '1', '2017-04-29 23:13:45', '0', null);
INSERT INTO `menu` VALUES ('85', '更新表达式', '1', '79', '$1$2$79$', '/manage/scheduleJob/updateCron', '6', '', '1', '2017-04-29 23:14:24', '0', null);
INSERT INTO `menu` VALUES ('86', '删除', '1', '79', '$1$2$79$', '/manage/scheduleJob/del', '7', '', '1', '2017-04-29 23:14:43', '0', null);
INSERT INTO `menu` VALUES ('87', '暂停', '1', '79', '$1$2$79$', '/manage/scheduleJob/pause', '8', '', '1', '2017-04-29 23:16:29', '0', null);
INSERT INTO `menu` VALUES ('88', '恢复', '1', '79', '$1$2$79$', '/manage/scheduleJob/resume', '9', '', '1', '2017-04-29 23:17:12', '0', null);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `level` tinyint(3) unsigned NOT NULL COMMENT '角色级别',
  `parentId` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '角色的父节点',
  `parentIdPath` varchar(255) DEFAULT NULL COMMENT '父节点路径',
  `name` varchar(50) DEFAULT NULL COMMENT '内勤管理角色名称',
  `type` int(4) DEFAULT NULL COMMENT '角色作用人员，1作用于内勤人员，2作用于外勤人员',
  `desc` varchar(200) DEFAULT NULL COMMENT '描述',
  `orderNo` int(11) DEFAULT '100',
  `popedom` int(4) DEFAULT NULL COMMENT '甜椒权限标识：甜椒为1001、1002等；权限值越小，权限越大，主要用于创建用户判断权限的大小',
  `projectId` bigint(20) DEFAULT '0',
  `state` int(2) DEFAULT '1' COMMENT '数据状态',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '1', '0', '$', '系统开发者', '1', '系统开发者', '1', null, '0', '1', '2013-01-21 14:24:33');
INSERT INTO `role` VALUES ('2', '2', '1', '$1$', '系统管理员', '1', '系统管理员', '2', null, '0', '1', '2013-01-21 14:33:17');
INSERT INTO `role` VALUES ('3', '3', '2', '$1$2$', '管理员', null, '管理员', '100', null, '0', '1', '2015-10-11 10:14:41');
INSERT INTO `role` VALUES ('4', '4', '3', '$1$2$3$', '账号管理员', null, '账号管理与电站管理', '100', null, '0', '1', '2015-12-07 21:26:55');
INSERT INTO `role` VALUES ('5', '5', '4', '$1$2$3$4$', '账号管理员子节点1', null, '账号管理员子节点1', '100', null, '0', '1', '2017-01-01 11:53:05');
INSERT INTO `role` VALUES ('9', '3', '2', '$1$2$', '管理角色', null, '管理角色', '100', null, '0', '1', '2017-01-05 22:32:34');
INSERT INTO `role` VALUES ('11', '4', '9', '$1$2$9$', '管理角色子节点', null, '管理角色子节点', '100', null, '0', '1', '2017-01-05 23:09:59');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu` (
  `roleId` bigint(20) NOT NULL DEFAULT '0' COMMENT '角色id',
  `menuId` bigint(20) NOT NULL DEFAULT '0' COMMENT '权限菜单id',
  PRIMARY KEY (`roleId`,`menuId`),
  UNIQUE KEY `idx_role_menu` (`roleId`,`menuId`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='角色对应权限表';

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES ('1', '1');
INSERT INTO `role_menu` VALUES ('1', '2');
INSERT INTO `role_menu` VALUES ('1', '3');
INSERT INTO `role_menu` VALUES ('1', '4');
INSERT INTO `role_menu` VALUES ('1', '5');
INSERT INTO `role_menu` VALUES ('1', '9');
INSERT INTO `role_menu` VALUES ('1', '10');
INSERT INTO `role_menu` VALUES ('1', '11');
INSERT INTO `role_menu` VALUES ('1', '12');
INSERT INTO `role_menu` VALUES ('1', '13');
INSERT INTO `role_menu` VALUES ('1', '16');
INSERT INTO `role_menu` VALUES ('1', '19');
INSERT INTO `role_menu` VALUES ('1', '20');
INSERT INTO `role_menu` VALUES ('1', '21');
INSERT INTO `role_menu` VALUES ('1', '22');
INSERT INTO `role_menu` VALUES ('1', '24');
INSERT INTO `role_menu` VALUES ('1', '25');
INSERT INTO `role_menu` VALUES ('1', '26');
INSERT INTO `role_menu` VALUES ('1', '27');
INSERT INTO `role_menu` VALUES ('1', '28');
INSERT INTO `role_menu` VALUES ('1', '65');
INSERT INTO `role_menu` VALUES ('1', '66');
INSERT INTO `role_menu` VALUES ('1', '67');
INSERT INTO `role_menu` VALUES ('1', '68');
INSERT INTO `role_menu` VALUES ('1', '69');
INSERT INTO `role_menu` VALUES ('1', '71');
INSERT INTO `role_menu` VALUES ('1', '74');
INSERT INTO `role_menu` VALUES ('1', '75');
INSERT INTO `role_menu` VALUES ('1', '76');
INSERT INTO `role_menu` VALUES ('1', '77');
INSERT INTO `role_menu` VALUES ('1', '78');
INSERT INTO `role_menu` VALUES ('1', '79');
INSERT INTO `role_menu` VALUES ('1', '80');
INSERT INTO `role_menu` VALUES ('1', '81');
INSERT INTO `role_menu` VALUES ('1', '82');
INSERT INTO `role_menu` VALUES ('1', '83');
INSERT INTO `role_menu` VALUES ('1', '84');
INSERT INTO `role_menu` VALUES ('1', '85');
INSERT INTO `role_menu` VALUES ('1', '86');
INSERT INTO `role_menu` VALUES ('1', '87');
INSERT INTO `role_menu` VALUES ('1', '88');
INSERT INTO `role_menu` VALUES ('2', '1');
INSERT INTO `role_menu` VALUES ('2', '2');
INSERT INTO `role_menu` VALUES ('2', '3');
INSERT INTO `role_menu` VALUES ('2', '5');
INSERT INTO `role_menu` VALUES ('2', '9');
INSERT INTO `role_menu` VALUES ('2', '10');
INSERT INTO `role_menu` VALUES ('2', '14');
INSERT INTO `role_menu` VALUES ('2', '15');
INSERT INTO `role_menu` VALUES ('2', '17');
INSERT INTO `role_menu` VALUES ('2', '19');
INSERT INTO `role_menu` VALUES ('2', '20');
INSERT INTO `role_menu` VALUES ('2', '21');
INSERT INTO `role_menu` VALUES ('2', '24');
INSERT INTO `role_menu` VALUES ('2', '25');
INSERT INTO `role_menu` VALUES ('2', '26');
INSERT INTO `role_menu` VALUES ('2', '27');
INSERT INTO `role_menu` VALUES ('2', '65');
INSERT INTO `role_menu` VALUES ('2', '67');
INSERT INTO `role_menu` VALUES ('2', '68');
INSERT INTO `role_menu` VALUES ('2', '69');
INSERT INTO `role_menu` VALUES ('3', '1');
INSERT INTO `role_menu` VALUES ('3', '2');
INSERT INTO `role_menu` VALUES ('3', '3');
INSERT INTO `role_menu` VALUES ('3', '19');
INSERT INTO `role_menu` VALUES ('3', '20');
INSERT INTO `role_menu` VALUES ('3', '21');
INSERT INTO `role_menu` VALUES ('3', '22');
INSERT INTO `role_menu` VALUES ('3', '65');
INSERT INTO `role_menu` VALUES ('3', '67');
INSERT INTO `role_menu` VALUES ('3', '68');
INSERT INTO `role_menu` VALUES ('4', '1');
INSERT INTO `role_menu` VALUES ('4', '2');
INSERT INTO `role_menu` VALUES ('4', '3');
INSERT INTO `role_menu` VALUES ('4', '19');
INSERT INTO `role_menu` VALUES ('4', '20');
INSERT INTO `role_menu` VALUES ('4', '21');
INSERT INTO `role_menu` VALUES ('4', '22');
INSERT INTO `role_menu` VALUES ('4', '65');
INSERT INTO `role_menu` VALUES ('4', '67');
INSERT INTO `role_menu` VALUES ('4', '68');
INSERT INTO `role_menu` VALUES ('5', '1');
INSERT INTO `role_menu` VALUES ('5', '2');
INSERT INTO `role_menu` VALUES ('5', '3');
INSERT INTO `role_menu` VALUES ('5', '19');
INSERT INTO `role_menu` VALUES ('5', '20');
INSERT INTO `role_menu` VALUES ('5', '21');
INSERT INTO `role_menu` VALUES ('5', '22');
INSERT INTO `role_menu` VALUES ('5', '65');
INSERT INTO `role_menu` VALUES ('5', '67');
INSERT INTO `role_menu` VALUES ('5', '68');
INSERT INTO `role_menu` VALUES ('7', '1');
INSERT INTO `role_menu` VALUES ('7', '2');
INSERT INTO `role_menu` VALUES ('7', '3');
INSERT INTO `role_menu` VALUES ('7', '19');
INSERT INTO `role_menu` VALUES ('7', '20');
INSERT INTO `role_menu` VALUES ('7', '21');
INSERT INTO `role_menu` VALUES ('9', '1');
INSERT INTO `role_menu` VALUES ('9', '2');
INSERT INTO `role_menu` VALUES ('9', '5');
INSERT INTO `role_menu` VALUES ('9', '9');
INSERT INTO `role_menu` VALUES ('9', '10');
INSERT INTO `role_menu` VALUES ('9', '24');
INSERT INTO `role_menu` VALUES ('9', '25');
INSERT INTO `role_menu` VALUES ('9', '26');
INSERT INTO `role_menu` VALUES ('9', '27');
INSERT INTO `role_menu` VALUES ('11', '1');
INSERT INTO `role_menu` VALUES ('11', '2');
INSERT INTO `role_menu` VALUES ('11', '5');
INSERT INTO `role_menu` VALUES ('11', '9');
INSERT INTO `role_menu` VALUES ('11', '10');
INSERT INTO `role_menu` VALUES ('11', '24');
INSERT INTO `role_menu` VALUES ('11', '25');
INSERT INTO `role_menu` VALUES ('11', '26');
INSERT INTO `role_menu` VALUES ('11', '27');

-- ----------------------------
-- Table structure for t_schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `t_schedule_job`;
CREATE TABLE `t_schedule_job` (
  `FJOBID` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务id,主键',
  `FJOB_NAME` varchar(256) NOT NULL DEFAULT '' COMMENT '任务名称',
  `FJOB_GROUP` varchar(256) NOT NULL DEFAULT '' COMMENT '任务分组',
  `FJOB_STATUS` varchar(32) NOT NULL DEFAULT 'NONE' COMMENT '任务状态 0:NONE, 1:NORMAL, 2:PAUSED, 3:COMPLETE, 4:ERROR, 5:BLOCKED',
  `FCRON_EXPRESSION` varchar(32) NOT NULL DEFAULT '' COMMENT '任务运行时间表达式',
  `FCONCURRENT` tinyint(4) NOT NULL DEFAULT '0' COMMENT '并行/串行标志位 1:并行,2:串行',
  `FDESC` varchar(700) NOT NULL DEFAULT '' COMMENT '任务描述',
  `FOPERATOR` int(11) NOT NULL DEFAULT '0' COMMENT '操作人员',
  `FOP_TIME` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间unix_timestamp格式 1411538492',
  `FIDC` varchar(16) DEFAULT '' COMMENT 'IDC_NS表示南沙机房:IDC_WX 无锡机房',
  `FISBOOT` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`FJOBID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='定时任务表';

-- ----------------------------
-- Records of t_schedule_job
-- ----------------------------
INSERT INTO `t_schedule_job` VALUES ('1', 'testTask', 'testTask', 'PAUSED', '*/5 * * * * ?', '0', '测试', '0', '1493477697', 'IDC_DEFAULT', '0');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(20) DEFAULT NULL COMMENT '登录名',
  `password` varchar(256) DEFAULT NULL COMMENT '密码',
  `salt` varchar(32) DEFAULT '' COMMENT '盐',
  `nickname` varchar(20) DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) DEFAULT NULL COMMENT '公司的个人邮箱，邮件提醒功能',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `state` int(2) DEFAULT NULL COMMENT '数据状态',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'dev', '31587b849f0a7e4e229af7ab742a1b634e1907d81abfa866bc9ac49f9d63da3c', '834ec6', '开发者', '', '18028763997', '1', '2013-01-23 17:33:24');
INSERT INTO `user` VALUES ('2', 'admin', '0c75f68693dfad5b3ef8d6581b21f5130fe312285821ad7f79e405b17dbed1e5', 'b9ecdc', '系统管理员', '', '12345678909', '1', '2013-01-23 17:33:46');
INSERT INTO `user` VALUES ('3', 'qnyk123', '77dda7c14c66ac20746997d066f479141ca22e526bd9f836fac06719a21beda2', 'f2ac4e', '清能云控', '', '18028763997', '1', '2015-10-10 22:59:59');
INSERT INTO `user` VALUES ('5', 'qnyk1234', '70504ff24b385392e82f3247d4d28788c18aa56df34d24c4b4c5798c47aa1274', 'd99193', '李', '', '13430909666', '1', '2015-11-25 01:08:33');
INSERT INTO `user` VALUES ('6', 'ewang', '812cd692cb0749c3bfaa3985400ff87ec9401a8c3fbda32568652abca17ae999', '7e44d7', 'Eric', '', '13810734019', '1', '2015-12-03 02:30:24');
INSERT INTO `user` VALUES ('7', 'ewang2', 'f81b3385baa4a7a776dc33324f2d61df9fea3f368e6f34e13ad6eedd7ee00a9b', '1bf5a1', 'Eric', '', '13810734019', '1', '2015-12-03 03:18:16');
INSERT INTO `user` VALUES ('9', 'qnyk2', '2e9f3cacb346e911feba8bf0fbf3d7ed7faa72082cacb175da99d67a1c02de7a', 'c88f1c', '电站管理员2', '', '12345678909', '1', '2015-12-06 21:58:55');
INSERT INTO `user` VALUES ('10', 'qnyk66', 'fcdf6a46d0095d4237daed3a5573d7174e314d1c7f32674a18d110cdf98b8497', '422528', '账号管理员', '', '12345678909', '1', '2015-12-07 21:27:52');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `roleId` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_role` (`userId`,`roleId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('1', '1', '1');
INSERT INTO `user_role` VALUES ('25', '2', '2');
INSERT INTO `user_role` VALUES ('33', '2', '11');
INSERT INTO `user_role` VALUES ('36', '3', '4');
INSERT INTO `user_role` VALUES ('35', '3', '9');
