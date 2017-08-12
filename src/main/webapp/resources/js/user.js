var users = {
    sunNum : 0,
    State : {"-1" : "删除", 0 : "禁用", 1 : "有效"},
    init: function () {
        this.bindEvents();
        this.loadTable();
    },
    loadTable: function () {
        var self = this;
        self.list(true);
    },
    insertResult: function (result,currentPage) {
        var arr = [], $opera;
        if (result.code != 200) {
            return;
        }
        var value = result.data.list;
        for (var i = 0; i < value.length; i++) {
            var createTime = new Date(value[i].createTime);
            value[i].createTime = createTime.format("yyyy-MM-dd hh:mm:ss");
            var stateText = users.State[value[i].state];
            users.sunNum = (i+1)+(currentPage * PAGE_SIZE);
            $opera = '<a href="#" class="operation J_delete" data-toggle="modal" data-target="#myModal" data-value=' + value[i].id + '>删除</a>' + 
            	'<a href="#" class="operation J_strategyInfo" data-toggle="modal" data-target="#myModal" data-value=' + value[i].id + '>详情</a>'+
                '<a href="#" class="operation dialog-editor" data-toggle="modal" data-target="#editorDialog"  data-value=' + value[i].id + '>编辑</a>' + 
                '<a href="#" class="operation dialog-roles" data-toggle="modal" data-target="#rolesDialog"  data-value=' + value[i].id + '>分配角色</a>' +
                '</td>';
            arr.push([users.sunNum,value[i].id,value[i].username,value[i].nickname, '<div class="text_l">'+ value[i].phone +'</div>', stateText,value[i].createTime, $opera]);
        }
        self.num++;
        result.draw = self.num;
        result.recordsTotal = result.data.totalResult;
        result.recordsFiltered = result.data.totalResult;
        result.data = arr;
    },
    dropDown: function (id, text, inp) {
        $('.' + id).delegate('li a', 'click', function () {
            $("#" + text).text($(this).text());
            var value = $(this).attr('value');
            $("input[name =" + inp + "]").attr("value", value);
            $("#" + text).attr("value", value);
        });
    },
    bindEvents: function () {
        var self = this;
        $('#startTime').datetimepicker({
        	language:'zh-CN',
        	weekStart: 1,
            todayBtn:  1,
    		autoclose: 1,
    		todayHighlight: 1,
    		startView: 2,
    		forceParse: 0,
            showMeridian: 1
        });
        $('#endTime').datetimepicker({
        	language:'zh-CN',
        	weekStart: 1,
            todayBtn:  1,
    		autoclose: 1,
    		todayHighlight: 1,
    		startView: 2,
    		forceParse: 0,
            showMeridian: 1
        });
        self.dropDown('modify_search_status', 'search_dropDown-status', 'status');
        self.dropDown('modify_search_status1', 'search_dropDown-status1', 'status1');
        $('.J_search').click(function () {
            self.list(true);
        });
        $('#table').delegate(".J_strategyInfo","click",function(){
            $('.modal-body').html($("#infoDialog").tmpl());
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_detail").removeClass("none");
            $('#myModalLabel').text("详情");
            $('.modal-dialog .modal-body').css({'overflow':"auto",'height':''});
            $('.modal-dialog .modal-content').css({'width':'800px'});
            var id = $(this).attr("data-value");
            $.get("/manager/user/detail",{"id":id}, function (result) {
                if (result.code == 200) {
                    self.detail(result);
                }
            });
        }).delegate(".J_delete","click",function(){
            $('.modal-body').empty().html("确定要删除吗？");
            $('#myModalLabel').text("删除用户");
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_delete_sure").removeClass("none");
            $('.modal-dialog .modal-body').css({'overflow':"auto",'height':''});
            $('.modal-dialog .modal-content').css({'width':'auto'});
            var id = $(this).attr("data-value");
            self.del(id);
        });
        $("#table").on("click", ".dialog-editor", function(){
            $('#myModalLabel').text("编辑用户");
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_sure").removeClass("none");
            $("#updContent").show();
            var id = $(this).attr("data-value");
            self.clearData();
            $.getJSON("/manager/user/detail", {id: id}, function(result){
            	if (result.code == 200) {
            		$('#search_dropDown-status1').attr("value", result.data.state).text(users.State[result.data.state]);
                    $("#id").val(result.data.id);
                    $("#username").val(result.data.username);
                    $("#nickname").val(result.data.nickname);
                    $("#phone").val(result.data.phone);
            	}
            });
        }).on("click", ".dialog-roles", function(){
        	$(".J_roles_sure").removeClass("none");
            var id = $(this).attr("data-value");
            $(".J_roles_sure").unbind().click(function () {
            	var treeObj=$.fn.zTree.getZTreeObj("roleTree");
                var nodes=treeObj.getCheckedNodes(true);
                
                var roleIds = [];
                for(var i = 0; i < nodes.length; i++) {
                	tmpNode = nodes[i];
					if(i!=nodes.length-1){
						roleIds += tmpNode.id+",";
					}else{
						roleIds += tmpNode.id;
					}
                }
                $.get("/manager/user/role/save",{userId:id, roleIds : roleIds},function(result){
                    if (result.code == 200) {
                        $('#rolesDialog').modal('hide');
                    } else {
                        asyncbox.alert("分配权限失败！\n"+result.msg,"提示");
                        $('#rolesDialog').modal('hide');
                    }
                });
            });
            $.getJSON("/manager/user/role/tree", {id: id}, function(result){
            	if (result.code == 200) {
            		var setting = {
        				check: {
        	        		chkboxType : { "Y" : "", "N" : "" },
        	        		enable: true
        	        	},
        	            view: {
        	                selectedMulti: false
        	            },
        	            data: {
        	                simpleData: {
        	                    enable: true,
        	                    idKey: "id",
        	    				pIdKey: "parentId",
        	                }
        	            },
        	            callback: {
        	            	
        	            }
        	        };
            		var zNodes = result.data;
        			$.fn.zTree.init($("#roleTree"), setting, zNodes);
        			$('#rolesDialog').modal('show');
            	}
            });
        });
        $("#updContent").click(function(){
        	self.update();
        });
        $(".add_dialog").click(function () {
        	var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_add_sure").removeClass("none");
            self.clearData();
        	self.add();
        });

        $("#editorDialog .close,#editorDialog .J_close_sure").click(function(){
            //location.reload();
        });
    },
    add: function () {
        var self = this;
        $(".J_add_sure").unbind('click').click(function () {
        	var param = {
                    username: $("#username").val(),
                    password: $("#password").val(),
                    nickname: $("#nickname").val(),
                    phone: $("#phone").val(),
                    state:$("#search_dropDown-status1").attr("value")
                };
                $.post("/manager/user/add", param, function(result){
                    if ( result.ret == 0 ) {
                        self.list();
                        $(".btn-default").trigger("click");
                    } else {
                        asyncbox.alert(result.msg,"提示");
                    }
                });
        });
    },
    del:function(id){
        var self =this;
        $(".J_delete_sure").unbind('click');
        $(".J_delete_sure").click(function () {
            $.get("/manager/user/del",{"id":id},function(result){
                if (result.code == 200) {
                    $('#myModal').modal('hide');
                    self.list();
                } else {
                    asyncbox.alert("删除失败！\n"+result.msg,"提示");
                    $('#myModal').modal('hide');
                }
            });
        });
        $('#myModal').modal('show');
    },
    update: function() {
    	var self = this;
    	var password = $("#password").val();
    	if(password) {
    		password = sha256(password);
    	}
        var param = {
            id: $("#id").val(),
            username: $("#username").val(),
            password: password,
            nickname: $("#nickname").val(),
            phone: $("#phone").val(),
            state:$("#search_dropDown-status1").attr("value")
        };
        $.post("/manager/user/update", param, function(result){
            if ( result.ret == 0 ) {
                self.list();
                $(".btn-default").trigger("click");
            } else {
                asyncbox.alert(result.msg,"提示");
            }
        });
    },
    detail: function (result) {
    	var data = result.data;
    	$(".idText").text(data.id);
        $(".usernameText").text(data.username);
        $(".nicknameText").text(data.nickname);
        $(".phoneText").text(data.phone);
        $(".stateText").text(users.State[data.state]);
        var createTime = new Date(data.createTime);
        data.createTime = createTime.format("yyyy-MM-dd hh:mm:ss");
        $(".createTimeText").text(data.createTime);
    },
    list: function (refresh) {
    	var self = this;
    	var state = $("#search_dropDown-status").attr("value");
    	var username = $("#usernameSearch").val();
    	var nickname = $("#nicknameSearch").val();
    	var phone = $("#phoneSearch").val();
        var action = "/manager/user/api/list", argument;
            argument = [
                {name:"state", value:state}, {name:"username", value:username},{name:"nickname", value:nickname}, {name:"phone", value:phone}
            ];
        
        oTable.dataTable(action, argument, self.insertResult, {refresh: refresh, pageSize: PAGE_SIZE});
    },
    clearData:function(){
    	$("#username").val("");
        $("#password").val("");
        $("#nickname").val("");
        $("#phone").val("");
    },
};
$(function () {
    users.init();
});


