<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
    <title>后台管理</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="keywords" content=""/>
    <meta name="description" content=""/>
    <link href="<%=basePath%>resources/css/bootstrap.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath%>resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath%>resources/css/dataTables.bootstrap.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath%>resources/css/common.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath%>resources/css/jquery-ui.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath%>resources/css/asyncbox.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath%>resources/css/box.css" rel="stylesheet" type="text/css" />
    <link type="text/css" rel="stylesheet" href="<%=basePath %>resources/plugins/zTree/metroStyle.css"/>
    <style type="text/css" media="screen">
        table td { overflow: visible !important; }    
    </style>
</head>
<body>
<div class="panel_con main_con" >
    <div class="current_position">
        <ol>
            <li class="glyphicon glyphicon-folder-open icon_home"></li>
            <li>当前位置：</li>
            <li><a href="#">定时任务</a>&gt; </li>
            <li class="active">定时任务</li>
        </ol>
    </div>
    <div class="panel panel-default main_contents">
        <div class="panel-body search_list">
            <div class="btn-group idc_dropDown pull-left">
                <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="idc" value="IDC_DEFAULT">默认机房</i></button>
                <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>
                <ul class="dropdown-menu dropDown_idc_list scrollBar">
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="IDC_DEFAULT">默认机房</a></li>
                </ul>
                <input type="hidden" value="IDC_DEFAULT" name="idc"/>
            </div>
            <button class="btn btn-primary add_dialog applic_btn" data-toggle="modal" data-target="#myModal">新增任务</button>
        </div>
        <table id="example" class="display custom-table fixed_table" cellspacing="0" width="100%">
            <thead>
            <tr>
<!--                 <th width="50">序号</th> -->
                <th width="60">任务id</th>
                <th>任务名称</th>
                <th>任务分组</th>
                <th width="90">任务状态</th>
                <th width="150">CRON</th>
                <th>任务描述</th>
                <th width="70">串/并行</th>
                <th>开机启动</th>
                <th width="110">操作时间</th>
                <th width="220">操作</th>
            </tr>
            </thead>
            <tbody id="table"></tbody>
        </table>
    </div>
</div>
</div>
<!-- 弹出框 -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">修改</h4>
            </div>
            <div class="modal-body">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary J_sure">确定</button>
                <button type="button" class="btn btn-primary J_add_sure none">确定</button>
                <button type="button" class="btn btn-primary j_delete_sure none">确定</button>
                <button type="button" class="btn btn-primary j_modify_sure none">确定</button>
                <button type="button" class="btn btn-primary j_update_sure none">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>
