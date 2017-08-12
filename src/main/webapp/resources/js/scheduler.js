var scheduler = {
    num: 0,
    idcMap: {
        IDC_DEFAULT: '默认机房'
    },
    init: function () {
        this.loadTable();
        this.bindEvents();
    },
    loadTable: function () {
        var self = this, argument;
        var action = "/manager/scheduleJob/api/list";
        argument = [
           {"name": "idc", "value": $('#idc').attr('value')}
        ];
        //初始化数据
        self.loading(action, argument, true);
    },
    loading: function (action, argument, refresh) {
        var self = this;
        oTable.dataTable(action, argument, self.insertResult, {refresh: refresh});
    },
    bindEvents: function () {
        var self = this;
        //修改
        $('#table').delegate('.dialog-modify', 'click', function () {
        	$('.modal-dialog').css({'width':"620"});
            $('.modal-dialog .modal-body').css({'height':'430'});
            $('.modal-body').html($("#modifyDialog").tmpl());
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".j_modify_sure").removeClass("none");
            $('#myModalLabel').text("修改");
            var taskName, desc,taskGroup,concurrent;
            var td = $(this).parent().parent().parent("td").parent("tr").children("td");
            taskName = td.eq(1).text();
            taskGroup = td.eq(2).text();
            desc = td.eq(5).text();
            concurrent = td.eq(6).text();
            boot = td.eq(7).text();
            $("input[name =taskName]").attr('value',taskName);
            $("input[name =taskGroup]").attr('value',taskGroup);
            $('input[name="idc1"]').attr('value',$('#idc').attr('value'));
            $('input[name="boot"]').attr('value', boot == '是' ? 1 : 0);
            $("#description").text(desc);
            $('#concurrent').text(concurrent);
            $('#idc1').text(self.idcMap[$('#idc').attr('value')]);
            $('#boot').text(boot);
            if(concurrent == '并行'){
                $('#concurrent').attr('value', 'true');
            }else{
                $('#concurrent').attr('value', 'false');
            }
            self.dropDown('dropDown_concurrent_list2', 'concurrent', 'concurrent');
            self.dropDown('idc_dropDown1', 'idc1', 'idc1');
            self.dropDown('boot_dropDown', 'boot', 'boot');
            var jobId = $(this).attr("data-value");
            //确认修改
            scheduler.modifySure(jobId);
        });
        //新增任务调度
        $('.add_dialog').click(function () {
        	$('.modal-dialog').css({'width':"620"});
            $('.modal-dialog .modal-body').css({'height':'550'});
            $('.modal-body').html($("#modeDialog").tmpl());
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_add_sure").removeClass("none");
            $('#myModalLabel').text("新增任务调度");
            self.dropDown('dropDown_status_list2', 'dropDown_status2', 'status');
            self.dropDown('dropDown_concurrent_list2', 'concurrent', 'concurrent');
            self.dropDown('idc_dropDown1', 'idc1', 'idc1');
            self.dropDown('boot_dropDown', 'boot', 'boot');
            //确认增加
            scheduler.addSure();
        });
        //删除
        $('#table').delegate('.dialog-delete', 'click', function () {
            $('.modal-body').empty().html("确定要删除吗？");
            $('.modal-dialog .modal-body').css({'height':''});
            $('#myModalLabel').text("删除");
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".j_delete_sure").removeClass("none");
            //删除
            var jobId = $(this).attr("data-value");
            scheduler.deleteSure(jobId);
        }).delegate('.dialog-confirm','click',function(){
            var self = this;
            var status = $(this).attr("data-value"),
                jobId = $(this).attr("data-id"), jobName = $(this).attr("data-jobName");
            if(status == 'NONE'){//NONE的任务现在只可以操作:部署,状态改为NORMAL并开始按cron运行
                $.get('/manager/scheduleJob/schedule',{"jobId": jobId,"jobName": jobName, "jobStatus": 'NORMAL', 'idc': $('#idc').attr('value')},function(result){
                    if(result.code == 200){
                        $(self).text("任务暂停").attr('data-value','NORMAL');
                        var action = "/manager/scheduleJob/api/list";
                        var argument = [];
                        //重新加载数据
                        scheduler.loading(action, argument);
                    }else{
                        asyncbox.alert("部署失败！"+result.msg,"提示");
                    }
                });
            }else if(status == 'NORMAL'){
                $.get('/manager/scheduleJob/pause',{"jobId": jobId, idc: $('#idc').attr('value')},function(result){
                    if(result.code == 200){
                        $(self).text("任务恢复").attr('data-value','PAUSED');
                        var action = "/console/scheduler/api/list";
                        var argument = [
                            {"name": "idc", "value": $('#idc').attr('value')}
                        ];
                        //重新加载数据
                        scheduler.loading(action, argument);
                    }else{
                        asyncbox.alert("暂停失败！"+result.message,"提示");
                    }
                });

            }else if(status == 'PAUSED'){
                $.get('/manager/scheduleJob/resume',{"jobId": jobId, idc: $('#idc').attr('value')},function(result){
                    if(result.code == 200){
                        $(self).text("任务暂停").attr('data-value','NORMAL');
                        var action = "/manager/scheduleJob/api/list";
                        var argument = [
                            {"name": "idc", "value": $('#idc').attr('value')}
                        ];
                        //重新加载数据
                        scheduler.loading(action, argument);
                    }else{
                        asyncbox.alert("恢复失败！"+result.msg,"提示");
                    }
                });
            }else{
                asyncbox.alert("此状态不可更改！","提示");
            }
        }).delegate('.dialog-trigger','click',function(){//jianbin
        	var jobId = $(this).attr("data-value");
        	$.get("/manager/scheduleJob/runOne", {"jobId": jobId, 'idc': $('#idc').attr('value')}, function (result) {
                if (result.code == 200) {
                    asyncbox.alert("运行成功","提示");
                    $('#myModal').modal('hide');
                    var action = "/manager/scheduleJob/api/list";
                    var argument = [
                        {"name": "idc", "value": $('#idc').attr('value')}
                    ];
                    //重新加载数据
                    scheduler.loading(action, argument);
                } else {
                	asyncbox.alert("运行失败: " + result.msg,"提示");
                    //asyncbox.alert("运行失败","提示");
                    $('#myModal').modal('hide');
                }
            });
            //scheduler.fireNowSure(jobId);
        }).delegate('.dialog-update','click',function(){
            $('.modal-dialog').css({'width':"620"});
            $('.modal-body').html($("#updateCRON").tmpl());
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".j_update_sure").removeClass("none");
            $('#myModalLabel').text("更新时间表达式");
            $('.modal-dialog .modal-body').css({'height':''});
            var cronExpression = $(this).attr("data-text");
            $("input[name =cronExpression]").attr('value',cronExpression);
            var jobId = $(this).attr("data-value");
            //确认更新
            scheduler.updateSureCRON(jobId);
        });

        self.dropDown('idc_dropDown', 'idc', 'idc', function() {
            self.loadTable();
        });
    },
    insertResult: function (result,currentPage) {
        var arr = [], opera, n,statusText,concurrentText,statusOpera,leaf;
        n = 0;
        statusOpera = "";
        var value = result.data.list;
        for (var i = 0; i < value.length; i++) {
            scheduler.num = (i+1)+(currentPage*10);
            n=i+1;
            var status = value[i].jobStatus,concurrent =value[i].concurrent;
            //NONE：初始化　NORMAL：正常,　PAUSED：暂停,　COMPLETE：完成,　ERROR：错误,　BLOCKED：堵塞
            if(status == 'NONE'){
                statusText ="初始化";
                statusOpera = '部署';
            }else if(status == 'NORMAL'){
                statusText ="正常";
                statusOpera = '任务暂停';
            }else if(status == 'PAUSED'){
                statusText ="暂停";
                statusOpera = '任务恢复';
            }else if(status == 'COMPLETE'){
                statusText ="完成";
            }else if(status == 'ERROR'){
                statusText ="错误";
            }else{
                statusText ="堵塞";
            }
            //1:并行,2:串行
            if(concurrent == true){
                concurrentText = '并行';
            }else{
                concurrentText = '串行';
            }
            leaf = '<ul class="dropdown-menu col-scrollBar" role="menu">'+
            		'<li><a href="javascript:void(0);" class="dialog-trigger" data-toggle="modal" data-target="#myModal" data-jobName="' + value[i].jobName + '" data-value="'+value[i].jobId+'">马上运行</a></li>'+
            		'<li><a href="javascript:void(0);" class="dialog-update" data-toggle="modal" data-target="#myModal" data-value="'+value[i].jobId+'" data-text="'+value[i].cronExpression+'">更新CRON</a></li>'+
                    '<li><a href="javascript:void(0);" class="dialog-delete" data-toggle="modal" data-target="#myModal" data-value="'+value[i].jobId+'">删除</a></li>';
            
            opera= '<ul class="nav navbar-nav navbar-more">'+
                    '<li><a href="javascript:void(0);" class="dialog-confirm" " data-jobName="' + value[i].jobName + '" data-id="'+value[i].jobId+'" data-value="'+value[i].jobStatus+'">'+statusOpera+'</a></li>'+
                    '<li><a href="javascript:void(0);" class="dialog-modify" data-toggle="modal" data-target="#myModal" data-id="'+value[i].jobId+'" data-value="'+value[i].jobId+'">修改</a></li>'+
                    '<li class="dropdown">'+
                    '<a href="#" class="dropdown-toggle" data-toggle="dropdown">更多<span class="caret"></span></a>'+leaf+'</li></ul>';

            var opTime = new Date(value[i].opTime*1000);
            value[i].opTime = opTime.format("yyyy-MM-dd");
            arr.push([//scheduler.num,
                      value[i].jobId, '<div class="text_l col-num-2" title="'+value[i].jobName+'">'+ value[i].jobName +'</div>',
                      '<div class="text_l col-num-2" title="'+value[i].jobGroup+'">'+ value[i].jobGroup +'</div>',statusText,
                      '<div class="text_l col-num-2" title="'+value[i].cronExpression+'">'+ value[i].cronExpression +'</div>',
                      '<div class="text_l col-num-2" title="'+value[i].desc+'">'+ value[i].desc +'</div>',concurrentText,value[i].isBoot == true ? '是' : '否',value[i].opTime,opera]);
        }
        self.num++;
        result.draw = self.num;
        result.recordsTotal = result.data.totalResult;
        result.recordsFiltered = result.data.totalResult;
        result.data = arr;
    },
    addSure: function () {
        $(".J_add_sure").unbind('click');
        $(".J_add_sure").click(function () {
            var jobName, jobGroup, jobStatus, cronExpression, concurrent, desc, idc, boot;
            jobName = $("input[name =taskName]").val();
            jobGroup = $("input[name =taskGroup]").val();
            cronExpression = $("input[name =cronExpression]").val();
            jobStatus = $("#dropDown_status2").attr("value");
            concurrent = $("#concurrent").attr("value");
            desc = $("#description").val();
            idc = $("#idc1").attr('value');
            boot = $('#boot').attr('value');
            $.get("/manager/scheduleJob/add", {"jobName": jobName, "jobGroup": jobGroup, "jobStatus": jobStatus, "cronExpression": cronExpression, "concurrent": concurrent, "desc": desc, "idc": idc, "boot": boot}, function (result) {
                if (result.code == 200) {
                    $('#myModal').modal('hide');
                    var action = "/manager/scheduleJob/api/list";
                    var argument = [
                        {"name": "idc", "value": $('#idc').attr('value')}
                    ];
                    scheduler.loading(action, argument);
                } else {
                    asyncbox.alert(result.message,"提示");
                }
            });
        });
    },
    modifySure: function (jobId) {
        $(".j_modify_sure").unbind('click');
        $(".j_modify_sure").click(function () {
            var desc = $("#description").val(),
                jobName =$("input[name =taskName]").val(),
                jobGroup =$("input[name =taskGroup]").val(),
                concurrent = $("#concurrent").attr('value'),
                idc = $('#idc1').attr('value'),
                boot = $('input[name="boot"]').attr('value');
            $.get("/manager/scheduleJob/update", {"jobId":jobId,"desc": desc,"jobName":jobName,"jobGroup":jobGroup,"concurrent":concurrent, "idc":idc, "boot": boot}, function (result) {
                if (result.code == 200) {
                    $('#myModal').modal('hide');
                    var action = "/manager/scheduleJob/api/list";
                    var argument = [
                        {"name": "idc", "value": $('#idc').attr('value')}
                    ];
                    //重新加载数据
                    scheduler.loading(action, argument);
                } else {
                    asyncbox.alert("修改失败！\n" + result.msg,"提示");
                }
            });
        });
    },
    deleteSure: function (jobId) {
        $(".j_delete_sure").unbind('click');
        $(".j_delete_sure").click(function () {
            $.get("/manager/scheduleJob/del", {"jobId": jobId, 'idc': $('#idc').attr('value')}, function (result) {
                if (result.code == 200) {
                    $('#myModal').modal('hide');
                    var action = "/manager/scheduleJob/api/list";
                    var argument = [
                        {"name": "idc", "value": $('#idc').attr('value')}
                    ];
                    //重新加载数据
                    scheduler.loading(action, argument);
                } else {
                    asyncbox.alert("删除失败","提示");
                    $('#myModal').modal('hide');
                }
            });
        });
    },
    updateSureCRON:function(jobId){
        $(".j_update_sure").unbind('click');
        $(".j_update_sure").click(function () {
            var cronExpression =$("input[name =cronExpression]").val();
            $.get("/manager/scheduleJob/updateCron", {"jobId":jobId,"cronExpression":cronExpression, 'idc': $('#idc').attr('value')}, function (result) {
                if (result.code == 200) {
                    $('#myModal').modal('hide');
                    var action = "/manager/scheduleJob/api/list";
                    var argument = [
                        {"name": "idc", "value": $('#idc').attr('value')}
                    ];
                    //重新加载数据
                    scheduler.loading(action, argument);
                } else {
                    asyncbox.alert("修改失败！\n" + result.msg,"提示");
                }
            });
        });
    },
    dropDown: function (id, text, inp, cb) {
        $('.' + id).delegate('li a', 'click', function () {
            $("#" + text).text($(this).text());
            var value = $(this).attr('value');
            $("input[name =" + inp + "]").attr("value", value);
            $("#" + text).attr("value", value);
            cb && cb(value);
        });
    }

};
$(function(){
    scheduler.init();
});