[#include "manage/common/foot.htm"]
<script id="modifyDialog" type="text/x-jquery-tmpl">
<div class="modify">
    <table cellspacing="0" width="100%" class="modifyTable labelTable">
        <tr class="labelId_style">
            <th>任务名称：</th>
            <td>
                <input type="text" class="form-control input_common" name="taskName" />
            </td>
        </tr>
        <tr>
            <th>任务分组：</th>
            <td>
                <input type="text" class="form-control input_common" name="taskGroup" />
            </td>
        </tr>
        <tr>
            <th>任务描述：</th>
            <td>
                <div class="form-group">
                    <textarea class="form-control input_common" id="description" rows="3" placeholder="" name="description" max-length="1024"></textarea>
                </div>
            </td>
        </tr>
        <tr>
            <th>开机启动：</th>
            <td>
                <div class="btn-group boot_dropDown pull-left">
                    <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="boot" name="boot" value="0">否</i></button>
                    <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                        <span class="caret"></span>
                        <span class="sr-only">Toggle Dropdown</span>
                    </button>
                    <ul class="dropdown-menu dropDown_boot_list scrollBar">
                        <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="1">是</a></li>
                        <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="0">否</a></li>
                    </ul>
                    <input type="hidden" value="0" name="boot"/>
                </div>
            </td>
        </tr>
        <tr>
            <th>串/并行：</th>
            <td>
                <div class="btn-group first_dropDown">
                    <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="concurrent" value="false">串行</i></button>
                    <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                        <span class="caret"></span>
                        <span class="sr-only">Toggle Dropdown</span>
                    </button>
                    <ul class="dropdown-menu dropDown_concurrent_list2 scrollBar">
                        <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="true">并行</a></li>
                        <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="false">串行</a></li>
                    </ul>
                    <input type="hidden" value="false" name="concurrent"/>
                </div>
            </td>
        </tr>
        <tr>
            <th>机房：</th>
            <td>
                <div class="btn-group idc_dropDown1 pull-left">
                    <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="idc1" value="IDC_DEFAULT">默认机房</i></button>
                    <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                        <span class="caret"></span>
                        <span class="sr-only">Toggle Dropdown</span>
                    </button>
                    <ul class="dropdown-menu dropDown_idc_list scrollBar">
                        <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="IDC_DEFAULT">默认机房</a></li>
                    </ul>
                    <input type="hidden" value="IDC_DEFAULT" name="idc1"/>
                </div>
            </td>
        </tr>
    </table>
</div>
</script>
<script id="modeDialog" type="text/x-jquery-tmpl">
  <div class="modify">
  <table cellspacing="0" width="100%" class="modifyTable labelTable">
    <tr class="labelId_style">
        <th>任务名称：</th>
        <td>
            <input type="text" class="form-control input_common" name="taskName" />
        </td>
    </tr>
    <tr>
        <th>任务分组：</th>
        <td>
            <input type="text" class="form-control input_common" name="taskGroup" />
        </td>
    </tr>
    <tr>
        <th>任务状态：</th>
        <td>
            <div class="btn-group first_dropDown">
                <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="dropDown_status2" value="NONE">初始化</i></button>
                <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>
                <ul class="dropdown-menu dropDown_status_list2 scrollBar">
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="NONE">初始化 </a></li>
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="NORMAL">正常</a></li>
                    <!--//jianbin 新增时不需要该状态
					<li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="PAUSED">暂停</a></li>-->
                </ul>
                <input type="hidden" value="0" name="status"/>
            </div>
        </td>
    </tr>
    <tr>
        <th>开机启动：</th>
        <td>
            <div class="btn-group boot_dropDown pull-left">
                <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="boot" value="0">否</i></button>
                <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>
                <ul class="dropdown-menu dropDown_boot_list scrollBar">
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="1">是</a></li>
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="0">否</a></li>
                </ul>
                <input type="hidden" value="0" name="boot"/>
            </div>
        </td>
    </tr>
    <tr>
        <th>CRON：</th>
        <td>
            <input type="text" class="form-control input_common" name="cronExpression" />
        </td>
    </tr>
     <tr>
        <th>串/并行：</th>
        <td>
            <div class="btn-group first_dropDown">
                <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="concurrent" value="false">串行</i></button>
                <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>
                <ul class="dropdown-menu dropDown_concurrent_list2 scrollBar">
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="true">并行</a></li>
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="false">串行</a></li>
                </ul>
                <input type="hidden" value="false" name="concurrent"/>
            </div>
        </td>
    </tr>
    <tr>
        <th>机房：</th>
        <td>
            <div class="btn-group idc_dropDown1 pull-left">
                <button type="button" class="btn btn-default btn-default-disable dropDown-style"><i id="idc1" value="IDC_DEFAULT">默认机房</i></button>
                <button type="button" class="btn btn-default dropdown-toggle  btn-default-disable adv_list2" data-toggle="dropdown">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>
                <ul class="dropdown-menu dropDown_idc_list scrollBar">
                    <li role="presentation"><a href="javascript:;" tabindex="-1" role="menuitem" value="IDC_DEFAULT">默认机房</a></li>
                </ul>
                <input type="hidden" value="IDC_DEFAULT" name="idc1"/>
            </div>
        </td>
    </tr>
    <tr>
        <th>任务描述：</th>
        <td>
            <div class="form-group">
                <textarea class="form-control input_common" id="description" rows="3" placeholder="" name="description" max-length="1024"></textarea>
            </div>
        </td>
    </tr>
  </table>
  </div>
</script>
<script id="updateCRON" type="text/x-jquery-tmpl">
<div class="modify">
    <table cellspacing="0" width="100%" class="modifyTable labelTable">
        <tr class="labelId_style">
            <th>CRON：</th>
            <td>
             <input type="text" class="form-control input_common" name="cronExpression" />
            </td>
        </tr>
    </table>
</div>
</script>
<script src="<%=basePath%>resources/js/common/common.js"></script>
<script src="<%=basePath%>resources/js/common/bootstrap.js"></script>
<script src="<%=basePath%>resources/plugins/zTree/jquery.ztree.all.js"></script>
<script src="<%=basePath%>resources/js/common/bootstrap-typeahead.js"></script>
<script src="<%=basePath%>resources/js/common/jquery.dataTables.js"></script>
<script src="<%=basePath%>resources/js/common/dataTables.bootstrap.js"></script>
<script src="<%=basePath%>resources/js/common/jquery-asyncbox.js"></script>
<script src="<%=basePath%>resources/js/common/jquery-ui.min.js"></script>
<script src="<%=basePath%>resources/js/common/public.js"></script>
<script src="<%=basePath %>/resources/js/common/sha256.min.js"></script>
<script src="<%=basePath%>resources/js/scheduler.js"></script>
</body>
</html>
